/**
 * RunnableJobErrorTableModel.java
 * edu.harvard.mcz.imagecapture.data
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
package edu.harvard.mcz.imagecapture.jobs;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/** RunnableJobErrorTableModel, table model for displaying error reports about preprocessing images.
 * Can handle different types of reports by specifying a TYPE_ constant in the constructor.   
 * 
 * @author Paul J. Morris
 *
 */
public class RunnableJobErrorTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3407074726845800411L;
	
	/**
	 * Constant for reporting on errors in preprocessing. 
	 */
	public static final int TYPE_PREPROCESS = 0;
	/**
	 * Constant for reporting on errors in rechecking for missing barcodes.
	 */
	public static final int TYPE_MISSING_BARCODES = 1;
	/**
	 * Constant for reporting on errors in loading data.
	 */
	public static final int TYPE_LOAD = 2;
	
	public static final int TYPE_FILE_RECONCILIATION = 3;
	
	private List<RunnableJobError> errors;
	private int type;
	
	public RunnableJobErrorTableModel(List<RunnableJobError> errorList) { 
		errors = errorList;
		type = TYPE_PREPROCESS;
	}
	
	public RunnableJobErrorTableModel(List<RunnableJobError> errorList, int listType) { 
		errors = errorList;
		type = TYPE_PREPROCESS;
		if (listType==TYPE_MISSING_BARCODES) { 
		    type = TYPE_MISSING_BARCODES;
		}
		if (listType==TYPE_LOAD) { 
		    type = TYPE_LOAD;
		} 		
		if (listType==TYPE_FILE_RECONCILIATION) {
			type = TYPE_FILE_RECONCILIATION;
		}
	}	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		int result = 10;
		switch (type) { 
		case TYPE_PREPROCESS: 
		   result =  9;
		   break;
		case TYPE_MISSING_BARCODES:
			result = 4;
			break;
		case TYPE_LOAD:
			result = 6;
			break;
		case TYPE_FILE_RECONCILIATION:
			result = 5;
			break;			
		} 
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return errors.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		RunnableJobError error = errors.get(rowIndex);
		Object result = null;
		switch (type) { 
		case TYPE_PREPROCESS: 
			switch (columnIndex) {
			case (0):
				result = error.getFailureType();
				break;
			case (1):
				result = error.getFilename();
				break;
			case (2):
				result = error.getBarcode();
				break;			
			case (3):
				result = error.getQrBarcode();
				break;
			case (4):
				result = error.getCommentBarcode();
				break;			
			case (5):
				result = error.getErrorMessage();
				break;
			case (6):
				if (error.getException()!=null) {
				    result = error.getException().getMessage();
			    } else { 
			    	result = "";
			    }
				break;
			case (7):
				if (error.getDrawerParser()!=null) {
				    result = error.getDrawerParser().getDrawerNumber();
			    } else { 
			    	result = "";
			    }
				break;				
			case (8):
				if (error.getTaxonParser()!=null) {
				    result = error.getTaxonParser().getFamily();
			    } else { 
			    	result = "";
			    }
				break;		
			}
			break;
		case TYPE_MISSING_BARCODES:
			switch (columnIndex) {
			case (0):
				result = error.getFailureType();
				break;
			case (1):
				result = error.getBarcode();
				break;
			case (2):
				result = error.getPrevious();
				break;			
			case (3):
				result = error.getPreviousPath();
				break;	
			}
			break;
		case TYPE_LOAD: 
			switch (columnIndex) {
			case (0):
				result = error.getFailureType();
				break;
			case (1):
				result = error.getFilename();
				break;
			case (2):
				result = error.getLineNumber();
				break;				
			case (3):
				result = error.getBarcode();
				break;					
			case (4):
				result = error.getErrorMessage();
				break;
			case (5):
				if (error.getException()!=null) {
				    result = error.getException().getMessage();
			    } else { 
			    	result = "";
			    }
				break;
			}
			break;
		case TYPE_FILE_RECONCILIATION:
			switch (columnIndex) {
			case (0):
				result = error.getFailureType();
				break;
			case (1):
				result = error.getQrBarcode(); // overloaded, path to file
				break;
			case (2):
				result = error.getFilename();
				break;	
			case (3):
				result = error.getBarcode();
				break;				
			case (4):
				result = error.getErrorMessage();
				break;	
			}
			break;			
		} 
		
		return result;
	}

	public String getColumnName(int columnIndex) {
		String result = "";
		switch (type) { 
		case TYPE_PREPROCESS: 
			switch (columnIndex) {
			case (0):
				result = "Type";
				break;
			case (1):
				result = "Filename";
				break;
			case (2):
				result = "Barcode"; 
				break;			
			case (3):
				result = "QR Barcode";
				break;
			case (4):
				result = "Comment Barcode";
				break;			
			case (5):
				result = "Error";
				break;
			case (6):
				result = "Exception";
				break;
			case (7):
				result = "Drawer";
				break;				
			case (8):
				result = "Family";
				break;			
			}
		   break;
		case TYPE_MISSING_BARCODES:
			switch (columnIndex) {
			case (0):
				result = "Type";
				break;
			case (1):
				result = "Barcode";
				break;
			case (2):
				result = "Previous File"; 
				break;			
			case (3):
				result = "Path";
				break;
			}
			break;
		case TYPE_FILE_RECONCILIATION:
			switch (columnIndex) {
			case (0):
				result = "Type";
				break;
			case (1):
				result = "Path";
				break;
			case (2):
				result = "Filename";
				break;
			case (3):
				result = "Barcode"; 
				break;			
			case (4):
				result = "Error";
				break;
			}
			break;			
		case TYPE_LOAD: 
			switch (columnIndex) {
			case (0):
				result = "Type";
				break;
			case (1):
				result = "Filename";
				break;
			case (2):
				result = "Linenumber";
				break;				
			case (3):
				result = "Barcode"; 
				break;			
			case (4):
				result = "Error";
				break;
			case (5):
				result = "Exception";
				break;
			}
		   break;			
		} 

		return result;		
	}
	
}
