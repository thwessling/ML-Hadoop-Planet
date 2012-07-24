package de.uniheidelberg.cl.advprog.planet.tree;

public class BranchingNode extends Node {
	private Attribute att;
	private double split;
	
	public BranchingNode(String name) {
		super(name);
		this.isLeaf = false;
	}
	
	public double getSplit() {
		return split;
	}
	public Attribute getAtt() {
		return att;
	}
	
	public void setAtt(Attribute att) {
		this.att = att;
	}

}
