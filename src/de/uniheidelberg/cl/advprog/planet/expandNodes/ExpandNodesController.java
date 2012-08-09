package de.uniheidelberg.cl.advprog.planet.expandNodes;

import org.apache.hadoop.conf.Configuration;
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

import de.uniheidelberg.cl.advprog.planet.io.InstanceReader;
import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.FourValueTuple;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;


public class ExpandNodesController  extends Configured implements Tool{

	private int featureIndex;
	private DecisionTree tree;
	private int nodeIndex;
	
	public ExpandNodesController(int idx,int nodeIndex,DecisionTree tree) {
		this.featureIndex = idx;
		this.tree = tree;
		this.nodeIndex = nodeIndex;
	}
	

	@Override
	public int run(String[] args) throws Exception {
		JobClient client = new JobClient();
		JobConf job = new JobConf();
	    Serializer.serializeModelToDFS(tree, job);

	    // specify output types
//	    conf.setOutputKeyClass(NodeFeatSplitKey.class);
//	    conf.setOutputValueClass(ThreeValueTuple.class);
	    job.setJobName("my-app-advanced");
	    job.set("FeatureIndex", String.valueOf(this.featureIndex));
	    job.set("NodeIndex", String.valueOf(this.featureIndex));
        job.setMapperClass(ExpandNodesMapper.class);
        job.setMapOutputKeyClass(FeatSplitKey.class);
//        job.setOutputKeyClass(NodeFeatSplitKey.class);
        job.setMapOutputValueClass(FourValueTuple.class);
        job.setReducerClass(ExpandNodesReducer.class);
	    MultipleOutputs.addNamedOutput(job, "bestModel", TextOutputFormat.class, FeatSplitKey.class, FourValueTuple.class);
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
