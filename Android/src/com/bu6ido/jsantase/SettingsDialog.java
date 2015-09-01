/**
 * 
 */
package com.bu6ido.jsantase;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.bu6ido.jsantase.common.SantaseSettings;
import com.bu6ido.jsantase.common.SantaseUtils;

/**
 * @author bu6ido
 *
 */
public class SettingsDialog extends JDialog 
{
	private static final long serialVersionUID = 1L;

	private JTextField txtNamePlayer1;
	private JTextField txtNamePlayer2;
	private JCheckBox chkRaising;
	private JButton btnSave;
	
	public SettingsDialog(JFrame frame)
	{
		super(frame, "Game settings:");
		
		Container cp = getContentPane();
		cp.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weightx = 0.5;
		gbc.weighty = 0.33;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		JLabel lblNamePlayer1 = new JLabel("Name of Player 1:");
		gbc.gridx = 0;
		gbc.gridy = 0;
		cp.add(lblNamePlayer1, gbc);
		
		txtNamePlayer1 = new JTextField(15);
		txtNamePlayer1.setMinimumSize(txtNamePlayer1.getPreferredSize());
		gbc.gridx = 1;
		gbc.gridy = 0;
		cp.add(txtNamePlayer1, gbc);
		
		JLabel lblNamePlayer2 = new JLabel("Name of Player 2:");
		gbc.gridx = 0;
		gbc.gridy = 1;
		cp.add(lblNamePlayer2, gbc);
		
		txtNamePlayer2 = new JTextField(15);
		txtNamePlayer2.setMinimumSize(txtNamePlayer2.getPreferredSize());
		gbc.gridx = 1;
		gbc.gridy = 1;
		cp.add(txtNamePlayer2, gbc);
		
		chkRaising = new JCheckBox("<html>Raising with a card of the same suit after<br>the game is closed or the deck is empty</html>");
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 2;
		cp.add(chkRaising, gbc);
		
		btnSave = new JButton("Save");
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 3;
		cp.add(btnSave, gbc);

		pack();
		setLocationRelativeTo(frame);
		
		SantaseSettings settings = SantaseUtils.loadSettings();
		setSettings(settings);
		
		addListeners();
	}
	
	public void setSettings(SantaseSettings settings)
	{
		if (settings == null)
		{
			return;
		}
		txtNamePlayer1.setText(settings.getNamePlayer1());
		txtNamePlayer2.setText(settings.getNamePlayer2());
		chkRaising.setSelected(settings.getRaising());
	}
	
	public SantaseSettings getSettings()
	{
		SantaseSettings result = new SantaseSettings();
		
		result.setNamePlayer1(txtNamePlayer1.getText());
		result.setNamePlayer2(txtNamePlayer2.getText());
		result.setRaising(chkRaising.isSelected());
		
		return result;
	}
	
	protected void addListeners()
	{
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent ev) 
			{
				SantaseSettings settings = SettingsDialog.this.getSettings();
				SantaseUtils.saveSettings(settings);
				JOptionPane.showMessageDialog(SettingsDialog.this, "Settings have been saved !!!\nPlease restart the game for changes to take effect !!!");
				SettingsDialog.this.setVisible(false);
			}
		});
	}
}
