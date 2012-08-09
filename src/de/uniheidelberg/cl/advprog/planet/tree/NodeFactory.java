package de.uniheidelberg.cl.advprog.planet.tree;

import de.uniheidelberg.cl.advprog.planet.structures.BestModel;

public class NodeFactory {

	
	private Node getNode(double instanceNum, Attribute nextAtt) {
		Node n;
		if (instanceNum < 1) {
			n = new LeafNode("leaf");
			n.setInstances(instanceNum);
		} else {
//			Attribute nextAtt = this.features.poll();
			n = new BranchingNode("node@" + nextAtt.getAttributeName());
			((BranchingNode) n).setAtt(nextAtt);
			n.setInstances(instanceNum);
		}
		return n;
	}
	
	
	public Node[] getNextNodes(BestModel model, BranchingNode mother) {
		Node[] nodes = new Node[2];
		mother.getAtt().setSplit(model.getBestSplit());
		double leftSize = model.getLeftBranchInstances();
		double rightSize = model.getRightBranchInstances();
		nodes[0] = this.getNode(leftSize);
		nodes[1] = this.getNode(rightSize);
		return nodes;
	}
	
	
}
