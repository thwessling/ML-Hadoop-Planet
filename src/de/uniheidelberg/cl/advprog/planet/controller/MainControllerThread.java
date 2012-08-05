package de.uniheidelberg.cl.advprog.planet.controller;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.io.OutputReader;
import de.uniheidelberg.cl.advprog.planet.structures.BestModel;
import de.uniheidelberg.cl.advprog.planet.tree.Attribute;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.LeafNode;
import de.uniheidelberg.cl.advprog.planet.tree.Node;
import de.uniheidelberg.cl.advprog.planet.tree.Split;
import de.uniheidelberg.cl.advprog.planet.tree.Split.SPLITTYPE;

public class MainControllerThread extends Thread {

	DecisionTree model;
	/**
	 * Nodes for which D* is too large to fit in memory.
	 */
	Queue<BranchingNode> mrq;
	/**
	 * Nodes for which D* fits in memory.
	 */
	List<Node> inMemQ;
	
	Queue<Attribute> features;
	
	public MainControllerThread() {
		this.mrq  = new LinkedList<BranchingNode>();
		this.inMemQ = new ArrayList<Node>();
	}
	
	private DecisionTree createInitialModel() throws IOException {
		DecisionTree tree = new DecisionTree();
		BufferedReader br = new BufferedReader(new FileReader("data/features.txt"));
		String[] featuresString = br.readLine().split(",");
		this.features = new LinkedList<Attribute>();
		for (String f : featuresString) {
			// split the feature file and read value range
			Attribute att = new Attribute(f.split(":")[0],0);
			// value range
			Pattern p = Pattern.compile("([^\\-]+)\\-(.*)");
			Matcher m = p.matcher(f.split(":")[1]);
			if (m.matches()) {
				// ordered
				double firstVal = Double.valueOf(m.group(1));
				double scndVal =  Double.valueOf(m.group(2));
				att.setOrdered(true);
				for (double val = firstVal; val <= scndVal; val++) {
					att.addValue(val);
				}
			} else { // not ordered
				att.setOrdered(false);
				for (String val : f.split(":")[1].split(";")) {
					att.addValue(Double.valueOf(val));
				}
			}
			this.features.add(att);
			tree.addAttribute(att);
		}
		Attribute rootAtt = this.features.poll();
		BranchingNode n = new BranchingNode(rootAtt.getAttributeName());
		n.setAtt(rootAtt);
		tree.setRoot(n);
		tree.addNode(n, null);
		this.mrq.add(n);
		
		return tree;
	}
	
	
	private void addResult(DecisionTree tree, BestModel model) {
		// add the best split to the decision tree
		BranchingNode n = (BranchingNode) tree.getNodeById(model.getNode());
		Split s = new Split(SPLITTYPE.NUMERIC, model.getFeatNum());
		s.setOrderedSplit(model.getSplit());
		n.getAtt().setSplit(s);
		// compute data set size in left and right branch
		double leftSize = model.getLeftBranchInstances();
		double rightSize = model.getRightBranchInstances();
		if (leftSize < 30) {
			LeafNode leaf = new LeafNode("leaf");
			tree.addNode(leaf, n);
		} else {
			Attribute nextAtt = this.features.poll();
			BranchingNode n_daughter = new BranchingNode(nextAtt.getAttributeName());
			n_daughter.setAtt(nextAtt);
			tree.addNode(n_daughter, n);
			n_daughter.setAtt(nextAtt);
			this.mrq.add(n);
		}
		if (rightSize < 30) {
			LeafNode leaf = new LeafNode("leaf");
			tree.addNode(leaf, n);
		} else {
			Attribute nextAtt = this.features.poll();
			BranchingNode n_daughter = new BranchingNode(nextAtt.getAttributeName());
			n_daughter.setAtt(nextAtt);
			tree.addNode(n_daughter, n);
			n_daughter.setAtt(nextAtt);
			this.mrq.add(n_daughter);
		}
	}
	
	
	public void loop() throws Exception {
		while (this.mrq.size() > 0) {
			BranchingNode n = this.mrq.poll();
			// start mr_expandNodes
			ExpandNodesController contr = new ExpandNodesController(n.getAtt().getIndex(), model);
			// read results and determine best split for node
			contr.run(new String[]{"test", "test_out"});
			// add the split information to the model file
			OutputReader reader = new OutputReader();
			Map<Integer, BestModel> models = reader.readBestModels();
			for (BestModel model : models.values()) {
				this.addResult(this.model, model);
			}
		}
	}
	
	public void startJob() throws Exception {
		this.model = createInitialModel();
		this.model.printTree(this.model.getRoot());
		loop();
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
