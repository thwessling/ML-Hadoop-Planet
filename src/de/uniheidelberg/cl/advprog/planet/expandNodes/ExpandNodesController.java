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
import org.apache.hadoop.util.Tool;

import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.ThreeValueTuple;
import de.uniheidelberg.cl.advprog.planet.structures.TreeModel;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;


public class ExpandNodesController  extends Configured implements Tool{

	private int featureIndex;
	private DecisionTree tree;
	
	public ExpandNodesController(int idx,DecisionTree tree) {
		this.featureIndex = idx;
		this.tree = tree;
	}
	

	@Override
	public int run(String[] args) throws Exception {
		JobClient client = new JobClient();
	    JobConf conf = new JobConf(ExpandNodesController.class);
	    Serializer.serializeModelToDFS(tree, conf);
	    
	    // specify output types
	    conf.setOutputKeyClass(NodeFeatSplitKey.class);
	    conf.setOutputValueClass(ThreeValueTuple.class);
	    conf.setJobName("my-app-advanced");
	    conf.set("FeatureIndex", String.valueOf(this.featureIndex));
	    FileInputFormat.setInputPaths(conf, args[0]);
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));
        conf.setMapperClass(ExpandNodesMapper.class);
        conf.setReducerClass(ExpandNodesReducer.class);
	    

	    client.setConf(conf);
	    try {
	      JobClient.runJob(conf);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return 0;
	}
	
	
}
