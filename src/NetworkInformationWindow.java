import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;

import edu.uci.ics.jung.graph.Graph;

import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NetworkInformationWindow extends JFrame {
	private static final long serialVersionUID = 2623914264077949541L;

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
	private JTabbedPane tabbedPane;
	
	private final int SIZE = 20;
	private NetworkWindow networkWindow;
	
	/**
	 * Create the frame.
	 */
	public NetworkInformationWindow(Graph<Node, Link> graph, NetworkWindow parent) {
		networkWindow = parent;
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("Network Status");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 328, 406);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel pnlSettings = new JPanel();
		tabbedPane.addTab("Simulator Settings", null, pnlSettings, null);
		pnlSettings.setLayout(null);
		
		JLabel lblSettings = new JLabel("Simulation Settings\r\n");
		lblSettings.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblSettings.setBounds(10, 11, 287, 28);
		pnlSettings.add(lblSettings);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 48, 287, 2);
		pnlSettings.add(separator_1);
		
		JLabel lblChangeTimeoutRate = new JLabel("Change Timeout Rate:");
		lblChangeTimeoutRate.setBounds(10, 72, 135, 20);
		pnlSettings.add(lblChangeTimeoutRate);
		
		final JSpinner spnRate = new JSpinner();
		spnRate.setModel(new SpinnerNumberModel(new Integer(5000), new Integer(1), null, new Integer(1)));
		spnRate.setBounds(155, 72, 89, 20);
		spnRate.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            NetworkController.setSleepTime((int) spnRate.getValue());
	        }
	    });
		pnlSettings.add(spnRate);
		
		JLabel lblMs = new JLabel("ms");
		lblMs.setBounds(251, 75, 46, 14);
		pnlSettings.add(lblMs);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 113, 287, 2);
		pnlSettings.add(separator_2);
		
		//First, set the button icon.
		String imageLoc = System.getProperty("user.dir") + "/images/pause.png";		
		ImageIcon icon = new ImageIcon(imageLoc);
		  
		//Modify the image.
		Image img = icon.getImage();
		img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		
		final JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnPause.getText().equals("Pause")){
					//Indicates that threads will be paused.
					NetworkController.setPaused(true);
					
					//Changes the button.
					btnPause.setText("Resume");
					String imageLoc = System.getProperty("user.dir") + "/images/resume.png";		
					ImageIcon icon = new ImageIcon(imageLoc);
					  
					//Modify the image.
					Image img = icon.getImage();
					img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
					icon = new ImageIcon(img);
					btnPause.setIcon(icon);
				} else {
					//Indicates that threads will be woken up.
					NetworkController.setPaused(false);
					
					//Changes the button.
					btnPause.setText("Pause");
					String imageLoc = System.getProperty("user.dir") + "/images/pause.png";		
					ImageIcon icon = new ImageIcon(imageLoc);
					  
					//Modify the image.
					Image img = icon.getImage();
					img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
					icon = new ImageIcon(img);
					btnPause.setIcon(icon);
				}
			}
		});

		//Set it as the button.
		btnPause.setIcon(icon);
		
		//Add the text.
		btnPause.setVerticalTextPosition(SwingConstants.BOTTOM);
	    btnPause.setHorizontalTextPosition(SwingConstants.CENTER);
		btnPause.setBounds(10, 269, 89, 59);
		pnlSettings.add(btnPause);
		
		//First, set the button icon.
		imageLoc = System.getProperty("user.dir") + "/images/stop.png";		
		icon = new ImageIcon(imageLoc);
		  
		//Modify the image.
		img = icon.getImage();
		img = img.getScaledInstance(SIZE, SIZE, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//We need to call the Network Controller.
				networkWindow.resetSimulation();
				dispose();
			}
		});

		//Set it as the button.
		btnStop.setIcon(icon);
		
		//Add the text.
		btnStop.setVerticalTextPosition(SwingConstants.BOTTOM);
	    btnStop.setHorizontalTextPosition(SwingConstants.CENTER);
		btnStop.setBounds(208, 269, 89, 59);
		pnlSettings.add(btnStop);
		
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
			
			pnlMonitor[i] = new JPanel();
			pnlMonitor[i].setBounds(10, 82, 287, 161);
			pnlElements[i].add(pnlMonitor[i]);
			pnlMonitor[i].setLayout(null);
			
			lblCurrent[i] = new JLabel("Current QoS Metrics:");
			lblCurrent[i].setFont(new Font("Tahoma", Font.PLAIN, 15));
			lblCurrent[i].setBounds(0, 0, 287, 24);
			pnlMonitor[i].add(lblCurrent[i]);
			
			lblPacketLoss[i] = new JLabel("Packet Loss: <TEST>%");
			lblPacketLoss[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblPacketLoss[i].setBounds(10, 42, 258, 24);
			pnlMonitor[i].add(lblPacketLoss[i]);
			
			lblLatency[i] = new JLabel("Latency: <TEST>ms");
			lblLatency[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblLatency[i].setBounds(10, 67, 258, 24);
			pnlMonitor[i].add(lblLatency[i]);
			
			lblJitter[i] = new JLabel("Jitter: <TEST>ms");
			lblJitter[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblJitter[i].setBounds(10, 92, 258, 24);
			pnlMonitor[i].add(lblJitter[i]);
			
			lblThroughput[i] = new JLabel("Throughput: <TEST>Mbps");
			lblThroughput[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblThroughput[i].setBounds(10, 117, 258, 24);
			pnlMonitor[i].add(lblThroughput[i]);
			
			pnlDetect[i] = new JPanel();
			pnlDetect[i].setVisible(false);
			pnlDetect[i].setBounds(10, 79, 287, 249);
			pnlElements[i].add(pnlDetect[i]);
			pnlDetect[i].setLayout(null);
			
			lblCurrentlyScanning[i] = new JLabel("Currently Scanning:");
			lblCurrentlyScanning[i].setFont(new Font("Tahoma", Font.ITALIC, 15));
			lblCurrentlyScanning[i].setBounds(10, 11, 267, 31);
			pnlDetect[i].add(lblCurrentlyScanning[i]);
			
			lblNode[i] = new JLabel("<NODE>");
			lblNode[i].setHorizontalAlignment(SwingConstants.CENTER);
			lblNode[i].setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblNode[i].setBounds(10, 43, 267, 25);
			pnlDetect[i].add(lblNode[i]);
			
			lblMetricsHere[i] = new JLabel("Metrics Here:");
			lblMetricsHere[i].setFont(new Font("Tahoma", Font.ITALIC, 15));
			lblMetricsHere[i].setBounds(10, 66, 267, 31);
			pnlDetect[i].add(lblMetricsHere[i]);
			
			lblDPacketLoss[i] = new JLabel("Packet Loss: <TEST>%");
			lblDPacketLoss[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDPacketLoss[i].setBounds(10, 100, 258, 24);
			pnlDetect[i].add(lblDPacketLoss[i]);
			
			lblDLatency[i] = new JLabel("Latency: <TEST>ms");
			lblDLatency[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDLatency[i].setBounds(10, 125, 258, 24);
			pnlDetect[i].add(lblDLatency[i]);
			
			lblDJitter[i] = new JLabel("Jitter: <TEST>ms");
			lblDJitter[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDJitter[i].setBounds(10, 150, 258, 24);
			pnlDetect[i].add(lblDJitter[i]);
			
			lblDThroughput[i] = new JLabel("Throughput: <TEST>Mbps");
			lblDThroughput[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
			lblDThroughput[i].setBounds(10, 175, 258, 24);
			pnlDetect[i].add(lblDThroughput[i]);
			
			sepDetect[i] = new JSeparator();
			sepDetect[i].setBounds(0, 210, 287, 2);
			pnlDetect[i].add(sepDetect[i]);
			
			lblIndicator[i] = new JLabel("No Problem Here");
			lblIndicator[i].setHorizontalAlignment(SwingConstants.CENTER);
			lblIndicator[i].setFont(new Font("Tahoma", Font.PLAIN, 13));
			lblIndicator[i].setBounds(10, 224, 267, 14);
			pnlDetect[i].add(lblIndicator[i]);
		}		
	}
	
	public void changeNodeMode(Node current, int type){
		//We get which node corresponds.
		int nodeVal = position.get(current);
		
		//Now we change that window information.
		if (type == 0){
			//We have monitor mode.
			lblMode[nodeVal].setText("Monitor Mode");
			pnlMonitor[nodeVal].setVisible(true);
			pnlDetect[nodeVal].setVisible(false);
		} else {
			lblMode[nodeVal].setText("Detection Mode");
			pnlMonitor[nodeVal].setVisible(false);
			pnlDetect[nodeVal].setVisible(true);
			
			//Sets focus to tab for detection mode.
			if (tabbedPane.getSelectedIndex() != 0)
				tabbedPane.setSelectedIndex(nodeVal + 1);
		}
	}

	public void passMonitorMetrics(Node current, int[] currentQoS, boolean[] improper) {
		//Get the node value.
		int nodeVal = position.get(current);
		
		//Sets up each of the values.
		lblPacketLoss[nodeVal].setText("Packet Loss: " + currentQoS[0] + "%"); 
		lblLatency[nodeVal].setText("Latency: " + currentQoS[1] + "ms"); 
		lblJitter[nodeVal].setText("Jitter: " + currentQoS[2] + "ms"); 
		lblThroughput[nodeVal].setText("Throughput: " + currentQoS[3] + Link.BAND_TYPE.valueOf(currentQoS[4])); 
		
		//Changes their colours.
		if (improper[0]) 
			lblPacketLoss[nodeVal].setForeground(Color.RED);
		else
			lblPacketLoss[nodeVal].setForeground(Color.GREEN);
		if (improper[1]) 
			lblLatency[nodeVal].setForeground(Color.RED);
		else
			lblLatency[nodeVal].setForeground(Color.GREEN);
		if (improper[2]) 
			lblJitter[nodeVal].setForeground(Color.RED);
		else
			lblJitter[nodeVal].setForeground(Color.GREEN);
		if (improper[3]) 
			lblThroughput[nodeVal].setForeground(Color.RED);
		else
			lblThroughput[nodeVal].setForeground(Color.GREEN);
	}
	
	public void setExaminingNode(Node source, Node current){
		//Get the node value.
		int nodeVal = position.get(source);
		lblNode[nodeVal].setText(current.getType().toString());
	}

	public void setErrorStatus(Node current, boolean isError) {
		//Get the node value.
		int nodeVal = position.get(current);
		
		//Check for error.
		if (isError){
			lblIndicator[nodeVal].setText("QoS Problem Located Here");
		} else {
			lblIndicator[nodeVal].setText("No QoS Problem Here");
		}
	}
	
	public void passDetectionMetrics(Node source, Node current, boolean[] qosError){
		//Get the node value.
		int nodeVal = position.get(source);
		
		//Sets up each of the values.
		lblDPacketLoss[nodeVal].setText("Packet Loss: " + current.currPacketLoss + "%"); 
		lblDLatency[nodeVal].setText("Latency: " + current.currLatency + "ms"); 
		lblDJitter[nodeVal].setText("Jitter: " + current.currJitter + "ms"); 
		lblDThroughput[nodeVal].setText("Throughput: " + current.currThroughput + current.currThroughputType.getName()); 
		
		//Changes their colours.
		if (qosError[0]) 
			lblPacketLoss[nodeVal].setForeground(Color.RED);
		else
			lblPacketLoss[nodeVal].setForeground(Color.GREEN);
		if (qosError[1]) 
			lblLatency[nodeVal].setForeground(Color.RED);
		else
			lblLatency[nodeVal].setForeground(Color.GREEN);
		if (qosError[2]) 
			lblJitter[nodeVal].setForeground(Color.RED);
		else
			lblJitter[nodeVal].setForeground(Color.GREEN);
		if (qosError[3]) 
			lblThroughput[nodeVal].setForeground(Color.RED);
		else
			lblThroughput[nodeVal].setForeground(Color.GREEN);
	}
}
