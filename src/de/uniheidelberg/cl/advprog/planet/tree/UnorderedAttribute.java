package de.uniheidelberg.cl.advprog.planet.tree;

import java.util.Set;

/**
 * Models an unordered attribute (categorial).
 * 
 * @author boegel
 *
 */
public class UnorderedAttribute extends Attribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5867764602080938954L;

	/**
	 * Instantiates a new unordered attribute.
	 * @param name Attribute name. 
	 * @param index Attribute index.
	 */
	public UnorderedAttribute(String name, int index) {
		super(name, index);
	}
	
	@Override
	public Set<Split> getPossibleSplits() {
		return super.getPossibleSplits();
	}

}
