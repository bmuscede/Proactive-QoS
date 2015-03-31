import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Vector;

import cern.colt.Arrays;

import edu.uci.ics.jung.graph.Graph;


public class Monitor implements Runnable {
	private Thread thread;
	
	//Variables for the monitor.
	private Node node;
	private int[] benchmarks;
	public volatile static NetworkController controller;
	
	public Monitor(Node dslNode, int[] benchmarks){
		node = dslNode;
		this.benchmarks = benchmarks;
	}
	
	public void run() {
		//Executes when the program runs.
		while(true){
			//Performs monitor and then detection mode.
			System.out.println(node.getType().getName() + " is entering monitor mode.");
			boolean success = monitorMode();
			
			if (!success){
				System.out.println(node.getType().getName() + " is entering detection mode.");
				detectionMode();
			} else {
				System.out.println(node.getType().getName() + " found no QoS Metric errors.");
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				
				//Aborts the thread.
				System.out.println("\nAborting " + node.getType().getName() + ".");
				break;
			}
		}
	}

	public void start() {
		//Checks if the thread is already running.
		if (thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	private boolean monitorMode(){
		//We request data from the controller.
		int[] currentQoS = null;
				
		try {
			synchronized(controller) {
				currentQoS = controller.requestData(node.getType());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return true;
		}			
		
		//We need to check whether the metrics meet standards.
		System.out.println(node.getType().getName() + " Benchmarks:\n" +
						   "Packet Loss: " + currentQoS[0] + "%\n" +
						   "Jitter: " + currentQoS[1] + "ms\n" +
						   "Latency: " + currentQoS[2] + "ms\n" +
						   "Throughput: " + currentQoS[3] + Link.BAND_TYPE.valueOf(currentQoS[4]) + "\n");
		return checkMetrics(currentQoS);
	}
	
	private boolean checkMetrics(int[] currentQoS) {
		//Loops through all the QoS values to check.
		for (int i = 0; i < currentQoS.length - 1; i++){
			if (i < 3){
				if (currentQoS[i] >= benchmarks[i]){
					return false;
				}
			} else {
				if ((currentQoS[i] < benchmarks[i] &&
				    currentQoS[i + 1] <= benchmarks[i + 1]) ||
				    currentQoS[i + 1] < benchmarks[i + 1]){
					return false;
				}
			}
		}
		
		return true;
	}

	private void detectionMode(){
		//First, we get the graph for the entire topology.
		Graph<Node, Link> graph = controller.requestErrorGraph();
		Node destination = controller.requestDestinationNode();
		
		//Next, we generate the path from source to destination.
		//List<Node> path = dikjstraAlgorithm(graph, node, destination);
		
		//Once we do this, we run loop through the path.
		Node problemNode = null;
		Link problemLink = null;
		/*for (int i = 1; i < path.size(); i++){
			//Gets the current node/link being examined.
			Node currentNode = path.get(i);
			Link currentLink = graph.findEdge(path.get(i - 1), path.get(i));
			
			//Checks the benchmarks at that node.
			
		}*/
		
		//Notifies the controller of its answer.
		boolean correct = controller.notifyDetected(problemNode, problemLink);
		if (!correct){
			//Will be handled if incorrect.
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			
			//Aborts the thread.
			System.out.println("\nAborting " + node.getType().getName() + ".");
		}
		
		System.out.println(node.getType().getName() + " found the error.");
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
}
