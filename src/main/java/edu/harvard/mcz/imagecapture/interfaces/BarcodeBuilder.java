/**
 * BarcodeBuilder.java
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
package edu.harvard.mcz.imagecapture.interfaces;

/** BarcodeBuilder, and interface for retrieving the formatted text found in a barcode label given 
 * the numeric part of that barcode text.  Only concerns the text string found in a barcode, not 
 * the actual barcode encoding of that text.
 * 
 * @author Paul J. Morris
 *
 */
public interface BarcodeBuilder {



	/**
	 * Make a formated string (e.g. "MCZ-ENT01234567") in the form of a barcode number 
	 * when given a number (e.g. 1234567) to include in that formatted string.   
	 * 
	 * @param aNumber
	 * @return
	 */
	public String makeFromNumber(Integer aNumber);
	
	public String makeGuidFromBarcode(String barcode);

}