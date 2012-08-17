package de.uniheidelberg.cl.advprog.planet.expandNodes;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.MultipleOutputs;
import org.apache.hadoop.util.Tool;

import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.FourValueTuple;
import de.uniheidelberg.cl.advprog.planet.structures.NodeFeatSplitKey;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;

/**
 * This class starts a MapReduce job by setting appropriate parameters 
 * and input specifications for mappers/reducers.
 * 
 * {@link ExpandNodesController} serves as an interface between the 
 * Main controller and MapReduce.
 *  
 * @author boegel
 *
 */
public class ExpandNodesController  extends Configured implements Tool{

	private int featureIndex;
	private DecisionTree tree;
	private int nodeIndex;
	
	/**
	 * Instantiates a new {@link ExpandNodesController} for a new job.
	 *  
	 * @param idx The index of the currently processed feature. 
	 * @param nodeIndex The index of the currently processed node.
	 * @param tree The decision tree model.
	 */
	public ExpandNodesController(int idx,int nodeIndex,DecisionTree tree) {
		this.featureIndex = idx;
		this.tree = tree;
		this.nodeIndex = nodeIndex;
	}
	

	public int run(String[] args) throws Exception {
		JobClient client = new JobClient();
		JobConf job = new JobConf();
		// distribute the decision tree to all mappers/reducers
	    Serializer.serializeModelToDFS(tree, job);

	    // specify output types
	    job.setJobName("ExpandNode@" + this.nodeIndex + "@" + this.featureIndex);
	    // set configuration options for mappers
	    job.set("FeatureIndex", String.valueOf(this.featureIndex));
	    job.set("NodeIndex", String.valueOf(this.nodeIndex));
        job.setMapperClass(ExpandNodesMapper.class);
        job.setMapOutputKeyClass(NodeFeatSplitKey.class);
        job.setMapOutputValueClass(FourValueTuple.class);
        job.setJarByClass(ExpandNodesController.class);
        job.setNumMapTasks(10);
        job.setNumReduceTasks(10);
        job.setPartitionerClass(NodeFeatSplitPartitioner.class);
        job.setReducerClass(ExpandNodesReducer.class);
	    
        // add two named outputs
        MultipleOutputs.addNamedOutput(job, "bestModel", TextOutputFormat.class, NodeFeatSplitKey.class, FourValueTuple.class);
	    MultipleOutputs.addNamedOutput(job, "branchCounts", TextOutputFormat.class, Text.class, DoubleWritable.class);
	    FileInputFormat.setInputPaths(job, args[0]);
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        
	    client.setConf(job);
	    try {
	      JobClient.runJob(job);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return 0;
	}
	
	
}
