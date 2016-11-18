/**
 * VerbatimCountTableModel.java
 * edu.harvard.mcz.imagecapture.data
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
package edu.harvard.mcz.imagecapture.struct;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class VerbatimCountTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 834148063219117706L;

	private static final Log log = LogFactory.getLog(VerbatimCountTableModel.class);
	
	private List<VerbatimCount> verbatimCounts;

	public VerbatimCountTableModel() { 
		verbatimCounts = new ArrayList<VerbatimCount>();
	}
	
	public VerbatimCountTableModel(List<VerbatimCount> verbatimCounts) { 
		this.verbatimCounts = verbatimCounts;
	}
	
	public void fireDataHasChanged() { 
		fireTableDataChanged();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return verbatimCounts.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 8;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		switch (columnIndex) { 
		case 0:
			result = verbatimCounts.get(rowIndex);
			break;
		case 1: 
			result = verbatimCounts.get(rowIndex).getCount();
			break;
		case 2: 
			result = verbatimCounts.get(rowIndex).getVerbatimLocality();
			break;
		case 3: 
			result = verbatimCounts.get(rowIndex).getVerbatimDate();
			break;
		case 4: 
			result = verbatimCounts.get(rowIndex).getVerbatimCollector();
			break;
		case 5: 
			result = verbatimCounts.get(rowIndex).getVerbatimCollection();
			break;
		case 6: 
			result = verbatimCounts.get(rowIndex).getVerbatimNumbers();
			break;
		case 7: 
			result = verbatimCounts.get(rowIndex).getVerbatimUnclassfiedText();
			break;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) { 
		case 0: 
			return VerbatimCount.class;
		case 1:
			return Integer.class;
		default:
			return String.class;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		String result = super.getColumnName(column);
		switch (column) { 
		case 0:
			result = " ";
			break;
		case 1: 
			result = "Count";
			break;
		case 2: 
			result = "Verb. Locality";
			break;
		case 3: 
			result = "Verb. Date";
			break;
		case 4: 
			result = "Verb. Collector";
			break;
		case 5: 
			result = "Verb. Collection";
			break;
		case 6: 
			result = "Verb. Numbers";
			break;
		case 7: 
			result = "Other Text";
			break;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean result = false;
		if (columnIndex==0) { 
			result = true;
		}
		return result;
	}
	
	
}
