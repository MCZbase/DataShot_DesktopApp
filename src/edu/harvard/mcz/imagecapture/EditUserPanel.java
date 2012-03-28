/**
 * EditUserPanel.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2009 President and Fellows of Harvard College
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture;

import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.utility.HashUtility;

/** EditUserPanel
 * 
 * @author Paul J. Morris
 *
 */
public class EditUserPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private Users user = null;  //  @jve:decl-index=0:
	private JLabel jLabel = null;
	private JTextField jTextFieldUsername = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JPasswordField jPasswordField = null;
	private JPasswordField jPasswordField1 = null;
	private JTextField jTextFieldFullName = null;
	private JTextField jTextFieldAbout = null;
	private JLabel jLabel5 = null;
	private JComboBox jComboBox = null;
	private JButton jButton = null;

	private JButton jButtonSetPassword = null;

	/**
	 * This is the default constructor
	 */
	public EditUserPanel() {
		super();
		initialize();
		setEditingState(false);
	}
	
	private void setEditingState(boolean enable) { 
		jTextFieldUsername.setEnabled(enable);
		jTextFieldFullName.setEnabled(enable);
		jTextFieldAbout.setEnabled(enable);
		jComboBox.setEnabled(enable);
		jComboBox.setEditable(false);
		jPasswordField.setEnabled(enable);
		jPasswordField1.setEnabled(enable);
		jButton.setEnabled(enable);
	}
	
	public void setUser(Users aUser) {
		setEditingState(true);
		if (aUser!=null) { 
			user = aUser;
			jTextFieldUsername.setText(user.getUsername());
			jTextFieldFullName.setText(user.getFullname());
			jTextFieldAbout.setText(user.getDescription());
			jComboBox.setSelectedItem(user.getRole());
		}
		if (user.getUserid()==null) { 
			jPasswordField.setVisible(true);
			jPasswordField1.setVisible(true);
			jPasswordField.setEnabled(true);
			jPasswordField1.setEnabled(true);	
			jButtonSetPassword.setEnabled(false);
			jButtonSetPassword.setVisible(false);
			jLabel2.setVisible(true);
		} else { 
			jPasswordField.setEnabled(false);
			jPasswordField1.setEnabled(false);
			jPasswordField.setVisible(false);
			jPasswordField1.setVisible(false);
			jButtonSetPassword.setEnabled(true);
			jButtonSetPassword.setVisible(true);
			jLabel2.setVisible(false);
		}
	}

	private void save() { 
		boolean okToSave = true;
		String message = "";
		// Check required values
		if (jPasswordField.isEnabled()) {
			// Passwords must match
			if (LoginDialog.hashPassword(jPasswordField).equals(LoginDialog.hashPassword(jPasswordField1))) { 
				user.setHash(LoginDialog.hashPassword(jPasswordField));
			} else { 
				okToSave = false;
				message = message + "Password and Password Again don't match.\n";
			}
			// Check for sufficiently complex password for new users.
			String pw = String.valueOf(jPasswordField.getPassword());
			if (Users.testProposedPassword(pw, user.getUsername())) { 
				user.setHash( HashUtility.getSHA1Hash(pw));
			} else { 
				okToSave = false;
				message = message + "Password is not sufficiently complex.  " +  Users.PASSWORD_RULES_MESSAGE +  " \n";
			}
		}
		// Don't check here yet for sufficiently complex password for 
		// existing users, as that would force a password change by the 
		// admin when changing other aspects of a user.
		// This will become desirable, but not yet.  
		
		// Are required fields populated?
		if (jTextFieldUsername.getText().equals("")) { 
			okToSave = false;
			message = message + "An email is required.\n";
		}
		if (jTextFieldFullName.getText().equals("")) { 
			okToSave = false;
			message = message + "A full name is required.\n";
		}		

		if (!okToSave) {
			// warn
			message = "Error. Can't Save.\n" + message;
			JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), message, "Error. Can't save.",  JOptionPane.OK_OPTION);	
		} else {
			// Save
			UsersLifeCycle uls = new UsersLifeCycle();
			user.setUsername(jTextFieldUsername.getText());
			user.setFullname(jTextFieldFullName.getText());
			user.setDescription(jTextFieldAbout.getText());
			if (jComboBox.getSelectedIndex()==-1 && jComboBox.getSelectedItem()==null) { 
				user.setRole(Users.ROLE_DATAENTRY);
			} else { 
				user.setRole(jComboBox.getSelectedItem().toString());
			}
			try {
				if (user.getUserid()==null) { 
					uls.persist(user);
				} else { 
					uls.attachDirty(user);
				}
				message = "Saved " + user.getFullname();
				Singleton.getSingletonInstance().getMainFrame().setStatusMessage(message);
			} catch (SaveFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		gridBagConstraints13.gridx = 1;
		gridBagConstraints13.gridy = 3;
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		gridBagConstraints12.gridx = 0;
		gridBagConstraints12.gridwidth = 2;
		gridBagConstraints12.weightx = 0.0;
		gridBagConstraints12.anchor = GridBagConstraints.NORTH;
		gridBagConstraints12.weighty = 1.0;
		gridBagConstraints12.gridy = 7;
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		gridBagConstraints11.fill = GridBagConstraints.BOTH;
		gridBagConstraints11.gridy = 6;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.gridx = 1;
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.anchor = GridBagConstraints.EAST;
		gridBagConstraints10.gridy = 6;
		jLabel5 = new JLabel();
		jLabel5.setText("Role");
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		gridBagConstraints9.fill = GridBagConstraints.BOTH;
		gridBagConstraints9.gridy = 5;
		gridBagConstraints9.weightx = 1.0;
		gridBagConstraints9.gridx = 1;
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.fill = GridBagConstraints.BOTH;
		gridBagConstraints8.gridy = 4;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.gridx = 1;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.fill = GridBagConstraints.BOTH;
		gridBagConstraints7.gridy = 2;
		gridBagConstraints7.weightx = 1.0;
		gridBagConstraints7.gridx = 1;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.fill = GridBagConstraints.BOTH;
		gridBagConstraints6.gridy = 1;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.gridx = 1;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.anchor = GridBagConstraints.EAST;
		gridBagConstraints5.gridy = 5;
		jLabel4 = new JLabel();
		jLabel4.setText("About");
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.anchor = GridBagConstraints.EAST;
		gridBagConstraints4.gridy = 4;
		jLabel3 = new JLabel();
		jLabel3.setText("Full Name");
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.anchor = GridBagConstraints.EAST;
		gridBagConstraints3.gridy = 2;
		jLabel2 = new JLabel();
		jLabel2.setText("Password Again");
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.EAST;
		gridBagConstraints2.gridy = 1;
		jLabel1 = new JLabel();
		jLabel1.setText("Password");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 0;
		jLabel = new JLabel();
		jLabel.setText("email");
		this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
		this.add(jLabel, gridBagConstraints);
		this.add(getJTextFieldUsername(), gridBagConstraints1);
		this.add(jLabel1, gridBagConstraints2);
		this.add(jLabel2, gridBagConstraints3);
		this.add(jLabel3, gridBagConstraints4);
		this.add(jLabel4, gridBagConstraints5);
		this.add(getJPasswordField(), gridBagConstraints6);
		this.add(getJPasswordField1(), gridBagConstraints7);
		this.add(getJTextFieldFullName(), gridBagConstraints8);
		this.add(getJTextFieldAbout(), gridBagConstraints9);
		this.add(jLabel5, gridBagConstraints10);
		this.add(getJComboBox(), gridBagConstraints11);
		this.add(getJButton(), gridBagConstraints12);
		this.add(getJButton1(), gridBagConstraints13);
	}

	/**
	 * This method initializes jTextFieldUsername	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldUsername() {
		if (jTextFieldUsername == null) {
			jTextFieldUsername = new JTextField();
		}
		return jTextFieldUsername;
	}

	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jPasswordField1	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField1() {
		if (jPasswordField1 == null) {
			jPasswordField1 = new JPasswordField();
		}
		return jPasswordField1;
	}

	/**
	 * This method initializes jTextFieldFullName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldFullName() {
		if (jTextFieldFullName == null) {
			jTextFieldFullName = new JTextField();
		}
		return jTextFieldFullName;
	}

	/**
	 * This method initializes jTextFieldAbout	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldAbout() {
		if (jTextFieldAbout == null) {
			jTextFieldAbout = new JTextField();
		}
		return jTextFieldAbout;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox(Users.ROLES);
		}
		return jComboBox;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setEnabled(false);
			jButton.setText("Save");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					save();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButtonSetPassword	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButtonSetPassword == null) {
			jButtonSetPassword = new JButton();
			jButtonSetPassword.setEnabled(false);
			jButtonSetPassword.setText("Set New Password");
			jButtonSetPassword.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					String suggestion = user.getUsername() + user.getFullname() + Math.random();
					suggestion =  HashUtility.getSHA1Hash(suggestion);
					suggestion.substring(1, 15);
					String pw = (String)JOptionPane.showInputDialog((Component)Singleton.getSingletonInstance().getMainFrame(),
							"Set a new password for " + user.getFullname() + " (at least 10 characters)", 
							"Change password for " + user.getUsername(), 
							JOptionPane.QUESTION_MESSAGE, 
							null, 
							null, 
							"");
					if (pw!=null && !pw.isEmpty()) { 
					    // user provided some input.
						if (Users.testProposedPassword(pw, user.getUsername())) { 
							user.setHash( HashUtility.getSHA1Hash(pw));
						} else { 
							JOptionPane.showMessageDialog((Component)Singleton.getSingletonInstance().getMainFrame(),
									"Password is not complex enough" + Users.PASSWORD_RULES_MESSAGE, "Password Not Changed.",
									JOptionPane.ERROR_MESSAGE);
						}
						
					}
				}
			});
		}
		return jButtonSetPassword;
	}

}
