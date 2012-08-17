package de.uniheidelberg.cl.advprog.planet.expandNodes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;


import de.uniheidelberg.cl.advprog.planet.structures.FourValueTuple;
import de.uniheidelberg.cl.advprog.planet.structures.NodeFeatSplitKey;
import de.uniheidelberg.cl.advprog.planet.tree.UnorderedSplit;



/**
 * Each Mapper processes ONE feature and ONE node.
 * 
 * The following metrics are computed and emitted as values: 
 * <ul>
 * 	<li>Sum of left branch y values</li>
 *  <li>Squared sum of left branch y values</li
 *  <li>Number of left branch instances</li>
 * </ul> 
 * 
 * The keys depend on the attribute type.
 * 
 * For ORDERED attributes:
 * The metrics for all splits are accumulated and emitted once after all instances have been  
 * processed.
 * Output: { [NodeIndex,FeatureIndex,Split] -> metric }
 * 
 * For UNORDERED attributes:
 * The metrics are counted accumulated for each possible feature value of the feature.
 * Output: { [NodeIndex,FeatureIndex,"UNORDERED"] -> metric }
 * 
 * MARGINALS:
 * In addition, marginals are computed i.e. the metrics are computed for each instance.
 * Output: { [NodeIndex,FeatureIndex,*] -> metric }
 *  
 */
public class ExpandNodesReducer implements Reducer<NodeFeatSplitKey, FourValueTuple,NullWritable,NullWritable> {

    NodeFeatSplitKey bestSplit;
    double bestSplitScore;
	Map<Integer, Double> bestSplitLeftBranchInstances;
	Map<Integer, Double> bestSplitLeftBranchYsum;
	Map<Integer, Double> bestSplitRightBranchInstances;
	Map<Integer, Double> bestSplitRightBranchYsum;
	Map<Double, FourValueTuple> unorderedXtoY;
	Map<Integer, FourValueTuple> marginals;
	private MultipleOutputs mos;
	private Reporter reporter;
	


	@Override
	public void configure(JobConf arg0) {
		this.marginals = new HashMap<Integer, FourValueTuple>();
		this.bestSplitLeftBranchInstances = new HashMap<Integer, Double>();
		this.bestSplitRightBranchInstances = new HashMap<Integer, Double>();
		this.bestSplitLeftBranchYsum = new HashMap<Integer, Double>();
		this.bestSplitRightBranchYsum = new HashMap<Integer, Double>();
		mos = new MultipleOutputs(arg0);
		this.unorderedXtoY = new HashMap<Double, FourValueTuple>();
	}

	
		
	public void reduce(NodeFeatSplitKey key, Iterator<FourValueTuple> values,
			OutputCollector<NullWritable, NullWritable> output, Reporter reporter)  {
		if (key.getSplitId().equals("*")) {
			/*
			 * MARGINALS 
			 */
			if (marginals.get(key.getNodeId()) == null) {
				marginals.put(key.getNodeId(), new FourValueTuple(0, 0, 0));
			}
			while (values.hasNext()) {
				FourValueTuple val = values.next();
				FourValueTuple val_old = marginals.get(key.getNodeId());
				val_old.add(val);
				marginals.put(key.getNodeId(), val_old);
			}
		} else if (key.getSplitId().equals("UNORDERED")) {
			/*
			 * UNORDERED
			 * Store counts for the currently processed feature value.
			 */
			while (values.hasNext()) {
				FourValueTuple val = values.next();
				FourValueTuple newVal = new FourValueTuple(val.getLeftBranchSum(), val.getSquareSum(), val.getInstanceNum());
				newVal.setFeatureVal(val.getFeatureVal());
				this.unorderedXtoY.put(Double.valueOf(newVal.getFeatureVal()), newVal);
			}
		} else { 
			/*
			 *	ORDERED attribute
			 *  Aggregate all counts for each split and determine best split.
			 *  
			 */
			FourValueTuple val = this.aggregateKeyCounts(values);
			
			double varianceReduction = this.varianceReduction(val , marginals.get(key.getNodeId()));
			System.out.println(key.toString() + "(" + key.hashCode() + ") value: " + val .toString() + " variance reduction: " + varianceReduction);

			// if there is a higher split: update
            if (this.bestSplit == null || 
                    this.bestSplitScore < varianceReduction) {
				this.bestSplitScore = varianceReduction;
				this.bestSplit = new NodeFeatSplitKey(key.getNodeId(), key.getFeatId(), key.getSplitId());
				this.bestSplitLeftBranchInstances.put(key.getNodeId(), val .getInstanceNum());
				this.bestSplitLeftBranchYsum.put(key.getNodeId(), val .getLeftBranchSum());
				this.bestSplitRightBranchInstances.put(key.getNodeId(), marginals.get(key.getNodeId()).getInstanceNum() -  val .getInstanceNum());
				this.bestSplitRightBranchYsum.put(key.getNodeId(), marginals.get(key.getNodeId()).getLeftBranchSum() - val .getLeftBranchSum());
			}

		}
		/* all values processed: compute best split for unordered attributes */
		if (key.getSplitId().equals("UNORDERED")) {
			UnorderedSplit split = this.computeBreimanSplit(key.getNodeId());
			// split id (for output) = feature values concatenated with ; 
			String splitId = "";
			for (double val : split.getLeftBranch()) {
				splitId = splitId + ";" + val;
			}
			splitId = splitId.substring(1);
			NodeFeatSplitKey outputKey = new NodeFeatSplitKey(key.getNodeId(),key.getFeatId(), splitId);
            this.bestSplit = outputKey;

			this.bestSplitLeftBranchInstances.put(key.getNodeId(), (double) split.getLeftBranchInstances());
			this.bestSplitRightBranchInstances.put(key.getNodeId(), marginals.get(key.getNodeId()).getInstanceNum() - (double) split.getLeftBranchInstances());
			this.bestSplitLeftBranchYsum.put(key.getNodeId(), split.getLeftBranchY());
			this.bestSplitRightBranchYsum.put(key.getNodeId(), marginals.get(key.getNodeId()).getLeftBranchSum() - split.getLeftBranchY());
		}
		this.reporter = reporter;
	}
	private FourValueTuple aggregateKeyCounts(Iterator<FourValueTuple> tuples) {
		FourValueTuple tuple = new FourValueTuple();
		while (tuples.hasNext()) {
			tuple.add(tuples.next());
		}
		return tuple;
	}

	/**
	 * Determines the best split for a set of unordered attributes using 
	 * the Breiman algorithm.
	 * 
	 * @param nodeId The node id currently being processed. 
	 * @return The optimal split for this attribute.
	 */
	private UnorderedSplit computeBreimanSplit(int nodeId) {
		TreeMap<Double, List<Double>> avgYtoValMap = new TreeMap<Double, List<Double>>();
		// sort x values according to their average y value
		for (double xVal : this.unorderedXtoY.keySet()) {
			double avgY = this.unorderedXtoY.get(xVal).getLeftBranchSum() / this.marginals.get(nodeId).getLeftBranchSum();

			if (!avgYtoValMap.containsKey(avgY))
				avgYtoValMap.put(avgY, new ArrayList<Double>());
			avgYtoValMap.get(avgY).add(xVal);
		}
		
		UnorderedSplit split = new UnorderedSplit(nodeId);
		double bestReduction = 0.0;
		for (double avgY : avgYtoValMap.descendingKeySet()) {
			for (double featVal : avgYtoValMap.get(avgY)) {
				split.addLeftBranchItem(featVal);
				/* compute complexity reduction with this feat
				 * if not greater: return */
				FourValueTuple leftBranchTuple = new FourValueTuple();
				for (double splitFeatVal : split.getLeftBranch()) {
					// add up all tuples in the left branch split
					leftBranchTuple.add(this.unorderedXtoY.get(splitFeatVal));
				}
				// compute complexity reduction
				double reduction = this.varianceReduction(leftBranchTuple, this.marginals.get(nodeId));
				if (reduction > bestReduction) {
					bestReduction = reduction;
					split.setLeftBranchInstances((int) leftBranchTuple.getInstanceNum());
					split.setLeftBranchY(leftBranchTuple.getLeftBranchSum());
				} else {
					// no improvement: remove the value and return
					split.removeItem(featVal);
					return split;
				}
			}
		}
		return split;
	}
	
	
	@SuppressWarnings("unchecked")
	public void close() {
		try {
		    int nodeId = this.bestSplit.getNodeId();
			double yLeftBranch = this.bestSplitLeftBranchYsum.get(nodeId);
			double yRightBranch = this.bestSplitRightBranchYsum.get(nodeId);
            mos.getCollector("bestModel",this.reporter).collect(this.bestSplit, new FourValueTuple(yLeftBranch, yRightBranch, 0));
			mos.getCollector("branchCounts", this.reporter).collect(new Text("node:" + nodeId + ":left:"), new DoubleWritable(this.bestSplitLeftBranchInstances.get(nodeId)));
			mos.getCollector("branchCounts", this.reporter).collect(new Text("node:" + nodeId + ":right:"), new DoubleWritable(this.bestSplitRightBranchInstances.get(nodeId)));
			mos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	};
	
	private double variance(FourValueTuple branch) {
		double var = (branch.getSquareSum() - ((branch.getLeftBranchSum() * branch.getLeftBranchSum())/branch.getInstanceNum())) / branch.getInstanceNum();
		return var;
	}
	
	private double varianceReduction(FourValueTuple leftBranch, FourValueTuple marginals) {
		FourValueTuple rightBranch = marginals.minus(leftBranch);
		double docSize = marginals.getInstanceNum();
		double docVar = this.variance(marginals);
		double rightVar = this.variance(rightBranch);
		double leftVar = this.variance(leftBranch);
		return docSize *docVar - (leftBranch.getInstanceNum() * leftVar + rightBranch.getInstanceNum() * rightVar);
	}



}
