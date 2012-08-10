package de.uniheidelberg.cl.advprog.planet.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import de.uniheidelberg.cl.advprog.planet.structures.BestModel;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.OrderedSplit;
import de.uniheidelberg.cl.advprog.planet.tree.Split;
import de.uniheidelberg.cl.advprog.planet.tree.UnorderedSplit;

/**
 * {@link OutputReader} processes the output of Hadoop Reducers and 
 * converts it into {@link BestModel} objects for each node.
 * 
 * @author boegel
 *
 */
public class OutputReader {
	
	/**
	 * Filter for "bestModel" files.
	 * @author boegel
	 *
	 */
	private class ModelFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith("bestModel");
		}
		
	}
	
	/**
	 * Filter for "branchCounts" files.
	 * @author boegel
	 *
	 */
	private class BranchCountFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith("branchCounts");
		}
		
	}
	/**
	 * In addition to the best model, branch counts (num of instances) 
	 * is written to a separate file and parsed in this method.
	 * 
	 * @param outputPath Path where branch counts should are written to by the reducer.
	 * @throws IOException If the file with the model counts cannot be read.
	 */
	private void readModelCounts(String outputPath, Map<Integer, BestModel> node2Model) throws IOException {
		for (String fn : new File(outputPath).list(new BranchCountFilter())) {
			BufferedReader br = new BufferedReader(new FileReader(new File(outputPath, fn)));
			while (br.ready()) {
				String line = br.readLine();
				// parse node id and count
				int nodeId = Integer.parseInt(line.split("\\:")[1]);
				double count = Double.parseDouble(line.split("\\:")[3]);
				// decide whether we process left or right branch instances
				if (line.split("\\:")[2].equals("left")) {
					node2Model.get(nodeId).setLeftBranchInstances((long) count);
					node2Model.get(nodeId).getBestSplit().setLeftBranchInstances((int) count);
				} else {
					node2Model.get(nodeId).setRightBranchInstances((long) count);
					node2Model.get(nodeId).getBestSplit().setRightBranchInstances((int) count);
				}
			}
		}
	}
	/**
	 * Output best models for debugging purposes.
	 * @param node2Model Mapping from node id to best Model.
	 */
	@SuppressWarnings("deprecation")
	private void debugOutput(Map<Integer, BestModel> node2Model) {
	      for (int nodeId : node2Model.keySet()) {
	            BestModel m = node2Model.get(nodeId);
	            String logString = String.format("<model> for node '%s': best split: %s. left/right: %s/%s ", 
	            		nodeId, m.getBestSplit(), m.getLeftBranchInstances(), m.getRightBranchInstances());
	            Logger.getLogger(this.getClass().toString()).log(Priority.INFO, logString);
	        }
	}
	
	
	/**
	 * Reads the output of reducers and returns a mapping from node id to {@link BestModel} objects 
	 * for the node.
	 * 
	 * @param outputPath Path where reducers write their output to.
	 * @param model The decision tree for retrieving nodes.
	 * @return A mapping from node id to the best model for a node. 
	 * @throws IOException If output files cannot be read.
	 */
	public Map<Integer, BestModel> readBestModels(String outputPath, DecisionTree model) throws IOException {
		Map<Integer, BestModel> node2Model = new HashMap<Integer, BestModel>();
		// iterate over the output of all reducers
		for (String fn : new File(outputPath).list(new ModelFilter())) {
			BufferedReader br = new BufferedReader(new FileReader(new File(outputPath, fn)));
			while (br.ready()) {
				String line = br.readLine();
				int nodeId = Integer.parseInt(line.split("\t")[0].split("\\:")[1]);
				/* 
				 * bestSplit is a string indicating the best split values for a node. 
				 * This can be a single double value (for ordered splits) or a list of value (for
				 * unordered splits), separated by ;
				 *  			
				 */
				String bestSplit = line.split("\t")[0].split("\\:")[5];
				double leftBranchSum = Double.parseDouble(line.split("\t")[1].split("\\:")[1].split(",")[0]);
				double rightBranchSum = Double.parseDouble(line.split("\t")[1].split("\\:")[1].split(",")[1]);
				BranchingNode n = model.getNodeById(nodeId);
				BestModel m = new BestModel(nodeId);
				Split split;
				if (n.getAtt().isOrdered()) {
					/* 
					 * Ordered attribute: parse the threshold. 
					 */
					split = new OrderedSplit(n.getAtt().getIndex());
					double bestSplitValue = Double.parseDouble(bestSplit);
					((OrderedSplit) split).setOrderedSplit(bestSplitValue);
				} else {
					/*
					 * Unordered attributes: parse left branch feature values.
					 */
					split = new UnorderedSplit(n.getAtt().getIndex());
					for (String splitValue: bestSplit.split(";")) {
						((UnorderedSplit) split).addLeftBranchItem(Double.parseDouble(splitValue));
					}
				}
				split.setLeftBranchY(leftBranchSum);
				split.setRightBranchY(rightBranchSum);
				m.setBestSplit(split);
				node2Model.put(nodeId, m);
			}
			
		}
		if (node2Model.size() == 0)
			return null;
		this.readModelCounts(outputPath, node2Model);
		this.debugOutput(node2Model);
		return node2Model;
	}
	
}
