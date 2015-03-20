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
import javax.swing.JToolBar;

public class NetworkWindow extends JFrame {
	private static final long serialVersionUID = -1880325660041879306L;
	
	private Graph<Node, Link> graph;
    private int nodeCount, edgeCount;
	private JPanel contentPane;

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
		
		JPanel pnlGraph = new JPanel();
		pnlGraph.setBackground(Color.WHITE);
		contentPane.add(pnlGraph, BorderLayout.CENTER);
		
		JToolBar tblOperations = new JToolBar();
		
		//Generate image operations.
		String[] iconFiles = {"dsl_end.gif", "voip_end.gif", "edge.gif", "core.gif", "gateway.gif", "link.gif" };
		String[] buttonLabels = {"Create DSL Customer Node", 
								 "Create VoIP Customer Node", 
								 "Create Edge Router", 
								 "Create Core Router", 
								 "Create Gateway Router", 
								 "Draw Link" };
		ImageIcon[] icons = new ImageIcon[iconFiles.length];
		JButton[] buttons = new JButton[buttonLabels.length];
		  
		//Insert them into the toolbar.
		for (int i = 0; i < buttonLabels.length; ++i) {
			icons[i] = new ImageIcon(iconFiles[i]);
		    buttons[i] = new JButton(icons[i]);
		    buttons[i].setToolTipText(buttonLabels[i]);
		      
		    //Check to see if we need a separator.
		    if (iconFiles[i].equals("edge.gif") || iconFiles[i].equals("link.gif")){
		    	tblOperations.addSeparator();
		    }
		    tblOperations.add(buttons[i]);
		}
	
		contentPane.add(tblOperations, BorderLayout.NORTH);
	}
	
	private void createGraph(){
		//Create the graph object and the nodes and link.
        graph = new UndirectedSparseMultigraph<Node, Link>();
        nodeCount = 0; edgeCount = 0;
	}

}
