/** LoginDialog.java
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
 * @author Paul J. Morris
 */
package edu.harvard.mcz.imagecapture;

import javax.swing.JDialog;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.utility.HashUtility;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.net.URL;

/** A database login dialog, including both username/password credentials and 
 * specification of the database connection parameters.  
 * 
 * @author Paul J. Morris
 */
public class LoginDialog extends JDialog {

	private static final long serialVersionUID = -2016769537635603794L;
	
	private static final Log log = LogFactory.getLog(LoginDialog.class);
	
	public static final int RESULT_CANCEL = 0;
	public static final int RESULT_LOGIN = 1;

    private JDialog self = null;
    private int result = RESULT_LOGIN;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldUsername = null;
	private JLabel jLabel1 = null;
	private JPasswordField jPasswordFieldDB = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldDriver = null;
	private JLabel jLabel3 = null;
	private JTextField jTextFieldConnection = null;
	private JLabel jLabel4 = null;
	private JTextField jTextFieldDialect = null;
	private JButton jButtonLogin = null;
	private JLabel jLabel5 = null;
	private JButton jButtonCancel = null;
	private JLabel jLabel7 = null;
	
	private JPanel jPanel1 = null;
	private JTextField jTextFieldEmail = null;
	private JLabel jLabel8 = null;
	private JLabel jLabel9 = null;
	private JPasswordField jPasswordFieldUser = null;
	private JButton jButton2 = null;
	private JPanel jPanelAdvanced = null;
	private JLabel jLabel6 = null;

	/**
	 * Default constructor.  Produces a login dialog.
	 * 
	 */
	public LoginDialog() {
		super();
		self = this;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setContentPane(getJPanel());
        this.setTitle("DataShot Login Dialog: Configured For: " + 
		     Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION)
        );
        URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/icon.png");          
        try {  
        	setIconImage(new ImageIcon(iconFile).getImage());
        } catch (Exception e) { 
        	log.error(e);
        }        
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LOGIN_SHOW_ADVANCED).equalsIgnoreCase("false")) {
			jPanelAdvanced.setVisible(false);
            this.setSize(new Dimension(698, 190));
		} else { 
			jPanelAdvanced.setVisible(true);
            this.setSize(new Dimension(698, 290));
		}
        this.getRootPane().setDefaultButton(jButtonLogin);
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
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.gridy = 16;
			jLabel6 = new JLabel();
			jLabel6.setText("");
			GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
			gridBagConstraints62.gridx = 0;
			gridBagConstraints62.gridwidth = 2;
			gridBagConstraints62.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints62.anchor = GridBagConstraints.WEST;
			gridBagConstraints62.gridy = 6;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.gridy = 4;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.fill = GridBagConstraints.BOTH;
			gridBagConstraints41.gridy = 3;
			gridBagConstraints41.weightx = 1.0;
			gridBagConstraints41.anchor = GridBagConstraints.WEST;
			gridBagConstraints41.gridx = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.anchor = GridBagConstraints.EAST;
			gridBagConstraints31.gridy = 3;
			jLabel9 = new JLabel();
			jLabel9.setText("Password");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.anchor = GridBagConstraints.EAST;
			gridBagConstraints21.gridy = 2;
			jLabel8 = new JLabel();
			jLabel8.setText("email");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.BOTH;
			gridBagConstraints16.gridy = 2;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 1;
			gridBagConstraints15.gridy = 12;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.gridwidth = 1;
			gridBagConstraints61.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints61.gridheight = 4;
			gridBagConstraints61.fill = GridBagConstraints.NONE;
			gridBagConstraints61.weighty = 1.0;
			gridBagConstraints61.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints61.gridy = 10;
			jLabel7 = new JLabel();
	        URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/key_small.png");
	        try {  
	        	//this.setIconImage(new ImageIcon(iconFile).getImage());
	        	jLabel7.setIcon(new ImageIcon(iconFile));
	        } catch (Exception e) { 
	        	System.out.println("Can't open icon file: " + iconFile);
	        }
			jLabel7.setText(" ");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 1;
			gridBagConstraints12.anchor = GridBagConstraints.NORTH;
			gridBagConstraints12.fill = GridBagConstraints.NONE;
			gridBagConstraints12.gridy = 1;
			jLabel5 = new JLabel();
			jLabel5.setText("Connect to Database");
			jLabel4 = new JLabel();
			jLabel4.setText("Dialect");
			jLabel3 = new JLabel();
			jLabel3.setText("Connection");
			jLabel2 = new JLabel();
			jLabel2.setText("Driver");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			jLabel1 = new JLabel();
			jLabel1.setText("DBPassword");
			jLabel = new JLabel();
			jLabel.setText("Schema");
			jPanel = new JPanel();
			
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(jLabel5, gridBagConstraints12);
			jPanel.add(jLabel7, gridBagConstraints61);
			jPanel.add(getJPanel1(), gridBagConstraints15);
			jPanel.add(getJTextFieldEmail(), gridBagConstraints16);
			jPanel.add(jLabel8, gridBagConstraints21);
			jPanel.add(jLabel9, gridBagConstraints31);
			jPanel.add(getJPasswordFieldUser(), gridBagConstraints41);
			jPanel.add(getJButton2(), gridBagConstraints51);
			jPanel.add(getJPanelAdvanced(), gridBagConstraints62);
			jPanel.add(jLabel6, gridBagConstraints17);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFieldUsername	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldUsername() {
		if (jTextFieldUsername == null) {
			jTextFieldUsername = new JTextField(30);
		}
		return jTextFieldUsername;
	}

	/**
	 * This method initializes jPasswordFieldDB	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordFieldDB() {
		if (jPasswordFieldDB == null) {
			jPasswordFieldDB = new JPasswordField(30);
		}
		return jPasswordFieldDB;
	}

	/**
	 * This method initializes jTextFieldDriver	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDriver() {
		if (jTextFieldDriver == null) {
			jTextFieldDriver = new JTextField("com.mysql.jdbc.Driver");
		}
		return jTextFieldDriver;
	}

	/**
	 * This method initializes jTextFieldConnection	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldConnection() {
		if (jTextFieldConnection == null) {
			jTextFieldConnection = new JTextField("jdbc:mysql://localhost:3306/lepidoptera");
		}
		return jTextFieldConnection;
	}

	/**
	 * This method initializes jTextFieldDialect	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDialect() {
		if (jTextFieldDialect == null) {
			jTextFieldDialect = new JTextField("org.hibernate.dialect.MySQLDialect");
		}
		return jTextFieldDialect;
	}

	/**
	 * This method initializes jButtonLogin	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonLogin() {
		if (jButtonLogin == null) {
			jButtonLogin = new JButton("Login");
			jButtonLogin.setMnemonic(KeyEvent.VK_L);
			jButtonLogin.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					result = LoginDialog.RESULT_LOGIN;
					jButtonLogin.grabFocus();
					getUserPasswordHash();
					self.setVisible(false);
				}
			});
		}
		return jButtonLogin;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton("Cancel");
			jButtonCancel.setMnemonic(KeyEvent.VK_C);
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					result = LoginDialog.RESULT_CANCEL;
					self.setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}
	
	public static String hashPassword(JPasswordField ajPasswordField) { 
		return HashUtility.getSHA1Hash(String.valueOf(ajPasswordField.getPassword()));
	}
	
	/**
	 * Obtain sha1 hash of the text in the user's Password field.  Assumes that the 
	 * text is in utf-8 encoding.  If SHA-1 isn't an available MessageDigest, 
	 * returns the plain text of the password.  
	 * 
	 * @return the SHA1 hash of the text in the (user)Password field.
	 */
	public String getUserPasswordHash() {
		String result = hashPassword(jPasswordFieldUser);
		return result;
	}
	
	public String getUsername() { 
		return jTextFieldEmail.getText();
	}
	
	public String getSchemaName() { 
		return jTextFieldUsername.getText();
	}
	
	public void setSchemaName(String aDBSchemaName) { 
		jTextFieldUsername.setText(aDBSchemaName);
	}
	
	public String getDBPassword() { 
		return String.valueOf(jPasswordFieldDB.getPassword());
	}
	
	public void setDBPassword(String aDBPassword) { 
		jPasswordFieldDB.setText(aDBPassword);
		// Force advanced panel to open if no database password is stored.
		if (aDBPassword==null||aDBPassword.length()==0) { 
			jPanelAdvanced.setVisible(true);
		}
	}
	
	public String getDriver() { 
		return jTextFieldDriver.getText();
	}
	
	public String getConnection() { 
		return jTextFieldConnection.getText();
	}
	
	public String getDialect() { 
		return jTextFieldDialect.getText();
	}
	
	public void setStatus(String aStatus) { 
	    jLabel6.setText(aStatus);	
	}
	
	
	/**
	 * @param textFieldDriver the jTextFieldDriver to set
	 */
	public void setDriver(String textFieldDriver) {
		jTextFieldDriver.setText(textFieldDriver);
	}

	/**
	 * @param textFieldConnection the jTextFieldConnection to set
	 */
	public void setConnection(String textFieldConnection) {
		jTextFieldConnection.setText(textFieldConnection);
	}

	/**
	 * @param textFieldDialect the jTextFieldDialect to set
	 */
	public void setDialect(String textFieldDialect) {
		jTextFieldDialect.setText(textFieldDialect);
	}
	
	public int getResult() { 
		return result;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.ipady = 3;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.ipady = 3;
			gridBagConstraints11.fill = GridBagConstraints.NONE;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJButtonCancel(), gridBagConstraints13);
			jPanel1.add(getJButtonLogin(), gridBagConstraints11);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jTextFieldEmail	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldEmail() {
		if (jTextFieldEmail == null) {
			jTextFieldEmail = new JTextField();
		}
		return jTextFieldEmail;
	}

	/**
	 * This method initializes jPasswordFieldUser	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordFieldUser() {
		if (jPasswordFieldUser == null) {
			jPasswordFieldUser = new JPasswordField();
		}
		return jPasswordFieldUser;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Advanced");
			jButton2.setMnemonic(KeyEvent.VK_A);
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                   toggleAdvanced();					
				}
			});
		}
		return jButton2;
	}
	
	public void toggleAdvanced() { 
		if (jPanelAdvanced.isVisible()) { 
            this.setSize(new Dimension(698, 190));
			jPanelAdvanced.setVisible(false);
		} else { 
            this.setSize(new Dimension(698, 290));
			jPanelAdvanced.setVisible(true);
		}
	}

	/**
	 * This method initializes jPanelAdvanced	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelAdvanced() {
		if (jPanelAdvanced == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.insets = new Insets(0, 15, 0, 0);
			gridBagConstraints4.gridx = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.anchor = GridBagConstraints.WEST;
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 3;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 1;
			gridBagConstraints14.gridy = 2;
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.anchor = GridBagConstraints.EAST;
			gridBagConstraints9.gridy = 3;
			gridBagConstraints9.gridx = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.anchor = GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.gridx = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.anchor = GridBagConstraints.WEST;
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 5;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.anchor = GridBagConstraints.EAST;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 5;
			gridBagConstraints7.insets = new Insets(0, 5, 0, 0);
			jPanelAdvanced = new JPanel();
			jPanelAdvanced.setLayout(new GridBagLayout());
			jPanelAdvanced.add(jLabel3, gridBagConstraints7);
			jPanelAdvanced.add(getJTextFieldConnection(), gridBagConstraints8);
			jPanelAdvanced.add(jLabel2, gridBagConstraints5);
			jPanelAdvanced.add(getJTextFieldDriver(), gridBagConstraints6);
			jPanelAdvanced.add(jLabel4, gridBagConstraints9);
			jPanelAdvanced.add(getJTextFieldDialect(), gridBagConstraints10);
			jPanelAdvanced.add(jLabel, gridBagConstraints);
			jPanelAdvanced.add(getJTextFieldUsername(), gridBagConstraints1);
			jPanelAdvanced.add(jLabel1, gridBagConstraints4);
			jPanelAdvanced.add(getJPasswordFieldDB(), gridBagConstraints3);
		}
		return jPanelAdvanced;
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
