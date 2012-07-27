package de.uniheidelberg.cl.advprog.planet.expandNodes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import de.uniheidelberg.cl.advprog.planet.tree.*;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class NodeFeatSplitKey implements WritableComparable<NodeFeatSplitKey> {

	private int nodeId;
	private int featId; 
	private String splitId;
	
	public NodeFeatSplitKey() {}
	
	public NodeFeatSplitKey(int nodeId, int featId, String splitId) {
		this.nodeId = nodeId;
		this.featId = featId; 
		this.splitId = splitId;
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
		if (this.nodeId == arg0.nodeId) {
		 	if (this.featId == arg0.featId) {
		 		return this.splitId.compareTo(arg0.splitId);
		 	} else {
		 		return this.featId - arg0.featId;
		 	}
		} else {
			return this.nodeId - arg0.nodeId;
		}
	}

	public int getFeatId() {
		return featId;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	@Override
	public String toString() {
		String s = "Key: " + this.nodeId + ", " + this.featId + ", " + this.splitId;
		return s;
	}
	
}
