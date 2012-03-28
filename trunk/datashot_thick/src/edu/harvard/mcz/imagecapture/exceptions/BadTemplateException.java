/**
 * BadTemplateException.java
 * edu.harvard.mcz.imagecapture.exceptions
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
package edu.harvard.mcz.imagecapture.exceptions;

/** BadTemplateException to be thrown when a template does not fit an image file to which it is being applied.  
 * This exception should be thrown when there is a problem applying a template, as opposed to NoSuchTemplateException 
 * which should be thrown when trying to construct a PositionTemplate.
 * 
 * @see edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException
 * 
 * @author Paul J. Morris
 *
 */
public class BadTemplateException extends Exception {

	private static final long serialVersionUID = -3569211932963119505L;

	/**
	 * Default Constructor
	 */
	public BadTemplateException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public BadTemplateException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public BadTemplateException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BadTemplateException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
