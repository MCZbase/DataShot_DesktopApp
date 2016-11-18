/**
 * GenusSpeciesCountTableModel.java
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class GenusSpeciesCountTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1907248383164693796L;
	
	private static final Log log = LogFactory .getLog(GenusSpeciesCountTableModel.class);

	private List<GenusSpeciesCount> genusSpeciesCounts = null;
	
	public GenusSpeciesCountTableModel() {
		genusSpeciesCounts = new ArrayList<GenusSpeciesCount>();
	}
	
	public GenusSpeciesCountTableModel(List<GenusSpeciesCount> genusSpeciesCounts) {
		this.genusSpeciesCounts = genusSpeciesCounts;
	}	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return genusSpeciesCounts.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 3;
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

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object returnvalue = null;
		switch (columnIndex) {
		case 0:
			returnvalue = genusSpeciesCounts.get(rowIndex);
			break;
		case 1: 
		    returnvalue =  genusSpeciesCounts.get(rowIndex).getCount().toString();
		    break;		    
		case 2: 
		    returnvalue = genusSpeciesCounts.get(rowIndex).getGenus() + " " + genusSpeciesCounts.get(rowIndex).getSpecificEpithet();
		    break;
		}
		return returnvalue;
		    
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0) { 
			return GenusSpeciesCount.class;
		} else { 
			return String.class;
		}
	}	
}
