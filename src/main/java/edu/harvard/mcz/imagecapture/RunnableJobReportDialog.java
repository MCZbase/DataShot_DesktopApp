/**
 * RunnableJobReportDialog.java
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.TableModel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.jobs.RunnableJobError;
import edu.harvard.mcz.imagecapture.jobs.RunnableJobErrorTableModel;

import java.awt.Insets;
import java.io.IOException;
import java.io.PrintWriter;

/** RunnableJobReportDialog
 * 
 * @author Paul J. Morris
 *
 */
public class RunnableJobReportDialog extends JDialog {

	private static final long serialVersionUID = -8151583200152856827L;

	private static final Log log = LogFactory.getLog(RunnableJobReportDialog.class);
	
	private String title = "Job Results";
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel = null;
	private JButton jButton = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JTextArea jTextArea = null;
	private JDialog thisDialog = null;
    private RunnableJobErrorTableModel model = null;
    private JButton btnSave;
	
	/**
	 * @param owner
	 * @wbp.parser.constructor
	 */
	public RunnableJobReportDialog(Frame owner) {
		super(owner);
		thisDialog = this;
		initialize();
	}
	
	public RunnableJobReportDialog(Frame owner, String resultsMessage, List<RunnableJobError> errors, String title) {
		super(owner);
		this.title = title;
		thisDialog = this;
	    model = new RunnableJobErrorTableModel(errors);
	    
	    log.debug(model.getRowCount());
		
		initialize();
		
		jTextArea.setText(resultsMessage);
        pack();
		
	}
	
	public RunnableJobReportDialog(Frame owner, String resultsMessage, List<RunnableJobError> errors, int listType, String title) {
		super(owner);
		this.title = title;
		thisDialog = this;
	    model = new RunnableJobErrorTableModel(errors, listType);
	    
	    log.debug(model.getRowCount());
		log.debug(model.getColumnCount());
		log.debug(model.getColumnName(4));
	    
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
		this.setPreferredSize(new Dimension(1000, 400));
		this.setTitle(title);
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
			jLabel.setText(title);
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
			GridBagConstraints gbc_jButtonSave = new GridBagConstraints();
			gbc_jButtonSave.insets = new Insets(0, 0, 0, 5);
			gbc_jButtonSave.gridx = 0;
			gbc_jButtonSave.gridy = 0;
			jPanel1.add(getJButtonSave(), gbc_jButtonSave);
			GridBagConstraints gbc_jButton = new GridBagConstraints();
			gbc_jButton.gridx = 1;
			gbc_jButton.gridy = 0;
			jPanel1.add(getJButton(), gbc_jButton);
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

	protected void serializeTableModel() {
		PrintWriter out = null;
		CSVPrinter writer = null;
		try {
			int cols = jTable.getModel().getColumnCount();
			CSVFormat csvFormat = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).withHeaderComments(jTextArea.getText());
			TableModel model = jTable.getModel();
			switch (cols) { 
			case 9:
				csvFormat =  CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).
				     withHeader(model.getColumnName(0), model.getColumnName(1), model.getColumnName(2), model.getColumnName(3),
				        	model.getColumnName(4), model.getColumnName(5), model.getColumnName(6), model.getColumnName(7),
				        	model.getColumnName(8)).
				     withCommentMarker('*').
				     withHeaderComments(jTextArea.getText());
				break;
			case 6: 
				csvFormat = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL). 
					withHeader(model.getColumnName(0), model.getColumnName(1), model.getColumnName(2), model.getColumnName(3),
							model.getColumnName(4), model.getColumnName(5)). 
				 	withCommentMarker('*').
					withHeaderComments(jTextArea.getText());
				break;
			case 5: 
				csvFormat = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL). 
					withHeader(model.getColumnName(0), model.getColumnName(1), model.getColumnName(2), model.getColumnName(3),
							model.getColumnName(4)). 
				 	withCommentMarker('*').
					withHeaderComments(jTextArea.getText());
				break;				
			case 4: 
				csvFormat = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL). 
				 	withHeader(model.getColumnName(0), model.getColumnName(1), model.getColumnName(2), model.getColumnName(3)). 
				 	withCommentMarker('*').
				 	withHeaderComments(jTextArea.getText());
				break;
			}
			
			log.debug(jTextArea.getText());
			log.debug(csvFormat.getHeaderComments());
			
			
			Date now = new Date(); 
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd_HHmmss");
			String time = dateFormat.format(now);
			String filename = "jobreport_"+time+".csv"; 
			out = new PrintWriter(filename);
 			
			writer = new CSVPrinter(out, csvFormat);
			writer.flush();

			int rows = jTable.getModel().getRowCount();
			for (int i=0; i<rows; i++) {
				ArrayList<String> values = new ArrayList();
				for (int col=0; col<cols; col++) { 
					values.add((String) jTable.getModel().getValueAt(i, col));
				}
				
				writer.printRecord(values);
			}
			writer.flush();
			writer.close();
			JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "Saved report to file: " + filename , "Report to CSV file", JOptionPane.OK_OPTION);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			try { 
			   out.close();	
			} catch (Exception e) {} 
			try { 
			   writer.close();	
			} catch (Exception e) {} 
			
		}
	}
	
	private JButton getJButtonSave() {
		if (btnSave == null) {
			btnSave = new JButton("Save");
			btnSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					serializeTableModel();
				}
			});
		}
		return btnSave;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
