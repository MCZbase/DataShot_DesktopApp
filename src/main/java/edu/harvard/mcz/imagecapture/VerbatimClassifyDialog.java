/**
 * VerbatimClassifyDialog.java
 * edu.harvard.mcz.imagecapture
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
package edu.harvard.mcz.imagecapture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.date.DateUtils;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.CollectorTableModel;
import edu.harvard.mcz.imagecapture.data.HigherGeographyComboBoxModel;
import edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRec;
import edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRecLifeCycle;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.NumberLifeCycle;
import edu.harvard.mcz.imagecapture.data.NumberTableModel;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.struct.CountValueTableModel;
import edu.harvard.mcz.imagecapture.struct.VerbatimCount;
import edu.harvard.mcz.imagecapture.struct.VerbatimCountTableModel;
import edu.harvard.mcz.imagecapture.ui.FilteringAgentJComboBox;
import edu.harvard.mcz.imagecapture.ui.FilteringGeogJComboBox;

import java.awt.GridBagLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;

/**
 * @author mole
 *
 */
public class VerbatimClassifyDialog extends JDialog {
	
	private static final long serialVersionUID = 2718225599980885040L;

	private static final Log log = LogFactory.getLog(VerbatimClassifyDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private VerbatimCount verbatimData = null;
	private FilteringGeogJComboBox comboBoxHigherGeog;
	private JTextArea textFieldVerbLocality;
	private JTextField textFieldMinElevation;
	private JTextField textFieldMaxElevation;
	private JComboBox comboBoxElevUnits;
	private JLabel lblCount;
	private JTextField textFieldVerbDate;
	private JTextField textFieldISODate;
	private JTextArea textFieldVerbCollector;
	private JTextArea textFieldVerbCollection;
	private JTextArea textFieldVerbNumbers;
	private JTextArea textFieldVerbUnclassifiedText;
	private JTextField textFieldSpecificLocality;
	private JScrollPane jScrollPaneCollectors = null;
	private JTable jTableCollectors = null;
	private JComboBox jComboBoxCollection = null; 
	private JScrollPane jScrollPaneNumbers = null;
	private JTable jTableNumbers = null;	
	private JTextField textFieldHabitat;
	private JTextField textFieldMicrohabitat;
	private JComboBox comboBoxWorkflowStatus;
	private JTable tableTaxa;
	
	private Specimen lastEditedSpecimen;
	
	/**
	 * Create the dialog.
	 */
	public VerbatimClassifyDialog() {
		verbatimData = new VerbatimCount(0, "", "", "", "", "", "");
		init();
	     setValues();
	} 
	
	/**
	 * Create the dialog for a particular instance of a set of verbatim values.
	 * 
	 * @param verbatimCount the set of verbatim values to display.
	 */
	public VerbatimClassifyDialog(VerbatimCount verbatimCount) {
	     verbatimData = verbatimCount;
	     init();
	     setValues();
	}
	
	protected void setValues() { 
		lblCount.setText("Number of records with these verbatim values: " + verbatimData.getCount() );
		textFieldVerbLocality.setText(verbatimData.getVerbatimLocality());
		textFieldVerbDate.setText(verbatimData.getVerbatimDate());
		textFieldVerbDate.setEditable(false);
		textFieldVerbCollection.setText(verbatimData.getVerbatimCollection());
		textFieldVerbCollector.setText(verbatimData.getVerbatimCollector());
		textFieldVerbNumbers.setText(verbatimData.getVerbatimNumbers());
		textFieldVerbUnclassifiedText.setText(verbatimData.getVerbatimUnclassfiedText());
	}
	
	protected void init() { 
		setTitle("Interpret verbatim data into fields.");
		setBounds(100, 100, 1203, 899);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JPanel panelVerbatimValues = new JPanel();
			contentPanel.add(panelVerbatimValues);
			GridBagLayout gbl_panelVerbatimValues = new GridBagLayout();
			gbl_panelVerbatimValues.columnWidths = new int[]{70, 0, 0, 0, 35, 0, 58, 0, 0, 0};
			gbl_panelVerbatimValues.rowHeights = new int[]{15, 0, 0, 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 83, 0};
			gbl_panelVerbatimValues.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0};
			gbl_panelVerbatimValues.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
			panelVerbatimValues.setLayout(gbl_panelVerbatimValues);
			{
				lblCount = new JLabel("New label");
				GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
				gbc_lblNewLabel.gridwidth = 2;
				gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel.anchor = GridBagConstraints.SOUTHWEST;
				gbc_lblNewLabel.gridx = 0;
				gbc_lblNewLabel.gridy = 0;
				panelVerbatimValues.add(lblCount, gbc_lblNewLabel);
			}
			
			JButton buttonFillFromLast = new JButton("Fill From Last");
			GridBagConstraints gbc_buttonFillFromLast = new GridBagConstraints();
			gbc_buttonFillFromLast.anchor = GridBagConstraints.SOUTH;
			gbc_buttonFillFromLast.insets = new Insets(0, 0, 5, 5);
			gbc_buttonFillFromLast.gridx = 3;
			gbc_buttonFillFromLast.gridy = 0;
			panelVerbatimValues.add(buttonFillFromLast, gbc_buttonFillFromLast);
			{
				JLabel lblNewLabel_1 = new JLabel("Field values to apply to all records.");
				GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
				gbc_lblNewLabel_1.anchor = GridBagConstraints.SOUTHEAST;
				gbc_lblNewLabel_1.gridwidth = 4;
				gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel_1.gridx = 4;
				gbc_lblNewLabel_1.gridy = 0;
				panelVerbatimValues.add(lblNewLabel_1, gbc_lblNewLabel_1);
			}
			{
				JLabel lblNewLabel_2 = new JLabel("Verbatim Locality");
				GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
				gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
				gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
				gbc_lblNewLabel_2.gridx = 0;
				gbc_lblNewLabel_2.gridy = 1;
				panelVerbatimValues.add(lblNewLabel_2, gbc_lblNewLabel_2);
			}
			{
				textFieldVerbLocality = new JTextArea();
				textFieldVerbLocality.setEditable(false);
				textFieldVerbLocality.setRows(5);
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.gridheight = 3;
				gbc_textField.insets = new Insets(0, 0, 5, 5);
				gbc_textField.fill = GridBagConstraints.BOTH;
				gbc_textField.gridx = 1;
				gbc_textField.gridy = 1;
				panelVerbatimValues.add(textFieldVerbLocality, gbc_textField);
				textFieldVerbLocality.setColumns(30);
			}
			
			JLabel lblNewLabel = new JLabel("HigherGeography");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 3;
			gbc_lblNewLabel.gridy = 1;
			panelVerbatimValues.add(lblNewLabel, gbc_lblNewLabel);
			
			GridBagConstraints gbc_textFieldHigherGeography = new GridBagConstraints();
			gbc_textFieldHigherGeography.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldHigherGeography.gridwidth = 5;
			gbc_textFieldHigherGeography.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldHigherGeography.gridx = 4;
			gbc_textFieldHigherGeography.gridy = 1;
			panelVerbatimValues.add(getComboBoxHighGeog(), gbc_textFieldHigherGeography);
			
			JButton btnCopyLocality = new JButton(">");
			btnCopyLocality.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (textFieldSpecificLocality.getText().isEmpty()) { 
					   textFieldSpecificLocality.setText(textFieldVerbLocality.getText().replace("\n", "").trim());
					}
				}});
			GridBagConstraints gbc_btnCopyLocality = new GridBagConstraints();
			gbc_btnCopyLocality.insets = new Insets(0, 0, 5, 5);
			gbc_btnCopyLocality.gridx = 2;
			gbc_btnCopyLocality.gridy = 2;
			panelVerbatimValues.add(btnCopyLocality, gbc_btnCopyLocality);
			
			JLabel lblSpecificLocality = new JLabel("Specific Locality");
			GridBagConstraints gbc_lblSpecificLocality = new GridBagConstraints();
			gbc_lblSpecificLocality.anchor = GridBagConstraints.EAST;
			gbc_lblSpecificLocality.insets = new Insets(0, 0, 5, 5);
			gbc_lblSpecificLocality.gridx = 3;
			gbc_lblSpecificLocality.gridy = 2;
			panelVerbatimValues.add(lblSpecificLocality, gbc_lblSpecificLocality);
			
			JLabel lblElevation = new JLabel("Elevation");
			GridBagConstraints gbc_lblElevation = new GridBagConstraints();
			gbc_lblElevation.anchor = GridBagConstraints.EAST;
			gbc_lblElevation.insets = new Insets(0, 0, 5, 5);
			gbc_lblElevation.gridx = 3;
			gbc_lblElevation.gridy = 3;
			panelVerbatimValues.add(lblElevation, gbc_lblElevation);
			
			textFieldMinElevation = new JTextField();
			textFieldMinElevation.setMinimumSize(new Dimension(60, 19));
			GridBagConstraints gbc_textFieldMinElevation = new GridBagConstraints();
			gbc_textFieldMinElevation.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldMinElevation.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldMinElevation.gridx = 4;
			gbc_textFieldMinElevation.gridy = 3;
			panelVerbatimValues.add(textFieldMinElevation, gbc_textFieldMinElevation);
			textFieldMinElevation.setColumns(4);
			
			JLabel lblTo = new JLabel("to");
			GridBagConstraints gbc_lblTo = new GridBagConstraints();
			gbc_lblTo.anchor = GridBagConstraints.EAST;
			gbc_lblTo.insets = new Insets(0, 0, 5, 5);
			gbc_lblTo.gridx = 5;
			gbc_lblTo.gridy = 3;
			panelVerbatimValues.add(lblTo, gbc_lblTo);
			
			textFieldMaxElevation = new JTextField();
			textFieldMaxElevation.setMinimumSize(new Dimension(60, 19));
			GridBagConstraints gbc_textFieldMaxElevation = new GridBagConstraints();
			gbc_textFieldMaxElevation.anchor = GridBagConstraints.WEST;
			gbc_textFieldMaxElevation.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldMaxElevation.gridx = 6;
			gbc_textFieldMaxElevation.gridy = 3;
			panelVerbatimValues.add(textFieldMaxElevation, gbc_textFieldMaxElevation);
			textFieldMaxElevation.setColumns(5);
			
			comboBoxElevUnits = new JComboBox();
			GridBagConstraints gbc_comboBoxElevUnits = new GridBagConstraints();
			gbc_comboBoxElevUnits.insets = new Insets(0, 0, 5, 5);
			gbc_comboBoxElevUnits.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxElevUnits.gridx = 7;
			gbc_comboBoxElevUnits.gridy = 3;
			panelVerbatimValues.add(comboBoxElevUnits, gbc_comboBoxElevUnits);
			{
				JLabel lblVerbatimdate = new JLabel("VerbatimDate");
				GridBagConstraints gbc_lblVerbatimdate = new GridBagConstraints();
				gbc_lblVerbatimdate.anchor = GridBagConstraints.EAST;
				gbc_lblVerbatimdate.insets = new Insets(0, 0, 5, 5);
				gbc_lblVerbatimdate.gridx = 0;
				gbc_lblVerbatimdate.gridy = 4;
				panelVerbatimValues.add(lblVerbatimdate, gbc_lblVerbatimdate);
			}
			
			textFieldVerbDate = new JTextField();
			GridBagConstraints gbc_textFieldVerbDate = new GridBagConstraints();
			gbc_textFieldVerbDate.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbDate.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldVerbDate.gridx = 1;
			gbc_textFieldVerbDate.gridy = 4;
			panelVerbatimValues.add(textFieldVerbDate, gbc_textFieldVerbDate);
			textFieldVerbDate.setColumns(30);
			
			JButton btnCopyDate = new JButton(">");
			btnCopyDate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (textFieldISODate.getText().isEmpty()) {
						Map<String,String> extractResult = DateUtils.extractDateFromVerbatim(textFieldVerbDate.getText().trim());
						if (extractResult.containsKey("result")) { 
							textFieldISODate.setText(extractResult.get("result"));
						}
						if (extractResult.containsKey("resultState")) { 
							// TODO: Report suspect etc
						}
					}
				}});
			GridBagConstraints gbc_btnCopyDate = new GridBagConstraints();
			gbc_btnCopyDate.insets = new Insets(0, 0, 5, 5);
			gbc_btnCopyDate.gridx = 2;
			gbc_btnCopyDate.gridy = 4;
			panelVerbatimValues.add(btnCopyDate, gbc_btnCopyDate);			
			
			JLabel lblNewLabel_3 = new JLabel("ISO Date");
			GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
			gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_3.gridx = 3;
			gbc_lblNewLabel_3.gridy = 4;
			panelVerbatimValues.add(lblNewLabel_3, gbc_lblNewLabel_3);
			
			textFieldISODate = new JTextField();
			textFieldISODate.setInputVerifier(
					MetadataRetriever.getInputVerifier(Specimen.class, "ISODate", textFieldISODate));
			textFieldISODate.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "ISODate"));
			GridBagConstraints gbc_textFieldISODate = new GridBagConstraints();
			gbc_textFieldISODate.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldISODate.gridwidth = 5;
			gbc_textFieldISODate.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldISODate.gridx = 4;
			gbc_textFieldISODate.gridy = 4;
			panelVerbatimValues.add(textFieldISODate, gbc_textFieldISODate);
			textFieldISODate.setColumns(10);
			
			textFieldSpecificLocality = new JTextField();
			GridBagConstraints gbc_textFieldSpecificLocality = new GridBagConstraints();
			gbc_textFieldSpecificLocality.gridwidth = 5;
			gbc_textFieldSpecificLocality.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldSpecificLocality.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldSpecificLocality.gridx = 4;
			gbc_textFieldSpecificLocality.gridy = 2;
			panelVerbatimValues.add(textFieldSpecificLocality, gbc_textFieldSpecificLocality);
			textFieldSpecificLocality.setColumns(25);
			{
				JLabel lblVerbatimCollector = new JLabel("Verbatim Collector");
				GridBagConstraints gbc_lblVerbatimCollector = new GridBagConstraints();
				gbc_lblVerbatimCollector.anchor = GridBagConstraints.EAST;
				gbc_lblVerbatimCollector.insets = new Insets(0, 0, 5, 5);
				gbc_lblVerbatimCollector.gridx = 0;
				gbc_lblVerbatimCollector.gridy = 6;
				panelVerbatimValues.add(lblVerbatimCollector, gbc_lblVerbatimCollector);
			}
			
			textFieldVerbCollector = new JTextArea();
			textFieldVerbCollector.setEditable(false);
			textFieldVerbCollector.setRows(3);
			GridBagConstraints gbc_textFieldVerbCollector = new GridBagConstraints();
			gbc_textFieldVerbCollector.gridheight = 2;
			gbc_textFieldVerbCollector.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbCollector.fill = GridBagConstraints.BOTH;
			gbc_textFieldVerbCollector.gridx = 1;
			gbc_textFieldVerbCollector.gridy = 6;
			panelVerbatimValues.add(textFieldVerbCollector, gbc_textFieldVerbCollector);
			textFieldVerbCollector.setColumns(30);
			
			JLabel lblCollectors = new JLabel("Collector(s)");
			GridBagConstraints gbc_lblCollectors = new GridBagConstraints();
			gbc_lblCollectors.anchor = GridBagConstraints.EAST;
			gbc_lblCollectors.insets = new Insets(0, 0, 5, 5);
			gbc_lblCollectors.gridx = 3;
			gbc_lblCollectors.gridy = 6;
			panelVerbatimValues.add(lblCollectors, gbc_lblCollectors);
			
			JButton btnAddCollector = new JButton("+");
		    btnAddCollector.addActionListener(new java.awt.event.ActionListener() {
		        public void actionPerformed(java.awt.event.ActionEvent e) {
		        	((CollectorTableModel)jTableCollectors.getModel()).addCollector(new Collector(null, ""));
		        }
		    });
			GridBagConstraints gbc_btnAddCollector = new GridBagConstraints();
			gbc_btnAddCollector.anchor = GridBagConstraints.EAST;
			gbc_btnAddCollector.insets = new Insets(0, 0, 5, 5);
			gbc_btnAddCollector.gridx = 3;
			gbc_btnAddCollector.gridy = 7;
			panelVerbatimValues.add(btnAddCollector, gbc_btnAddCollector);
			
			GridBagConstraints gbc_collectorTable = new GridBagConstraints();
			gbc_collectorTable.insets = new Insets(0, 0, 5, 5);
			gbc_collectorTable.fill = GridBagConstraints.BOTH;
			gbc_collectorTable.gridx = 4;
			gbc_collectorTable.gridy = 6;			
			gbc_collectorTable.gridheight = 2;
			gbc_collectorTable.gridwidth = 5;
			panelVerbatimValues.add(getJScrollPaneCollectors(),gbc_collectorTable);
			
			JLabel lblVerbatimCollection = new JLabel("Verbatim Collection");
			GridBagConstraints gbc_lblVerbatimCollection = new GridBagConstraints();
			gbc_lblVerbatimCollection.anchor = GridBagConstraints.EAST;
			gbc_lblVerbatimCollection.insets = new Insets(0, 0, 5, 5);
			gbc_lblVerbatimCollection.gridx = 0;
			gbc_lblVerbatimCollection.gridy = 8;
			panelVerbatimValues.add(lblVerbatimCollection, gbc_lblVerbatimCollection);
			
			textFieldVerbCollection = new JTextArea();
			textFieldVerbCollection.setEditable(false);
			textFieldVerbCollection.setRows(3);
			GridBagConstraints gbc_textFieldVerbCollection = new GridBagConstraints();
			gbc_textFieldVerbCollection.gridheight = 2;
			gbc_textFieldVerbCollection.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbCollection.fill = GridBagConstraints.BOTH;
			gbc_textFieldVerbCollection.gridx = 1;
			gbc_textFieldVerbCollection.gridy = 8;
			panelVerbatimValues.add(textFieldVerbCollection, gbc_textFieldVerbCollection);
			textFieldVerbCollection.setColumns(30);
			
			JLabel lblCollection = new JLabel("Collection");
			GridBagConstraints gbc_lblCollection = new GridBagConstraints();
			gbc_lblCollection.anchor = GridBagConstraints.EAST;
			gbc_lblCollection.insets = new Insets(0, 0, 5, 5);
			gbc_lblCollection.gridx = 3;
			gbc_lblCollection.gridy = 8;
			panelVerbatimValues.add(lblCollection, gbc_lblCollection);
			
			GridBagConstraints gbc_textFieldCollection = new GridBagConstraints();
			gbc_textFieldCollection.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldCollection.gridwidth = 5;
			gbc_textFieldCollection.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldCollection.gridx = 4;
			gbc_textFieldCollection.gridy = 8;
			panelVerbatimValues.add(getJComboBoxCollection(), gbc_textFieldCollection);
			
			JLabel lblVerbatimNumbers = new JLabel("Verbatim Numbers");
			GridBagConstraints gbc_lblVerbatimNumbers = new GridBagConstraints();
			gbc_lblVerbatimNumbers.anchor = GridBagConstraints.EAST;
			gbc_lblVerbatimNumbers.insets = new Insets(0, 0, 5, 5);
			gbc_lblVerbatimNumbers.gridx = 0;
			gbc_lblVerbatimNumbers.gridy = 10;
			panelVerbatimValues.add(lblVerbatimNumbers, gbc_lblVerbatimNumbers);
			
			textFieldVerbNumbers = new JTextArea();
			textFieldVerbNumbers.setRows(3);
			textFieldVerbNumbers.setEditable(false);
			GridBagConstraints gbc_textFieldVerbNumbers = new GridBagConstraints();
			gbc_textFieldVerbNumbers.gridheight = 2;
			gbc_textFieldVerbNumbers.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbNumbers.fill = GridBagConstraints.BOTH;
			gbc_textFieldVerbNumbers.gridx = 1;
			gbc_textFieldVerbNumbers.gridy = 10;
			panelVerbatimValues.add(textFieldVerbNumbers, gbc_textFieldVerbNumbers);
			textFieldVerbNumbers.setColumns(30);
			
			JLabel lblNumbers = new JLabel("Numbers");
			GridBagConstraints gbc_lblNumbers = new GridBagConstraints();
			gbc_lblNumbers.anchor = GridBagConstraints.EAST;
			gbc_lblNumbers.insets = new Insets(0, 0, 5, 5);
			gbc_lblNumbers.gridx = 3;
			gbc_lblNumbers.gridy = 10;
			panelVerbatimValues.add(lblNumbers, gbc_lblNumbers);
			
			JButton btnAddNumber = new JButton("+");
			btnAddNumber.addActionListener(new java.awt.event.ActionListener() {
		        public void actionPerformed(java.awt.event.ActionEvent e) {
		        	((NumberTableModel)jTableNumbers.getModel()).addNumber(new edu.harvard.mcz.imagecapture.data.Number(null, "", ""));
		        }
		    });
			GridBagConstraints gbc_btnAddNumber = new GridBagConstraints();
			gbc_btnAddNumber.anchor = GridBagConstraints.EAST;
			gbc_btnAddNumber.insets = new Insets(0, 0, 5, 5);
			gbc_btnAddNumber.gridx = 3;
			gbc_btnAddNumber.gridy = 11;
			panelVerbatimValues.add(btnAddNumber, gbc_btnAddNumber);
			
			GridBagConstraints gbc_numberTable = new GridBagConstraints();
			gbc_numberTable.insets = new Insets(0, 0, 5, 5);
			gbc_numberTable.fill = GridBagConstraints.BOTH;
			gbc_numberTable.gridx = 4;
			gbc_numberTable.gridy = 10;			
			gbc_numberTable.gridheight = 2;
			gbc_numberTable.gridwidth = 5;
			panelVerbatimValues.add(this.getJScrollPaneNumbers(),gbc_numberTable);
			
			JLabel lblVerbatimOtherText = new JLabel("Verbatim Other Text");
			GridBagConstraints gbc_lblVerbatimOtherText = new GridBagConstraints();
			gbc_lblVerbatimOtherText.anchor = GridBagConstraints.EAST;
			gbc_lblVerbatimOtherText.insets = new Insets(0, 0, 5, 5);
			gbc_lblVerbatimOtherText.gridx = 0;
			gbc_lblVerbatimOtherText.gridy = 12;
			panelVerbatimValues.add(lblVerbatimOtherText, gbc_lblVerbatimOtherText);
			
			textFieldVerbUnclassifiedText = new JTextArea();
			textFieldVerbUnclassifiedText.setEditable(false);
			textFieldVerbUnclassifiedText.setRows(5);
			GridBagConstraints gbc_textFieldVerbUnclassifiedText = new GridBagConstraints();
			gbc_textFieldVerbUnclassifiedText.gridheight = 3;
			gbc_textFieldVerbUnclassifiedText.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbUnclassifiedText.fill = GridBagConstraints.BOTH;
			gbc_textFieldVerbUnclassifiedText.gridx = 1;
			gbc_textFieldVerbUnclassifiedText.gridy = 12;
			panelVerbatimValues.add(textFieldVerbUnclassifiedText, gbc_textFieldVerbUnclassifiedText);
			textFieldVerbUnclassifiedText.setColumns(30);
			
			JLabel lblHabitat = new JLabel("Habitat");
			GridBagConstraints gbc_lblHabitat = new GridBagConstraints();
			gbc_lblHabitat.anchor = GridBagConstraints.EAST;
			gbc_lblHabitat.insets = new Insets(0, 0, 5, 5);
			gbc_lblHabitat.gridx = 3;
			gbc_lblHabitat.gridy = 12;
			panelVerbatimValues.add(lblHabitat, gbc_lblHabitat);
			
			textFieldHabitat = new JTextField();
			GridBagConstraints gbc_textFieldHabitat = new GridBagConstraints();
			gbc_textFieldHabitat.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldHabitat.gridwidth = 4;
			gbc_textFieldHabitat.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldHabitat.gridx = 4;
			gbc_textFieldHabitat.gridy = 12;
			panelVerbatimValues.add(textFieldHabitat, gbc_textFieldHabitat);
			textFieldHabitat.setColumns(10);
			
			JLabel lblMicrohabitat = new JLabel("Microhabitat");
			GridBagConstraints gbc_lblMicrohabitat = new GridBagConstraints();
			gbc_lblMicrohabitat.anchor = GridBagConstraints.EAST;
			gbc_lblMicrohabitat.insets = new Insets(0, 0, 5, 5);
			gbc_lblMicrohabitat.gridx = 3;
			gbc_lblMicrohabitat.gridy = 13;
			panelVerbatimValues.add(lblMicrohabitat, gbc_lblMicrohabitat);
			
			textFieldMicrohabitat = new JTextField();
			GridBagConstraints gbc_textFieldMicrohabitat = new GridBagConstraints();
			gbc_textFieldMicrohabitat.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldMicrohabitat.gridwidth = 4;
			gbc_textFieldMicrohabitat.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldMicrohabitat.gridx = 4;
			gbc_textFieldMicrohabitat.gridy = 13;
			panelVerbatimValues.add(textFieldMicrohabitat, gbc_textFieldMicrohabitat);
			textFieldMicrohabitat.setColumns(10);
			
			JLabel lblNewLabel_4 = new JLabel("Workflow Status");
			GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
			gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_4.gridx = 3;
			gbc_lblNewLabel_4.gridy = 15;
			panelVerbatimValues.add(lblNewLabel_4, gbc_lblNewLabel_4);
			
			comboBoxWorkflowStatus = new JComboBox(WorkFlowStatus.getVerbatimClassifiedWorkFlowStatusValues());
			comboBoxWorkflowStatus.setSelectedItem(WorkFlowStatus.STAGE_CLASSIFIED);
			GridBagConstraints gbc_comboBoxWorkflowStatus = new GridBagConstraints();
			gbc_comboBoxWorkflowStatus.gridwidth = 4;
			gbc_comboBoxWorkflowStatus.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxWorkflowStatus.insets = new Insets(0, 0, 5, 5);
			gbc_comboBoxWorkflowStatus.gridx = 4;
			gbc_comboBoxWorkflowStatus.gridy = 15;
			panelVerbatimValues.add(comboBoxWorkflowStatus, gbc_comboBoxWorkflowStatus);
			
			JButton btnApplyToAll = new JButton("Apply To All Records");
			GridBagConstraints gbc_btnApplyToAll = new GridBagConstraints();
			gbc_btnApplyToAll.gridwidth = 4;
			gbc_btnApplyToAll.insets = new Insets(0, 0, 5, 5);
			gbc_btnApplyToAll.gridx = 4;
			gbc_btnApplyToAll.gridy = 16;
			panelVerbatimValues.add(btnApplyToAll, gbc_btnApplyToAll);
			btnApplyToAll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveChanges();
				}
			});
		}
		{
			JPanel panelRelatedData = new JPanel();
			contentPanel.add(panelRelatedData);
			GridBagLayout gbl_panelRelatedData = new GridBagLayout();
			gbl_panelRelatedData.maximumLayoutSize(panelRelatedData);
			gbl_panelRelatedData.columnWidths = new int[]{20, 33, 1, 0};
			gbl_panelRelatedData.rowHeights = new int[]{25, 0};
			gbl_panelRelatedData.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
			gbl_panelRelatedData.rowWeights = new double[]{1.0, Double.MIN_VALUE};
			panelRelatedData.setLayout(gbl_panelRelatedData);
			{
				JLabel lblTaxa = new JLabel("Taxa");
				GridBagConstraints gbc_lblTaxa = new GridBagConstraints();
				gbc_lblTaxa.anchor = GridBagConstraints.NORTHEAST;
				gbc_lblTaxa.insets = new Insets(0, 0, 0, 5);
				gbc_lblTaxa.gridx = 0;
				gbc_lblTaxa.gridy = 0;
				panelRelatedData.add(lblTaxa, gbc_lblTaxa);
			}
			
			JScrollPane scrollPane = new JScrollPane();
			// scrollPane.setPreferredSize(new Dimension(1000,100));
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.anchor = GridBagConstraints.NORTH;
			gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 1;
			gbc_scrollPane.gridy = 0;
			panelRelatedData.add(scrollPane, gbc_scrollPane);
			
			tableTaxa = new JTable();
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			tableTaxa.setModel(new CountValueTableModel(sls.findTaxaFromVerbatim(this.verbatimData),"Current Idenfifications for these verbatim values."));
			scrollPane.setViewportView(tableTaxa);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.setActionCommand("Close");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}});
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private JScrollPane getJScrollPaneCollectors() {
		if (jScrollPaneCollectors == null) {
			jScrollPaneCollectors = new JScrollPane();
			jScrollPaneCollectors.setViewportView(getJTableCollectors());
			jScrollPaneCollectors.setPreferredSize(new Dimension(jScrollPaneCollectors.getWidth(), 150));
		}
		return jScrollPaneCollectors;
	}	
	
	private FilteringGeogJComboBox getComboBoxHighGeog() {
		if (comboBoxHigherGeog == null) {
			MCZbaseGeogAuthRecLifeCycle mls = new MCZbaseGeogAuthRecLifeCycle();
			comboBoxHigherGeog = new FilteringGeogJComboBox();
			comboBoxHigherGeog.setHGModel(new HigherGeographyComboBoxModel(mls.findAll()));
			comboBoxHigherGeog.setEditable(true);
		}
		return comboBoxHigherGeog;
	}
	
	
	private JTable getJTableCollectors() {
		if (jTableCollectors == null) {
			jTableCollectors = new JTable(new CollectorTableModel());
			
			// Note: When setting the values, the table column editor needs to be reset there, as the model is replaced.
			FilteringAgentJComboBox field = new FilteringAgentJComboBox();
			jTableCollectors.getColumnModel().getColumn(0).setCellEditor(new ComboBoxCellEditor(field));
			jTableCollectors.setRowHeight(jTableCollectors.getRowHeight()+4);
		}
		return jTableCollectors;
	}	
	
	/**
	 * This method initializes jComboBoxCollection for entering collections  with a controled vocabulary
	 * 	
	 * @return javax.swing.JComboBox for collections.
	 */
	private JComboBox getJComboBoxCollection() {
		if (jComboBoxCollection == null) {
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			jComboBoxCollection = new JComboBox();
			jComboBoxCollection.setModel(new DefaultComboBoxModel<String>(sls.getDistinctCollections()));
			jComboBoxCollection.setEditable(true);
			jComboBoxCollection.setToolTipText(MetadataRetriever.getFieldHelp(Specimen.class, "Collection"));
			AutoCompleteDecorator.decorate(jComboBoxCollection);
		}
		return jComboBoxCollection;
	}	
	
	/**
	 * This method initializes jScrollPaneNumbers	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPaneNumbers() {
		if (jScrollPaneNumbers == null) {
			jScrollPaneNumbers = new JScrollPane();
			jScrollPaneNumbers.setViewportView(getJTableNumbers());
			jScrollPaneNumbers.setPreferredSize(new Dimension(jScrollPaneCollectors.getWidth(), 150));
		}
		return jScrollPaneNumbers;
	}


	
	/**
	 * This method initializes jTableNumbers	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableNumbers() {
		if (jTableNumbers == null) {
			jTableNumbers = new JTable(new NumberTableModel());
			JComboBox<String> jComboNumberTypes = new JComboBox<String>();
			jComboNumberTypes.setModel(new DefaultComboBoxModel<String>(NumberLifeCycle.getDistinctTypes()));
			jComboNumberTypes.setEditable(true);
			TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
			DefaultCellEditor comboBoxEditor = new DefaultCellEditor(jComboNumberTypes);
			//TODO: enable autocomplete for numbertypes picklist.
			//AutoCompleteDecorator.decorate((JComboBox) comboBoxEditor.getComponent());
			typeColumn.setCellEditor(comboBoxEditor);
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setToolTipText("Click for pick list of number types.");
            typeColumn.setCellRenderer(renderer);
		}
		return jTableNumbers;
	}	
	
	protected void saveChanges() { 
		if (jTableCollectors.isEditing()) { 
		    jTableCollectors.getCellEditor().stopCellEditing();
		}
		if (jTableNumbers.isEditing()) { 
		    jTableNumbers.getCellEditor().stopCellEditing();
		}		
		
		log.debug("Saving verbatim data changes to all records with shared verbatim data.");
		log.debug("Should affect " + this.verbatimData.getCount() + " Specimen records." );
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		List<Specimen> specimens = sls.findSpecimensFromVerbatim(verbatimData);	
		Iterator<Specimen> i = specimens.iterator();
		while (i.hasNext()) { 
			Specimen specimen = i.next();
			
			// populate fields from parsed (10 fields, 2 lists)
			
			
		    if (comboBoxHigherGeog.getSelectedIndex()==-1 && comboBoxHigherGeog.getSelectedItem()==null) { 
		    	specimen.setHigherGeography("");
		    } else {
		    	// combo box contains a geography object, obtain the higher geography string.
		    	specimen.setHigherGeography(((HigherGeographyComboBoxModel)comboBoxHigherGeog.getModel()).getSelectedItemHigherGeography());
		    }
		    
			specimen.setSpecificLocality(textFieldSpecificLocality.getText());
			
			if (jTableCollectors.getModel().getRowCount()>0) { 
				// add collectors
				int rows = jTableCollectors.getModel().getRowCount();
				for (int row=0; row<rows; row++) { 
					String collector = (String) jTableCollectors.getModel().getValueAt(row, 1);
					specimen.getCollectors().add(new Collector(specimen, collector));
				}
			}
		    
		    
		    if (jComboBoxCollection.getSelectedIndex()==-1 && jComboBoxCollection.getSelectedItem()==null) { 
		    	specimen.setCollection("");
		    } else {
		    	specimen.setCollection(jComboBoxCollection.getSelectedItem().toString());
		    }
		   
		    
		    // Elevations
		    Long min_elev;
		    if (textFieldMinElevation.getText().trim().length()==0)  {
		    	min_elev = null;
		    } else { 
		        try { 
		            min_elev = Long.parseLong(textFieldMinElevation.getText());
		        } catch (NumberFormatException e) { 
	    	    	min_elev = null;
		        }
		    }
		    specimen.setMinimum_elevation(min_elev);
		    Long max_elev;
		    if (textFieldMaxElevation.getText().trim().length()==0)  {
		    	max_elev = null;
		    } else { 
		        try { 
		            max_elev = Long.parseLong(textFieldMaxElevation.getText());
		        } catch (NumberFormatException e) { 
	    	    	max_elev = null;
		        }
		    }	    
		    specimen.setMaximum_elevation(max_elev);
		    if (this.comboBoxElevUnits.getSelectedIndex()==-1 && comboBoxElevUnits.getSelectedItem()==null) { 
		    	specimen.setElev_units("");
		    } else { 
		    	specimen.setElev_units(comboBoxElevUnits.getSelectedItem().toString());
		    }	
		    
		    specimen.setIsoDate(textFieldISODate.getText().trim());
		    
		    
		    specimen.setHabitat(textFieldHabitat.getText());
		    specimen.setMicrohabitat(textFieldMicrohabitat.getText());
		    
			specimen.setWorkFlowStatus((String)comboBoxWorkflowStatus.getSelectedItem());
	
			// store values for reuse 
		    storeLastEditedValues(specimen);
		    
			try {
				sls.attachDirty(specimen);
			} catch (SaveFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	private void storeLastEditedValues(Specimen lastSpecimen) { 
    	if (lastEditedSpecimen==null) { 
    		lastEditedSpecimen = new Specimen();
    	}
    	lastEditedSpecimen.setSpecificLocality(lastSpecimen.getSpecificLocality());
    	lastEditedSpecimen.setHigherGeography(lastSpecimen.getHigherGeography());
    	lastEditedSpecimen.setCollection(lastSpecimen.getCollection());
	}
}