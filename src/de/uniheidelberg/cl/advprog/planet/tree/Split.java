package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;

public class Split implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7173846856166158003L;
	private double[] leftBranch;
	private double[] rightBranch;
	private double threshold;
	private int feature;
	private Node node;
	
	public enum SPLITTYPE {
		NUMERIC,CATEGORIAL;
	}
	private SPLITTYPE type;
	
	public Split(SPLITTYPE type, int feature) {
		this.type = type;
		this.feature = feature;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	
	public double getThreshold() {
		return threshold;
	}
	public void setOrderedSplit(double threshold) {
		this.threshold = threshold; 
	}
	
	public String getSplitId() {
		return this.feature + ":" + type.toString() + ":" + this.threshold; 
	}
	
	public int getFeature() {
		return feature;
	}
	@Override
	public String toString() {
		return this.getSplitId();
	}
	
	
}
