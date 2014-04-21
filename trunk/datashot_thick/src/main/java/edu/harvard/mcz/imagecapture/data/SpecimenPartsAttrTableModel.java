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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import java.util.ArrayList;


/**
 * @author mole
 *
 */
public class SpecimenPartsAttrTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = -4139892019645663114L;
	
	private Collection<SpecimenPartAttribute> specimenPartAttributes;
	
	public SpecimenPartsAttrTableModel() {
		super();
		this.specimenPartAttributes = new HashSet<SpecimenPartAttribute>();
	}

	
	/**
	 * @param specimenParts
	 */
	public SpecimenPartsAttrTableModel(Collection<SpecimenPartAttribute> specimenPartAttributes) {
		super();
		this.specimenPartAttributes = specimenPartAttributes; 
	}	
	

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return specimenPartAttributes.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String returnvalue = null;
		switch (columnIndex) { 
		case 0: 
			returnvalue = "Type";
			break;
		case 1: 
			returnvalue = "Value";
			break;
		case 2: 
			returnvalue = "Units";
			break;
		case 3: 
			returnvalue = "Remarks";
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
			result = ((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).getAttributeType();
			break;
		case 1:
			result = ((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).getAttributeValue();
			break;
		case 2:
			result = ((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).getAttributeUnits();
			break;
		case 3:
			result = ((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).getAttributeRemark();
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
			((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).setAttributeType((String)aValue);
			break;
		case 1:
			((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).setAttributeValue((String)aValue);
			break;
		case 2:
			((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).setAttributeUnits((String)aValue);
			break;
		case 3:
			((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]).setAttributeRemark((String)aValue);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean result = false;
		return result;
	}


	/**
	 * @param rowIndex row to be deleted
	 */
	public void deleteRow(int rowIndex) {
		SpecimenPartAttribute toRemove = ((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]);
		SpecimenPartAttributeLifeCycle spals = new SpecimenPartAttributeLifeCycle();
		try {
			spals.remove(toRemove);
		    specimenPartAttributes.remove(toRemove);
		    fireTableDataChanged();
		} catch (SaveFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SpecimenPartAttribute getRowObject(int rowIndex) { 
		return ((SpecimenPartAttribute)specimenPartAttributes.toArray()[rowIndex]);
	}

}
