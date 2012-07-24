package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import de.uniheidelberg.cl.advprog.planet.tree.Split.SPLITTYPE;

public class Attribute implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8677216573475157715L;
	private String attributeName;
	private boolean isOrdered;
	private Set<Double> values;
	private Split split;
	
	public Attribute(String name) {
		this.attributeName = name;
		this.values = new TreeSet<Double>();
	}
	
	public void addValue(double value) {
		this.values.add(value);
	}
	public Set<Double> getValues() {
		return values;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;
	}
	
	public Set<Split> getPossibleSplits() {
		Set<Split> possibleSplits = new HashSet<Split>();
		if (this.isOrdered) {
			for (double num : this.values) {
				Split s = new Split(SPLITTYPE.NUMERIC);
				s.setOrderedSplit(num);
				possibleSplits.add(s);
			}
		}
		return possibleSplits;
	}
	
	public boolean isOrdered() {
		return isOrdered;
	}
	
	public Split getSplit() {
		return split;
	}
	public void setSplit(Split split) {
		this.split = split;
	}
}
