package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uniheidelberg.cl.advprog.planet.tree.Split.BRANCH;


public class DecisionTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<Node> nodeSet;
	private List<Attribute> attributeSet;
	private Node root;
	/**
	 * Mapping from feature indices to node objects to access a node for a specific feature.
	 * Note that the feature index is identical to the node index for convenience.
	 */
	private HashMap<Integer, BranchingNode> idxToNode;
	
	public DecisionTree() {
		nodeSet = new HashSet<Node>();
		attributeSet = new ArrayList<Attribute>();
		this.idxToNode = new HashMap<Integer, BranchingNode>();
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public BranchingNode getNodeById(int id) {
		return this.idxToNode.get(id);
	}
	
	public void addNode(Node n, Node mother) {
		this.nodeSet.add(n);
		n.setMother(mother);
		if (mother != null)
			mother.addDaughter(n);
		if (! n.isLeaf()) {
			// if this is a branching node: add the feature->node mapping
			BranchingNode b = (BranchingNode) n;
			this.idxToNode.put(b.getAtt().getIndex(), b);
		}
	}
	
	public void addAttribute(Attribute a) { 
		this.attributeSet.add(a);
		a.setIndex(this.attributeSet.size()-1);
	}
	public List<Attribute> getAttributeSet() {
		return attributeSet;
	}
	
	
	public boolean isInstanceActive(Double[] featureValues, int featureIdx) {
		BranchingNode motherNode = (BranchingNode) this.root;
		// go down the tree up to featureIdx
		for (int i = 0; i < featureIdx ; i++) {
			if (motherNode.getAtt().getIndex() == i) {
				if (motherNode.getAtt().getSplit().getBranchForValue(featureValues[i]).equals(BRANCH.LEFT)) {
					// go down left branch
					Node leftBranchTarget = motherNode.getDaughters().get(0);
					if (leftBranchTarget.isLeaf())
						return false;
					motherNode = (BranchingNode) leftBranchTarget;
				} else {
					// go down left branch
					Node rightBranchTarget = motherNode.getDaughters().get(1);
					if (rightBranchTarget.isLeaf())
						return false;
					motherNode = (BranchingNode) rightBranchTarget;
				}
			}
		}
		if (motherNode.getAtt().getIndex() == featureIdx) 
			return true;
		else 
			return false;
		
	}
	
	
	public void printTree(Node n) {
		String motherString = "<null>";
		if (n.getMother() != null) {
			motherString = "<" + n.getMother().toString() + ">";
		}
		System.out.println(motherString + " --> " + n.toString());
		if (n.getDaughters().size() > 0) {
            for (int i = 0; i < n.getDaughters().size() ; i++) {
                printTree(n.getDaughters().get(i));
                //System.out.println(n.toString() + " --> " + n.getDaughters().get(i));
            }
		} else {
            return;
        }
           
	}
		
	
	
}
