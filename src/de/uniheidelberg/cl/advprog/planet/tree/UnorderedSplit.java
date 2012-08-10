package de.uniheidelberg.cl.advprog.planet.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link UnorderedSplit} is a {@link Split} for an unordered attribute.
 * 
 * @author boegel
 *
 */
public class UnorderedSplit extends Split {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8647059166757797655L;
	/**
	 * Feature values for which instances are sent to the left branch.
	 */
	private List<Double> leftBranch;

	/**
	 * Instantiates a new {@link UnorderedSplit}.
	 * @param feature
	 */
	public UnorderedSplit(int feature) {
		super(SPLITTYPE.CATEGORIAL, feature);
		this.leftBranch = new ArrayList<Double>();
	}

	/**
	 * Adds a new value to the list of left branch feature values.
	 * 
	 * @param val The value to be added.
	 */
	public void addLeftBranchItem(double val) {
		this.leftBranch.add(val);
	}

	@Override
	public BRANCH getBranchForValue(double value) {
		if (this.leftBranch.contains(value)) 
			return BRANCH.LEFT;
		else 
			return BRANCH.RIGHT;
	}
	
	public List<Double> getLeftBranch() {
		return leftBranch;
	}
	
	/**
	 * Removes a feature value from the list of left branch feature values.
	 * 
	 * @param val The value to be removed.
	 */
	public void removeItem(double val) {
		this.leftBranch.remove(val);
	}

	@Override
	public String toString() {
		String out = "lbranch vals: [";
		for (Double val : this.leftBranch) {
			out = out + val + ";";
		}
		out += "]";
		return out;
	}
	
}
