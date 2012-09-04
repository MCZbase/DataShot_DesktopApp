/**
 * ChangePasswordDialog.java
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

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import java.awt.Dimension;

/** ChangePasswordDialog
 * 
 * @author Paul J. Morris
 *
 */
public class ChangePasswordDialog extends JDialog {
	
	private static final Log log = LogFactory.getLog(ChangePasswordDialog.class);

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JLabel jLabel = null;
	private JTextField jTextField = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JPasswordField jPasswordFieldOld = null;
	private JPasswordField jPasswordField1 = null;
	private JPasswordField jPasswordField2 = null;
	private JLabel jLabelResponse = null;
	private JPanel jPanel = null;
	private Users user = null;
	
	/** Must construct with a Users object.
	 * 
	 */
	@SuppressWarnings("unused")
	private ChangePasswordDialog() {
	}

	/** Must construct with a Users object.
	 * 
	 */
	@SuppressWarnings("unused")
	private ChangePasswordDialog(Frame owner) {
	}
	
	/** Construct a new change password dialog for a user.
	 * @param owner
	 */
	public ChangePasswordDialog(Frame owner, Users aUser) {
		super(owner);
		user = aUser;
		initialize();
		setValues();
		setForDisplay();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(414, 144);
		this.setTitle("Change Password");
		this.setContentPane(getJContentPane());
	}
	
	private void setValues() { 
		jTextField.setText(user.getFullname());
	}
	
	private void setForDisplay() { 
		this.pack();
		Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation((screenSize.width -   this.getWidth()) / 2 , 
	                     (screenSize.height - this.getHeight()) / 2 );
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.weightx = 0.0;
			gridBagConstraints10.weighty = 1.0;
			gridBagConstraints10.gridy = 6;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.gridwidth = 2;
			gridBagConstraints9.gridy = 4;
			jLabelResponse = new JLabel();
			jLabelResponse.setText("");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 3;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 2;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 1;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 5;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.CENTER;
			gridBagConstraints4.gridy = 5;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.EAST;
			gridBagConstraints3.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("New Password Again");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("New Password");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Old Password");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Change Password For:");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(jLabel, gridBagConstraints);
			jContentPane.add(getJTextField(), gridBagConstraints1);
			jContentPane.add(jLabel1, gridBagConstraints11);
			jContentPane.add(jLabel2, gridBagConstraints2);
			jContentPane.add(jLabel3, gridBagConstraints3);
			jContentPane.add(getJButton(), gridBagConstraints4);
			jContentPane.add(getJButton1(), gridBagConstraints5);
			jContentPane.add(getJPasswordFieldOld(), gridBagConstraints6);
			jContentPane.add(getJPasswordField1(), gridBagConstraints7);
			jContentPane.add(getJPasswordField2(), gridBagConstraints8);
			jContentPane.add(jLabelResponse, gridBagConstraints9);
			jContentPane.add(getJPanel(), gridBagConstraints10);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setEditable(false);
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Save");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean ok = false;
					UsersLifeCycle uls = new UsersLifeCycle() ;
					if (user!=null) { 
						String hash1 = LoginDialog.hashPassword(jPasswordField1);
						String hash2 = LoginDialog.hashPassword(jPasswordField2);
						// Check that new passwords are the same
						if (hash1.equals(hash2)) { 
							if (Users.testProposedPassword(String.valueOf(jPasswordField1.getPassword()), user.getUsername())) { 
								// check that old password is correct
								if (LoginDialog.hashPassword(jPasswordFieldOld).equals(user.getHash())) { 
									try {
										user.setHash(LoginDialog.hashPassword(jPasswordField1)) ;
										uls.attachDirty(user);
										ok = true;
									} catch (SaveFailedException ex) { 
										log.error(e);
										jLabelResponse.setText("DB Error: Unable to save change.");
									}
								} else { 
									jLabelResponse.setText("The old password isn't correct.");	
								}
							} else {
								jLabelResponse.setText("New password isn't complex enough." + Users.PASSWORD_RULES_MESSAGE);	
							}
						} else {
							jLabelResponse.setText("New Passwords Don't Match.");
						}
					}
					if (ok) { 
					   String aMessage = "Changed password for " + user.getFullname();
					   Singleton.getSingletonInstance().getMainFrame().setStatusMessage(aMessage);
                       setVisible(false);
					}
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Cancel");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 String aMessage = "Password change canceled.";
					   Singleton.getSingletonInstance().getMainFrame().setStatusMessage(aMessage);
					setVisible(false);
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jPasswordFieldOld	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordFieldOld() {
		if (jPasswordFieldOld == null) {
			jPasswordFieldOld = new JPasswordField();
		}
		return jPasswordFieldOld;
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
	 * This method initializes jPasswordField2	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField2() {
		if (jPasswordField2 == null) {
			jPasswordField2 = new JPasswordField();
		}
		return jPasswordField2;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
