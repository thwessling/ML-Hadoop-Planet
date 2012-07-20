package de.uniheidelberg.cl.advprog.planet.controller;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.util.ToolRunner;


import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.structures.TreeModel;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.Node;

public class MainControllerThread extends Thread {

	TreeModel model;
	/**
	 * Nodes for which D* is too large to fit in memory.
	 */
	List<Node> mrq;
	/**
	 * Nodes for which D* fits in memory.
	 */
	List<Node> inMemQ;
	
	public MainControllerThread() {
		this.mrq  = new ArrayList<Node>();
		this.inMemQ = new ArrayList<Node>();
	}
	
	private static void serializeModelToDFS(DecisionTree model) throws IOException, URISyntaxException {
		JobConf conf = new JobConf();
		FileSystem fs = FileSystem.get(conf);
		Path hdfsPath = new Path("tree_model.ser");

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try 	{
			fos = new FileOutputStream("tree_model1.ser");
		    out = new ObjectOutputStream(fos);
		    out.writeObject(model);
		    out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		// upload the file to hdfs. Overwrite any existing copy.
		fs.copyFromLocalFile(false, true, new Path("tree_model1.ser"),
				hdfsPath);

		DistributedCache.addCacheFile(new URI("tree_model1.ser"), conf);
	}
	
	public void startJob() throws Exception {
		//ToolRunner.run(new ExpandNodesController(), new String[]{"test", "test_out"});
		DecisionTree model = new DecisionTree();
		MainControllerThread.serializeModelToDFS(model);
	}
	
	@Override
	public void run() {
		
		super.run();
	}
	
	public static void main(String[] args) throws Exception {
		MainControllerThread thread = new MainControllerThread();
		thread.startJob();
	}
}
