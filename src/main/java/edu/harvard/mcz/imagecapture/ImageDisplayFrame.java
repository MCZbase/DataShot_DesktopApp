/**
 * ImageDisplayFrame.java
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
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.BadTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.ImageLoadException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Insets;

/** Display parts of images (and possibly a data entry form) of a specimen and its labels.
 *  
 * @author Paul J. Morris
 */
public class ImageDisplayFrame extends JFrame {
	
	private static final long serialVersionUID = 6208387412508034014L;
	
	private static final Log log = LogFactory.getLog(ImageDisplayFrame.class);
	
	// display order for tabs
	public static final int TAB_BARCODE = 5;
	public static final int TAB_SPECIMEN  = 1;
	public static final int TAB_LABELS = 2;
	public static final int TAB_UNITTRAY = 4;
	public static final int TAB_UNITTRAYLABELS = 3;
	public static final int TAB_FULLIMAGE = 0;

	// The displayed image read from a file.
	// Set of image files managed by loading one at a time and
	// displaying its components in a set of tabs.
	BufferedImage imagefile = null;  //  @jve:decl-index=0:
	
	private JPanel jContentPane = null;
	private JTabbedPane jTabbedPane = null;  // Tab pane to display image components
	// The specimen
	private JPanel jPanelSpecimen = null;
	private JScrollPane jScrollPane = null;
	private ImageZoomPanel imagePanelSpecimen = null;
	// the barcode
	private JLabel imagePanelBarcode = null;  // The barcode label.
	private JPanel jPanel = null;
	private JTextField jTextBarcode = null;  // Text read from the barcode.
	// pin labels
	private JPanel jPanelLabels = null;
	private ImageZoomPanel imagePanelPinLabels = null;   // the specimen labels from the pin.
	// unit tray taxon name header label
	private JPanel jPanelUnitTray = null;  //  @jve:decl-index=0:visual-constraint="574,225"
	private ImageZoomPanel imagePanelUnitTrayTaxon = null;  // The current determination new unit tray label for OCR.
	private JTextField jTextFieldRawOCR = null;
	// old unit tray labels
	private JPanel jPanelUnitTrayLabels = null;  //  @jve:decl-index=0:visual-constraint="574,225"
	private ImageZoomPanel imagePanelTrayLabels = null;  // small labels present in the unit tray.
    // full image
	private ImageZoomPanel imagePaneFullImage = null;

	private JPanel jPanelImagePicker = null;

	private JLabel jLabel = null;

	private JComboBox jComboBoxImagePicker = null;

	private JLabel jLabel1 = null;

	private JPanel jPanelImagesPanel = null;
	
	private Specimen targetSpecimen = null;
	private SpecimenControler targetSpecimenController = null;
	private ICImage selectedImage = null;
	
	private JButton templatePicker = null;
	private JButton btnVerbatimtranscription;

	/**
	 * This is the default constructor
	 * @param specimen 
	 */
	public ImageDisplayFrame(Specimen specimen, SpecimenControler specimenController) {
		super();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		targetSpecimen = specimen;
		targetSpecimenController = specimenController;
		initialize();
		//this.center();
	}
	
	public ImageDisplayFrame() {
		super();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		targetSpecimen = null;
		initialize();
	}

	/** Given a set of ICImages, display one of them in the tabs of the ImageDisplayFrame, and
	 * populate the image chooser pick list with a list of all the images.  Call this method to display 
	 * more than one image in an ImageDisplayFrame.  Single image is displayed with a call to loadImagesFromFileSingle().
	 *
	 * @see edu.harvard.mcz.imagecapture.ImageDisplayFrame#loadImagesFromFile(File, PositionTemplate)
	 * 
	 * @param imageFiles
	 */
	public void loadImagesFromFiles(Set<ICImage> imageFiles)  {
		log.debug(imageFiles.size());
		jComboBoxImagePicker.removeAllItems();
		Iterator<ICImage> i = imageFiles.iterator();
		ICImage image = null;
		int fileCount = 0;
		while (i.hasNext()) {
			fileCount++;
			image = i.next();
	        jComboBoxImagePicker.addItem(image.getFilename());
	        log.debug("Adding image to picklist: " + image.getPath() + image.getFilename());
		}
		//TODO: stored path may need separator conversion for different systems. 
		//String startPointName = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
		String path = image.getPath();
		if (path == null) { path = ""; } 
		//File fileToCheck = new File(startPointName + path + image.getFilename());
		File fileToCheck = new File(ImageCaptureProperties.assemblePathWithBase(path, image.getFilename()));
		jLabel1.setText("(" + fileCount + ")");
		jComboBoxImagePicker.setEnabled(true);
		jComboBoxImagePicker.setSelectedItem(image.getFilename());
		try {
		    PositionTemplate defaultTemplate = PositionTemplate.findTemplateForImage(image);
			loadImagesFromFileSingle(fileToCheck, defaultTemplate, image);
		} catch (BadTemplateException e) {
			log.error(e);
		} catch (ImageLoadException e) {
			log.error(e);
		}
		jTabbedPane.setSelectedIndex(0);   // move focus to full image tab
	}
	
	/** Call this method to display a single image for a specimen, with its component parts, if indicated
	 * by the PositionTemplate.  Wrapper around loadImagesFromFileSingle().  The combo box to pick images 
	 * will display just the name of this file, and won't be selectable.
	 * 
	 * @see edu.harvard.mcz.imagecapture.ImageDisplayFrame#loadImagesFromFiles(Set)   
	 * 
	 * @param anImageFile the image file to display in the tabs of the frame.
	 * @param template the position template to use to split the image file into components.
	 * @throws ImageLoadException if there is a problem with the image.
	 * @throws BadTemplateException 
	 */
	public void loadImagesFromFile(File anImageFile, PositionTemplate template, ICImage image) throws ImageLoadException, BadTemplateException {
		log.debug(anImageFile.getName());
		loadImagesFromFileSingle(anImageFile, template, image);
        jLabel1.setText("(1)");
        log.debug(1);
        if (jComboBoxImagePicker.getModel().getSize()>0) { 
            jComboBoxImagePicker.removeAllItems();
        }
        log.debug(2);
        jComboBoxImagePicker.addItem(anImageFile.getName());
        log.debug(3);
        jComboBoxImagePicker.setEnabled(false);
        jTabbedPane.setSelectedIndex(0);   // move focus to full image tab
	} 
	
	/** Based on the position template, display the full image in one tab, and parts of the image
	 * described by the template in other tabs.   
	 * 
	 * @param anImageFile
	 * @param defaultTemplate
	 * @throws ImageLoadException
	 * @throws BadTemplateException
	 */
	protected void loadImagesFromFileSingle(File anImageFile, PositionTemplate defaultTemplate, ICImage image) throws ImageLoadException, BadTemplateException {
		log.debug(anImageFile.getName());
		boolean templateProblem = false;
		selectedImage = image;
		//TODO: template detection
		try {
			imagefile = ImageIO.read(anImageFile);
			log.debug(anImageFile.getPath());
			// Display the full image
			this.setFullImage();
			// Show the component parts of the image as defined by the position template.
			if (defaultTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) {
				// clear component parts
				URL url = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/gnome-mime-image.png");
				BufferedImage anImage = ImageIO.read(url);
				this.setBarcodeImage(anImage);
				this.setSpecimenImage(anImage);
				this.setPinLabelImage(anImage);
				this.setUnitTrayImage(anImage);
				this.setUnitTrayLabelsImage(anImage);
			} else {
				if (imagefile.getHeight()!=defaultTemplate.getImageSize().height || imagefile.getWidth()!=defaultTemplate.getImageSize().width) {
					// TODO: template strategy
					throw new BadTemplateException("Template size doesn't match image size. " + defaultTemplate.getName());
				}
				try { 
					int x = defaultTemplate.getBarcodeULPosition().width;    
					int y =  defaultTemplate.getBarcodeULPosition().height; 
					int w = defaultTemplate.getBarcodeSize().width;  
					int h = defaultTemplate.getBarcodeSize().height;
					this.setBarcodeImage(imagefile.getSubimage(x, y, w, h));
				} catch (Exception ex) { 
					try { 
						this.setBarcodeImage(imagefile);
					} catch (Exception ex2) { 
						System.out.println(ex.getMessage());	
						throw new ImageLoadException("Unable to load images from " + anImageFile.getPath() + " " + ex2.getMessage());
					}
					templateProblem = true;
					System.out.println(ex.getMessage());	
				}
				try { 
					int x = defaultTemplate.getSpecimenPosition().width;    
					int y =  defaultTemplate.getSpecimenPosition().height; 
					int w = defaultTemplate.getSpecimenSize().width;  
					int h = defaultTemplate.getSpecimenSize().height;
					BufferedImage img = imagefile.getSubimage(x, y, w, h);
					this.setSpecimenImage(img);
				} catch (Exception ex) { 
					templateProblem = true;
					System.out.println(ex.getMessage());	
				}			
				try { 
					int x = defaultTemplate.getLabelPosition().width;    
					int y =  defaultTemplate.getLabelPosition().height; 
					int w = defaultTemplate.getLabelSize().width;  
					int h = defaultTemplate.getLabelSize().height;
					this.setPinLabelImage(imagefile.getSubimage(x, y, w, h));
				} catch (Exception ex) { 
					templateProblem = true;
					System.out.println(ex.getMessage());	
				}
				try { 
					int x = defaultTemplate.getTextPosition().width;    
					int y =  defaultTemplate.getTextPosition().height; 
					int w = defaultTemplate.getTextSize().width;  
					int h = defaultTemplate.getTextSize().height;
					this.setUnitTrayImage(imagefile.getSubimage(x, y, w, h));
				} catch (Exception ex) { 
					templateProblem = true;
					System.out.println(ex.getMessage());	
				}
				try { 
					int x = defaultTemplate.getUTLabelsPosition().width;    
					int y =  defaultTemplate.getUTLabelsPosition().height; 
					int w = defaultTemplate.getUTLabelsSize().width;  
					int h = defaultTemplate.getUTLabelsSize().height;
					this.setUnitTrayLabelsImage(imagefile.getSubimage(x, y, w, h));
				} catch (Exception ex) { 
					templateProblem = true;
					System.out.println(ex.getMessage());	
				}
			}
		} catch (IOException e1) {
			System.out.println("Error reading image file: " + e1.getMessage());
			throw new ImageLoadException("Unable to read image file " + anImageFile.getPath() + " " + e1.getMessage());
		}
		if (templateProblem) { 
			throw new BadTemplateException("Template doesn't fit file " + anImageFile.getPath());
		}
		log.debug(anImageFile.getPath());
		if (UsersLifeCycle.isUserChiefEditor(Singleton.getSingletonInstance().getUser().getUserid())) { 
		    updateTemplateList();
		}
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(755, 353);
		this.setPreferredSize(new Dimension(1350,1000));
		this.setContentPane(getJContentPane());
		this.setTitle("Image File and Barcode Value");
	}
	
	public void center() { 
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
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanelImagesPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}
	
	public void setActiveTab(int tab) { 
		try { 
		    jTabbedPane.setSelectedIndex(tab);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Failed to activate tab. " + e.getMessage());
		}
	}
	
	public void addWest(JPanel panel) {
//		JScrollPane scroll = new JScrollPane(panel);
//		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		jContentPane.add(scroll,BorderLayout.WEST);
		jContentPane.add(panel,BorderLayout.WEST);
		panel.setPreferredSize(new Dimension(594,panel.getHeight()));
//		scroll.setPreferredSize(new Dimension(594,panel.getHeight()));
		this.pack();
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelBarcode() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jTextBarcode = new JTextField();
			jTextBarcode.setText("Barcode");
			jPanel.add(getJScrollPane(), BorderLayout.CENTER);
			jPanel.add(jTextBarcode, BorderLayout.SOUTH);
		}
		return jPanel;
	}
	
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelSpecimen() {
		if (jPanelSpecimen == null) {
			jPanelSpecimen = new JPanel();
			jPanelSpecimen.setLayout(new BorderLayout());
			jPanelSpecimen.add(getJLabelSpecimen(), BorderLayout.CENTER);
		}
		return jPanelSpecimen;
	}	

	public void setSpecimenImage(BufferedImage anImage) { 
		imagePanelSpecimen.setImage(anImage);
		imagePanelSpecimen.zoomToFit();
		if (imagePanelSpecimen.getPreferredSize().height>500 || imagePanelSpecimen.getPreferredSize().width>500) { 
			imagePanelSpecimen.setPreferredSize(new Dimension(1000,900));
   	    } 
	}
	
	/**
	 * Fit the image in the PinLabels tab to the size of the imagePanel it is shown in.
	 */
	public void fitPinLabels() { 
		imagePanelPinLabels.zoomToFit();
	}
	
	/**
	 * Center the specimen image in the imagePanel it is shown in.
	 */
	public void centerSpecimen() { 
		imagePanelSpecimen.center();
	}
	
	public void setUnitTrayImage(Image anImage) { 
		imagePanelUnitTrayTaxon.setImage((BufferedImage) anImage);
		imagePanelUnitTrayTaxon.zoomToFit();
//		jLabelUnitTray.setIcon(new ImageIcon(anImage));
//		this.pack();
  	    if (imagePanelUnitTrayTaxon.getPreferredSize().height>500 || imagePanelUnitTrayTaxon.getPreferredSize().width>500) { 
  	    	imagePanelUnitTrayTaxon.setPreferredSize(new Dimension(500,500));
		} 
//		jLabelUnitTray.setMaximumSize(new Dimension(500,500));
	}	

	public void setUnitTrayLabelsImage(Image anImage) { 
		imagePanelTrayLabels.setImage((BufferedImage) anImage);
		imagePanelTrayLabels.zoomToFit();
		if (imagePanelTrayLabels.getPreferredSize().height>500 || imagePanelTrayLabels.getPreferredSize().width>500) { 
			imagePanelTrayLabels.setPreferredSize(new Dimension(500,500));
		} 
	}		
	
	public void setPinLabelImage(Image anImage) { 
		imagePanelPinLabels.setImage((BufferedImage) anImage);
		imagePanelPinLabels.zoomToFit();
		this.pack();
		if (imagePanelPinLabels.getPreferredSize().height>500 || imagePanelPinLabels.getPreferredSize().width>500) { 
			imagePanelPinLabels.setPreferredSize(new Dimension(500,500));
		} 
		imagePanelPinLabels.setMaximumSize(new Dimension(500,500));
	}
	
	public void setBarcodeImage(Image anImage) { 
		imagePanelBarcode.setIcon(new ImageIcon(anImage));
		this.pack();
		if (imagePanelBarcode.getPreferredSize().height>500 || imagePanelBarcode.getPreferredSize().width>500) { 
		    jScrollPane.setPreferredSize(new Dimension(500,500));
		} 
		imagePanelBarcode.setMaximumSize(new Dimension(500,500));
	}

	public void setBarcode(String someText) { 
	    jTextBarcode.setText(someText);
	    jTextBarcode.setEditable(false);
	    jTextBarcode.setEnabled(true);
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			imagePanelBarcode = new JLabel();
			imagePanelBarcode.setText("");
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(imagePanelBarcode);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private ImageZoomPanel getJLabelSpecimen() {
		if (imagePanelSpecimen == null) {
			imagePanelSpecimen = new ImageZoomPanel();
		}
		return imagePanelSpecimen;
	}	
	
	private JPanel getJPanelLabels() {
		if (jPanelLabels == null) {
			jPanelLabels = new JPanel();
			jPanelLabels.setLayout(new BorderLayout());
			jPanelLabels.add(getImagePanePinLabels(), BorderLayout.CENTER);
		}
		return jPanelLabels;
	}	
	
	private ImageZoomPanel getImagePanePinLabels() {
		if (imagePanelPinLabels == null) {
			imagePanelPinLabels = new ImageZoomPanel();
		}
		return imagePanelPinLabels;
	}	
	
	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.insertTab("FullImage", null, getPanelFullImage(), null, TAB_FULLIMAGE);
			jTabbedPane.insertTab("Specimen", null, getJPanelSpecimen(), null,TAB_SPECIMEN);
			jTabbedPane.insertTab("PinLabels", null, getJPanelLabels(), null, TAB_LABELS);
			jTabbedPane.insertTab("UnitTray Labels", null, getJPanelUTL(), null, TAB_UNITTRAYLABELS);			
			jTabbedPane.insertTab("Taxon Label", null, getJPanelUnitTrayTaxon(), null, TAB_UNITTRAY);
			jTabbedPane.insertTab("Barcode", null, getJPanelBarcode(), null,TAB_BARCODE);
			
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelUnitTrayTaxon() {
		if (jPanelUnitTray == null) {
			jPanelUnitTray = new JPanel();
			jPanelUnitTray.setLayout(new BorderLayout());
			jPanelUnitTray.setSize(new Dimension(108, 72));
			jPanelUnitTray.add(getJTextField(), BorderLayout.SOUTH);
			jPanelUnitTray.add(getImagePanelUnitTrayTaxon(), BorderLayout.CENTER);
		}
		return jPanelUnitTray;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private ImageZoomPanel getImagePanelUnitTrayTaxon() {
		if (imagePanelUnitTrayTaxon == null) {
			imagePanelUnitTrayTaxon = new ImageZoomPanel();
		}
		return imagePanelUnitTrayTaxon;
	}
	
    private void setFullImage() { 
    	if (imagefile!=null) { 
    	    getPanelFullImage().setImage(imagefile);
    	    // We need to make sure the container hieriarchy holding the image knows what 
    	    // size the image pane is before zoom to fit will work.f
    	    this.pack();  
    	    imagePaneFullImage.zoomToFit();
    	}
    }
	
	private ImageZoomPanel getPanelFullImage() { 
		if (imagePaneFullImage == null) { 
			if (imagefile==null) { 
				imagePaneFullImage = new ImageZoomPanel();
			} else { 
			    imagePaneFullImage = new ImageZoomPanel(imagefile);
			} 
		}
		return imagePaneFullImage;
	}
	
	/**
	 * This method initializes jPanelUTL
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelUTL() {
		if (jPanelUnitTrayLabels == null) {
			jPanelUnitTrayLabels = new JPanel();
			jPanelUnitTrayLabels.setLayout(new BorderLayout());
			jPanelUnitTrayLabels.setSize(new Dimension(108, 72));
			jPanelUnitTrayLabels.add(getImagePanelTrayLabels(), BorderLayout.CENTER);
		}
		return jPanelUnitTrayLabels;
	}
	
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private ImageZoomPanel getImagePanelTrayLabels() {
		if (imagePanelTrayLabels == null) {
			imagePanelTrayLabels = new ImageZoomPanel();
		}
		return imagePanelTrayLabels;
	}
	
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldRawOCR == null) {
			jTextFieldRawOCR = new JTextField();
			jTextFieldRawOCR.setEditable(false);
		}
		return jTextFieldRawOCR;
	}
	
	public void setRawOCRLabel(String text) { 
		jTextFieldRawOCR.setText(text);
	}

	/**
	 * This method initializes jPanelImagePicker	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelImagePicker() {
		if (jPanelImagePicker == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 5, 5);
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.gridy = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("(0)");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridx = 3;
			jLabel = new JLabel();
			jLabel.setText("Images: ");
			jPanelImagePicker = new JPanel();
			GridBagLayout gbl_jPanelImagePicker = new GridBagLayout();
			gbl_jPanelImagePicker.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0};
			jPanelImagePicker.setLayout(gbl_jPanelImagePicker);
			GridBagConstraints gbc_btnVerbatimtranscription = new GridBagConstraints();
			gbc_btnVerbatimtranscription.insets = new Insets(0, 0, 5, 5);
			gbc_btnVerbatimtranscription.gridx = 0;
			gbc_btnVerbatimtranscription.gridy = 0;
			jPanelImagePicker.add(getBtnVerbatimtranscription(), gbc_btnVerbatimtranscription);
			GridBagConstraints gbc_jLabel = new GridBagConstraints();
			gbc_jLabel.insets = new Insets(0, 0, 5, 5);
			gbc_jLabel.gridx = 1;
			gbc_jLabel.gridy = 0;
			jPanelImagePicker.add(jLabel, gbc_jLabel);
			jPanelImagePicker.add(getJComboBoxImagePicker(), gridBagConstraints);
			jPanelImagePicker.add(jLabel1, gridBagConstraints1);
		}
		return jPanelImagePicker;
	}

	/**
	 * This method initializes jComboBoxImagePicker	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxImagePicker() {
		if (jComboBoxImagePicker == null) {
			jComboBoxImagePicker = new JComboBox();
			if (targetSpecimen!=null) { 
				Iterator<ICImage> i = targetSpecimen.getICImages().iterator();
				while (i.hasNext()) { 
					String filename = i.next().getFilename();
					jComboBoxImagePicker.addItem(filename);
					log.debug(filename);
				}
			}
			jComboBoxImagePicker.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Intended to be fired when picklist item is selected, is
					// being fired on other events as well.
					log.debug(e.getActionCommand());
					// If there is no selection, then we shouldn't be doing anything.
					if (jComboBoxImagePicker.getSelectedItem()==null) { 
						log.debug("No selected item");
					} else { 
						ICImage pattern = new ICImage();
						try { 
							boolean hasParameter = false;
							if (jComboBoxImagePicker.getSelectedItem()!=null) { 
								pattern.setFilename(jComboBoxImagePicker.getSelectedItem().toString());
								hasParameter = true;
								log.debug("Parameter: " + jComboBoxImagePicker.getSelectedItem().toString());
							}
							if (targetSpecimen!=null) { 
								pattern.setSpecimen(targetSpecimen);
								hasParameter = true;
								log.debug("Parameter: " + targetSpecimen.getBarcode());
							}
							if (hasParameter) { 
								// find matching images, set first one as the display image.
								ICImageLifeCycle ils = new ICImageLifeCycle();
								List<ICImage> images = ils.findByExample(pattern);
								if (images!=null && images.size()>0) { 
									log.debug("Found: " + images.size());
									Iterator<ICImage> ii = images.iterator();
									boolean found = false;
									while (ii.hasNext() && !found) { 
										ICImage image = ii.next();
										if (image.getSpecimen()!=null  && !image.getSpecimen().getBarcode().equals(targetSpecimen.getBarcode())) { 
											// same filename, but wrong path.
											log.debug("WrongFile: " + image.getPath());
										} else { 
											found = true;
											//String startPointName = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
											String path = image.getPath();
											if (path == null) { path = ""; } 
											File fileToCheck = new File(ImageCaptureProperties.assemblePathWithBase(path, image.getFilename()));
											PositionTemplate defaultTemplate;
											try {
												defaultTemplate = PositionTemplate.findTemplateForImage(image);
												loadImagesFromFileSingle(fileToCheck, defaultTemplate, image);
											} catch (ImageLoadException e3) {
												log.error(e3);
											} catch (BadTemplateException e1) {
												log.error(e1);
											}
										}
									}
								}
							}
						} catch (NullPointerException e2) {
							// Probably means an empty jComboBoxImagePicker
							e2.printStackTrace();
							log.error(e2.getMessage(),e2);
						}
					}
				}
			});
		}
		return jComboBoxImagePicker;
	}

	private JButton getTemplatePicker() {
		if (templatePicker==null) { 
		    templatePicker = new JButton();
		    String template = "No Template Selected";
		    if (selectedImage!=null) { 
		       template = selectedImage.getTemplateId();
		    }
		    templatePicker.setText(template);
		    log.debug(template);
		    log.debug(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS);
		    if (template.equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) { 
		    	templatePicker.setEnabled(true);
		    } else { 
		    	templatePicker.setEnabled(false);
		    }
			    templatePicker.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						TemplatePickerDialog dialog = new TemplatePickerDialog(selectedImage);
						dialog.setVisible(true);
					} 
			    	
			    } );		    	

		}
		return templatePicker;
	}
	
	private void updateTemplateList() { 
		getTemplatePicker();
	    String template = "No Template Selected";
	    if (selectedImage!=null) { 
	       template = selectedImage.getTemplateId();
	    }
	    templatePicker.setText(template);	
	    if (template.equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) { 
	    	templatePicker.setEnabled(true);
	    } else { 
	    	templatePicker.setEnabled(false);
	    }
	}
	
	/**
	 * This method initializes jPanelImagesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelImagesPanel() {
		if (jPanelImagesPanel == null) {
			jPanelImagesPanel = new JPanel();
			jPanelImagesPanel.setLayout(new BorderLayout());
			jPanelImagesPanel.add(getJTabbedPane(), BorderLayout.CENTER);
			jPanelImagesPanel.add(getJPanelImagePicker(), BorderLayout.NORTH);
			if (UsersLifeCycle.isUserChiefEditor(Singleton.getSingletonInstance().getUser().getUserid())) { 
				jPanelImagesPanel.add(getTemplatePicker(), BorderLayout.SOUTH);
			}
		}
		return jPanelImagesPanel;
	}

	
	private JButton getBtnVerbatimtranscription() {
		if (btnVerbatimtranscription == null) {
			btnVerbatimtranscription = new JButton("VerbatimTranscription");
			btnVerbatimtranscription.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					VerbatimCaptureDialog dialog = new VerbatimCaptureDialog(targetSpecimen, targetSpecimenController);
					dialog.setVisible(true);
					setVisible(false);
				} 
				
			});
			btnVerbatimtranscription.setEnabled(false);
			if (targetSpecimen!=null && WorkFlowStatus.allowsVerbatimUpdate(targetSpecimen.getWorkFlowStatus())) { 
				btnVerbatimtranscription.setEnabled(true);
			}
		}
		return btnVerbatimtranscription;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
