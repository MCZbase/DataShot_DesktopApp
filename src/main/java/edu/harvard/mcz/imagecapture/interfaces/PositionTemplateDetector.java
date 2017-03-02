/**
 * PositionTemplateDetector.java
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

import java.io.File;

import edu.harvard.mcz.imagecapture.CandidateImageFile;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;

/** PositionTemplateDetector interface for detecting PositionTemplates for image files.
 * 
 * @author Paul J. Morris
 *
 */
public interface PositionTemplateDetector {

	/** Given a file, determine if a PositionTemplate applies to that file and if so
	 * return the identifier for the PositionTemplate.
	 * 
	 * @see edu.harvard.mcz.imagecapture.PositionTemplate
	 * @param anImageFile to check 
	 * @return the templateId of the PositionTemplate as a String 
	 * @throws UnreadableFileException if the file cannot be read.
	 */
	public String detectTemplateForImage(File anImageFile) throws UnreadableFileException;
	
	public String detectTemplateForImage(CandidateImageFile scannableFile) throws UnreadableFileException;
	
}
