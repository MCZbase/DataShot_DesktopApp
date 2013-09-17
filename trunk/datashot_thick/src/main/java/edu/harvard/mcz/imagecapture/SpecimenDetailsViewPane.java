/**
 * SpecimenDetailsViewPane.java
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
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;

import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.CollectorTableModel;
import edu.harvard.mcz.imagecapture.data.Features;
import edu.harvard.mcz.imagecapture.data.HigherTaxonLifeCycle;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.LifeStage;
import edu.harvard.mcz.imagecapture.data.LocationInCollection;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.NumberLifeCycle;
import edu.harvard.mcz.imagecapture.data.NumberTableModel;
import edu.harvard.mcz.imagecapture.data.Sex;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPart;
import edu.harvard.mcz.imagecapture.data.SpecimenPartAttribute;
import edu.harvard.mcz.imagecapture.data.SpecimenPartAttributeLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPartLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPartsTableModel;
import edu.harvard.mcz.imagecapture.data.Tracking;
import edu.harvard.mcz.imagecapture.data.TrackingLifeCycle;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SpecimenExistsException;

import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * JPanel for editing a record of a Specimen in a details view for that specimen. 
 * 
 * @author Paul J. Morris
 * 
 *  TODO: BugID: 10 add length limits (remaining to do for Number/Collector tables, 
 *  and for JComboBox fields).
 */
public class SpecimenDetailsViewPane extends JPanel {
	
	private static final Log log = LogFactory.getLog(SpecimenDetailsViewPane.class);
	
	private static final long serialVersionUID = 3716072190995030749L;
	
	private static final int STATE_CLEAN = 0;
	private static final int STATE_DIRTY = 1;
	
	private Specimen specimen;  //  @jve:decl-index=0:
	private SpecimenControler myControler = null;
	private int state;   // dirty if data in controls has been changed and not saved to specimen.
	
	private JTextField jTextFieldStatus = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldBarcode = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldGenus = null;
	private JTextField jTextFieldSpecies = null;
	private JTextField jTextFieldSubspecies = null;
	private JTextField jTextFieldLocality = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JButton jButton = null;
	private JLabel jLabel5 = null;
	private JComboBox jComboBoxCollection = null;  
	private JLabel jLabel6 = null;
	private JTextField jTextFieldLastUpdatedBy = null;
	private JScrollPane jScrollPaneCollectors = null;
	private JTable jTableCollectors = null;
	private JScrollPane jScrollPaneSpecimenParts = null;
	private JTable jTableSpecimenParts = null;
	private JLabel jLabel7 = null;
	private JLabel jLabel8 = null;
	private JTextField jTextFieldDateLastUpdated = null;
	private JTextField jTextFieldCreator = null;
	private JLabel jLabel9 = null;
	private JTextField jTextFieldDateCreated = null;
	private JLabel jLabel10 = null;
	private JLabel jLabel11 = null;
	private JScrollPane jScrollPaneNumbers = null;
	private JTable jTableNumbers = null;
	private JButton jButtonNumbersAdd = null;
	private JButton jButtonCollsAdd = null;
	private JScrollPane jScrollPane = null;
	private JTextPane jTextPaneWarnings = null;
	private JLabel jLabel12 = null;
	private JTextField jTextFieldDrawerNumber = null;
	private JLabel jLabel13 = null;
	private JLabel jLabel14 = null;
	private JLabel jLabel15 = null;
	private JTextField jTextFieldVerbatimLocality = null;
	private JTextField jTextFieldCountry = null;
	private JTextField jTextFieldPrimaryDivision = null;
	private JLabel jLabel16 = null;
	private JLabel jLabel17 = null;
	private JLabel jLabel18 = null;
	private JComboBox jComboBoxFamily = null;
	private JComboBox jComboBoxSubfamily = null;
	private JTextField jTextFieldTribe = null;
	private JLabel jLabel19 = null;
	private JLabel jLabel20 = null;
	private JLabel jLabel21 = null;
	private JComboBox jComboBoxSex = null;
	private JComboBox jComboBoxFeatures = null;
	private JComboBox jComboBoxLifeStage = null;
	private JLabel jLabel22 = null;
	private JTextField jTextFieldDateNos = null;
	private JTextField jTextFieldDateEmerged = null;
	private JTextField jTextFieldDateEmergedIndicator = null;
	private JTextField jTextFieldDateCollected = null;
	private JTextField jTextFieldDateCollectedIndicator = null;
	private JLabel jLabel25 = null;
	private JLabel jLabel26 = null;
	private JLabel jLabel27 = null;
	private JTextField jTextFieldInfraspecificEpithet = null;
	private JLabel jLabel28 = null;
	private JTextField jTextFieldInfraspecificRank = null;
	private JLabel jLabel29 = null;
	private JTextField jTextFieldAuthorship = null;
	private JLabel jLabel30 = null;
	private JTextField jTextFieldUnnamedForm = null;
	private JLabel jLabel32 = null;
	private JTextField jTextFieldVerbatimElevation = null;
	private JTextField jTextFieldCollectingMethod = null;
	private JLabel jLabel33 = null;
	private JLabel jLabel34 = null;
	private JLabel jLabel35 = null;
	private JTextArea jTextAreaSpecimenNotes = null;
	private JCheckBox jCheckBoxValidDistributionFlag = null;
	private JLabel jLabel36 = null;
	private JLabel jLabel37 = null;
	private JLabel jLabel38 = null;
	private JLabel jLabel39 = null;
	private JLabel jLabel40 = null;
	private JTextField jLabelMigrationStatus = null;
	private JTextField jTextFieldQuestions = null;
	// private JTextField jTextFieldPreparationType = null;
	private JButton jButtonAddPreparationType;
	private JTextField jTextFieldAssociatedTaxon = null;
	private JTextField jTextFieldHabitat = null;
	private JLabel jLabel41 = null;
	private JComboBox jComboBoxWorkflowStatus = null;
	private JLabel jLabel42 = null;
	private JComboBox jComboBoxLocationInCollection = null;
	private JLabel jLabel43 = null;
	private JTextField jTextFieldInferences = null;
	private JButton jButtonGetHistory = null;
	private JButton jButtonNext = null;
	private SpecimenDetailsViewPane thisPane = null;
    private JButton jButtonPrevious = null;
    private JPanel jPanel1 = null;   // panel for navigation buttons
	private JTextField jTextFieldISODate = null;
	private JButton jButtonDeterminations = null;
	private JLabel jLabel31 = null;
	private JTextField jTextFieldCitedInPub = null;
	private JScrollPane jScrollPane1 = null;
	private JLabel jLabel44 = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JButton jButtonSpecificLocality = null;
	private JLabel jTextFieldImageCount = null;

	/**
	 * Construct an instance of a SpecimenDetailsViewPane showing the data present
	 * in aSpecimenInstance. 
	 * 
	 * @param aSpecimenInstance the Specimen instance to display for editing.
	 */
	public SpecimenDetailsViewPane(Specimen aSpecimenInstance, SpecimenControler aControler) { 
		specimen = aSpecimenInstance;
		SpecimenLifeCycle s = new SpecimenLifeCycle();
		setStateToClean();
//		SpecimenPartAttributeLifeCycle spals = new SpecimenPartAttributeLifeCycle();
//		Iterator<SpecimenPart> i = specimen.getSpecimenParts().iterator();
//		while (i.hasNext()) { 
//			Iterator<SpecimenPartAttribute> ia = i.next().getAttributeCollection().iterator();
//			while (ia.hasNext()) { 
//				try {
//					SpecimenPartAttribute spa = ia.next();
//					log.debug(spa.getSpecimenPartAttributeId());
//					spals.attachDirty(spa);
//					log.debug(spa.getSpecimenPartAttributeId());
//				} catch (SaveFailedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		s.attachClean(specimen);
	    myControler = aControler;
		initialize();
		setValues();	
		thisPane = this;
	}
	
	public void setWarning(String warning) { 
		jTextPaneWarnings.setText(warning);
		jTextPaneWarnings.setForeground(Color.RED);
	}
	
	private void setWarnings() { 
		log.debug("In set warnings");
		if (specimen.getICImages()!=null) { 
			log.debug("specimen.getICImages is not null");
			java.util.Iterator<ICImage> i = specimen.getICImages().iterator();
			log.debug(i.hasNext());
			while (i.hasNext()) { 
				log.debug("Checking image " + i );
				ICImage im = i.next();
				String rbc = "";
				if (im.getRawBarcode()!=null) { rbc = im.getRawBarcode(); }
				String ebc = "";
				if (im.getRawExifBarcode()!=null) { ebc = im.getRawExifBarcode(); } 
				if (!rbc.equals(ebc)) { 
					jTextPaneWarnings.setText("Warning: An image has missmatch between Comment and Barcode.");
					jTextPaneWarnings.setForeground(Color.RED);
					log.debug("Setting: Warning: Image has missmatch between Comment and Barcode.");
				}
			}
		}
	}
		
	private void save() { 
		try { 
		jTextFieldStatus.setText("Saving");
		if (jTableCollectors.isEditing()) { 
		    jTableCollectors.getCellEditor().stopCellEditing();
		}
		if (jTableSpecimenParts.isEditing()) { 
		    jTableSpecimenParts.getCellEditor().stopCellEditing();
		}		
		if (jTableNumbers.isEditing()) { 
		    jTableNumbers.getCellEditor().stopCellEditing();
		}
		specimen.setLocationInCollection(jComboBoxLocationInCollection.getSelectedItem().toString());
		specimen.setDrawerNumber(jTextFieldDrawerNumber.getText());
	    if (jComboBoxFamily.getSelectedIndex()==-1 && jComboBoxFamily.getSelectedItem()==null) { 
	    	specimen.setFamily("");
	    } else {
	    	specimen.setFamily(jComboBoxFamily.getSelectedItem().toString());
	    }
	    if (jComboBoxSubfamily.getSelectedIndex()==-1 && jComboBoxSubfamily.getSelectedItem()==null) { 
	    	specimen.setSubfamily("");
	    } else {
	    	specimen.setSubfamily(jComboBoxSubfamily.getSelectedItem().toString());
	    }
		specimen.setTribe(jTextFieldTribe.getText());
	    specimen.setGenus(jTextFieldGenus.getText());
	    specimen.setSpecificEpithet(jTextFieldSpecies.getText());
	    specimen.setSubspecificEpithet(jTextFieldSubspecies.getText());
	    specimen.setInfraspecificEpithet(jTextFieldInfraspecificEpithet.getText());
	    specimen.setInfraspecificRank(jTextFieldInfraspecificRank.getText());
	    specimen.setAuthorship(jTextFieldAuthorship.getText());
	    specimen.setUnNamedForm(jTextFieldUnnamedForm.getText());
	    specimen.setVerbatimLocality(jTextFieldVerbatimLocality.getText());
	    specimen.setCountry(jTextFieldCountry.getText());
	    specimen.setValidDistributionFlag(jCheckBoxValidDistributionFlag.isSelected());
	    specimen.setPrimaryDivison(jTextFieldPrimaryDivision.getText());
	    specimen.setSpecificLocality(jTextFieldLocality.getText());
	    specimen.setVerbatimElevation(jTextFieldVerbatimElevation.getText());
	    specimen.setCollectingMethod(jTextFieldCollectingMethod.getText());
	    specimen.setIsoDate(jTextFieldISODate.getText());
	    specimen.setDateNos(jTextFieldDateNos.getText());
	    specimen.setDateCollected(jTextFieldDateCollected.getText());
	    specimen.setDateEmerged(jTextFieldDateEmerged.getText());
	    specimen.setDateCollectedIndicator(jTextFieldDateCollectedIndicator.getText());
	    specimen.setDateEmergedIndicator(jTextFieldDateEmergedIndicator.getText());
	    if (jComboBoxCollection.getSelectedIndex()==-1 && jComboBoxCollection.getSelectedItem()==null) { 
	    	specimen.setCollection("");
	    } else {
	    	specimen.setCollection(jComboBoxCollection.getSelectedItem().toString());
	    }
	    if (jComboBoxFeatures.getSelectedIndex()==-1 && jComboBoxFeatures.getSelectedItem()==null) { 
	    	    specimen.setFeatures("");
	    } else {
	        specimen.setFeatures(jComboBoxFeatures.getSelectedItem().toString());
	    }
	    if (jComboBoxLifeStage.getSelectedIndex()==-1 && jComboBoxLifeStage.getSelectedItem()==null) { 
	    	specimen.setLifeStage("");
	    } else {
	        specimen.setLifeStage(jComboBoxLifeStage.getSelectedItem().toString());
	    }
	    if (jComboBoxSex.getSelectedIndex()==-1 && jComboBoxSex.getSelectedItem()==null) { 
	    	specimen.setSex("");
	    } else {
	        specimen.setSex(jComboBoxSex.getSelectedItem().toString());
	        log.debug("jComboBoxSex selectedIndex=" + jComboBoxSex.getSelectedIndex());
	    }
	    
        log.debug("sex=" + specimen.getSex()); 
        
        specimen.setCitedInPublication(jTextFieldCitedInPub.getText());
	    //specimen.setPreparationType(jTextFieldPreparationType.getText());
	    specimen.setAssociatedTaxon(jTextFieldAssociatedTaxon.getText());
	    specimen.setHabitat(jTextFieldHabitat.getText());
	    specimen.setSpecimenNotes(jTextAreaSpecimenNotes.getText());
	    specimen.setInferences(jTextFieldInferences.getText());
	    specimen.setLastUpdatedBy(Singleton.getSingletonInstance().getUserFullName());
	    specimen.setDateLastUpdated(new Date());
	    specimen.setWorkFlowStatus(jComboBoxWorkflowStatus.getSelectedItem().toString());
	    specimen.setQuestions(jTextFieldQuestions.getText());
	    try { 
	    	myControler.save();   // save the record
            setStateToClean();    // enable the navigation buttons
	    	jTextFieldStatus.setText("Saved");  // inform the user
	    	jTextFieldStatus.setForeground(Color.BLACK);
	    	setWarnings();
	    	jTextFieldLastUpdatedBy.setText(specimen.getLastUpdatedBy());
	    	jTextFieldDateLastUpdated.setText(specimen.getDateLastUpdated().toString());
	    } catch (SaveFailedException e) { 
	    	setStateToDirty();    // disable the navigation buttons
	    	jTextFieldStatus.setText("Error. " + e.getMessage()); 
	    	jTextFieldStatus.setForeground(MainFrame.BG_COLOR_ERROR);
	    }
	    SpecimenLifeCycle sls = new SpecimenLifeCycle();
		Singleton.getSingletonInstance().getMainFrame().setCount(sls.findSpecimenCount());
		} catch (Exception e) {
			// trap any exception and notify the user
	    	setStateToDirty();    // disable the navigation buttons
	    	jTextFieldStatus.setText("Error. " + e.getMessage()); 
	    	jTextFieldStatus.setForeground(MainFrame.BG_COLOR_ERROR);
		}
		updateDeterminationCount();
	}
	
	private void setValues() { 
		jTextFieldStatus.setText("Loading");
		
		jTextFieldBarcode.setText(specimen.getBarcode());
		jComboBoxLocationInCollection.setSelectedItem(specimen.getLocationInCollection());
		jTextFieldDrawerNumber.setText(specimen.getDrawerNumber());
		jComboBoxFamily.setSelectedItem(specimen.getFamily());
		jComboBoxSubfamily.setSelectedItem(specimen.getSubfamily());
		jTextFieldTribe.setText(specimen.getTribe());
		jTextFieldGenus.setText(specimen.getGenus());
		jTextFieldSpecies.setText(specimen.getSpecificEpithet());
		jTextFieldSubspecies.setText(specimen.getSubspecificEpithet());
		jTextFieldInfraspecificEpithet.setText(specimen.getInfraspecificEpithet());
		jTextFieldInfraspecificRank.setText(specimen.getInfraspecificRank());
		jTextFieldAuthorship.setText(specimen.getAuthorship());
		jTextFieldUnnamedForm.setText(specimen.getUnNamedForm());
		jTextFieldVerbatimLocality.setText(specimen.getVerbatimLocality());
		jTextFieldCountry.setText(specimen.getCountry());
		if (specimen.getValidDistributionFlag()!=null) { 
		    jCheckBoxValidDistributionFlag.setSelected(specimen.getValidDistributionFlag());
		} else { 
			jCheckBoxValidDistributionFlag.setSelected(false);
		}
		jTextFieldPrimaryDivision.setText(specimen.getPrimaryDivison());
		jTextFieldLocality.setText(specimen.getSpecificLocality());
		jTextFieldVerbatimElevation.setText(specimen.getVerbatimElevation());
		jTextFieldCollectingMethod.setText(specimen.getCollectingMethod());
		jTextFieldISODate.setText(specimen.getIsoDate());
		jTextFieldDateNos.setText(specimen.getDateNos());
		jTextFieldDateCollected.setText(specimen.getDateCollected());
		jTextFieldDateEmerged.setText(specimen.getDateEmerged());
		jTextFieldDateCollectedIndicator.setText(specimen.getDateCollectedIndicator());
		jTextFieldDateEmergedIndicator.setText(specimen.getDateEmergedIndicator());
		jComboBoxCollection.setSelectedItem(specimen.getCollection());
		//jTextFieldPreparationType.setText(specimen.getPreparationType());
		jTextFieldAssociatedTaxon.setText(specimen.getAssociatedTaxon());
		jTextFieldHabitat.setText(specimen.getHabitat());
		jTextAreaSpecimenNotes.setText(specimen.getSpecimenNotes());
		jComboBoxFeatures.setSelectedItem(specimen.getFeatures());
		jComboBoxLifeStage.setSelectedItem(specimen.getLifeStage());
		jComboBoxSex.setSelectedItem(specimen.getSex());
		jTextFieldCitedInPub.setText(specimen.getCitedInPublication());
		jTextFieldQuestions.setText(specimen.getQuestions());
		jComboBoxWorkflowStatus.setSelectedItem(specimen.getWorkFlowStatus());
	    if (specimen.isStateDone()) { 
	    	jLabelMigrationStatus.setText("http://mczbase.mcz.harvard.edu/guid/MCZ:Ent:" + specimen.getCatNum());
	    } else { 
	    	jLabelMigrationStatus.setText("");
	    }
		jTextFieldInferences.setText(specimen.getInferences());
		jTextFieldCreator.setText(specimen.getCreatedBy());
		if (specimen.getDateCreated()!=null) { 
		   jTextFieldDateCreated.setText(specimen.getDateCreated().toString());
		} 
		jTextFieldLastUpdatedBy.setText(specimen.getLastUpdatedBy());
		if (specimen.getDateLastUpdated()!=null) { 
		   jTextFieldDateLastUpdated.setText(specimen.getDateLastUpdated().toString());
		} 
		
		jTableNumbers.setModel(new NumberTableModel(specimen.getNumbers()));
		// Setting the model will overwrite the existing cell editor bound 
		// to the column model, so we need to add it again.
		JTextField field1 = new JTextField();
		field1.setInputVerifier(MetadataRetriever.getInputVerifier(edu.harvard.mcz.imagecapture.data.Number.class, "Number", field1));
		field1.setVerifyInputWhenFocusTarget(true);
		jTableNumbers.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field1));
		JComboBox jComboNumberTypes = new JComboBox(NumberLifeCycle.getDistinctTypes());
		jComboNumberTypes.setEditable(true);
		TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
		typeColumn.setCellEditor(new DefaultCellEditor(jComboNumberTypes));
		
		jTableCollectors.setModel(new CollectorTableModel(specimen.getCollectors()));
		// Setting the model will overwrite the existing cell editor bound 
		// to the column model, so we need to add it again.
		JTextField field = new JTextField();
		field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
		field.setVerifyInputWhenFocusTarget(true);
		jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field));
		
		jTableSpecimenParts.setModel(new SpecimenPartsTableModel(specimen.getSpecimenParts()));
		setSpecimenPartsTableCellEditors();
		
		updateDeterminationCount();
		
		if (specimen.getICImages()!=null) { 
			int imageCount = specimen.getICImages().size();
			jTextFieldImageCount.setText("Number of Images="+ imageCount);
			if (imageCount>1) { 
				jTextFieldImageCount.setForeground(Color.RED);
			} else { 
				jTextFieldImageCount.setForeground(Color.BLACK);
			}
		}
		
		setWarnings();
		this.setStateToClean();
		jTextFieldStatus.setText("Loaded");
	}

	private void updateDeterminationCount() {
		if (specimen.getDeterminations()==null) { 
			jButtonDeterminations.setText("0 Dets.");
		} else { 
		    if (specimen.getDeterminations().size()==1) { 
		    	jButtonDeterminations.setText(Integer.toString(specimen.getDeterminations().size()) + " Det.");
		    } else {
			    jButtonDeterminations.setText(Integer.toString(specimen.getDeterminations().size()) + " Dets.");
		    }
		}
	}
	
     /* initializes this
	 * 
	 */
	private void initialize() {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setHgap(0);
		borderLayout.setVgap(0);
		this.setLayout(borderLayout);
		this.setSize(new Dimension(594, 900));
		//this.setPreferredSize(new Dimension(490, 917));
	    this.add(getJTextFieldStatus(), BorderLayout.SOUTH);
	    // this.add(getJPanel(), BorderLayout.CENTER);
	    if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
	    		 ImageCaptureProperties.KEY_DETAILS_SCROLL).equals(ImageCaptureProperties.VALUE_DETAILS_SCROLL_FORCE_ON)) { 
	    JScrollPane scrollPane = new JScrollPane(getJPanel(),
	    		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	    		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    this.add(scrollPane, BorderLayout.CENTER);
	    } else { 
	    	this.add(getJPanel(), BorderLayout.CENTER);
	    }
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldStatus() {
		if (jTextFieldStatus == null) {
			jTextFieldStatus = new JTextField("Status");
			jTextFieldStatus.setEditable(false);
			jTextFieldStatus.setEnabled(true);
		}
		return jTextFieldStatus;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints216 = new GridBagConstraints();
			gridBagConstraints216.fill = GridBagConstraints.BOTH;
			gridBagConstraints216.gridy = 6;
			gridBagConstraints216.weightx = 1.0;
			gridBagConstraints216.anchor = GridBagConstraints.WEST;
			gridBagConstraints216.gridwidth = 2;
			gridBagConstraints216.insets = new Insets(0, 3, 5, 5);
			gridBagConstraints216.ipadx = 3;
			gridBagConstraints216.gridx = 4;
			GridBagConstraints gridBagConstraints124 = new GridBagConstraints();
			gridBagConstraints124.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints124.gridx = 0;
			gridBagConstraints124.anchor = GridBagConstraints.EAST;
			gridBagConstraints124.gridy = 19;
			GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
			gridBagConstraints38.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints38.gridx = 0;
			gridBagConstraints38.anchor = GridBagConstraints.EAST;
			gridBagConstraints38.gridy = 25;
			GridBagConstraints gridBagConstraints215 = new GridBagConstraints();
			gridBagConstraints215.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints215.gridx = 0;
			gridBagConstraints215.anchor = GridBagConstraints.EAST;
			gridBagConstraints215.gridy = 24;
			GridBagConstraints gridBagConstraints120 = new GridBagConstraints();
			gridBagConstraints120.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints120.gridx = 3;
			gridBagConstraints120.weightx = 1.0;
			gridBagConstraints120.gridy = 23;
			jLabel44 = new JLabel();
			jLabel44.setText("yyyy/mm/dd");
			GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
			gridBagConstraints49.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints49.fill = GridBagConstraints.BOTH;
			gridBagConstraints49.weighty = 1.0;
			gridBagConstraints49.weightx = 1.0;
			gridBagConstraints49.gridx = 1;
			gridBagConstraints49.gridwidth = 5;
			gridBagConstraints49.gridy = 33;
			GridBagConstraints gridBagConstraints214 = new GridBagConstraints();
			gridBagConstraints214.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints214.anchor = GridBagConstraints.EAST;
			gridBagConstraints214.gridx = 4;
			gridBagConstraints214.gridy = 40;
			gridBagConstraints214.weightx = 0.0;
			gridBagConstraints214.weighty = 0.0;
			gridBagConstraints214.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints119 = new GridBagConstraints();
			gridBagConstraints119.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints119.anchor = GridBagConstraints.WEST;
			gridBagConstraints119.gridx = 5;
			gridBagConstraints119.gridy = 40;
			gridBagConstraints119.weightx = 0.0;
			gridBagConstraints119.weighty = 0.0;
			gridBagConstraints119.fill = GridBagConstraints.NONE;
			GridBagConstraints gridBagConstraints213 = new GridBagConstraints();
			gridBagConstraints213.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints213.fill = GridBagConstraints.BOTH;
			gridBagConstraints213.gridy = 29;
			gridBagConstraints213.weightx = 0.0;
			gridBagConstraints213.gridwidth = 5;
			gridBagConstraints213.anchor = GridBagConstraints.WEST;
			gridBagConstraints213.gridx = 1;
			GridBagConstraints gridBagConstraints118 = new GridBagConstraints();
			gridBagConstraints118.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints118.gridx = 0;
			gridBagConstraints118.anchor = GridBagConstraints.EAST;
			gridBagConstraints118.gridwidth = 1;
			gridBagConstraints118.gridy = 29;
			jLabel31 = new JLabel();
			jLabel31.setText("Publications");
			GridBagConstraints gridBagConstraints212 = new GridBagConstraints();
			gridBagConstraints212.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints212.gridx = 3;
			gridBagConstraints212.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints212.gridwidth = 2;
			gridBagConstraints212.gridy = 15;
			GridBagConstraints gridBagConstraints117 = new GridBagConstraints();
			gridBagConstraints117.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints117.fill = GridBagConstraints.BOTH;
			gridBagConstraints117.gridy = 23;
			gridBagConstraints117.weightx = 0.0;
			gridBagConstraints117.gridwidth = 2;
			gridBagConstraints117.anchor = GridBagConstraints.WEST;
			gridBagConstraints117.gridx = 4;
			GridBagConstraints gridBagConstraints211 = new GridBagConstraints();
			gridBagConstraints211.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints211.gridx = 3;
			gridBagConstraints211.gridwidth = 2;
			gridBagConstraints211.weighty = 1.0;
			gridBagConstraints211.gridy = 42;
			GridBagConstraints gridBagConstraintsMS = new GridBagConstraints();
			gridBagConstraintsMS.insets = new Insets(0, 0, 0, 5);
			gridBagConstraintsMS.fill = GridBagConstraints.BOTH;
			gridBagConstraintsMS.gridx = 0;
			gridBagConstraintsMS.gridwidth = 6;
			gridBagConstraintsMS.weighty = 1.0;
			gridBagConstraintsMS.gridy = 43;			
			gridBagConstraintsMS.gridx = 0;
			
			GridBagConstraints gridBagConstraints116 = new GridBagConstraints();
			gridBagConstraints116.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints116.gridx = 4;
			gridBagConstraints116.anchor = GridBagConstraints.EAST;
			gridBagConstraints116.gridy = 39;
			GridBagConstraints gridBagConstraints66 = new GridBagConstraints();
			gridBagConstraints66.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints66.fill = GridBagConstraints.BOTH;
			gridBagConstraints66.gridy = 34;
			gridBagConstraints66.weightx = 1.0;
			gridBagConstraints66.anchor = GridBagConstraints.WEST;
			gridBagConstraints66.gridwidth = 5;
			gridBagConstraints66.gridx = 1;
			GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
			gridBagConstraints56.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints56.gridx = 0;
			gridBagConstraints56.anchor = GridBagConstraints.EAST;
			gridBagConstraints56.gridy = 34;
			jLabel43 = new JLabel();
			jLabel43.setText("Inferences");
			GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
			gridBagConstraints48.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints48.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints48.gridy = 0;
			gridBagConstraints48.weightx = 1.0;
			gridBagConstraints48.anchor = GridBagConstraints.WEST;
			gridBagConstraints48.gridwidth = 2;
			gridBagConstraints48.gridx = 4;
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints37.gridx = 3;
			gridBagConstraints37.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints37.gridy = 0;
			jLabel42 = new JLabel();
			jLabel42.setText("Collection");
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints29.fill = GridBagConstraints.BOTH;
			gridBagConstraints29.gridy = 39;
			gridBagConstraints29.weightx = 1.0;
			gridBagConstraints29.anchor = GridBagConstraints.WEST;
			gridBagConstraints29.gridwidth = 3;
			gridBagConstraints29.gridx = 1;
			GridBagConstraints gridBagConstraints115 = new GridBagConstraints();
			gridBagConstraints115.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints115.gridx = 0;
			gridBagConstraints115.gridy = 39;
			jLabel41 = new JLabel();
			jLabel41.setText("Workflow Status");
			GridBagConstraints gridBagConstraints172 = new GridBagConstraints();
			gridBagConstraints172.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints172.fill = GridBagConstraints.BOTH;
			gridBagConstraints172.gridy = 31;
			gridBagConstraints172.weightx = 1.0;
			gridBagConstraints172.anchor = GridBagConstraints.WEST;
			gridBagConstraints172.gridwidth = 3;
			gridBagConstraints172.gridx = 4;
			GridBagConstraints gridBagConstraints162 = new GridBagConstraints();
			gridBagConstraints162.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints162.fill = GridBagConstraints.BOTH;
			gridBagConstraints162.gridy = 31;
			gridBagConstraints162.weightx = 1.0;
			gridBagConstraints162.anchor = GridBagConstraints.WEST;
			gridBagConstraints162.gridwidth = 2;
			gridBagConstraints162.gridx = 1;
			GridBagConstraints gridBagConstraints142 = new GridBagConstraints();
			gridBagConstraints142.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints142.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints142.gridy = 40;
			gridBagConstraints142.weightx = 1.0;
			gridBagConstraints142.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints142.gridwidth = 3;
			gridBagConstraints142.ipady = 2;
			gridBagConstraints142.gridx = 1;
			GridBagConstraints gridBagConstraints133 = new GridBagConstraints();
			gridBagConstraints133.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints133.gridx = 0;
			gridBagConstraints133.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints133.gridy = 40;
			jLabel40 = new JLabel();
			jLabel40.setText("Questions");
			GridBagConstraints gridBagConstraints123 = new GridBagConstraints();
			gridBagConstraints123.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints123.gridx = 3;
			gridBagConstraints123.anchor = GridBagConstraints.EAST;
			gridBagConstraints123.gridy = 31;
			jLabel39 = new JLabel();
			jLabel39.setText("Habitat");
			GridBagConstraints gridBagConstraints114 = new GridBagConstraints();
			gridBagConstraints114.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints114.gridx = 0;
			gridBagConstraints114.anchor = GridBagConstraints.EAST;
			gridBagConstraints114.gridy = 31;
			jLabel38 = new JLabel();
			jLabel38.setText("Associated Taxon");
			GridBagConstraints gridBagConstraints94 = new GridBagConstraints();
			gridBagConstraints94.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints94.gridx = 4;
			gridBagConstraints94.anchor = GridBagConstraints.WEST;
			gridBagConstraints94.gridy = 17;
			jLabel36 = new JLabel();
			jLabel36.setText("Valid Dist.");
			GridBagConstraints gridBagConstraints83 = new GridBagConstraints();
			gridBagConstraints83.gridx = 3;
			gridBagConstraints83.anchor = GridBagConstraints.EAST;
			gridBagConstraints83.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints83.weightx = 1.0;
			gridBagConstraints83.gridy = 17;
			GridBagConstraints gridBagConstraints65 = new GridBagConstraints();
			gridBagConstraints65.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints65.gridx = 0;
			gridBagConstraints65.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints65.gridy = 33;
			jLabel35 = new JLabel();
			jLabel35.setText("Specimen Notes");
			GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
			gridBagConstraints47.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints47.gridx = 0;
			gridBagConstraints47.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints47.ipady = 12;
			gridBagConstraints47.gridy = 5;
			jLabel34 = new JLabel();
			jLabel34.setText("     ");
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints28.gridx = 4;
			gridBagConstraints28.anchor = GridBagConstraints.WEST;
			gridBagConstraints28.fill = GridBagConstraints.NONE;
			gridBagConstraints28.gridy = 21;
			jLabel33 = new JLabel();
			jLabel33.setText("Collecting Method");
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints19.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints19.gridy = 22;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints19.gridwidth = 2;
			gridBagConstraints19.gridx = 4;
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints18.fill = GridBagConstraints.BOTH;
			gridBagConstraints18.gridy = 18;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			gridBagConstraints18.gridwidth = 2;
			gridBagConstraints18.gridx = 4;
			GridBagConstraints gridBagConstraints171 = new GridBagConstraints();
			gridBagConstraints171.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints171.gridx = 3;
			gridBagConstraints171.anchor = GridBagConstraints.EAST;
			gridBagConstraints171.gridy = 18;
			jLabel32 = new JLabel();
			jLabel32.setText("Elevation");
			jLabel25 = new JLabel();
			jLabel25.setText("Text");
			jLabel26 = new JLabel();
			jLabel26.setText("Text");
			jLabel28 = new JLabel();
			jLabel28.setText("Rank");
			GridBagConstraints gridBagConstraints161 = new GridBagConstraints();
			gridBagConstraints161.gridx = 0;
			GridBagConstraints gridBagConstraints151 = new GridBagConstraints();
			gridBagConstraints151.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints151.fill = GridBagConstraints.BOTH;
			gridBagConstraints151.gridy = 25;
			gridBagConstraints151.weightx = 1.0;
			gridBagConstraints151.gridx = 1;
			GridBagConstraints gridBagConstraints141 = new GridBagConstraints();
			gridBagConstraints141.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints141.fill = GridBagConstraints.BOTH;
			gridBagConstraints141.gridy = 24;
			gridBagConstraints141.weightx = 1.0;
			gridBagConstraints141.gridx = 1;
			GridBagConstraints gridBagConstraints132 = new GridBagConstraints();
			gridBagConstraints132.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints132.fill = GridBagConstraints.BOTH;
			gridBagConstraints132.gridy = 25;
			gridBagConstraints132.weightx = 1.0;
			gridBagConstraints132.gridx = 4;
			GridBagConstraints gridBagConstraints122 = new GridBagConstraints();
			gridBagConstraints122.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints122.fill = GridBagConstraints.BOTH;
			gridBagConstraints122.gridy = 24;
			gridBagConstraints122.weightx = 1.0;
			gridBagConstraints122.gridx = 4;
			GridBagConstraints gridBagConstraints113 = new GridBagConstraints();
			gridBagConstraints113.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints113.gridx = 3;
			gridBagConstraints113.anchor = GridBagConstraints.EAST;
			gridBagConstraints113.gridy = 25;
			GridBagConstraints gridBagConstraints103 = new GridBagConstraints();
			gridBagConstraints103.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints103.gridx = 3;
			gridBagConstraints103.anchor = GridBagConstraints.EAST;
			gridBagConstraints103.gridy = 24;
			GridBagConstraints gridBagConstraints93 = new GridBagConstraints();
			gridBagConstraints93.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints93.fill = GridBagConstraints.BOTH;
			gridBagConstraints93.gridy = 12;
			gridBagConstraints93.weightx = 1.0;
			gridBagConstraints93.gridx = 1;
			GridBagConstraints gridBagConstraints84 = new GridBagConstraints();
			gridBagConstraints84.fill = GridBagConstraints.BOTH;
			gridBagConstraints84.gridy = 12;
			gridBagConstraints84.weightx = 1.0;
			gridBagConstraints84.anchor = GridBagConstraints.WEST;
			gridBagConstraints84.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints84.gridx = 4;
			GridBagConstraints gridBagConstraints74 = new GridBagConstraints();
			gridBagConstraints74.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints74.gridx = 3;
			gridBagConstraints74.anchor = GridBagConstraints.EAST;
			gridBagConstraints74.weightx = 0.0;
			gridBagConstraints74.gridy = 12;
//			GridBagConstraints gridBagConstraints64 = new GridBagConstraints();
//			gridBagConstraints64.fill = GridBagConstraints.BOTH;
//			gridBagConstraints64.gridy = 14;
//			gridBagConstraints64.weightx = 1.0;
//			gridBagConstraints64.gridx = 3;
			GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
			gridBagConstraints55.gridx = 2;
			gridBagConstraints55.gridy = 14;
			//jLabel31 = new JLabel();
			//jLabel31.setText("Qual.");
			GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
			gridBagConstraints46.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints46.fill = GridBagConstraints.BOTH;
			gridBagConstraints46.gridy = 14;
			gridBagConstraints46.weightx = 1.0;
			gridBagConstraints46.anchor = GridBagConstraints.WEST;
			gridBagConstraints46.gridx = 1;
			gridBagConstraints46.gridwidth = 3;
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints36.gridx = 0;
			gridBagConstraints36.anchor = GridBagConstraints.EAST;
			gridBagConstraints36.gridy = 14;
			jLabel30 = new JLabel();
			jLabel30.setText("Unnamed Form");
			GridBagConstraints gridBagConstraints210 = new GridBagConstraints();
			gridBagConstraints210.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints210.fill = GridBagConstraints.BOTH;
			gridBagConstraints210.gridy = 13;
			gridBagConstraints210.weightx = 1.0;
			gridBagConstraints210.anchor = GridBagConstraints.WEST;
			gridBagConstraints210.gridwidth = 3;
			gridBagConstraints210.gridx = 1;
			GridBagConstraints gridBagConstraints112 = new GridBagConstraints();
			gridBagConstraints112.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints112.gridx = 0;
			gridBagConstraints112.anchor = GridBagConstraints.EAST;
			gridBagConstraints112.gridy = 13;
			jLabel29 = new JLabel();
			jLabel29.setText("Author");
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.gridy = 12;
			jLabel27 = new JLabel();
			jLabel27.setText("Infrasubspecifc Name");
			GridBagConstraints gridBagConstraints63 = new GridBagConstraints();
			gridBagConstraints63.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints63.fill = GridBagConstraints.BOTH;
			gridBagConstraints63.gridy = 23;
			gridBagConstraints63.weightx = 1.0;
			gridBagConstraints63.anchor = GridBagConstraints.WEST;
			gridBagConstraints63.gridx = 1;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints35.gridx = 0;
			gridBagConstraints35.anchor = GridBagConstraints.EAST;
			gridBagConstraints35.gridy = 23;
			jLabel22 = new JLabel();
			jLabel22.setText("Verbatim Date");
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints27.fill = GridBagConstraints.BOTH;
			gridBagConstraints27.gridy = 28;
			gridBagConstraints27.weightx = 1.0;
			gridBagConstraints27.anchor = GridBagConstraints.WEST;
			gridBagConstraints27.gridx = 1;
			GridBagConstraints gridBagConstraints102 = new GridBagConstraints();
			gridBagConstraints102.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints102.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints102.gridy = 28;
			gridBagConstraints102.weightx = 1.0;
			gridBagConstraints102.anchor = GridBagConstraints.WEST;
			gridBagConstraints102.gridwidth = 2;
			gridBagConstraints102.gridx = 4;
			GridBagConstraints gridBagConstraints92 = new GridBagConstraints();
			gridBagConstraints92.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints92.gridx = 3;
			gridBagConstraints92.anchor = GridBagConstraints.EAST;
			gridBagConstraints92.gridy = 28;
			jLabel21 = new JLabel();
			jLabel21.setText("Sex");
			GridBagConstraints gridBagConstraints82 = new GridBagConstraints();
			gridBagConstraints82.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints82.gridx = 0;
			gridBagConstraints82.anchor = GridBagConstraints.EAST;
			gridBagConstraints82.gridy = 28;
			jLabel20 = new JLabel();
			jLabel20.setText("LifeStage");
			GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
			gridBagConstraints62.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints62.fill = GridBagConstraints.BOTH;
			gridBagConstraints62.gridy = 8;
			gridBagConstraints62.weightx = 1.0;
			gridBagConstraints62.anchor = GridBagConstraints.WEST;
			gridBagConstraints62.gridwidth = 3;
			gridBagConstraints62.gridx = 1;
			GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
			gridBagConstraints53.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints53.fill = GridBagConstraints.BOTH;
			gridBagConstraints53.gridy = 7;
			gridBagConstraints53.weightx = 1.0;
			gridBagConstraints53.anchor = GridBagConstraints.WEST;
			gridBagConstraints53.gridwidth = 3;
			gridBagConstraints53.gridx = 1;
			GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
			gridBagConstraints44.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints44.fill = GridBagConstraints.BOTH;
			gridBagConstraints44.gridy = 6;
			gridBagConstraints44.weightx = 1.0;
			gridBagConstraints44.anchor = GridBagConstraints.WEST;
			gridBagConstraints44.gridwidth = 3;
			gridBagConstraints44.gridx = 1;
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints34.gridx = 0;
			gridBagConstraints34.anchor = GridBagConstraints.EAST;
			gridBagConstraints34.gridy = 8;
			jLabel18 = new JLabel();
			jLabel18.setText("Tribe");
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.anchor = GridBagConstraints.EAST;
			gridBagConstraints26.gridy = 7;
			jLabel17 = new JLabel();
			jLabel17.setText("Subfamily");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints16.gridx = 0;
			gridBagConstraints16.anchor = GridBagConstraints.EAST;
			gridBagConstraints16.gridy = 6;
			jLabel16 = new JLabel();
			jLabel16.setText("Family");
			GridBagConstraints gridBagConstraints131 = new GridBagConstraints();
			gridBagConstraints131.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints131.fill = GridBagConstraints.BOTH;
			gridBagConstraints131.gridy = 18;
			gridBagConstraints131.weightx = 1.0;
			gridBagConstraints131.anchor = GridBagConstraints.WEST;
			gridBagConstraints131.gridwidth = 2;
			gridBagConstraints131.gridx = 1;
			GridBagConstraints gridBagConstraints121 = new GridBagConstraints();
			gridBagConstraints121.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints121.fill = GridBagConstraints.BOTH;
			gridBagConstraints121.gridy = 17;
			gridBagConstraints121.weightx = 1.0;
			gridBagConstraints121.anchor = GridBagConstraints.WEST;
			gridBagConstraints121.gridwidth = 1;
			gridBagConstraints121.gridx = 1;
			GridBagConstraints gridBagConstraints111 = new GridBagConstraints();
			gridBagConstraints111.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints111.fill = GridBagConstraints.BOTH;
			gridBagConstraints111.gridy = 16;
			gridBagConstraints111.weightx = 1.0;
			gridBagConstraints111.anchor = GridBagConstraints.WEST;
			gridBagConstraints111.gridwidth = 5;
			gridBagConstraints111.gridx = 1;
			GridBagConstraints gridBagConstraints101 = new GridBagConstraints();
			gridBagConstraints101.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints101.gridx = 0;
			gridBagConstraints101.anchor = GridBagConstraints.EAST;
			gridBagConstraints101.gridy = 18;
			jLabel15 = new JLabel();
			jLabel15.setText("State/Province");
			GridBagConstraints gridBagConstraints91 = new GridBagConstraints();
			gridBagConstraints91.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints91.gridx = 0;
			gridBagConstraints91.anchor = GridBagConstraints.EAST;
			gridBagConstraints91.gridy = 17;
			jLabel14 = new JLabel();
			jLabel14.setText("Country");
			GridBagConstraints gridBagConstraints81 = new GridBagConstraints();
			gridBagConstraints81.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints81.gridx = 0;
			gridBagConstraints81.anchor = GridBagConstraints.EAST;
			gridBagConstraints81.gridy = 16;
			jLabel13 = new JLabel();
			jLabel13.setText("Verbatim Locality");
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints71.fill = GridBagConstraints.BOTH;
			gridBagConstraints71.gridy = 15;
			gridBagConstraints71.weightx = 1.0;
			gridBagConstraints71.anchor = GridBagConstraints.WEST;
			gridBagConstraints71.gridx = 1;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.anchor = GridBagConstraints.EAST;
			gridBagConstraints61.gridy = 15;
			jLabel12 = new JLabel();
			jLabel12.setText("DrawerNumber");
			GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
			gridBagConstraints52.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints52.fill = GridBagConstraints.BOTH;
			gridBagConstraints52.gridy = 41;
			gridBagConstraints52.weightx = 1.0;
			gridBagConstraints52.weighty = 1.0;
			gridBagConstraints52.gridwidth = 6;
			gridBagConstraints52.gridx = 0;
			GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
			gridBagConstraints43.gridx = 0;
			gridBagConstraints43.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints43.ipady = 0;
			gridBagConstraints43.insets = new Insets(0, 0, 22, 5);
			gridBagConstraints43.gridy = 22;
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints33.gridy = 4;
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints25.fill = GridBagConstraints.BOTH;
			gridBagConstraints25.gridy = 3;
			gridBagConstraints25.weightx = 1.0;
			gridBagConstraints25.weighty = 0.0;
			gridBagConstraints25.gridheight = 3;
			gridBagConstraints25.gridwidth = 4;
			gridBagConstraints25.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints15.gridy = 3;
			jLabel11 = new JLabel();
			jLabel11.setText("Numbers");
			GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
			gridBagConstraints42.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints42.gridx = 3;
			gridBagConstraints42.anchor = GridBagConstraints.EAST;
			gridBagConstraints42.gridy = 35;
			jLabel10 = new JLabel();
			jLabel10.setText("Date Created");
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints32.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints32.gridy = 35;
			gridBagConstraints32.weightx = 1.0;
			gridBagConstraints32.anchor = GridBagConstraints.WEST;
			gridBagConstraints32.gridwidth = 2;
			gridBagConstraints32.gridx = 4;
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints24.gridx = 0;
			gridBagConstraints24.anchor = GridBagConstraints.EAST;
			gridBagConstraints24.gridy = 35;
			jLabel9 = new JLabel();
			jLabel9.setText("CreatedBy");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.gridy = 35;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.gridy = 37;
			gridBagConstraints23.weightx = 1.0;
			gridBagConstraints23.anchor = GridBagConstraints.WEST;
			gridBagConstraints23.gridwidth = 2;
			gridBagConstraints23.gridx = 4;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints13.gridx = 3;
			gridBagConstraints13.anchor = GridBagConstraints.EAST;
			gridBagConstraints13.gridy = 37;
			jLabel8 = new JLabel();
			jLabel8.setText("Last Updated");
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints22.gridy = 21;
			jLabel7 = new JLabel();
			jLabel7.setText("Collectors");
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints12.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 37;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints51.fill = GridBagConstraints.BOTH;
			gridBagConstraints51.gridy = 21;
			gridBagConstraints51.weightx = 1.0;
			gridBagConstraints51.anchor = GridBagConstraints.WEST;
			gridBagConstraints51.gridheight = 2;
			gridBagConstraints51.gridwidth = 3;
			gridBagConstraints51.gridx = 1;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.fill = GridBagConstraints.NONE;
			gridBagConstraints41.anchor = GridBagConstraints.NORTHEAST;
			gridBagConstraints41.gridy = 37;
			jLabel6 = new JLabel();
			jLabel6.setText("LastUpdatedBy");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.gridy = 20;
			gridBagConstraints31.weightx = 1.0;
			gridBagConstraints31.anchor = GridBagConstraints.WEST;
			gridBagConstraints31.gridwidth = 4;
			gridBagConstraints31.gridx = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.anchor = GridBagConstraints.EAST;
			gridBagConstraints21.gridy = 20;
			jLabel5 = new JLabel();
			jLabel5.setText("Collection");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints10.gridx = 5;
			gridBagConstraints10.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints10.gridy = 39;
			gridBagConstraints10.weighty = 0.0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.anchor = GridBagConstraints.EAST;
			gridBagConstraints9.gridy = 11;
			jLabel4 = new JLabel();
			jLabel4.setText("Subspecies");
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.anchor = GridBagConstraints.EAST;
			gridBagConstraints8.gridy = 10;
			jLabel3 = new JLabel();
			jLabel3.setText("Species");
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridy = 19;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.WEST;
			gridBagConstraints7.gridwidth = 5;
			gridBagConstraints7.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 11;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.WEST;
			gridBagConstraints5.gridwidth = 3;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 10;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.gridwidth = 3;
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 9;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridwidth = 3;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 9;
			jLabel2 = new JLabel();
			jLabel2.setText("Genus");
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Barcode");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(jLabel, gridBagConstraints);
			jPanel.add(getJTextFieldBarcode(), gridBagConstraints1);
			jPanel.add(jLabel2, gridBagConstraints2);
			jPanel.add(getJTextField1(), gridBagConstraints3);
			jPanel.add(getJTextField12(), gridBagConstraints4);
			jPanel.add(getJTextField2(), gridBagConstraints5);
			jPanel.add(getJTextField3(), gridBagConstraints7);
			jPanel.add(jLabel3, gridBagConstraints8);
			jPanel.add(jLabel4, gridBagConstraints9);
			GridBagConstraints gridBagConstraints72 = new GridBagConstraints();
			gridBagConstraints72.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints72.gridx = 0;
			gridBagConstraints72.anchor = GridBagConstraints.EAST;
			gridBagConstraints72.gridy = 26;
			jLabel19 = new JLabel();
			jLabel19.setText("Features");
			jPanel.add(jLabel19, gridBagConstraints72);
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints17.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints17.gridy = 26;
			gridBagConstraints17.weightx = 1.0;
			gridBagConstraints17.anchor = GridBagConstraints.WEST;
			gridBagConstraints17.gridx = 1;
			jPanel.add(getJComboBoxFeatures(), gridBagConstraints17);
			GridBagConstraints gridBagConstraints104 = new GridBagConstraints();
			gridBagConstraints104.insets = new Insets(0, 0, 0, 5);
			gridBagConstraints104.gridx = 3;
			gridBagConstraints104.anchor = GridBagConstraints.EAST;
			gridBagConstraints104.gridy = 26;
			jLabel37 = new JLabel();
			jLabel37.setText("Prep Type");
			jPanel.add(jLabel37, gridBagConstraints104);
			GridBagConstraints gridBagConstraints152 = new GridBagConstraints();
			gridBagConstraints152.insets = new Insets(0, 0, 5, 0);
			gridBagConstraints152.fill = GridBagConstraints.BOTH;
			gridBagConstraints152.gridy = 26;
			gridBagConstraints152.weightx = 1.0;
			gridBagConstraints152.anchor = GridBagConstraints.WEST;
			gridBagConstraints152.gridwidth = 3;
			gridBagConstraints152.gridx = 4;
			jPanel.add(getJTextFieldPrepType(), gridBagConstraints152);
			
			GridBagConstraints gridBagConstraintsPR = new GridBagConstraints();
			gridBagConstraintsPR.insets = new Insets(0, 0, 0, 5);
			gridBagConstraintsPR.gridx = 0;
			gridBagConstraintsPR.fill = GridBagConstraints.BOTH;
			gridBagConstraintsPR.gridwidth = 6;
			gridBagConstraintsPR.weighty = 1.0;
			gridBagConstraintsPR.gridy = 27;	
			
			jPanel.add(getJScrollPaneSpecimenParts(),gridBagConstraintsPR);
			jPanel.add(getJButton(), gridBagConstraints10);
			jPanel.add(jLabel5, gridBagConstraints21);
			jPanel.add(getJTextFieldCollection(), gridBagConstraints31);
			jPanel.add(jLabel6, gridBagConstraints41);
			jPanel.add(getJTextField14(), gridBagConstraints12);
			jPanel.add(getJScrollPaneCollectors(), gridBagConstraints51);
			jPanel.add(jLabel7, gridBagConstraints22);
			jPanel.add(jLabel8, gridBagConstraints13);
			jPanel.add(getJTextFieldDateUpdated(), gridBagConstraints23);
			jPanel.add(getJTextField22(), gridBagConstraints14);
			jPanel.add(jLabel9, gridBagConstraints24);
			jPanel.add(getJTextField32(), gridBagConstraints32);
			jPanel.add(jLabel10, gridBagConstraints42);
			jPanel.add(jLabel11, gridBagConstraints15);
			jPanel.add(getJScrollPaneNumbers(), gridBagConstraints25);
			jPanel.add(getJButtonNumbersAdd(), gridBagConstraints33);
			jPanel.add(getJButtonCollsAdd(), gridBagConstraints43);
			jPanel.add(getJScrollPane(), gridBagConstraints52);
			jPanel.add(jLabel12, gridBagConstraints61);
			jPanel.add(getJTextField(), gridBagConstraints71);
			jPanel.add(jLabel13, gridBagConstraints81);
			jPanel.add(jLabel14, gridBagConstraints91);
			jPanel.add(jLabel15, gridBagConstraints101);
			jPanel.add(getJTextField4(), gridBagConstraints111);
			jPanel.add(getJTextField13(), gridBagConstraints121);
			jPanel.add(getJTextField23(), gridBagConstraints131);
			jPanel.add(jLabel16, gridBagConstraints16);
			jPanel.add(jLabel17, gridBagConstraints26);
			jPanel.add(jLabel18, gridBagConstraints34);
			jPanel.add(getJTextField5(), gridBagConstraints44);
			jPanel.add(getJTextField15(), gridBagConstraints53);
			jPanel.add(getJTextField24(), gridBagConstraints62);
			jPanel.add(jLabel20, gridBagConstraints82);
			jPanel.add(jLabel21, gridBagConstraints92);
			jPanel.add(getJComboBoxSex(), gridBagConstraints102);
			jPanel.add(getJComboBoxLifeStage(), gridBagConstraints27);
			jPanel.add(jLabel22, gridBagConstraints35);
			jPanel.add(getJTextField6(), gridBagConstraints63);
			jPanel.add(jLabel27, gridBagConstraints110);
			jPanel.add(jLabel29, gridBagConstraints112);
			jPanel.add(getJTextFieldAuthorship(), gridBagConstraints210);
			jPanel.add(jLabel30, gridBagConstraints36);
			jPanel.add(getJTextFieldUnnamedForm(), gridBagConstraints46);
			jPanel.add(jLabel28, gridBagConstraints74);
			jPanel.add(getJTextField17(), gridBagConstraints84);
			jPanel.add(getJTextField8(), gridBagConstraints93);
			jPanel.add(jLabel25, gridBagConstraints103);
			jPanel.add(jLabel26, gridBagConstraints113);
			jPanel.add(getJTextField25(), gridBagConstraints122);
			jPanel.add(getJTextField33(), gridBagConstraints132);
			jPanel.add(getJTextField16(), gridBagConstraints141);
			jPanel.add(getJTextField7(), gridBagConstraints151);
			jPanel.add(jLabel32, gridBagConstraints171);
			jPanel.add(getJTextField11(), gridBagConstraints18);
			jPanel.add(getJTextField19(), gridBagConstraints19);
			jPanel.add(jLabel33, gridBagConstraints28);
			jPanel.add(jLabel34, gridBagConstraints47);
			jPanel.add(jLabel35, gridBagConstraints65);
			jPanel.add(getJCheckBox(), gridBagConstraints83);
			jPanel.add(jLabel36, gridBagConstraints94);
			jPanel.add(jLabel38, gridBagConstraints114);
			jPanel.add(jLabel39, gridBagConstraints123);
			jPanel.add(jLabel40, gridBagConstraints133);
			jPanel.add(getJTextField20(), gridBagConstraints142);
			jPanel.add(getJTextField26(), gridBagConstraints162);
			jPanel.add(getJTextField34(), gridBagConstraints172);
			jPanel.add(jLabel41, gridBagConstraints115);
			jPanel.add(getJComboBox(), gridBagConstraints29);
			jPanel.add(jLabel42, gridBagConstraints37);
			jPanel.add(getJComboBox2(), gridBagConstraints48);
			jPanel.add(jLabel43, gridBagConstraints56);
			jPanel.add(getJTextField21(), gridBagConstraints66);
			jPanel.add(getJButton1(), gridBagConstraints116);
			jPanel.add(getJPanel1(), gridBagConstraints211);
			jPanel.add(getJTextField10(), gridBagConstraints117);
			jPanel.add(getJButton12(), gridBagConstraints212);
			jPanel.add(jLabel31, gridBagConstraints118);
			jPanel.add(getJTextField9(), gridBagConstraints213);
			jPanel.add(getJButtonNext(), gridBagConstraints119);
			jPanel.add(getJButtonPrevious(), gridBagConstraints214);
			jPanel.add(getJScrollPane1(), gridBagConstraints49);
			jPanel.add(jLabel44, gridBagConstraints120);
			jPanel.add(getJButton13(), gridBagConstraints215);
			jPanel.add(getJButton2(), gridBagConstraints38);
			jPanel.add(getJButtonSpecificLocality(), gridBagConstraints124);
			jPanel.add(getJTextField18(), gridBagConstraints216);
			jPanel.add(getJLabelMigrationStatus(),gridBagConstraintsMS);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFieldBarcode	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldBarcode() {
		if (jTextFieldBarcode == null) {
			jTextFieldBarcode = new JTextField(11);
			jTextFieldBarcode.setEditable(false);
            jTextFieldBarcode.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Barcode"));		
		}
		return jTextFieldBarcode;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (jTextFieldGenus == null) {
			jTextFieldGenus = new JTextField();
			jTextFieldGenus.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Genus", jTextFieldGenus));
			jTextFieldGenus.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Genus"));
			jTextFieldGenus.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldGenus;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField12() {
		if (jTextFieldSpecies == null) {
			jTextFieldSpecies = new JTextField();
			jTextFieldSpecies.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "SpecificEpithet", jTextFieldSpecies));
			jTextFieldSpecies.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SpecificEpithet"));
			jTextFieldSpecies.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldSpecies;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField2() {
		if (jTextFieldSubspecies == null) {
			jTextFieldSubspecies = new JTextField();
			jTextFieldSubspecies.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "SubspecificEpithet", jTextFieldSubspecies));
			jTextFieldSubspecies.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SubspecificEpithet"));
			jTextFieldSubspecies.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldSubspecies;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField3() {
		if (jTextFieldLocality == null) {
			jTextFieldLocality = new JTextField();
			jTextFieldLocality.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "SpecificLocality", jTextFieldLocality));
			jTextFieldLocality.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SpecificLocality"));
			jTextFieldLocality.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldLocality;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton("Save");
			if (specimen.isStateDone()) { 
				jButton.setEnabled(false);
				jButton.setText("Migrated " + specimen.getLoadFlags());
			}
			jButton.setMnemonic(KeyEvent.VK_S);
			jButton.setToolTipText("Save changes to this record to the database.  No fields should have red backgrounds before you save.");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try { 
					    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					} catch (Exception ex) { 
						log.error(ex);
					}
					
					save();
					
					
					try { 
					    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					} catch (Exception ex) { 
						log.error(ex);
					}
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextFieldCollection() {
		if (jComboBoxCollection == null) {
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			jComboBoxCollection = new JComboBox(sls.getDistinctCollections());
			jComboBoxCollection.setEditable(true);
			//jComboBoxCollection.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Collection", jComboBoxCollection));
			jComboBoxCollection.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Collection"));
			jComboBoxCollection.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxCollection);
		}
		return jComboBoxCollection;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField14() {
		if (jTextFieldLastUpdatedBy == null) {
			jTextFieldLastUpdatedBy = new JTextField();
			jTextFieldLastUpdatedBy.setEditable(false);
			jTextFieldLastUpdatedBy.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "LastUpdatedBy"));
			//jTextFieldLastUpdatedBy.setEnabled(false);
			jTextFieldLastUpdatedBy.setForeground(Color.BLACK);
		}
		return jTextFieldLastUpdatedBy;
	}

	/**
	 * This method initializes jScrollPaneCollectors	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneCollectors() {
		if (jScrollPaneCollectors == null) {
			jScrollPaneCollectors = new JScrollPane();
			jScrollPaneCollectors.setViewportView(getJTableCollectors());
			jScrollPaneCollectors.setPreferredSize(new Dimension(jScrollPaneCollectors.getWidth(), 150));
			jScrollPaneCollectors.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jScrollPaneCollectors;
	}

	/**
	 * This method initializes jTableCollectors	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableCollectors() {
		if (jTableCollectors == null) {
			jTableCollectors = new JTable(new CollectorTableModel());
			JTextField field = new JTextField();
			field.setInputVerifier(MetadataRetriever.getInputVerifier(Collector.class, "CollectorName", field));
			//field.setVerifyInputWhenFocusTarget(true);
			jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ValidatingTableCellEditor(field));
			jTableCollectors.setRowHeight(jTableCollectors.getRowHeight()+4);
		    jTableCollectors.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTableCollectors;
	}
	
	private JScrollPane getJScrollPaneSpecimenParts() {
		if (jScrollPaneSpecimenParts == null) {
			jScrollPaneSpecimenParts = new JScrollPane();
			jScrollPaneSpecimenParts.setViewportView(getJTableSpecimenParts());
			jScrollPaneSpecimenParts.setPreferredSize(new Dimension(0, 150));
			jScrollPaneSpecimenParts.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jScrollPaneSpecimenParts;
	}	
	
	private JTable getJTableSpecimenParts() { 
		if (jTableSpecimenParts == null) {
			try { 
		        jTableSpecimenParts = new JTable(new SpecimenPartsTableModel(specimen.getSpecimenParts()));
			} catch (NullPointerException e) { 
			    jTableSpecimenParts = new JTable(new SpecimenPartsTableModel());
			}
			setSpecimenPartsTableCellEditors();
		    log.debug(specimen.getSpecimenParts().size());
		}
		return jTableSpecimenParts;
	}
	
    private void setSpecimenPartsTableCellEditors() { 
        log.debug("Setting cell editors");
		JComboBox comboBoxPart = new JComboBox();
		comboBoxPart.addItem("whole animal");
		comboBoxPart.addItem("partial animal");
		getJTableSpecimenParts().getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboBoxPart));
		JComboBox comboBoxPrep = new JComboBox();
		comboBoxPrep.addItem("pinned");
		comboBoxPrep.addItem("pointed");
		comboBoxPrep.addItem("carded");
		comboBoxPrep.addItem("capsule");
		comboBoxPrep.addItem("envelope");
		getJTableSpecimenParts().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBoxPrep));
		
		getJTableSpecimenParts().getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
		getJTableSpecimenParts().getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(ButtonEditor.OPEN_SPECIMENPARTATTRIBUTES, this));
	    	
    }

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDateUpdated() {
		if (jTextFieldDateLastUpdated == null) {
			jTextFieldDateLastUpdated = new JTextField();
			jTextFieldDateLastUpdated.setEditable(false);
			//jTextFieldDateLastUpdated.setEnabled(false);
			jTextFieldDateLastUpdated.setForeground(Color.BLACK);
		}
		return jTextFieldDateLastUpdated;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField22() {
		if (jTextFieldCreator == null) {
			jTextFieldCreator = new JTextField();
			jTextFieldCreator.setEditable(false);
			//jTextFieldCreator.setEnabled(false);
			jTextFieldCreator.setForeground(Color.BLACK);
			jTextFieldCreator.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Creator"));
		}
		return jTextFieldCreator;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField32() {
		if (jTextFieldDateCreated == null) {
			jTextFieldDateCreated = new JTextField();
			jTextFieldDateCreated.setEditable(false);
			//jTextFieldDateCreated.setEnabled(false);
			jTextFieldDateCreated.setForeground(Color.BLACK);
		}
		return jTextFieldDateCreated;
	}

	/**
	 * This method initializes jScrollPaneNumbers	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneNumbers() {
		if (jScrollPaneNumbers == null) {
			jScrollPaneNumbers = new JScrollPane();
			jScrollPaneNumbers.setViewportView(getJTable());
			jScrollPaneNumbers.setPreferredSize(new Dimension(jScrollPaneNumbers.getWidth(),jTextFieldBarcode.getFontMetrics(jTextFieldBarcode.getFont()).getHeight()*6));
			jScrollPaneNumbers.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jScrollPaneNumbers;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTableNumbers == null) {
			jTableNumbers = new JTable(new NumberTableModel());
			JComboBox jComboNumberTypes = new JComboBox(NumberLifeCycle.getDistinctTypes());
			jComboNumberTypes.setEditable(true);
			TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
			DefaultCellEditor comboBoxEditor = new DefaultCellEditor(jComboNumberTypes);
			//TODO: enable autocomplete for numbertypes picklist.
			//AutoCompleteDecorator.decorate((JComboBox) comboBoxEditor.getComponent());
			typeColumn.setCellEditor(comboBoxEditor);
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setToolTipText("Click for pick list of number types.");
            typeColumn.setCellRenderer(renderer);
            jTableNumbers.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTableNumbers;
	}

	/**
	 * This method initializes jButtonNumbersAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonNumbersAdd() {
		if (jButtonNumbersAdd == null) {
			jButtonNumbersAdd = new JButton();
			jButtonNumbersAdd.setText("+");
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/b_plus.png");
	        try {  
	        	jButtonNumbersAdd.setText("");
	        	jButtonNumbersAdd.setIcon(new ImageIcon(iconFile));
	        	jButtonNumbersAdd.addActionListener(new java.awt.event.ActionListener() {
	        		public void actionPerformed(java.awt.event.ActionEvent e) {
	        			((NumberTableModel)jTableNumbers.getModel()).addNumber(new edu.harvard.mcz.imagecapture.data.Number(specimen, "", ""));
	        			thisPane.setStateToDirty();
	        		}
	        	});
	        } catch (Exception e) { 
			    jButtonNumbersAdd.setText("+");
	        }
		}
		return jButtonNumbersAdd;
	}

	/**
	 * This method initializes jButtonCollsAdd	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCollsAdd() {
		if (jButtonCollsAdd == null) {
			jButtonCollsAdd = new JButton();
			jButtonCollsAdd.setText("+");
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/b_plus.png");
	        try {  
	        	jButtonCollsAdd.setText("");
	        	jButtonCollsAdd.setIcon(new ImageIcon(iconFile));
	        	jButtonCollsAdd.addActionListener(new java.awt.event.ActionListener() {
	        		public void actionPerformed(java.awt.event.ActionEvent e) {
	        			((CollectorTableModel)jTableCollectors.getModel()).addCollector(new Collector(specimen, ""));
	        			thisPane.setStateToDirty();
	        		}
	        	});
	        } catch (Exception e) { 
			    jButtonCollsAdd.setText("+");
	        }
			
		}
		return jButtonCollsAdd;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextPane());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getJTextPane() {
		if (jTextPaneWarnings == null) {
			jTextPaneWarnings = new JTextPane();
			jTextPaneWarnings.setEditable(false);
			jTextPaneWarnings.setBackground(this.getBackground());
		}
		return jTextPaneWarnings;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextFieldDrawerNumber == null) {
			jTextFieldDrawerNumber = new JTextField();
			jTextFieldDrawerNumber.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "DrawerNumber", jTextFieldDrawerNumber));
			jTextFieldDrawerNumber.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DrawerNumber"));
			jTextFieldDrawerNumber.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDrawerNumber;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField4() {
		if (jTextFieldVerbatimLocality == null) {
			jTextFieldVerbatimLocality = new JTextField();
			jTextFieldVerbatimLocality.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "VerbatimLocality", jTextFieldVerbatimLocality));
			jTextFieldVerbatimLocality.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "VerbatimLocality"));
			jTextFieldVerbatimLocality.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldVerbatimLocality;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField13() {
		if (jTextFieldCountry == null) {
			jTextFieldCountry = new JTextField();
			jTextFieldCountry.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "Country", jTextFieldCountry));
			jTextFieldCountry.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Country"));
			jTextFieldCountry.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldCountry;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField23() {
		if (jTextFieldPrimaryDivision == null) {
			jTextFieldPrimaryDivision = new JTextField();
			jTextFieldPrimaryDivision.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "primaryDivison", jTextFieldPrimaryDivision));
			jTextFieldPrimaryDivision.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "primaryDivison"));
			jTextFieldPrimaryDivision.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldPrimaryDivision;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextField5() {
		if (jComboBoxFamily == null) {
			jComboBoxFamily = new JComboBox(HigherTaxonLifeCycle.selectDistinctFamily());
			jComboBoxFamily.setEditable(true);
			//jTextFieldFamily.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Family", jTextFieldFamily));
			jComboBoxFamily.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Family"));
			jComboBoxFamily.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxFamily);
		}
		return jComboBoxFamily;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextField15() {
		if (jComboBoxSubfamily == null) {
			jComboBoxSubfamily = new JComboBox(HigherTaxonLifeCycle.selectDistinctSubfamily(""));
			jComboBoxSubfamily.setEditable(true);
			//jTextFieldSubfamily.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Subfamily", jTextFieldSubfamily));
			jComboBoxSubfamily.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Subfamily"));
			jComboBoxSubfamily.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxSubfamily);
		}
		return jComboBoxSubfamily;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField24() {
		if (jTextFieldTribe == null) {
			jTextFieldTribe = new JTextField();
			jTextFieldTribe.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Tribe", jTextFieldTribe));
			jTextFieldTribe.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Tribe"));
			jTextFieldTribe.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldTribe;
	}

	/**
	 * This method initializes jComboBoxSex	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxSex() {
		if (jComboBoxSex == null) {
			jComboBoxSex = new JComboBox(Sex.getSexValues());
			jComboBoxSex.setEditable(true);
			jComboBoxSex.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Sex"));
			jComboBoxSex.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxSex);
		}
		return jComboBoxSex;
	}

	/**
	 * This method initializes jComboBoxFeatures	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxFeatures() {
		if (jComboBoxFeatures == null) {
			jComboBoxFeatures = new JComboBox(Features.getFeaturesValues());
			jComboBoxFeatures.setEditable(true);
			jComboBoxFeatures.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Features"));
			jComboBoxFeatures.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxFeatures);
			// TODO: validate input length 
		}
		return jComboBoxFeatures;
	}

	/**
	 * This method initializes jComboBoxLifeStage	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxLifeStage() {
		if (jComboBoxLifeStage == null) {
			jComboBoxLifeStage = new JComboBox(LifeStage.getLifeStageValues());
			jComboBoxLifeStage.setEditable(true);
			jComboBoxLifeStage.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Lifestage"));
			jComboBoxLifeStage.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxLifeStage);
		}
		return jComboBoxLifeStage;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField6() {
		if (jTextFieldDateNos == null) {
			jTextFieldDateNos = new JTextField();
			//jTextFieldDateNos.setToolTipText("Date found on labels where date might be either date collected or date emerged, or some other date");
			jTextFieldDateNos.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "DateNOS", jTextFieldDateNos));
			jTextFieldDateNos.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateNOS"));
			jTextFieldDateNos.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateNos;
	}



	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField16() {
		if (jTextFieldDateEmerged == null) {
			jTextFieldDateEmerged = new JTextField(15);
			jTextFieldDateEmerged.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "DateEmerged", jTextFieldDateEmerged));
			jTextFieldDateEmerged.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateEmerged"));
			jTextFieldDateEmerged.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateEmerged;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField25() {
		if (jTextFieldDateEmergedIndicator == null) {
			jTextFieldDateEmergedIndicator = new JTextField(15);
			jTextFieldDateEmergedIndicator.setToolTipText("Verbatim text indicating that this is a date emerged.");
			jTextFieldDateEmergedIndicator.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "DateEmergedIndicator", jTextFieldDateEmergedIndicator));
			jTextFieldDateEmergedIndicator.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateEmergedIndicator"));
			jTextFieldDateEmergedIndicator.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateEmergedIndicator;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField7() {
		if (jTextFieldDateCollected == null) {
			jTextFieldDateCollected = new JTextField(15);
			jTextFieldDateCollected.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateCollected"));
			jTextFieldDateCollected.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateCollected;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField33() {
		if (jTextFieldDateCollectedIndicator == null) {
			jTextFieldDateCollectedIndicator = new JTextField(15);
			jTextFieldDateCollectedIndicator.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "DateCollectedIndicator", jTextFieldDateCollectedIndicator));
			jTextFieldDateCollectedIndicator.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "DateCollectedIndicator"));
			jTextFieldDateCollectedIndicator.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldDateCollectedIndicator;
	}


	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField8() {
		if (jTextFieldInfraspecificEpithet == null) {
			jTextFieldInfraspecificEpithet = new JTextField(18);
			jTextFieldInfraspecificEpithet.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "InfraspecificEpithet", jTextFieldInfraspecificEpithet));
			jTextFieldInfraspecificEpithet.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "InfraspecificEpithet"));
			jTextFieldInfraspecificEpithet.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldInfraspecificEpithet;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField17() {
		if (jTextFieldInfraspecificRank == null) {
			jTextFieldInfraspecificRank = new JTextField(5);
			jTextFieldInfraspecificRank.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "InfraspecificRank", jTextFieldInfraspecificRank));
			jTextFieldInfraspecificRank.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "InfraspecificRank"));
			jTextFieldInfraspecificRank.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldInfraspecificRank;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldAuthorship() {
		if (jTextFieldAuthorship == null) {
			jTextFieldAuthorship = new JTextField();
			jTextFieldAuthorship.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Authorship", jTextFieldAuthorship));
			jTextFieldAuthorship.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Authorship"));
			jTextFieldAuthorship.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldAuthorship;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldUnnamedForm() {
		if (jTextFieldUnnamedForm == null) {
			jTextFieldUnnamedForm = new JTextField();
			jTextFieldUnnamedForm.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "UnnamedForm", jTextFieldUnnamedForm));
			jTextFieldUnnamedForm.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "UnnamedForm"));
			jTextFieldUnnamedForm.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldUnnamedForm;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField11() {
		if (jTextFieldVerbatimElevation == null) {
			jTextFieldVerbatimElevation = new JTextField();
			jTextFieldVerbatimElevation.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "VerbatimElevation", jTextFieldVerbatimElevation));
			jTextFieldVerbatimElevation.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "VerbatimElevation"));
			jTextFieldVerbatimElevation.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldVerbatimElevation;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField19() {
		if (jTextFieldCollectingMethod == null) {
			jTextFieldCollectingMethod = new JTextField();
			jTextFieldCollectingMethod.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "CollectingMethod", jTextFieldCollectingMethod));
			jTextFieldCollectingMethod.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "CollectingMethod"));
			jTextFieldCollectingMethod.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldCollectingMethod;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextAreaSpecimenNotes == null) {
			jTextAreaSpecimenNotes = new JTextArea();
			jTextAreaSpecimenNotes.setRows(3);
			jTextAreaSpecimenNotes.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "SpecimenNotes"));
			jTextAreaSpecimenNotes.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextAreaSpecimenNotes;
	}

	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBox() {
		if (jCheckBoxValidDistributionFlag == null) {
			jCheckBoxValidDistributionFlag = new JCheckBox();
			//jCheckBoxValidDistributionFlag.setToolTipText("Check if locality represents natural biological range.  Uncheck for Specimens that came from a captive breeding program");
			jCheckBoxValidDistributionFlag.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "ValidDistributionFlag"));
			jCheckBoxValidDistributionFlag.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jCheckBoxValidDistributionFlag;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField20() {
		if (jTextFieldQuestions == null) {
			jTextFieldQuestions = new JTextField();
			jTextFieldQuestions.setBackground(MainFrame.BG_COLOR_QC_FIELD);
			jTextFieldQuestions.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "Questions", jTextFieldQuestions));
			jTextFieldQuestions.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Questions"));
			jTextFieldQuestions.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldQuestions;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JButton getJTextFieldPrepType() {
		if (jButtonAddPreparationType == null) {
			jButtonAddPreparationType = new JButton("Add Prep");
			jButtonAddPreparationType.setMnemonic(KeyEvent.VK_P);
			
			jButtonAddPreparationType.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					log.debug("Adding new SpecimenPart");
					SpecimenPart newPart = new SpecimenPart();
					newPart.setPreserveMethod(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_DEFAULT_PREPARATION));
					newPart.setSpecimenId(specimen);
					SpecimenPartLifeCycle spls = new SpecimenPartLifeCycle();
                    log.debug("Attaching new SpecimenPart");
					try {
						spls.persist(newPart);
					    specimen.getSpecimenParts().add(newPart);
					    ((AbstractTableModel)jTableSpecimenParts.getModel()).fireTableDataChanged();
					    log.debug("Added new SpecimenPart");
					} catch (SaveFailedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});			
		}
		return jButtonAddPreparationType;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField26() {
		if (jTextFieldAssociatedTaxon == null) {
			jTextFieldAssociatedTaxon = new JTextField();
			jTextFieldAssociatedTaxon.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "AssociatedTaxon", jTextFieldAssociatedTaxon));
			jTextFieldAssociatedTaxon.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "AssociatedTaxon"));
			jTextFieldAssociatedTaxon.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldAssociatedTaxon;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField34() {
		if (jTextFieldHabitat == null) {
			jTextFieldHabitat = new JTextField();
			jTextFieldHabitat.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Habitat", jTextFieldHabitat));
			jTextFieldHabitat.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Habitat"));
			jTextFieldHabitat.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldHabitat;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBoxWorkflowStatus == null) {
			jComboBoxWorkflowStatus = new JComboBox(WorkFlowStatus.getWorkFlowStatusValues());
			jComboBoxWorkflowStatus.setEditable(false);
			jComboBoxWorkflowStatus.setBackground(MainFrame.BG_COLOR_QC_FIELD);
			jComboBoxWorkflowStatus.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "WorkflowStatus"));
			AutoCompleteDecorator.decorate(jComboBoxWorkflowStatus);
		}
		return jComboBoxWorkflowStatus;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox2() {
		if (jComboBoxLocationInCollection == null) {
			jComboBoxLocationInCollection = new JComboBox(LocationInCollection.getLocationInCollectionValues());
			jComboBoxLocationInCollection.setEditable(false);
			jComboBoxLocationInCollection.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "LocationInCollection"));
			jComboBoxLocationInCollection.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
			AutoCompleteDecorator.decorate(jComboBoxLocationInCollection);
		}
		return jComboBoxLocationInCollection;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField21() {
		if (jTextFieldInferences == null) {
			jTextFieldInferences = new JTextField();
			jTextFieldInferences.setBackground(MainFrame.BG_COLOR_ENT_FIELD);
			jTextFieldInferences.setInputVerifier(MetadataRetriever.getInputVerifier(Specimen.class, "Inferences", jTextFieldInferences));
			jTextFieldInferences.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Inferences"));
			jTextFieldInferences.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldInferences;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButtonGetHistory == null) {
			jButtonGetHistory = new JButton();
			jButtonGetHistory.setText("History");
			jButtonGetHistory.setToolTipText("Show the history of who edited this record");
			jButtonGetHistory.setMnemonic(KeyEvent.VK_H);
			jButtonGetHistory.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// retrieve and display the tracking events for this specimen 
					//Tracking t = new Tracking();
					//t.setSpecimen(specimen);
					TrackingLifeCycle tls = new TrackingLifeCycle();
					//Request by specimen doesn't work with Oracle.  Why?  
					//EventLog logViewer = new EventLog(new ArrayList<Tracking>(tls.findBySpecimen(specimen)));
					EventLog logViewer = new EventLog(new ArrayList<Tracking>(tls.findBySpecimenId(specimen.getSpecimenId())));
					logViewer.pack();
					logViewer.setVisible(true);
					
				}
			});
		}
		return jButtonGetHistory;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonNext() {
		if (jButtonNext == null) {
			jButtonNext = new JButton();
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/next.png");
			if (iconFile!=null) { 
			   jButtonNext.setIcon(new ImageIcon(iconFile));
			} else { 
				jButtonNext.setText("N");
			}
			jButtonNext.setMnemonic(KeyEvent.VK_N);
			jButtonNext.setEnabled(myControler.isInTable());
			jButtonNext.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
						// try to move to the next specimen in the table.
						if (thisPane.myControler.nextSpecimenInTable()) { 
						   //thisPane.myControler.setSpecimen(thisPane.specimen.getSpecimenId() + 1);
						   thisPane.setVisible(false);
						   thisPane.myControler.displayInEditor();
						   thisPane.invalidate();
						}
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally { 
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
					}
				}
			});
		}
		return jButtonNext;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonPrevious() {
		if (jButtonPrevious == null) {
			jButtonPrevious = new JButton();
			URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/back.png");
			if (iconFile!=null) {
			   jButtonPrevious.setIcon(new ImageIcon(iconFile));
			} else { 
				jButtonPrevious.setText("P");				
			}
			jButtonPrevious.setMnemonic(KeyEvent.VK_P);
			jButtonPrevious.setToolTipText("Move to Previous Specimen");
			jButtonPrevious.setEnabled(myControler.isInTable());
			jButtonPrevious.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						try { 
						    thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						}
						// try to move to the previous specimen in the table.
						if (thisPane.myControler.previousSpecimenInTable()) {
						   thisPane.setVisible(false);
						   thisPane.myControler.displayInEditor();
						   thisPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						   thisPane.invalidate();
						}
						try { 
						thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} catch (Exception ex) { 
							log.error(ex);
						} 
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						try { 
						thisPane.getParent().getParent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));;
						} catch (Exception ex) { 
							log.error(ex);
						}
					}
				}
			});
		}
		return jButtonPrevious;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
		}
		return jPanel1;
	}
	
	private void setStateToClean() { 
		state = STATE_CLEAN;
		// if this is a record that is part of a navigatable set, enable the navigation buttons
		if (myControler!=null){
			log.debug("Has controler");
			if (myControler.isInTable()) { 
				log.debug("controler is in table");
				// test to make sure the buttons have been created before trying to enable them.
				if (jButtonNext!=null) { 
					jButtonNext.setEnabled(true);
				}
				if (jButtonPrevious!=null) { 
					jButtonPrevious.setEnabled(true);
				}
			}
		}
	}
	
	private void setStateToDirty() { 
		state = STATE_DIRTY;
		if (jButtonNext!=null) { 
			this.jButtonNext.setEnabled(false);
		}
		if (jButtonPrevious!=null) { 
			this.jButtonPrevious.setEnabled(false);
		}
	}
	
	/** State of the data in the forms as compared to the specimen from which the data was loaded.
	 * 
	 * @return true if the data as displayed in the forms hasn't changed since the data was last loaded from
	 * or saved to the specimen, otherwise false indicating a dirty record.  
	 */
	private boolean isClean()  {
	    boolean result = false;
	    if (state==STATE_CLEAN) { 
	    	result = true;
	    }
	    return result;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField10() {
		if (jTextFieldISODate == null) {
			jTextFieldISODate = new JTextField();
			jTextFieldISODate.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "ISODate", jTextFieldISODate));
			jTextFieldISODate.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "ISODate"));
			jTextFieldISODate.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldISODate;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton12() {
		if (jButtonDeterminations == null) {
			jButtonDeterminations = new JButton();
			jButtonDeterminations.setText("Dets.");
			jButtonDeterminations.setMnemonic(KeyEvent.VK_D);
			
			jButtonDeterminations.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DeterminationFrame dets = new DeterminationFrame(specimen);
					dets.setVisible(true);
				}
			});
		}
		return jButtonDeterminations;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField9() {
		if (jTextFieldCitedInPub == null) {
			jTextFieldCitedInPub = new JTextField();
			jTextFieldCitedInPub.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "CitedInPublication", jTextFieldCitedInPub));
			jTextFieldCitedInPub.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "CitedInPublication"));
			jTextFieldCitedInPub.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					thisPane.setStateToDirty();
				}
			});
		}
		return jTextFieldCitedInPub;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJTextArea());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton13() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Date Emerged");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (jTextFieldDateNos.getText().equals("")) { 
						jTextFieldDateNos.setText(jTextFieldDateEmerged.getText());
					} else { 
						jTextFieldDateEmerged.setText(jTextFieldDateNos.getText());
					}
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Date Collected");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (jTextFieldDateNos.getText().equals("")) { 
						jTextFieldDateNos.setText(jTextFieldDateCollected.getText());
					} else { 
						jTextFieldDateCollected.setText(jTextFieldDateNos.getText());
					}
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jButtonSpecificLocality	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSpecificLocality() {
		if (jButtonSpecificLocality == null) {
			jButtonSpecificLocality = new JButton();
			jButtonSpecificLocality.setText("Specific Locality");
			jButtonSpecificLocality.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (jTextFieldVerbatimLocality.getText().equals("")) { 
						jTextFieldVerbatimLocality.setText(jTextFieldLocality.getText());
					} else { 
						jTextFieldLocality.setText(jTextFieldVerbatimLocality.getText());
					}
				}
			});
		}
		return jButtonSpecificLocality;
	}
	
	private JTextField getJLabelMigrationStatus() {
		if (jLabelMigrationStatus==null) { 
			jLabelMigrationStatus = new JTextField(60);
			//jLabelMigrationStatus.setBackground(null);
			//jLabelMigrationStatus.setBorder(null);
			jLabelMigrationStatus.setEditable(false);
			jLabelMigrationStatus.setText("");
		    if (specimen.isStateDone()) { 
		    	String uri = "http://mczbase.mcz.harvard.edu/guid/MCZ:Ent:" + specimen.getCatNum();
		    	jLabelMigrationStatus.setText(uri);
		    }
		}
	    return jLabelMigrationStatus;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JLabel getJTextField18() {
		if (jTextFieldImageCount == null) {
			jTextFieldImageCount = new JLabel("Number of Images=  ");
			jTextFieldDateCreated.setForeground(Color.BLACK);
		}
		return jTextFieldImageCount;
	}

}  //  @jve:decl-index=0:visual-constraint="10,15"
