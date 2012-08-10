package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class models an attribute (feature).
 * 
 * @author boegel
 *
 */
public class Attribute implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8677216573475157715L;
	/**
	 * Attribute name.
	 */
	private String attributeName;
	/**
	 * Specifies whether this is an ordered or un-ordered attribute.
	 */
	private boolean isOrdered;
	/**
	 * Set of values this attribute can take.
	 */
	private Set<Double> values;
	/**
	 * Best split for this attribute. 
	 */
	private Split split;
	/**
	 * Set of possible splits for this attribute.
	 */
	private Set<Split> possibleSplits;
	/**
	 * Unique attribute index.
	 */
	private int index;
	/**
	 * Instantiates a new attribute. 
	 * @param name Attribute name. 
	 * @param index Attribute index.
	 */
	public Attribute(String name, int index) {
		this.attributeName = name;
		this.values = new TreeSet<Double>();
		this.index = index;
	}
	
	/**
	 * Adds a possible values to this attribute.
	 * @param value Value to be added.
	 */
	public void addValue(double value) {
		this.values.add(value);
	}
	
	/**
	 * Getter for the attribute index.
	 * @return Attribute index.
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * Getter for the attribute values.
	 * @return attribute values.
	 */
	public Set<Double> getValues() {
		return values;
	}
	/**
	 * Getter for the attribute name. 
	 * @return Attribute name.
	 */
	public String getAttributeName() {
		return attributeName;
	}
	/**
	 * Returns whether this attribute is ordered or not.
	 * @param isOrdered <code>true</code> if the attribute is ordered, <code>false</code> otherwise.
	 */
	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;
	}
	
	/**
	 * Returns the set of possible splits.
	 * @return Set of possible splits.
	 */
	public Set<Split> getPossibleSplits() {
		if (this.possibleSplits != null)
			return this.possibleSplits;
		this.possibleSplits = new HashSet<Split>();
		if (this.isOrdered) {
			for (double num : this.values) {
				OrderedSplit s = new OrderedSplit(this.index);
				s.setOrderedSplit(num);
				possibleSplits.add(s);
			}
		}
		return this.possibleSplits;
	}
	/**
	 * Setter for the attribute index.
	 * @param index Attribute index.
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * Returns whether this attribute is ordered or not.
	 * @return <code>true</code> if the attribute is ordered, <code>false</code> otherwise.
	 */
	public boolean isOrdered() {
		return isOrdered;
	}
	/**
	 * Getter for the best split of this attribute.
	 * @return The best split for this attribute.
	 */
	public Split getSplit() {
		return split;
	}
	/**
	 * Sets the best split for this attribute. 
	 * 
	 * @param split The best split for this attribute.
	 */
	public void setSplit(Split split) {
		this.split = split;
	}
}
