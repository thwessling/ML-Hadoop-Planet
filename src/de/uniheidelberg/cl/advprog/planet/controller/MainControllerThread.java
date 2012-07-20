package de.uniheidelberg.cl.advprog.planet.controller;


import java.util.ArrayList;
import java.util.List;


import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.structures.TreeModel;
import de.uniheidelberg.cl.advprog.planet.tree.Node;

public class MainControllerThread extends Thread {

	TreeModel model;
	/**
	 * Nodes for which D* is too large to fit in memory.
	 */
	List<Node> mrq;
	/**
	 * Nodes for which D* fits in memory.
	 */
	List<Node> inMemQ;
	
	public MainControllerThread() {
		this.mrq  = new ArrayList<Node>();
		this.inMemQ = new ArrayList<Node>();
	}
	
	public void startJob() throws Exception {
		ExpandNodesController.main(new String[]{"test", "test"});
	}
	
	@Override
	public void run() {
		
		super.run();
	}
	
	public static void main(String[] args) throws Exception {
		MainControllerThread thread = new MainControllerThread();
		thread.startJob();
	}
}
