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
		System.out.println(error);
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
		
		return graph;
	}
	
	public Node requestDestinationNode(){
		//First, check for error.
		if (error == false) return null;
		
		Iterator<Node> vertices = graph.getVertices().iterator();
		while (vertices.hasNext()){
			//Gets the next node.
			Node current = vertices.next();
			
			//Checks the node type.
			if (current.getType().equals(Node.NodeType.GATEWAY)) return current;
		}
		
		return null;
	}
	
	public synchronized boolean notifyDetected(Node indicatedNode, Link indicatedLink){
		error = false;
		
		//Finally, wakes up the other threads.
		notifyAll();
		return true;
	}
	
	private void buildErrorGraph(int[] valuesQoS) {

	}

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
		System.out.println(violations);
		
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
			qosValues[i] = generator.nextInt(currentBenchmark);
		} else {
			//Everything else.
			qosValues[i] = currentBenchmark + generator.nextInt(currentBenchmark/10);
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

	public boolean alreadyError() {
		return error;
	}
}
