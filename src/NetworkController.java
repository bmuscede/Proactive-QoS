import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;

public class NetworkController {
	//Error Boolean
	private boolean error;
	
	//Minimum Benchmarks
	private int[] dslBenchmarks;
	private int[] voipBenchmarks;
	
	//Failure Rate
	private int failureRate;
	public Node errorNode;
	private Node destNode;
	
	//Graph
	private Graph<Node, Link> graph;
	private Vector<Monitor> dslNodes;
	private Vector<Monitor> voipNodes;
	
	//Network Window
	public NetworkWindow graphWindow;
	
	//Thread sleep times.
	private static int sleepTime = 5000;
	
	//Pause boolean.
	public static boolean pauseBoolean = false;
	
	/**********************************
	 * 
	 * NEWTORKCONTROLLER constructor
	 *
	 **********************************/
	public NetworkController(NetworkWindow window, Graph<Node, Link> graph, int[] dslBenchmarks,
			int[] voipBenchmarks, int failureRate) {
		//Gets the graph window.
		graphWindow = window;
		
		//Sets up error boolean.
		error = false;
		
		//Sets up the benchmarks.
		this.dslBenchmarks = dslBenchmarks;
		this.voipBenchmarks = voipBenchmarks;
		
		//Now saves the graph and failure rate.
		this.graph = graph;
		this.failureRate = failureRate;
		
		//Next, we need to get all the DSL nodes and VOIP nodes.
		setupNodes();
	}
	
	
	/**********************************
	 * 
	 * setupNodes
	 *
	 **********************************/
	private void setupNodes(){
		Monitor.controller = this;
		
		//Setup the node objects.
		dslNodes = new Vector<Monitor>();
		voipNodes = new Vector<Monitor>();
		
		//We now loop through and add to the monitor lists.
		Iterator<Node> vertices = graph.getVertices().iterator();
		while (vertices.hasNext()){
			//Gets the next node.
			Node current = vertices.next();
			
			//Checks the node type.
			if (current.getType().equals(Node.NodeType.DSL_END)){
				dslNodes.add(new Monitor(current, dslBenchmarks));
			} else if (current.getType().equals(Node.NodeType.VOIP_END)){
				voipNodes.add(new Monitor(current, voipBenchmarks));
			}
		}
	}
	
	
	/**********************************
	 * 
	 * start
	 *
	 **********************************/
	public void start(){
		//Starts each of the monitor threads.
		for (int i = 0; i < dslNodes.size(); i++){
			dslNodes.elementAt(i).start();
		}
		for (int i = 0; i < voipNodes.size(); i++){
			voipNodes.elementAt(i).start();
		}
	}

	
	/**********************************
	 * 
	 * randomizeError
	 *
	 **********************************/
	private boolean randomizeError(){
		//Create new random generator.
		Random randomGenerator = new Random();
		
		//Gets a random integer.
		int randInteger = randomGenerator.nextInt(100);
		
		//Checks to see if the integer falls outside of the failure rate.
		if (randInteger > failureRate) {
			return false;
		}
		
		return true;
	}
	
	
	/**********************************
	 * 
	 * requestData
	 *
	 **********************************/
	public synchronized int[] requestData(Node userNode) throws InterruptedException {
		if (alreadyError()){
			wait();
		}
		
		//Determines whether there is an error.
		error = randomizeError();
		int[] valuesQoS;

		if (error){
			//Generates data with random error.
			valuesQoS = generateRandomError(userNode.getType());
            buildErrorGraph(valuesQoS, userNode);
		} else {
			//Generates normal QoS.
			valuesQoS = generateRandomNormal(userNode.getType());
		}
		
		return valuesQoS;
	}

	
	/**********************************
	 * 
	 * requestErrorGraph
	 *
	 **********************************/
	public Graph<Node, Link> requestErrorGraph(){
		//First, check for error.
		if (error == false) return null;
		
		return graph;
	}
	
	
	/**********************************
	 * 
	 * requestDestinationNode
	 *
	 **********************************/
	public Node requestDestinationNode(){
		//First, check for error.
		if (error == false) return null;
		
		return destNode;
	}
	
	/**********************************
	 * 
	 * notifyDetected
	 *
	 **********************************/
	public synchronized boolean notifyDetected(Node indicatedNode, Link indicatedLink){
		//Check the indicated node with the error.
		boolean properError = false;
		if (indicatedNode == errorNode){
			properError = true;
		} else { 
		}
		error = false;
		
		//Finally, wakes up the other threads.
		notifyAll();
		return properError;
	}
	
	
	/**********************************
	 * 
	 * buildErrorGraph
	 *
	 **********************************/
	private void buildErrorGraph(int[] valuesQoS, Node userNode) {
      //pick destination node 
		Iterator<Node> vertices = graph.getVertices().iterator();
		Node dest = null;
		
		//Get the end node.
		Random generator = new Random();
		int nodeEnd = generator.nextInt(graph.getVertexCount());
		int i = 0;
		while (i <= nodeEnd && vertices.hasNext()){
			//Gets the next node.
			dest = vertices.next();
			
			//Checks the node type.
			if (i == nodeEnd) break;
			i++;
		}
		destNode = dest;
		
		//use djikstra's again
		List<Node> path = dikjstraAlgorithm(graph, userNode, dest);
		
		boolean badMetrics[] = new boolean[4];
		
		int currentBenchmark[] = (userNode.getType().getNumVal() == Node.NodeType.DSL_END.getNumVal())
 				? dslBenchmarks : voipBenchmarks;
 				
		/***-------------Check which metric was a problem ----------------****/
		//if packet loss violated
		badMetrics[0] = (valuesQoS[0] >= currentBenchmark[0]) ? true : false;
		// if jitter violated
		badMetrics[1] = (valuesQoS[1] >= currentBenchmark[1]) ? true : false;
		//if latency violated
		badMetrics[2] = (valuesQoS[2] >= currentBenchmark[2]) ? true : false;
		//if throughput violated
		badMetrics[3] = (((valuesQoS[3] < currentBenchmark[3] &&
			    valuesQoS[4] <= currentBenchmark[4]) ||
			    valuesQoS[4] < currentBenchmark[4])) ? true : false;
		
		
		//pick a number between 0 and size of path for error source
		int findBad = generator.nextInt(path.size());
		//GENERATE MERTICS FOR NODES IN PATH
		//fill in qOs metrics 
		Iterator<Node> findBadNode = path.iterator();
	    Node nextNode = null;
	    int nodeQoS[] = new int[5];
	    int totalLatency = valuesQoS[2];
	    int totalPacketLoss = valuesQoS[0];
	    int pathSize = path.size();
	    int latency, packetLoss;
	    i = 0;
		while (findBadNode.hasNext()){
		  //Gets the next node.
		  nextNode = findBadNode.next();
		 //random number between 10 and total_latency/path_size or just evenly divide latency
		  latency = (totalLatency/pathSize); 
		  packetLoss = totalPacketLoss / pathSize;
		  
		  if(findBad == 0 && i == findBad){
			  errorNode = nextNode;
		  }
		  else if(i == findBad){
			  errorNode = nextNode;
			  nodeQoS[0] = (badMetrics[0] == true)? generator.nextInt(10) + (totalPacketLoss/pathSize) : packetLoss; //packet loss
			  nodeQoS[1] = (badMetrics[1] == true)? generator.nextInt(10) + currentBenchmark[1] : generator.nextInt(currentBenchmark[1]); //jitter
			  nodeQoS[2] = (badMetrics[2] == true)? generator.nextInt(10) + (totalLatency/pathSize) : latency; //latency
			  nodeQoS[3] = (badMetrics[3] == true)? generator.nextInt(currentBenchmark[3]) : generator.nextInt(1000 - currentBenchmark[3]) + (currentBenchmark[3]); //throughput
			  nodeQoS[4] = (badMetrics[3] == true)? generator.nextInt(currentBenchmark[4] + 1) : generator.nextInt(Link.BAND_TYPE.TBPS.getInternal() + 1) + currentBenchmark[4]; //throughput values.
		  }
		  else{
			  nodeQoS[0] = packetLoss; //packet loss
			  nodeQoS[1] = generator.nextInt(currentBenchmark[1]); //jitter
			  //generate random latency value between 10 and total latency divided by path size
			  nodeQoS[2] = latency; 
			  nodeQoS[3] = generator.nextInt(1000 - currentBenchmark[3]) + (currentBenchmark[3]); //throughput
			  nodeQoS[4] = generator.nextInt(Link.BAND_TYPE.TBPS.getInternal() + 1) + currentBenchmark[4]; //throughput values.
		  }
		  
		  totalLatency = totalLatency - latency;
		  totalPacketLoss -= totalPacketLoss - packetLoss;
		  pathSize--;
		  
		  i++;
		  
		  nextNode.setQoSMetrics(nodeQoS);
		 }
	}

	/**********************************
	 * 
	 * generateRandomError
	 *
	 **********************************/
	private int[] generateRandomError(Node.NodeType type) {
		//Random generator.
		Random generator = new Random();
		
		//generate regular qosValues array
		int[] qosValues = generateRandomNormal(type);
		
		/* generate random number 1-10 
		 * 
		 * 1-5: indicates only one metric will violate benchmarks
		 * 6-7: indicates two metrics will violate benchmarks
		 * 8: 	indicates 3 metrics will violate benchmarks
		 * 9: 	indicates 4 metrics will violate benchmarks
		 * 10:	indicates 5 metrics will violate benchmarks
		 * 
		 */
		
		int violations = generator.nextInt(11);
		
		/* CASE 1: Only one metric will violate benchmarks */
		if(violations <= 5){
			//choose randomly one of the five metrics to violate
			int num = generator.nextInt(5);
			//change metric in order to violate benchmark
			qosValues = changeMetric(type, qosValues, num);
		}
		
		/* CASE 2: Two metrics will violate benchmarks */
		else if(violations==6 || violations == 7){
			
			//generate first metric to be violated randomly
			int num1 = generator.nextInt(5);
			int num2 = num1;
			
			//generate second metric to be violated randomly, until it is different from first metric
			while(num1==num2){
				num2 = generator.nextInt(5);
			}
			
			//modify qosMetrics
			qosValues = changeMetric(type, qosValues, num1);
			qosValues = changeMetric(type, qosValues, num2);
			
		}
		/* CASE 3: Three metrics will violate benchmarks */
		else if(violations == 8){
			int num1 = generator.nextInt(5);
			int num2 = num1;
			int num3 = num1;
			
			//loop until all three metrics are randomly selected
			while(num1==num2 || num1==num3 || num2 == num3){
				if(num1==num2){
					num2 = generator.nextInt(5);
				}
				if(num1==num3 ||num2==num3){
					num3 = generator.nextInt(5);
				}
			}
			
			//modify qosMetrics
			qosValues = changeMetric(type, qosValues, num1);
			qosValues = changeMetric(type, qosValues, num2);
			qosValues = changeMetric(type, qosValues, num3);				
		}
		
		/* CASE 4: Four metrics will violate Benchmarks */
		else if(violations == 9){
			int unchanged = generator.nextInt(5);
			
			int[] nums = {0 ,1, 2, 3, 4};
			
			for(int i =0; i< nums.length; i++){
				
				if(i != unchanged){
					qosValues = changeMetric(type, qosValues, i);
				}
				
			}
		}
		
		/* CASE 5: Five Metric will violate Benchmarks */
		else if(violations==10){
			qosValues = changeMetric(type, qosValues, 0);
			qosValues = changeMetric(type, qosValues, 1);
			qosValues = changeMetric(type, qosValues, 2);
			qosValues = changeMetric(type, qosValues, 3);
			qosValues = changeMetric(type, qosValues, 4);
		}
		
		return qosValues;
	}
	
	
	/**********************************
	 * 
	 * changeMetric
	 *
	 **********************************/
	private int[] changeMetric(Node.NodeType type, int[] qosValues, int i){
		
		//Random generator.
		Random generator = new Random();
		
		//Gets the current benchmark.
		int currentBenchmark = (type.getNumVal() == Node.NodeType.DSL_END.getNumVal())
				 				? dslBenchmarks[i] : voipBenchmarks[i];
				 				
		//We check for what metric we are calculating.
		if (i == 3){
			//Throughput.
			qosValues[i] = generator.nextInt(currentBenchmark); 
			qosValues[i + 1] = (type.getNumVal() == Node.NodeType.DSL_END.getNumVal())
	 				? dslBenchmarks[i + 1] : voipBenchmarks[i + 1];
		} else if (i == 4) {
			//Throughput Indicator.
			if (currentBenchmark == 0) {
				qosValues[i] = currentBenchmark; 
				qosValues[i - 1] = (type.getNumVal() == Node.NodeType.DSL_END.getNumVal())
		 				? generator.nextInt(dslBenchmarks[i - 1]) : generator.nextInt(voipBenchmarks[i - 1]);
			} else { 
				qosValues[i] = generator.nextInt(currentBenchmark);
			}
		} else {
			//Everything else.
			qosValues[i] = currentBenchmark + generator.nextInt(currentBenchmark/10);
		}	
		return qosValues;
	}
	
	
	/**********************************
	 * 
	 * generateRandomNormal
	 *
	 **********************************/
	private int[] generateRandomNormal(Node.NodeType type) {
		//Random generator.
		Random generator = new Random();
		
		//Creates a new integer array.
		int[] qosValues = new int[5];
		for (int i = 0; i < qosValues.length; i++){
			//Gets the current benchmark.
			int currentBenchmark = (type.getNumVal() == Node.NodeType.DSL_END.getNumVal())
					 				? dslBenchmarks[i] : voipBenchmarks[i];
					 				
			//We check for what metric we are calculating.
			if (i == 3){
				//Throughput.
				qosValues[i] = generator.nextInt(1000 - currentBenchmark) + currentBenchmark;
			} else if (i == 4) {
				//Throughput Indicator.
				qosValues[i] = generator.nextInt(Link.BAND_TYPE.NUM_INTERNAL - currentBenchmark) + currentBenchmark;
			} else {
				//Everything else.
				qosValues[i] = generator.nextInt(currentBenchmark);
			}
		}
		
		//Returns the value to the controller.
		return qosValues;
	}

	/**********************************
	 * 
	 * alreadyError
	 *
	 **********************************/
	public boolean alreadyError() {
		return error;
	}
	

	/**********************************
	 * 
	 * dikjstraAlgotirhm
	 *
	 **********************************/
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
	
	/**********************************
	 * 
	 * getShortestPaths
	 *
	 **********************************/
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
	
	public static void setSleepTime(int newTime){
		sleepTime = newTime;
	}
	public static int getSleepTime(){
		return sleepTime;
	}


	public static void setPaused(boolean paused) {
		if (paused){
			pauseBoolean = true;
		} else {
			pauseBoolean = false;
		}
	}


	public void removeNodes() {
		//Nullifies the nodes.
		for (int i = 0; i < dslNodes.size(); i++){
			Monitor current = dslNodes.elementAt(i);
			current.kill();
		}
		for (int i = 0; i < voipNodes.size(); i++){
			Monitor current = voipNodes.elementAt(i);
			current.kill();
		}
		dslNodes = null;
		voipNodes = null;
	}	
}
