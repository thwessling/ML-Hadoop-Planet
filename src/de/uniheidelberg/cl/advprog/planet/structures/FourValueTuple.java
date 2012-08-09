package de.uniheidelberg.cl.advprog.planet.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class FourValueTuple implements WritableComparable<FourValueTuple>{

	private double featureVal;
	private double leftBranchSum;
	private double squareSum;
	private double instanceNum;
	
	public FourValueTuple() {}
	
	public FourValueTuple(double leftBranchSum, double squareSum, double instanceNum) {
		this.leftBranchSum = leftBranchSum;
		this.squareSum = squareSum;
		this.instanceNum = instanceNum;
		this.featureVal = -1;
	}
	
	public void setFeatureVal(double featureVal) {
		this.featureVal = featureVal;
	}
	public double getFeatureVal() {
		return featureVal;
	}
	
	public double getLeftBranchSum() {
		return leftBranchSum;
	}
	
	public double getInstanceNum() {
		return instanceNum;
	}
	
	public double getSquareSum() {
		return squareSum;
	}
	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.leftBranchSum = arg0.readDouble();
		this.squareSum = arg0.readDouble();
		this.instanceNum = arg0.readDouble();
		this.featureVal = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeDouble(leftBranchSum);
		arg0.writeDouble(squareSum);
		arg0.writeDouble(instanceNum);
		arg0.writeDouble(featureVal);
	}
	
	public void add(FourValueTuple tuple) {
		this.leftBranchSum += tuple.leftBranchSum;
		this.squareSum += tuple.squareSum;
		this.instanceNum += tuple.instanceNum;
		this.featureVal = tuple.featureVal;
	}
	
	public FourValueTuple minus(FourValueTuple t) {
		FourValueTuple result = new FourValueTuple(this.leftBranchSum, this.squareSum, this.instanceNum);
		result.leftBranchSum -= t.leftBranchSum;
		result.squareSum -= t.squareSum;
		result.instanceNum -= t.instanceNum;
		return result;
	}
	

	@Override
	public int compareTo(FourValueTuple o) {
		if (this.leftBranchSum == o.leftBranchSum) {
			if (this.squareSum == o.squareSum) {
				if (this.instanceNum == o.instanceNum) {
					return (int) Math.round(this.featureVal - o.featureVal);
				} else { return (int) Math.round(this.instanceNum - o.instanceNum); }
			} else { return (int) Math.round(this.squareSum - o.squareSum); }
			
		} else {
			return (int) Math.round(this.leftBranchSum - o.leftBranchSum);
		}
	}

	@Override
	public String toString() {
		String s = "Tuple: " + this.leftBranchSum + ", " + this.squareSum + ", " + this.instanceNum + " feature value " + this.featureVal;
		return s;
	}
	
}
