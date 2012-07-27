package de.uniheidelberg.cl.advprog.planet.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class ThreeValueTuple implements WritableComparable<ThreeValueTuple>{

	private double leftBranchSum;
	private double squareSum;
	private double marginals;
	
	public ThreeValueTuple() {}
	
	public ThreeValueTuple(double leftBranchSum, double squareSum, double marginal) {
		this.leftBranchSum = leftBranchSum;
		this.squareSum = squareSum;
		this.marginals = marginal;
	}
	
	public double getLeftBranchSum() {
		return leftBranchSum;
	}
	
	public double getMarginals() {
		return marginals;
	}
	
	public double getSquareSum() {
		return squareSum;
	}
	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.leftBranchSum = arg0.readDouble();
		this.squareSum = arg0.readDouble();
		this.marginals = arg0.readDouble();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeDouble(leftBranchSum);
		arg0.writeDouble(squareSum);
		arg0.writeDouble(marginals);
	}
	
	public void add(ThreeValueTuple tuple) {
		this.leftBranchSum += tuple.leftBranchSum;
		this.squareSum += tuple.squareSum;
		this.marginals += tuple.marginals;
	}

	@Override
	public int compareTo(ThreeValueTuple o) {
		if (this.leftBranchSum == o.leftBranchSum) {
			if (this.squareSum == o.squareSum) {
				return (int) Math.round( (this.marginals - o.marginals));
			} else { return (int) Math.round(this.squareSum - o.squareSum); }
			
		} else {
			return (int) Math.round(this.leftBranchSum - o.leftBranchSum);
		}
	}

	@Override
	public String toString() {
		String s = "Tuple: " + this.leftBranchSum + ", " + this.squareSum + ", " + this.marginals;
		return s;
	}
	
}
