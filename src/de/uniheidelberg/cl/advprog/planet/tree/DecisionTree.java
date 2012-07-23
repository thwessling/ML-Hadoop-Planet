package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DecisionTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Set<Node> nodeSet;
	private Set<Attribute> attributeSet;
	private Node root;
	
	
	
	public DecisionTree() {
		nodeSet = new HashSet<Node>();
		attributeSet = new HashSet<Attribute>();
	}
	
	public void setRoot(Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void addNode(Node n, Node mother) {
		this.nodeSet.add(n);
		n.setMother(mother);
		mother.addDaughter(n);
	}
	
	public void addAttribute(Attribute a) { 
		this.attributeSet.add(a);
	}
	
	private Node getNode(Node mother, int idx, double value) {
		for (Node d : mother.getDaughters()) {
				if (d.getFeatureIndex() == idx) {
					BranchingNode node = (BranchingNode) d;
					
				}
		}
	}
	
	private boolean traverseTree(Node n, int feature, int value) {
	
	public boolean isFeatureActive(double[] featureValues, int featureIdx, double value) {
		// go down the tree up to featureIdx
		for (int i = 0; i < featureIdx - 1; i++) {
			
		}
		return true;
	}
		
	
		
	}
	
	
}
