/**
 * ICImageListTableModel.java
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

/** ICImageListTableModel model to display image metadata in a table.
 * 
 * @author Paul J. Morris
 *
 */
public class ICImageListTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1122512383053371L;
	
	public static final int COL_ID = 0;
	public static final int COL_PATH = 1;
	public static final int COL_FILENAME = 2;
	public static final int COL_BARCODE = 6;
	public static final int COL_DRAWER = 8;
	
	private List<ICImage> images = null;
	
//	public ICImageListTableModel() { 
//		images = new ArrayList<ICImage>();
//	}
	
	public ICImageListTableModel(List <ICImage> anImageList) { 
		images = anImageList;	
	}	
	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 9;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return images.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ICImage image = images.get(rowIndex);
		Object result = null;
		switch (columnIndex) { 
		case COL_ID:
			if (image.getSpecimen()!=null) { 
			    //result = image.getSpecimen().getSpecimenId();
				result = image.getSpecimen();
			} else { 
				// Kludge - work around for images not bound to specimens.
				//result = -1L;
				result = new Specimen();
			}	
			break;
		case COL_PATH:
			result = image.getPath();
			break;
		case COL_FILENAME: 
			result = image.getFilename();
			break;
		case 3: 
			result = image.getRawBarcode();
			break;
		case 4:
			result = image.getRawExifBarcode();
			break;
		case 5:
			result = image.getRawOcr();
			break;
		case COL_BARCODE:
			if (image.getSpecimen()!=null) { 
			result = image.getSpecimen().getBarcode();
			} else { 
				result = "";
			}
			break;
		case 7:
			result = image.getTemplateId();
			break;	
		case COL_DRAWER:
			result = image.getDrawerNumber();
			break;			
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
		case COL_PATH:
			result = "Path";
			break;
		case COL_FILENAME: 
			result = "Filename";
			break;
		case 3: 
			result = "Barcode (ocr)";
			break;
		case 4:
			result = "Barcode (scan)";
			break;
		case 5:
			result = "Raw OCR";
			break;
		case COL_BARCODE:
			result = "Barcode";
			break;
		case 7:
			result = "Template";
			break;
		case COL_DRAWER:
			result = "Drawer#";
			break;			
		}
		return result;
	} 
	
	/**
	 * Must be implemented for ButtonEditor to work.  Needs to return Long for 
	 * ID column that is to contain button to work with ButtonEditor.
	 */
	public Class<?> getColumnClass(int c) {
		 // Given current implementation of button in SpecimenBrowser,  
		 // needs to return Long for ID column that is to contain button 
		 // and ** Must Not ** return Long for any other column).
		Class<?> result = String.class;  // Default value to return when table is empty.
		try { 
		    result = getValueAt(0, c).getClass();
		} catch (NullPointerException e) { 
			// continue
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
