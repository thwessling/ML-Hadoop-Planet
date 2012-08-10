package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;

/**
 * Models a split for an Attribute.
 * 
 * @author boegel
 *
 */
public class Split implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7173846856166158003L;
	
	/**
	 * Feature index.
	 */
	private int feature;
	/**
	 * Node of this split.
	 */
	private Node node;
	/**
	 * Specifies whether this split splits an ordered attribute.
	 */
	private boolean isOrdered;
	/**
	 * Number of instances in the left branch.
	 */
	private int leftBranchInstances;
	/**
	 * Average y value in the left branch.
	 */
	private double leftBranchY;
	/**
	 * Number of instances in the right branch.
	 */
	private int rightBranchInstances;
	/**
	 * Average y value in the right branch.
	 */
	private double rightBranchY;
	
	/**
	 * Split type
	 * @author boegel
	 *
	 */
	public enum SPLITTYPE {
		ORDERED,CATEGORIAL;
	}
	
	/**
	 * Branch to which an instance is assigned.
	 * @author boegel
	 *
	 */
	public enum BRANCH {
		LEFT,RIGHT;
	}
	
	private SPLITTYPE type;
	
	/**
	 * Creates a new Split.
	 * 
	 * @param type {@link SPLITTYPE} of the split.
	 * @param feature Feature index of this split.
	 */
	public Split(SPLITTYPE type, int feature) {
		this.type = type;
		this.feature = feature;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public boolean isOrdered() {
		return isOrdered;
	}
	
	public Node getNode() {
		return node;
	}
	public void setLeftBranchY(double leftBranchY) {
		this.leftBranchY = leftBranchY;
	}
	public void setRightBranchY(double rightBranchY) {
		this.rightBranchY = rightBranchY;
	}
	public double getLeftBranchY() {
		return leftBranchY;
	}
	public double getRightBranchY() {
		return rightBranchY;
	}
	
	public String getSplitId() {
		return "Feature [ " + this.feature + "]:" + type.toString(); 
	}
	
	public int getFeature() {
		return feature;
	}
	public String getHadoopString() {
		return String.valueOf(this.hashCode());
	}
	public int getLeftBranchInstances() {
		return leftBranchInstances;
	}
	public void setLeftBranchInstances(int leftBranchInstances) {
		this.leftBranchInstances = leftBranchInstances;
	}
	
	public int getRightBranchInstances() {
		return rightBranchInstances;
	}
	
	public void setRightBranchInstances(int rightBranchInstances) {
		this.rightBranchInstances = rightBranchInstances;
	}
	@Override
	public String toString() {
		return this.getSplitId();
	}
	
	public BRANCH getBranchForValue(double value) {
		return BRANCH.LEFT;
	}

	
	
}
