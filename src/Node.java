
public class Node {
	//Required to define the enum.
	public static enum NodeType { DSL_END, VOIP_END, EDGE, CORE, GATEWAY };
	
	//Private variables.
	private NodeType node;
	
	public Node(NodeType conType){
		node = conType;
	}
	
	public String toString(){
		return node.name();
	}
}
