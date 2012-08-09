package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7560082326651977480L;
	protected boolean isLeaf;
	private List<Node> daughters;
	protected Node mother;
	protected String name;
	protected double averageY;
	protected double instances;
	private int nodeIndex;
	
	public Node(String name) {
		this.name = name;
		this.daughters = new ArrayList<Node>();
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNodeIndex() {
		return nodeIndex;
	}
	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
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
	public void setAverageY(double averageY) {
		this.averageY = averageY;
	}
	public double getAverageY() {
		return averageY;
	}
	
	public void setInstances(double instances) {
		this.instances = instances;
	}
	
	public double getInstances() {
		return instances;
	}
	
	@Override
	public abstract String toString();
	
}
