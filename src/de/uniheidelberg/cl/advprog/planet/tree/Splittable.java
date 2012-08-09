package de.uniheidelberg.cl.advprog.planet.tree;

import de.uniheidelberg.cl.advprog.planet.tree.Split.BRANCH;

public interface Splittable {

	
	public BRANCH getBranchForValue(double value);
	
}
