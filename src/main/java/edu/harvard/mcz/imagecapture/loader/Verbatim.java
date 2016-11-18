/**
 * Verbatim.java
 * edu.harvard.mcz.imagecapture.loader
 * Copyright © 2016 President and Fellows of Harvard College
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
package edu.harvard.mcz.imagecapture.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class Verbatim {
	private static final Log log = LogFactory.getLog(Verbatim.class);
	
	public static final String NO_PIN_LABELS = "No Pin Labels";
	public static final String PARTLY_ILLEGIBLE = "Partly illegible";
	public static final String ENTIRELY_ILLEGIBLE = "Entirely illegible";
	public static final String TRUNCATED_BY_IMAGE = "Edge of image truncates label";
	
	public static final String NO_LOCALITY_DATA = "[No specific locality data][No higher geography data]";
	
	public static final String SEPARATOR = "|";
	
	
}
