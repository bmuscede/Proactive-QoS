
public class Node {
	//Required to define the enum.
	public static enum NodeType { DSL_END, VOIP_END, EDGE, CORE, GATEWAY };
	
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
