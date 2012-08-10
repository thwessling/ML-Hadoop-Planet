package de.uniheidelberg.cl.advprog.planet.tree;

/**
 * A branching node.
 * 
 * @author boegel
 *
 */
public class BranchingNode extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = -740857178045129631L;
	
	/**
	 * The attribute that is used to branch this node.
	 */
	private Attribute att;
	
	/**
	 * Instantiates a new {@link BranchingNode} with the specified name.
	 * @param name
	 */
	public BranchingNode(String name) {
		super(name);
		this.isLeaf = false;
	}
	
	/**
	 * Getter for the branching attribute.
	 * @return The branching attribute.
	 */
	public Attribute getAtt() {
		return att;
	}
	
	/**
	 * Setter for the branching attribute.
	 * @param att The branching attribute.
	 */
	public void setAtt(Attribute att) {
		this.att = att;
	}
	
	@Override
	public String toString() {
		String avgYString = String.format(" avg Y: (%.2f)", this.averageY) ;
		if (this.att.getSplit() != null)
			return "'" + this.name + "'@" + this.getNodeIndex() +  " (" + att.getSplit().toString() + ")" + avgYString;
		else 
			return "'" + this.name + "'@" + this.getNodeIndex() + " " + avgYString;
	}

}
