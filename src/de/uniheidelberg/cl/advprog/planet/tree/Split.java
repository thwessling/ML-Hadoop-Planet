package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;

public class Split implements Serializable {

	private double[] leftBranch;
	private double[] rightBranch;
	private double threshold;
	
	public enum SPLITTYPE {
		NUMERIC,CATEGORIAL;
	}
	private SPLITTYPE type;
	
	public Split(SPLITTYPE type) {
		this.type = type;
	}
	
	public void setOrderedSplit(double threshold) {
		this.threshold = threshold; 
	}
	
}
