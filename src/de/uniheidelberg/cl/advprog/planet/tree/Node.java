package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Models a node in a decision tree.
 * 
 * @author boegel
 *
 */
public abstract class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7560082326651977480L;
	/**
	 * Specifies if this is a leaf node.
	 */
	protected boolean isLeaf;
	/**
	 * List of daughter nodes.
	 */
	private List<Node> daughters;
	/**
	 * Mother node.
	 */
	protected Node mother;
	/**
	 * Node node.
	 */
	protected String name;
	/**
	 * Average y value of this node.
	 */
	protected double averageY;
	/**
	 * Number of instances at this node.
	 */
	protected double instances;
	/**
	 * Node index.
	 */
	private int nodeIndex;
	
	/**
	 * Instantiates a new node with the specified name.
	 * @param name
	 */
	public Node(String name) {
		this.name = name;
		this.daughters = new ArrayList<Node>();
	}
	/**
	 * Setter for the node name.
	 * @param name Node name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Getter for the node index.
	 * @return Node index.
	 */
	public int getNodeIndex() {
		return nodeIndex;
	}
	/**
	 * Setter for the node index.
	 * @param nodeIndex Node index.
	 */
	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}
	/**
	 * Setter for the mother node.
	 * @param mother Mother node of this node.
	 */
	public void setMother(Node mother) {
		this.mother = mother;
	}
	/**
	 * Getter for the mother node.
	 * @return Mother node of this node.
	 */
	public Node getMother() {
		return mother;
	}
	/**
	 * Adds a new daughter node. 
	 * @param daughter The daughter node to be added.
	 */
	public void addDaughter(Node daughter) {
		this.daughters.add(daughter);
	}
	/**
	 * Getter for the daughter nodes of this node. 
	 * @return Daugther nodes of this node.
	 */
	public List<Node> getDaughters() {
		return daughters;
	}
	/**
	 * Returns whether this is a leaf node. 
	 * @return <code>True</code> if this is a leaf node, <code>false</code> otherwise.
	 */
	public boolean isLeaf() {
		return isLeaf;
	}
	/**
	 * Setter for the average y value of this node.
	 * @param averageY The average y value of this node.
	 */
	public void setAverageY(double averageY) {
		this.averageY = averageY;
	}
	/**
	 * Getter of the average y value of this node.
	 * @return The average y value of this node.
	 */
	public double getAverageY() {
		return averageY;
	}
	/**
	 * Setter for the number of instances at this node. 
	 * @param instances The number of instances at this node. 
	 */
	public void setInstances(double instances) {
		this.instances = instances;
	}
	/**
	 * Getter for the number of instances at this node.
	 * @return Number of instances at this node.
	 */
	public double getInstances() {
		return instances;
	}
	
	@Override
	public abstract String toString();
	
}
