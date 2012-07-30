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
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.ThreeValueTuple;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.Split;

/**
 * Word count with relative frequencies. Implemented using the pairs approach.
 */
public class ExpandNodesMapper extends MapReduceBase implements
	Mapper<LongWritable, Text, NodeFeatSplitKey, ThreeValueTuple>  {

		OutputCollector<NodeFeatSplitKey, ThreeValueTuple> output;
		Map<Split, ThreeValueTuple> splitStats;
		ThreeValueTuple marginalStats;
		
		DecisionTree tree;
		private Integer featureIdx;
		
		@Override
		public void configure(JobConf job) {
			// read node to be processed
			this.featureIdx = Integer.parseInt(job.get("FeatureIndex"));
			System.out.println("Processing feature " + this.featureIdx);
			this.splitStats = new HashMap<Split, ThreeValueTuple>();
			this.marginalStats = new ThreeValueTuple(0.0, 0.0, 0.0);
			try {
				this.tree = Serializer.readModelFromDFS(job);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		// return mapping from word pairs to 1
		public void map(LongWritable key, Text value,
				OutputCollector<NodeFeatSplitKey, ThreeValueTuple> output, Reporter reporter)
				throws IOException {
			this.output = output;
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line,",");
			List<Double> elems = new ArrayList<Double>();
			while (itr.hasMoreTokens()) {
				elems.add(Double.parseDouble(itr.nextToken()));
			}
			
			// extract and remove the class label
			double label = elems.get(elems.size()-1);
			elems.remove(elems.size()-1);
			// get Node 
			BranchingNode node = (BranchingNode) this.tree.getNodeById(this.featureIdx);
			// get splits
			Set<Split> splits = node.getAtt().getPossibleSplits();
			Set<Double> values = node.getAtt().getValues();

			// collect statistics for each possible split
			for (Split s : splits) {
				// compute metric tuples
				ThreeValueTuple tuple = this.computeSplitMetrics(elems.get(0), label, s);
				if (tuple == null) continue;
				
				if (this.splitStats.get(s) != null) {
					ThreeValueTuple old_tuple = this.splitStats.get(s);
					tuple.add(old_tuple);
				}
				this.splitStats.put(s, tuple);
			}
			
			// count marginals
			
			this.marginalStats.add(new ThreeValueTuple(label,Math.pow(label, 2),1.0));
			// do some computation
//			if (!this.tree.isFeatureActive(elems.toArray(new Double[elems.size()]), this.featureIdx, elems.get(0)));
		}
		
		@Override
		public void close() throws IOException {
			for (Split s : this.splitStats.keySet()) {
				NodeFeatSplitKey split = new NodeFeatSplitKey(s.getFeature(), 
					    s.getFeature(), 
					    s.getSplitId());
				output.collect(split, this.splitStats.get(s));
//				System.out.println("Split : " + split + " tuple: " + this.splitStats.get(s));
			}
			output.collect(new NodeFeatSplitKey(this.featureIdx, -1, "*"), this.marginalStats);
			super.close();
		}
		
		private ThreeValueTuple computeSplitMetrics(Double val, double label, Split s) {
			if (val < s.getThreshold()) {
//				System.out.println("Value " + val + ", threshold " + s.getThreshold() + " label: " + label);
				ThreeValueTuple tuple = new ThreeValueTuple(label, Math.pow(label,2), 1.0);
				return tuple;
			}
			return null;
			    
		}
		
}
