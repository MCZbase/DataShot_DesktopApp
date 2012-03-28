/**
 * SpecimenBrowser.java
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

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageListTableModel;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.data.Specimen;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.util.ArrayList;

/** ImageListBrowser is a Searchable, Sortable, tabular view of multiple specimen images with Edit buttons
 * to display the details for a specimen found in an image.
 * 
 * @author Paul J. Morris
 *
 */
public class ImageListBrowser extends JPanel {

	private static final long serialVersionUID = 1336228109288304785L;
	
	private boolean showJustMissmatches = false;
	private JScrollPane jScrollPane = null;
	private JTable jTableImages = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldFilename = null;
	private JComboBox jComboBox = null;
	private JLabel jLabel1 = null;
	private TableRowSorter<ICImageListTableModel> sorter;
	private JTextField jTextFieldBarcode = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField jTextFieldDrawerNumber = null;
	private ICImage searchCriteria = null;

	/**
	 * This method initializes an instance of SpecimenBrowser  
	 * 
	 */
	public ImageListBrowser() {
		super();
		showJustMissmatches = false;
		searchCriteria = null;
		initialize();
	}
	
	public ImageListBrowser(boolean onlyShowMissmatches) { 
		super();
		showJustMissmatches = onlyShowMissmatches;
		searchCriteria = null;
		initialize();		
	}
	
	public ImageListBrowser(ICImage imageSearchCriteria) { 
		super();
		showJustMissmatches = false;
		searchCriteria = imageSearchCriteria;
		initialize();		
	}	

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
        this.setSize(new Dimension(444, 290));
        this.add(getJScrollPane(), BorderLayout.CENTER);
        this.add(getJPanel(), BorderLayout.NORTH);
			
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
			jScrollPane.setPreferredSize(new Dimension(444, 290));
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTableImages == null) {
			jTableImages = new JTable();
			ICImageLifeCycle s = new ICImageLifeCycle();
			ICImageListTableModel model = null;
			if (showJustMissmatches) { 
				model = new ICImageListTableModel(ICImageLifeCycle.findMismatchedImages());	
			} else { 
				if (searchCriteria==null) { 
				    model = new ICImageListTableModel(s.findAll());
				} else { 
					model = new ICImageListTableModel(s.findByExample(searchCriteria));
				}
			}
			if (model!=null) { 
			   jTableImages.setModel(model);
			    sorter = new TableRowSorter<ICImageListTableModel>(model);
				jTableImages.setRowSorter(sorter);
			}
			jTableImages.setDefaultRenderer(Specimen.class, new ButtonRenderer());
            jTableImages.setDefaultEditor(Specimen.class, new ButtonEditor());
		}
		return jTableImages;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.BOTH;
			gridBagConstraints22.gridy = 0;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.anchor = GridBagConstraints.WEST;
			gridBagConstraints22.gridx = 5;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 4;
			gridBagConstraints12.gridy = 0;
			jLabel3 = new JLabel();
			jLabel3.setText("Drawer:");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.gridy = 0;
			jLabel2 = new JLabel();
			jLabel2.setText("Barcode:");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 0;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.anchor = GridBagConstraints.WEST;
			gridBagConstraints11.gridx = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 6;
			gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints2.gridy = 0;
			jLabel1 = new JLabel();
			jLabel1.setText("Path:");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 7;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.anchor = GridBagConstraints.WEST;
			gridBagConstraints.weightx = 1.0;
			jLabel = new JLabel();
			jLabel.setText("Find Filename:");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(jLabel, new GridBagConstraints());
			jPanel.add(getJTextField(), gridBagConstraints);
			jPanel.add(getJComboBox(), gridBagConstraints1);
			jPanel.add(jLabel1, gridBagConstraints2);
			jPanel.add(getJTextField2(), gridBagConstraints11);
			jPanel.add(jLabel2, gridBagConstraints21);
			jPanel.add(jLabel3, gridBagConstraints12);
			jPanel.add(getJTextField3(), gridBagConstraints22);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldFilename == null) {
			jTextFieldFilename = new JTextField();
			jTextFieldFilename.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					newFilter();
				}
			});
		}
		return jTextFieldFilename;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			ICImageLifeCycle ils = new ICImageLifeCycle();
	 		jComboBox = new JComboBox(ils.getDistinctPaths());
	 		jComboBox.setEditable(true);
		    TableColumn pathColumn = jTableImages.getColumnModel().getColumn(ICImageListTableModel.COL_PATH);
		    pathColumn.setCellEditor(new DefaultCellEditor(jComboBox));
			jComboBox.addItem("");
			jComboBox.setSelectedItem("");
			jComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jTextFieldFilename.setText("");
					newFilter();
				}
			});
		}
		return jComboBox;
	}
	
    private void newFilter() {
        RowFilter<ICImageListTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
        	if (jTextFieldFilename.getText().equals("") && jTextFieldBarcode.getText().equals("") && jTextFieldDrawerNumber.getText().equals("")) { 
        		rf = RowFilter.regexFilter(jComboBox.getSelectedItem().toString(), ICImageListTableModel.COL_PATH);
        	}  else { 
        		RowFilter<ICImageListTableModel, Object> rf_filename = null;
        		RowFilter<ICImageListTableModel, Object> rf_barcode = null;
        		RowFilter<ICImageListTableModel, Object> rf_drawer = null;
                rf_filename = RowFilter.regexFilter(jTextFieldFilename.getText(), ICImageListTableModel.COL_FILENAME);
        	    rf_barcode = RowFilter.regexFilter(jTextFieldBarcode.getText(), ICImageListTableModel.COL_BARCODE);
        	    rf_drawer = RowFilter.regexFilter(jTextFieldDrawerNumber.getText(), ICImageListTableModel.COL_DRAWER);
        	    ArrayList<RowFilter<ICImageListTableModel, Object>> i = new ArrayList<RowFilter<ICImageListTableModel, Object>>();
        	    i.add(rf_filename);
        	    i.add(rf_barcode);
        	    i.add(rf_drawer);
        	    rf = RowFilter.andFilter((Iterable<RowFilter<ICImageListTableModel, Object>>)i);
        	} 
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
		sorter.setRowFilter(rf);
    }

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextFieldBarcode == null) {
			jTextFieldBarcode = new JTextField();
			jTextFieldBarcode.setText("");
			jTextFieldBarcode.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					newFilter();
				}
			});
		}
		return jTextFieldBarcode;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (jTextFieldDrawerNumber == null) {
			jTextFieldDrawerNumber = new JTextField();
			jTextFieldDrawerNumber.setText("");
			jTextFieldDrawerNumber.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					newFilter();
				}
			});
		}
		return jTextFieldDrawerNumber;
	}
	
	public int getRowCount() { 
		int result = 0;
		if (jTableImages!=null)  {
			result = jTableImages.getRowCount();
		}
		return result;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
