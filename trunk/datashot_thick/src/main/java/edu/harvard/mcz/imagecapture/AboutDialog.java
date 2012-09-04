/**
 * AboutDialog.java
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

import javax.swing.JDialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

/** AboutDialog, a dialog describing the application, its version, contributors, and libraries.
 * 
 * Note: Revision, as shown here is one revision behind the commit to CVS of the jar file.
 * 
 * @author Paul J. Morris
 *
 */
public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 5585851631769499899L;
	private JPanel jContentPane = null;
	private JPanel jPanel1 = null;
	private JButton jButton = null;
	private AboutDialog thisDialog = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldApp = null;
	private JLabel jLabel1 = null;
	private JTextField jTextFieldVersion = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JTextField jTextField = null;
	private JTextArea jTextArea = null;
	private JTextField jTextFieldCopyright = null;
	private JTextField jTextField1 = null;
	private JTextField jTextField2 = null;
	private JTextArea textArea;
	private JLabel lblLicence;

	/**
	 * This method initializes 
	 * 
	 */
	public AboutDialog() {
		super();
		thisDialog = this;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(694, 530));
        this.setTitle("About" + ImageCaptureApp.APP_NAME);
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
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel1(), BorderLayout.SOUTH);
			jContentPane.add(getJPanel2(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJButton(), new GridBagConstraints());
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Close");
			jButton.setMnemonic(KeyEvent.VK_C);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thisDialog.setVisible(false);
					thisDialog.dispose();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints10.gridy = 7;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints9.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints9.gridy = 6;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints8.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints8.gridy = 5;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints5.anchor = GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 7;
			jLabel6 = new JLabel();
			jLabel6.setText("Libraries");
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 6;
			jLabel5 = new JLabel();
			jLabel5.setText("Contributors");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints31.anchor = GridBagConstraints.EAST;
			gridBagConstraints31.gridy = 5;
			jLabel4 = new JLabel();
			jLabel4.setText("Copyright");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints21.anchor = GridBagConstraints.EAST;
			gridBagConstraints21.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("Description");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints11.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("Revision");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints2.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Version");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.insets = new Insets(0, 10, 5, 5);
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Application");
			jPanel = new JPanel();
			GridBagLayout gbl_jPanel = new GridBagLayout();
			gbl_jPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0};
			gbl_jPanel.columnWeights = new double[]{0.0, 1.0};
			jPanel.setLayout(gbl_jPanel);
			jPanel.add(jLabel, gridBagConstraints);
			jPanel.add(getJTextFieldApp(), gridBagConstraints1);
			jPanel.add(jLabel1, gridBagConstraints2);
			jPanel.add(getJTextFieldVersion(), gridBagConstraints3);
			jPanel.add(jLabel2, gridBagConstraints11);
			jPanel.add(jLabel3, gridBagConstraints21);
			GridBagConstraints gbc_lblLicence = new GridBagConstraints();
			gbc_lblLicence.anchor = GridBagConstraints.EAST;
			gbc_lblLicence.insets = new Insets(0, 0, 5, 5);
			gbc_lblLicence.gridx = 0;
			gbc_lblLicence.gridy = 4;
			jPanel.add(getLblLicence(), gbc_lblLicence);
			GridBagConstraints gbc_textArea = new GridBagConstraints();
			gbc_textArea.insets = new Insets(0, 0, 5, 0);
			gbc_textArea.fill = GridBagConstraints.BOTH;
			gbc_textArea.gridx = 1;
			gbc_textArea.gridy = 4;
			jPanel.add(getTextArea(), gbc_textArea);
			jPanel.add(jLabel4, gridBagConstraints31);
			jPanel.add(jLabel5, gridBagConstraints4);
			jPanel.add(jLabel6, gridBagConstraints5);
			jPanel.add(getJTextField(), gridBagConstraints6);
			jPanel.add(getJTextArea(), gridBagConstraints7);
			jPanel.add(getJTextField1(), gridBagConstraints8);
			jPanel.add(getJTextField12(), gridBagConstraints9);
			jPanel.add(getJTextField2(), gridBagConstraints10);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFieldApp	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldApp() {
		if (jTextFieldApp == null) {
			jTextFieldApp = new JTextField(ImageCaptureApp.APP_NAME);
			jTextFieldApp.setEditable(false);
		}
		return jTextFieldApp;
	}

	/**
	 * This method initializes jTextFieldVersion	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldVersion() {
		if (jTextFieldVersion == null) {
			jTextFieldVersion = new JTextField(ImageCaptureApp.APP_VERSION);
			jTextFieldVersion.setEditable(false);
		}
		return jTextFieldVersion;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField(ImageCaptureApp.APP_REV);
			jTextField.setEditable(false);	
		}
		return jTextField;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea(3,50);
			jTextArea.setText(ImageCaptureApp.APP_DESCRIPTION);
			jTextArea.setEditable(false);
		}
		return jTextArea;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (jTextFieldCopyright == null) {
			jTextFieldCopyright = new JTextField(ImageCaptureApp.APP_COPYRIGHT);
			jTextFieldCopyright.setEditable(false);
		}
		return jTextFieldCopyright;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField12() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField(ImageCaptureApp.APP_CONTRIBUTORS);
			jTextField1.setEditable(false);
		}
		return jTextField1;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField(ImageCaptureApp.APP_LIBRARIES);
			jTextField2.setEditable(false);
		}
		return jTextField2;
	}
	

	private JTextArea getTextArea() {
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setText(ImageCaptureApp.APP_LICENSE);
			textArea.setEditable(false);
		}
		return textArea;
	}
	private JLabel getLblLicence() {
		if (lblLicence == null) {
			lblLicence = new JLabel("Licence");
		}
		return lblLicence;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
