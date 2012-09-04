/**
 * ValidatingTableCellEditor.java
 * edu.harvard.mcz.imagecapture
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
package edu.harvard.mcz.imagecapture;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;


/** ValidatingTableCellEditor provides a JTextField with an inputVerifier as a
 * cell editor that can be used as an editor for cells in a JTable.
 * 
 * Example:
 * <pre>
 	 JTextField field = new JTextField();
	 field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
	 jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field));
   </pre>
 * 
 * @author Paul J. Morris
 *
 */
public class ValidatingTableCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -4777010317672887845L;
	
	private JTextField field;

	/**
	 * @param textField (with an inputVerifier already set)
	 */
	public ValidatingTableCellEditor(JTextField textField) {
		super(textField);
		field = textField;
	}

	@Override
	  public Object getCellEditorValue() {
	    return field.getText();
	  }
	
	
//	/* (non-Javadoc)
//	 * @see javax.swing.DefaultCellEditor#cancelCellEditing()
//	 */
//	@Override
//	public void cancelCellEditing() {
//		if (field.isValid()) { 
//			field.setBackground(MainFrame.BG_COLOR_ENT_FIELD);
//		   super.cancelCellEditing();
//		} else { 
//			field.setBackground(MainFrame.BG_COLOR_ERROR);
//		}
//	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#stopCellEditing()
	 */
	@Override
	public boolean stopCellEditing() {		
		if (field.getInputVerifier().shouldYieldFocus(field)) { 
			field.setBackground(Color.WHITE);
		   return super.stopCellEditing();
		} else {
			field.setBackground(MainFrame.BG_COLOR_ERROR);
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.DefaultCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub
		if (value!=null) { 
		   field.setText(value.toString());
		} else { 
		   field.setText("");
		}
		return field;
	}

}
