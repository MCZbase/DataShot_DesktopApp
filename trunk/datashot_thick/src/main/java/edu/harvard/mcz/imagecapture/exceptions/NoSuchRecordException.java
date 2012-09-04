/**
 * NoSuchRecordException.java
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

/** NoSuchRecordException can be thrown when an attempt to look up a particular record in a 
 * database returns no matches.  
 * 
 * @author Paul J. Morris
 *
 */
public class NoSuchRecordException extends Exception {

	private static final long serialVersionUID = 2430363609924870002L;

	/**
	 * 
	 */
	public NoSuchRecordException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public NoSuchRecordException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public NoSuchRecordException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchRecordException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
