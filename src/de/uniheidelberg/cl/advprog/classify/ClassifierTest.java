package de.uniheidelberg.cl.advprog.classify;

import java.io.IOException;
import java.util.List;

import de.uniheidelberg.cl.advprog.planet.io.InstanceReader;
import de.uniheidelberg.cl.advprog.planet.io.Serializer;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;

/**
 * Tests a trained classifier.
 * 
 * @author boegel
 *
 */
public class ClassifierTest {

	/**
	 * Path to the serialized {@link DecisionTree} model.
	 */
	private static final String MODEL_PATH="tree_model_local.ser";
	
	/**
	 * Decision tree model used for predictions.
	 */
	private DecisionTree tree;
	
	/**
	 * Loads the serialized classifier.
	 * @throws IOException If the classifier file could not be read.
	 * @throws ClassNotFoundException If the {@link DecisionTree} could not be de-serialized.
	 */
	public ClassifierTest() throws IOException, ClassNotFoundException {
		tree = Serializer.readModelFromFile(MODEL_PATH);
	}
	
	/**
	 * Classifies an instance (double[]) and computes the performance.
	 * The last entry of the feature vector is expected to be the class label.
	 * 
	 * @param inst Instance to be classified
	 * @return 1.0 if the label is classified correctly, 0.0 otherwise.
	 */
	public double classifyInstance(Double[] inst) {
		double label = inst[inst.length-1];
		double prediction = Math.round(tree.classify(inst));
		System.out.printf("prediction: %s -> label %s\n", prediction, label);
		if (prediction == label)
			return 1.0;
		else
			return 0.0;
	}
	
	/**
	 * Classifies all instances and returns accuracy.
	 * 
	 * @param instances The instances to classify (List of double[], where the last item 
	 * in the vector is the class label for evaluation).
	 * 
	 * @return Accuracy for the specified instances;
	 */
	public double classifyAllInstances(List<Double[]> instances) {
		double scores = 0.0;
		for (Double[] inst : instances) {
			scores += this.classifyInstance(inst);
		}
		return scores/instances.size();
	}
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// read and de-serialized the trained model 
		InstanceReader reader = new InstanceReader();
		List<Double[]> instances = reader.readInstances();
		ClassifierTest test = new ClassifierTest();
		
		// classify all instances
		double acc = test.classifyAllInstances(instances);
		System.out.println("Accuracy: " + acc);
		
	}
	
	
}
