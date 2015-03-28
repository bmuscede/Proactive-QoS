import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import edu.uci.ics.jung.graph.Graph;

public class NetworkController {
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
				dslNodes.add(new Monitor(current, dslBenchmarks, this));
			} else if (current.getType().equals(Node.NodeType.VOIP_END)){
				voipNodes.add(new Monitor(current, voipBenchmarks, this));
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
	
	public int[] requestData(Node.NodeType type) {
		//Determines whether there is an error.
		boolean error = randomizeError();
		int[] valuesQoS;
		
		if (error){
			//Generates data with random error.
			valuesQoS = generateRandomError(type);
			System.out.println("Error for: " + type.getName());
		} else {
			//Generates normal QoS.
			valuesQoS = generateRandomNormal(type);
		}
		
		return valuesQoS;
	}

	private int[] generateRandomError(Node.NodeType type) {
		return null;
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
}
