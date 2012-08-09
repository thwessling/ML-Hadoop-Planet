package de.uniheidelberg.cl.advprog.planet.structures;

import de.uniheidelberg.cl.advprog.planet.tree.Split;

public class BestModel {
	int node;
	long leftBranchInstances;
	long rightBranchInstances;
	int featNum;
	Split bestSplit;
	
	
	public BestModel(int node) {
		this.node = node;
	}
	public void setBestSplit(Split bestSplit) {
		this.bestSplit = bestSplit;
	}
	public Split getBestSplit() {
		return bestSplit;
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
	
}
