package de.uniheidelberg.cl.advprog.planet.expandNodes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class FeatSplitKey implements WritableComparable<FeatSplitKey> {

	private int featId; 
	private String splitId;
	
	public FeatSplitKey() {}
	
	public FeatSplitKey(int featId, String splitId) {
		this.featId = featId; 
		this.splitId = splitId;
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {	
		featId = arg0.readInt();
		splitId = arg0.readUTF();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(featId);
		arg0.writeUTF(splitId);
	}

	@Override
	public int compareTo(FeatSplitKey arg0) {
	 	if (this.featId == arg0.featId) {
	 		return this.splitId.compareTo(arg0.splitId);
	 	} else {
	 		return this.featId - arg0.featId;
	 	}
	}

	public int getFeatId() {
		return featId;
	}
	public String getSplitId() {
		return splitId;
	}
	@Override
	public String toString() {
		String s = "node:" + this.featId + ":split:" + this.splitId;
		return s;
	}
	
}
