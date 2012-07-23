package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class Attribute implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8677216573475157715L;
	private String attributeName;
	private boolean isOrdered;
	private Set<Double> values;
	
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
	public boolean isOrdered() {
		return isOrdered;
	}
}
