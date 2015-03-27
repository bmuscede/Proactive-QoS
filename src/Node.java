
public class Node {
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
		
	public Node(NodeType conType){
		node = conType;
	}
	
	public String toString(){
		return node.getName();
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
}
