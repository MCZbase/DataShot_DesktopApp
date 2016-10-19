/**
 * Counter.java
 * edu.harvard.mcz.imagecapture
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
package edu.harvard.mcz.imagecapture;

import java.util.ArrayList;
import java.util.List;

import edu.harvard.mcz.imagecapture.data.ImagePreprocessError;

/** Counter
 * 
 * @author Paul J. Morris
 *
 */
public class Counter {

	private int totalCount = 0;
	private int filesSeen = 0;
	private int filesDatabased = 0;
	private int filesExisting = 0;
	private int filesFailed = 0;
	private int specimensDatabased = 0;
	private int directories = 0;
	private int directoriesFailed = 0;
	private int specimensUpdated = 0;
	private int filesUpdated = 0;
	private StringBuffer errorReport;
	private List<ImagePreprocessError> errors = null;


	public Counter() { 
		totalCount = 0;
		filesSeen = 0;
		filesDatabased = 0;
		filesExisting = 0;
		filesFailed = 0;
		specimensDatabased = 0;
		directories = 0;
		directoriesFailed = 0;
		specimensUpdated = 0;
		filesUpdated = 0;
		errorReport = new StringBuffer();
		errors = new ArrayList<ImagePreprocessError>();
	}

	public void appendError(ImagePreprocessError anError) { 
		errors.add(anError);
		errorReport.append(anError.toString());
		errorReport.append("\n");
	}

	public String getErrorReport() { 
		return errorReport.toString();
	}

	public void incrementSpecimens() { 
		specimensDatabased++;
	}

	public void incrementTotal() { 
		totalCount++;
	}
	public void incrementFilesSeen() { 
		filesSeen++;
	}
	public void incrementFilesFailed() { 
		filesFailed++;
	}
	public void incrementFilesDatabased() { 
		filesDatabased++;
	}
	public void incrementFilesExisting() { 
		filesExisting++;
	}	    
	public void incrementDirectories() { 
		directories++;
	}
	public void incrementDirectoriesFailed() { 
		directoriesFailed++;
	}

	public int getSpecimens() { 
		return specimensDatabased;
	}
	public int getTotal() { 
		return totalCount;
	}

	/**
	 * @return the files checked
	 */
	 public int getFilesSeen() {
		return filesSeen;
	}

	/**
	 * @return the files for which new database records were created
	 */
	 public int getFilesDatabased() {
		 return filesDatabased;
	 }

	 /**
	  * @return the files for which database records allready existed
	  */
	 public int getFilesExisting() {
		 return filesExisting;
	 }

	 /**
	  * @return the files for which a check for information needed to create a record Failed.
	  */
	 public int getFilesFailed() {
		 return filesFailed;
	 }


	 /**
	  * @return the directories
	  */
	 public int getDirectories() {
		 return directories;
	 }


	 /**
	  * @return the directoriesFailed
	  */
	 public int getDirectoriesFailed() {
		 return directoriesFailed;
	 }

	 /**
	 * @return the specimensUpdated
	 */
	public int getSpecimensUpdated() {
		return specimensUpdated;
	}

	/**
	 * @param specimensUpdated the specimensUpdated to set
	 */
	public void incrementSpecimensUpdated() {
		this.specimensUpdated++;
	}

	/**
	 * @return the filesUpdated
	 */
	public int getFilesUpdated() {
		return filesUpdated;
	}

	/**
	 * @param filesUpdated the filesUpdated to set
	 */
	public void incrementFilesUpdated() {
		this.filesUpdated++;
	}

	public List<ImagePreprocessError> getErrors() { 
		 return errors;
	 }

}
