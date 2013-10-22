/**
 * UserListPanel.java
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
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.data.UsersTableModel;
import edu.harvard.mcz.imagecapture.ui.ButtonEditor;
import edu.harvard.mcz.imagecapture.ui.ButtonRenderer;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Dimension;

/** UserListPanel
 * 
 * @author Paul J. Morris
 *
 */
public class UserListBrowser extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final Log log = LogFactory.getLog(UserListBrowser.class);
	
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	private JPanel jPanel1 = null;
	private JToolBar jJToolBarBar = null;
	private JButton jButtonAddUser = null;
	private EditUserPanel editUserPanel = null;
	private UserListBrowser thisPanel = null; 
	
	/**
	 * This is the default constructor
	 */
	public UserListBrowser() {
		super();
		thisPanel = this;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(566, 308);
		this.setLayout(new BorderLayout());
		this.add(getJPanel1(), BorderLayout.SOUTH);
		this.add(getJScrollPane(), BorderLayout.CENTER);
		this.add(getJJToolBarBar(), BorderLayout.NORTH);
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
			jTable = new JTable();
			UsersTableModel model = new UsersTableModel(UsersLifeCycle.findAll());
			jTable.setModel(model);
			jTable.setDefaultRenderer(Users.class, new ButtonRenderer());
            jTable.setDefaultEditor(Users.class, new ButtonEditor(ButtonEditor.OPEN_USER, thisPanel));
		}
		return jTable;
	}
	
	private JPanel getJPanel1() { 
		if (jPanel1== null) { 
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.add(getEditUserPanel(), BorderLayout.CENTER);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jJToolBarBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			jJToolBarBar = new JToolBar();
			jJToolBarBar.setFloatable(false);
			jJToolBarBar.add(getJButtonAddUser());
		}
		return jJToolBarBar;
	}

	/**
	 * This method initializes jButtonAddUser	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAddUser() {
		if (jButtonAddUser == null) {
			jButtonAddUser = new JButton();
			jButtonAddUser.setText("Add New User");
			try { 
				jButtonAddUser.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/add_person_30px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jButtonAddUser.");
				log.error(e);
			}
			jButtonAddUser.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Users newUser = new Users();
					thisPanel.editUserPanel.setUser(newUser);
				}
			});
		}
		return jButtonAddUser;
	}

	/**
	 * This method initializes editUserPanel	
	 * 	
	 * @return edu.harvard.mcz.imagecapture.EditUserPanel	
	 */
	public EditUserPanel getEditUserPanel() {
		if (editUserPanel == null) {
			editUserPanel = new EditUserPanel();
		}
		return editUserPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
