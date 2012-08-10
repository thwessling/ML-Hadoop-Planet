package de.uniheidelberg.cl.advprog.planet.structures;

import de.uniheidelberg.cl.advprog.planet.tree.Split;

/**
 * Represents the best model as output by a Reducer.
 * 
 * @author boegel
 *
 */
public class BestModel {
	/**
	 * The node to which this model applies.
	 */
	int node;
	/**
	 * Number of instances in the left branch.
	 */
	long leftBranchInstances;
	/**
	 * Number of instances in the right branch.
	 */
	long rightBranchInstances;
	/**
	 * Feature index to which this model applies.
	 */
	int featNum;
	/**
	 * Best split.
	 */
	Split bestSplit;
	
	
	/**
	 * Instantiates a new best model for a node.
	 * @param node The node to which this model applies.
	 */
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
