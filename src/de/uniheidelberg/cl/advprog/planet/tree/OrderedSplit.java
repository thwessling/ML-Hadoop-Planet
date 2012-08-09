package de.uniheidelberg.cl.advprog.planet.tree;

import de.uniheidelberg.cl.advprog.planet.tree.Split.SPLITTYPE;

public class OrderedSplit extends Split implements Splittable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -885107084327205200L;

	/**
	 * Everything smaller than the threshold: left branch; anything else: right branch.
	 */
	private double threshold;

	
	public OrderedSplit(int feature) {
		super(SPLITTYPE.ORDERED, feature);
	}
	
	@Override
	public BRANCH getBranchForValue(double value) {
		if (value < threshold) return BRANCH.LEFT;
		else return BRANCH.RIGHT;
	}

	public double getThreshold() {
		return threshold;
	}
	public void setOrderedSplit(double threshold) {
		this.threshold = threshold; 
	}
	
	@Override
	public String getHadoopString() {
		return String.valueOf(this.threshold);
	}
	
	@Override
	public String toString() {
		return " threshold: " + this.threshold;
	}
	
}
