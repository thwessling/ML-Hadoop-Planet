package de.uniheidelberg.cl.advprog.planet.expandNodes;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;


public class ExpandNodesController {

		/**
		 * The main driver for word count map/reduce program. Invoke this method to
		 * submit the map/reduce job.
		 * 
		 * @throws IOException
		 *             When there is communication problems with the job tracker.
		 */
		public static void main(String[] args) throws Exception {
			JobConf conf = new JobConf(ExpandNodesMapper.class);
			conf.setJobName("pairs, custom RecordReader + modif. LineReader");

			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(DoubleWritable.class);

			conf.setMapperClass(ExpandNodesMapper.class);
			conf.setReducerClass(ExpandNodesReducer.class);

		
			FileInputFormat.setInputPaths(conf, args[0]);
			FileOutputFormat.setOutputPath(conf, new Path(args[1]));

			JobClient.runJob(conf);
		}
}
