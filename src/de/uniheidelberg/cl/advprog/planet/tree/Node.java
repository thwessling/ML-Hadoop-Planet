package de.uniheidelberg.cl.advprog.planet.tree;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String attributeName;
	protected boolean isLeaf;
	private List<Node> daughters;
	private Node mother;
	
	public Node(String name) {
		this.attributeName = name;
		this.daughters = new ArrayList<Node>();
	}
	
	public void setMother(Node mother) {
		this.mother = mother;
	}
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
	public String getAttributeName() {
		return attributeName;
	}
	
}
