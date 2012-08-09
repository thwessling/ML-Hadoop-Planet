package de.uniheidelberg.cl.advprog.graph;

import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.graph.DefaultEdge;

public class SplitEdgeNameProvider implements EdgeNameProvider<SplitEdge> {


	@Override
	public String getEdgeName(SplitEdge arg0) {
		return arg0.getInstanceNum() + " instances"; 
	}

}
