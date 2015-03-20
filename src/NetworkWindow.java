import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.apache.commons.collections15.Factory;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JToolBar;
import javax.swing.JMenuItem;

public class NetworkWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = -1880325660041879306L;
	
	//Graph items.
	private Graph<Node, Link> graph;
    private int nodeCount, edgeCount;
    private EditingModalGraphMouse<Node, Link> gm;
    
    //Factories
    Factory<Node> nodeFactory;
    Factory<Link> edgeFactory;
    
    //Swing items.
	private JPanel contentPane;
	private final String IMAGE_LOC = "images";
	private String[] iconFiles = { "dsl_end.png", "voip_end.png", "edge.png", "core.png", "gateway.png", "transform.png",
								   "move.png", "simulate.png"};
	private String[] buttonLabels = {"Create DSL Customer Node", 
			 						 "Create VoIP Customer Node", 
			 					     "Create Edge Router", 
			 					     "Create Core Router", 
			 					     "Create Gateway Router", 
			 					     "Move View",
			 					     "Move Individual Nodes", 
			 					     "Simulate Current Topology" };
	private String[] buttonText = {"DSL Node", "Voip Node", "Edge", "Core", "Gateway", "Transform", "Move", "Simulate"};
	private final int SIZE = 20;
	
	//Enum for mode.
	private Node.NodeType state = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NetworkWindow frame = new NetworkWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NetworkWindow() {
		//Create the graph.
		createGraph();
	
		setTitle("Proactive QoS Monitoring Tool");
		
		//Draw the window components.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 630, 450);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);
		
		JMenu mnSaveAs = new JMenu("Save As...");
		mnFile.add(mnSaveAs);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		Layout<Node, Link> layout = new StaticLayout<Node, Link>(graph);
		VisualizationViewer<Node, Link> pnlGraph = new VisualizationViewer<Node, Link>(layout);
		pnlGraph.setBackground(Color.WHITE);
		contentPane.add(pnlGraph, BorderLayout.CENTER);
		
		JToolBar tblOperations = new JToolBar();

		//Generate image operations.
		String[] icons = new String[iconFiles.length];
		JButton[] buttons = new JButton[buttonLabels.length];
		  
		//Insert them into the toolbar.
		for (int i = 0; i < buttonLabels.length; ++i) {
			//Create the new button.
			buttons[i] = new JButton();
			
			//First, set the button icon.
			icons[i] = System.getProperty("user.dir") + "/" + IMAGE_LOC + "/" + iconFiles[i];		
			ImageIcon icon = new ImageIcon(icons[i]);
			  
			//Modify the image.
			Image img = icon.getImage();
			img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
			icon = new ImageIcon(img);
			
			//Set it as the button.
			buttons[i].setIcon(icon);
			
			//Add the text.
			buttons[i].setVerticalTextPosition(SwingConstants.BOTTOM);
		    buttons[i].setHorizontalTextPosition(SwingConstants.CENTER);
		    
		    //Set the button properties.
		    buttons[i].addActionListener(this);
		    buttons[i].setToolTipText(buttonLabels[i]);
		    buttons[i].setText(buttonText[i]);
		    
		    //Check to see if we need a separator.
		    if (iconFiles[i].equals("edge.png") || iconFiles[i].equals("transform.png") ||
		    	iconFiles[i].equals("simulate.png")){
		    	tblOperations.addSeparator();
		    }
		    tblOperations.add(buttons[i]);
		}
	
		contentPane.add(tblOperations, BorderLayout.NORTH);
		
		//Now we create a node/link labeler system.
        pnlGraph.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Node>());
        pnlGraph.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Link>());
        
        //Allow the user to add items using their mouse.
        gm = new EditingModalGraphMouse<Node, Link>(pnlGraph.getRenderContext(), 
        											nodeFactory, edgeFactory); 
        pnlGraph.setGraphMouse(gm);
        
        //Start the graph off in transforming mode.
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	}
	
	private void createGraph(){
		//Create the graph object and the nodes and link.
        graph = new UndirectedSparseMultigraph<Node, Link>();
        nodeCount = 0; edgeCount = 0;
        
        //We now need to define both the node and link factories.
        nodeFactory = new Factory<Node>(){
			public Node create() {
				nodeCount++;
				return new Node(state);
			}
        };
        
        edgeFactory = new Factory<Link>(){
			public Link create() {
				edgeCount++;
				return new Link();
			}
        	
        };
	}

	@Override
	public void actionPerformed(ActionEvent event){
		//Get the source of the click.
		JButton clicked = (JButton) event.getSource();
		String tooltip = clicked.getToolTipText();
		
		//We now see which button in the toolbar was pressed.
		if (tooltip.equals(buttonLabels[0])){
			//We set the mode to edit.
			gm.setMode(ModalGraphMouse.Mode.EDITING);
			
			//Now we indicate the type of node.
			state = Node.NodeType.DSL_END;
		} else if (tooltip.equals(buttonLabels[1])){
			//We set the mode to transform.
			gm.setMode(ModalGraphMouse.Mode.EDITING);
			
			//Now we indicate the type of node.
			state = Node.NodeType.VOIP_END;
		} else if (tooltip.equals(buttonLabels[2])){
			//We set the mode to transform.
			gm.setMode(ModalGraphMouse.Mode.EDITING);
			
			//Now we indicate the type of node.
			state = Node.NodeType.EDGE;
		} else if (tooltip.equals(buttonLabels[3])){
			//We set the mode to transform.
			gm.setMode(ModalGraphMouse.Mode.EDITING);
			
			//Now we indicate the type of node.
			state = Node.NodeType.CORE;
		} else if (tooltip.equals(buttonLabels[4])){
			//We set the mode to transform.
			gm.setMode(ModalGraphMouse.Mode.EDITING);
			
			//Now we indicate the type of node.
			state = Node.NodeType.GATEWAY;
		} else if (tooltip.equals(buttonLabels[5])){
			//We set the mode to transform.
			gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			
			//Now we indicate the type of node.
			state = null;
		} else if (tooltip.equals(buttonLabels[6])){
			//We now set the mode to move nodes.
			gm.setMode(ModalGraphMouse.Mode.PICKING);
			
			//Now we indicate the type of node.
			state = null;
		}
	}
}
