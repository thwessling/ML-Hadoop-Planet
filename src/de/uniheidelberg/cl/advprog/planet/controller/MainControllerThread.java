package de.uniheidelberg.cl.advprog.planet.controller;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniheidelberg.cl.advprog.graph.GraphWriter;
import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.io.InstanceReader;
import de.uniheidelberg.cl.advprog.planet.io.OutputReader;
import de.uniheidelberg.cl.advprog.planet.structures.BestModel;
import de.uniheidelberg.cl.advprog.planet.tree.Attribute;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.LeafNode;
import de.uniheidelberg.cl.advprog.planet.tree.Node;

public class MainControllerThread extends Thread {

	private static final String OUTPUTPATH = "test_out_";
	
	DecisionTree model;
	/**
	 * Nodes for which D* is too large to fit in memory.
	 */
	LinkedList<BranchingNode> mrq;
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
		int featureIndex = 0;
		for (int i = 0; i<featuresString.length-1; i++) {
			String f = featuresString[i];
			// split the feature file and read value range
			Attribute att = new Attribute(f.split(":")[0],featureIndex);
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
			} else { // not ordered; possible values separated by ;
				att.setOrdered(false);
				for (String val : f.split(":")[1].split(";")) {
					att.addValue(Double.valueOf(val));
				}
			}
			this.features.add(att);
			tree.addAttribute(att);
			featureIndex++;
		}
		Attribute rootAtt = this.features.poll();
		BranchingNode n = new BranchingNode("node@" + rootAtt.getAttributeName());
		n.setAtt(rootAtt);
		tree.setRoot(n);
		tree.addNode(n, null);
		this.mrq.add(n);
		
		return tree;
	}
	
	
	private void addResult(DecisionTree tree, BestModel model) {
		// add the best split to the decision tree
		BranchingNode n = (BranchingNode) tree.getNodeById(model.getNode());
		n.getAtt().setSplit(model.getBestSplit());
		// compute data set size in left and right branch
		double leftSize = model.getLeftBranchInstances();
		double rightSize = model.getRightBranchInstances();
		double rightAvgY = model.getBestSplit().getRightBranchY() / rightSize;
		double leftAvgY = model.getBestSplit().getLeftBranchY() / leftSize;
//		Attribute nextAtt = this.features.poll();
		Attribute nextAtt = this.model.getAttributeByIdx(n.getAtt().getIndex()+1);
		if (leftSize < 5 || nextAtt == null) {
			LeafNode leaf = new LeafNode("leaf");
			tree.addNode(leaf, n);
			leaf.setInstances(leftSize);
			leaf.setAverageY(leftAvgY);
		} else {
			BranchingNode n_daughter = new BranchingNode("node@" + nextAtt.getAttributeName());
			n_daughter.setAtt(nextAtt);
			tree.addNode(n_daughter, n);
			n_daughter.setAverageY(leftAvgY);
			n_daughter.setAtt(nextAtt);
			n_daughter.setInstances(leftSize);
			this.mrq.addLast(n_daughter);
		}
		if (rightSize < 5 || nextAtt == null) {
			LeafNode leaf = new LeafNode("leaf");
			tree.addNode(leaf, n);
			leaf.setInstances(rightSize);
			leaf.setAverageY(rightAvgY);
		} else {
			BranchingNode n_daughter = new BranchingNode("node@" + nextAtt.getAttributeName() + " [" + rightAvgY + "]");
			n_daughter.setAtt(nextAtt);
			tree.addNode(n_daughter, n);
			n_daughter.setInstances(rightSize);
			n_daughter.setAverageY(rightAvgY);
			n_daughter.setAtt(nextAtt);
			this.mrq.addLast(n_daughter);
		}
//        this.model.printTree(this.model.getRoot());
        GraphWriter.writeGraph("graph.dot", tree);
	}
	
	private void addLeafNode(BranchingNode motherNode) {
		LeafNode ln = new LeafNode("leaf");
		ln.setAverageY(motherNode.getAverageY());
		ln.setInstances(motherNode.getInstances());
		this.model.addNode(ln, motherNode);
	}
	
	public void loop() throws Exception  {
		int counter = 0;
		while (this.mrq.size() > 0) {
			String outPath = OUTPUTPATH.concat(String.valueOf(counter));
			BranchingNode n = this.mrq.removeFirst();
			System.out.println("Processing node " + n);
			// start mr_expandNodes
			ExpandNodesController contr = new ExpandNodesController(n.getAtt().getIndex(),n.getNodeIndex(), model);
			// read results and determine best split for node
			contr.run(new String[]{"test", outPath});
			// add the split information to the model file
			OutputReader reader = new OutputReader();
			Map<Integer, BestModel> models = reader.readBestModels(outPath, model);
			counter += 1;
			if (models == null) {
				this.addLeafNode(n);
				continue;
			}
				
			for (BestModel model : models.values()) {
				this.addResult(this.model, model);
			}
		}

	}

    public void deleteReducerOutput() {
        File outputDir = new File("./test_out/");
        if (!outputDir.exists())
            return;
        for (File outputFile : outputDir.listFiles()) {
            outputFile.delete();
        }
        System.out.println(outputDir.delete());
        System.out.println("Output dir deleted");
    }
	
	public void startJob() throws Exception {
        this.deleteReducerOutput();
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
