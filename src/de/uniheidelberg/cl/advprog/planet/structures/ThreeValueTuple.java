package de.uniheidelberg.cl.advprog.planet.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class ThreeValueTuple implements WritableComparable<ThreeValueTuple>{

	private double leftBranchSum;
	private double squareSum;
	private double instanceNum;
	
	public ThreeValueTuple() {}
	
	public ThreeValueTuple(double leftBranchSum, double squareSum, double marginal) {
		this.leftBranchSum = leftBranchSum;
		this.squareSum = squareSum;
		this.instanceNum = marginal;
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
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeDouble(leftBranchSum);
		arg0.writeDouble(squareSum);
		arg0.writeDouble(instanceNum);
	}
	
	public void add(ThreeValueTuple tuple) {
		this.leftBranchSum += tuple.leftBranchSum;
		this.squareSum += tuple.squareSum;
		this.instanceNum += tuple.instanceNum;
	}
	
	public ThreeValueTuple minus(ThreeValueTuple t) {
		ThreeValueTuple result = new ThreeValueTuple(this.leftBranchSum, this.squareSum, this.instanceNum);
		result.leftBranchSum -= t.leftBranchSum;
		result.squareSum -= t.squareSum;
		result.instanceNum -= t.instanceNum;
		return result;
	}
	

	@Override
	public int compareTo(ThreeValueTuple o) {
		if (this.leftBranchSum == o.leftBranchSum) {
			if (this.squareSum == o.squareSum) {
				return (int) Math.round( (this.instanceNum - o.instanceNum));
			} else { return (int) Math.round(this.squareSum - o.squareSum); }
			
		} else {
			return (int) Math.round(this.leftBranchSum - o.leftBranchSum);
		}
	}

	@Override
	public String toString() {
		String s = "Tuple: " + this.leftBranchSum + ", " + this.squareSum + ", " + this.instanceNum;
		return s;
	}
	
}
