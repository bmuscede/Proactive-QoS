import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.GraphMLWriter;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.GraphMetadata.EdgeDefault;
import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;
import edu.uci.ics.jung.io.graphml.NodeMetadata;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconShapeTransformer;
import edu.uci.ics.jung.samples.VertexImageShaperDemo.DemoVertexIconTransformer;
import edu.uci.ics.jung.visualization.LayeredIcon;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JToolBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import java.awt.geom.Point2D;

public class NetworkWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = -1880325660041879306L;
	
	//Main network controller.
	public NetworkController control;
	public NetworkInformationWindow informationWindow;
	
	//JMenu Items
	JMenuItem mntmNew;
	JMenuItem mntmOpen;
	JMenuItem mntmSave;
	JMenuItem mntmSaveAs;
	JMenuItem mntmExit;
	
	//Graph items.
	private Graph<Node, Link> graph;
    private int nodeCount, edgeCount;
    private EditingModalGraphMouse<Node, Link> gm;
    private String filename = null;
    private Map<Node, Icon> iconMap; 
    
    //Factories
    private Factory<Node> nodeFactory;
    private Factory<Link> edgeFactory;
    
    //Swing items.
	private JPanel contentPane;
	private final String IMAGE_LOC = "images";
	protected String[] icons;
	protected ImageIcon[] preloadIcons;
	private String[] iconFiles = { "dsl_end.png", "voip_end.png", "edge.png", "core.png", "gateway.png", "transform.png",
								   "move.png", "simulate.png"};
	private String[] iconError = { "dsl_end_error.png", "voip_end_error.png", "edge_error.png", "core_error.png", "gateway_error.png"};
	private String[] iconYes = { "dsl_end_yes.png", "voip_end_yes.png", "edge_yes.png", "core_yes.png", "gateway_yes.png"};
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
	private AbstractLayout<Node, Link> layout;
	private VisualizationViewer<Node, Link> pnlGraph;
	
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
		
		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(this);
		mnFile.add(mntmNew);
		
		mntmOpen = new JMenuItem("Open...");
		mntmOpen.addActionListener(this);
		mnFile.add(mntmOpen);
		
		JSeparator sepTop = new JSeparator();
		mnFile.add(sepTop);
		
		mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(this);
		mnFile.add(mntmSave);
		
		mntmSaveAs = new JMenuItem("Save As...");
		mntmSaveAs.addActionListener(this);
		mnFile.add(mntmSaveAs);
		
		JSeparator sepBottom = new JSeparator();
		mnFile.add(sepBottom);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("Button.background"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		layout = new StaticLayout<Node, Link>(graph);
		
		pnlGraph = new VisualizationViewer<Node, Link>(layout);
		pnlGraph.setBackground(Color.WHITE);
		contentPane.add(pnlGraph, BorderLayout.CENTER);
		
		JToolBar tblOperations = new JToolBar();

		//Generate image operations.
		icons = new String[iconFiles.length];
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
        
        //Sets up the icons for the nodes.
        final DemoVertexIconTransformer<Node> vertexIconTransformer = 
        		new DemoVertexIconTransformer<Node>();
        final DemoVertexIconShapeTransformer<Node> vertexIconShapeTransformer =
        		new DemoVertexIconShapeTransformer<Node>(new EllipseVertexShapeTransformer<Node>());
        
        //Creates the icon map.
        iconMap = new HashMap<Node, Icon>();
        
        vertexIconShapeTransformer.setIconMap(iconMap);
        vertexIconTransformer.setIconMap(iconMap);
        
        pnlGraph.getRenderContext().setVertexIconTransformer(vertexIconTransformer);
        pnlGraph.getRenderContext().setVertexShapeTransformer(vertexIconShapeTransformer);
        
        //Finally, preloads the image icons in to ensure fast loading.
        preloadIcons = new ImageIcon[icons.length];
        for (int i = 0; i < preloadIcons.length; i++){
        	preloadIcons[i] = new ImageIcon(icons[i]);
        	
        	//Modify the image.
			Image img = preloadIcons[i].getImage();
			img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
			preloadIcons[i] = new ImageIcon(img);
        }
        
	}

	@Override
	public void actionPerformed(ActionEvent event){
		//Get the source of the click.
		JButton clicked = null;
		try{
			clicked = (JButton) event.getSource();
		} catch (ClassCastException e){
			menuEvent(event);
			return;
		}
		
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
		} else {
			//We start the simulation dialog.
			SimulationDialog parameters = new SimulationDialog();
			parameters.setVisible(true);
			
			//Check whether accepted or not.
			if (!parameters.isAccepted()){
				return;
			}
			
			//Get specific simulation parameters.
			int[] dslBenchmarks = parameters.getDSL();
			int[] voipBenchmarks = parameters.getVoIP();
			int failureRate = parameters.getFailure();
			
			//We start the network information pane.
			informationWindow = new NetworkInformationWindow(graph);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
	        int x = (int) rect.getMaxX() - informationWindow.getWidth();
	        int y = (int) rect.getMaxY() - informationWindow.getHeight();
	        informationWindow.setLocation(x, y);
	        informationWindow.setVisible(true);
	        
			//With these values, we start the simulation.
			control = new NetworkController(this, graph, dslBenchmarks, voipBenchmarks, failureRate);
			control.start();
		}
	}
	
	private void menuEvent(ActionEvent event) {
		//Checks to see which object was clicked.
		if (event.getSource().equals(mntmNew)){
			createNew();
		} else if (event.getSource().equals(mntmOpen)) {
			openGraph();
		} else if (event.getSource().equals(mntmSave)){
			saveGraph(filename);
		} else if (event.getSource().equals(mntmSaveAs)) {
			saveGraph(null);
		} else if (event.getSource().equals(mntmExit)) {
			System.exit(0);
		}
	}

	private void saveGraph(String fn) {
		//Check to see if we need save as diagram.
		if (fn == null){
			//Prompts the user for a save file.
			JFileChooser dialog = new JFileChooser();
			
			//Sets the file filter.
			dialog.removeChoosableFileFilter(dialog.getFileFilter());
			dialog.addChoosableFileFilter(new FileNameExtensionFilter("Topology File (.top)", "top"));
			
			int retCode = dialog.showSaveDialog(NetworkWindow.this);
			
			//Check the return code.
			if (retCode != 0)
				return;
			
			//Extract the filename.
			filename = dialog.getSelectedFile().getAbsolutePath();
			String winName = dialog.getSelectedFile().getName();
			if (dialog.getFileFilter().getDescription().equals("Topology File (.top)") &&
					!filename.endsWith("top")){
				filename += ".top";
				winName += ".top";
			}
			
			//Sets the window name.
			this.setTitle("Proactive QoS Monitoring Tool - [" + winName + "]");
		}
		
		//Now we actually save the file.
		GraphMLWriter<Node, Link> writer = new GraphMLWriter<Node, Link>();
		PrintWriter out = null;
		try {
			//We create a writer for the file.
			out = new PrintWriter(
				      new BufferedWriter(
				          new FileWriter(filename)));
			
			//We embed the X, Y coordinates.
			writer.addVertexData("x", null, "0",
					new Transformer<Node, String>() {
						public String transform(Node v) {
							return Double.toString(layout.getX(v));
				        }
				    }
			);
				 
			writer.addVertexData("y", null, "0",
					new Transformer<Node, String>() {
				        public String transform(Node v) {
				            return Double.toString(layout.getY(v));
				       }
				    }
			);
			
			//Now we write the graph to the file.
			writer.save(graph, out);
				        
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void openGraph() {
		//We now choose to open a graph.
		JFileChooser dialog = new JFileChooser();
		
		//Sets the file filter.
		dialog.removeChoosableFileFilter(dialog.getFileFilter());
		dialog.addChoosableFileFilter(new FileNameExtensionFilter("Topology File (.top)", "top"));
		
		int retCode = dialog.showOpenDialog(NetworkWindow.this);
		
		//Check the return code.
		if (retCode != 0)
			return;
		
		//Extract the filename.
		filename = dialog.getSelectedFile().getAbsolutePath();
		String winName = dialog.getSelectedFile().getName();
		
		//Sets the window name.
		this.setTitle("Proactive QoS Monitoring Tool - [" + winName + "]");
		
		//Now we load the file.
		try {
			//We create a buffered reader.
			BufferedReader fileReader = new BufferedReader(
			        new FileReader(filename));
			
			//Create a transformer to read and view the graph elements.
			Transformer<GraphMetadata, Graph<Node, Link>> graphTransformer = new Transformer<GraphMetadata,
			                          														Graph<Node, Link>>() {
				public Graph<Node, Link> transform(GraphMetadata metadata) {
			        metadata.getEdgeDefault();
					//We check what type of graph we have.
					if (!metadata.getEdgeDefault().equals(EdgeDefault.DIRECTED)) {
			            return new UndirectedSparseMultigraph<Node, Link>();
			        }
					
					return null;
			      }
			};
			
			//Create a vertex transformer to produce the nodes.
			Transformer<NodeMetadata, Node> vertexTransformer = new Transformer<NodeMetadata, Node>() {
				public Node transform(NodeMetadata metadata) {
					Node v = nodeFactory.create();
			        v.setX(Double.parseDouble(
			                           metadata.getProperty("x")));
			        v.setY(Double.parseDouble(
			                           metadata.getProperty("y")));
			        return v;
			    }
			};
			
			//Create an edge transformer for the link.
			 Transformer<EdgeMetadata, Link> edgeTransformer = new Transformer<EdgeMetadata, Link>() {
			     public Link transform(EdgeMetadata metadata) {
			         Link e = edgeFactory.create();
			         return e;
			     }
			 };
			 
			 //Create a hyperedge transformer.
			 Transformer<HyperEdgeMetadata, Link> hyperEdgeTransformer
			 = new Transformer<HyperEdgeMetadata, Link>() {
			      public Link transform(HyperEdgeMetadata metadata) {
			          Link e = edgeFactory.create();
			          return e;
			      }
			 };
			 
			 //Creates the object to read.
			 GraphMLReader2<Graph<Node, Link>, Node, Link> reader = 
					 new GraphMLReader2<Graph<Node, Link>, Node, Link> 
			 			(fileReader, graphTransformer, vertexTransformer, edgeTransformer, hyperEdgeTransformer);
			 
			 //Reads the graph.
			 graph = reader.readGraph();
			 
			 //Resets the graph.
			 layout = new StaticLayout<Node, Link>(graph, new Transformer<Node, Point2D>() {
				   public Point2D transform(Node v) {
				       Point2D p = new Point2D.Double(v.getX(), v.getY());
				       return p;
				   }               
			 });
			 
			 //Repaints the panel.
			 pnlGraph.repaint();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (GraphIOException e) {
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * This method clears the previously defined topology and refreshes everything.
	 */
	private void createNew() {
		//Refreshes all the variables.
		createGraph();
		layout.reset();
		layout.setGraph(graph);
		
		//Repaints the panel.
		pnlGraph.repaint();
	}
	
	
	private void createGraph(){
		//Create the graph object and the nodes and link.
        graph = new UndirectedSparseMultigraph<Node, Link>();
        setNodeCount(0); setEdgeCount(0);
        
        //We now need to define both the node and link factories.
        nodeFactory = new Factory<Node>(){
			public Node create() {
				//Set the node count.
				setNodeCount(getNodeCount() + 1);
				
				//We now create the new Node.
				Node nNode = new Node(state);
				
				//Finally, we assign the icon to the graph.
				int stateVal = state.getNumVal();
				try {
	        		Icon icon = new LayeredIcon(preloadIcons[stateVal].getImage());
	        		iconMap.put(nNode, icon);
	        	} catch (Exception e){
	        		e.printStackTrace();
	        	}
				
				//Return the node.
				return nNode;
			}
        };
        
        edgeFactory = new Factory<Link>(){
			public Link create() {
				//We need the bandwidth of the link.
				LinkDialog link = new LinkDialog();
				link.setVisible(true);
				
				//Check to see status.
				if (link.isAccepted() == false)
					return null;
				
				int bandwidth = link.getBandwidth();
				Link.BAND_TYPE type = link.getBandType();
				
				setEdgeCount(getEdgeCount() + 1);
				return new Link(bandwidth, type);
			}
        	
        };
	}

	public int getNodeCount() {
		return nodeCount;
	}
	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}
	public int getEdgeCount() {
		return edgeCount;
	}
	public void setEdgeCount(int edgeCount) {
		this.edgeCount = edgeCount;
	}
	
	public void setNodeIcon(Node selectedNode, int status){
		//Gets the image.
		ImageIcon image = null;
		if (status == 0){
			image = new ImageIcon(System.getProperty("user.dir") + "/" + IMAGE_LOC + "/" + iconFiles[selectedNode.getType().getNumVal()]);
		} else if (status == 1){
			image = new ImageIcon(System.getProperty("user.dir") + "/" + IMAGE_LOC + "/" + iconYes[selectedNode.getType().getNumVal()]);
		} else if (status == 2){
			image = new ImageIcon(System.getProperty("user.dir") + "/" + IMAGE_LOC + "/" + iconError[selectedNode.getType().getNumVal()]);
		}

    	//Modify the image.
		Image img = image.getImage();
		img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
		image = new ImageIcon(img);
		
		//Sets it in the graph.
		Icon icon = new LayeredIcon(image.getImage());
		iconMap.remove(selectedNode);
		iconMap.put(selectedNode, icon);
		
		//Repaints the panel.
		pnlGraph.repaint();
	}
}
