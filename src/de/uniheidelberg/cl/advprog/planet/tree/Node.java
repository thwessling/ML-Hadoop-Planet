package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7560082326651977480L;
	protected boolean isLeaf;
	private List<Node> daughters;
	private Node mother;
	private String name;
	
	public Node(String name) {
		this.name = name;
		this.daughters = new ArrayList<Node>();
	}
	
	public void setMother(Node mother) {
		this.mother = mother;
	}
	
//	public void setFeatureIndex(int featureIndex) {
//		this.featureIndex = featureIndex;
//	}
//	public int getFeatureIndex() {
//		return featureIndex;
//	}
	
	public Node getMother() {
		return mother;
	}
	public void addDaughter(Node daughter) {
		this.daughters.add(daughter);
	}
	
	public List<Node> getDaughters() {
		return daughters;
	}
	public boolean isLeaf() {
		return isLeaf;
	}
	
	@Override
	public String toString() {
		return String.valueOf("Node " + this.name + "; leaf? " + this.isLeaf() );
	}
	
}
