/**
 * UserDialog.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import java.awt.Insets;

/** UserDialog is a user interface for editing metadata about
 * participants in the project.  
 * 
 * @see edu.harvard.mcz.imagecapture.data.Users
 * 
 * @author Paul J. Morris
 *
 */
public class UserDialog extends JDialog {

	private static final long serialVersionUID = -8881672324009775369L;
	
	private UserDialog thisDialog = null; // allow reference to this in button event methods
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton jButtonSave = null;
	private JButton jButtonCancel = null;
	private JPanel jPanel2 = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldUsername = null;
	private JLabel jLabel1 = null;
	private JTextField jTextFieldFullname = null;
	private JTextField jTextFieldDescription = null;
	private JLabel jLabel2 = null;
	private JLabel jLabelMessage = null;
	private Users userToEdit = null;
	private boolean wasCancled = true;

	/**
	 * Default constructor.  Dialog is built as modal off of MainFrame.
	 * 
	 */
	public UserDialog() {
		super(Singleton.getSingletonInstance().getMainFrame(), true);  // construct as modal dialog
		thisDialog = this;
		userToEdit = new Users();
		userToEdit.setRole("undefined");
		initialize();
		setValues();
	}
	
	/**
	 * Constructor specifying a user to edit.  Dialog is built as modal off of MainFrame.
	 * 
	 */
	public UserDialog(Users aUser) {
		super(Singleton.getSingletonInstance().getMainFrame(), true); // construct as modal dialog
		thisDialog = this;
		userToEdit = aUser;
		initialize();
		setValues();
		jTextFieldUsername.setEditable(false);
	}	
	
	/**
	 * @return the user edited in this dialog.
	 */
	public Users getUser() { 
		return userToEdit;
	}

	/**
	 * 
	 * @return false if the user was saved, true otherwise.
	 */
	public boolean getWasCancled() { 
		return wasCancled;
	}
	
	private void setValues() {
		if (userToEdit!=null) {
			jTextFieldUsername.setText(userToEdit.getUsername());
			jTextFieldFullname.setText(userToEdit.getFullname());
			jTextFieldDescription.setText(userToEdit.getDescription());
		}
	}
	
	/**
	 * This method initializes this, setting up the layout of the dialog.
	 * Built with VisualEditor in Eclipse.
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(504, 188));
        this.setPreferredSize(new Dimension(504, 188));
        this.setTitle("Details about a person");
        this.setContentPane(getJPanel());
        Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation((screenSize.width -   this.getWidth()) / 2 , 
                (screenSize.height - this.getHeight()) / 2 );	
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJPanel1(), BorderLayout.SOUTH);
			jPanel.add(getJPanel2(), BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJButtonSave(), gridBagConstraints1);
			jPanel1.add(getJButtonCancel(), gridBagConstraints);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButtonSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setText("Save");
			jButtonSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					userToEdit.setFullname(jTextFieldFullname.getText());
					userToEdit.setUsername(jTextFieldUsername.getText());
					userToEdit.setDescription(jTextFieldDescription.getText());
					userToEdit.setRole("undefined");
					UsersLifeCycle u = new UsersLifeCycle();
					// find out if a matching record exists, if it does, update it, if it doesn't add one.
					Users check = new Users();
					check.setUsername(userToEdit.getUsername());
					try { 
						if (u.findByExample(check).isEmpty()) { 
							u.persist(userToEdit);
						} else { 
							u.attachDirty(userToEdit);
						} 
						wasCancled = false;
						thisDialog.setVisible(false);
					} catch (SaveFailedException ex) { 
						setMessage("Unable to save this record. Name or About may be too long." + ex.getMessage());
				    }
				}
			});
		}
		return jButtonSave;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thisDialog.setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.NORTH;
			gridBagConstraints8.gridwidth = 2;
			gridBagConstraints8.weightx = 0.0;
			gridBagConstraints8.insets = new Insets(0, 0, 20, 0);
			gridBagConstraints8.fill = GridBagConstraints.NONE;
			gridBagConstraints8.gridy = 0;
			jLabelMessage = new JLabel();
			jLabelMessage.setText("Who is this?");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridy = 3;
			jLabel2 = new JLabel();
			jLabel2.setText("About this person");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 2;
			jLabel1 = new JLabel();
			jLabel1.setText("Full Name");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.insets = new Insets(0, 6, 0, 0);
			gridBagConstraints2.gridy = 1;
			jLabel = new JLabel();
			jLabel.setText("Database Username");
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.add(jLabel, gridBagConstraints2);
			jPanel2.add(getJTextFieldUsername(), gridBagConstraints3);
			jPanel2.add(jLabel1, gridBagConstraints4);
			jPanel2.add(getJTextFieldFullname(), gridBagConstraints5);
			jPanel2.add(getJTextFieldDescription(), gridBagConstraints7);
			jPanel2.add(jLabel2, gridBagConstraints6);
			jPanel2.add(jLabelMessage, gridBagConstraints8);
		}
		return jPanel2;
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
	 * This method initializes jTextFieldFullname	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldFullname() {
		if (jTextFieldFullname == null) {
			jTextFieldFullname = new JTextField();
		}
		return jTextFieldFullname;
	}

	/**
	 * This method initializes jTextFieldDescription	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDescription() {
		if (jTextFieldDescription == null) {
			jTextFieldDescription = new JTextField();
		}
		return jTextFieldDescription;
	}
	
	/**
	 * Set the message to appear in the dialog above the data entry fields.
	 * 
	 * @param text the text of the message.
	 */
	public void setMessage(String text) { 
		jLabelMessage.setText(text);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
