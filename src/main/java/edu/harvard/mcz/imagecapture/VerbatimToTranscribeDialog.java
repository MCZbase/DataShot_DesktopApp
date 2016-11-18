/**
 * VerbatimToTranscribeDialog.java
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.data.UsersTableModel;
import edu.harvard.mcz.imagecapture.struct.GenusSpeciesCount;
import edu.harvard.mcz.imagecapture.struct.GenusSpeciesCountTableModel;
import edu.harvard.mcz.imagecapture.ui.ButtonEditor;
import edu.harvard.mcz.imagecapture.ui.ButtonRenderer;

import javax.swing.JTable;

/**
 * @author mole
 *
 */
public class VerbatimToTranscribeDialog extends JDialog {
	
	private static final long serialVersionUID = 1871411835203004797L;

	private static final Log log = LogFactory.getLog(VerbatimToTranscribeDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JTable table;

	/**
	 * Create the dialog.
	 */
	public VerbatimToTranscribeDialog() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 726, 557);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			table = new JTable();
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			GenusSpeciesCountTableModel model = new GenusSpeciesCountTableModel(sls.countSpecimensForVerbatim());
			table.setModel(model);
			table.setDefaultRenderer(GenusSpeciesCount.class, new ButtonRenderer("Transcribe"));
            table.setDefaultEditor(GenusSpeciesCount.class, new ButtonEditor(ButtonEditor.OPEN_SPECIMEN_VERBATIM, this));			
			contentPanel.add(table, BorderLayout.CENTER);
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
					}
					
				});
				buttonPane.add(cancelButton);
			}
		}
	}
}
