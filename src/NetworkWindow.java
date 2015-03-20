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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.commons.collections15.Factory;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToolBar;

public class NetworkWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = -1880325660041879306L;
	
	//Graph items.
	private Graph<Node, Link> graph;
    private int nodeCount, edgeCount;
    private EditingModalGraphMouse<Node, Link> gm;
    
    //Swing items.
	private JPanel contentPane;
	private String[] iconFiles = {"dsl_end.gif", "voip_end.gif", "edge.gif", "core.gif", "gateway.gif", "transform.gif" };
	private String[] buttonLabels = {"Create DSL Customer Node", 
			 						 "Create VoIP Customer Node", 
			 					     "Create Edge Router", 
			 					     "Create Core Router", 
			 					     "Create Gateway Router", 
			 					     "Transform" };
	
	//Enum for mode.
	private enum GraphMode { DSL_END, VOIP_END, EDGE, CORE, GATEWAY };
	private GraphMode state = null;
	
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
		ImageIcon[] icons = new ImageIcon[iconFiles.length];
		JButton[] buttons = new JButton[buttonLabels.length];
		  
		//Insert them into the toolbar.
		for (int i = 0; i < buttonLabels.length; ++i) {
			icons[i] = new ImageIcon(iconFiles[i]);
		    buttons[i] = new JButton(icons[i]);
		    buttons[i].addActionListener(this);
		    buttons[i].setToolTipText(buttonLabels[i]);
		      
		    //Check to see if we need a separator.
		    if (iconFiles[i].equals("edge.gif") || iconFiles[i].equals("link.gif")){
		    	tblOperations.addSeparator();
		    }
		    tblOperations.add(buttons[i]);
		}
	
		contentPane.add(tblOperations, BorderLayout.NORTH);
		
		//Now we create a node/link labeler system.
        pnlGraph.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Node>());
        pnlGraph.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Link>());
        
        //Allow the user to add items using their mouse.
        gm = new EditingModalGraphMouse<Node, Link>(pnlGraph.getRenderContext(), null, null); 
        pnlGraph.setGraphMouse(gm);
        
        //Start the graph off in transforming mode.
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	}
	
	private void createGraph(){
		//Create the graph object and the nodes and link.
        graph = new UndirectedSparseMultigraph<Node, Link>();
        nodeCount = 0; edgeCount = 0;
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
			state = GraphMode.DSL_END;
		} else if (tooltip.equals(buttonLabels[5])){
			//We set the mode to transform.
			gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			
			//Now we indicate the type of node.
			state = null;
		}
	}

}
