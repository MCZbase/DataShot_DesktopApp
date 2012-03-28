/**
 * SearchDialog.java
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
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;

import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.CollectorLifeCycle;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.Tracking;
import edu.harvard.mcz.imagecapture.data.TrackingLifeCycle;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** SearchDialog
 * 
 * @author Paul J. Morris
 *
 */
public class SearchDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldDrawerNumber = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField jTextFieldBarcode = null;
	private JTextField jTextFieldFamily = null;
	private JTextField jTextFieldGenus = null;
	private JTextField jTextFieldSpecies = null;
	private JLabel jLabel4 = null;
	private JLabel jLabel5 = null;
	private JLabel jLabel6 = null;
	private JComboBox jComboBoxCollection = null;
	private JComboBox jComboBoxWorkflowStatus = null;
	private JLabel jLabel7 = null;
	private JLabel jLabel8 = null;
	private JLabel jLabel9 = null;
	private JTextField jTextFieldImageFilename = null;
	private JComboBox jComboBoxPath = null;
	private JComboBox jComboBoxEntryBy = null;
	private JLabel jLabel10 = null;
	private JTextField jTextFieldSubfamily = null;
	private JLabel jLabel11 = null;
	private JTextField jTextFieldSubspecies = null;
	private JButton jButton1 = null;
	private JLabel jLabel12 = null;
	private JLabel jLabel13 = null;
	private JComboBox jComboBoxCollector = null;
	private JLabel jLabel14 = null;
	private JLabel jLabel15 = null;
	private JLabel jLabel16 = null;
	private JTextField jTextFieldVerbatimLocality = null;
	private JComboBox jComboBoxCountry = null;
	private JComboBox jComboBoxQuestions = null;
	private JLabel jLabel17 = null;
	private JLabel jLabel18 = null;
	private JTextField jTextFieldTribe = null;
	private JLabel jLabel19 = null;
	private JTextField jTextFieldPrimaryDivision = null;
	/**
	 * @param owner
	 */
	public SearchDialog(Frame owner) {
		super(owner);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(415, 500);
		this.setTitle("Search For Specimens");
		this.setContentPane(getJContentPane());
		this.setPreferredSize(new Dimension(415, 500));
		this.pack();
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
			jContentPane.add(getJPanel(), BorderLayout.SOUTH);
			jContentPane.add(getJPanel1(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButton(), new GridBagConstraints());
			jPanel.add(getJButton1(), gridBagConstraints17);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Search");
			jButton.setMnemonic(KeyEvent.VK_S);
			this.getRootPane().setDefaultButton(jButton);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Specimen searchCriteria = new Specimen();
					// Default specimen is created with valid distribution flag = true, etc. need to remove this 
					// from the search criteria for a search by example.
					searchCriteria.clearDefaults();
					if (jTextFieldDrawerNumber.getText()!=null && jTextFieldDrawerNumber.getText().length()>0) { 
					    searchCriteria.setDrawerNumber(jTextFieldDrawerNumber.getText());
				    } 
					if (jTextFieldBarcode.getText()!=null && jTextFieldBarcode.getText().length() > 0) { 
						searchCriteria.setBarcode(jTextFieldBarcode.getText());
					}
					if (jTextFieldFamily.getText()!=null && jTextFieldFamily.getText().length() > 0) { 
						searchCriteria.setFamily(jTextFieldFamily.getText());
					}
					if (jTextFieldSubfamily.getText()!=null && jTextFieldSubfamily.getText().length() > 0) { 
						searchCriteria.setSubfamily(jTextFieldSubfamily.getText());
					}
					if (jTextFieldTribe.getText()!=null && jTextFieldTribe.getText().length() > 0) { 
						searchCriteria.setTribe(jTextFieldTribe.getText());
					}					
					if (jTextFieldGenus.getText()!=null && jTextFieldGenus.getText().length() > 0) { 
						searchCriteria.setGenus(jTextFieldGenus.getText());
					}
					if (jTextFieldSpecies.getText()!=null && jTextFieldSpecies.getText().length() > 0) { 
						searchCriteria.setSpecificEpithet(jTextFieldSpecies.getText());
					}
					if (jTextFieldSubspecies.getText()!=null && jTextFieldSubspecies.getText().length() > 0) { 
						searchCriteria.setSubspecificEpithet(jTextFieldSubspecies.getText());
					}
					if (jTextFieldVerbatimLocality.getText()!=null && jTextFieldVerbatimLocality.getText().length() > 0) { 
						searchCriteria.setVerbatimLocality(jTextFieldVerbatimLocality.getText());
					}
					if (jTextFieldPrimaryDivision.getText()!=null && jTextFieldPrimaryDivision.getText().length() > 0) { 
						searchCriteria.setPrimaryDivison(jTextFieldPrimaryDivision.getText());
					}
					if (jComboBoxWorkflowStatus.getSelectedItem()!=null) {
						if (!jComboBoxWorkflowStatus.getSelectedItem().toString().equals("")) { 
						   searchCriteria.setWorkFlowStatus(jComboBoxWorkflowStatus.getSelectedItem().toString());
						}
					}
					if (jComboBoxCountry.getSelectedItem()!=null) {
						if (!jComboBoxCountry.getSelectedItem().toString().equals("")) { 
						   searchCriteria.setCountry(jComboBoxCountry.getSelectedItem().toString());
						}
					}
					if (jComboBoxCollector.getSelectedItem()!=null) {
						if (!jComboBoxCollector.getSelectedItem().toString().equals("")) {
							Collector c = new Collector();
							c.setCollectorName(jComboBoxCollector.getSelectedItem().toString());
							Set<Collector> collectors = new HashSet<Collector>();
							collectors.add(c);
							searchCriteria.setCollectors(collectors);
						}
					}
					if ((jTextFieldImageFilename.getText()!=null && jTextFieldImageFilename.getText().length() > 0) ||
							(jComboBoxEntryBy.getSelectedItem()!=null))
					{ 
						// Either image filename or date imaged or both have content
						// so we need to add an image to the search criteria.
						ICImage i = new ICImage();
						
						if (jTextFieldImageFilename.getText()!=null && jTextFieldImageFilename.getText().length() > 0) {
							// if filename has content, add it
							i.setFilename(jTextFieldImageFilename.getText());
						}
						if (jComboBoxPath.getSelectedItem()!=null) {
							if (!jComboBoxPath.getSelectedItem().toString().equals("")) {
								// it the path = date imaged has content, add it
								i.setPath(jComboBoxPath.getSelectedItem().toString());
							}
						}
						Set<ICImage> im = new HashSet<ICImage>();
						im.add(i);
						searchCriteria.setICImages(im);
					}
					if (jComboBoxCollection.getSelectedItem()!=null) {
						if (!jComboBoxCollection.getSelectedItem().toString().equals("")) {
							searchCriteria.setCollection(jComboBoxCollection.getSelectedItem().toString());
						}
					}
					if (jComboBoxEntryBy.getSelectedItem()!=null) {
						if (!jComboBoxEntryBy.getSelectedItem().toString().equals("")) {
							Tracking tc = new Tracking();
							tc.setUser(jComboBoxEntryBy.getSelectedItem().toString());
							Set<Tracking>trackings = new HashSet<Tracking>();
							trackings.add(tc);
							searchCriteria.setTrackings(trackings);
						}
					}
					if (jComboBoxQuestions.getSelectedItem()!=null) {
						if (!jComboBoxQuestions.getSelectedItem().toString().equals("")) { 
						   searchCriteria.setQuestions(jComboBoxQuestions.getSelectedItem().toString());
						}
					}
					Singleton.getSingletonInstance().getMainFrame().setSpecimenBrowseList(searchCriteria);
					
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.fill = GridBagConstraints.BOTH;
			gridBagConstraints34.gridy = 14;
			gridBagConstraints34.weightx = 1.0;
			gridBagConstraints34.gridx = 1;
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints25.gridy = 14;
			jLabel19 = new JLabel();
			jLabel19.setText("State/Province");
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = GridBagConstraints.BOTH;
			gridBagConstraints24.gridy = 7;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.anchor = GridBagConstraints.WEST;
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.anchor = GridBagConstraints.EAST;
			gridBagConstraints110.gridy = 7;
			jLabel18 = new JLabel();
			jLabel18.setText("Tribe");
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.gridx = 0;
			gridBagConstraints71.gridwidth = 2;
			gridBagConstraints71.gridy = 1;
			jLabel17 = new JLabel();
			jLabel17.setText("Use %_% in a field to find all records with a value in that field.");
			GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
			gridBagConstraints62.fill = GridBagConstraints.BOTH;
			gridBagConstraints62.gridy = 18;
			gridBagConstraints62.weightx = 1.0;
			gridBagConstraints62.gridx = 1;
			GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
			gridBagConstraints52.fill = GridBagConstraints.BOTH;
			gridBagConstraints52.gridy = 13;
			gridBagConstraints52.weightx = 1.0;
			gridBagConstraints52.gridx = 1;
			GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
			gridBagConstraints43.fill = GridBagConstraints.BOTH;
			gridBagConstraints43.gridy = 12;
			gridBagConstraints43.weightx = 1.0;
			gridBagConstraints43.gridx = 1;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints33.gridy = 13;
			jLabel16 = new JLabel();
			jLabel16.setText("Country");
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints23.gridy = 12;
			jLabel15 = new JLabel();
			jLabel15.setText("Verbatim Locality");
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints19.gridy = 18;
			jLabel14 = new JLabel();
			jLabel14.setText("Questions");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = GridBagConstraints.BOTH;
			gridBagConstraints18.gridy = 16;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 1;
			gridBagConstraints61.gridy = 0;
			jLabel13 = new JLabel();
			jLabel13.setText("Search for specimens. Use % as a wildcard.");
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 0;
			gridBagConstraints51.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints51.gridy = 16;
			jLabel12 = new JLabel();
			jLabel12.setText("Collector");
			GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
			gridBagConstraints42.fill = GridBagConstraints.BOTH;
			gridBagConstraints42.gridy = 11;
			gridBagConstraints42.weightx = 1.0;
			gridBagConstraints42.gridx = 1;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints32.gridy = 11;
			jLabel11 = new JLabel();
			jLabel11.setText("Subspecies");
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.fill = GridBagConstraints.BOTH;
			gridBagConstraints22.gridy = 8;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.gridx = 1;
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints16.gridy = 8;
			jLabel10 = new JLabel();
			jLabel10.setText("Subfamily");
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.fill = GridBagConstraints.BOTH;
			gridBagConstraints15.gridy = 19;
			gridBagConstraints15.weightx = 1.0;
			gridBagConstraints15.gridx = 1;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = GridBagConstraints.BOTH;
			gridBagConstraints14.gridy = 3;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.gridy = 2;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.gridx = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints12.gridy = 19;
			jLabel9 = new JLabel();
			jLabel9.setText("Entry By");
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.gridx = 0;
			gridBagConstraints111.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints111.gridy = 3;
			jLabel8 = new JLabel();
			jLabel8.setText("Date Imaged");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints10.gridy = 2;
			jLabel7 = new JLabel();
			jLabel7.setText("Image Filename");
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 17;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 15;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints7.gridy = 17;
			jLabel6 = new JLabel();
			jLabel6.setText("Workflow Status");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints6.gridy = 15;
			jLabel5 = new JLabel();
			jLabel5.setText("Collection");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints5.gridy = 10;
			jLabel4 = new JLabel();
			jLabel4.setText("Species");
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.fill = GridBagConstraints.BOTH;
			gridBagConstraints41.gridy = 10;
			gridBagConstraints41.weightx = 1.0;
			gridBagConstraints41.gridx = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.fill = GridBagConstraints.BOTH;
			gridBagConstraints31.gridy = 9;
			gridBagConstraints31.weightx = 1.0;
			gridBagConstraints31.gridx = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 6;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 5;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints4.gridy = 9;
			jLabel3 = new JLabel();
			jLabel3.setText("Genus");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints3.gridy = 6;
			jLabel2 = new JLabel();
			jLabel2.setText("Family");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints2.gridy = 5;
			jLabel1 = new JLabel();
			jLabel1.setText("Barcode");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 4;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints.gridy = 4;
			jLabel = new JLabel();
			jLabel.setText("Drawer Number");
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(jLabel, gridBagConstraints);
			jPanel1.add(getJTextField(), gridBagConstraints1);
			jPanel1.add(jLabel1, gridBagConstraints2);
			jPanel1.add(jLabel2, gridBagConstraints3);
			jPanel1.add(jLabel3, gridBagConstraints4);
			jPanel1.add(getJTextField1(), gridBagConstraints11);
			jPanel1.add(getJTextField2(), gridBagConstraints21);
			jPanel1.add(getJTextField3(), gridBagConstraints31);
			jPanel1.add(getJTextField4(), gridBagConstraints41);
			jPanel1.add(jLabel4, gridBagConstraints5);
			jPanel1.add(jLabel5, gridBagConstraints6);
			jPanel1.add(jLabel6, gridBagConstraints7);
			jPanel1.add(getJComboBox(), gridBagConstraints8);
			jPanel1.add(getJComboBox1(), gridBagConstraints9);
			jPanel1.add(jLabel7, gridBagConstraints10);
			jPanel1.add(jLabel8, gridBagConstraints111);
			jPanel1.add(jLabel9, gridBagConstraints12);
			jPanel1.add(getJTextField5(), gridBagConstraints13);
			jPanel1.add(getJComboBox2(), gridBagConstraints14);
			jPanel1.add(getJComboBox3(), gridBagConstraints15);
			jPanel1.add(jLabel10, gridBagConstraints16);
			jPanel1.add(getJTextField6(), gridBagConstraints22);
			jPanel1.add(jLabel11, gridBagConstraints32);
			jPanel1.add(getJTextField12(), gridBagConstraints42);
			jPanel1.add(jLabel12, gridBagConstraints51);
			jPanel1.add(jLabel13, gridBagConstraints61);
			jPanel1.add(getJComboBox4(), gridBagConstraints18);
			jPanel1.add(jLabel14, gridBagConstraints19);
			jPanel1.add(jLabel15, gridBagConstraints23);
			jPanel1.add(jLabel16, gridBagConstraints33);
			jPanel1.add(getJTextField7(), gridBagConstraints43);
			jPanel1.add(getJComboBox5(), gridBagConstraints52);
			jPanel1.add(getJComboBox12(), gridBagConstraints62);
			jPanel1.add(jLabel17, gridBagConstraints71);
			jPanel1.add(jLabel18, gridBagConstraints110);
			jPanel1.add(getJTextFieldTribe(), gridBagConstraints24);
			jPanel1.add(jLabel19, gridBagConstraints25);
			jPanel1.add(getJTextFieldPrimaryDivision(), gridBagConstraints34);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldDrawerNumber == null) {
			jTextFieldDrawerNumber = new JTextField();
		}
		return jTextFieldDrawerNumber;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (jTextFieldBarcode == null) {
			jTextFieldBarcode = new JTextField();
		}
		return jTextFieldBarcode;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextFieldFamily == null) {
			jTextFieldFamily = new JTextField();
		}
		return jTextFieldFamily;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (jTextFieldGenus == null) {
			jTextFieldGenus = new JTextField();
		}
		return jTextFieldGenus;
	}

	/**
	 * This method initializes jTextField4	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField4() {
		if (jTextFieldSpecies == null) {
			jTextFieldSpecies = new JTextField();
		}
		return jTextFieldSpecies;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBoxCollection == null) {
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			jComboBoxCollection = new JComboBox(sls.getDistinctCollections());
			jComboBoxCollection.setEditable(true);
		}
		return jComboBoxCollection;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox1() {
		if (jComboBoxWorkflowStatus == null) {
			ArrayList<String> values = new ArrayList<String>();
			values.add(""); 
			String[] wfsv = WorkFlowStatus.getWorkFlowStatusValues();
			for (int x=0; x<wfsv.length; x++) { 
				values.add(wfsv[x]);
			}
			jComboBoxWorkflowStatus = new JComboBox(values.toArray());
			jComboBoxWorkflowStatus.getModel().setSelectedItem("");
			jComboBoxWorkflowStatus.setEditable(true);
		}
		return jComboBoxWorkflowStatus;
	}

	/**
	 * This method initializes jTextField5	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField5() {
		if (jTextFieldImageFilename == null) {
			jTextFieldImageFilename = new JTextField();
		}
		return jTextFieldImageFilename;
	}

	/**
	 * This method initializes jComboBox2	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox2() {
		if (jComboBoxPath == null) {
			ICImageLifeCycle ils = new ICImageLifeCycle();
			jComboBoxPath = new JComboBox(ils.getDistinctPaths());
			jComboBoxPath.setEditable(true);
		}
		return jComboBoxPath;
	}

	/**
	 * This method initializes jComboBox3	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox3() {
		if (jComboBoxEntryBy == null) {
			TrackingLifeCycle tls = new TrackingLifeCycle();
			jComboBoxEntryBy = new JComboBox(tls.getDistinctUsers());
			jComboBoxEntryBy.setEditable(true);
		}
		return jComboBoxEntryBy;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField6() {
		if (jTextFieldSubfamily == null) {
			jTextFieldSubfamily = new JTextField();
		}
		return jTextFieldSubfamily;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField12() {
		if (jTextFieldSubspecies == null) {
			jTextFieldSubspecies = new JTextField();
		}
		return jTextFieldSubspecies;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Close");
			jButton1.setMnemonic(KeyEvent.VK_C);
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox4() {
		if (jComboBoxCollector == null) {
			CollectorLifeCycle cls = new CollectorLifeCycle();
			jComboBoxCollector = new JComboBox(cls.getDistinctCollectors());
			jComboBoxCollector.setEditable(true);
		}
		return jComboBoxCollector;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField7() {
		if (jTextFieldVerbatimLocality == null) {
			jTextFieldVerbatimLocality = new JTextField();
		}
		return jTextFieldVerbatimLocality;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox5() {
		if (jComboBoxCountry == null) {
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			ArrayList<String> values = new ArrayList<String>();
			values.add(""); 
			values.add("%_%");
			String[] cv = sls.getDistinctCountries();
			for (int x=0; x<cv.length; x++) { 
				values.add(cv[x]);
			}
			jComboBoxCountry = new JComboBox(values.toArray());
			jComboBoxCountry.setEditable(true);
		}
		return jComboBoxCountry;
	}

	/**
	 * This method initializes jComboBox1	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox12() {
		if (jComboBoxQuestions == null) {
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			ArrayList<String> values = new ArrayList<String>();
			values.add(""); 
			values.add("%_%");
			String[] qv = sls.getDistinctQuestions();
			for (int x=0; x<qv.length; x++) { 
				values.add(qv[x]);
			}
			jComboBoxQuestions = new JComboBox(values.toArray());
			jComboBoxQuestions.setEditable(true);
		}
		return jComboBoxQuestions;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldTribe() {
		if (jTextFieldTribe == null) {
			jTextFieldTribe = new JTextField();
		}
		return jTextFieldTribe;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldPrimaryDivision() {
		if (jTextFieldPrimaryDivision == null) {
			jTextFieldPrimaryDivision = new JTextField();
		}
		return jTextFieldPrimaryDivision;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
