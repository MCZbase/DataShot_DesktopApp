/**
 * OCR.java
 * edu.harvard.mcz.imagecapture.interfaces
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
package edu.harvard.mcz.imagecapture.interfaces;

import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;

/**
 * Interface for a class that can deliver a string from the result
 * of some Optical Character Recognition operation, and may throw an
 * exception as a result of that operation.  
 * <BR>
 * This interface doesn't include a defined setOCRSource method, as this might
 * be a method or a constructor providing one of a variety of sources, and is
 * thus left up to the implementation.  Compare how ConvertTesseractOCR is constructed
 * and is used as a wrapper for TesseractOCR to see why the OCR interface was
 * defined this way.
 * 
 * @see edu.harvard.mcz.imagecapture.exceptions.OCRReadException
 * 
 * @see edu.harvard.mcz.imagecapture.ConvertTesseractOCR
 * @see edu.harvard.mcz.imagecapture.TesseractOCR
 * 
 * @author Paul J. Morris
 *
 */
public interface OCR {
	
	/**
	 * 
	 * @return the text from the OCR source.
	 * @throws OCRReadException 
	 */
	public String getOCRText() throws OCRReadException;

}
