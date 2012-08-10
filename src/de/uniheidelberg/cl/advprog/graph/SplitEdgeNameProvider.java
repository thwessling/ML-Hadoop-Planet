package de.uniheidelberg.cl.advprog.graph;

import org.jgrapht.ext.EdgeNameProvider;

/**
 * Name provider for an edge, mainly consisting of the 
 * number of instances at each branch.
 *  
 * @author boegel
 *
 */
public class SplitEdgeNameProvider implements EdgeNameProvider<SplitEdge> {


	@Override
	public String getEdgeName(SplitEdge arg0) {
		return arg0.getInstanceNum() + " instances"; 
	}

}
