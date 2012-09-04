/**
 * ConvertTesseractOCR.java 
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
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;

import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;
import edu.harvard.mcz.imagecapture.interfaces.OCR;

/**Takes a buffered image, generates a TIFF file from it with a call
 * out to ImageMagick or GraphicsMagick convert, then passes that tiff
 * file on to TesseractOCR for Tesseract to OCR the tiff tile.
 * <br>
 * Usage:
 * <pre>
     ConvertTesseractOCR ocr = new ConvertTesseractOCR(aBufferedImage);
     String result = ocr.getOCRText();
   </pre>
 * 
 * @author Paul J. Morris
 *
 */
public class ConvertTesseractOCR implements OCR {

	private BufferedImage subimage = null;
	private boolean debug = false;
	
	public ConvertTesseractOCR(BufferedImage anImageToOCR) { 
		subimage = anImageToOCR;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.OCR#getOCRText()
	 */
	@Override
	public String getOCRText() throws OCRReadException {
		String returnValue = "";
		// write to tiff file for ocr
	    ImageTypeSpecifier typeSpecifier = new ImageTypeSpecifier(subimage);
	    Iterator<ImageWriter> iter = ImageIO.getImageWriters(typeSpecifier, "tif");
	    if (iter.hasNext()) { 
	    	File tifffile = new File("temptiff.tif");
	    	try { 
    		    ImageIO.write(subimage, "tif", tifffile);
	    	} catch (IOException e) { 
    			System.out.println(e.getMessage());
    			throw new OCRReadException("Native tiff file creation failed." +  e.getMessage());
    		}
	    } else { 
	        // Print out list of supported write formats
	    	if (debug) { 
	           String [] formatNames = ImageIO.getWriterFormatNames();
	           for (int i=0; i<formatNames.length;i++) { 
	                System.out.println("Write: " + formatNames[i]);
	           }
	    	} 
	    	
	    	System.out.println("No tiff image writer.");
	    	System.out.println("trying convert from jpg");
	    	File jpgFile = new File("temptiff.jpg");
    		try {
				ImageIO.write(subimage, "jpg", jpgFile);
	            // Run imagemagick to convert the target file to TIFF for 
	            //String runCommand = "convert temptiff.jpg -depth 8 temptiff.tif"; 
	    		String runCommand = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_CONVERT_EXECUTABLE) 
	    		    + " temptiff.jpg " 
	    			+ Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_CONVERT_PARAMETERS) 
	    			+ " temptiff.tif"; 
	            Runtime r = Runtime.getRuntime();
	            Process proc = r.exec(runCommand);
	            System.out.println(runCommand);
	            int exitVal = proc.waitFor();
	            if (exitVal>0) { 
	            	throw new OCRReadException("ImageMagick temporary TIFF file creation failed.");	
	            }
			} catch (IOException e) {
    			System.out.println(e.getMessage());
    			throw new OCRReadException("Native temporary jpeg file creation failed. " +  e.getMessage());
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
    			throw new OCRReadException("Call to ImageMagick failed." +  e.getMessage());
			}


		    // OCR and parse UnitTray Label
        	TesseractOCR o = new TesseractOCR();
        	// TODO: pull out labelbit to tiff for scanning
        	try {
				o.setTarget("temptiff.tif");
			} catch (OCRReadException e1) {
				System.out.println(e1.getMessage());
				e1.printStackTrace();
			}
        	String rawOCR = "";
        	try {
				rawOCR = o.getOCRText();
				returnValue = rawOCR;
			} catch (OCRReadException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
            
	    }
	    return returnValue; 
	}

	/**Command line test for this class.
	 * @param args will take first argument as path and name of file, 
	 * will extract a portion of this image to test using the default 
	 * position template.
	 * @see edu.harvard.mcz.imagecapture.PositionTemplate
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String target = "/home/mole/stuff/MCZ/mcz/insects/testImages/base/photo_plate_test_good_4_template.JPG";
		if (args.length==1) { 
			target = args[0];
		}
		File targetFile = new File(target);
		PositionTemplate defaultTemplate = new PositionTemplate();
		BufferedImage imagefile;
		try {
			imagefile = ImageIO.read(targetFile);
	        int x = defaultTemplate.getTextPosition().width;    
    		int y =  defaultTemplate.getTextPosition().height; 
    		int w = defaultTemplate.getTextSize().width;  
    		int h = defaultTemplate.getTextSize().height;
    		
    		ConvertTesseractOCR converter = new ConvertTesseractOCR(imagefile.getSubimage(x, y, w, h));
    	
    		try {
				String output = converter.getOCRText();
				System.out.println(output);
			} catch (OCRReadException e) {
				System.out.println("OCR Failed." + e.getMessage());
			}
		} catch (IOException e1) {
			System.out.println("Couldn't read imagefile " + target +  "." + e1.getMessage());
		}		

	}

}
