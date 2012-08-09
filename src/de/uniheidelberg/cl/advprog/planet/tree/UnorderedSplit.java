package de.uniheidelberg.cl.advprog.planet.tree;

import java.util.ArrayList;
import java.util.List;

public class UnorderedSplit extends Split {

	private List<Double> leftBranch;

	
	
	public UnorderedSplit(int feature) {
		super(SPLITTYPE.CATEGORIAL, feature);
		this.leftBranch = new ArrayList<Double>();
	}

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
	
	public void removeItem(double val) {
		this.leftBranch.remove(val);
	}

	@Override
	public String toString() {
		String out = "Left branch values: ";
		for (Double val : this.leftBranch) {
			out = out + val + ";";
		}
		return out;
	}
	
}
