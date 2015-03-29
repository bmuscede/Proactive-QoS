import java.util.Iterator;
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
	
	//Graph
	private Graph<Node, Link> graph;
	private Vector<Monitor> dslNodes;
	private Vector<Monitor> voipNodes;
	
	public NetworkController(Graph<Node, Link> graph, int[] dslBenchmarks,
			int[] voipBenchmarks, int failureRate) {
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
	
	public void start(){
		//Starts each of the monitor threads.
		for (int i = 0; i < dslNodes.size(); i++){
			dslNodes.elementAt(i).start();
		}
		for (int i = 0; i < voipNodes.size(); i++){
			voipNodes.elementAt(i).start();
		}
	}

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
	
	public synchronized int[] requestData(Node.NodeType type) throws InterruptedException {
		if (alreadyError()){
			System.out.println("Waiting for the error to be resolved.");
			wait();
			System.out.println("Woken up!");
		}
		
		//Determines whether there is an error.
		error = randomizeError();
		int[] valuesQoS;
		
		if (error){
			//Generates data with random error.
			valuesQoS = generateRandomError(type);
			buildErrorGraph(valuesQoS);
		} else {
			//Generates normal QoS.
			valuesQoS = generateRandomNormal(type);
		}
		
		return valuesQoS;
	}

	public Graph<Node, Link> requestErrorGraph(){
		//First, check for error.
		if (error == false) return null;
		
		return null;
	}
	
	public Node requestDestinationNode(){
		//First, check for error.
		if (error == false) return null;
		
		return null;
	}
	
	public synchronized void notifyDetected(Node indicatedNode, Link indicatedLink){
		error = false;
		
		//Finally, wakes up the other threads.
		notifyAll();
	}
	
	private void buildErrorGraph(int[] valuesQoS) {

	}

	private int[] generateRandomError(Node.NodeType type) {
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
				qosValues[i] = currentBenchmark + generator.nextInt(1000 - currentBenchmark);
			} if (i == 4) {
				//Throughput Indicator.
				qosValues[i] = currentBenchmark + generator.nextInt(Link.BAND_TYPE.NUM_INTERNAL);
			} else {
				//Everything else.
				qosValues[i] = generator.nextInt(currentBenchmark);
			}
		}
		
		return qosValues;
	}
	
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
				qosValues[i] = currentBenchmark + generator.nextInt(1000 - currentBenchmark);
			} if (i == 4) {
				//Throughput Indicator.
				qosValues[i] = currentBenchmark + generator.nextInt(Link.BAND_TYPE.NUM_INTERNAL);
			} else {
				//Everything else.
				qosValues[i] = generator.nextInt(currentBenchmark);
			}
		}
		
		//Returns the value to the controller.
		return qosValues;
	}

	public boolean alreadyError() {
		return error;
	}
}
