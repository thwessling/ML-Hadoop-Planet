package de.uniheidelberg.cl.advprog.planet.expandNodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.Split;

/**
 * Word count with relative frequencies. Implemented using the pairs approach.
 */
public class ExpandNodesMapper extends MapReduceBase implements
	Mapper<LongWritable, Text, Text, DoubleWritable>  {

		DecisionTree tree;
		private Integer featureIdx;
		
		@Override
		public void configure(JobConf job) {
			// read node to be processed
			this.featureIdx = Integer.parseInt(job.get("FeatureIndex"));
			System.out.println("Processing feature " + this.featureIdx);
			
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
				OutputCollector<Text, DoubleWritable> output, Reporter reporter)
				throws IOException {
			String line = value.toString();
			StringTokenizer itr = new StringTokenizer(line,",");
			List<String> elems = new ArrayList<String>();
			while (itr.hasMoreTokens())
				elems.add(itr.nextToken());
			
			// extract and remove the class label
			int label = Integer.parseInt(elems.get(elems.size()-1));
			elems.remove(elems.size()-1);
			
			// get Node 
			BranchingNode node = (BranchingNode) this.tree.getNodeById(this.featureIdx);
			// get splits
			Set<Split> splits = node.getAtt().getPossibleSplits();
			Set<Double> values = node.getAtt().getValues();
			for (Double val : values) {
				output.collect(new Text(String.valueOf(this.featureIdx)), new DoubleWritable(val));
			}
			
			
			// do some computation
//			if (!this.tree.isFeatureActive(this.featureIdx, Integer.valueOf(elems.get(0))))
//				return;
		}
}
