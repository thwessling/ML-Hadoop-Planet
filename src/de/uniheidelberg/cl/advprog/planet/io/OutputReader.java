package de.uniheidelberg.cl.advprog.planet.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class OutputReader {

	
	
	public void readBestModels() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("test-out/bestModel-r-00000"));
		while (br.ready()) {
			String line = br.readLine();
			int nodeId = Integer.parseInt(line.split("\t")[0].split("\\:")[1].split(",")[0]);
			double bestSplit = Double.parseDouble(line.split("\t")[0].split("\\:")[3]);
		}
		
		br = new BufferedReader(new FileReader("test-out/branchCounts-r-00000"));
		while (br.ready()) {
			String line = br.readLine();
			int nodeId = Integer.parseInt(line.split("\\:")[1]);
			double bestSplit = Double.parseDouble(line.split("\t")[0].split("\\:")[3]);
		}
		
	}
	
}
