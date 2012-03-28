/**
 * UsersTableModel.java
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/** UsersTableModel
 * 
 * @author Paul J. Morris
 *
 */
public class UsersTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 2713593770437005589L;
	
	private List<Users> users = null;
	
	public UsersTableModel() { 
		users = new ArrayList<Users>();
	}
	
	/**
	 * @param findAll
	 */
	public UsersTableModel(List<Users> userList) {
		if (userList==null) { 
			users = new ArrayList<Users>();	
		} else { 
		    users = userList;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 5;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return users.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		switch (columnIndex) {
		case 0:
		    result = users.get(rowIndex);
		    break;
		case 1:
		    result = users.get(rowIndex).getUsername();
		    break;
		case 2:
		    result = users.get(rowIndex).getFullname();
		    break;
		case 3:
		    result = users.get(rowIndex).getDescription();
		    break;
		case 4:
		    result = users.get(rowIndex).getRole();
		    break;		    
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		String result = "";
		switch (column) { 
		case 0:
		    result = "";
		    break;
		case 1:
		    result = "Email";
		    break;
		case 2:
		    result = "Full Name";
		    break;
		case 3:
		    result = "About this person";
		    break;
		case 4:
		    result = "Role";
		    break;		    
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex==0) { 
			return true;
		} else { 
			return  false;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		super.setValueAt(value, rowIndex, columnIndex);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0) { 
			return Users.class;
		} else { 
			return String.class;
		}
	}

}
