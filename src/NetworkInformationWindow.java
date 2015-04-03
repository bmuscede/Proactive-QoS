import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;

import edu.uci.ics.jung.graph.Graph;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class NetworkInformationWindow extends JFrame {
	private HashMap<Node, Integer> position;
	
	private JPanel contentPane;
	private JPanel[] pnlElements;
	private JLabel[] lblCurrentState;
	private JLabel[] lblMode;
	private JSeparator[] separator;
	private JPanel[] pnlMonitor;
	private JLabel[] lblCurrent;
	private JLabel[] lblPacketLoss;
	private JLabel[] lblLatency;
	private JLabel[] lblJitter;
	private JLabel[] lblThroughput;
	private JPanel[] pnlDetect;
	private JLabel[] lblCurrentlyScanning;
	private JLabel[] lblNode;
	private JLabel[] lblMetricsHere;
	private JLabel[] lblDPacketLoss;
	private JLabel[] lblDLatency;
	private JLabel[] lblDJitter;
	private JLabel[] lblDThroughput;
	private JSeparator[] sepDetect;
	private JLabel[] lblIndicator;
	
	/**
	 * Create the frame.
	 */
	public NetworkInformationWindow(Graph<Node, Link> graph) {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("Network Status");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 328, 406);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel pnlSettings = new JPanel();
		tabbedPane.addTab("Simulator Settings", null, pnlSettings, null);
		
		//Gets the number of DSL and VoIP end nodes.
		Iterator<Node> graphIt = graph.getVertices().iterator();
		int numEnd = 0;
		while (graphIt.hasNext()){
			Node curr = graphIt.next();
			if (curr.getType().getNumVal() == Node.NodeType.DSL_END.getNumVal() ||
					curr.getType().getNumVal() == Node.NodeType.VOIP_END.getNumVal()){
				numEnd++;
			}
		}
		
		if (numEnd == 0) return;
		
		//Sets up node to tab correspondence.
		position = new HashMap<Node, Integer>();
		graphIt = graph.getVertices().iterator();
		
		//Creates each of the elements for each node.
		pnlElements = new JPanel[numEnd];
		lblCurrentState = new JLabel[numEnd];
		lblMode = new JLabel[numEnd];
		separator = new JSeparator[numEnd];
		pnlMonitor = new JPanel[numEnd];
		lblCurrent = new JLabel[numEnd];
		lblPacketLoss = new JLabel[numEnd];
		lblLatency = new JLabel[numEnd];
		lblJitter = new JLabel[numEnd];
		lblThroughput = new JLabel[numEnd];
		pnlDetect = new JPanel[numEnd];
		lblCurrentlyScanning = new JLabel[numEnd];
		lblNode = new JLabel[numEnd];
		lblMetricsHere = new JLabel[numEnd];
		lblDPacketLoss = new JLabel[numEnd];
		lblDLatency = new JLabel[numEnd];
		lblDJitter = new JLabel[numEnd];
		lblDThroughput = new JLabel[numEnd];
		sepDetect = new JSeparator[numEnd];
		lblIndicator = new JLabel[numEnd];
		
		for (int i = 0; i < numEnd; i++){
			//Required for setting up information window.
			Node curr = null;
			while(true){
				curr = graphIt.next();
				if (curr.getType().getNumVal() == Node.NodeType.DSL_END.getNumVal() ||
						curr.getType().getNumVal() == Node.NodeType.VOIP_END.getNumVal()){
					break;
				}
			}
			position.put(curr, i);
			
			pnlElements[i] = new JPanel();
			tabbedPane.addTab(curr.getType().getName() + " #" + i, null, pnlElements[i], null);
			pnlElements[i].setLayout(null);
			
			lblCurrentState[i] = new JLabel("Current State:");
			lblCurrentState[i].setHorizontalAlignment(SwingConstants.CENTER);
			lblCurrentState[i].setFont(new Font("Tahoma", Font.BOLD, 15));
			lblCurrentState[i].setBounds(10, 11, 287, 24);
			pnlElements[i].add(lblCurrentState[i]);
			
			lblMode[i] = new JLabel("Monitor Mode");
			lblMode[i].setHorizontalAlignment(SwingConstants.CENTER);
			lblMode[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblMode[i].setBounds(10, 34, 287, 24);
			pnlElements[i].add(lblMode[i]);
			
			separator[i] = new JSeparator();
			separator[i].setBounds(10, 69, 287, 2);
			pnlElements[i].add(separator[i]);
			
			pnlMonitor = new JPanel();
			pnlMonitor.setBounds(10, 82, 287, 161);
			pnlElements[i].add(pnlMonitor);
			pnlMonitor.setLayout(null);
			
			lblCurrent = new JLabel("Current QoS Metrics:");
			lblCurrent.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblCurrent.setBounds(0, 0, 287, 24);
			pnlMonitor.add(lblCurrent);
			
			lblPacketLoss = new JLabel("Packet Loss: <TEST>%");
			lblPacketLoss.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblPacketLoss.setBounds(10, 42, 258, 24);
			pnlMonitor.add(lblPacketLoss);
			
			lblLatency = new JLabel("Latency: <TEST>ms");
			lblLatency.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblLatency.setBounds(10, 67, 258, 24);
			pnlMonitor.add(lblLatency);
			
			lblJitter = new JLabel("Jitter: <TEST>ms");
			lblJitter.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblJitter.setBounds(10, 92, 258, 24);
			pnlMonitor.add(lblJitter);
			
			lblThroughput = new JLabel("Throughput: <TEST>Mbps");
			lblThroughput.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblThroughput.setBounds(10, 117, 258, 24);
			pnlMonitor.add(lblThroughput);
			
			pnlDetect = new JPanel();
			pnlDetect.setVisible(false);
			pnlDetect.setBounds(10, 79, 287, 249);
			pnlElements[i].add(pnlDetect);
			pnlDetect.setLayout(null);
			
			lblCurrentlyScanning = new JLabel("Currently Scanning:");
			lblCurrentlyScanning.setFont(new Font("Tahoma", Font.ITALIC, 15));
			lblCurrentlyScanning.setBounds(10, 11, 267, 31);
			pnlDetect.add(lblCurrentlyScanning);
			
			lblNode = new JLabel("<NODE>");
			lblNode.setHorizontalAlignment(SwingConstants.CENTER);
			lblNode.setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblNode.setBounds(10, 43, 267, 25);
			pnlDetect.add(lblNode);
			
			lblMetricsHere = new JLabel("Metrics Here:");
			lblMetricsHere.setFont(new Font("Tahoma", Font.ITALIC, 15));
			lblMetricsHere.setBounds(10, 66, 267, 31);
			pnlDetect.add(lblMetricsHere);
			
			lblDPacketLoss = new JLabel("Packet Loss: <TEST>%");
			lblDPacketLoss.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDPacketLoss.setBounds(10, 100, 258, 24);
			pnlDetect.add(lblDPacketLoss);
			
			lblDLatency = new JLabel("Latency: <TEST>ms");
			lblDLatency.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDLatency.setBounds(10, 125, 258, 24);
			pnlDetect.add(lblDLatency);
			
			lblDJitter = new JLabel("Jitter: <TEST>ms");
			lblDJitter.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDJitter.setBounds(10, 150, 258, 24);
			pnlDetect.add(lblDJitter);
			
			lblDThroughput = new JLabel("Throughput: <TEST>Mbps");
			lblDThroughput.setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDThroughput.setBounds(10, 175, 258, 24);
			pnlDetect.add(lblDThroughput);
			
			sepDetect = new JSeparator();
			sepDetect.setBounds(0, 210, 287, 2);
			pnlDetect.add(sepDetect);
			
			lblIndicator = new JLabel("No Problem Here");
			lblIndicator.setHorizontalAlignment(SwingConstants.CENTER);
			lblIndicator.setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblIndicator.setBounds(10, 224, 267, 14);
			pnlDetect.add(lblIndicator);
		}
		
		//For testing purposes
		JPanel pnltest = new JPanel();
		tabbedPane.addTab(" #", null, pnltest, null);
		pnltest.setLayout(null);
		
		JLabel lblCurrentStat = new JLabel("Current State:");
		lblCurrentStat.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentStat.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCurrentStat.setBounds(10, 11, 287, 24);
		pnltest.add(lblCurrentStat);
		
		JLabel lblMod = new JLabel("Monitor Mode");
		lblMod.setHorizontalAlignment(SwingConstants.CENTER);
		lblMod.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMod.setBounds(10, 34, 287, 24);
		pnltest.add(lblMod);
		
		JSeparator separato = new JSeparator();
		separato.setBounds(10, 69, 287, 2);
		pnltest.add(separato);
		
		
	}
	
	public void changeNodeMode(Node current, int type){
		//We get which node corresponds.
	}
}
