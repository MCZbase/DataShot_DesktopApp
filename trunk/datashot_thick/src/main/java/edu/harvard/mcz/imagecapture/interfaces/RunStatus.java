/** 
 * RunStatus.java
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

/** Describes the status of a RunnableJob
 *  
 * @author Paul J. Morris
 *
 */
public class RunStatus {
	
	// use values less than STATUS_FAILED for jobs that aren't complete.
	public static final int STATUS_NEW = 0;
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_CANCEL_REQUESTED = 50;
	// use values greater than or equal to STATUS_FAILED for completed jobs.
	public static final int STATUS_FAILED = 100;
	public static final int STATUS_TERMINATED = 102;
	public static final int STATUS_DONE = 101;
	
	/**
	 * 
	 * @param aStatus status code to test, from one of the RunStatus.STATUS_* constants.
	 * @return false if aStatus is that of a still running job, true if aStatus is that
	 * of a job that has completed either successfully or with an error state.
	 */
	public static boolean testComplete(int aStatus) { 
		boolean returnValue = false;
		if (aStatus>=STATUS_FAILED) { 
			returnValue = true;
		}
		return returnValue;
	}

}
