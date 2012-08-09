package de.uniheidelberg.cl.advprog.graph;

import java.io.FileWriter;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerEdgeNameProvider;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

import de.uniheidelberg.cl.advprog.planet.tree.BranchingNode;
import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;
import de.uniheidelberg.cl.advprog.planet.tree.Node;

public class GraphWriter {

	
	public static void writeGraph(String outFile, DecisionTree tree) {
		DirectedGraph<Node, SplitEdge> graph = constructGraph(tree);
		IntegerNameProvider<Node> nodeIdProv = new IntegerNameProvider<Node>();
		StringNameProvider<Node> nodeLabelProv = new StringNameProvider<Node>();
		DOTExporter<Node, SplitEdge> export = new DOTExporter<Node, SplitEdge>(nodeIdProv, 
							nodeLabelProv, new SplitEdgeNameProvider());
		try {
			FileWriter fw = new FileWriter(outFile);
			export.export(fw, graph);
			fw.close();
		} catch (Exception e) {
			System.err.println("Couldn't write graph to " + outFile);
			e.printStackTrace();
		}
	}
	
	
	private static DirectedGraph<Node, SplitEdge> constructGraph(DecisionTree tree) {
		DirectedGraph<Node, SplitEdge> graph = new DirectedAcyclicGraph<Node, SplitEdge>(SplitEdge.class);
		graph.addVertex(tree.getRoot());
		traverse(graph, tree.getRoot());
		return graph;
	}
	
	private static void traverse (DirectedGraph<Node, SplitEdge> graph, Node n) {
		if (n.getDaughters().size() > 0) {
            for (int i = 0; i < n.getDaughters().size() ; i++) {
            	graph.addVertex(n.getDaughters().get(i));
            	SplitEdge edge = new SplitEdge();
            	if (i== 0)
            		edge.setInstanceNum(((BranchingNode) n).getAtt().getSplit().getLeftBranchInstances());
            	else 
            		edge.setInstanceNum(((BranchingNode) n).getAtt().getSplit().getRightBranchInstances());
            	graph.addEdge(n, n.getDaughters().get(i),edge);
            	traverse(graph, n.getDaughters().get(i));
                //System.out.println(n.toString() + " --> " + n.getDaughters().get(i));
                
            }
		} else {
            return;
        }
	}
	
	
	
	
	
	
}
