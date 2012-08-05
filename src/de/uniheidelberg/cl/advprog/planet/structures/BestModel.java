package de.uniheidelberg.cl.advprog.planet.structures;

public class BestModel {
	int node;
	double split;
	long leftBranchInstances;
	long rightBranchInstances;
	int featNum;
	
	
	public BestModel(int node, double split) {
		this.node = node;
		this.split = split;
	}
	
	public void setLeftBranchInstances(long leftBranchInstances) {
		this.leftBranchInstances = leftBranchInstances;
	}
	public int getFeatNum() {
		return featNum;
	}
	public void setFeatNum(int featNum) {
		this.featNum = featNum;
	}
	public void setRightBranchInstances(long rightBranchInstances) {
		this.rightBranchInstances = rightBranchInstances;
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
