package edu.harvard.mcz.imagecapture.data;

import java.util.HashSet;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

/**
 * NumberTableModel Table model for displaying and editing Number records in a JTable.
 * 
 * @author Paul J. Morris
 *
 */
public class NumberTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 2244991738218368487L;
	
	public static final int COLUMN_TYPE = 1;
	
	private Set <Number> numbers = null;
	
	public NumberTableModel() { 
		numbers = new HashSet<Number>();
	}
	
	public NumberTableModel(Set <Number> aNumberList) { 
		numbers = aNumberList;	
	}
	
	public void addNumber(Number aNumber) { 
		numbers.add(aNumber);
		this.fireTableDataChanged();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		String returnvalue = null;
		switch (columnIndex) {
		case 0: 
			returnvalue = "Number";
			break;
		case COLUMN_TYPE: 
			returnvalue = "Type";
			break;
		}
		return returnvalue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return numbers.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object returnvalue = null;
		switch (columnIndex) { 
		case 0: 
			returnvalue = ((Number)numbers.toArray()[rowIndex]).getNumber();
			break;
		case COLUMN_TYPE: 
			returnvalue = ((Number)numbers.toArray()[rowIndex]).getNumberType();
			break;
		}
		return returnvalue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		switch (columnIndex) { 
		case 0: 
			((Number)numbers.toArray()[rowIndex]).setNumber((String)value);
			break;
		case COLUMN_TYPE: 
			((Number)numbers.toArray()[rowIndex]).setNumberType((String)value);
			break;
		}
		
	}
}
