package de.uniheidelberg.cl.advprog.planet.tree;

/**
 * A leaf node.
 * @author boegel
 *
 */
public class LeafNode extends Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6179860484928822981L;

	/**
	 * Instantiates a new leaf node. 
	 * @param name Name of the node.
	 */
	public LeafNode(String name) {
		super(name);
		this.isLeaf = true;
	}

	@Override
	public String toString() {
		return String.format("[LEAF] (avg y: %.2f) %s inst", this.averageY,this.instances);
	}
}
