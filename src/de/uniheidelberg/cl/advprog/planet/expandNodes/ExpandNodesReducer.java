package de.uniheidelberg.cl.advprog.planet.expandNodes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import de.uniheidelberg.cl.advprog.planet.tree.*;
import de.uniheidelberg.cl.advprog.planet.structures.ThreeValueTuple;


public class ExpandNodesReducer extends MapReduceBase implements
				Reducer<NodeFeatSplitKey, ThreeValueTuple, NodeFeatSplitKey, ThreeValueTuple> {

	Map<NodeFeatSplitKey,ThreeValueTuple> splitMetrics;
	OutputCollector<NodeFeatSplitKey, ThreeValueTuple> output;
	Map<Node, Split> bestSplits;
	Map<Integer, ThreeValueTuple> marginals;
	
	@Override
	public void configure(JobConf job) {
		this.marginals = new HashMap<Integer, ThreeValueTuple>();
		this.bestSplits = new HashMap<Node, Split>();
		super.configure(job);
		this.splitMetrics = new HashMap<NodeFeatSplitKey, ThreeValueTuple>();
	}
	
	
	
	public void reduce(NodeFeatSplitKey key, Iterator<ThreeValueTuple> values,
			OutputCollector<NodeFeatSplitKey, ThreeValueTuple> output,
			Reporter reporter) throws IOException {
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
			for (Integer i : marginals.keySet()) {
				output.collect(new NodeFeatSplitKey(i, -1, ""), marginals.get(i));
			}
		} else {
			ThreeValueTuple val = values.next();
			output.collect(key, val);
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
	}
	
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
