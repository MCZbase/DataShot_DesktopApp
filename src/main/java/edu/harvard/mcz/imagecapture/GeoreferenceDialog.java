/**
 * GeoreferenceDialog.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2014 President and Fellows of Harvard College
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
import java.awt.FlowLayout;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import edu.harvard.mcz.imagecapture.data.LatLong;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.JFormattedTextField;

/**
 * @author mole
 *
 */
public class GeoreferenceDialog extends JDialog {
	
	private static final long serialVersionUID = -257199970146455008L;

	private static final Log log = LogFactory.getLog(GeoreferenceDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	
	private JComboBox<String> comboBoxOrigUnits;
	private JComboBox<String> comboBoxErrorUnits;
	private JTextField txtGPSAccuracy;
	private JTextField textFieldDecimalLat;
	private JTextField textFieldDecimalLong;
	private JTextField txtLatDegrees;
	private JTextField txtLatDecMin;
	private JTextField txtLatMin;
	private JTextField txtLatSec;
	private JComboBox<String> cbLatDir;
	private JTextField txtLongDegrees;
	private JTextField txtLongDecMin;
	private JTextField txtLongMin;
	private JTextField txtLongSec;
	private JComboBox<String> cbLongDir;	
	private JComboBox<String> cbDatum;
	private JComboBox<String> cbMethod;
    private LatLong georeference;
    private JButton okButton;
    private JLabel lblErrorLabel;
    private JLabel lblNewLabel;
    private JTextField textFieldRemarks;
    private JLabel lblErrorRadius;
    private JTextField txtErrorRadius;
	private JTextField textFieldDetBy;
	private JFormattedTextField textDetDate;
	private JTextField textRefSource;
	
	/**
	 * Create the dialog.
	 */
	public GeoreferenceDialog() {
		init();
	} 
	
	public GeoreferenceDialog(LatLong georeference) {
		this.georeference = georeference;
		init();
		loadData();
		setState();
	} 	
	
	private void loadData() { 
		lblErrorLabel.setText("");
		textFieldDecimalLat.setText(georeference.getDecLatString());
		textFieldDecimalLong.setText(georeference.getDecLongString());
		cbDatum.setSelectedItem(georeference.getDatum());
		cbMethod.setSelectedItem(georeference.getGeorefmethod());
		
		txtGPSAccuracy.setText(georeference.getGpsaccuracyString());
	
		comboBoxOrigUnits.setSelectedItem(georeference.getOrigLatLongUnits());
		
		txtLatDegrees.setText(georeference.getLatDegString());
		txtLatDecMin.setText(georeference.getDecLatMinString());
		txtLatMin.setText(georeference.getLatMinString());
		txtLatSec.setText(georeference.getLatSecString());
		cbLatDir.setSelectedItem(georeference.getLatDir());
	
		txtLongDegrees.setText(georeference.getLongDegString());
		txtLongDecMin.setText(georeference.getDecLongMinString());
		txtLongMin.setText(georeference.getLongMinString());
		txtLongSec.setText(georeference.getLongSecString());
		cbLongDir.setSelectedItem(georeference.getLongDir());
		
		txtErrorRadius.setText(georeference.getMaxErrorDistanceString().toString());
		comboBoxErrorUnits.setSelectedItem(georeference.getMaxErrorUnits());
		
		this.textFieldDetBy.setText(georeference.getDeterminedByAgent());
		this.textDetDate.setValue(new SimpleDateFormat("yyyy-MM-dd").format(georeference.getDeterminedDate()));
		this.textRefSource.setText(georeference.getLatLongRefSource());
		
		textFieldRemarks.setText(georeference.getLatLongRemarks());
	}
	
	private void setState() { 
		String acc = this.cbMethod.getSelectedItem().toString();
		if (acc.equals("GPS")) { 
			this.txtGPSAccuracy.setEnabled(true);
		} else { 
			this.txtGPSAccuracy.setEnabled(false);
		}
		
		String state = this.comboBoxOrigUnits.getSelectedItem().toString();
		log.debug(state);
		switch (state) { 
		case "degrees dec. minutes":
			this.textFieldDecimalLat.setEnabled(false);
			this.textFieldDecimalLong.setEnabled(false);
			this.txtLatDegrees.setEnabled(true);
			this.txtLatDecMin.setEnabled(true);
			this.txtLatMin.setEnabled(false);
			this.txtLatSec.setEnabled(false);
			this.cbLatDir.setEnabled(true);
			this.txtLongDegrees.setEnabled(true);
			this.txtLongDecMin.setEnabled(true);
			this.txtLongMin.setEnabled(false);
			this.txtLongSec.setEnabled(false);
			this.cbLongDir.setEnabled(true);
			break;
		case "deg. min. sec.":
			this.textFieldDecimalLat.setEnabled(false);
			this.textFieldDecimalLong.setEnabled(false);
			this.txtLatDegrees.setEnabled(true);
			this.txtLatDecMin.setEnabled(false);
			this.txtLatMin.setEnabled(true);
			this.txtLatSec.setEnabled(true);
			this.cbLatDir.setEnabled(true);
			this.txtLongDegrees.setEnabled(true);
			this.txtLongDecMin.setEnabled(false);
			this.txtLongMin.setEnabled(true);
			this.txtLongSec.setEnabled(true);
			this.cbLongDir.setEnabled(true);
			break;
		case "decimal degrees":
		case "unknown":
		default: 
			this.textFieldDecimalLat.setEnabled(true);
			this.textFieldDecimalLong.setEnabled(true);
			this.txtLatDegrees.setEnabled(false);
			this.txtLatDecMin.setEnabled(false);
			this.txtLatMin.setEnabled(false);
			this.txtLatSec.setEnabled(false);
			this.cbLatDir.setEnabled(false);
			this.txtLongDegrees.setEnabled(false);
			this.txtLongDecMin.setEnabled(false);
			this.txtLongMin.setEnabled(false);
			this.txtLongSec.setEnabled(false);
			this.cbLongDir.setEnabled(false);
			break;
		}
		
	}
	
	private boolean saveData() {
		boolean result = true;
		this.okButton.grabFocus();
		if (textFieldDecimalLat.getText().length()>0) { 
			try { 
		       georeference.setDecLat(BigDecimal.valueOf(Double.parseDouble(textFieldDecimalLat.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Latitude number format" );
				result = false;
			}
		}
		if (textFieldDecimalLong.getText().length()>0) {
			try { 
		        georeference.setDecLong(BigDecimal.valueOf(Double.parseDouble(textFieldDecimalLong.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Longitude number format" );
				result = false;
			}
		}
		if (cbDatum.getSelectedItem()!=null) { 
		    georeference.setDatum(cbDatum.getSelectedItem().toString());
		}
		if (cbMethod.getSelectedItem()!=null) { 
		    georeference.setGeorefmethod(cbMethod.getSelectedItem().toString());
		}
		
		if (txtGPSAccuracy.getText().length()>0) {
			try { 
		      georeference.setGpsaccuracy(BigDecimal.valueOf(Double.parseDouble(txtGPSAccuracy.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: GPS Accuracy number format" );
				result = false;
			}
		}
		
		if (comboBoxOrigUnits.getSelectedItem()!=null) { 
		    georeference.setOrigLatLongUnits(comboBoxOrigUnits.getSelectedItem().toString());
		} 
		
		if (txtLatDegrees.getText().length()>0) {
			try { 
		        georeference.setLatDeg(Integer.parseInt(txtLatDegrees.getText()));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Lat Degrees number format" );
				result = false;
			}
		}		
		if (txtLatDecMin.getText().length()>0) {
			try { 
		        georeference.setDecLatMin(BigDecimal.valueOf(Double.parseDouble(txtLatDecMin.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Lat Dec Min number format" );
				result = false;
			}
		}
		if (txtLatMin.getText().length()>0) {
			try { 
		        georeference.setLatMin(Integer.parseInt(txtLatMin.getText()));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Lat Min number format" );
				result = false;
			}
		}
		if (txtLatSec.getText().length()>0) {
			try { 
		        georeference.setLatSec(BigDecimal.valueOf(Double.parseDouble(txtLatSec.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Lat Degrees number format" );
				result = false;
			}
		}
		if (cbLatDir.getSelectedItem()!=null) { 
		    georeference.setLatDir(cbLatDir.getSelectedItem().toString());
		}
	
		if (txtLongDegrees.getText().length()>0) {
			try { 
		        georeference.setLongDeg(Integer.parseInt(txtLongDegrees.getText()));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Long Degrees number format" );
				result = false;
			}
		}		
		if (txtLongDecMin.getText().length()>0) {
			try { 
		        georeference.setDecLongMin(BigDecimal.valueOf(Double.parseDouble(txtLongDecMin.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Long Dec Min number format" );
				result = false;
			}
		}
		if (txtLongMin.getText().length()>0) {
			try { 
		        georeference.setLongMin(Integer.parseInt(txtLongMin.getText()));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Long Min number format" );
				result = false;
			}
		}
		if (txtLongSec.getText().length()>0) {
			try { 
		        georeference.setLongSec(BigDecimal.valueOf(Double.parseDouble(txtLongSec.getText())));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Long Degrees number format" );
				result = false;
			}
		}
		if (cbLongDir.getSelectedItem()!=null) { 
		   georeference.setLongDir(cbLongDir.getSelectedItem().toString());
		}
		
		if (txtErrorRadius.getText().length()>0) { 
			try { 
			   georeference.setMaxErrorDistance(Integer.parseInt(txtErrorRadius.getText()));
			} catch (NumberFormatException e) { 
				lblErrorLabel.setText("Error: Error radius number format" );
				result = false;
			}
		}
		
		if (comboBoxErrorUnits.getSelectedItem()!=null) { 
			georeference.setMaxErrorUnits(comboBoxErrorUnits.getSelectedItem().toString());
		}
		
		georeference.setDeterminedByAgent(this.textFieldDetBy.getText());
		
		try {
			georeference.setDeterminedDate(new SimpleDateFormat("yyyy-MM-dd").parse(this.textDetDate.getText()));
		} catch (ParseException e) {
			lblErrorLabel.setText("Error: Error date determined format" );
			result = false;
		}
		
		georeference.setLatLongRefSource(this.textRefSource.getText());
		
		georeference.setLatLongRemarks(this.textFieldRemarks.getText());
		
		return result;
	}
	
	private void init() { 
		setBounds(100, 100, 450, 560);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JLabel lblLatitude = new JLabel("Latitude");
			lblLatitude.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(lblLatitude);
		}
		
		textFieldDecimalLat = new JTextField();
		contentPanel.add(textFieldDecimalLat);
		textFieldDecimalLat.setColumns(10);
		
		JLabel lblLongitude = new JLabel("Longitude");
		lblLongitude.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLongitude);
		{
			textFieldDecimalLong = new JTextField();
			contentPanel.add(textFieldDecimalLong);
			textFieldDecimalLong.setColumns(10);
		}
		{
			JLabel lblDatum = new JLabel("Datum");
			lblDatum.setHorizontalAlignment(SwingConstants.RIGHT);
			contentPanel.add(lblDatum);
		}
		
		@SuppressWarnings("unchecked")
		ComboBoxModel<String> datumModel = new ListComboBoxModel<String>(LatLong.getDatumValues());
		cbDatum = new JComboBox<String>(datumModel);
		contentPanel.add(cbDatum);
		
		JLabel lblMethod = new JLabel("Method");
		lblMethod.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblMethod);
		
		@SuppressWarnings("unchecked")
		ComboBoxModel<String> methodModel = new ListComboBoxModel<String>(LatLong.getGeorefMethodValues());
		cbMethod = new JComboBox<String>(new DefaultComboBoxModel<String>(new String[] {"not recorded", "unknown", "GEOLocate", "Google Earth", "Gazeteer", "GPS", "MaNIS/HertNet/ORNIS Georeferencing Guidelines"}));
		cbMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState();
			}
		});
		contentPanel.add(cbMethod);
		
		JLabel lblAccuracy = new JLabel("GPS Accuracy");
		lblAccuracy.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblAccuracy);
		
		txtGPSAccuracy = new JTextField();
		txtGPSAccuracy.setColumns(10);
		contentPanel.add(txtGPSAccuracy);
		
		JLabel lblNewLabel_1 = new JLabel("Original Units");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel_1);
		
		comboBoxOrigUnits = new JComboBox<String>();
		comboBoxOrigUnits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setState();
			}
		});
		comboBoxOrigUnits.setModel(new DefaultComboBoxModel<String>(new String[] {"decimal degrees", "deg. min. sec.", "degrees dec. minutes", "unknown"}));
		contentPanel.add(comboBoxOrigUnits);
		
		lblErrorRadius = new JLabel("Error Radius");
		lblErrorRadius.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblErrorRadius);
		
		txtErrorRadius = new JTextField();
		txtErrorRadius.setColumns(10);
		contentPanel.add(txtErrorRadius);
		
		JLabel lblErrorRadiusUnits = new JLabel("Error Radius Units");
		lblErrorRadiusUnits.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblErrorRadiusUnits);
		
		comboBoxErrorUnits = new JComboBox<String>();
		comboBoxErrorUnits.setModel(new DefaultComboBoxModel<String>(new String[] {"m","ft","km","mi","yd"}));
		contentPanel.add(comboBoxErrorUnits);		
		
		
		JLabel lblLatDegrees = new JLabel("Lat Degrees");
		lblLatDegrees.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLatDegrees);
		
		txtLatDegrees = new JTextField();
		txtLatDegrees.setColumns(4);
		contentPanel.add(txtLatDegrees);
		
		JLabel lblLatDecMin = new JLabel("Lat Dec Min");
		lblLatDecMin.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLatDecMin);
		
		txtLatDecMin = new JTextField();
		txtLatDecMin.setColumns(6);
		contentPanel.add(txtLatDecMin);		
		
		JLabel lblLatMin = new JLabel("Lat Min");
		lblLatMin.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLatMin);
		
		txtLatMin = new JTextField();
		txtLatMin.setColumns(6);
		contentPanel.add(txtLatMin);	
		
		JLabel lblLatSec = new JLabel("Lat Sec");
		lblLatSec.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLatSec);
		
		txtLatSec = new JTextField();
		txtLatSec.setColumns(6);
		contentPanel.add(txtLatSec);	
		
		JLabel lblLatDir = new JLabel("Lat N/S");
		lblLatDir.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLatDir);
		
		cbLatDir = new JComboBox<String>();
		cbLatDir.setModel(new DefaultComboBoxModel<String>(new String[] {"N", "S"}));
		contentPanel.add(cbLatDir);			
				
		
		JLabel lblLongDegrees = new JLabel("Long Degrees");
		lblLongDegrees.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLongDegrees);
		
		txtLongDegrees = new JTextField();
		txtLongDegrees.setColumns(4);
		contentPanel.add(txtLongDegrees);
		
		JLabel lblLongDecMin = new JLabel("Long Dec Min");
		lblLongDecMin.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLongDecMin);
		
		txtLongDecMin = new JTextField();
		txtLongDecMin.setColumns(6);
		contentPanel.add(txtLongDecMin);		
		
		JLabel lblLongMin = new JLabel("Long Min");
		lblLongMin.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLongMin);
		
		txtLongMin = new JTextField();
		txtLongMin.setColumns(6);
		contentPanel.add(txtLongMin);	
		
		JLabel lblLongSec = new JLabel("Long Sec");
		lblLongSec.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLongSec);
		
		txtLongSec = new JTextField();
		txtLongSec.setColumns(6);
		contentPanel.add(txtLongSec);	
		
		JLabel lblLongDir = new JLabel("Long E/W");
		lblLongDir.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblLongDir);
		
		cbLongDir = new JComboBox<String>();
		cbLongDir.setModel(new DefaultComboBoxModel<String>(new String[] {"E", "W"}));
		contentPanel.add(cbLongDir);		
		
		JLabel lblDetBy = new JLabel("Determined By");
		lblDetBy.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblDetBy);
		
		
		textFieldDetBy = new JTextField();
		contentPanel.add(textFieldDetBy);
		textFieldDetBy.setColumns(10);
		
		JLabel lblDetDate = new JLabel("Date Determined");
		lblDetDate.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblDetDate);
		
		try {
			textDetDate = new JFormattedTextField(new MaskFormatter("####-##-##"));
		} catch (ParseException e1) {
			textDetDate = new JFormattedTextField();
		}
		textDetDate.setToolTipText("Date on which georeference was made yyyy-mm-dd");
		contentPanel.add(textDetDate);
		
		JLabel lblRef = new JLabel("Reference Source");
		lblRef.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblRef);
		
		textRefSource = new JTextField();
		contentPanel.add(textRefSource);
		textRefSource.setColumns(10);
		
		lblNewLabel = new JLabel("Remarks");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPanel.add(lblNewLabel);
		
		textFieldRemarks = new JTextField();
		contentPanel.add(textFieldRemarks);
		textFieldRemarks.setColumns(10);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
					lblErrorLabel = new JLabel("Message");
					buttonPane.add(lblErrorLabel);
			}
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					 
						lblErrorLabel.setText("");
						
						if (saveData()) { 
						   setVisible(false);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						loadData();
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
