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
import de.uniheidelberg.cl.advprog.planet.tree.UnorderedSplit;



public class ExpandNodesReducer implements Reducer<FeatSplitKey, FourValueTuple,NullWritable,NullWritable> {

	Map<FeatSplitKey,FourValueTuple> splitMetrics;
	//OutputCollector<NodeFeatSplitKey, ThreeValueTuple> output;
	Map<Integer, FeatSplitKey> bestSplits;
	Map<Integer, Double> bestSplitLeftBranchInstances;
	Map<Integer, Double> bestSplitLeftBranchYsum;
	Map<Integer, Double> bestSplitRightBranchInstances;
	Map<Integer, Double> bestSplitRightBranchYsum;
	Map<FeatSplitKey, Double> splitScores;
	Map<Double, FourValueTuple> unorderedXtoY;
	Map<Integer, FourValueTuple> marginals;
	private MultipleOutputs mos;
	private Reporter reporter;
	


	@Override
	public void configure(JobConf arg0) {
		this.marginals = new HashMap<Integer, FourValueTuple>();
		this.bestSplits = new HashMap<Integer, FeatSplitKey>();
		this.bestSplitLeftBranchInstances = new HashMap<Integer, Double>();
		this.bestSplitRightBranchInstances = new HashMap<Integer, Double>();
		this.bestSplitLeftBranchYsum = new HashMap<Integer, Double>();
		this.bestSplitRightBranchYsum = new HashMap<Integer, Double>();
		this.splitScores = new HashMap<FeatSplitKey, Double>();
		this.splitMetrics = new HashMap<FeatSplitKey, FourValueTuple>();
		mos = new MultipleOutputs(arg0);
		this.unorderedXtoY = new HashMap<Double, FourValueTuple>();
	}

	
		
	public void reduce(FeatSplitKey key, Iterator<FourValueTuple> values,
			OutputCollector<NullWritable, NullWritable> output, Reporter reporter)  {
		/*
		 * keys:
		 * 1) n: all agg_tup_n tuples output by the mappers; feature idx = -1
		 * 2) 
		 */
		if (key.getSplitId().equals("*")) {
			/*
			 * marginals 
			 */
			if (marginals.get(key.getFeatId()) == null) {
				marginals.put(key.getFeatId(), new FourValueTuple(0, 0, 0));
			}
			while (values.hasNext()) {
				FourValueTuple val = values.next();
				FourValueTuple val_old = marginals.get(key.getFeatId());
				val_old.add(val);
				marginals.put(key.getFeatId(), val_old);
			}
		} else if (key.getSplitId().equals("UNORDERED")) {
			while (values.hasNext()) {
				FourValueTuple val = values.next();
				FourValueTuple newVal = new FourValueTuple(val.getLeftBranchSum(), val.getSquareSum(), val.getInstanceNum());
				newVal.setFeatureVal(val.getFeatureVal());
				this.unorderedXtoY.put(Double.valueOf(newVal.getFeatureVal()), newVal);
			}
		} else {
			while (values.hasNext()) {
				FourValueTuple val = values.next();
				double varianceReduction = this.varianceReduction(val, marginals.get(key.getFeatId()));
				// check if there is a higher split
				if (this.bestSplits.get(key.getFeatId()) == null || 
						this.splitScores.get(bestSplits.get(key.getFeatId())) < varianceReduction) {
					this.splitScores.put(key, varianceReduction);
					this.bestSplits.put(key.getFeatId(), key);
					this.bestSplitLeftBranchInstances.put(key.getFeatId(), val.getInstanceNum());
					this.bestSplitLeftBranchYsum.put(key.getFeatId(), val.getLeftBranchSum());
					this.bestSplitRightBranchInstances.put(key.getFeatId(), marginals.get(key.getFeatId()).getInstanceNum() -  val.getInstanceNum());
					this.bestSplitRightBranchYsum.put(key.getFeatId(), marginals.get(key.getFeatId()).getLeftBranchSum() - val.getLeftBranchSum());
				}
				System.out.println(key.toString() + " value: " + val.toString() + " variance reduction: " + varianceReduction);
			}
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
		/* all values processed: compute best split for unordered attributes */
		if (key.getSplitId().equals("UNORDERED")) {
			UnorderedSplit split = this.computeBreimanSplit(key);
			// split id (for output) = feature values concatenated with ; 
			String splitId = "";
			for (double val : split.getLeftBranch()) {
				splitId = splitId + ";" + val;
			}
			splitId = splitId.substring(1);
			FeatSplitKey outputKey = new FeatSplitKey(key.getFeatId(), splitId);
			this.bestSplits.put(key.getFeatId(), outputKey);

			this.bestSplitLeftBranchInstances.put(key.getFeatId(), (double) split.getLeftBranchInstances());
			this.bestSplitRightBranchInstances.put(key.getFeatId(), marginals.get(key.getFeatId()).getInstanceNum()- (double) split.getRightBranchInstances());
			this.bestSplitLeftBranchYsum.put(key.getFeatId(), split.getLeftBranchY());
			this.bestSplitRightBranchYsum.put(key.getFeatId(), marginals.get(key.getFeatId()).getLeftBranchSum() - split.getLeftBranchY());
		}
		
		this.reporter = reporter;
		
	};

	private UnorderedSplit computeBreimanSplit(FeatSplitKey key) {
		TreeMap<Double, List<Double>> avgYtoValMap = new TreeMap<Double, List<Double>>();
		// sort x values according to their average y value
		for (double xVal : this.unorderedXtoY.keySet()) {
			double avgY = this.unorderedXtoY.get(xVal).getLeftBranchSum() / this.marginals.get(key.getFeatId()).getLeftBranchSum();
			System.out.println("xValue: " + xVal + " y value: " + this.unorderedXtoY.get(xVal) + " avgY " + avgY);

			if (!avgYtoValMap.containsKey(avgY))
				avgYtoValMap.put(avgY, new ArrayList<Double>());
			avgYtoValMap.get(avgY).add(xVal);
		}
		
		System.out.println("Ordered split keys breiman:");
		UnorderedSplit split = new UnorderedSplit(key.getFeatId());
		double bestReduction = 0.0;
		for (double avgY : avgYtoValMap.descendingKeySet()) {
			System.out.printf("\nclass association: %s", avgY);
			for (double featVal : avgYtoValMap.get(avgY)) {
				System.out.printf("\t feature: " + featVal + "\n");
				split.addLeftBranchItem(featVal);
				/* compute complexity reduction with this feat
				 * if not greater: return */
				FourValueTuple leftBranchTuple = new FourValueTuple();
				for (double splitFeatVal : split.getLeftBranch()) {
					// add up all tuples in the left branch split
					leftBranchTuple.add(this.unorderedXtoY.get(splitFeatVal));
				}
				System.out.println("Left branch tuple " + leftBranchTuple);
				// compute complexity reduction
				double reduction = this.varianceReduction(leftBranchTuple, this.marginals.get(key.getFeatId()));
				System.out.println("Reduction: " + reduction);
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
	
	
	public void close() {
		try {
			for (int nodeId : this.bestSplits.keySet()) {
				System.out.println("Writing");
				double yLeftBranch = this.bestSplitLeftBranchYsum.get(nodeId);
				double yRightBranch = this.bestSplitRightBranchYsum.get(nodeId);
				mos.getCollector("bestModel",this.reporter).collect(this.bestSplits.get(nodeId), new FourValueTuple(yLeftBranch, yRightBranch, 0));
				mos.getCollector("branchCounts", this.reporter).collect(new Text("node:" + nodeId + ":left:"), new DoubleWritable(this.bestSplitLeftBranchInstances.get(nodeId)));
				mos.getCollector("branchCounts", this.reporter).collect(new Text("node:" + nodeId + ":right:"), new DoubleWritable(this.bestSplitRightBranchInstances.get(nodeId)));
				//output.collect(this.bestSplits.get(nodeId), new ThreeValueTuple(0, 0, 0));
			}
			mos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	};
	
	private double variance(FourValueTuple branch) {
		double meanOfSquaredSum = branch.getLeftBranchSum()  / branch.getInstanceNum();
		double squaredMean = branch.getSquareSum()/ branch.getInstanceNum();
		double var = (branch.getSquareSum() - ((branch.getLeftBranchSum() * branch.getLeftBranchSum())/branch.getInstanceNum())) / branch.getInstanceNum();
		return var;
		//return Math.sqrt(meanOfSquaredSum - squaredMean);
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
