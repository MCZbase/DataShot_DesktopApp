/**
 * PreprocessReportDialog.java
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JTable;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.ImagePreprocessError;
import edu.harvard.mcz.imagecapture.data.ImagePreprocessErrorTableModel;

/** PreprocessReportDialog
 * 
 * @author Paul J. Morris
 *
 */
public class PreprocessReportDialog extends JDialog {

	private static final long serialVersionUID = -8151583200152856827L;

	private static final Log log = LogFactory.getLog(PreprocessReportDialog.class);
	
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel = null;
	private JButton jButton = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JTextArea jTextArea = null;
	private JDialog thisDialog = null;
    private ImagePreprocessErrorTableModel model = null;
	
	/**
	 * @param owner
	 */
	public PreprocessReportDialog(Frame owner) {
		super(owner);
		thisDialog = this;
		initialize();
	}
	
	public PreprocessReportDialog(Frame owner, String resultsMessage, List<ImagePreprocessError> errors) {
		super(owner);
		thisDialog = this;
	    model = new ImagePreprocessErrorTableModel(errors);
	    
	    log.debug(model.getRowCount());
		
		initialize();
		
		jTextArea.setText(resultsMessage);
        pack();
		
	}
	
	public PreprocessReportDialog(Frame owner, String resultsMessage, List<ImagePreprocessError> errors, int listType) {
		super(owner);
		thisDialog = this;
	    model = new ImagePreprocessErrorTableModel(errors, listType);
	    
	    log.debug(model.getRowCount());
		
		initialize();
		
		jTextArea.setText(resultsMessage);
        pack();
		
	}		

	public void setMessage(String resultsMessage) {
		if (resultsMessage==null) { 
			jTextArea.setText("");
		} else { 
		    jTextArea.setText(resultsMessage);
		}
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setPreferredSize(new Dimension(965, 300));
		this.setTitle("Preprocessing Results");
		this.setContentPane(getJContentPane());
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
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
			jContentPane.add(getJPanel1(), BorderLayout.SOUTH);
			jContentPane.add(getJScrollPane(), BorderLayout.CENTER);
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
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridheight = 3;
			gridBagConstraints.gridx = 0;
			jLabel = new JLabel();
			jLabel.setText("Preprocess Results");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(jLabel, new GridBagConstraints());
			jPanel.add(getJTextArea(), gridBagConstraints);
		}
		return jPanel;
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
			jPanel1.add(getJButton(), new GridBagConstraints());
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("OK");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thisDialog.setVisible(false);
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
			jTable = new JTable(model);
			//jTable.setModel(model);
		}
		return jTable;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setEditable(false);
			jTextArea.setEnabled(true);
		}
		return jTextArea;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
