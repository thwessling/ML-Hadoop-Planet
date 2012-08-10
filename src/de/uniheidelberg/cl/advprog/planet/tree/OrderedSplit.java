package de.uniheidelberg.cl.advprog.planet.tree;

/**
 * An ordered split is a {@link Split} of ordered attributes, i.e. with a fixed 
 * threshold.
 * 
 * 
 * @author boegel
 *
 */
public class OrderedSplit extends Split  {

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
