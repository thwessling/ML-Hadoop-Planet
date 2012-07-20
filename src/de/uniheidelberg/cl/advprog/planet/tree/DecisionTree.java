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
	
	public DecisionTree() {
		nodeSet = new HashSet<Node>();
		attributeSet = new HashSet<Attribute>();
	}
	
	public void addNode(Node n) {
		this.nodeSet.add(n);
	}
	
	public void addAttribute(Attribute a) { 
		this.attributeSet.add(a);
	}
	
}
