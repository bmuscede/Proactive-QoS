import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

import cern.colt.Arrays;

import edu.uci.ics.jung.graph.Graph;


public class Monitor implements Runnable {
	private Thread thread;
	
	//Variables for the monitor.
	private Node node;
	private int[] benchmarks;
	public volatile static NetworkController controller;
	private volatile boolean isRunning = true;
	   
	public Monitor(Node dslNode, int[] benchmarks){
		node = dslNode;
		this.benchmarks = benchmarks;
	}
	
	public void run() {
		//Executes when the program runs.
		while(isRunning){
			//Performs monitor and then detection mode.
			boolean success = monitorMode();
			sleepThread();
			
			if (!success){
				controller.graphWindow.setNodeIcon(node, 2);
				detectionMode(node);
			} else {
				controller.graphWindow.setNodeIcon(node, 1);
			}
		}
	}
   public void kill() {
       isRunning = false;
   }
   
   public void start() {
		//Checks if the thread is already running.
		if (thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	private boolean monitorMode(){
		//Notifies GUI that we are in monitor mode.
		NetworkWindow.informationWindow.changeNodeMode(node, 0);
		
		//We request data from the controller.
		int[] currentQoS = null;
				
		try {
			synchronized(controller) {
				currentQoS = controller.requestData(node);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return true;
		}			
		
		//We check for the metrics.
		return checkMetrics(currentQoS);
	}
	
	private boolean checkMetrics(int[] currentQoS) {
		boolean goodQoS = true;
		boolean improper[] = new boolean[4];
		
		//Loops through all the QoS values to check.
		for (int i = 0; i < currentQoS.length - 1; i++){
			if (i < 3){
				if (currentQoS[i] >= benchmarks[i]){
					goodQoS = false;
					improper[i] = true;
				}
			} else {
				if ((currentQoS[i] < benchmarks[i] &&
				    currentQoS[i + 1] <= benchmarks[i + 1]) ||
				    currentQoS[i + 1] < benchmarks[i + 1]){
					goodQoS = false;
					
					if (i == 4) improper[i - 1] = true;
					else improper[i] = true;
				}
			}
		}
		
		//Passes the metrics to the GUI window.
		NetworkWindow.informationWindow.passMonitorMetrics(node, currentQoS, improper);
		return goodQoS;
	}

	private void detectionMode(Node userWithProblem){
		//Notifies GUI that we are in detection mode.
		NetworkWindow.informationWindow.changeNodeMode(node, 1);
		
		//First, we get the graph for the entire topology.
		Graph<Node, Link> graph = controller.requestErrorGraph();
		Node destination = controller.requestDestinationNode();
		
		//Next, we generate the path from source to destination.
		List<Node> path = dikjstraAlgorithm(graph, node, destination);
		
		//Once we do this, we run loop through the path.
		Node problemNode = null;
		Link problemLink = null;
		for (int i = 0; i < path.size(); i++){
			//Gets the current node/link being examined.
			Node currentNode = path.get(i);
			//Link currentLink = graph.findEdge(path.get(i - 1), path.get(i));
			
			//Determines whether we have an error.
			boolean isError = determineNode(currentNode);
			
			//Notifies users which node we're currently examining.
			NetworkWindow.informationWindow.setExaminingNode(node, currentNode);
			
			//Determines if we have an error.
			NetworkWindow.informationWindow.setErrorStatus(node, isError);
			if (isError){
				controller.graphWindow.setNodeIcon(currentNode, 2);
				problemNode = currentNode;
				break;
			} else {
				controller.graphWindow.setNodeIcon(currentNode, 1);
			}
			
			sleepThread();
		}
		
		//Notifies the controller of its answer.
		boolean correct = controller.notifyDetected(problemNode, problemLink);
		if (!correct){
			//Will be handled if incorrect.
		}
		resetColours(graph);
	}

	private boolean determineNode(Node currentNode) {
		//Placeholder for later.
		if (controller.errorNode == currentNode){
			return true;
		}
		
		return false;
	}

	private void resetColours(Graph<Node, Link> graph) {
		Iterator<Node> vertices = graph.getVertices().iterator();
		while (vertices.hasNext()){
			Node current = vertices.next();
			
			//Check for DSL END or VOIP End
			if (current.getType().getNumVal() != Node.NodeType.DSL_END.getNumVal() &&
					current.getType().getNumVal() != Node.NodeType.VOIP_END.getNumVal()){
				controller.graphWindow.setNodeIcon(current, 0);
			}
		}
	}

	private List<Node> dikjstraAlgorithm(Graph<Node, Link> graph, Node source, Node destination) {
		//First, we fill in the nodes for the graph.
		graph = getShortestPaths(graph, source);
		
		//Creates a node path object.
		List<Node> path = new ArrayList<Node>();
		
		//Iterates through the graph.
        for (Node currentNode = destination; currentNode != null;){
        	path.add(currentNode);
        	currentNode = currentNode.previous;
        }

        //Reverse the path.
        Collections.reverse(path);
        return path;
	}
	
	private Graph<Node, Link> getShortestPaths(Graph<Node, Link> graph, Node source){
		//We set the smallest difference and add it to the priority queue.
		source.minDistance = 0;
		PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
      	nodeQueue.add(source);
      	
      	//Now, we loop until we have no more nodes to give.
      	while (nodeQueue.isEmpty() == false){
      		//Get the smallest node.
      		Node small = nodeQueue.poll();
      		
      		//Populates the smallest values for each.
      		Collection<Node> adj = graph.getNeighbors(small);
      		for(Iterator<Node> it = adj.iterator(); it.hasNext();){
      			Node current = it.next();
      			Link link = graph.findEdge(small, current);
      			
      			double dist = small.minDistance + (link.getBandwidthValue() * link.getBandwidthType().getBitNum());
      			
      			//Re-adds the node if smaller.
      			if (dist < current.minDistance){
      				nodeQueue.remove(current);
      				current.previous = small;
      				current.minDistance = dist;
      				nodeQueue.add(current);
      			}
      		}
      	}
      	
      	return graph;
	}
	
	private void sleepThread(){
		do {
			try {
				//Sleeps the thread.
				Thread.sleep(NetworkController.getSleepTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (NetworkController.pauseBoolean); //Keeps looping if paused.
	}
}
