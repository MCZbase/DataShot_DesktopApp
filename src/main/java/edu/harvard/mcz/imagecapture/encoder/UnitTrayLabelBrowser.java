/**
 * UnitTrayLabelBrowser.java
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
package edu.harvard.mcz.imagecapture.encoder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.AbstractAction;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;

import edu.harvard.mcz.imagecapture.ImageCaptureApp;
import edu.harvard.mcz.imagecapture.data.SpecimenListTableModel;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabelTableModel;
import edu.harvard.mcz.imagecapture.exceptions.PrintFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.utility.DragDropJTable;

import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** UnitTrayLabelBrowser
 * 
 * @author Paul J. Morris
 *
 */
public class UnitTrayLabelBrowser extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(ImageCaptureApp.class);
	
	private JPanel jContentPane = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private TableRowSorter<UnitTrayLabelTableModel> sorter;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private UnitTrayLabelBrowser thisFrame = null;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenu = null;
	private JMenuItem jMenuItemCopy = null;
	private JMenuItem jMenuItemPaste = null;
	private JMenuItem jMenuItemUndo = null;
	private JMenuItem jMenuItemRedo = null;
	private JMenu jMenu1 = null;
	private JMenuItem jMenuItem3 = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItemAddRow = null;
	private UnitTrayLabelTableModel tableModel = null;
	protected UndoManager undo = new UndoManager();
	protected UndoAction undoAction = null;
	protected RedoAction redoAction = null;

	/**
	 * This is the default constructor
	 */
	public UnitTrayLabelBrowser() {
		super();
		initialize();
		pack();
		center();
		thisFrame = this;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(1100, 900);
		this.setJMenuBar(getJJMenuBar());
		this.setPreferredSize(new Dimension(1400,900));
		this.setContentPane(getJContentPane());
		this.setTitle("Create Unit Tray Labels");
	}
	
	public void center() { 
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
			tableModel = new UnitTrayLabelTableModel();
			jTable = new DragDropJTable(tableModel);
			jTable.setDragEnabled(true);
			jTable.setDropMode(DropMode.ON);
			//tableModel.addUndoableEditListener(new MyUndoableEditListener());
			sorter = new TableRowSorter<UnitTrayLabelTableModel>(tableModel);
			jTable.setRowSorter(sorter);
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
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJButton(), new GridBagConstraints());
			jPanel.add(getJButton1(), gridBagConstraints);
			jPanel.add(getJButton2(), gridBagConstraints1);
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
			jButton.setText("Add");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                     addRow();
				}
			});
		}
		return jButton;
	}

	private void addRow() {
		
		tableModel.addRow();

		// scroll to last row in table.  Make first cell modal in latest row editable.
		jTable.scrollRectToVisible(jTable.getCellRect(jTable.getRowCount(), 1, false));

	    int lastRow = jTable.getRowCount() - 1;
	    jTable.getSelectionModel().setSelectionInterval(lastRow, lastRow);
	    int editColumn = 0;
	    jTable.editCellAt(lastRow, editColumn);
	    // scroll to the editing field
	    jTable.scrollRectToVisible(
	    		jTable.getCellRect(lastRow, editColumn, true));
				
		log.debug(jTable.getVisibleRect());
	}
	
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Make PDF");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    makePDF();
				}
			});
		}
		return jButton1;
	}

	public void makePDF() { 
		boolean ok = false;
		String message = "";
		try {
			ok = LabelEncoder.printList(tableModel.getList());
		} catch (PrintFailedException e1) {
			message = "PDF generation failed: " + e1.getMessage();
		}
		if (ok) { 
			message = "File labels.pdf was generated.";
			((AbstractTableModel)jTable.getModel()).fireTableDataChanged();
		} 
		JOptionPane.showMessageDialog(thisFrame, message, "PDF Generation", JOptionPane.OK_OPTION);	
	}
	
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Close");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return jButton2;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenu1());
			jJMenuBar.add(getJMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu() {
		if (jMenu == null) {
			jMenu = new JMenu();
			jMenu.setText("Edit");
			jMenu.setMnemonic(KeyEvent.VK_E);
			jMenu.add(getJMenuItem());
			jMenu.add(getJMenuItem1());
			jMenu.add(getJMenuItemUndo());
			jMenu.add(getJMenuItemRedo());
			jMenu.add(getJMenuItemAddRow());
		}
		return jMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem() {
		if (jMenuItemCopy == null) {
			jMenuItemCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
			jMenuItemCopy.setText("Copy");
			jMenuItemCopy.setMnemonic(KeyEvent.VK_C);
			jMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
			jMenuItemCopy.setEnabled(true);
		}
		return jMenuItemCopy;
	}

	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem1() {
		if (jMenuItemPaste == null) {
			jMenuItemPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
			jMenuItemPaste.setText("Paste");
			jMenuItemPaste.setMnemonic(KeyEvent.VK_P);
			jMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke(
			        KeyEvent.VK_V, ActionEvent.CTRL_MASK));			
			jMenuItemPaste.setEnabled(true);
		}
		return jMenuItemPaste;
	}

	private JMenuItem getJMenuItemUndo() {
		if (jMenuItemUndo == null) {
			undoAction = new UndoAction();
			jMenuItemUndo = new JMenuItem(undoAction);
			jMenuItemUndo.setText("Undo");
			jMenuItemUndo.setMnemonic(KeyEvent.VK_U);		
			jMenuItemUndo.setEnabled(false);
		}
		return jMenuItemUndo;
	}
	
	private JMenuItem getJMenuItemRedo() {
		if (jMenuItemRedo == null) {
			redoAction = new RedoAction();
			jMenuItemRedo = new JMenuItem(redoAction);
			jMenuItemRedo.setText("Redo");
			jMenuItemRedo.setMnemonic(KeyEvent.VK_R);			
			jMenuItemRedo.setEnabled(false);
		}
		return jMenuItemRedo;
	}	
	

	/**
	 * This method initializes jMenu1	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenu1() {
		if (jMenu1 == null) {
			jMenu1 = new JMenu();
			jMenu1.setText("File");
			jMenu1.setMnemonic(KeyEvent.VK_F);
			jMenu1.add(getJMenuItem3());
			jMenu1.add(getJMenuItem2());
		}
		return jMenu1;
	}

	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("Create PDF");
			jMenuItem3.setMnemonic(KeyEvent.VK_D);
			jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					makePDF();
				}
			});
		}
		return jMenuItem3;
	}

	private JMenuItem getJMenuItemAddRow() {
		if (jMenuItemAddRow == null) {
			jMenuItemAddRow = new JMenuItem();
			jMenuItemAddRow.setText("Add Blank Row");
			jMenuItemAddRow.setMnemonic(KeyEvent.VK_D);
			jMenuItemAddRow.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addRow();
					

					//jTable.scrollRectToVisible(jTable.getCellRect(jTable.getRowCount(), 1, false));
					//jTable.editCellAt(jTable.getRowCount(), 1);
					//jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getMaximum());
					//jScrollPane.getViewport().invalidate();
					//jScrollPane.getViewport().validate();
					//jScrollPane.getVerticalScrollBar().setValue(jScrollPane.getVerticalScrollBar().getMaximum());
					
					Rectangle newCell = new Rectangle(
							1,
						    jTable.getRowHeight()*(jTable.getRowCount()),
						    10,
						    jTable.getRowHeight()
						    );
					//jTable.scrollRectToVisible(newCell);
				}
			});
		}
		return jMenuItemAddRow;
	}	
	
	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem2() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("Close Window");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return jMenuItem;
	}
	
	protected class MyUndoableEditListener implements UndoableEditListener {
		
		public void undoableEditHappened(UndoableEditEvent e) {
			//Remember the edit and update the menus
			undo.addEdit(e.getEdit());
			//undoAction.updateUndoState();
			//redoAction.updateRedoState();
		}
	}  

	
	protected class UndoAction extends AbstractAction {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
		    try {
		        undo.undo();
		    } catch (CannotUndoException ex) {
		        System.out.println("Unable to undo: " + ex);
		        ex.printStackTrace();
		    }
		    //updateUndoState();
		    //redoAction.updateRedoState();
		} 
		
	}
	
	protected class RedoAction extends AbstractAction {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
		        undo.redo();
		    } catch (CannotRedoException ex) {
		        System.out.println("Unable to redo: " + ex);
		        ex.printStackTrace();
		    }
		    //updateRedoState();
		    //undoAction.updateUndoState();
		} 
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
