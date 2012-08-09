package de.uniheidelberg.cl.advprog.planet.tree;

public class BranchingNode extends Node {
	private Attribute att;
//	private double split;
	
	public BranchingNode(String name) {
		super(name);
		this.isLeaf = false;
	}
	
//	public double getSplit() {
//		return split;
//	}
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
			return "[BRAN] " + this.name + " att '" + att.getAttributeName() + "' split " + att.getSplit().toString() + avgYString;
		else 
			return "[BRAN] " + this.name + " att '" + att.getAttributeName() + "'" + avgYString;
	}

}
