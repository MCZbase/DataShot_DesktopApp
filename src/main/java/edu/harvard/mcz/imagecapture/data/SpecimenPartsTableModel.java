/**
 * SpecimenPartsTableModel
 * edu.harvard.mcz.imagecapture
 * Copyright © 2013 President and Fellows of Harvard College
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

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;


/**
 * @author mole
 *
 */
public class SpecimenPartsTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -4139892019645663114L;
	
	private Set<SpecimenPart> specimenParts;
	
	public SpecimenPartsTableModel() {
		super();
		this.specimenParts = new HashSet<SpecimenPart>();
	}

	/**
	 * @param specimenParts
	 */
	public SpecimenPartsTableModel(Set<SpecimenPart> specimenParts) {
		super();
		this.specimenParts = specimenParts;
	}	

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return specimenParts.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 5;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String returnvalue = null;
		switch (columnIndex) { 
		case 0: 
			returnvalue = "Part";
			break;
		case 1: 
			returnvalue = "Prep.";
			break;
		case 2: 
			returnvalue = "Count";
			break;
		case 3: 
			returnvalue = "Attributes";
			break;			
		case 4: 
			returnvalue = "";
			break;			
		}
		return returnvalue;
	}	
	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		switch (columnIndex) { 
		case 0:
			result = ((SpecimenPart)specimenParts.toArray()[rowIndex]).getPartName();
			break;
		case 1:
			result = ((SpecimenPart)specimenParts.toArray()[rowIndex]).getPreserveMethod();
			break;
		case 2:
			result = ((SpecimenPart)specimenParts.toArray()[rowIndex]).getLotCount();
			break;
		case 3:
			result = ((SpecimenPart)specimenParts.toArray()[rowIndex]).getPartAttributeValuesConcat();
			break;
		case 4:
			result = (SpecimenPart)specimenParts.toArray()[rowIndex];
			break;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) { 
		case 0:
			((SpecimenPart)specimenParts.toArray()[rowIndex]).setPartName((String)aValue);
			break;
		case 1:
			((SpecimenPart)specimenParts.toArray()[rowIndex]).setPreserveMethod((String)aValue);
			break;
		case 2:
			((SpecimenPart)specimenParts.toArray()[rowIndex]).setLotCount(Integer.parseInt((String)aValue));
			break;
		case 3:
			break;
		case 4:
			break;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean result = true;
		if (columnIndex==3) { result = false; } 
		return result;
	}	
	
	/**
	 * @param rowIndex row to be deleted
	 */
	public void deleteRow(int rowIndex) {
		SpecimenPart toRemove = ((SpecimenPart)specimenParts.toArray()[rowIndex]);
		SpecimenPartLifeCycle spals = new SpecimenPartLifeCycle();
		try {
			spals.remove(toRemove);
		    specimenParts.remove(toRemove);
		    fireTableDataChanged();
		} catch (SaveFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
