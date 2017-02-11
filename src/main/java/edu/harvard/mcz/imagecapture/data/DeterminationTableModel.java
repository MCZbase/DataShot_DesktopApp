/**
 * DeterminationTableModel.java
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
package edu.harvard.mcz.imagecapture.data;

import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;


/** DeterminationTableModel, a table model for Determinations, able to list the
 * additional determinations for a specimen.
 * 
 * @author Paul J. Morris
 *
 */
public class DeterminationTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -671159987795807222L;
	
	private static final Log log = LogFactory.getLog(DeterminationTableModel.class);
	
	private Set <Determination> determinations = null;
	
	// Allow display of a cell editor, intended for adding a details button.  
	private boolean showEditableId = false; 
	// private static final int ROW_ID = 0;
	private static final int SPECIES_NUMBER = 0;
	public static final int ROW_SPECIMEN = 14;
	public static final int ROW_IDENTIFIEDBY = 8;
	public static final int ROW_TYPESTATUS = 7;
	public static final int ROW_NATUREOFID = 11;
	public static final int ROW_DATEIDENTIFIED = 12;
	
	
	/**
	 * 
	 */
	public DeterminationTableModel() {
		determinations = new HashSet<Determination>();
	}
	
	public DeterminationTableModel(Set<Determination> aDeterminationList) {
		determinations = aDeterminationList;
	}	
	
	/**
	 * Constructor for cases where it is desirable to display the table with a 
	 * cell editor (particularly an active button to pop up a details window or
	 * take another action on an ID)
	 * 
	 * @param aDeterminationList the lists of determinations to display in the table.
	 * @param enableIdEditor true to include the determinationId to the table model in an editable 
	 * column.
	 */
	public DeterminationTableModel(Set<Determination> aDeterminationList, boolean enableIdEditor) {
		showEditableId = enableIdEditor;
		determinations = aDeterminationList;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// all fields in determination except identificationQualifier
		int result = 15;
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return determinations.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object returnvalue = null;
		switch (columnIndex) {
//		case ROW_ID: 
//		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getDeterminationId();
//		    break;
		case SPECIES_NUMBER: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getSpeciesNumber();
		    break;		    
		case 1: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getGenus();
		    break;
		case 2: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getSpecificEpithet();
		    break;
		case 3: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getSubspecificEpithet();
		    break;
		case 4: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getInfraspecificEpithet();
		    break;	
		case 5: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getInfraspecificRank();
		    break;
		case 6: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getAuthorship();
		    break;
		case ROW_TYPESTATUS: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getTypeStatus();
		    break;
		case ROW_IDENTIFIEDBY: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getIdentifiedBy();
		    break;	
		case 9: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getUnNamedForm();
		    break;
		case 10: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getVerbatimText();
		    break;
		case ROW_NATUREOFID: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getNatureOfId();
		    break;
		case ROW_DATEIDENTIFIED: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getDateIdentified();
		    break;
		case 13: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getRemarks();
		    break;		    
		case ROW_SPECIMEN: 
		    returnvalue = ((Determination)determinations.toArray()[rowIndex]).getSpecimen().getBarcode();
		    break;			    
		}
		
		return returnvalue;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean result = true;
		// allow editor for all columns
		//if (columnIndex==ROW_ID || columnIndex==ROW_SPECIMEN) {
	    if (columnIndex==ROW_SPECIMEN) {
			// turn off editing of determinationId and barcode.
			result = false;
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		switch (columnIndex) {
//		case ROW_ID: 
//		    // do nothing
//		    break;
		case SPECIES_NUMBER: 
			((Determination)determinations.toArray()[rowIndex]).setSpeciesNumber((String)value);
		    break;
		case 1: 
		    ((Determination)determinations.toArray()[rowIndex]).setGenus((String)value);
		    break;
		case 2: 
		    ((Determination)determinations.toArray()[rowIndex]).setSpecificEpithet((String)value);
		    break;
		case 3: 
		    ((Determination)determinations.toArray()[rowIndex]).setSubspecificEpithet((String)value);
		    break;
		case 4: 
		    ((Determination)determinations.toArray()[rowIndex]).setInfraspecificEpithet((String)value);
		    break;	
		case 5: 
		    ((Determination)determinations.toArray()[rowIndex]).setInfraspecificRank((String)value);
		    break;
		case 6: 
		    ((Determination)determinations.toArray()[rowIndex]).setAuthorship((String)value);
		    break;
		case ROW_TYPESTATUS: 
		    ((Determination)determinations.toArray()[rowIndex]).setTypeStatus((String)value);
		    break;
		case ROW_IDENTIFIEDBY: 
		    ((Determination)determinations.toArray()[rowIndex]).setIdentifiedBy(((MCZbaseAuthAgentName)value).getAgent_name());
		    break;	
		case 9: 
		    ((Determination)determinations.toArray()[rowIndex]).setUnNamedForm((String)value);
		    break;
		case 10: 
		    ((Determination)determinations.toArray()[rowIndex]).setVerbatimText((String)value);
		    break;
		case ROW_NATUREOFID: 
		    ((Determination)determinations.toArray()[rowIndex]).setNatureOfId((String)value);
		    break;
		case ROW_DATEIDENTIFIED: 
		    ((Determination)determinations.toArray()[rowIndex]).setDateIdentified((String)value);
		    break;
		case 13: 
		    ((Determination)determinations.toArray()[rowIndex]).setRemarks((String)value);
		    break;
		case ROW_SPECIMEN: 
		    // do nothing, don't allow editing specimen record.
		    break;			    
		}
	}

	
	/** addDetermination adds a determination to this model as a new row
	 * @param aDetermination the determination to add to the table model.
	 */
	public void addDetermination(Determination aDetermination) {
		determinations.add(aDetermination);
		this.fireTableDataChanged();
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String returnvalue = "";
		switch (columnIndex) {
//		case ROW_ID:
//		    returnvalue = "ID";
//		    break;
		case SPECIES_NUMBER: 
		    returnvalue = "Species Number";
		    break;		    
		case 1: 
		    returnvalue = "Genus";
		    break;
		case 2: 
		    returnvalue =  "Species";
		    break;
		case 3: 
		    returnvalue = "Subspecies";
		    break;
		case 4: 
		    returnvalue = "Infraspecific";
		    break;	
		case 5: 
		    returnvalue = "Infra Rank";
		    break;
		case 6: 
		    returnvalue = "Authorship";
		    break;
		case ROW_TYPESTATUS: 
		    returnvalue = "Type Status";
		    break;
		case ROW_IDENTIFIEDBY: 
		    returnvalue = "Determiner";
		    break;	
		case 9: 
		    returnvalue = "Un-named form";
		    break;
		case 10: 
		    returnvalue = "Verbatim Text";
		    break;
		case ROW_NATUREOFID: 
		    returnvalue = "Nature Of ID";
		    break;
		case ROW_DATEIDENTIFIED: 
		    returnvalue = "ID Date";
		    break;
		case 13: 
		    returnvalue = "Remarks";
		    break;
		case ROW_SPECIMEN: 
		    returnvalue = "Barcode";
		    break;			    
		}
		
		return returnvalue;
	}

	/**
	 * @param rowIndex row to be deleted
	 */
	public void deleteRow(int rowIndex) {
		Determination toRemove = ((Determination)determinations.toArray()[rowIndex]);
		DeterminationLifeCycle spals = new DeterminationLifeCycle();
		try {
			spals.delete(toRemove);
		    determinations.remove(toRemove);
		    fireTableDataChanged();
		} catch (SaveFailedException e) {
			log.error(e.getMessage());
		}
	}

}