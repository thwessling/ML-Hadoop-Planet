package de.uniheidelberg.cl.advprog.planet.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uniheidelberg.cl.advprog.planet.tree.Attribute;

public class InstanceReader {

	private static final String INSTANCE_SPEC = "data/features.txt";
	private static final String INSTANCES = "data/test.txt";
	
	
	/**
	 * Reads feature specifications from a text file and converts them into {@link Attribute} 
	 * objects.
	 * See the readme file for a specification of feature definitions.
	 * 
	 * @return A list of Attributes in the specification file.
	 * @throws IOException If the specification file could not be read.
	 */
	public List<Attribute> readAttributeSpecs() throws NumberFormatException, IOException {
		List<Attribute> atts = new ArrayList<Attribute>();
		BufferedReader br = new BufferedReader(new FileReader(INSTANCE_SPEC));

		// create unique indices for features 
		int featureIndex = 0;
		String line = null;
		// read one feature spec per line
		while ((line = br.readLine()) != null) {
			// extract feature name
			Attribute att = new Attribute(line.split(":")[0],featureIndex);
			// value range for ordered attributes (e.g. 1-4)
			Pattern p = Pattern.compile("([^\\-]+)\\-(.*)");
			Matcher m = p.matcher(line.split(":")[1]);
			if (m.matches()) { 
				/* 
				 * ORDERED attribute
				 * add all possible values
				 */
				double firstVal = Double.valueOf(m.group(1));
				double scndVal =  Double.valueOf(m.group(2));
				att.setOrdered(true);
				for (double val = firstVal; val <= scndVal; val++) {
					att.addValue(val);
				}
			} else {
				/* 
				 * UNORDERED
				 * possible values separated by ;
				 */
				att.setOrdered(false);
				for (String val : line.split(":")[1].split(";")) {
					att.addValue(Double.valueOf(val));
				}
			}
			featureIndex++;
			atts.add(att);
		}
		br.close();
		return atts;
	}
	
	
	
	/**
	 * Reads instances from a text file and returns them.
	 * 
	 * @return A list of instances (represented as double arrays).
	 * @throws IOException If the instance file could not be read.
	 */
	public List<Double[]> readInstances() throws IOException {
		List<Double[]>  instances = new ArrayList<Double[]>();
		BufferedReader br = new BufferedReader(new FileReader(INSTANCES));
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
