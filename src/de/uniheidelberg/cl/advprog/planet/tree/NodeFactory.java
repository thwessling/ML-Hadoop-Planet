package de.uniheidelberg.cl.advprog.planet.tree;

import de.uniheidelberg.cl.advprog.planet.structures.BestModel;

public class NodeFactory {

	
	private Node getNode(double instanceNum, double avgY, Attribute nextAtt) {
		Node n;
		if (instanceNum < 20) {
			/*
			 * Less than 5 instances: add a leaf node.
			 */
			n = new LeafNode("leaf");
		} else {
			/*
			 * Add a new branching node with the next feature.
			 */
			n = new BranchingNode("node@" + nextAtt.getAttributeName());
			((BranchingNode) n).setAtt(nextAtt);
		}
		n.setInstances(instanceNum);
		n.setAverageY(avgY);
		return n;
	}
	
	
	/**
	 * Processes the best model and adds two new nodes to a mother node.
	 * 
	 * @param model The best model.
	 * @param mother The mother node under which two new models should be added.
	 * @param nextAtt The next attribute to be processed.
	 * @return Two daugther nodes under the moder node.
	 */
	public Node[] expandNode(BestModel model, BranchingNode mother, Attribute nextAtt) {
		Node[] nodes = new Node[2];
		mother.getAtt().setSplit(model.getBestSplit());
		double leftSize = model.getLeftBranchInstances();
		double leftAvgY = model.getBestSplit().getLeftBranchY() / leftSize;
		double rightSize = model.getRightBranchInstances();
		double rightAvgY = model.getBestSplit().getRightBranchY() / rightSize;

		nodes[0] = this.getNode(leftSize, leftAvgY, nextAtt);
		nodes[1] = this.getNode(rightSize, rightAvgY, nextAtt);
		return nodes;
	}
	
	
}
