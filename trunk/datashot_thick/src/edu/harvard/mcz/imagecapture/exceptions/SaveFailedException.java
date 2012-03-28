/** SaveFailedException
 *  edu.harvard.mcz.imagecapture.exceptions
 *  Copyright © 2009 President and Fellows of Harvard College
 *  @author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture.exceptions;

/**
 * SaveFailedException can be thrown when an attempt to persist an instance of a class
 * as a database record fails.
 * 
 * @author Paul J. Morris
 *
 */
public class SaveFailedException extends Exception {

	private static final long serialVersionUID = -4963776101118122744L;

	/**
	 * 
	 */
	public SaveFailedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SaveFailedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SaveFailedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SaveFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
	
}
