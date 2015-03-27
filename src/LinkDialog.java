import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class LinkDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private boolean accepted;
	private JComboBox comboBox;
	
	/**
	 * Create the dialog.
	 */
	public LinkDialog() {
		accepted = false;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("Proactive QoS Monitoring Tool");
		setBounds(100, 100, 339, 110);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblLinkBandwidth = new JLabel("Link Bandwidth:");
			lblLinkBandwidth.setBounds(10, 11, 100, 19);
			lblLinkBandwidth.setFont(new Font("Tahoma", Font.PLAIN, 15));
			contentPanel.add(lblLinkBandwidth);
		}
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textField.setBounds(120, 11, 93, 21);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		comboBox = new JComboBox();
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Kbps", "Mbps", "Gbps", "Tbps"}));
		comboBox.setBounds(223, 12, 90, 20);
		contentPanel.add(comboBox);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//Checks for number.
						try{
							Integer.parseInt(textField.getText());
						} catch (NumberFormatException e){
							JOptionPane.showMessageDialog(null, "Please enter a valid number.", 
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
	public int getBandwidth(){
		int bandwidth = Integer.parseInt(textField.getText());
		
		return bandwidth;
	}
	public Link.BAND_TYPE getBandType(){
		Link.BAND_TYPE temp;
		String selectedType = (String) comboBox.getSelectedItem();
		
		switch (selectedType){
			case "Kbps":
				temp = Link.BAND_TYPE.KBPS;
				break;
			case "Tbps":
				temp = Link.BAND_TYPE.TBPS;
				break;
			case "Gbps":
				temp = Link.BAND_TYPE.GPBS;
				break;
			case "Mbps":
			default:
				temp = Link.BAND_TYPE.MBPS;
		}
		
		return temp;
	}
}
