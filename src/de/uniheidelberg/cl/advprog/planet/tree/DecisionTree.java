package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DecisionTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<Node> nodeSet;
	private List<Attribute> attributeSet;
	private Node root;
	private HashMap<Integer, Node> idxToNode;
	
	
	
	public DecisionTree() {
		nodeSet = new HashSet<Node>();
		attributeSet = new ArrayList<Attribute>();
		this.idxToNode = new HashMap<Integer, Node>();
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public Node getNodeById(int id) {
		return this.idxToNode.get(id);
	}
	
	public void addNode(Node n, Node mother) {
		this.nodeSet.add(n);
		n.setMother(mother);
		if (mother != null)
			mother.addDaughter(n);
		this.idxToNode.put(n.getFeatureIndex(), n);
	}
	
	public void addAttribute(Attribute a) { 
		this.attributeSet.add(a);
		a.setIndex(this.attributeSet.size());
	}
	public List<Attribute> getAttributeSet() {
		return attributeSet;
	}
	
	
	public boolean isFeatureActive(Double[] doubles, int featureIdx, double value) {
		Node motherNode = this.root;
		// go down the tree up to featureIdx
		for (int i = 0; i < featureIdx - 1; i++) {
			if (motherNode.getFeatureIndex() == featureIdx) {
				BranchingNode branchingMother = (BranchingNode) motherNode;
				if (doubles[i] < branchingMother.getSplit()) {
					// go down left branch
					motherNode = branchingMother.getDaughters().get(0);
				} else {
					motherNode = branchingMother.getDaughters().get(1);
				}
			}
		}
		if (motherNode.getFeatureIndex() == featureIdx) 
			return true;
		else 
			return false;
		
	}
	
		
	
		
	
}
