package de.uniheidelberg.cl.advprog.planet.expandNodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Word count with relative frequencies. Implemented using the pairs approach.
 */
public class ExpandNodesMapper extends MapReduceBase implements
	Mapper<LongWritable, Text, Text, DoubleWritable>  {


		// return mapping from word pairs to 1

		public void map(LongWritable key, Text value,
				OutputCollector<Text, DoubleWritable> output, Reporter reporter)
				throws IOException {
			String line = value.toString();
			System.out.println(value);
			StringTokenizer itr = new StringTokenizer(line);
			List<String> elems = new ArrayList<String>();
			while (itr.hasMoreTokens())
				elems.add(itr.nextToken());
			int counter = 0;
			// iterate over all elements in one transaction
			for (int i = 0; i < elems.size(); i++) {
				// iterate over all paired elements and add +1 for each element
				for (int z = i + 1; z < elems.size(); z++) {
					counter += 1;
					String secondToken = elems.get(z);
					output.collect(new Text(secondToken), new DoubleWritable(1.0));
				}
			}
			System.out.println("Pairs: " + counter);
		}
}
