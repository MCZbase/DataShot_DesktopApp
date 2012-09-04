/**
 * EventLog.java
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
 * @author Paul J. Morris
 */
package edu.harvard.mcz.imagecapture;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import edu.harvard.mcz.imagecapture.data.Tracking;
import edu.harvard.mcz.imagecapture.data.TrackingLifeCycle;
import edu.harvard.mcz.imagecapture.data.TrackingTableModel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.util.List;

/** Display the history of database record creation and updates for specimens including the history 
 * of status changes in the workflow.  Can display either the history for a single specimen or the entire
 * tracking table for all specimens.  
 * 
 * @author Paul J. Morris
 */
public class EventLog extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButton = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JPanel jPanel = null;
	private EventLog eventLog = null;
	private List<Tracking> events = null;

	/**
	 * Default constructor shows a list of all entries in tracking table.
	 */
	public EventLog() {
		super();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//TODO: make this manageable for large numbers of records.  
		eventLog = this;
		TrackingLifeCycle t = new TrackingLifeCycle();
		events = (List<Tracking>) t.findAll();
		initialize();
	}
	
	public EventLog(List<Tracking> eventsList) {
		super();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		eventLog = this;
		events = eventsList;
		initialize();
	}	

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(709, 340);
		this.setContentPane(getJContentPane());
		this.setTitle("Event Log");
		Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation((screenSize.width -   this.getWidth()) / 2 , 
	                     (screenSize.height - this.getHeight()) / 2 );
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
			jContentPane.add(getJPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Close");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					eventLog.setVisible(false);
					eventLog.dispose();
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setPreferredSize(new Dimension(700,300));
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(new TrackingTableModel(events));
			jTable.setAutoCreateRowSorter(true);
		}
		return jTable;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButton(), new GridBagConstraints());
		}
		return jPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
