/**
 * SpecimenPartAttributeDialog.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2013 President and Fellows of Harvard College
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
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.AssociatedTaxon;
import edu.harvard.mcz.imagecapture.data.Caste;
import edu.harvard.mcz.imagecapture.data.LifeStage;
import edu.harvard.mcz.imagecapture.data.Sex;
import edu.harvard.mcz.imagecapture.data.SpecimenPart;
import edu.harvard.mcz.imagecapture.data.SpecimenPartAttribute;
import edu.harvard.mcz.imagecapture.data.SpecimenPartAttributeLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPartLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenPartsAttrTableModel;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog providing a list of specimen part attributes for a specimen part, 
 * with the ability to add more attributes.
 * 
 * @author mole
 *
 */
public class SpecimenPartAttributeDialog extends JDialog {
	private static final long serialVersionUID = 1870811168027430021L;

	private static final Log log = LogFactory.getLog(SpecimenPartAttributeDialog.class);
	
	private SpecimenPartAttributeDialog thisDialog;
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JPopupMenu popupMenu; // on table
	private int clickedOnRow;     // on table
	
	private JButton okButton;
	
	private SpecimenPart parentPart;
	private JComboBox comboBoxType;
	private JComboBox comboBoxValue;
	private JTextField textFieldUnits;
	private JTextField textFieldRemarks;

	/**
	 * Create the dialog.
	 */
	public SpecimenPartAttributeDialog(SpecimenPart parentPart) {
		this.parentPart = parentPart;
		SpecimenPartLifeCycle spls = new SpecimenPartLifeCycle();
		spls.merge(parentPart);
//		SpecimenPartAttributeLifeCycle spals = new SpecimenPartAttributeLifeCycle();
//		Iterator<SpecimenPartAttribute> i = parentPart.getAttributeCollection().iterator();
//		while (i.hasNext()) { 
//			try {
//				SpecimenPartAttribute attrib = i.next();
//				spals.persist(attrib);
//			} catch (SaveFailedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		init();
	} 
	
	public SpecimenPartAttributeDialog() { 
		this.parentPart = new SpecimenPart();
		init();
	}
	
	private void init() { 
		thisDialog = this;
		setBounds(100, 100, 820, 335);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
			JPanel panel = new JPanel();
			panel.setLayout(new FormLayout(new ColumnSpec[] {
					FormSpecs.RELATED_GAP_COLSPEC,
					FormSpecs.DEFAULT_COLSPEC,
					FormSpecs.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,}));
			{
				JLabel lblAttributeType = new JLabel("Attribute Type");
				panel.add(lblAttributeType, "2, 2, right, default");
			}
			{
				comboBoxType = new JComboBox();
				comboBoxType.setModel(new DefaultComboBoxModel(new String[] {"caste", "scientific name", "sex", "life stage", "associated taxon"}));
				comboBoxType.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						String item = comboBoxType.getSelectedItem().toString();
						if (item!=null) {
							comboBoxValue.setEditable(false);
							if (item.equals("scientific name")) { 
								comboBoxValue.setEditable(true);
							}
							if (item.equals("sex")) { 
								comboBoxValue.setModel(new DefaultComboBoxModel(Sex.getSexValues()));
							}
							if (item.equals("life stage")) { 
								comboBoxValue.setModel(new DefaultComboBoxModel(LifeStage.getLifeStageValues()));
							}
							if (item.equals("caste")) { 
								comboBoxValue.setModel(new DefaultComboBoxModel(Caste.getCasteValues()));
							}
							if (item.equals("associated taxon")) { 
								comboBoxValue.setModel(new DefaultComboBoxModel(AssociatedTaxon.getAssociatedTaxonValues()));
							}
						}
					} 
					
				});
				panel.add(comboBoxType, "4, 2, fill, default");
			}
			{
				JLabel lblValue = new JLabel("Value");
				panel.add(lblValue, "2, 4, right, default");
			}
			{
				comboBoxValue = new JComboBox();
				comboBoxValue.setModel(new DefaultComboBoxModel(Caste.getCasteValues()));
				
				panel.add(comboBoxValue, "4, 4, fill, default");
			}
			{
				JLabel lblUnits = new JLabel("Units");
				panel.add(lblUnits, "2, 6, right, default");
			}
			{
				textFieldUnits = new JTextField();
				panel.add(textFieldUnits, "4, 6, fill, default");
				textFieldUnits.setColumns(10);
			}
			{
				JLabel lblRemarks = new JLabel("Remarks");
				panel.add(lblRemarks, "2, 8, right, default");
			}
			contentPanel.setLayout(new BorderLayout(0, 0));
			contentPanel.add(panel, BorderLayout.WEST);
			{
				textFieldRemarks = new JTextField();
				panel.add(textFieldRemarks, "4, 8, fill, default");
				textFieldRemarks.setColumns(10);
			}
			{
				JButton btnAdd = new JButton("Add");
				btnAdd.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SpecimenPartAttribute newAttribs = new SpecimenPartAttribute();
						newAttribs.setAttributeType(comboBoxType.getSelectedItem().toString());
						newAttribs.setAttributeValue(comboBoxValue.getSelectedItem().toString());
						newAttribs.setAttributeUnits(textFieldUnits.getText());
						newAttribs.setAttributeRemark(textFieldRemarks.getText());
						newAttribs.setSpecimenPartId(parentPart);
						newAttribs.setAttributeDeterminer(Singleton.getSingletonInstance().getUserFullName());
						parentPart.getAttributeCollection().add(newAttribs);
						SpecimenPartAttributeLifeCycle sls = new SpecimenPartAttributeLifeCycle();
						try {
							sls.attachDirty(newAttribs);
						} catch (SaveFailedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						((AbstractTableModel)table.getModel()).fireTableDataChanged();
					}
				});
				panel.add(btnAdd, "4, 10");
			}
	        try {
			    JLabel lblNewLabel = new JLabel(parentPart.getSpecimenId().getBarcode() + ":" + parentPart.getPartName() + " " + parentPart.getPreserveMethod() + " (" + parentPart.getLotCount() + ")    Right click on table to edit attributes.");
			    contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		    } catch (Exception e) {
	    		JLabel lblNewLabel = new JLabel("No Specimen");
    			contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		    }
			JComboBox comboBox = new JComboBox();
			comboBox.addItem("caste");
			JComboBox comboBox1 = new JComboBox();
			for (int i=0; i<Caste.getCasteValues().length; i++) { 
				comboBox1.addItem(Caste.getCasteValues()[i]);
			}
			JScrollPane scrollPane = new JScrollPane();
			
		    table = new JTable(new SpecimenPartsAttrTableModel((Collection<SpecimenPartAttribute>) parentPart.getAttributeCollection()));
		    
		    //table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(comboBox));	
		    
		    table.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnRow = ((JTable)e.getComponent()).getSelectedRow();
					     popupMenu.show(e.getComponent(),e.getX(),e.getY());
					}
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) { 
						 clickedOnRow = ((JTable)e.getComponent()).getSelectedRow();
					     popupMenu.show(e.getComponent(),e.getX(),e.getY());
					}
				}
			});
		    
		    popupMenu = new JPopupMenu();
		    
			JMenuItem mntmCloneRow = new JMenuItem("Edit Row");
			mntmCloneRow.setMnemonic(KeyEvent.VK_E);
			mntmCloneRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					try {
						// Launch a dialog to edit the selected row.
						SpecimenPartAttribEditDialog popup = new SpecimenPartAttribEditDialog(((SpecimenPartsAttrTableModel)table.getModel()).getRowObject(clickedOnRow));
						popup.setVisible(true);
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(thisDialog, "Failed to edit a part attribute row. " + ex.getMessage());
					}
				}
			});	
			popupMenu.add(mntmCloneRow);
			
			JMenuItem mntmDeleteRow = new JMenuItem("Delete Row");
			mntmDeleteRow.setMnemonic(KeyEvent.VK_D);
			mntmDeleteRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { 
					try { 
						if (clickedOnRow>=0) { 
					       ((SpecimenPartsAttrTableModel)table.getModel()).deleteRow(clickedOnRow);
						}
					} catch (Exception ex) { 
						log.error(ex.getMessage());
						JOptionPane.showMessageDialog(thisDialog, "Failed to delete a part attribute row. " + ex.getMessage());
					}
				}
			});	
			popupMenu.add(mntmDeleteRow);	
		    
		    // TODO: Enable controlled value editing of selected row.
		    
		    // table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(comboBox1));
		    
		    scrollPane.setViewportView(table);
			contentPanel.add(scrollPane, BorderLayout.EAST);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButton.grabFocus();
						thisDialog.setVisible(false);
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
						thisDialog.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
