/**
 * PositionTemplate.java
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

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.Template;
import edu.harvard.mcz.imagecapture.data.TemplateLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.BadTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.ImageLoadException;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import edu.harvard.mcz.imagecapture.interfaces.PositionTemplateDetector;

/** Description of the coordinates within an image where parts of the image to pass to 
 * barcode reading software, OCR software, and which parts are to be displayed as containing
 * a specimen image or specimen labels.   Wrapper for persistent Template class.  Provides special
 * case template TEMPLATE_NO_COMPONENT_PARTS for handling any size image that isn't templated.  
 * Provides hard coded default template.  Provides methods to get/set coordinates as Dimension 
 * objects as a convenience over Template's get/set of individual heights and widths as integers.
 * <BR>
 * DefaultPositionTemplateDetector makes the assumption that a template can be uniquely identified 
 * by the location of the barcode in the image.  
 * <BR>
 * Each template must have the barcode in a uniquely different place.
 * 
 * @author Paul J. Morris
 */
public class PositionTemplate {
	
	private static final Log log = LogFactory.getLog(PositionTemplate.class);
	
	/** Special case template for images that aren't split into component parts
	 * with a template.
	 * @see edu.harvard.mcz.imagecapture.exceptions.NoComponentPartsTemplateException  
	 */
	public static final String TEMPLATE_NO_COMPONENT_PARTS = "Whole Image Only";
	
	/** The hardcoded default template. */
	public static final String TEMPLATE_DEFAULT = "Default template";
	public static final String TEMPLATE_TEST_1 = "Small Template 1";

	private Dimension imageSize;        // Pixel dimensions of the image
	private Dimension barcodePosition;  // Location of upper left corner of area to scan for barcode in image.
	private Dimension barcodeSize;      // Size of area in image to scan for barcode.
	private Dimension specimenPosition; // Location of upper left corner of part of image containing specimen.
	private Dimension specimenSize;
	private Dimension textPosition;     // Unit tray label with current determination
	private Dimension textSize;
	private Dimension labelSize;        // Specimen labels from pin.
	private Dimension labelPosition;
	private Dimension utLabelSize;      // Specimen labels from unit tray.
	private Dimension utLabelPosition;
	private Dimension utBarcodeSize;    // Barcode on UnitTrayLabel
	private Dimension utBarcodePosition;
	private ICImage referenceImage;      // Filename of reference image for this template.
	private String templateName;        // free text description of the template
	private String templateId;          // the identifying string for this template = TEMPLATE_ constants
	private boolean isEditable;         // false for TEMPLATE_s, true for DB records.
	
	/** Fetch the list of valid template names (including the no component parts template).  
	 * Use these names in the constructor PositionTemplate(String templateToUse);   
	 * 
	 * @return a list of the identifiers of the currently available templates.
	 */
	public static List<String> getTemplateIds() { 
		String[] templates = { TEMPLATE_TEST_1, TEMPLATE_DEFAULT, TEMPLATE_NO_COMPONENT_PARTS  };
		
		List<String> temp = Arrays.asList(templates);
		ArrayList<String> templateIdList = new ArrayList<String>();
		for (int i=0; i<temp.size(); i++) { 
			templateIdList.add(temp.get(i));
		}
		TemplateLifeCycle tls = new TemplateLifeCycle();
		List<Template> persistentTemplates = tls.findAll();
		if (persistentTemplates==null) { 
			tls.cleanUpReferenceImage();
			persistentTemplates = tls.findAll();
		}
		ListIterator<Template> iter = persistentTemplates.listIterator();
		while (iter.hasNext()) { 
			templateIdList.add(iter.next().getTemplateId());
		}
		return templateIdList;
	}
	
	public static List<PositionTemplate> getTemplates() { 
		//TemplateLifeCycle tls = new TemplateLifeCycle();
		//List<Template> templates = tls.findAll();
		ArrayList<PositionTemplate> results = new ArrayList<PositionTemplate>();
		List<String> templateIds = PositionTemplate.getTemplateIds();
		ListIterator<String> i = templateIds.listIterator();
		while (i.hasNext()) { 
			try {
				results.add(new PositionTemplate(i.next()));
			} catch (NoSuchTemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return results;
	}
	
	/**
	 * Use a (currently hardcoded) default template for which pixel coordinates on the 
	 * image contain which information. 
	 */
	public PositionTemplate() { 
       useDefaultValues();
	}
	
	/**
	 * Create a new (probably editable) template based on the default template for which pixel coordinates on the 
	 * image contain which information. 
	 * 
	 * @param editable true for an editable template.
	 */
	public PositionTemplate(boolean editable) { 
         useDefaultValues();
         isEditable = editable; 
	}
	
	/** Use a template defined by a PositionTemplate.TEMPLATE_* constant or potentially from
	 * another valid source.  A template defines which pixel coordinates on the image contain 
	 * which information.   The template cannot be changed for an instance of PositionTemplate once
	 * it has been instantiated.  This constructor is the only means of setting the template to use.  
	 * Create a new instance of PositionTemplate if you wish to use a different template.
	 * 
	 * The list of available templateIds can be retrieved with PositionTemplate.getTemplates().
	 * @see edu.harvard.mcz.imagecapture.PositionTemplate#getTemplateIds()
	 * 
	 * @param templateToUse the templateID of the template to use in this instance of PositionTemplate.
	 * @throws NoSuchTemplateException when templateToUse doesn't exist.
	 * @see edu.harvard.mcz.imagecapture.exceptions.NoComponentPartsTemplateException
	 */
	public PositionTemplate(String templateToUse) throws NoSuchTemplateException {
		boolean found = false;
		if (templateToUse.equals(TEMPLATE_DEFAULT)) { 
			useDefaultValues();
			found = true;
		}
		found = loadTemplateValues(templateToUse);
//		if (templateToUse.equals(PositionTemplate.TEMPLATE_TEST_1)) { 
//			loadTemplateValues(templateToUse);
//			found = true;
//		}		
		if (!found) { 
			throw new NoSuchTemplateException("No such template as " + templateToUse );
		}
	}
	
	/** Construct a PositionTemplate from a Template.
	 * 
	 * @param templateInstance
	 */
	public PositionTemplate(Template templateInstance) {
	   	templateName = templateInstance.getName();
	   	templateId = templateInstance.getTemplateId();
	   	imageSize = new Dimension(templateInstance.getImageSizeX(),templateInstance.getImageSizeY());
	   	barcodePosition = new Dimension(templateInstance.getBarcodePositionX(),templateInstance.getBarcodePositionY());
	   	barcodeSize = new Dimension(templateInstance.getBarcodeSizeX(),templateInstance.getBarcodeSizeY());
	   	specimenPosition = new Dimension(templateInstance.getSpecimenPositionX(),templateInstance.getSpecimenPositionY());
	   	specimenSize = new Dimension(templateInstance.getSpecimenSizeX(),templateInstance.getSpecimenSizeY());
	   	labelPosition = new Dimension(templateInstance.getLabelPositionX() , templateInstance.getLabelPositionY());
	   	labelSize = new Dimension(templateInstance.getLabelSizeX(), templateInstance.getLabelSizeY());
	   	utLabelPosition = new Dimension(templateInstance.getUtLabelPositionX(), templateInstance.getUtLabelPositionY());
	   	utLabelSize = new Dimension(templateInstance.getUtLabelSizeX(), templateInstance.getUtLabelSizey());
	   	utBarcodePosition = new Dimension(templateInstance.getUtBarcodePositionX(), templateInstance.getUtBarcodePositionY());
	   	utBarcodeSize = new Dimension(templateInstance.getUtBarcodeSizeX(), templateInstance.getUtBarcodeSizeY());
	   	textPosition = new Dimension(templateInstance.getTextPositionX() , templateInstance.getTextPositionY());
	   	textSize = new Dimension(templateInstance.getTextSizeX(), templateInstance.getTextSizeY());
	}
	
	/**
	 * Hardcoded default template values are defined here.
	 */
	private void useDefaultValues() {
		templateName = "Default template for initial test image.";
		templateId = PositionTemplate.TEMPLATE_DEFAULT;
		// Set default values
		imageSize = new Dimension(2848,4272);
		// Approximately 300 x 300 pixel area in upper right corner of 12 mega-pixel image.
		barcodePosition = new Dimension(2490,90);
		barcodeSize = new Dimension (300,300);
		// lower half of image
		specimenPosition = new Dimension(0,2200);
		specimenSize = new Dimension(2848,1900);
	    // text to ocr at top left
		textPosition = new Dimension(110,105);
		textSize = new Dimension(1720,700);
		// pin labels on right above half
		labelPosition = new Dimension(1300,700);
		labelSize = new Dimension(1500,1300);
		// labels on left above half
		utLabelPosition = new Dimension(0,850);    // not defined as different from labels in test image
		utLabelSize = new Dimension(1500,1161);
		// QRCode barcode on unit tray label encoding unit tray label fields.
		utBarcodePosition = new Dimension(1200,105);    
		utBarcodeSize = new Dimension(950,800);			
		referenceImage = null;
		isEditable = false;
	}
	
	/** Get the identifying name of this position template.  This name is fixed for an instance 
	 * during its construction.   This name corresponds to one of the strings returned by 
	 * PositionTemplate.getTemplates();   Redundant with getTemplateId()
	 * 
	 * @return the identifier of the template in use in this instance of PositionTemplate.
	 * @see edu.harvard.mcz.imagecapture.PositionTemplate#getTemplateIds()  
	 * @see edu.harvard.mcz.imagecapture.PositionTemplate#getTemplateId() 
	 * @deprecated
	 */
	public String getTemplateIdentifier() { 
		return getTemplateId();
	}
	
	/** Get the free text descriptive name of the position template for potential display to a person.
	 * Use getTemplateIdentifier() to identify templates in code.  
	 * 
	 * @return the descriptive name of the template.
	 */
	public String getName() { 
		return templateName;
	}
	
	public Dimension getImageSize() { 
		return imageSize;
	}
	
	public Dimension getBarcodeULPosition() { 
		return barcodePosition;
	}
	
	public Dimension getBarcodeSize() { 
		return barcodeSize;
	}
	
	//TODO: add define/save templates
	
	//TODO: add retrieval of template from file. 

	/**
	 * @return the barcodePosition
	 */
	public Dimension getBarcodePosition() {
		return barcodePosition;
	}


	/**
	 * @return the specimenPosition
	 */
	public Dimension getSpecimenPosition() {
		return specimenPosition;
	}


	/**
	 * @return the specimenSize
	 */
	public Dimension getSpecimenSize() {
		return specimenSize;
	}


	/**
	 * @return the textPosition
	 */
	public Dimension getTextPosition() {
		return textPosition;
	}


	/**
	 * @return the textSize
	 */
	public Dimension getTextSize() {
		return textSize;
	}


	/**
	 * @return the labelSize
	 */
	public Dimension getLabelSize() {
		return labelSize;
	}


	/**
	 * @return the labelPosition
	 */
	public Dimension getLabelPosition() {
		return labelPosition;
	}
	
	/**
	 * @return the labelSize
	 */
	public Dimension getUTLabelsSize() {
		return utLabelSize;
	}


	/**
	 * @return the labelPosition
	 */
	public Dimension getUTLabelsPosition() {
		return utLabelPosition;
	}	      
	
	/** private method, to use a different template, instantiate a new PositionTemplate.  
	 * Contains hardcoded templates other than the default
	 * 
	 * TODO: Extend to external persistent templates that aren't hard coded.
	 * 
	 * @param templateToUse
	 * @return true if the template was found.
	 */
	private boolean loadTemplateValues(String templateToUse)  {
		boolean found = false;
		if (templateToUse.equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) { 
			templateName = "Whole image only.";
			templateId = templateToUse;
			// Set all values to null
			imageSize = null;
			barcodePosition = null;
			barcodeSize = null;
			specimenPosition = null;
			specimenSize = null;
			textPosition = null;
			textSize = null;
			labelPosition = null;
			labelSize = null;
			utLabelPosition = null;
			utLabelSize = null;
			utBarcodePosition = null;
			utBarcodeSize = null;
		    isEditable = false;
		    referenceImage = null;
			found = true;
		}
		if (templateToUse.equals(PositionTemplate.TEMPLATE_TEST_1)) {
			templateName = "Cannon DigitalRebel L with small test carrier.";
			templateId = templateToUse;
			// Set default values
			imageSize = new Dimension(2848,4272);
			// Approximately 300 x 300 pixel area in upper right corner of 12 mega-pixel image.
			barcodePosition = new Dimension(2280,0);
			barcodeSize = new Dimension (550,310);
			// lower half of image
			specimenPosition = new Dimension(0,2140);
			specimenSize = new Dimension(2847,2130);
		    // text to ocr at top left of image
			textPosition = new Dimension(110,105);
			textSize = new Dimension(1720,700);
			// pin labels on right side of upper half
			labelPosition = new Dimension(1500,780);
			labelSize = new Dimension(1348,1360);
			// unit tray labels on left side of upper half
			utLabelPosition = new Dimension(0,780);
			utLabelSize = new Dimension(1560,1360);
			// QRCode barcode on unit tray label encoding unit tray label fields.
			utBarcodePosition = new Dimension(1200,105);    
			utBarcodeSize = new Dimension(950,800);				
			isEditable = false;
			referenceImage = null;
			found = true;
		}
		if (templateToUse.equals(TEMPLATE_DEFAULT)) { 
			useDefaultValues();
            isEditable = false;
			found = true;
		}
		if (!found) { 
			found = loadByTemplateId(templateToUse);
		}
		return found;
	}
	
	public boolean loadByTemplateId(String aTemplateId) { 
	    boolean result = false;
		Template t_result = null;
		TemplateLifeCycle tls = new TemplateLifeCycle();
		t_result = tls.findById(aTemplateId);
		if (t_result!=null) { 
			// We know this is a valid template, so set the id
			templateId = aTemplateId;			
			// retrieve the rest of the values
			templateName = t_result.getName();
			imageSize = new Dimension(t_result.getImageSizeX(), t_result.getImageSizeY());
			barcodePosition = new Dimension (t_result.getBarcodePositionX(), t_result.getBarcodePositionY());
			barcodeSize = new Dimension (t_result.getBarcodeSizeX(), t_result.getBarcodeSizeY());
			specimenPosition = new Dimension(t_result.getSpecimenPositionX(), t_result.getSpecimenPositionY());
			specimenSize = new Dimension(t_result.getSpecimenSizeX(), t_result.getSpecimenSizeY());
			textPosition = new Dimension(t_result.getTextPositionX(), t_result.getTextPositionY());
			textSize = new Dimension(t_result.getTextSizeX(), t_result.getTextSizeY());
			labelPosition = new Dimension(t_result.getLabelPositionX(), t_result.getLabelPositionY());
			labelSize = new Dimension(t_result.getLabelSizeX(), t_result.getLabelSizeY());
			utLabelPosition = new Dimension(t_result.getUtLabelPositionX(), t_result.getUtLabelPositionY());
			utLabelSize = new Dimension(t_result.getUtLabelSizeX(), t_result.getUtLabelSizey());
			utBarcodePosition = new Dimension(t_result.getUtBarcodePositionX(), t_result.getUtBarcodePositionY());
			utBarcodeSize = new Dimension(t_result.getUtBarcodeSizeX(), t_result.getUtBarcodeSizeY());
			isEditable = true;
			referenceImage = t_result.getReferenceImage();
			result = true;
		}
		return result;
	}
	
	public void populateTemplateFromPositionTemplate(Template templateInstance) throws BadTemplateException { 
		if (templateId==null || templateId.trim().equals("")) { 
			throw new BadTemplateException("Can't save a template with a blank templateID");
		}
		if (imageSize==null) { 
			// Note: if persistence of TEMPLATE_NO_COMPONENT_PARTS is desired, this 
			// needs to be changed to test for that template.
			throw new BadTemplateException("Can't save a template with no image size.");
		} 
		if (templateInstance==null) { 
			throw new BadTemplateException("Can't save a null template.");
		}
		templateInstance.setTemplateId(templateId);
		templateInstance.setName(templateName);
		templateInstance.setEditable(isEditable);
	    templateInstance.setReferenceImage(referenceImage);
		templateInstance.setImageSizeX(imageSize.width);
		templateInstance.setImageSizeY(imageSize.height);
		templateInstance.setBarcodePositionX(barcodePosition.width);
		templateInstance.setBarcodePositionY(barcodePosition.height);
		templateInstance.setBarcodeSizeX(barcodeSize.width);
		templateInstance.setBarcodeSizeY(barcodeSize.height);
		templateInstance.setSpecimenPositionX(specimenPosition.width);
		templateInstance.setSpecimenPositionY(specimenPosition.height);
		templateInstance.setSpecimenSizeX(specimenSize.width);
		templateInstance.setSpecimenSizeY(specimenSize.height);
		templateInstance.setTextPositionX(textPosition.width);
		templateInstance.setTextPositionY(textPosition.height);
		templateInstance.setTextSizeX(textSize.width);
		templateInstance.setTextSizeY(textSize.height);
		templateInstance.setLabelPositionX(labelPosition.width);
		templateInstance.setLabelPositionY(labelPosition.height);
		templateInstance.setLabelSizeX(labelSize.width);
		templateInstance.setLabelSizeY(labelSize.height);
		templateInstance.setUtLabelPositionX(utLabelPosition.width);
		templateInstance.setUtLabelPositionY(utLabelPosition.height);
		templateInstance.setUtLabelSizeX(utLabelSize.width);
		templateInstance.setUtLabelSizey(utLabelSize.height);
		templateInstance.setUtBarcodePositionX(utBarcodePosition.width);
		templateInstance.setUtBarcodePositionY(utBarcodePosition.height);
		templateInstance.setUtBarcodeSizeX(utBarcodeSize.width);
		templateInstance.setUtBarcodeSizeY(utBarcodeSize.height);		
	}	
	
	public void persist() throws BadTemplateException, SaveFailedException { 
		if ((templateId==null) || templateId.trim().equals("")) { 
			throw new BadTemplateException("Can't save a template with a blank templateID");
		}
		if (imageSize==null) { 
			// Note: if persistence of TEMPLATE_NO_COMPONENT_PARTS is desired, this 
			// needs to be changed to test for that template.
			throw new BadTemplateException("Can't save a template with no image size.");
		}
		TemplateLifeCycle tls = new TemplateLifeCycle();
		Template templateInstance = tls.findById(templateId); 
		if (templateInstance==null) { 
			templateInstance = new Template();
		}
		populateTemplateFromPositionTemplate(templateInstance);
		
		// save/update
		tls.attachDirty(templateInstance);

	}
	
	/**
	 * 
	 * @return true if this PositionTemplate can be edited by the user.
	 */
	public boolean isEditable() { 
		return isEditable;
	}

	/**
	 * @return a String representing the filename of the referenceImage for this
	 * PoistionTemplate.
	 */
	public ICImage getReferenceImage() {
		return referenceImage;
	}
	
	public void setReferenceImage(ICImage anImage) { 
		referenceImage = anImage;
	}
	
	public String getReferenceImageFilePath() { 
		String result = "";
		String base = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
		if (referenceImage!=null && referenceImage.getPath()!=null && referenceImage.getFilename()!=null) { 
		   result = base + referenceImage.getPath() + referenceImage.getFilename();
		}
		return result;
	}
	
	/** Given an ICImage, look up the template for that image in the database, if none, run a template detector
	 * to determine the template for the image. 
	 * 
	 * @param image the ICImage for which the PositionTemplate is to be returned.
	 * @return the PositionTemplate for this image indicating what information is where in the image.  
	 * @throws ImageLoadException
	 */
	public static PositionTemplate findTemplateForImage(ICImage image) throws ImageLoadException  {
		PositionTemplate result = null;
		//TODO: stored path may need separator conversion for different systems. 
		//String startPointName = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
		String path = image.getPath();
		if (path == null) { path = ""; } 
		//File fileToCheck = new File(startPointName + path + image.getFilename());
		File fileToCheck = new File(ImageCaptureProperties.assemblePathWithBase(path, image.getFilename()));
		String templateId = image.getTemplateId();
		if (templateId==null || templateId.equals("")) { 
			// No template is defined in the database for this image file.
			// Check the image with a template detector.
			PositionTemplateDetector detector = new DefaultPositionTemplateDetector();
			try {
				templateId = detector.detectTemplateForImage(fileToCheck);
			} catch (UnreadableFileException e) {
				throw new ImageLoadException(e.getMessage());
			}
		} 
		// There is a template defined in the database for this image file
		// Check to see if this is a valid template.
		try {
			result = new PositionTemplate(templateId);
			// Template exists, load image with it. 
		} catch (NoSuchTemplateException e) {
			// This template isn't known.  Log the problem.
			log.error("Image database record includes an unknown template. " + e.getMessage());
			// Use the no component parts template instead.
			try {
				result = new PositionTemplate(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS);
			} catch (NoSuchTemplateException e1) {
				log.fatal("TEMPLATE_NO_COMPONENT_PARTS produced a NoSuchTemplateException.");
				log.fatal(e1);
				log.trace(e1);
				ImageCaptureApp.exit(ImageCaptureApp.EXIT_ERROR);
			} 
		}
		return result;
	}

//	/**
//	 * @return the utLabelSize
//	 */
//	public Dimension getUtLabelSize() {
//		return utLabelSize;
//	}
//
	/**
	 * @param utLabelSize the utLabelSize to set
	 */
	public void setUTLabelsSize(Dimension utLabelSize) {
		this.utLabelSize = utLabelSize;
	}

//	/**
//	 * @return the utLabelPosition
//	 */
//	public Dimension getUtLabelPosition() {
//		return utLabelPosition;
//	}

	/**
	 * @param utLabelPosition the utLabelPosition to set
	 */
	public void setUTLabelsPosition(Dimension utLabelPosition) {
		this.utLabelPosition = utLabelPosition;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/** Get the identifying name of this position template.  This name is fixed for an instance 
	 * during its construction.   This name corresponds to one of the strings returned by 
	 * PositionTemplate.getTemplates();  Redundant with getTemplateIdentifier().
	 * 
	 * @return the identifier of the template in use in this instance of PositionTemplate.
	 * @see edu.harvard.mcz.imagecapture.PositionTemplate#getTemplateIds()  
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @param imageSize the imageSize to set
	 */
	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}

	/**
	 * @param barcodePosition the barcodePosition to set
	 */
	public void setBarcodePosition(Dimension barcodePosition) {
		this.barcodePosition = barcodePosition;
	}

	/**
	 * @param barcodeSize the barcodeSize to set
	 */
	public void setBarcodeSize(Dimension barcodeSize) {
		this.barcodeSize = barcodeSize;
	}

	/**
	 * @param specimenPosition the specimenPosition to set
	 */
	public void setSpecimenPosition(Dimension specimenPosition) {
		this.specimenPosition = specimenPosition;
	}

	/**
	 * @param specimenSize the specimenSize to set
	 */
	public void setSpecimenSize(Dimension specimenSize) {
		this.specimenSize = specimenSize;
	}

	/**
	 * @param textPosition the textPosition to set
	 */
	public void setTextPosition(Dimension textPosition) {
		this.textPosition = textPosition;
	}

	/**
	 * @param textSize the textSize to set
	 */
	public void setTextSize(Dimension textSize) {
		this.textSize = textSize;
	}

	/**
	 * @param labelSize the labelSize to set
	 */
	public void setLabelSize(Dimension labelSize) {
		this.labelSize = labelSize;
	}

	/**
	 * @param labelPosition the labelPosition to set
	 */
	public void setLabelPosition(Dimension labelPosition) {
		this.labelPosition = labelPosition;
	}

	public void setUtBarcodeSize(Dimension utBarcodeSize) {
		this.utBarcodeSize = utBarcodeSize;
	}

	public Dimension getUtBarcodeSize() {
		return utBarcodeSize;
	}

	public void setUtBarcodePosition(Dimension utBarcodePosition) {
		this.utBarcodePosition = utBarcodePosition;
	}

	public Dimension getUtBarcodePosition() {
		return utBarcodePosition;
	}

}
