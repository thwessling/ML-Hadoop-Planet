package de.uniheidelberg.cl.advprog.planet.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uniheidelberg.cl.advprog.planet.tree.Split.BRANCH;

/**
 * This class models a binary decision tree.
 * A decision tree consists of nodes and connection between nodes. 
 * A node can either be a branching node or a leaf node. A branching node separates an instance 
 * depending on its feature value and assigns it to the left or right daughter node. Thus, 
 * each branching node corresponds to one specific attribute.
 * 
 * A leaf node contains an average Y value from which a class label can be derived.
 *  
 * @author boegel
 *
 */
public class DecisionTree implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Set of Nodes in this decision tree.
	 */
	private Set<Node> nodeSet;
	/**
	 * Set of attributes in this tree.
	 */
	private List<Attribute> attributeSet;
	/**
	 * Root node.
	 */
	private Node root;
	/**
	 * Mapping from feature indices to node objects to access a node for a specific feature.
	 * Note that the feature index is NOT identical to the node index.
	 */
	private HashMap<Integer, BranchingNode> featIdxToNode;
	/**
	 * Mapping from node index to the corresponding node.
	 */
	private HashMap<Integer, BranchingNode> nodeIdxToNode;
	
	/**
	 * Instantiates a new decision tree and initializes collections.
	 */
	public DecisionTree() {
		nodeSet = new HashSet<Node>();
		attributeSet = new ArrayList<Attribute>();
		this.featIdxToNode = new HashMap<Integer, BranchingNode>();
		this.nodeIdxToNode = new HashMap<Integer, BranchingNode>();
	}
	/**
	 * Setter for the root node.
	 * @param root Root node.
	 */
	public void setRoot(Node root) {
		this.root = root;
	}
	
	/**
	 * Getter for the root node.
	 * @return Root node.
	 */
	public Node getRoot() {
		return root;
	}
	/**
	 * Returns the attribute with the specified index.
	 * @param id The index of the attribute to be retrieved. 
	 * @return Attribute with the specified index.
	 */
	public Attribute getAttributeByIdx(int id) {
		if (id+1 > this.attributeSet.size())
			return null;
		return this.attributeSet.get(id);
	}
	
	/**
	 * Returns the node with the specified index.
	 * @param id The index of the node to be retrieved. 
	 * @return The node with the specified index.
	 */
	public BranchingNode getNodeById(int id) {
		return this.nodeIdxToNode.get(id);
	}
	
	/**
	 * Ads a new node under an already existing one.
	 * @param n The node to be added. 
	 * @param mother The node to be used as its mother.
	 */
	public void addNode(Node n, Node mother) {
		this.nodeSet.add(n);
		n.setMother(mother);
		if (mother != null)
			mother.addDaughter(n);
		if (! n.isLeaf()) {
			// if this is a branching node: add the feature->node mapping
			BranchingNode b = (BranchingNode) n;
			this.featIdxToNode.put(b.getAtt().getIndex(), b);
			int newNodeIdx = this.nodeIdxToNode.size();
			b.setNodeIndex(newNodeIdx);
			this.nodeIdxToNode.put(newNodeIdx, b);
		}
	}
	/**
	 * Adds a new attribute to the tree.
	 * @param a The attribute to be added.
	 */
	public void addAttribute(Attribute a) { 
		this.attributeSet.add(a);
		a.setIndex(this.attributeSet.size()-1);
	}
	/**
	 * Gets the set of attributes. 
	 * @return Set of attributes in this tree.
	 */
	public List<Attribute> getAttributeSet() {
		return attributeSet;
	}
	
	/**
	 * Classifies an instance (specified by a double[]) by following 
	 * the tree and returning the average y value.
	 * 
	 * @param instance The instance to be classified.
	 * @return Average y value (class label).
	 */
	public double classify(Double[] instance) {
		BranchingNode motherNode = (BranchingNode) this.root;
		// go down the tree for all instances (except the class label)
		for (int i = 0; i < instance.length-1 ; i++) {
			if (motherNode.getAtt().getIndex() == i) {
				if (motherNode.getAtt().getSplit().getBranchForValue(instance[i]).equals(BRANCH.LEFT)) {
					// go down left branch
					Node leftBranchTarget = motherNode.getDaughters().get(0);
					if (leftBranchTarget.isLeaf())
						return leftBranchTarget.averageY;
					motherNode = (BranchingNode) leftBranchTarget;
				} else {
					// go down left branch
					Node rightBranchTarget = motherNode.getDaughters().get(1);
					if (rightBranchTarget.isLeaf())
						return rightBranchTarget.averageY;
					motherNode = (BranchingNode) rightBranchTarget;
				}
			}
		}
		return -1.0;
	}
	/**
	 * Determines whether an instance actually needs to be investigated as input for a node. 
	 * 
	 * @param featureValues Feature vector of the instance.
	 * @param featureIdx Feature index of the currently investigated feature in the vector. 
	 * @param nodeIndex Node index to check if the instance is input of the node.
	 * @return <code>True</code> if the instance is input to the node, <code>false</code> otherwise.
	 */
	public boolean isInstanceActive(Double[] featureValues, int featureIdx, int nodeIndex) {
		BranchingNode motherNode = (BranchingNode) this.root;
		// go down the tree up to featureIdx
		for (int i = 0; i < featureIdx ; i++) {
			if (motherNode.getAtt().getIndex() == i) {
				if (motherNode.getAtt().getSplit().getBranchForValue(featureValues[i]).equals(BRANCH.LEFT)) {
					// go down left branch
					Node leftBranchTarget = motherNode.getDaughters().get(0);
					if (leftBranchTarget.isLeaf())
						return false;
					motherNode = (BranchingNode) leftBranchTarget;
				} else {
					// go down left branch
					Node rightBranchTarget = motherNode.getDaughters().get(1);
					if (rightBranchTarget.isLeaf())
						return false;
					motherNode = (BranchingNode) rightBranchTarget;
				}
			}
		}
		//if (motherNode.getAtt().getIndex() == featureIdx)
		if (motherNode.getNodeIndex() == nodeIndex)
			return true;
		else 
			return false;
		
	}
	
	/**
	 * Prints a tree representation of the tree.
	 * 
	 * @param n Root node.
	 */
	public void printTree(Node n) {
		String motherString = "<null>";
		if (n.getMother() != null) {
			motherString = "<" + n.getMother().toString() + ">";
		}
		System.out.println(motherString + " --> " + n.toString());
		if (n.getDaughters().size() > 0) {
            for (int i = 0; i < n.getDaughters().size() ; i++) {
                printTree(n.getDaughters().get(i));
                //System.out.println(n.toString() + " --> " + n.getDaughters().get(i));
            }
		} else {
            return;
        }
           
	}
		
	
	
}
