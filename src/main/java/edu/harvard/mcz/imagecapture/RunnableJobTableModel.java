/**
 * JobTableModel.java
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

import java.text.DateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;

/** JobTableModel
 * 
 * @author Paul J. Morris
 *
 */
public class RunnableJobTableModel extends AbstractTableModel implements RunnerListener {

	private static final long serialVersionUID = 2701547555516905743L;
	
	private static final Log log = LogFactory.getLog(RunnableJobTableModel.class);
	
	private Set<RunnableJob> jobs;
	
	/**
	 * 
	 */
	public RunnableJobTableModel() {
		jobs = new HashSet<RunnableJob>();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 4;
	}
	
	/**
	 * Must be implemented for ButtonEditor to work.  Needs to return Integer for 
	 * row number column that is to contain button to work with ButtonEditor.
	 */
	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		 // Given current implementation of button in SpecimenBrowser,  
		 // needs to return Long for ID column that is to contain button 
		 // and ** Must Not ** return Long for any other column).
		Class result = String.class;  // Default value to return when table is empty.
		switch (columnIndex) { 
		case  0: 
		    result = Integer.class;
		    break;
		case 1:  
		    result = String.class;
		    break;
		case 2:  
		    result = String.class;
		    break;
		case 3:  
		    result = int.class;
		    break;		    
		}
        return result; 
    }

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return jobs.size();
	}

	public String getColumnName(int columnIndex) {
		String returnvalue = null;
		switch (columnIndex) { 
		case  0: 
		    returnvalue = " ";
		    break;
		case 1:  
		    returnvalue = "Job";
		    break;
		case 2: 
			returnvalue = "Started";
			break;
		case 3: 
			returnvalue = "Progress";
			break;			
		}
		return returnvalue;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object returnvalue = null;
		try { 
			switch (columnIndex) { 
			case  0: 
				returnvalue = (Integer)rowIndex;
				break;
			case 1:  
				returnvalue = ((RunnableJob)jobs.toArray()[rowIndex]).getName();
				break;
			case 2: 
				if (((RunnableJob)jobs.toArray()[rowIndex]).getStartTime()==null) { 
					returnvalue = "----";
				} else { 
					returnvalue = DateFormat.getTimeInstance().format(((RunnableJob)jobs.toArray()[rowIndex]).getStartTime());
				}
				break;
			case 3:  
				returnvalue = ((RunnableJob)jobs.toArray()[rowIndex]).percentComplete();
				log.debug(returnvalue);
				break;			
			}
		} catch (ArrayIndexOutOfBoundsException e) { 
			log.debug(e.getMessage());
			fireTableDataChanged();
		} catch (NullPointerException e) { 
			log.debug(e.getMessage());
			fireTableDataChanged();
		}
		return returnvalue;
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		boolean returnvalue = false;
		switch (columnIndex) {
		case  0: 
			returnvalue = true;
			break;
		case 1:  
			returnvalue = false;
			break;
		case 2:  
			returnvalue = false;
			break;
		case 3:  
			returnvalue = false;
			break;			
		}
		return returnvalue;
	}

	public RunnableJob getJobAt(Integer rowIndex) {
		RunnableJob returnvalue = null;
		returnvalue = (RunnableJob) jobs.toArray()[rowIndex];
		return returnvalue;
	}
	
	/** addJob adds a RunnableJob to this model as a new row
	 * @param aJob the RunnableJob to add to the table model.
	 */
	public void addJob(RunnableJob aJob) { 
		jobs.add(aJob);
		aJob.registerListener(this);
		this.fireTableDataChanged();
	}
	
	public void removeJob(RunnableJob aJob) { 
		jobs.remove(aJob);
		this.fireTableDataChanged();
	}
	
	public void registerListener(RunnerListener listener) { 
		Iterator<RunnableJob> i = jobs.iterator();
		while (i.hasNext()) { 
			i.next().registerListener(listener);
		}
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnerListener#notifyListener(int, edu.harvard.mcz.imagecapture.interfaces.RunnableJob)
	 */
	@Override
	public void notifyListener(int anEvent, RunnableJob notifyingJob) {
		log.debug(anEvent);
		this.fireTableDataChanged();
	}

}
