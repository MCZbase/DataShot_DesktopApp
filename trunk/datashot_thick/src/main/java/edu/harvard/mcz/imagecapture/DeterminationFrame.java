/**
 * DeterminationFrame.java
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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.harvard.mcz.imagecapture.data.Determination;
import edu.harvard.mcz.imagecapture.data.DeterminationTableModel;
import edu.harvard.mcz.imagecapture.data.Specimen;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;

/** DeterminationFrame
 * 
 * @author Paul J. Morris
 *
 */
public class DeterminationFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	
    private DeterminationTableModel determinations = null;
    private Specimen specimen = null;
	private JPanel jPanel = null;
	private JButton jButtonAdd = null;
	private JButton jButtonDone = null;

	/**
	 * This is the default constructor
	 */
	public DeterminationFrame() {
		super();
		this.determinations = new DeterminationTableModel();
		initialize();
		jButtonAdd.setEnabled(false);
	}
	
	/**
	 * Constructor to show an arbitrary list of determinations.
	 * 
	 * @param determinations
	 */
	public DeterminationFrame(DeterminationTableModel determinations) {
		super();
		this.determinations = determinations;
		initialize();
		jButtonAdd.setEnabled(false);
	}	
	
	/** Constructor to show a list of determinations for a particular specimen.
	 * 
	 * @param aSpecimen
	 */
	public DeterminationFrame(Specimen aSpecimen) {
		super();
		determinations = new DeterminationTableModel(aSpecimen.getDeterminations());
		specimen = aSpecimen;
		initialize();
		jButtonAdd.setEnabled(true);
	}	
	
	public void setSpecimen(Specimen aSpecimen) { 
		determinations = new DeterminationTableModel(aSpecimen.getDeterminations());
		jTable.setModel(determinations);
		jButtonAdd.setEnabled(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(940, 351);
		this.setPreferredSize(new Dimension(1250, 350));
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
		this.pack();
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
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
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
			jTable = new JTable();
			DeterminationTableModel model = new DeterminationTableModel();
			jTable.setModel(model);
			if (determinations!=null) { 
				jTable.setModel(determinations);
			}
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
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButtonAdd(), new GridBagConstraints());
			jPanel.add(getJButtonDone(), gridBagConstraints);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton();
			jButtonAdd.setText("Add");
			jButtonAdd.setMnemonic(KeyEvent.VK_A);
			jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (specimen!=null) { 
					   if (jTable.isEditing()) { 
						   jTable.getCellEditor().stopCellEditing();
					   }
					   Determination d = new Determination();
			           d.setSpecimen(specimen);
					   determinations.addDetermination(d);
					} 
				}
			});
		}
		return jButtonAdd;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonDone() {
		if (jButtonDone == null) {
			jButtonDone = new JButton();
			jButtonDone.setText("Done");
			jButtonDone.setMnemonic(KeyEvent.VK_D);
			jButtonDone.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Make sure changes in field that was modal before button
					// click are saved to the model.
					if (jTable.isEditing()) { 
					    jTable.getCellEditor().stopCellEditing();
					}
					//
					setVisible(false);
				}
			});
		}
		return jButtonDone;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
