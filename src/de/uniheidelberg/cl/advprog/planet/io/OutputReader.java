package de.uniheidelberg.cl.advprog.planet.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.uniheidelberg.cl.advprog.planet.structures.BestModel;

public class OutputReader {
	
	public Map<Integer, BestModel> readBestModels() throws IOException {
		Map<Integer, BestModel> node2Model = new HashMap<Integer, BestModel>();
		
		BufferedReader br = new BufferedReader(new FileReader("test-out/bestModel-r-00000"));
		while (br.ready()) {
			String line = br.readLine();
			int nodeId = Integer.parseInt(line.split("\t")[0].split("\\:")[1].split(",")[0]);
			double bestSplit = Double.parseDouble(line.split("\t")[0].split("\\:")[3]);
			BestModel m = new BestModel(nodeId, bestSplit);
			node2Model.put(nodeId, m);
		}
		
		br = new BufferedReader(new FileReader("test-out/branchCounts-r-00000"));
		while (br.ready()) {
			String line = br.readLine();
			int nodeId = Integer.parseInt(line.split("\\:")[1]);
			double count = Double.parseDouble(line.split("\\:")[3]);
			if (line.split("\\:")[2].equals("left")) {
				node2Model.get(nodeId).setLeftBranchInstances((long) count);
			}
			double bestSplit = Double.parseDouble(line.split("\t")[0].split("\\:")[3]);
		}
		return node2Model;
	}
	
}
