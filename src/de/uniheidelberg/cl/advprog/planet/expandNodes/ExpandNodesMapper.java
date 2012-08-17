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
import de.uniheidelberg.cl.advprog.planet.structures.NodeFeatSplitKey;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.Split;
import de.uniheidelberg.cl.advprog.planet.tree.Split.BRANCH;

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
public class ExpandNodesMapper implements Mapper<LongWritable, Text, NodeFeatSplitKey, FourValueTuple>  {
		
		/**
		 * Accumulation of counts for all splits.
		 */
		private Map<Split, FourValueTuple> orderedSplitStats;
		/**
		 * Accumulation of counts for all possible feature values of a feature.
		 */
		Map<Double, FourValueTuple> unOrderedAttValStats;
		FourValueTuple marginalStats;
		
		DecisionTree tree;
		private int featureIdx;
		private int nodeIdx;
		OutputCollector<NodeFeatSplitKey, FourValueTuple> output;

		@Override
		public void configure(JobConf context) {
			// read node to be processed
			this.featureIdx = Integer.parseInt(context.get("FeatureIndex"));
			this.nodeIdx = Integer.parseInt(context.get("NodeIndex"));
			System.out.println("Processing feature " + this.featureIdx);
			this.orderedSplitStats = new HashMap<Split, FourValueTuple>();
			this.unOrderedAttValStats = new HashMap<Double, FourValueTuple>();
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
				OutputCollector<NodeFeatSplitKey, FourValueTuple> output,
				Reporter reporter) throws IOException {
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line,",");
			List<Double> elems = new ArrayList<Double>();
			// add all feature values to a feature vector 
			while (itr.hasMoreTokens()) {
				elems.add(Double.parseDouble(itr.nextToken()));
			}
			// extract and remove the class label
			double label = elems.get(elems.size()-1);
			elems.remove(elems.size()-1);

			/* 
			 * check whether this instance is active for the current node i.e.  
			 * whether the instance would actually reach the currently processed node. 
			 */
			if (!this.tree.isInstanceActive(elems.toArray(new Double[elems.size()]), this.featureIdx, this.nodeIdx)) {
				return;
			}
			
			// get the currently processed node
			BranchingNode node = (BranchingNode) this.tree.getNodeById(this.nodeIdx);
			
			// collect statistics for each possible split of this feature
			if (node.getAtt().isOrdered()) {
				/*
				 * ORDERED attribute: use each possible value as split point and 
				 * collect statistics
				 */
				Set<Split> splits = node.getAtt().getPossibleSplits();
				
				for (Split s : splits) {
					// compute counts and accumulate
					FourValueTuple tuple = this.computeSplitMetrics(elems.get(this.featureIdx), label, s);
					if (tuple == null) continue;
					
					if (this.orderedSplitStats.get(s) != null) {
						FourValueTuple old_tuple = this.orderedSplitStats.get(s);
						tuple.add(old_tuple);
					}
					this.orderedSplitStats.put(s, tuple);
				}
			} else {
				/*
				 * UNORDERED attribute: count statistics for EACH possible value of the feature.
				 */
				FourValueTuple tuple = this.computeUnorderedStat(elems.get(this.featureIdx), label);
				if (this.unOrderedAttValStats.get(elems.get(this.featureIdx)) != null) {
					FourValueTuple old_tuple = this.unOrderedAttValStats.get(elems.get(this.featureIdx));
					old_tuple.setFeatureVal(elems.get(this.featureIdx));
					tuple.add(old_tuple);
				}
				this.unOrderedAttValStats.put(elems.get(this.featureIdx), tuple);
			}
			
			/*
			 * Count marginals independent from argument type. 
			 */
			this.marginalStats.add(new FourValueTuple(label,Math.pow(label, 2),1.0));
			this.output = output;
		}
		
		public void close() {
			// if we do not have any active features at this node: emit nothing 
			if (this.marginalStats.getInstanceNum() == 0)
				return;
			
			/*
			 * ORDERED attributes
			 */
			for (Split s : this.orderedSplitStats.keySet()) {
				// create the key and emit the counts for this key
				NodeFeatSplitKey split = new NodeFeatSplitKey(this.nodeIdx, s.getFeature(),s.getHadoopString());
				try {
					this.output.collect(split, this.orderedSplitStats.get(s));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			/*
			 * UNORDERED attributes
			 */
			for (double val : this.unOrderedAttValStats.keySet()) {
				NodeFeatSplitKey featureValue = new NodeFeatSplitKey(this.nodeIdx, this.featureIdx, "UNORDERED");
				try {
					this.output.collect(featureValue, this.unOrderedAttValStats.get(val));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			/*
			 * MARGINALS
			 */
			try {
				this.output.collect(new NodeFeatSplitKey(this.nodeIdx, this.featureIdx, "*"), this.marginalStats);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};		

		private FourValueTuple computeUnorderedStat(Double val, double label) {
			FourValueTuple tuple = new FourValueTuple(label, Math.pow(label, 2), 1.0);
			tuple.setFeatureVal(val);
			return tuple;
		}
		
		/**
		 * Collect counts if the feature is in the left branch of the split.
		 * 
		 * @param val Feature value.
		 * @param label Label of the instance.
		 * @param s Split to be evaluated.
		 * @return A tuple with the counts.
		 */
		private FourValueTuple computeSplitMetrics(Double val, double label, Split s) {
			if (s.getBranchForValue(val).equals(BRANCH.LEFT)) {
				FourValueTuple tuple = new FourValueTuple(label, Math.pow(label,2), 1.0);
				return tuple;
			}
			return null;
			    
		}

		
}
