/**
 * edu.harvard.mcz.imagecapture.exceptions
 * Copyright © 2013 President and Fellows of Harvard College
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

/**
 * Exeptions produced by trying to create a specimen record when a record
 * for that specimen already exists.
 * 
 * @author mole
 *
 */
public class SpecimenExistsException extends Exception {

	private static final long serialVersionUID = 931483814304020341L;

	/**
	 * 
	 */
	public SpecimenExistsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SpecimenExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public SpecimenExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}
