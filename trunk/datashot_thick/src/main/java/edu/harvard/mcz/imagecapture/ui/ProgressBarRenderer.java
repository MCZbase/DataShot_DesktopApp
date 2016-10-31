/**
 * ProgressBarRenderer.java
 * edu.harvard.mcz.imagecapture.ui
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
package edu.harvard.mcz.imagecapture.ui;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class ProgressBarRenderer implements TableCellRenderer {
	
	private static final Log log = LogFactory.getLog(ProgressBarRenderer.class);

	private static final long serialVersionUID = 2860749265938403343L;
	
	private JProgressBar progressBar = null;
	
	public ProgressBarRenderer() { 
		progressBar = new JProgressBar();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		log.debug(value);
		if (value!=null) { 
			try { 
				progressBar.setValue((int) value);
			} catch (ClassCastException e) { 
				log.error(e.getMessage());
			}
		}
		log.debug(progressBar.getValue());
		return progressBar;
	}
}
