import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class SimulationDialog extends JDialog {
	//Modal Parameters
	private boolean accepted;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtJitterDSL;
	private JTextField txtLatencyDSL;
	private JTextField txtThroughputDSL;
	private JTextField txtJitterVOIP;
	private JTextField txtLatencyVOIP;
	private JTextField txtThroughputVOIP;
	private JSpinner spnFailure;
	private JSpinner spnPacketLossDSL;
	private JSpinner spnPacketLossVOIP;
	private JComboBox cmbTypeDSL;
	private JComboBox cmbTypeVOIP;
	
	/**
	 * Create the dialog.
	 */
	public SimulationDialog() {
		accepted = false;
		
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Proactive QoS Monitoring Tool - [Simulate]");
		setBounds(100, 100, 326, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblTitle = new JLabel("Simulation Options");
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
			lblTitle.setBounds(10, 10, 303, 25);
			lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 20));
			contentPanel.add(lblTitle);
		}
		
		JPanel pnlDSL = new JPanel();
		pnlDSL.setBorder(new TitledBorder(null, "DSL QoS Benchmarks", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlDSL.setBounds(10, 46, 303, 125);
		contentPanel.add(pnlDSL);
		pnlDSL.setLayout(null);
		
		JLabel lblPacketLoss = new JLabel("Packet Loss:");
		lblPacketLoss.setForeground(Color.BLACK);
		lblPacketLoss.setBounds(10, 23, 89, 14);
		pnlDSL.add(lblPacketLoss);
		
		JLabel lblJitter = new JLabel("Jitter:");
		lblJitter.setBounds(10, 48, 89, 14);
		pnlDSL.add(lblJitter);
		
		JLabel lblLatency = new JLabel("Latency:");
		lblLatency.setBounds(10, 73, 89, 14);
		pnlDSL.add(lblLatency);
		
		JLabel lblThroughput = new JLabel("Throughput:");
		lblThroughput.setBounds(10, 100, 89, 14);
		pnlDSL.add(lblThroughput);
		
		spnPacketLossDSL = new JSpinner();
		spnPacketLossDSL.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		spnPacketLossDSL.setBounds(90, 19, 63, 20);
		pnlDSL.add(spnPacketLossDSL);
		
		JLabel lblPercent = new JLabel("%");
		lblPercent.setBounds(163, 22, 46, 14);
		pnlDSL.add(lblPercent);
		
		txtJitterDSL = new JTextField();
		txtJitterDSL.setBounds(90, 44, 86, 20);
		pnlDSL.add(txtJitterDSL);
		txtJitterDSL.setColumns(10);
		
		JLabel lblMs = new JLabel("ms");
		lblMs.setBounds(186, 47, 46, 14);
		pnlDSL.add(lblMs);
		
		txtLatencyDSL = new JTextField();
		txtLatencyDSL.setColumns(10);
		txtLatencyDSL.setBounds(90, 69, 86, 20);
		pnlDSL.add(txtLatencyDSL);
		
		JLabel label = new JLabel("ms");
		label.setBounds(186, 72, 46, 14);
		pnlDSL.add(label);
		
		txtThroughputDSL = new JTextField();
		txtThroughputDSL.setColumns(10);
		txtThroughputDSL.setBounds(90, 96, 86, 20);
		pnlDSL.add(txtThroughputDSL);
		
		cmbTypeDSL = new JComboBox();
		cmbTypeDSL.setModel(new DefaultComboBoxModel(new String[] {"Kbps", "Mbps", "Gbps", "Tbps"}));
		cmbTypeDSL.setBounds(186, 96, 63, 20);
		pnlDSL.add(cmbTypeDSL);
		
		JPanel pnlVoIP = new JPanel();
		pnlVoIP.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "VoIP QoS Benchmarks", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlVoIP.setBounds(10, 182, 303, 125);
		contentPanel.add(pnlVoIP);
		pnlVoIP.setLayout(null);
		
		cmbTypeVOIP = new JComboBox();
		cmbTypeVOIP.setModel(new DefaultComboBoxModel(new String[] {"Kbps", "Mbps", "Gbps", "Tbps"}));
		cmbTypeVOIP.setBounds(188, 94, 63, 20);
		pnlVoIP.add(cmbTypeVOIP);
		
		JLabel label_1 = new JLabel("Packet Loss:");
		label_1.setBounds(10, 20, 89, 14);
		pnlVoIP.add(label_1);
		
		spnPacketLossVOIP = new JSpinner();
		spnPacketLossVOIP.setBounds(92, 17, 63, 20);
		pnlVoIP.add(spnPacketLossVOIP);
		
		JLabel label_2 = new JLabel("%");
		label_2.setBounds(165, 20, 46, 14);
		pnlVoIP.add(label_2);
		
		JLabel label_3 = new JLabel("ms");
		label_3.setBounds(188, 45, 46, 14);
		pnlVoIP.add(label_3);
		
		JLabel label_4 = new JLabel("ms");
		label_4.setBounds(188, 70, 46, 14);
		pnlVoIP.add(label_4);
		
		txtJitterVOIP = new JTextField();
		txtJitterVOIP.setColumns(10);
		txtJitterVOIP.setBounds(92, 42, 86, 20);
		pnlVoIP.add(txtJitterVOIP);
		
		txtLatencyVOIP = new JTextField();
		txtLatencyVOIP.setColumns(10);
		txtLatencyVOIP.setBounds(92, 67, 86, 20);
		pnlVoIP.add(txtLatencyVOIP);
		
		txtThroughputVOIP = new JTextField();
		txtThroughputVOIP.setColumns(10);
		txtThroughputVOIP.setBounds(92, 94, 86, 20);
		pnlVoIP.add(txtThroughputVOIP);
		
		JLabel label_5 = new JLabel("Throughput:");
		label_5.setBounds(10, 97, 89, 14);
		pnlVoIP.add(label_5);
		
		JLabel label_6 = new JLabel("Latency:");
		label_6.setBounds(10, 70, 89, 14);
		pnlVoIP.add(label_6);
		
		JLabel label_7 = new JLabel("Jitter:");
		label_7.setBounds(10, 45, 89, 14);
		pnlVoIP.add(label_7);
		
		JPanel pnlOther = new JPanel();
		pnlOther.setLayout(null);
		pnlOther.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Other Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlOther.setBounds(10, 315, 303, 52);
		contentPanel.add(pnlOther);
		
		JLabel lblErrorRate = new JLabel("Error Rate:");
		lblErrorRate.setBounds(10, 24, 90, 14);
		pnlOther.add(lblErrorRate);
		
		spnFailure = new JSpinner();
		spnFailure.setBounds(93, 21, 63, 20);
		pnlOther.add(spnFailure);
		
		JLabel label_8 = new JLabel("%");
		label_8.setBounds(166, 24, 21, 14);
		pnlOther.add(label_8);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//We first check for errors.
						boolean error = false;
						try {
							Integer.parseInt(txtJitterDSL.getText());
						} catch (NumberFormatException e) {
							error = true;
							txtJitterDSL.setForeground(Color.RED);
						}
						try {
							Integer.parseInt(txtLatencyDSL.getText());
						} catch (NumberFormatException e) {
							error = true;
							txtLatencyDSL.setForeground(Color.RED);
						}
						try {
							Integer.parseInt(txtThroughputDSL.getText());
						} catch (NumberFormatException e) {
							error = true;
							txtThroughputDSL.setForeground(Color.RED);
						}
						try {
							Integer.parseInt(txtJitterVOIP.getText());
						} catch (NumberFormatException e) {
							error = true;
							txtJitterVOIP.setForeground(Color.RED);
						}
						try {
							Integer.parseInt(txtLatencyVOIP.getText());
						} catch (NumberFormatException e) {
							error = true;
							txtLatencyVOIP.setForeground(Color.RED);
						}
						try {
							Integer.parseInt(txtThroughputVOIP.getText());
						} catch (NumberFormatException e) {
							error = true;
							txtThroughputVOIP.setForeground(Color.RED);
						}
						
						//Sees if there is an error.
						if (error){
							JOptionPane.showMessageDialog(null, "Please fix the marked error(s).\n" +
									"Valid numbers required!", 
									"Proactive QoS Monitoring Tool", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						accepted = true;
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public boolean isAccepted(){
		return accepted;
	}

	public int[] getDSL() {
		//Create an array for parameters.
		int[] parameters = new int[5];
		parameters[0] = (Integer) spnPacketLossDSL.getValue();
		parameters[1] = Integer.parseInt(txtJitterDSL.getText());
		parameters[2] = Integer.parseInt(txtLatencyDSL.getText());
		parameters[3] = Integer.parseInt(txtThroughputDSL.getText());
		
		switch ((String) cmbTypeDSL.getSelectedItem()){
			case "Kbps":
				parameters[4] = Link.BAND_TYPE.KBPS.getInternal();
				break;
			case "Tbps":
				parameters[4] = Link.BAND_TYPE.TBPS.getInternal();
				break;
			case "Gbps":
				parameters[4] = Link.BAND_TYPE.GPBS.getInternal();
				break;
			case "Mbps":
			default:
				parameters[4] = Link.BAND_TYPE.MBPS.getInternal();
		}
		
		return parameters;
	}

	public int[] getVoIP() {
		//Create an array for parameters.
		int[] parameters = new int[5];
		parameters[0] = (Integer) spnPacketLossVOIP.getValue();
		parameters[1] = Integer.parseInt(txtJitterVOIP.getText());
		parameters[2] = Integer.parseInt(txtLatencyVOIP.getText());
		parameters[3] = Integer.parseInt(txtThroughputVOIP.getText());
		
		switch ((String) cmbTypeVOIP.getSelectedItem()){
			case "Kbps":
				parameters[4] = Link.BAND_TYPE.KBPS.getInternal();
				break;
			case "Tbps":
				parameters[4] = Link.BAND_TYPE.TBPS.getInternal();
				break;
			case "Gbps":
				parameters[4] = Link.BAND_TYPE.GPBS.getInternal();
				break;
			case "Mbps":
			default:
				parameters[4] = Link.BAND_TYPE.MBPS.getInternal();
		}
		
		return parameters;
	}

	public int getFailure() {
		//We return the value of the failure spinner.
		return (Integer) spnFailure.getValue();
	}
}
