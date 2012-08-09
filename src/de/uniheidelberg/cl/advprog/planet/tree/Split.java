package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;

public class Split implements Serializable, Splittable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7173846856166158003L;
	private int feature;
	private Node node;
	private boolean isOrdered;
	private int leftBranchInstances;
	private double leftBranchY;
	private int rightBranchInstances;
	private double rightBranchY;
	
	public enum SPLITTYPE {
		ORDERED,CATEGORIAL;
	}
	
	public enum BRANCH {
		LEFT,RIGHT;
	}
	
	private SPLITTYPE type;
	
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
		return "";
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
	
	@Override
	public BRANCH getBranchForValue(double value) {
		return BRANCH.LEFT;
	}

	
	
}
