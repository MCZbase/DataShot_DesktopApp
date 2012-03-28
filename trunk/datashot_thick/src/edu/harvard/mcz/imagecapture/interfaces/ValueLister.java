/** 
 * ValueLister.java
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

/**
 * Interface for providing a string array of values that can be used
 * for purposes such as populating a pick list.
 * 
 * @author Paul J. Morris
 *
 */
public interface ValueLister {

	/**
	 * Obtain a list of Valid values in a controlled vocabulary, or a list of values in current use in a user
	 * extendible controlled vocabulary.
	 * 
	 * @return a string array where each entry in the array is a valid value within a controlled vocabulary.
	 */
	public String[] getValues();
	
}
