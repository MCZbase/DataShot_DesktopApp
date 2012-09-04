/** 
 * TesseractOCR.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;
import edu.harvard.mcz.imagecapture.interfaces.OCR;

/**Uses a system runtime call to tesseract to obtain OCR text from a TIFF image file.
 * <BR> 
 * Usage: 
 * <pre>
   String output = null; 
   try { 
      TesseractOCR t = new TesseractOCR();
      t.setTarget("filenameToOCR.tif");
      output = t.getOCRText();
   } catch (OCRReadException e) { 
      // handle exception
   }   
   </pre>
 * 
 * @author Paul J. Morris
 *
 */
public class TesseractOCR implements OCR {
	
	private String target = "/home/mole/stuff/MCZ/mcz/insects/testImages/text.tif";
	private String tempFileBase = "tempOCRtextOutput";
    //private String language = "-l spa";
	private String language = "-l lepid";  // Lepidoptera training set
	//private String language = "-l eng";
	
	private static final Log log = LogFactory.getLog(TesseractOCR.class);
	
	/**Sets the name of the TIFF image file from which tesseract is to try to 
	 * extract text.
	 * 
	 * @param aTargetFile filename, including path, of the file that is to be OCRed.
	 * @throws OCRReadException if the file cannot be read.
	 */
	public void setTarget(String aTargetFile) throws OCRReadException {
		if (new File(aTargetFile).canRead()) { 
			target = aTargetFile; 		
		} else { 
			throw new OCRReadException("Can't read file: " + aTargetFile);
		}
	}

	/**
	 * Set the language to use with Tesseract, use "eng" for english, passed
	 * to tesseract as "-l lang".  Represents the leading string for the tesseract
	 * training files (e.g. eng for eng.normproto, eng.word-dawg, etc).  Default is "lepid"
	 * for custom scientific name/author training files.
	 * 
	 * @param lang
	 */
	public void setLang(String lang) { 
		language = "-l " + lang;
	}
	
	public String getOCRText(String lang) throws OCRReadException {
		setLang(lang);
		return getOCRText();
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.OCR#getOCRText()
	 */
	@Override
	public String getOCRText() throws OCRReadException {
		StringBuffer result = new StringBuffer();
		BufferedReader resultReader = null;
		try
		{            
			Runtime r = Runtime.getRuntime();

			// Run tesseract to OCR the target file.
			String runCommand = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_TESSERACT_EXECUTABLE)   
			+ target + " " + tempFileBase + " " +  language;
			Process proc = r.exec(runCommand);
			System.out.println(runCommand);

			// Create and start a reader for the error stream
			StreamReader errorReader = new StreamReader(proc.getErrorStream(), "ERROR");            
			errorReader.run();

			// run the process and wait for the exit value
			int exitVal = proc.waitFor();

			System.out.println("Tesseract Exit Value: " + exitVal);

			if (exitVal==0) { 
				resultReader = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(tempFileBase+".txt"),
								"UTF-8")
						);
				String s;
				while ((s = resultReader.readLine()) != null) {  
					result.append(s);
					result.append("\n");
				}
				resultReader.close();
			} else {
				throw new OCRReadException("OCR process failed (exit value>0).");
			}
			
			if (result.length() > 0) { 
				String resString = result.toString();
				// $ is returned by tesseract to indicate italics
				// @ is returned by tesseract to indicate bold
				// | and _ are common interpretations of QR barcode blocks
				result = new StringBuffer();
				result.append(resString.replaceAll("[|@\\$_]", ""));
			}
			
		} catch (IOException eio)   {
			log.error(eio);
			throw new OCRReadException("OCR process failed with IO Exception.");
		} catch (InterruptedException ei) {
			log.error(ei);
			throw new OCRReadException("OCR process failed with Interrupted Exception.");
		} finally { 
			if (resultReader!=null) { 
				try {
					resultReader.close();
				} catch (IOException e) {
					log.debug(e);
				} 
			}   
		}

		log.debug(result.toString());
		return result.toString();
	}

	/**Run TesseractOCR from the command line to OCR a single file, intended as a test of installation.
	 * Displays the content of the tesseract output file to standard output.  
	 * Could be used as a shell wrapper to run tesseract and put the results on standard output to be 
	 * piped elsewhere.
	 * 
	 * @param args name of the TIFF file to try to OCR.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("java TesseractOCR <inputfilename> [<lang>]");
            System.exit(1);
        } else { 
        	TesseractOCR o = new TesseractOCR();
        	try {
        		if (args.length>1) { 
        			o.setLang(args[1]);
        		}
				o.setTarget(args[0]);
				System.out.println(o.getOCRText());
				System.exit(0);
			} catch (OCRReadException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(2);
			}
        }
		
		

	}

}
