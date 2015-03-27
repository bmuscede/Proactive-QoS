import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dialog.ModalExclusionType;


public class SimulationDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtJitterDSL;
	private JTextField txtLatencyDSL;
	private JTextField txtThroughputDSL;
	private JTextField txtJitterVOIP;
	private JTextField txtLatencyVOIP;
	private JTextField txtThroughputVOIP;

	/**
	 * Create the dialog.
	 */
	public SimulationDialog() {
		setResizable(false);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
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
		
		JSpinner spnPacketLossDSL = new JSpinner();
		spnPacketLossDSL.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		spnPacketLossDSL.setBounds(82, 20, 63, 20);
		pnlDSL.add(spnPacketLossDSL);
		
		JLabel lblPercent = new JLabel("%");
		lblPercent.setBounds(155, 23, 46, 14);
		pnlDSL.add(lblPercent);
		
		txtJitterDSL = new JTextField();
		txtJitterDSL.setBounds(82, 45, 86, 20);
		pnlDSL.add(txtJitterDSL);
		txtJitterDSL.setColumns(10);
		
		JLabel lblMs = new JLabel("ms");
		lblMs.setBounds(178, 48, 46, 14);
		pnlDSL.add(lblMs);
		
		txtLatencyDSL = new JTextField();
		txtLatencyDSL.setColumns(10);
		txtLatencyDSL.setBounds(82, 70, 86, 20);
		pnlDSL.add(txtLatencyDSL);
		
		JLabel label = new JLabel("ms");
		label.setBounds(178, 73, 46, 14);
		pnlDSL.add(label);
		
		txtThroughputDSL = new JTextField();
		txtThroughputDSL.setColumns(10);
		txtThroughputDSL.setBounds(82, 97, 86, 20);
		pnlDSL.add(txtThroughputDSL);
		
		JComboBox cmbTypeDSL = new JComboBox();
		cmbTypeDSL.setModel(new DefaultComboBoxModel(new String[] {"Kbps", "Mbps", "Gbps", "Tbps"}));
		cmbTypeDSL.setBounds(178, 97, 63, 20);
		pnlDSL.add(cmbTypeDSL);
		
		JPanel pnlVoIP = new JPanel();
		pnlVoIP.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "VoIP QoS Benchmarks", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlVoIP.setBounds(10, 182, 303, 125);
		contentPanel.add(pnlVoIP);
		pnlVoIP.setLayout(null);
		
		JComboBox cmbTypeVOIP = new JComboBox();
		cmbTypeVOIP.setModel(new DefaultComboBoxModel(new String[] {"Kbps", "Mbps", "Gbps", "Tbps"}));
		cmbTypeVOIP.setBounds(178, 94, 63, 20);
		pnlVoIP.add(cmbTypeVOIP);
		
		JLabel label_1 = new JLabel("Packet Loss:");
		label_1.setBounds(10, 20, 89, 14);
		pnlVoIP.add(label_1);
		
		JSpinner spnPacketLossVOIP = new JSpinner();
		spnPacketLossVOIP.setBounds(82, 17, 63, 20);
		pnlVoIP.add(spnPacketLossVOIP);
		
		JLabel label_2 = new JLabel("%");
		label_2.setBounds(155, 20, 46, 14);
		pnlVoIP.add(label_2);
		
		JLabel label_3 = new JLabel("ms");
		label_3.setBounds(178, 45, 46, 14);
		pnlVoIP.add(label_3);
		
		JLabel label_4 = new JLabel("ms");
		label_4.setBounds(178, 70, 46, 14);
		pnlVoIP.add(label_4);
		
		txtJitterVOIP = new JTextField();
		txtJitterVOIP.setColumns(10);
		txtJitterVOIP.setBounds(82, 42, 86, 20);
		pnlVoIP.add(txtJitterVOIP);
		
		txtLatencyVOIP = new JTextField();
		txtLatencyVOIP.setColumns(10);
		txtLatencyVOIP.setBounds(82, 67, 86, 20);
		pnlVoIP.add(txtLatencyVOIP);
		
		txtThroughputVOIP = new JTextField();
		txtThroughputVOIP.setColumns(10);
		txtThroughputVOIP.setBounds(82, 94, 86, 20);
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
		
		JSpinner spinner = new JSpinner();
		spinner.setBounds(82, 21, 63, 20);
		pnlOther.add(spinner);
		
		JLabel label_8 = new JLabel("%");
		label_8.setBounds(155, 24, 21, 14);
		pnlOther.add(label_8);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
