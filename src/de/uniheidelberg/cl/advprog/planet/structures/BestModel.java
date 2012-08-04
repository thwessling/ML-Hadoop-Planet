package de.uniheidelberg.cl.advprog.planet.structures;

public class BestModel {
	int node;
	double split;
	long leftBranchInstances;
	long rightBranchInstances;
	
	public BestModel(int node, double split, long leftb, long rightb) {
		this.node = node;
		this.split = split;
		this.leftBranchInstances = leftb;
		this.rightBranchInstances = rightb;
	}
	
	public long getLeftBranchInstances() {
		return leftBranchInstances;
	}
	
	public long getRightBranchInstances() {
		return rightBranchInstances;
	}
	
	public int getNode() {
		return node;
	}
	
	public double getSplit() {
		return split;
	}
}
