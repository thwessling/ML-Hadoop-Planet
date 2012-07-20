
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
public class ExpandNodes extends Configured implements Tool {

	/**
	 * Counts the words in each line. For each line of input, break the line
	 * into words and emit them as ([<b>word1</b>, <b>word2</b>], 1).
	 */
	public static class MapClass extends MapReduceBase implements
			Mapper<LongWritable, Text, Text, DoubleWritable> {

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

	/**
	 * A reducer class that just emits the sum of the input values.
	 */
	public static class Reduce extends MapReduceBase implements
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		private double marginals;

		public void reduce(Text key, Iterator<DoubleWritable> values,
				OutputCollector<Text, DoubleWritable> output,
				Reporter reporter) throws IOException {
			double sum = 0;
			while (values.hasNext()) {
				DoubleWritable val = values.next();
				sum += 1.0;

			}
		}
	}

	static int printUsage() {
		System.out
				.println("wordcount [-m <maps>] [-r <reduces>] <input> <output>");
		ToolRunner.printGenericCommandUsage(System.out);
		return -1;
	}

	/**
	 * The main driver for word count map/reduce program. Invoke this method to
	 * submit the map/reduce job.
	 * 
	 * @throws IOException
	 *             When there is communication problems with the job tracker.
	 */
	public int run(String[] args) throws Exception {
		JobConf conf = new JobConf(getConf(), ExpandNodes.class);
		conf.setJobName("pairs, custom RecordReader + modif. LineReader");
//		conf.setInputFormat(FullTextToSentenceFileFormat.class);

		// the keys are Text instances
		conf.setOutputKeyClass(Text.class);
		// the values are counts (doubles)
		conf.setOutputValueClass(DoubleWritable.class);

		conf.setMapperClass(MapClass.class);
		conf.setReducerClass(Reduce.class);
//		conf.setPartitionerClass(PairPartitioner.class);

		List<String> other_args = new ArrayList<String>();
		for (int i = 0; i < args.length; ++i) {
			try {
				if ("-m".equals(args[i])) {
					conf.setNumMapTasks(Integer.parseInt(args[++i]));
				} else if ("-r".equals(args[i])) {
					conf.setNumReduceTasks(Integer.parseInt(args[++i]));
				} else {
					other_args.add(args[i]);
				}
			} catch (NumberFormatException except) {
				System.out.println("ERROR: Integer expected instead of "
						+ args[i]);
				return printUsage();
			} catch (ArrayIndexOutOfBoundsException except) {
				System.out.println("ERROR: Required parameter missing from "
						+ args[i - 1]);
				return printUsage();
			}
		}
		// Make sure there are exactly 2 parameters left.
		if (other_args.size() != 2) {
			System.out.println("ERROR: Wrong number of parameters: "
					+ other_args.size() + " instead of 2.");
			return printUsage();
		}
		FileInputFormat.setInputPaths(conf, other_args.get(0));
		FileOutputFormat.setOutputPath(conf, new Path(other_args.get(1)));

		JobClient.runJob(conf);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new ExpandNodes(), args);
		System.exit(res);
	}

}
