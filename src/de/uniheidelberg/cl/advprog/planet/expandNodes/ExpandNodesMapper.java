package de.uniheidelberg.cl.advprog.planet.expandNodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.FourValueTuple;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.Split;
import de.uniheidelberg.cl.advprog.planet.tree.Split.BRANCH;

/**
 * Word count with relative frequencies. Implemented using the pairs approach.
 */
public class ExpandNodesMapper implements Mapper<LongWritable, Text, FeatSplitKey, FourValueTuple>  {
		Map<Split, FourValueTuple> orderedSplitStats;
		Map<Double, FourValueTuple> unOrderedSplitStats;
		FourValueTuple marginalStats;
		
		DecisionTree tree;
		private int featureIdx;
		private int nodeIdx;
		OutputCollector<FeatSplitKey, FourValueTuple> output;

		@Override
		public void configure(JobConf context) {
			// read node to be processed
			this.featureIdx = Integer.parseInt(context.get("FeatureIndex"));
			this.nodeIdx = Integer.parseInt(context.get("NodeIndex"));
			System.out.println("Processing feature " + this.featureIdx);
			this.orderedSplitStats = new HashMap<Split, FourValueTuple>();
			this.unOrderedSplitStats = new HashMap<Double, FourValueTuple>();
			this.marginalStats = new FourValueTuple(0.0, 0.0, 0.0);
			try {
				this.tree = Serializer.readModelFromDFS(context);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		
		
		public void map(LongWritable key, Text value,
				OutputCollector<FeatSplitKey, FourValueTuple> output,
				Reporter reporter) throws IOException {
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line,",");
			List<Double> elems = new ArrayList<Double>();
			while (itr.hasMoreTokens()) {
				elems.add(Double.parseDouble(itr.nextToken()));
			}
			
			// extract and remove the class label
			double label = elems.get(elems.size()-1);
			elems.remove(elems.size()-1);
			
			/* check whether this instance is active for the current feature */
			if (!this.tree.isInstanceActive(elems.toArray(new Double[elems.size()]), this.featureIdx))
				return;

			
			// get Node for this feature
			BranchingNode node = (BranchingNode) this.tree.getNodeById(this.featureIdx);
			// get splits
			Set<Split> splits = node.getAtt().getPossibleSplits();
			// collect statistics for each possible split
			if (node.getAtt().isOrdered()) {
				for (Split s : splits) {
					// compute metric tuples
					FourValueTuple tuple = this.computeSplitMetrics(elems.get(this.featureIdx), label, s);
					if (tuple == null) continue;
					
					if (this.orderedSplitStats.get(s) != null) {
						FourValueTuple old_tuple = this.orderedSplitStats.get(s);
						tuple.add(old_tuple);
					}
					this.orderedSplitStats.put(s, tuple);
				}
			} else {
				FourValueTuple tuple = this.computeUnorderedStat(elems.get(this.featureIdx), label);
				if (this.unOrderedSplitStats.get(elems.get(this.featureIdx)) != null) {
					FourValueTuple old_tuple = this.unOrderedSplitStats.get(elems.get(this.featureIdx));
					old_tuple.setFeatureVal(elems.get(this.featureIdx));
					tuple.add(old_tuple);
				}
				this.unOrderedSplitStats.put(elems.get(this.featureIdx), tuple);
//				System.out.println("Tuple for feature value: " + elems.get(this.featureIdx) + ": " +  tuple);
			}
			
			// count marginals
			this.output = output;
			this.marginalStats.add(new FourValueTuple(label,Math.pow(label, 2),1.0));
			// do some computation
			
		}
		
		public void close() { 
			for (Split s : this.orderedSplitStats.keySet()) {
				FeatSplitKey split = new FeatSplitKey(s.getFeature(),s.getHadoopString());
				try {
					this.output.collect(split, this.orderedSplitStats.get(s));
				} catch (IOException e) {
					e.printStackTrace();
				}
//				System.out.println("Split : " + split + " tuple: " + this.splitStats.get(s));
			}
			for (double val : this.unOrderedSplitStats.keySet()) {
				/*
				 * for unordered attributes: collect {(featureIdx,"UNORDERED") -> (ThreevalueTuple)} pairs 
				 * 
				 */
				FeatSplitKey featureValue = new FeatSplitKey(this.featureIdx, "UNORDERED");
				try {
					this.output.collect(featureValue, this.unOrderedSplitStats.get(val));
				} catch (IOException e) {
					e.printStackTrace();
				}
//				System.out.println("Split : " + split + " tuple: " + this.splitStats.get(s));
			}
			try {
				this.output.collect(new FeatSplitKey(this.featureIdx, "*"), this.marginalStats);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};		

		private FourValueTuple computeUnorderedStat(Double val, double label) {
			FourValueTuple tuple = new FourValueTuple(label, Math.pow(label, 2), 1.0);
			tuple.setFeatureVal(val);
			return tuple;
		}
		
		private FourValueTuple computeSplitMetrics(Double val, double label, Split s) {
			if (s.getBranchForValue(val).equals(BRANCH.LEFT)) {
//				System.out.println("Value " + val + ", threshold " + s.getThreshold() + " label: " + label);
				FourValueTuple tuple = new FourValueTuple(label, Math.pow(label,2), 1.0);
				return tuple;
			}
			return null;
			    
		}

		
}
