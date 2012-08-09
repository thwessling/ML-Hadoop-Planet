package de.uniheidelberg.cl.advprog.planet.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstanceReader {

	
	
	public List<Double[]> readInstances(String fn) throws IOException {
		List<Double[]>  instances = new ArrayList<Double[]>();
		BufferedReader br = new BufferedReader(new FileReader(fn));
		String line = br.readLine();
		
		while (br.ready()) {
			Double[] instanceLine = new Double[line.split(",").length];
			int i = 0;
			for (String item : line.split(",")) {
				instanceLine[i] = Double.valueOf(item);
				i++;
			}
			instances.add(instanceLine);
			line = br.readLine();
		}
		br.close();
		return instances;
	}
	
	
	
}
