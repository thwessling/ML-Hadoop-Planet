package de.uniheidelberg.cl.advprog.planet.expandNodes;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleOutputs;

import de.uniheidelberg.cl.advprog.planet.structures.ThreeValueTuple;



public class ExpandNodesReducer implements Reducer<NodeFeatSplitKey, ThreeValueTuple,NullWritable,NullWritable> {

	Map<NodeFeatSplitKey,ThreeValueTuple> splitMetrics;
	//OutputCollector<NodeFeatSplitKey, ThreeValueTuple> output;
	Map<Integer, NodeFeatSplitKey> bestSplits;
	Map<Integer, Double> bestSplitLeftBranch;
	Map<Integer, Double> bestSplitRightBranch;
	Map<NodeFeatSplitKey, Double> splitScores;
	Map<Integer, ThreeValueTuple> marginals;
	private MultipleOutputs mos;
	private Reporter reporter;
	


	@Override
	public void configure(JobConf arg0) {
		this.marginals = new HashMap<Integer, ThreeValueTuple>();
		this.bestSplits = new HashMap<Integer, NodeFeatSplitKey>();
		this.bestSplitLeftBranch = new HashMap<Integer, Double>();
		this.bestSplitRightBranch = new HashMap<Integer, Double>();
		this.splitScores = new HashMap<NodeFeatSplitKey, Double>();
		this.splitMetrics = new HashMap<NodeFeatSplitKey, ThreeValueTuple>();
		mos = new MultipleOutputs(arg0);
		
	}

	
		
	public void reduce(NodeFeatSplitKey key, Iterator<ThreeValueTuple> values,
			OutputCollector<NullWritable, NullWritable> output, Reporter reporter)  {
		/*
		 * keys:
		 * 1) n: all agg_tup_n tuples output by the mappers; feature idx = -1
		 * 2) 
		 */
		if (key.getFeatId() == -1) {
			/*
			 * marginals 
			 */
			if (marginals.get(key.getNodeId()) == null) {
				marginals.put(key.getNodeId(), new ThreeValueTuple(0, 0, 0));
			}
			while (values.hasNext()) {
				ThreeValueTuple val = values.next();
				ThreeValueTuple val_old = marginals.get(key.getNodeId());
				val_old.add(val);
				marginals.put(key.getNodeId(), val_old);
			}
//			for (Integer i : marginals.keySet()) {
//				output.collect(new NodeFeatSplitKey(i, -1, ""), marginals.get(i));
//			}
		} else {
			ThreeValueTuple val = values.next();
			double varianceReduction = this.varianceReduction(val, marginals.get(key.getNodeId()));
			// check if there is a higher split
			if (this.bestSplits.get(key.getNodeId()) == null || 
					this.splitScores.get(bestSplits.get(key.getNodeId())) < varianceReduction) {

				this.splitScores.put(key, varianceReduction);
				this.bestSplits.put(key.getNodeId(), key);
				this.bestSplitLeftBranch.put(key.getNodeId(), val.getInstanceNum());
				this.bestSplitRightBranch.put(key.getNodeId(), marginals.get(key.getNodeId()).getInstanceNum() -  val.getInstanceNum());
			}
			System.out.println(key.toString() + " value: " + val.toString() + " variance reduction: " + this.varianceReduction(val, marginals.get(key.getNodeId())));
			
//			while (values.hasNext()) {
//				ThreeValueTuple val = values.next();
//				ThreeValueTuple val_old = splitMetrics.get(key);
//				if (val_old != null) {
//					System.out.printf(val + " +  " + val_old);
//					val_old.add(val);
//					splitMetrics.put(key, val_old);
//					System.out.printf(" = %s\n", splitMetrics.get(key));
//				} else {
//					splitMetrics.put(key, val);
//				}
//			}
		}
		this.reporter = reporter;
		
	};

	
	public void close() {
		try {
			for (int nodeId : this.bestSplits.keySet()) {
				System.out.println("Writing");
				mos.getCollector("bestModel",this.reporter).collect(this.bestSplits.get(nodeId), new ThreeValueTuple(0, 0, 0));
				mos.getCollector("branchCounts", this.reporter).collect(new Text("node:" + nodeId + ":left:"), new DoubleWritable(this.bestSplitLeftBranch.get(nodeId)));
				mos.getCollector("branchCounts", this.reporter).collect(new Text("node:" + nodeId + ":right:"), new DoubleWritable(this.bestSplitRightBranch.get(nodeId)));
				//output.collect(this.bestSplits.get(nodeId), new ThreeValueTuple(0, 0, 0));
			}
			mos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	};
	
	private double variance(ThreeValueTuple branch) {
		double meanOfSquaredSum = branch.getSquareSum() / branch.getInstanceNum();
		double squaredMean = branch.getLeftBranchSum() / branch.getInstanceNum();
		return Math.sqrt(meanOfSquaredSum - squaredMean);
	}
	
	private double varianceReduction(ThreeValueTuple leftBranch, ThreeValueTuple marginals) {
		ThreeValueTuple rightBranch = marginals.minus(leftBranch);
		double docSize = marginals.getInstanceNum();
		double docVar = this.variance(marginals);
		double rightVar = this.variance(rightBranch);
		double leftVar = this.variance(leftBranch);
		
		return docSize * docVar - (leftBranch.getInstanceNum() * leftVar + rightBranch.getInstanceNum() * rightVar);
	}



}
