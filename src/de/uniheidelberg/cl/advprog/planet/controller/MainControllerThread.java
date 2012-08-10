package de.uniheidelberg.cl.advprog.planet.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uniheidelberg.cl.advprog.graph.GraphWriter;
import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.io.InstanceReader;
import de.uniheidelberg.cl.advprog.planet.io.OutputReader;
import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.structures.BestModel;
import de.uniheidelberg.cl.advprog.planet.tree.Attribute;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.LeafNode;
import de.uniheidelberg.cl.advprog.planet.tree.Node;
import de.uniheidelberg.cl.advprog.planet.tree.NodeFactory;
import de.uniheidelberg.cl.advprog.planet.tree.OrderedSplit;

/**
 * This class controls the decision tree model creation by 
 * reading feature specifications and sending partial jobs to 
 * Hadoop. The produced results are parsed and integrated into 
 * the model file. 
 *  
 * @author boegel
 *
 */
public class MainControllerThread {

	private static final String OUTPUTPATH = "test_out_";
	
	/**
	 * Decision tree model.
	 */
	DecisionTree model;
	/**
	 * Nodes for which D* is too large to fit in memory.
	 */
	LinkedList<BranchingNode> mrq;
	/**
	 * Nodes for which D* fits in memory.
	 */
	List<Node> inMemQ;
	
	/**
	 * Creates a new instance and initialized collections.
	 */
	public MainControllerThread() {
		this.mrq  = new LinkedList<BranchingNode>();
		this.inMemQ = new ArrayList<Node>();
	}
	
	/**
	 * Create the initial decision tree model with a root node and 
	 * a list of features.
	 * @return {@link DecisionTree} model.
	 * @throws IOException If the feature specification cannot be read.
	 */
	private DecisionTree createInitialModel() throws IOException {
		DecisionTree tree = new DecisionTree();
		// read the list of attributes and feature ranges
		InstanceReader reader = new InstanceReader();

		for (Attribute att : reader.readAttributeSpecs()) {
			tree.addAttribute(att);
		}
		// set the first feature as the root node
		Attribute rootAtt = tree.getAttributeByIdx(0);
		BranchingNode n = new BranchingNode("node@" + rootAtt.getAttributeName());
		n.setAtt(rootAtt);
		tree.setRoot(n);
		tree.addNode(n, null);
		this.mrq.add(n);
		return tree;
	}
	
	/**
	 * Adds the best split to the decision tree and determines 
	 * the nodes to be processed next. 
	 * 
	 * @param model The best model for the current node.
	 */
	private void addResult(BestModel model) {
		// get the node for this best split
		BranchingNode n = (BranchingNode) this.model.getNodeById(model.getNode());
		n.getAtt().setSplit(model.getBestSplit());

		// add new nodes to the mother node depending on the best model
		NodeFactory factory = new NodeFactory();
		Attribute nextAtt = this.model.getAttributeByIdx(n.getAtt().getIndex()+1);
		Node[] daughters = factory.expandNode(model, n, nextAtt);
		
		for (Node d : daughters) {
			this.model.addNode(d, n);
			if (!d.isLeaf()) {
				/*
				 * if this is not a leaf: add it to the queue of processable nodes
				 */
				this.mrq.addLast((BranchingNode) d);
			}
		}
        GraphWriter.writeGraph("graph.dot", this.model);
	}
	
	/**
	 * If a node cannot be branched: add a leaf node directly under it.
	 * 
	 * @param motherNode The node to which a leaf node should be added.
	 */
	private void addLeafNode(BranchingNode motherNode) {
		LeafNode ln = new LeafNode("leaf");
		ln.setAverageY(motherNode.getAverageY());
		ln.setInstances(motherNode.getInstances());
		/* set the motherNode split to a very high value such that
		 * we always arrive at the leaf node */ 
		if (motherNode.getAtt().isOrdered()) {
			OrderedSplit s = new OrderedSplit(motherNode.getAtt().getIndex());
			s.setOrderedSplit(Double.MAX_VALUE);
			motherNode.getAtt().setSplit(s);
		}
		this.model.addNode(ln, motherNode);
	}
	
	/**
	 * This is the main loop sending jobs for all features 
	 * in the tree to Mappers/Reducers and reading/reacting 
	 * to their output.
	 *  
	 * @throws Exception Global bail-out in case anything goes wrong.
	 */
	public void loop() throws Exception  {
		int counter = 0;
		// while we still have nodes to process
		while (this.mrq.size() > 0) {
			// output path consists of standard output path + an incrementing counter
			String outPath = OUTPUTPATH.concat(String.valueOf(counter));
			// get the next node to process
			BranchingNode n = this.mrq.removeFirst();
			System.out.println("Processing node " + n);

			// start and wait for hadoop job: mr_expandNodes
			ExpandNodesController contr = new ExpandNodesController(n.getAtt().getIndex(),n.getNodeIndex(), model);
			contr.run(new String[]{"test", outPath});

			// read results and determine best split for node
			OutputReader reader = new OutputReader();
			Map<Integer, BestModel> models = reader.readBestModels(outPath, model);
			counter += 1;
			/*
			 *  if no best split could be determined: add a leaf node directly under the node
			 */
			if (models == null) {
				this.addLeafNode(n);
				continue;
			}
				
			for (BestModel model : models.values()) {
				this.addResult(model);
			}
		}

	}

	/**
	 * Starts the learning job by reading feature specs 
	 * and kicking off the learning process.
	 * @throws Exception In case anything goes wrong: abort everything.
	 */
	public void startJob() throws Exception {
		this.model = createInitialModel();
		loop();
		// serialize the final model
	    Serializer.serializeModelToFile(model);
	}
	
	
	public static void main(String[] args) throws Exception {
		MainControllerThread thread = new MainControllerThread();
		thread.startJob();
	}
}
