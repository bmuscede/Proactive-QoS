
public class Node {
	//Required to define the enum.
	public static enum NodeType { 
		DSL_END(0), VOIP_END(1), EDGE(2), CORE(3), GATEWAY(4);

	    private int numVal;

	    NodeType(int numVal) {
	        this.numVal = numVal;
	    }

	    public int getNumVal() {
	        return numVal;
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
		return node.name();
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
