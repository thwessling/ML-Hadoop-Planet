package de.uniheidelberg.cl.advprog.planet.tree;

import java.util.Set;
import java.util.TreeSet;

public class Attribute {
	private String attributeName;
	private boolean isOrdered;
	private Set<String> values;
	
	public Attribute(String name) {
		this.attributeName = name;
		this.values = new TreeSet<String>();
	}
	
	public void addValue(String value) {
		this.values.add(value);
	}
	public Set<String> getValues() {
		return values;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public boolean isOrdered() {
		return isOrdered;
	}
}
