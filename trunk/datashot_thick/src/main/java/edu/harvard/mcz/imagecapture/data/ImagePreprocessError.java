/**
 * ImagePreprocessError.java
 * edu.harvard.mcz.imagecapture.data
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
package edu.harvard.mcz.imagecapture.data;

import edu.harvard.mcz.imagecapture.interfaces.DrawerNameReturner;
import edu.harvard.mcz.imagecapture.interfaces.TaxonNameReturner;

/** ImagePreprocessError, a class for logging the details of multiple sorts of problems
 * that can be found in preproccessing an image.  Allows a report of preprocessing errors to be
 * displayed in a table.  Handles subtypes of errors with the TYPE_ constants.
 * 
 * @author Paul J. Morris
 *
 */
public class ImagePreprocessError {
	
	public static final int TYPE_SAVE_FAILED = 0;
	public static final int TYPE_NO_TEMPLATE = 1;
	public static final int TYPE_MISMATCH = 2;
	public static final int TYPE_BARCODE_MISSING_FROM_SEQUENCE = 3;
	public static final int TYPE_BAD_PARSE = 4;
	public static final int TYPE_DUPLICATE = 5;
	public static final int TYPE_FAILOVER_TO_OCR = 6;

	private String filename;
	private String barcode;
	private String qrBarcode;
	private String commentBarcode;
	private String errorMessage;
	private TaxonNameReturner taxonParser;
	private DrawerNameReturner drawerParser;
	private Exception exception;
	private int failureType;
	private String previous;
	private String previousPath;
	
	/**
	 * @param filename
	 * @param barcode
	 * @param qrBarcode
	 * @param commentBarcode
	 * @param errorMessage
	 * @param taxonParser
	 * @param drawerParser
	 * @param exception
	 * @param previous
	 */
	public ImagePreprocessError(String filename, String barcode,
			String qrBarcode, String commentBarcode, String errorMessage,
			TaxonNameReturner taxonParser, DrawerNameReturner drawerParser,
			Exception exception, int failureType, String previous, String aPreviousPath) {
		this.filename = filename;
		this.barcode = barcode;
		this.qrBarcode = qrBarcode;
		this.commentBarcode = commentBarcode;
		this.errorMessage = errorMessage;
		this.taxonParser = taxonParser;
		this.drawerParser = drawerParser;
		this.exception = exception;
		this.failureType = failureType;
		this.setPrevious(previous);
		this.setPreviousPath(aPreviousPath);
	}

	/**
	 * 
	 * @param filename
	 * @param barcode
	 * @param qrBarcode
	 * @param commentBarcode
	 * @param errorMessage
	 * @param taxonParser
	 * @param drawerParser
	 * @param exception
	 * @param failureType
	 */
	public ImagePreprocessError(String filename, String barcode,
			String qrBarcode, String commentBarcode, String errorMessage,
			TaxonNameReturner taxonParser, DrawerNameReturner drawerParser,
			Exception exception, int failureType) {
		this.filename = filename;
		this.barcode = barcode;
		this.qrBarcode = qrBarcode;
		this.commentBarcode = commentBarcode;
		this.errorMessage = errorMessage;
		this.taxonParser = taxonParser;
		this.drawerParser = drawerParser;
		this.exception = exception;
		this.failureType = failureType;
		this.setPrevious("");
	}	
	
	public String asString() {
		String result = "";
		String exceptionMessage = "";
		if (exception!=null) { 
			exceptionMessage = exception.getMessage();
		}
		switch (failureType) { 
		case TYPE_SAVE_FAILED: 
			result = getFailureType() + ":" + filename + " " + barcode + "\n" + errorMessage + "\n" + exceptionMessage;
			break;
		case TYPE_MISMATCH:
			result = getFailureType() + ":" + filename + " " + qrBarcode + " != " + commentBarcode;
			break;
		case TYPE_NO_TEMPLATE:
			result = getFailureType() + ":" + filename + " "  + qrBarcode + ", " + commentBarcode;
			break;
		case TYPE_BARCODE_MISSING_FROM_SEQUENCE:
			result = getFailureType() + ":" + barcode;
			break;
		}
		return result;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return the qrBarcode
	 */
	public String getQrBarcode() {
		return qrBarcode;
	}

	/**
	 * @param qrBarcode the qrBarcode to set
	 */
	public void setQrBarcode(String qrBarcode) {
		this.qrBarcode = qrBarcode;
	}

	/**
	 * @return the commentBarcode
	 */
	public String getCommentBarcode() {
		return commentBarcode;
	}

	/**
	 * @param commentBarcode the commentBarcode to set
	 */
	public void setCommentBarcode(String commentBarcode) {
		this.commentBarcode = commentBarcode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the taxonParser
	 */
	public TaxonNameReturner getTaxonParser() {
		return taxonParser;
	}

	/**
	 * @param taxonParser the taxonParser to set
	 */
	public void setTaxonParser(TaxonNameReturner taxonParser) {
		this.taxonParser = taxonParser;
	}

	/**
	 * @return the drawerParser
	 */
	public DrawerNameReturner getDrawerParser() {
		return drawerParser;
	}

	/**
	 * @param drawerParser the drawerParser to set
	 */
	public void setDrawerParser(DrawerNameReturner drawerParser) {
		this.drawerParser = drawerParser;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getFailureType() {
		String result = "";
		switch (failureType) { 
		case TYPE_SAVE_FAILED: 
			result = "Save Failed";
			break;
		case TYPE_MISMATCH:
			result = "Barcode Mismatch";
			break;
		case TYPE_NO_TEMPLATE:
			result = "No Template";
			break;
		case TYPE_BARCODE_MISSING_FROM_SEQUENCE:
			result = "Not Found";
			break;
		case TYPE_BAD_PARSE:
			result = "Parsing problem";
			break;		
		case TYPE_DUPLICATE:
			result = "Duplicate image record";
			break;			
		case TYPE_FAILOVER_TO_OCR:
			result = "Failover to OCR";
			break;				
		}
		return result;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPreviousPath(String previousPath) {
		this.previousPath = previousPath;
	}

	public String getPreviousPath() {
		return previousPath;
	}
	

}
