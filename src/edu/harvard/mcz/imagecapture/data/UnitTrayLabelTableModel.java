/**
 * UnitTrayLabelTableModel.java
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
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

/** UnitTrayLabelTableModel
 * 
 * @author Paul J. Morris
 *
 */
public class UnitTrayLabelTableModel extends AbstractTableModel {
	
	private static final Log log = LogFactory.getLog(UnitTrayLabelTableModel.class);

	private static final int COLUMN_TO_PRINT = 13;
	private static final int COLUMN_PRINTED = 14;
	
	/** When a new row is added, the value of ordinal will be set to zero.
	 * 
	 */
	public static final int NEXT_ORDINAL_ZERO = 0;
	
	/** When a new row is added the value of ordinal will be set to the 
	 * value of the largest ordinal currently in the database plus 1.
	 */
	public static final int NEXT_ORDINAL_MAXPLUSONE = 1;
	
	private static final long serialVersionUID = -8022147291895055945L;
	
	private List<UnitTrayLabel> labels= null;
	
	public UnitTrayLabelTableModel(List<UnitTrayLabel> labels) { 
		this.labels = labels;
	}
	
	
	/**
	 * 
	 */
	public UnitTrayLabelTableModel() {
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		this.labels = uls.findAll();
	}

	public void setLabels(List<UnitTrayLabel> labels) { 
		this.labels = labels;
	}
	
	public void addRow() {
		UnitTrayLabel newLabel = new UnitTrayLabel();
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		try {
			//TODO: Finish making this a configuration option.
			int nextOrdinalMethod = NEXT_ORDINAL_ZERO;
			int nextOrdinal = 0;
			if (nextOrdinalMethod==NEXT_ORDINAL_MAXPLUSONE) {
				nextOrdinal = uls.findMaxOrdinal() + 1;
			}
			if (nextOrdinalMethod==NEXT_ORDINAL_ZERO) { 
				nextOrdinal = 0;
			}
			newLabel.setOrdinal(nextOrdinal);
			uls.persist(newLabel);
			this.labels.add(newLabel);
			this.fireTableDataChanged();
		} catch (SaveFailedException e1) {
			JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
					"Save of new record failed.\n" + e1.getMessage(), 
					"Save Failed", 
					JOptionPane.ERROR_MESSAGE);	
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 15;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return labels.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String returnvalue = null;
		switch (columnIndex) {
		case 0: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getFamily();
			break;
		case 1: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getSubfamily();
			break;
		case 2: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getTribe();
			break;
		case 3: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getGenus();
			break;
		case 4: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getSpecificEpithet();
			break;		
		case 5: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getSubspecificEpithet();
			break;		
		case 6: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getInfraspecificRank();
			break;		
		case 7: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getInfraspecificEpithet();
			break;		
		case 8: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getAuthorship();
			break;		
		case 9: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getUnNamedForm();
			break;		
		case 10: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getDrawerNumber();
			break;		
		case 11: 
			returnvalue = ((UnitTrayLabel)labels.get(rowIndex)).getCollection();
			break;		
		case 12: 
			returnvalue = Integer.toString(((UnitTrayLabel)labels.get(rowIndex)).getOrdinal());
			break;		
		case COLUMN_TO_PRINT: 
			returnvalue = Integer.toString(((UnitTrayLabel)labels.get(rowIndex)).getNumberToPrint());
			break;		
		case COLUMN_PRINTED: 
			returnvalue = Integer.toString(((UnitTrayLabel)labels.get(rowIndex)).getPrinted());
			break;			
		}
		return returnvalue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String returnvalue = null;
		switch (columnIndex) {
		case 0: 
			returnvalue = "Family";
			break;
		case 1: 
			returnvalue = "SubFamily";
			break;
		case 2: 
			returnvalue = "Tribe";
			break;
		case 3: 
			returnvalue = "Genus";
			break;
		case 4: 
			returnvalue = "Species";
			break;		
		case 5: 
			returnvalue = "Subspecies";
			break;		
		case 6: 
			returnvalue = "Infra Rank";
			break;		
		case 7: 
			returnvalue = "Infraspecific";
			break;		
		case 8: 
			returnvalue = "Authorship";
			break;		
		case 9: 
			returnvalue = "UnNamed Form";
			break;		
		case 10: 
			returnvalue = "Drawer";
			break;		
		case 11: 
			returnvalue = "Collection";
			break;		
		case 12: 
			returnvalue = "Sort";
			break;		
		case COLUMN_TO_PRINT: 
			returnvalue = "To Print";
			break;		
		case COLUMN_PRINTED: 
			returnvalue = "Printed";
			break;			
		}
		return returnvalue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex==COLUMN_PRINTED) { 
			return false; 
		} else { 
		    return true;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		log.debug("Object to save at " + rowIndex+","+columnIndex + " is " + value.toString());
		switch (columnIndex) {
		case 0: 
			((UnitTrayLabel)labels.get(rowIndex)).setFamily(value.toString());
			break;
		case 1: 
			((UnitTrayLabel)labels.get(rowIndex)).setSubfamily(value.toString());
			break;
		case 2: 
			((UnitTrayLabel)labels.get(rowIndex)).setTribe(value.toString());
			break;
		case 3: 
			((UnitTrayLabel)labels.get(rowIndex)).setGenus(value.toString());
			break;
		case 4: 
			((UnitTrayLabel)labels.get(rowIndex)).setSpecificEpithet(value.toString());
			break;		
		case 5: 
			((UnitTrayLabel)labels.get(rowIndex)).setSubspecificEpithet(value.toString());
			break;		
		case 6: 
			((UnitTrayLabel)labels.get(rowIndex)).setInfraspecificRank(value.toString());
			break;		
		case 7: 
			((UnitTrayLabel)labels.get(rowIndex)).setInfraspecificEpithet(value.toString());
			break;		
		case 8: 
			((UnitTrayLabel)labels.get(rowIndex)).setAuthorship(value.toString());
			break;		
		case 9: 
			((UnitTrayLabel)labels.get(rowIndex)).setUnNamedForm(value.toString());
			break;		
		case 10: 
			((UnitTrayLabel)labels.get(rowIndex)).setDrawerNumber(value.toString());
			break;		
		case 11: 
			((UnitTrayLabel)labels.get(rowIndex)).setCollection(value.toString());
			break;		
		case 12: 
			((UnitTrayLabel)labels.get(rowIndex)).setOrdinal(Integer.parseInt(value.toString()));
			break;		
		case COLUMN_TO_PRINT: 
			((UnitTrayLabel)labels.get(rowIndex)).setNumberToPrint((Integer.parseInt(value.toString())));
			break;		
		case COLUMN_PRINTED: 
			((UnitTrayLabel)labels.get(rowIndex)).setPrinted((Integer.parseInt(value.toString())));
			break;			
		}
		saveRow(rowIndex);
	}

	/**
	 * @return
	 */
	public List<UnitTrayLabel> getList() {
		return labels;
	}

	private void saveRow(int rowIndex) { 
		UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
		try {
			uls.attachDirty(((UnitTrayLabel)labels.get(rowIndex)));
		} catch (SaveFailedException e) {
			log.error(e);
			JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
					"Save failed for a unit tray label." + "\n" + e.getMessage(), 
					"Save Failed", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
}
