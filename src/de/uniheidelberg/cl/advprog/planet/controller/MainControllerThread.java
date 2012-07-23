package de.uniheidelberg.cl.advprog.planet.controller;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;
import org.apache.hadoop.util.ToolRunner;


import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.TreeModel;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
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
	
	Queue<String> features;
	
	public MainControllerThread() {
		this.mrq  = new ArrayList<Node>();
		this.inMemQ = new ArrayList<Node>();
	}
	
	private DecisionTree createInitialModel() throws IOException {
		DecisionTree tree = new DecisionTree();
		BufferedReader br = new BufferedReader(new FileReader("data/features.txt"));
		String[] featuresString = br.readLine().split(",");
		this.features = new LinkedList<String>();
		for (String f : featuresString) {
			this.features.add(f);
		}
		Node n = new BranchingNode(this.features.poll());
		tree.setRoot(n);
		n.setFeatureIndex(0);
		this.mrq.add(n);
		
		return tree;
	}
	
	public void startJob() throws Exception {
		DecisionTree model = createInitialModel();
		JobConf conf = new JobConf();
		Serializer.serializeModelToDFS(model, conf);
		// start mr_expandNodes
		ToolRunner.run(new ExpandNodesController(this.mrq.get(0).getFeatureIndex()), new String[]{"test", "test_out"});
		// read results and determine best split for node
		
		// add the split information to the model file
		
		// compute next nodes to be expanded
		
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
