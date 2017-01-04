/**
 * MCZBarcodePositionTemplateDetector.java
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import edu.harvard.mcz.imagecapture.interfaces.PositionTemplateDetector;

/** MCZBarcodePositionTemplateDetector find a template by the position of a barcode in an image file
 * without recourse to construction of a a CandidateImageFile instance, and checks for an MCZ catalog
 * number barcode in the templated position, not just any readable barcode.
 *   
 * This class makes the assumption that a template can be uniqely idenfified by the location of the
 * barcode in the image.  Each template must have the bardcode in a uniquely different place.
 * 
 * @author Paul J. Morris
 *
 * @see edu.harvard.mcz.imagecapture.PositionTemplate
 *
 */
public class MCZBarcodePositionTemplateDetector implements	PositionTemplateDetector {

	private static final Log log = LogFactory.getLog(MCZBarcodePositionTemplateDetector.class);
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.PositionTemplateDetector#detectTemplateForImage(java.io.File)
	 */
	@Override
	public String detectTemplateForImage(File anImageFile) throws UnreadableFileException {
		// Set default response if no template is found.
		String result = PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS;
		
		// Read the image file, if possible, otherwise throw exception.
		if (!anImageFile.canRead()) { 
			throw new UnreadableFileException("Unable to read " + anImageFile.getName());
		}
		
		BufferedImage image = null;
		try { 
		    image = ImageIO.read(anImageFile);
		} catch (IOException e) {
			throw new UnreadableFileException("IOException trying to read " + anImageFile.getName());
		}

		// iterate through templates and check until the first template where a barcode is found
		List<String> templates = PositionTemplate.getTemplateIds();
		ListIterator<String> i = templates.listIterator();
		boolean found = false;
		while (i.hasNext() && !found) {
			try {
				// get the next template from the list
				PositionTemplate template = new PositionTemplate((String)i.next());
				log.debug("Testing template: " + template.getTemplateId());
				if (template.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS))  { 
					// skip, this is the default result if no other is found.
				}  else { 
					if (image.getWidth()==template.getImageSize().getWidth()) { 
						// Check to see if the barcode is in the part of the template
						// defined by getBarcodeULPosition and getBarcodeSize.
						String text = CandidateImageFile.getBarcodeTextFromImage(image, template);
						log.debug("Found:[" + text + "] ");
						if (text.length()>0) {
							// a barcode was scanned 
							// Check to see if it matches the expected pattern.
							if (Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(text)) { 
							// if (text.matches(MCZENTBarcode.PATTERN)) { 
								found = true;
								log.debug("Match to:" + template.getTemplateId());
								result = template.getTemplateId();
							} 
						}
					} else { 
						log.debug("Skipping as template " + template.getTemplateId() + " is not same size as image. ");
					}
				}
			} catch (NoSuchTemplateException e) {
				// Ending up here means a serious error in PositionTemplate
				// as the list of position templates returned by getTemplates() includes
				// an entry that isn't recognized as a valid template.  
				log.fatal("Fatal error.  PositionTemplate.getTemplates() includes an item that isn't a valid template.");
				log.trace(e);
				ImageCaptureApp.exit(ImageCaptureApp.EXIT_ERROR);
			} 
		}
		return result;
	}

}
