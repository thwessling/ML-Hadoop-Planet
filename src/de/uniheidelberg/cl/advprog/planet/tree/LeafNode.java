package de.uniheidelberg.cl.advprog.planet.tree;

public class LeafNode extends Node {

	private double weight;

	public LeafNode(String name) {
		super(name);
		this.isLeaf = true;
	}

	public double getWeight() {
		return weight;
	}
}
