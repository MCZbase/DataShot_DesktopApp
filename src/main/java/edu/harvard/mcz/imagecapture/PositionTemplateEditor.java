/**
 * PositionTemplateEditor.java
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
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.BadTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.ui.ButtonEditor;
import edu.harvard.mcz.imagecapture.ui.ButtonRenderer;

/** PositionTemplateEditor allows viewing and creation of position templates indicating which portions 
 * of an image file contain a barcode, OCR text, a specimen, labels from the unit tray, and labels from
 * the specimen's pin.
 * 
 * @author Paul J. Morris
 *
 */
public class PositionTemplateEditor extends JFrame {
	
	private static final long serialVersionUID = -6969168467927467337L;

	private static final Log log = LogFactory.getLog(PositionTemplateEditor.class);
	
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JButton jButtonSave = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JTextField jTextFieldTemplateId = null;
	private JTextField jTextFieldName = null;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JLabel jLabel7 = null;
	private JTextField jTextField2 = null;
	private JButton controlBarcode = null;
	private JButton controlText = null;
	private JButton controlLabel = null;
	private JButton controlUTLabels = null;
	private JButton controlSpecimen = null;
	private JLabel jLabel8 = null;
	private JTextField jTextFieldImageFileName = null;
	private JPanel jPanel2 = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private ImagePanelForDrawing imagePanelForDrawing = null;
    private PositionTemplateEditor thisFrame;
    private boolean runningFromMain = false;
	private JPanel jPanel1 = null;
	private PositionTemplate template = null;  //  @jve:decl-index=0:
	private JScrollPane jScrollPane1 = null;
	private String referenceImageFilename;  // name of the currently loaded image file.  //  @jve:decl-index=0:
	private String referenceImagePath;  //  @jve:decl-index=0:
	private JLabel jLabelFeedback = null;
	private JButton jButton = null;
	private JTextField jTextFieldBarcodeScan = null;
	private JButton jButtonUnitTrayBarcode = null;
	private JTextField jTextFieldUnitTrayBarcode = null;
	private JLabel jLabel9 = null;
	private JButton controlUTBarcode = null;
	
	
	/**
	 * This is the default constructor
	 */
	public PositionTemplateEditor() {
		super();
		thisFrame = this;
		initialize();
		pack();
		setBlankBackgroundImage();
	}
	
	/**
	 * Constructor called from main method when running as stand alone application.
	 * 
	 * @param runningAsApplication true to display file/exit menu option
	 */
	public PositionTemplateEditor(boolean runningAsApplication) {
		super();
		thisFrame = this;
		runningFromMain = true;
		initialize();
		pack();
		setBlankBackgroundImage();
	}	
	
	public void setBlankBackgroundImage() { 
		try {
			// Can't retrieve resource as a file from Jar file, unless giving File to ImageIcon....
			URL url = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/blank2848x4272.jpg");
			log.error(url);
			log.error(url.toExternalForm());
			setImageFile(url);
		} catch (IOException e) {
			log.error("Can't load blank template image");
			log.error(e);
		}
	}
	
	public void setTemplate(String aTemplateId) throws NoSuchTemplateException { 
			template = new PositionTemplate(aTemplateId);
            jTextFieldTemplateId.setText(aTemplateId);
            jTextFieldName.setText(template.getName());
            if  (template.getImageSize()==null) {
            	jTextField2.setText("Any Size");
            	controlBarcode.setText("No Value");
            	controlText.setText("No Value");
            	controlLabel.setText("No Value");
            	controlUTLabels.setText("No Value");
            	controlSpecimen.setText("No Value");
            	controlUTBarcode.setText("No Value");
            } else {  
            	jTextField2.setText("Width="+template.getImageSize().width+" Height="+template.getImageSize().height);
            	setButtonTexts();
            	if (template.getReferenceImage()!=null) { 
            		try {
						setImageFile(new File(template.getReferenceImageFilePath()));
					} catch (IOException e) {
						log.error("Failed to load default image for template.");
						log.error(e);
					}
            	}
            }
            thisFrame.pack();
    	    jButtonSave.setEnabled(template.isEditable());
    	    controlBarcode.setEnabled(template.isEditable());
    	    controlText.setEnabled(template.isEditable());
    	    controlLabel.setEnabled(template.isEditable());
    	    controlUTLabels.setEnabled(template.isEditable());
    	    controlSpecimen.setEnabled(template.isEditable());
    	    controlUTBarcode.setEnabled(template.isEditable());
    	    drawLayers();
	}
	
	/** Set the image displayed in the editor given an URL (needed to load from resource inside jar).
	 * 
	 * @param anImageURL an URL pointing to an image file.
	 * @throws IOException
	 */
	public void setImageFile(URL anImageURL) throws IOException { 
		referenceImageFilename = anImageURL.getPath();
		//TODO: Won't work with referenceImageFilename lookup.
		loadImage(ImageIO.read(anImageURL));
	}
	
	/**Set the image displayed in the editor given a File
	 * 
	 * @param anImageFile the image file to display.
	 * @throws IOException
	 */
	public void setImageFile(File anImageFile) throws IOException { 
		if (anImageFile!=null) { 
			referenceImageFilename = anImageFile.getName();
			referenceImagePath = anImageFile.getPath();
		    loadImage(ImageIO.read(anImageFile));
			jTextFieldImageFileName.setText(anImageFile.getName());
		}
	}
	
	private void loadImage(Image anImage) { 
		imagePanelForDrawing.setImage(anImage);
		imagePanelForDrawing.zoomToFit();
	}
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setPreferredSize(new Dimension(1100, 900));
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("Image Template Editor");
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
			jContentPane.add(getJPanel1(), java.awt.BorderLayout.CENTER);
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
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = GridBagConstraints.BOTH;
			gridBagConstraints24.gridy = 10;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.anchor = GridBagConstraints.WEST;
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints113 = new GridBagConstraints();
			gridBagConstraints113.gridx = 0;
			gridBagConstraints113.anchor = GridBagConstraints.EAST;
			gridBagConstraints113.gridy = 10;
			jLabel9 = new JLabel();
			jLabel9.setText("Taxon Name Barcode");
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.fill = GridBagConstraints.BOTH;
			gridBagConstraints23.gridy = 13;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.gridx = 1;
			GridBagConstraints gridBagConstraints112 = new GridBagConstraints();
			gridBagConstraints112.gridx = 0;
			gridBagConstraints112.gridy = 13;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.BOTH;
			gridBagConstraints22.gridy = 12;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.anchor = GridBagConstraints.WEST;
			gridBagConstraints22.gridx = 1;
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.gridx = 0;
			gridBagConstraints111.gridy = 12;
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.gridx = 1;
			gridBagConstraints110.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints110.gridwidth = 1;
			gridBagConstraints110.anchor = GridBagConstraints.NORTH;
			gridBagConstraints110.gridy = 14;
			jLabelFeedback = new JLabel();
			jLabelFeedback.setText(" ");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.weightx = 2.0;
			gridBagConstraints21.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints13.gridy = 0;
			jLabel8 = new JLabel();
			jLabel8.setText("ImageFile");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.BOTH;
			gridBagConstraints18.gridy = 9;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.fill = GridBagConstraints.BOTH;
			gridBagConstraints17.gridy = 8;
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridx = 1;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = GridBagConstraints.BOTH;
			gridBagConstraints16.gridy = 7;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.BOTH;
			gridBagConstraints15.gridy = 6;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			gridBagConstraints14.gridy = 5;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = GridBagConstraints.BOTH;
			gridBagConstraints12.gridy = 3;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.anchor = GridBagConstraints.WEST;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = GridBagConstraints.EAST;
			gridBagConstraints11.gridy = 9;
			jLabel7 = new JLabel();
			jLabel7.setText("Specimen");
			jLabel7.setForeground(Color.ORANGE);
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.EAST;
			gridBagConstraints10.gridy = 8;
			jLabel6 = new JLabel();
			jLabel6.setText("Tray Labels");
			jLabel6.setForeground(Color.CYAN);
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.EAST;
			gridBagConstraints9.gridy = 7;
			jLabel5 = new JLabel();
			jLabel5.setText("Pin Labels");
			jLabel5.setForeground(Color.MAGENTA);
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.insets = new Insets(0, 3, 0, 0);
			gridBagConstraints8.gridy = 6;
			jLabel4 = new JLabel();
			jLabel4.setText("Taxon Name Label");
			jLabel4.setForeground(Color.BLUE);
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.EAST;
			gridBagConstraints7.gridy = 5;
			jLabel3 = new JLabel();
			jLabel3.setText("Barcode");
			jLabel3.setForeground(Color.RED);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridy = 3;
			jLabel2 = new JLabel();
			jLabel2.setText("Image Size");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 2;
			jLabel1 = new JLabel();
			jLabel1.setText("Name");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.anchor = GridBagConstraints.EAST;
			gridBagConstraints1.gridy = 1;
			jLabel = new JLabel();
			jLabel.setText("Template ID");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 11;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButtonSave(), gridBagConstraints);
			jPanel.add(jLabel, gridBagConstraints1);
			jPanel.add(jLabel1, gridBagConstraints2);
			jPanel.add(getJTextFieldTemplateId(), gridBagConstraints4);
			jPanel.add(getJTextFieldName(), gridBagConstraints5);
			jPanel.add(jLabel2, gridBagConstraints6);
			jPanel.add(jLabel3, gridBagConstraints7);
			jPanel.add(jLabel4, gridBagConstraints8);
			jPanel.add(jLabel5, gridBagConstraints9);
			jPanel.add(jLabel6, gridBagConstraints10);
			jPanel.add(jLabel7, gridBagConstraints11);
			jPanel.add(getJTextField2(), gridBagConstraints12);
			jPanel.add(getJTextField3(), gridBagConstraints14);
			jPanel.add(getJTextField4(), gridBagConstraints15);
			jPanel.add(getJTextField5(), gridBagConstraints16);
			jPanel.add(getJTextField6(), gridBagConstraints17);
			jPanel.add(getJTextField7(), gridBagConstraints18);
			jPanel.add(jLabel8, gridBagConstraints13);
			jPanel.add(getJTextField8(), gridBagConstraints21);
			jPanel.add(jLabelFeedback, gridBagConstraints110);
			jPanel.add(getJButton(), gridBagConstraints111);
			jPanel.add(getJTextFieldBarcodeScan(), gridBagConstraints22);
			jPanel.add(getJButton1(), gridBagConstraints112);
			jPanel.add(getJTextField(), gridBagConstraints23);
			jPanel.add(jLabel9, gridBagConstraints113);
			jPanel.add(getJTextField9(), gridBagConstraints24);
		}
		return jPanel;
	}
	

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setText("Save Template");
			jButtonSave.setEnabled(false);
			jButtonSave.setMnemonic(KeyEvent.VK_S);
			jButtonSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jLabelFeedback.setText(" ");
					if (template.isEditable()) { 
						template.setTemplateId(jTextFieldTemplateId.getText());
						template.setTemplateName(jTextFieldName.getText());
						template.setImageSize(imagePanelForDrawing.getImageSize());
						try {
							if (template.getTemplateId().trim().equals("")) { 
								throw new BadTemplateException("Template ID can't be blank.");
							}
							if (template.getReferenceImage()==null) {
								ICImageLifeCycle ils = new ICImageLifeCycle();
								ICImage imageToFind = new ICImage();
								imageToFind.setFilename(referenceImageFilename);
								List<ICImage> images = ils.findByExample(imageToFind);
								if (!images.isEmpty()) { 
									template.setReferenceImage(images.get(0));
								} else { 
									//create a new image record
									ICImage newImage = new ICImage();
									newImage.setFilename(referenceImageFilename);
									// path should be relative to the base path
								    // just substituting won't work for images off the base path.
									String startPointName = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
									newImage.setPath(
											referenceImagePath.replaceAll(startPointName, "").
											replaceAll(referenceImageFilename + "$", "")
											);
									newImage.setTemplateId(template.getTemplateId());
									ils.persist(newImage);
									if (referenceImageFilename!=null) { 
										jTextFieldImageFileName.setText(referenceImageFilename);
									}
								}
							}		
							
							//TODO: Check that template is valid, not overlapping with existing template.
							//Test images IMG_00005.jpg and IMG_00001.jpg suggest that overlapping templates might
							//be needed - where all parameters except extent of barcode are the same.  
							
							template.persist();
							
                            jLabelFeedback.setText("Saved " + template.getTemplateId());
							
						} catch (BadTemplateException e1) {
							JOptionPane.showMessageDialog(thisFrame,
								    "Error. Unable to save template, invalid data. " + e1.getMessage(),
								    "Error:BadTemplateData",
								    JOptionPane.ERROR_MESSAGE);
							log.debug(e1);
						} catch (SaveFailedException e2) {
							JOptionPane.showMessageDialog(thisFrame,
								    "Error. Unable to save template. " + e2.getMessage(),
								    "Error:SaveFailed",
								    JOptionPane.ERROR_MESSAGE);
							log.debug(e2);
						}
					}
				}
			});
		}
		return jButtonSave;
	}

	/**
	 * This method initializes jTextFieldTemplateId	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldTemplateId() {
		if (jTextFieldTemplateId == null) {
			jTextFieldTemplateId = new JTextField(50);
		}
		return jTextFieldTemplateId;
	}

	/**
	 * This method initializes jTextFieldName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldName() {
		if (jTextFieldName == null) {
			jTextFieldName = new JTextField();
		}
		return jTextFieldName;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu() {
		if (jMenu == null) {
			jMenu = new JMenu();
			jMenu.setText("File");
			jMenu.setMnemonic(KeyEvent.VK_F);
			jMenu.add(getJMenuItemLoadImage());
			jMenu.add(getJMenuItemCreateTemplate());
			jMenu.add(getJMenuItem2());
		}
		return jMenu;
	}
	
	/**
	 * Set the text for each button as the position and size of the relevant portion of the template.
	 * 
	 */
	private void setButtonTexts() { 
	    controlBarcode.setText("UL="+template.getBarcodePosition().width + "," + template.getBarcodePosition().height + " W/H="+template.getBarcodeSize().width + "," + template.getBarcodeSize().height );
	    controlText.setText("UL="+template.getTextPosition().width + "," + template.getTextPosition().height  + " W/H="+template.getTextSize().width + "," + template.getTextSize().height);
	    controlLabel.setText("UL="+template.getLabelPosition().width + "," + template.getLabelPosition().height  + " W/H="+template.getLabelSize().width + "," + template.getLabelSize().height);
	    controlUTLabels.setText("UL="+template.getUTLabelsPosition().width + "," + template.getUTLabelsPosition().height  + " W/H="+template.getUTLabelsSize().width + "," + template.getUTLabelsSize().height);
	    controlSpecimen.setText("UL="+template.getSpecimenPosition().width + "," + template.getSpecimenPosition().height  + " W/H="+template.getSpecimenSize().width + "," + template.getSpecimenSize().height);
        controlUTBarcode.setText("UL="+template.getUtBarcodePosition().width + "," + template.getUtBarcodePosition().height  + " W/H="+template.getUtBarcodeSize().width + "," + template.getUtBarcodeSize().height);
	}        	    
                	    
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemCreateTemplate() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Create New Template From Image");
			jMenuItem.setMnemonic(KeyEvent.VK_N);
			jMenuItem.setEnabled(true);
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					template = new PositionTemplate(true);
					template.setImageSize(imagePanelForDrawing.getImageSize());
	            	jTextField2.setText("Width="+template.getImageSize().width+" Height="+template.getImageSize().height);
					setButtonTexts();
	        	    drawLayers();
	        	    jButtonSave.setEnabled(template.isEditable());
	        	    controlBarcode.setEnabled(template.isEditable());
	        	    controlText.setEnabled(template.isEditable());
	        	    controlLabel.setEnabled(template.isEditable());
	        	    controlUTLabels.setEnabled(template.isEditable());
	        	    controlSpecimen.setEnabled(template.isEditable());
	        	    controlUTBarcode.setEnabled(template.isEditable());
	        	    drawLayers();
				}
			});
		}
		return jMenuItem;
	}

	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemLoadImage() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("Load Image");
			jMenuItem1.setMnemonic(KeyEvent.VK_L);
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jLabelFeedback.setText("");
					final JFileChooser fileChooser = new JFileChooser();
					if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTPATH)!=null) { 
						fileChooser.setCurrentDirectory(new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTPATH)));
					} 
					//FileNameExtensionFilter filter = new FileNameExtensionFilter("TIFF Images", "tif", "tiff");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "tif", "tiff", "jpg", "jpeg", "png");
					fileChooser.setFileFilter(filter);
					int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						jLabelFeedback.setText("Loading...");
						try {
							setImageFile(fileChooser.getSelectedFile());
							jLabelFeedback.setText("");
							drawLayers();
						} catch (IOException e1) {
							log.debug(e1);
							jLabelFeedback.setText("Unable to load image.");
						}
					}
					drawLayers();
				}
			});
		}
		return jMenuItem1;
	}

	/**
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			if (runningFromMain) { 
				jMenuItem2.setText("Exit");
			    jMenuItem2.setMnemonic(KeyEvent.VK_E);
			} else {
			    jMenuItem2.setText("Close Window");
			    jMenuItem2.setMnemonic(KeyEvent.VK_C);
		    }
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (runningFromMain) { 
						ImageCaptureApp.exit(ImageCaptureApp.EXIT_NORMAL);
					} else { 
						thisFrame.setVisible(false);
					}
				}
			});
		}
		return jMenuItem2;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
			jTextField2.setEditable(false);
		}
		return jTextField2;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextField3() {
		if (controlBarcode == null) {
			controlBarcode = new JButton();
			controlBarcode.setEnabled(false);
			controlBarcode.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PositionTemplateBoxDialog dialog = 
							new PositionTemplateBoxDialog(
									thisFrame,template.getImageSize(),
									template.getBarcodeULPosition(),
									template.getBarcodeSize(),
									"Barcode in " +template.getTemplateId());
						dialog.setVisible(true);
						if (template.isEditable()) { 
							template.setBarcodePosition(dialog.getUL());
							template.setBarcodeSize(dialog.getSize());
							setButtonTexts();
						}
					} catch (BadTemplateException e1) {
						JOptionPane.showMessageDialog(thisFrame,
							    "Error. Unable to edit, invalid template data. " + e1.getMessage(),
							    "Error:BadTemplate",
							    JOptionPane.ERROR_MESSAGE);
						log.error(e1);
					}
					drawLayers();
				}
			});
		}
		return controlBarcode;
	}

	/**
	 * This method initializes jTextField4	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextField4() {
		if (controlText == null) {
			controlText = new JButton();
			controlText.setEnabled(false);
			controlText.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PositionTemplateBoxDialog dialog = 
							new PositionTemplateBoxDialog(
									thisFrame,template.getImageSize(),
									template.getTextPosition(),
									template.getTextSize(),
									"Taxon Name Label in " +template.getTemplateId());
						dialog.setVisible(true);
						if (template.isEditable()) { 
							template.setTextPosition(dialog.getUL());
							template.setTextSize(dialog.getSize());
							setButtonTexts();
						}
					} catch (BadTemplateException e1) {
						JOptionPane.showMessageDialog(thisFrame,
							    "Error. Unable to edit, invalid template data. " + e1.getMessage(),
							    "Error:BadTemplate",
							    JOptionPane.ERROR_MESSAGE);
						log.error(e1);
					}
					drawLayers();
				}
			});
		}
		return controlText;
	}

	/**
	 * This method initializes jTextField5	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextField5() {
		if (controlLabel == null) {
			controlLabel = new JButton();
			controlLabel.setEnabled(false);
			controlLabel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PositionTemplateBoxDialog dialog = 
							new PositionTemplateBoxDialog(
									thisFrame,template.getImageSize(),
									template.getLabelPosition(),
									template.getLabelSize(),
									"Pin Labels in " +template.getTemplateId());
						dialog.setVisible(true);
						if (template.isEditable()) { 
							template.setLabelPosition(dialog.getUL());
							template.setLabelSize(dialog.getSize());
							setButtonTexts();
						}
					} catch (BadTemplateException e1) {
						JOptionPane.showMessageDialog(thisFrame,
							    "Error. Unable to edit, invalid template data. " + e1.getMessage(),
							    "Error:BadTemplate",
							    JOptionPane.ERROR_MESSAGE);
						log.error(e1);
					}
					drawLayers();
				}
			});
		}
		return controlLabel;
	}

	/**
	 * This method initializes jTextField6	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextField6() {
		if (controlUTLabels == null) {
			controlUTLabels = new JButton();
			controlUTLabels.setEnabled(false);
			controlUTLabels.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PositionTemplateBoxDialog dialog = 
							new PositionTemplateBoxDialog(
									thisFrame,template.getImageSize(),
									template.getUTLabelsPosition(),
									template.getUTLabelsSize(), 
									"Unit Tray Labels in " +template.getTemplateId());
						dialog.setVisible(true);
						if (template.isEditable()) { 
							template.setUTLabelsPosition(dialog.getUL());
							template.setUTLabelsSize(dialog.getSize());
							setButtonTexts();
						}
					} catch (BadTemplateException e1) {
						JOptionPane.showMessageDialog(thisFrame,
							    "Error. Unable to edit, invalid template data. " + e1.getMessage(),
							    "Error:BadTemplate",
							    JOptionPane.ERROR_MESSAGE);
						log.error(e1);
					}
					drawLayers();
				}
			});
		}
		return controlUTLabels;
	}

	/**
	 * This method initializes jTextField7	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextField7() {
		if (controlSpecimen == null) {
			controlSpecimen = new JButton();
			controlSpecimen.setEnabled(false);
			controlSpecimen.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PositionTemplateBoxDialog dialog = 
							new PositionTemplateBoxDialog(
									thisFrame,template.getImageSize(),
									template.getSpecimenPosition(),
									template.getSpecimenSize(), 
									"Specimen in " +template.getTemplateId());
						dialog.setVisible(true);
						if (template.isEditable()) { 
							template.setSpecimenPosition(dialog.getUL());
							template.setSpecimenSize(dialog.getSize());
							setButtonTexts();
						}
					} catch (BadTemplateException e1) {
						JOptionPane.showMessageDialog(thisFrame,
							    "Error. Unable to edit, invalid template data. " + e1.getMessage(),
							    "Error:BadTemplate",
							    JOptionPane.ERROR_MESSAGE);
						log.error(e1);
					}
					drawLayers();
				}
			});
		}
		return controlSpecimen;
	}


	
	/**
	 * This method initializes jTextField8	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField8() {
		if (jTextFieldImageFileName == null) {
			jTextFieldImageFileName = new JTextField(50);
			jTextFieldImageFileName.setEditable(false);
		}
		return jTextFieldImageFileName;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.NORTH;
			gridBagConstraints3.weightx = 1.0;
			jPanel2 = new JPanel();
			jPanel2.setLayout(new GridBagLayout());
			jPanel2.add(getJScrollPane(), gridBagConstraints3);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			//jScrollPane.setPreferredSize(new Dimension(600,150));
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			List<PositionTemplate> templates = PositionTemplate.getTemplates();
			jTable.setModel(new PositionTemplateTableModel(templates));
			jTable.getColumn("").setCellRenderer( new ButtonRenderer());
			jTable.getColumn("").setCellEditor(new ButtonEditor(ButtonEditor.OPEN_TEMPLATE,thisFrame));
		}
		return jTable;
	}

	/**
	 * This method initializes imagePanelForDrawing	
	 * 	
	 * @return edu.harvard.mcz.imagecapture.ImagePanelForDrawing	
	 */
	private ImagePanelForDrawing getImagePanelForDrawing() {
		if (imagePanelForDrawing == null) {
			imagePanelForDrawing = new ImagePanelForDrawing();
			imagePanelForDrawing.setPreferredSize(new Dimension(600,600));
		}
		return imagePanelForDrawing;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.fill = GridBagConstraints.BOTH;
			gridBagConstraints19.weighty = 1.0;
			gridBagConstraints19.gridx = 2;
			gridBagConstraints19.gridy = 1;
			gridBagConstraints19.weightx = 1.0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			GridBagConstraints g1 = new GridBagConstraints();
			g1.gridx=1;
			g1.anchor = GridBagConstraints.NORTHEAST;
			g1.weightx = 0.1;
			g1.weighty = 0.75;
			g1.fill = GridBagConstraints.HORIZONTAL;
			g1.gridy = 1;
			GridBagConstraints g2 = new GridBagConstraints();
			g2.gridx = 1;
			g2.anchor = GridBagConstraints.NORTH;
			g2.fill = GridBagConstraints.BOTH;
			g2.gridwidth = 2;
			g2.weighty = 0.2;
			g2.weightx = 0.0;
			g2.gridy = 0;
			jPanel1.add(getJPanel(), g1);
			jPanel1.add(getJPanel2(), g2);
			jPanel1.add(getJScrollPane1(), gridBagConstraints19);
			
		}
		return jPanel1;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setPreferredSize(new Dimension(800,800));
			jScrollPane1.setViewportView(getImagePanelForDrawing());
		}
		return jScrollPane1;
	}
	
	/**
	 * Draw boxes delimiting the various parts of the template on the current image.
	 * Public so that it can be invoked from ButtonEditor().
	 */
	public void drawLayers() { 
		// draw the image bounds in black.
		if (template!=null) {
			imagePanelForDrawing.clearOverlay();
		    imagePanelForDrawing.drawBox(new Dimension(0,0), template.getImageSize(), Color.BLACK, new BasicStroke(2F));
		    // draw each template layer in a distinct color (keyed to UI text).
		    imagePanelForDrawing.drawBox(template.getBarcodeULPosition(), template.getBarcodeSize(), Color.RED);
		    imagePanelForDrawing.drawBox(template.getTextPosition(), template.getTextSize(), Color.BLUE);
		    imagePanelForDrawing.drawBox(template.getSpecimenPosition(), template.getSpecimenSize(), Color.ORANGE);
		    imagePanelForDrawing.drawBox(template.getUTLabelsPosition(), template.getUTLabelsSize(), Color.CYAN);
		    imagePanelForDrawing.drawBox(template.getLabelPosition(), template.getLabelSize(), Color.MAGENTA);
		    imagePanelForDrawing.drawBox(template.getUtBarcodePosition(), template.getUtBarcodeSize(), Color.BLACK);
		} 
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Check for Barcode");
			jButton.setMnemonic(KeyEvent.VK_C);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (template!=null && imagePanelForDrawing.getImage()!= null) { 
					    jTextFieldBarcodeScan.setText(CandidateImageFile.getBarcodeTextFromImage((BufferedImage) imagePanelForDrawing.getImage(),template,false));
					}
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldBarcodeScan() {
		if (jTextFieldBarcodeScan == null) {
			jTextFieldBarcodeScan = new JTextField();
			jTextFieldBarcodeScan.setEditable(false);
		}
		return jTextFieldBarcodeScan;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButtonUnitTrayBarcode == null) {
			jButtonUnitTrayBarcode = new JButton();
			jButtonUnitTrayBarcode.setText("Check Taxon Barcode");
			jButtonUnitTrayBarcode.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextFieldUnitTrayBarcode.setText(CandidateImageFile.getBarcodeUnitTrayTextFromImage((BufferedImage) imagePanelForDrawing.getImage(),template));
				}
			});
		}
		return jButtonUnitTrayBarcode;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldUnitTrayBarcode == null) {
			jTextFieldUnitTrayBarcode = new JTextField();
			jTextFieldUnitTrayBarcode.setEditable(false);
		}
		return jTextFieldUnitTrayBarcode;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextField9() {
		
		if (controlUTBarcode == null) {
			controlUTBarcode = new JButton();
			controlUTBarcode.setEnabled(false);
			controlUTBarcode.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						PositionTemplateBoxDialog dialog = 
							new PositionTemplateBoxDialog(
									thisFrame,template.getImageSize(),
									template.getUtBarcodePosition(),
									template.getUtBarcodeSize(),
									"UnitTray/Taxon Barcode in " +template.getTemplateId());
						dialog.setVisible(true);
						if (template.isEditable()) { 
							template.setUtBarcodePosition(dialog.getUL());
							template.setUtBarcodeSize(dialog.getSize());
							setButtonTexts();
						}
					} catch (BadTemplateException e1) {
						JOptionPane.showMessageDialog(thisFrame,
							    "Error. Unable to edit, invalid template data. " + e1.getMessage(),
							    "Error:BadTemplate",
							    JOptionPane.ERROR_MESSAGE);
						log.error(e1);
					}
					drawLayers();
				}
			});
		}
		
		return controlUTBarcode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PositionTemplateEditor thisClass = new PositionTemplateEditor(true);
				thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				thisClass.setVisible(true);
			}
		});
	}
	
}  //  @jve:decl-index=0:visual-constraint="9,-1"
