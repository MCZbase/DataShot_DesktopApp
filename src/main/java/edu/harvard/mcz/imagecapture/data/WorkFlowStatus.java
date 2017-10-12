/**
 * WorkFlowStatus.java 
 *
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
package edu.harvard.mcz.imagecapture.data;

/**Authority list for values of Specimen.workFlowStatus.
 * 
 * @author Paul J. Morris
 *
 */
public class WorkFlowStatus {
	
	/**
	 * Specimen record created from barcode, with current determination, higher taxon, and drawer number
	 * populated by OCR and parsing of unit tray label.
	 */
	public static final String STAGE_0 = "OCR";
	
	/**
	 * Specimen record with clean with current determination, higher taxon, and drawer number, either after human
	 * cleanup of OCR, or when record was populated from taxon label data encoded in a barcode on the unit tray label.
	 */
	public static final String STAGE_1 = "Taxon Entered";
	
	/**
	 * Specimen record where the text from the pin labels has been transcribed into verbatim database fields
	 * by a data entry person (or in an external transcription process).  Data transcribed into verbatim fields, 
	 * but still needs to be interpreted further into atomic fields.
	 */	
	public static final String STAGE_VERBATIM = "Verbatim Entered";
	
	/**
	 * Specimen record where the text from the pin labels has been transcribed into verbatim database fields
	 * by a data entry person (or in an external transcription process) and this verbatim data has been 
	 * further interpreted into atomic fields, but all data may not yet have been entered from the image.
	 */	
	public static final String STAGE_CLASSIFIED = "Verbatim Classified";
	
	/**
	 * Specimen record where the text from the pin labels has been transcribed into database fields by a data
	 * entry person. 
	 */
	public static final String STAGE_2 = "Text Entered";
	
	/**
	 * State change for a specimen record where a human or an automated process has identified a 
	 * quality control issue with the record.
	 */
	public static final String STAGE_QC_FAIL = "QC Problems";
	
	/**
	 * State change for a specimen record on review after text entry by a person other than the data entry person who did 
	 * the text entry.
	 */
	public static final String STAGE_QC_PASS = "QC Reviewed";
	
	/**
	 * State change for a specimen record indicating that the specimen record that has been reviewed by a taxonomist.
	 */
	public static final String STAGE_CLEAN = "Specialist Reviewed";
	
	/**
	 * Specimen record has moved at least into the MCZbase bulkloader.  Flags indicate further progress to complete 
	 * load of all data into MCZbase.  Record is now not editable in DataShot.
	 */
	public static final String STAGE_DONE = "Moved to MCZbase";
	
	private static final String[] CHANGABLE_VALUES = {STAGE_0, STAGE_1,  STAGE_VERBATIM, STAGE_CLASSIFIED, STAGE_2,STAGE_QC_FAIL,STAGE_QC_PASS,STAGE_CLEAN}; 
	private static final String[] VALUES = {STAGE_0, STAGE_1, STAGE_VERBATIM, STAGE_CLASSIFIED, STAGE_2,STAGE_QC_FAIL,STAGE_QC_PASS,STAGE_CLEAN,STAGE_DONE}; 
	private static final String[] VERBATIM_VALUES = {STAGE_1, STAGE_VERBATIM};
	private static final String[] VERBATIM_CLASSIFIED_VALUES = {STAGE_VERBATIM, STAGE_CLASSIFIED};
	
	/**
	 * Obtain the list of all workflow status values that a user can put a record into.
	 * 
	 * @return array of string constants for all workflow status values that might be
	 * set by a user.
	 */
	public static String[] getWorkFlowStatusValues() { 
		String[] result = CHANGABLE_VALUES;  
		return result;
	}
	
	/**
	 * Obtain the list of all workflow status values that can be used when a record may be
	 * put into a verbatim captured state.  
	 * 
	 * @return array of string constants
	 */
	public static String[] getVerbatimWorkFlowStatusValues() { 
		String[] result = VERBATIM_VALUES;  
		return result;
	}	
	/**
	 * Obtain the list of all workflow status values that can be used when a record may
	 * be put into a verbatim classified state.
	 * 
	 * @return array of string constants.
	 */
	public static String[] getVerbatimClassifiedWorkFlowStatusValues() { 
		String[] result = VERBATIM_CLASSIFIED_VALUES;  
		return result;
	}	

	/**
	 * Obtain the complete list of all possible workflow status values.
	 * 
	 * @return arry of string constants for all workflow status states.
	 */
	public static String[] getAllWorkFlowStatusValues() { 
		String[] result = VALUES;  
		return result;
	}
	
	/**
	 * Test to see whether or not a state can be changed to verbatim captured. 
	 * 
	 * @param workflowStatus a current workflow state to check 
	 * @return true if the record can be placed into state verbatim captured from 
	 *   its current (other) state, false otherwise.  False if the current state
	 *   is already verbatim captured.
	 */
	public static boolean allowsVerbatimUpdate(String workflowStatus) { 
		boolean result = false;
		
		if (workflowStatus.equals(STAGE_0)) { result = true; } 
		if (workflowStatus.equals(STAGE_1)) { result = true; } 
		
		return result;
	}
	
	/**
	 * Test to see whether or not a state can be changed to verbatim classified.
	 * 
	 * @param workflowStatus a current workflow state to check. 
	 * 
	 * @return true if the record can be placed into state verbatim classified from 
	 *   its current (other) state, false otherwise.  True if the current state is
	 *   verbatim classified.
	 */
	public static boolean allowsClassifiedUpdate(String workflowStatus) { 
		boolean result = false;
		
		if (workflowStatus.equals(STAGE_0)) { result = true; } 
		if (workflowStatus.equals(STAGE_1)) { result = true; } 
		if (workflowStatus.equals(STAGE_VERBATIM)) { result = true; } 
		if (workflowStatus.equals(STAGE_CLASSIFIED)) { result = true; } 
		
		return result;
	}	
}
