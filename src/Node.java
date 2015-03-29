
public class Node implements Comparable<Node> {
	//Required to define the enum.
	public static enum NodeType { 
		DSL_END(0, "DSL User"), VOIP_END(1, "VoIP User"), EDGE(2, "Edge"), CORE(3, "Core"), GATEWAY(4, "Gateway");

	    private int numVal;
	    private String name;
	    
	    NodeType(int numVal, String name) {
	        this.numVal = numVal;
	        this.name = name;
	    }

	    public int getNumVal() {
	        return numVal;
	    }
	    
	    public String getName(){
	    	return name;
	    }
	};
	
	//Private variables.
	private NodeType node;
	private double x;
	private double y;
	
	//For pathfinding.
	public double minDistance = Double.POSITIVE_INFINITY;
	public Node previous = null;
	
	//Current QoS benchmarks.
	public int currPacketLoss = -1;
	public int currJitter = -1;
	public int currLatency = -1;
	public int currThroughput = -1; 
	public Link.BAND_TYPE currThroughputType = null;
	
	public Node(NodeType conType){
		node = conType;
	}
	
	public void setQoSMetrics(int[] metrics){
		//Sets up the current QoS metrics.
		currPacketLoss = metrics[0];
		currJitter = metrics[1];
		currLatency = metrics[2];
		currThroughput = metrics[3];
		currThroughputType = Link.BAND_TYPE.valueOf(metrics[4]);
	}
	
	public String toString(){
		return node.getName();
	}

	public NodeType getType(){
		return node;
	}
	
	public void setX(double xVal) {
		x = xVal;
	}

	public void setY(double yVal) {
		y = yVal;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	
    public int compareTo(Node other)
    {
        return Double.compare(minDistance, other.minDistance);
    }
}
