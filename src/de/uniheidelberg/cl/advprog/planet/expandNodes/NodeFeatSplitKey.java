package de.uniheidelberg.cl.advprog.planet.expandNodes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class NodeFeatSplitKey implements WritableComparable<NodeFeatSplitKey> {

	private int featId; 
	private String splitId;
	private int nodeId;
	private double varianceReduction;
	
	public NodeFeatSplitKey() {}
	
	public NodeFeatSplitKey(int nodeId, int featId, String splitId) {
		this.nodeId = nodeId;
		this.featId = featId; 
		this.splitId = splitId;
	}
	public void setVarianceReduction(double varianceReduction) {
		this.varianceReduction = varianceReduction;
	}
	public double getVarianceReduction() {
		return varianceReduction;
	}
	@Override
	public void readFields(DataInput arg0) throws IOException {	
		nodeId = arg0.readInt();
		featId = arg0.readInt();
		splitId = arg0.readUTF();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(nodeId);
		arg0.writeInt(featId);
		arg0.writeUTF(splitId);
	}

	@Override
	public int compareTo(NodeFeatSplitKey arg0) {
	 	if (this.featId == arg0.featId) {
	 		if (this.nodeId == arg0.nodeId) {
	 			return this.splitId.compareTo(arg0.splitId);
	 		} else { return this.nodeId - arg0.nodeId; }
	 	} else {
	 		return this.featId - arg0.featId;
	 	}
	}

	public int getFeatId() {
		return featId;
	}
	public int getNodeId() {
		return nodeId;
	}
	public String getSplitId() {
		return splitId;
	}
	@Override
	public String toString() {
		String s = "node:" + this.nodeId + ":feat:" + this.featId + ":split:" + this.splitId;
		return s;
	}
	
}
