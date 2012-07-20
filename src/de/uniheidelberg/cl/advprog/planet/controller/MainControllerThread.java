package de.uniheidelberg.cl.advprog.planet.controller;


import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;

import de.uniheidelberg.cl.advprog.planet.expandNodes.ExpandNodesController;
import de.uniheidelberg.cl.advprog.planet.structures.TreeModel;

public class MainControllerThread extends Thread {

	TreeModel model;
	List<String> mrq;
	List<String> inMemQ;
	
	public MainControllerThread() {
		this.mrq  = new ArrayList<String>();
		this.inMemQ = new ArrayList<String>();
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
