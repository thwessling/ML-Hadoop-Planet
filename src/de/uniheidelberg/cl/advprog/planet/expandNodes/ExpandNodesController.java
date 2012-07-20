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


public class ExpandNodesController  extends Configured implements Tool{

	public int run(String[] args) throws Exception {
        // Configuration processed by ToolRunner
        Configuration conf = getConf();
        
        // Create a JobConf using the processed conf
        JobConf job = new JobConf(conf, ExpandNodesController.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        // Process custom command-line options
        Path out = new Path(args[1]);
        
        // Specify various job-specific parameters     
        job.setJobName("my-app");
        FileInputFormat.setInputPaths(job, args[0]);
        FileOutputFormat.setOutputPath(job, out);
        job.setMapperClass(ExpandNodesMapper.class);
        job.setReducerClass(ExpandNodesReducer.class);

        // Submit the job, then poll for progress until the job is complete
        JobClient.runJob(job);
        return 0;
      }
	
}
