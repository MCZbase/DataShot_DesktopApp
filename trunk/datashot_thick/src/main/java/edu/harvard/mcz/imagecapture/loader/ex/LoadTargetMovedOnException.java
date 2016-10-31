/**
 * LoadTargetMovedOnException.java
 * edu.harvard.mcz.imagecapture.loader.ex
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
package edu.harvard.mcz.imagecapture.loader.ex;

/**
 * Exception on data loading indicating that the target record for update
 * from the data load has moved further down the the workflow within the
 * DataShot system and is past the point where the desired load is allowed.
 * 
 * @author mole
 *
 */
public class LoadTargetMovedOnException extends LoadException {
	
	private static final long serialVersionUID = 5171194333707975248L;
	
}
