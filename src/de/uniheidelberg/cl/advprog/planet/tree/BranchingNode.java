package de.uniheidelberg.cl.advprog.planet.tree;

public class BranchingNode extends Node {
	private Attribute att;
//	private double split;
	
	public BranchingNode(String name) {
		super(name);
		this.isLeaf = false;
	}
	
	public Attribute getAtt() {
		return att;
	}
	
	public void setAtt(Attribute att) {
		this.att = att;
	}
	
	@Override
	public String toString() {
		String avgYString = " avg Y: " + this.averageY;
		if (this.att.getSplit() != null)
			return "'" + this.name + "'@" + this.getNodeIndex() +  " split " + att.getSplit().toString() + avgYString;
		else 
			return "'" + this.name + "'@" + this.getNodeIndex() + " " + avgYString;
	}

}
