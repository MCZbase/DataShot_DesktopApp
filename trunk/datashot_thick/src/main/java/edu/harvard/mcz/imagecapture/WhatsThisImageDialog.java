/**
 * WhatsThisImageDialog.java
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

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** WhatsThisImageDialog is a dialog to allow users to identify the
 * nature (specimen, drawer) of images for which barcode detection and
 * OCR isn't able to detect the nature of the image. 
 * 
 * @author Paul J. Morris
 *
 */
public class WhatsThisImageDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String SEL_SPECIMEN = "Specimen";
	private static final String SEL_DRAWER = "Paper in Drawer";
	private static final String SEL_OTHER = "Other";
	
	private static final Log log = LogFactory.getLog(WhatsThisImageDialog.class);  //  @jve:decl-index=0:
	
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private ImageZoomPanel imagePanel = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldBarcode = null;
	private JLabel jLabel3 = null;
	private JTextField jTextFieldDrawerNumber = null;
	private JButton jButton = null;
	private JPanel jPanel2 = null;
	private JLabel jLabel4 = null;
	private JComboBox jComboBox = null;
	private WhatsThisImageDialog thisDialog = null;

	/** Default constructor, probably not the one to use, 
	 * as image needs to be provided.
	 *  
	 * @param owner the parent frame for this dialog.
	 */
	public WhatsThisImageDialog(Frame owner) {
		super(owner,true);
		thisDialog = this;
		initialize();
	}
	
	/** Constructor with the image to display as a parameter. 
	 * 
	 * @param owner the parent frame for this dialog.
	 * @param imageToShow the image of unknown nature to display.
	 */
	public WhatsThisImageDialog(Frame owner, BufferedImage imageToShow) {
		super(owner,true);
		thisDialog = this;
		initialize();
		this.setImage(imageToShow);
	}	

	/** Constructor with an image File to display as a parameter.  Will
	 * display a broken image icon if File can't be read as an
	 * image.
	 * 
	 * @param owner the parent frame for this dialog.
	 * @param imageFileToShow the image file of unknown nature to display.
	 */
	public WhatsThisImageDialog(Frame owner, File imageFileToShow) {
		super(owner,true);
		thisDialog = this;
		initialize();
		BufferedImage image;
		try {
			image = ImageIO.read(imageFileToShow);
			this.setImage(image);
		} catch (IOException e) {
			log.error("Unable to open selected image " + imageFileToShow.getName());
            log.debug(e);
			URL errorFilename = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/unableToLoadImage.jpg");
			try {
				this.setImage(ImageIO.read(errorFilename));
			} catch (IOException e1) {
				log.error("Unable to open resource image");
				log.error(e1);
			}
		}
	}	
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(755, 357);
		this.setTitle("What is this image");
		this.setContentPane(getJContentPane());
	}

	public String getBarcode(){ 
		return this.jTextFieldBarcode.getText();
	}
	
	public String getDrawerNumber() { 
		return this.jTextFieldDrawerNumber.getText();
	}
	
	public void setImage(BufferedImage anImage) { 
		imagePanel.setImage(anImage);
	}
	
	public boolean isSpecimen() { 
		boolean result = false;
		if (((String)jComboBox.getSelectedItem()).equals(SEL_SPECIMEN)
				&& Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(jTextFieldBarcode.getText())) { 
			result = true;
		}
		return result;
	}

	public boolean isDrawerImage() { 
		boolean result = false;
		if (((String)jComboBox.getSelectedItem()).equals(SEL_DRAWER) 
				&& jTextFieldDrawerNumber.getText().matches(ImageCaptureApp.REGEX_DRAWERNUMBER)) { 
			result = true;
		}
		return result;
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.WEST);
			jContentPane.add(getImagePanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 4;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.anchor = GridBagConstraints.WEST;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.gridy = 4;
			jLabel4 = new JLabel();
			jLabel4.setText("Image Of:");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.gridy = 6;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.gridy = 5;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.weighty = 0.0;
			gridBagConstraints4.gridy = 3;
			jLabel3 = new JLabel();
			jLabel3.setText("DrawerNumber:");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints3.weighty = 0.0;
			gridBagConstraints3.anchor = GridBagConstraints.NORTH;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.WEST;
			gridBagConstraints2.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.weighty = 0.0;
			gridBagConstraints1.gridy = 2;
			jLabel2 = new JLabel();
			jLabel2.setText("Barcode:");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints.weighty = 0.0;
			gridBagConstraints.gridy = 1;
			jLabel1 = new JLabel();
			jLabel1.setText("Please Identify this Image.");
			jLabel = new JLabel();
			jLabel.setText("No Barcode or drawer number found.");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(jLabel, gridBagConstraints3);
			jPanel.add(jLabel1, gridBagConstraints);
			jPanel.add(jLabel2, gridBagConstraints1);
			jPanel.add(getJTextFieldBarcode(), gridBagConstraints2);
			jPanel.add(jLabel3, gridBagConstraints4);
			jPanel.add(getJTextFieldDrawerNumber(), gridBagConstraints5);
			jPanel.add(getJButton(), gridBagConstraints6);
			jPanel.add(getJPanel2(), gridBagConstraints7);
			jPanel.add(jLabel4, gridBagConstraints8);
			jPanel.add(getJComboBox(), gridBagConstraints9);
		}
		return jPanel;
	}

	/**
	 * This method initializes imagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private ImageZoomPanel getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new ImageZoomPanel();
		}
		return imagePanel;
	}

	/**
	 * This method initializes jTextFieldBarcode	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldBarcode() {
		if (jTextFieldBarcode == null) {
			jTextFieldBarcode = new JTextField();
			jTextFieldBarcode.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					checkValues();
				}
			});
		}
		return jTextFieldBarcode;
	}

	/**
	 * This method initializes jTextFieldDrawerNumber	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDrawerNumber() {
		if (jTextFieldDrawerNumber == null) {
			jTextFieldDrawerNumber = new JTextField();
			jTextFieldDrawerNumber.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					checkValues();
				}
			});
		}
		return jTextFieldDrawerNumber;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Continue");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// test for required values based on the selection on the combo box 
					boolean okToClose = false;
					if (((String)jComboBox.getSelectedItem()).equals(SEL_SPECIMEN)) { 
						if (Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(jTextFieldBarcode.getText())) {
							okToClose = true;
						} else { 
							jTextFieldBarcode.setBackground(MainFrame.BG_COLOR_ERROR);
						}
					}
					if (((String)jComboBox.getSelectedItem()).equals(SEL_DRAWER)) { 
						if (jTextFieldDrawerNumber.getText().matches(ImageCaptureApp.REGEX_DRAWERNUMBER)) {
							okToClose = true;
						} else { 
							jTextFieldDrawerNumber.setBackground(MainFrame.BG_COLOR_ERROR);
						}
					}
					if (((String)jComboBox.getSelectedItem()).equals(SEL_DRAWER)) { 
						okToClose = true;
					}
					// Only close if set of values makes sense.
					if (okToClose) { 
						thisDialog.setVisible(false);
					}
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
		}
		return jPanel2;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.addItem("");
			jComboBox.addItem(SEL_SPECIMEN);
			jComboBox.addItem(SEL_DRAWER);
			jComboBox.addItem(SEL_OTHER);
			jComboBox.setSelectedItem("");
		}
		return jComboBox;
	}
	
	private void checkValues() {
		if (((String)jComboBox.getSelectedItem()).equals(SEL_SPECIMEN)) { 
			if (Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(jTextFieldBarcode.getText())) {
				jTextFieldBarcode.setBackground(MainFrame.BG_COLOR_ENT_FIELD);
			} else { 
				jTextFieldBarcode.setBackground(MainFrame.BG_COLOR_ERROR);
			}
		}
		if (((String)jComboBox.getSelectedItem()).equals(SEL_DRAWER)) { 
			if (jTextFieldDrawerNumber.getText().matches(ImageCaptureApp.REGEX_DRAWERNUMBER)) {
				jTextFieldDrawerNumber.setBackground(MainFrame.BG_COLOR_ENT_FIELD);
			} else { 
				jTextFieldDrawerNumber.setBackground(MainFrame.BG_COLOR_ERROR);
			}
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
