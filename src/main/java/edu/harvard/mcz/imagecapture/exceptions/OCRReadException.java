/**
 *  Copyright © 2009 President and Fellows of Harvard College
 *  @author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture.exceptions;

/**
 * OCRReadException can be thrown when an implementation of the OCR interface fails 
 * to OCR text from an image.
 * 
 * @see edu.harvard.mcz.imagecapture.interfaces.OCR
 * @author Paul J. Morris
 *
 */
public class OCRReadException extends Exception {

	private static final long serialVersionUID = -2129432968655902781L;

	/**
	 * default constructor
	 */
	public OCRReadException() {
		super("OCRReadException");
	}

	/**
	 * @param message
	 */
	public OCRReadException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public OCRReadException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OCRReadException(String message, Throwable cause) {
		super(message, cause);
	}

}
