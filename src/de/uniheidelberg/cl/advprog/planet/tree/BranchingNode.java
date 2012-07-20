package de.uniheidelberg.cl.advprog.planet.tree;

public class BranchingNode extends Node {
	private Attribute att;
	private String split;
	
	public BranchingNode(String name) {
		super(name);
		this.isLeaf = false;
	}
	
	public String getSplit() {
		return split;
	}
	public Attribute getAtt() {
		return att;
	}

}
