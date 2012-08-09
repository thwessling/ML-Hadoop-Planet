package de.uniheidelberg.cl.advprog.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

public class SplitEdge extends DefaultWeightedEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6664905708814827335L;
	private int instanceNum;
	
	
	public SplitEdge() {
		super();
	}
	
	public int getInstanceNum() {
		return instanceNum;
	}
	public void setInstanceNum(int instanceNum) {
		this.instanceNum = instanceNum;
	}
	
	
	
	
}
