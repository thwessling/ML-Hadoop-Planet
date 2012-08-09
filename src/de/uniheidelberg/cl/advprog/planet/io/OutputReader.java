package de.uniheidelberg.cl.advprog.planet.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.uniheidelberg.cl.advprog.planet.structures.BestModel;
import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.OrderedSplit;
import de.uniheidelberg.cl.advprog.planet.tree.Split;
import de.uniheidelberg.cl.advprog.planet.tree.UnorderedSplit;

public class OutputReader {
	
	
	
	public Map<Integer, BestModel> readBestModels(String outputPath, DecisionTree model) throws IOException {
		Map<Integer, BestModel> node2Model = new HashMap<Integer, BestModel>();
		if (!new File(outputPath, "bestModel-r-00000").exists())
			return null;
		BufferedReader br = new BufferedReader(new FileReader(new File(outputPath, "bestModel-r-00000")));
		while (br.ready()) {
			String line = br.readLine();
			int nodeId = Integer.parseInt(line.split("\t")[0].split("\\:")[1]);
			// if no instance arrives at this node: it should be a leaf node
			/* bestSplit is a string indicating the best split values for a node. 
			 * This can be a single double value (for ordered splits) or a list of value (for
			 * unordered splits), separated by ; 												 */
			String bestSplit = line.split("\t")[0].split("\\:")[5];
			double leftBranchSum = Double.parseDouble(line.split("\t")[1].split("\\:")[1].split(",")[0]);
			double rightBranchSum = Double.parseDouble(line.split("\t")[1].split("\\:")[1].split(",")[1]);
			BranchingNode n = model.getNodeById(nodeId);
			BestModel m = new BestModel(nodeId);
			Split split;
			if (n.getAtt().isOrdered()) {
				split = new OrderedSplit(n.getAtt().getIndex());
				double bestSplitValue = Double.parseDouble(bestSplit);
				((OrderedSplit) split).setOrderedSplit(bestSplitValue);
			} else {
				split = new UnorderedSplit(n.getAtt().getIndex());
				// if this is an un-ordered attribute
				// determine split points for left branch
				for (String splitValue: bestSplit.split(";")) {
					((UnorderedSplit) split).addLeftBranchItem(Double.parseDouble(splitValue));
				}
			}
			split.setLeftBranchY(leftBranchSum);
			split.setRightBranchY(rightBranchSum);
			m.setBestSplit(split);
			node2Model.put(nodeId, m);
		}
		
		br = new BufferedReader(new FileReader(new File(outputPath, "branchCounts-r-00000")));
		while (br.ready()) {
			String line = br.readLine();
			int nodeId = Integer.parseInt(line.split("\\:")[1]);
			double count = Double.parseDouble(line.split("\\:")[3]);
			if (line.split("\\:")[2].equals("left")) {
				node2Model.get(nodeId).setLeftBranchInstances((long) count);
				node2Model.get(nodeId).getBestSplit().setLeftBranchInstances((int) count);
			} else {
				node2Model.get(nodeId).setRightBranchInstances((long) count);
				node2Model.get(nodeId).getBestSplit().setRightBranchInstances((int) count);
			}
		}

        for (int nodeId : node2Model.keySet()) {
            BestModel m = node2Model.get(nodeId);
            System.out.println("<model> for node: " + nodeId + "; best split: " + m.getBestSplit() + " left branch inst: " + m.getLeftBranchInstances());
        }


		return node2Model;
	}
	
}
