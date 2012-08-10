package de.uniheidelberg.cl.advprog.planet.expandNodes;

import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

import de.uniheidelberg.cl.advprog.planet.structures.FourValueTuple;

public class NodeFeatSplitPartitioner implements Partitioner<NodeFeatSplitKey, FourValueTuple> {
	@Override
	public int getPartition(NodeFeatSplitKey arg0, FourValueTuple arg1, int numReducers) {
		return arg0.getNodeId() % numReducers;
	}

	@Override
	public void configure(JobConf arg0) {}

	

}
