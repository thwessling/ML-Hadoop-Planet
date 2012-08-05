package de.uniheidelberg.cl.advprog.planet.tree;

import java.util.Set;

public class UnorderedAttribute extends Attribute {

	public UnorderedAttribute(String name, int index) {
		super(name, index);
	}
	
	@Override
	public Set<Split> getPossibleSplits() {
		// TODO do some magic
		return super.getPossibleSplits();
	}

}
