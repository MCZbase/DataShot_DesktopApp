/**
 * SpecimenListTableModel.java
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

import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.SpecimenControler;

/** SpecimenListTableModel
 * 
 * @author Paul J. Morris
 *
 */
public class SpecimenListTableModel extends AbstractTableModel {

	private static final Log log = LogFactory.getLog(SpecimenListTableModel.class);
	
	private static final long serialVersionUID = -8394267503927374758L;
	
	public static final int COL_ID = 0;
	public static final int COL_BARCODE = 1;
	public static final int COL_WORKFLOW = 2;
	public static final int COL_FAMILY = 3;
	public static final int COL_SUBFAMILY = 4;
	public static final int COL_TRIBE = 5;
	public static final int COL_GENUS = 6;
	public static final int COL_SPECIFIC = 7;
	public static final int COL_SUBSPECIFIC = 8;
	public static final int COL_HIGHERGEOG = 9;
	public static final int COL_VERBLOCALITY = 10;
	public static final int COL_COUNTRY = 11;
	public static final int COL_DRAWER = 12;
	public static final int COLUMCOUNT = 13;
	
	private List<Specimen> specimens = null;
	
	public SpecimenListTableModel(List<Specimen> specimenList) { 
		specimens = specimenList;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return COLUMCOUNT;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return specimens.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Specimen s = specimens.get(rowIndex);
		Object result = null;
		switch (columnIndex) { 
		case COL_ID:
			//result = s.getSpecimenId();
			result = s;
			break;
		case COL_BARCODE:
			result = s.getBarcode();
			break;
		case COL_WORKFLOW: 
			result = s.getWorkFlowStatus();
			break;
		case COL_FAMILY: 
			result = s.getFamily();
			break;
		case COL_SUBFAMILY: 
			result = s.getSubfamily();
			break;
		case COL_TRIBE: 
			result = s.getTribe();
			break;			
		case COL_GENUS: 
			result = s.getGenus();
			break;
		case COL_SPECIFIC: 
			result = s.getSpecificEpithet();
			break;	
		case COL_SUBSPECIFIC: 
			result = s.getSubspecificEpithet();
			break;			
		case COL_HIGHERGEOG: 
			result = s.getHigherGeography();
			break;
		case COL_VERBLOCALITY: 
			result = s.getVerbatimLocality();
			break;
		case COL_COUNTRY: 
			result = s.getCountry();
			break;			
		case COL_DRAWER: 
			result = s.getDrawerNumber();
			break;
		}
		return result;
	}
	
	/**
	 * Must be implemented for ButtonEditor to work.  Needs to return Long for 
	 * ID column that is to contain button to work with ButtonEditor.
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		 // Given current implementation of button in SpecimenBrowser,  
		 // needs to return Long for ID column that is to contain button 
		 // and ** Must Not ** return Long for any other column).
		Class result = String.class;  // Default value to return when table is empty.
		try { 
		    result = getValueAt(0, c).getClass();
		} catch (NullPointerException e) { 
			// continue
		}
        return result; 
    }

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String result = null;
		switch (columnIndex) { 
		case COL_ID:
			result = "";
			break;
		case COL_BARCODE:
			result = "Barcode";
			break;
		case COL_WORKFLOW: 
			result = "Workflow";
			break;
		case COL_FAMILY: 
			result = "Family";
			break;
		case COL_SUBFAMILY: 
			result = "Subfamily";
			break;
		case COL_TRIBE: 
			result = "Tribe";
			break;			
		case COL_GENUS: 
			result = "Genus";
			break;
		case COL_SPECIFIC: 
			result = "Species";
			break;
		case COL_SUBSPECIFIC: 
			result = "Subspecies";
			break;
		case COL_HIGHERGEOG: 
			result = "Higher Geography";
			break;
		case COL_VERBLOCALITY: 
			result = "Verbatim Locality";
			break;
		case COL_COUNTRY: 
			result = "Country";
			break;			
		case COL_DRAWER: 
			result = "Drawer";
			break;			
		}
		return result;
	}

	/** Must be implemented for ButtonEditor to work.
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean result = false;
		if (columnIndex==COL_ID) { 
			result = true;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		super.setValueAt(value, rowIndex, columnIndex); 
		// don't do anything particular, edit is only to enable a buttonEditor as a 
		// renderer that can hear mouse clicks.
	}

}
