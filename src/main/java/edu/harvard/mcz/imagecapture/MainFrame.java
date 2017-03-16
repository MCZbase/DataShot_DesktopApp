/**
 * MainFrame.java
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.text.DefaultEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.HibernateUtil;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.encoder.UnitTrayLabelBrowser;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeMatcher;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;
import edu.harvard.mcz.imagecapture.jobs.Counter;
import edu.harvard.mcz.imagecapture.jobs.JobAllImageFilesScan;
import edu.harvard.mcz.imagecapture.jobs.JobCleanDirectory;
import edu.harvard.mcz.imagecapture.jobs.JobFileReconciliation;
import edu.harvard.mcz.imagecapture.jobs.JobRecheckForTemplates;
import edu.harvard.mcz.imagecapture.jobs.JobRepeatOCR;
import edu.harvard.mcz.imagecapture.jobs.JobSingleBarcodeScan;
import edu.harvard.mcz.imagecapture.jobs.RunnableJobError;
import edu.harvard.mcz.imagecapture.jobs.RunnableJobErrorTableModel;
import edu.harvard.mcz.imagecapture.loader.JobVerbatimFieldLoad;

import java.awt.GridBagConstraints;
import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Main user interface window for image capture application when run as a java application from the desktop.
 *  
 * @author Paul J. Morris
 *
 */
public class MainFrame extends JFrame implements RunnerListener {

	private static final long serialVersionUID = 536567118673854270L;
	
	private static final Log log = LogFactory.getLog(MainFrame.class);  //  @jve:decl-index=0:
	
	private MainFrame thisMainFrame;
	
	private static final int STATE_INIT = 0;     // initial state of application - most menu items disabled
	public static final int STATE_RUNNING = 1;   // setup actions complete, menu items enabled.
	public static final int STATE_RESET = 2;     // user logged out, menu items disabled.
	
	public static final Color BG_COLOR_QC_FIELD = new Color(204,204,255); // Sun 4 //  @jve:decl-index=0:
	public static final Color BG_COLOR_ENT_FIELD = new Color(255,246,213);  //  @jve:decl-index=0:
	public static final Color BG_COLOR_ERROR = new Color(255,73,43); // highlight fields with data errors  //  @jve:decl-index=0:

	private int state = STATE_INIT;
	private ImageListBrowser ilb = null;
	private SpecimenBrowser slb = null;
	private UserListBrowser ulb = null;
	//private JPanel listBrowser = null;
	
	private JMenuBar jJMenuBar = null;
	private JMenu jMenuFile = null;
	private JMenuItem jMenuItemExit = null;
	private JMenu jMenuAction = null;
	private JMenu jMenuHelp = null;
	private JMenuItem jMenuItemAbout = null;
	private JMenuItem jMenuItemPreprocess = null;
	private JMenuItem jMenuItemLoadData = null;
	private JMenuItem jMenuItemVersion = null;
	private JMenuItem jMenuItemScanOneBarcode = null;
	private JMenuItem jMenuItemScanOneBarcodeSave = null;
	private JMenu jMenuConfig = null;
	private JMenu jMenuEdit = null;
	private JMenuItem jMenuItemPreferences = null;
	private JMenuItem jMenuItemCopy = null;
	private JMenuItem jMenuItemPaste = null;
	private JMenuItem jMenuItemLog = null;
	private JPanel jPanel = null;
	private JProgressBar jProgressBar = null;
	private JPanel jPanel1 = null;
	private JLabel jLabelStatus = null;
	private JMenuItem jMenuItemVerbatimTranscription = null;
	private JMenuItem jMenuItemVerbatimClassification = null;
	private JMenuItem jMenuItemBrowseSpecimens = null;
	private JPanel jPanelCenter = null;
	private JMenuItem jMenuItemEditTemplates = null;
	private JMenuItem jMenuItemBrowseImages = null;
	private JLabel jLabelCount = null;
	private JMenuItem jMenuItemPreprocessOneDir = null;
	private JMenu jMenuData = null;
	private JMenu jMenuQualityControl = null;
	private JMenuItem jMenuItemCheckForABarcode = null;
	private JMenuItem jMenuItemQCBarcodes = null;
	private JMenuItem jMenuItemSearch = null;
	private JMenuItem jMenuItemUsers = null;
	private JMenuItem jMenuItemLogout = null;
	private JMenuItem jMenuItemChangePassword = null;
	private JMenuItem jMenuItemCreateLabels = null;	
	private JMenuItem jMenuItemReconcileFiles = null;

	private JMenuItem jMenuItemStats = null;

	private JMenuItem jMenuItemRepeatOCR = null;

	private JMenuItem jMenuItemListRunningJobs = null;

	private JMenuItem jMenuItemRedoOCROne = null;
	private JMenuItem jMenuItemCleanupDirectory = null;
	private JMenuItem jMenuItemRecheckTemplates = null;
	private JMenuItem jMenuItemRecheckAllTemplates = null;
	
	public MainFrame() { 
        thisMainFrame = this;
		initialize();
		setState(STATE_INIT);
		this.pack();
		this.setVisible(true);
		// Set a reasonable font width for a relatively wide character
		Singleton.getSingletonInstance().setCharacterWidth(this.getFontMetrics(this.getFont()).getWidths()[109]);
    }
	
	public void setState(int aState) { 
		switch (aState) { 
		case STATE_INIT:
			// can't return to state_init.
			if (state==STATE_INIT) { 
				// do initial setup, disable most menus
				jMenuItemLog.setEnabled(false);
				jMenuEdit.setEnabled(false);
				jMenuAction.setEnabled(false);
				jMenuConfig.setEnabled(false);
				jMenuData.setEnabled(false);
				jMenuQualityControl.setEnabled(false);
				jMenuItemUsers.setEnabled(false);
				jMenuItemChangePassword.setEnabled(false);
				jMenuItemStats.setEnabled(false);
			}
			break;
		case STATE_RESET:
			// state when user logs out.
			jMenuEdit.setEnabled(true);
			jMenuHelp.setEnabled(true);
			jMenuItemLogout.setEnabled(true);
		    // disable all but edit/help menus
			jMenuItemLog.setEnabled(false);
			jMenuAction.setEnabled(false);
			jMenuConfig.setEnabled(false);
			jMenuData.setEnabled(false);
			jMenuQualityControl.setEnabled(false);
			jMenuItemUsers.setEnabled(false);
			jMenuItemChangePassword.setEnabled(false);
			// disable stats item on help menu
			jMenuItemStats.setEnabled(false);
			break;			
		case STATE_RUNNING:
			if (state==STATE_INIT) { 
				state=STATE_RUNNING;
				jMenuItemLog.setEnabled(true);
				jMenuEdit.setEnabled(true);
				activateMenuItemsByUser();
			}
			if (state==STATE_RUNNING) { 
				activateMenuItemsByUser();
			}
			break;
		}
	}

	/**
	 * Enable/disable menu items based on current user
	 * and their login state.  
	 */
	private void activateMenuItemsByUser() {
		// ***********************************************************************************************
		// ********* Important bit: This is where user rights are actually applied.     ******************
		// ********* Other than limits on editing/creating users, this only place where ******************
		// ********* the application level users and rights are controlled.             ******************
		// ***********************************************************************************************
		// Disable some menu items if user canceled login dialog.
		if (Singleton.getSingletonInstance().getUser()==null) {
			jMenuData.setEnabled(false);
			jMenuItemChangePassword.setEnabled(false);
			jMenuItemPreferences.setEnabled(false);
			jMenuItemPreprocess.setEnabled(false);
			jMenuItemLoadData.setEnabled(false);
			jMenuItemPreprocessOneDir.setEnabled(false);
			jMenuItemCreateLabels.setEnabled(false);
			jMenuItemStats.setEnabled(false);
			jMenuItemLog.setEnabled(false);
		} else { 
			// Anyone authenticated user can change their own password.
			jMenuConfig.setEnabled(true);
			jMenuItemChangePassword.setEnabled(true);
			// Set levels for data entry personnel.
			jMenuData.setEnabled(true);
			jMenuAction.setEnabled(false);
			jMenuItemUsers.setEnabled(false);
			jMenuItemPreprocess.setEnabled(false);
			jMenuItemLoadData.setEnabled(false);
			jMenuItemPreprocessOneDir.setEnabled(false);
			jMenuItemCreateLabels.setEnabled(true);
			jMenuItemPreferences.setEnabled(false);
			jMenuItemEditTemplates.setEnabled(false);
			jMenuQualityControl.setEnabled(true);
			jMenuItemCheckForABarcode.setEnabled(true);
			jMenuItemQCBarcodes.setEnabled(false);
			jMenuItemSearch.setEnabled(true);
			jMenuItemStats.setEnabled(true);
			jMenuItemLog.setEnabled(true);
			jMenuItemCleanupDirectory.setEnabled(false);
			jMenuItemRecheckTemplates.setEnabled(false);
			jMenuItemRecheckAllTemplates.setEnabled(false);
			try { 
				// Enable some menu items only for administrators.
				if (UsersLifeCycle.isUserAdministrator(Singleton.getSingletonInstance().getUser().getUserid())) { 
					//jMenuItemUsers.setEnabled(true);
					jMenuItemPreprocess.setEnabled(true);
				}
				// User privileges and some other items to the chief editor.  
				if (UsersLifeCycle.isUserChiefEditor(Singleton.getSingletonInstance().getUser().getUserid())) { 
					jMenuItemUsers.setEnabled(true);
					jMenuItemEditTemplates.setEnabled(true);
					jMenuItemLoadData.setEnabled(true);
					jMenuItemCleanupDirectory.setEnabled(true);
				}
				// Enable other menu items only for those with full access rights
				// Administrator and full roles both have full access rights
				if (Singleton.getSingletonInstance().getUser().isUserRole(Users.ROLE_FULL)) {
					jMenuAction.setEnabled(true);
					jMenuItemPreprocessOneDir.setEnabled(true);
					jMenuConfig.setEnabled(true);
					jMenuItemPreferences.setEnabled(true);
					jMenuQualityControl.setEnabled(true);
					jMenuItemQCBarcodes.setEnabled(true);
					jMenuItemRecheckTemplates.setEnabled(true);
					jMenuItemRecheckAllTemplates.setEnabled(true);
				}
			} catch (Exception e) { 
				// catch any problem with testing administration or user rights and do nothing.
			}			
		}

	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(1280, 750));
        this.setPreferredSize(new Dimension(1280, 800));
        this.setMinimumSize(new Dimension(300, 200));
		Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
	    this.setLocation((screenSize.width -   this.getWidth()) / 2 , 
	                     (screenSize.height - this.getHeight()) / 2 );
        //String iconFile = this.getClass().getResource("resources/icon.ico").getFile();
        URL iconFile = this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/icon.png");          
        try {  
        	setIconImage(new ImageIcon(iconFile).getImage());
        } catch (Exception e) { 
        	log.error("Can't open icon file: " + iconFile);
        	log.error(e);
        }
        this.setTitle(ImageCaptureApp.APP_NAME + ": MCZ Rapid Data Capture Application.  Configured For: " + 
		     Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION)
        );
        this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJPanel());
			
	}

	public void updateTitle() { 
        this.setTitle(ImageCaptureApp.APP_NAME + ": MCZ Rapid Data Capture Application.  Configured For: " + 
		     Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION)
        );
	}
	
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenuFile());
			jJMenuBar.add(getJMenuEdit());
			jJMenuBar.add(getJMenuAction());
			jJMenuBar.add(getJMenuData());
			jJMenuBar.add(getJMenuQualityControl());
			jJMenuBar.add(getJMenuConfig());
			jJMenuBar.add(getJMenuHelp());
		}
		return jJMenuBar;
	}
	/**
	 * This method initializes jMenuFile	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuFile() {
		if (jMenuFile == null) {
			jMenuFile = new JMenu();
			jMenuFile.setText("File");
			jMenuFile.setMnemonic(KeyEvent.VK_F);
			jMenuFile.add(getJMenuItemLog());
			jMenuFile.add(getJMenuItemLogout());
			jMenuFile.add(getJMenuItemExit());
		}
		return jMenuFile;
	}
	/**
	 * This method initializes jMenuItemExit	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemExit() {
		if (jMenuItemExit == null) {
			jMenuItemExit = new JMenuItem();
			jMenuItemExit.setText("Exit");
			jMenuItemExit.setMnemonic(KeyEvent.VK_E);
			jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					ImageCaptureApp.cleanUp();	
					System.out.println("Exit by user from main menu.");
					ImageCaptureApp.exit(ImageCaptureApp.EXIT_NORMAL);
				}
			});
		}
		return jMenuItemExit;
	}
	
	/**
	 * This method initializes jMenuItemLogout	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemLogout() {
		if (jMenuItemLogout == null) {
			jMenuItemLogout = new JMenuItem();
			jMenuItemLogout.setText("Change User");
			jMenuItemLogout.setMnemonic(KeyEvent.VK_U);
			jMenuItemLogout.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// remove the current user's browse (important if this is a userbrowse by an
					// administrator.
					jPanelCenter.removeAll();
					String oldUser = Singleton.getSingletonInstance().getUserFullName();
					setState(MainFrame.STATE_RESET);
					Singleton.getSingletonInstance().unsetCurrentUser();
					HibernateUtil.terminateSessionFactory();
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Logged out " + oldUser);
					// Force a login dialog by connecting to obtain record count;
					SpecimenLifeCycle sls = new SpecimenLifeCycle();
				    setCount(sls.findSpecimenCount());
				}
			});
		}
		return jMenuItemLogout;
	}	

	/**
	 * This method initializes jMenuAction	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuAction() {
		if (jMenuAction == null) {
			jMenuAction = new JMenu();
			jMenuAction.setText("Action");
			jMenuAction.setMnemonic(KeyEvent.VK_A);
			jMenuAction.add(getJMenuItemScanOneBarcodeSave());
			jMenuAction.add(getJMenuItemPreprocess());
			jMenuAction.add(getJMenuItemPreprocessOne());
			jMenuAction.add(getJMenuItemRedoOCROne());
			jMenuAction.add(getJMenuItemRepeatOCR());
			jMenuAction.add(getJMenuItemRecheckTemplates());
			jMenuAction.add(getJMenuItemRecheckAllTemplates());
			jMenuAction.add(getJMenuItemScanOneBarcode());
			jMenuAction.add(getJMenuItemCleanupDirectory());
			jMenuAction.add(getJMenuItemLoadData());
			jMenuAction.add(getJMenuItemListRunningJobs());
			jMenuAction.add(getJMenuItemCreateLabels());
		}
		
		return jMenuAction;
	}

	/**
	 * This method initializes jMenuHelp	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuHelp() {
		if (jMenuHelp == null) {
			jMenuHelp = new JMenu();
			jMenuHelp.setText("Help");
			jMenuHelp.setMnemonic(KeyEvent.VK_H);
			jMenuHelp.add(getJMenuItemAbout());
			jMenuHelp.addSeparator();
			jMenuHelp.add(getJMenuItem());
			jMenuHelp.add(getJMenuItemVersion());
		}
		return jMenuHelp;
	}

	/**
	 * This method initializes jMenuItemAbout	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemAbout() {
		if (jMenuItemAbout == null) {
			jMenuItemAbout = new JMenuItem();
			jMenuItemAbout.setText("About");
			jMenuItemAbout.setMnemonic(KeyEvent.VK_B);
			jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					AboutDialog a = new AboutDialog();
					a.pack();
					a.setVisible(true);
				}
			});
		}
		return jMenuItemAbout;
	}

	/**
	 * This method initializes jMenuItemPreprocess	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemPreprocess() {
		if (jMenuItemPreprocess == null) {
			jMenuItemPreprocess = new JMenuItem();
			jMenuItemPreprocess.setText("Preprocess All");
			jMenuItemPreprocess.setEnabled(true);
			try { 
				jMenuItemPreprocess.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/barcode_icon_16px.jpg")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemScanOneBarcode.");
				log.error(e.getLocalizedMessage());
			}
			jMenuItemPreprocess.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int result = JOptionPane.showConfirmDialog(Singleton.getSingletonInstance().getMainFrame(), "Are you sure, this will check all image files and may take some time.", "Preprocess All?", JOptionPane.YES_NO_OPTION);
					if (result==JOptionPane.YES_OPTION) { 
					    JobAllImageFilesScan scan = new JobAllImageFilesScan();
					    (new Thread(scan)).start();
					} else {
						Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Preprocess canceled.");
					}
				}
			});
		}
		return jMenuItemPreprocess;
	}
	
	private JMenuItem getJMenuItemLoadData() {
		if (jMenuItemLoadData == null) {
			jMenuItemLoadData = new JMenuItem();
			jMenuItemLoadData.setText("Load Data");
			try { 
				jMenuItemLoadData.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/cycle_icon_16px.jpg")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemRecheckAllTemplates.");
				log.error(e);
			}			
			jMenuItemLoadData.setEnabled(true);
			jMenuItemLoadData.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 JobVerbatimFieldLoad scan = new JobVerbatimFieldLoad();
					 (new Thread(scan)).start();
				}
			});
		}
		return jMenuItemLoadData;
	}	

	/**
	 * This method initializes jMenuItemVersion	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemVersion() {
		if (jMenuItemVersion == null) {
			jMenuItemVersion = new JMenuItem(ImageCaptureApp.APP_NAME + " Ver: " + ImageCaptureApp.APP_VERSION);
			jMenuItemVersion.setEnabled(false);
		}
		return jMenuItemVersion;
	}

	/**
	 * This method initializes jMenuItemScanOneBarcode	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemScanOneBarcode() {
		if (jMenuItemScanOneBarcode == null) {
			jMenuItemScanOneBarcode = new JMenuItem();
			jMenuItemScanOneBarcode.setText("Scan A File For Barcodes");
			jMenuItemScanOneBarcode.setMnemonic(KeyEvent.VK_S);
			try { 
			    jMenuItemScanOneBarcode.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/barcode_icon_16px.jpg")));
			} catch (Exception e) { 
				System.out.println("Can't open icon file for jMenuItemScanOneBarcode.");
				System.out.println(e.getLocalizedMessage());
			}
			jMenuItemScanOneBarcode.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {			
					JobSingleBarcodeScan scan = new JobSingleBarcodeScan(false);
					scan.registerListener((RunnerListener)thisMainFrame);
					(new Thread(scan)).start();
				}
			});
		}
		return jMenuItemScanOneBarcode;
	}
	
	/**
	 * This method initializes jMenuItemScanOneBarcodeSave	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemScanOneBarcodeSave() {
		if (jMenuItemScanOneBarcodeSave == null) {
			jMenuItemScanOneBarcodeSave = new JMenuItem();
			jMenuItemScanOneBarcodeSave.setMnemonic(KeyEvent.VK_D);
			jMenuItemScanOneBarcodeSave.setText("Database One File");
			try { 
			    jMenuItemScanOneBarcodeSave.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/barcode_icon_16px.jpg")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemScanOneBarcode.");
				log.error(e.getLocalizedMessage());
			}
			jMenuItemScanOneBarcodeSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobSingleBarcodeScan scan = new JobSingleBarcodeScan(true);
					(new Thread(scan)).start();
				}
			});
		}
		return jMenuItemScanOneBarcodeSave;
	}

	/**
	 * This method initializes jMenuConfig	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuConfig() {
		if (jMenuConfig == null) {
			jMenuConfig = new JMenu();
			jMenuConfig.setText("Configuration");
			jMenuConfig.setMnemonic(KeyEvent.VK_C);
			jMenuConfig.setEnabled(true);
			jMenuConfig.add(getJMenuItemEditTemplates());
			jMenuConfig.add(getJMenuItemPreferences());
			jMenuConfig.add(getJMenuItemUsers());
			jMenuConfig.add(getJMenuItemChangePassword());
		}
		return jMenuConfig;
	}

	/**
	 * @return
	 */
	private JMenuItem getJMenuItemUsers() {
		if (jMenuItemUsers==null) { 
			jMenuItemUsers = new JMenuItem();
			jMenuItemUsers.setText("Users");
			jMenuItemUsers.setMnemonic(KeyEvent.VK_U);
			jMenuItemUsers.setEnabled(false);
			try { 
				jMenuItemUsers.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/people_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemUsers.");
				log.error(e);
			}
			jMenuItemUsers.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					ulb = new UserListBrowser();
					if (slb!=null) { jPanelCenter.remove(slb); } 
					if (ilb!=null) { jPanelCenter.remove(ilb); } 
					jPanelCenter.removeAll();
					jPanelCenter.add(ulb, BorderLayout.CENTER);
					jPanelCenter.revalidate();
					jPanelCenter.repaint();
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
		}
		return jMenuItemUsers;
	}

	/**
	 * This method initializes jMenuEdit	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuEdit() {
		if (jMenuEdit == null) {
			jMenuEdit = new JMenu();
			jMenuEdit.setText("Edit");
			jMenuEdit.setMnemonic(KeyEvent.VK_E);
			jMenuEdit.add(getJMenuItemCopy());
			jMenuEdit.add(getJMenuItemPaste());
		}
		return jMenuEdit;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemPreferences() {
		if (jMenuItemPreferences == null) {
			jMenuItemPreferences = new JMenuItem();
			jMenuItemPreferences.setText("Preferences");
			jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PropertiesEditor p = new PropertiesEditor();
					p.pack();
					p.setVisible(true);
				}
			});
		}
		return jMenuItemPreferences;
	}

	/**
	 * This method initializes jMenuItem1	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemCopy() {
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
	 * This method initializes jMenuItem2	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemPaste() {
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

	/**
	 * This method initializes jMenuItemLog	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemLog() {
		if (jMenuItemLog == null) {
			jMenuItemLog = new JMenuItem();
			jMenuItemLog.setText("View History");
			jMenuItemLog.setMnemonic(KeyEvent.VK_H);
			jMenuItemLog.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					EventLogFrame logWindow = new EventLogFrame();
					logWindow.pack();
					logWindow.setVisible(true);
					System.gc();
				}
			});
		}
		return jMenuItemLog;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJPanel1(), BorderLayout.SOUTH);
			jPanel.add(getJPanelCenter(), BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 2.0;
			gridBagConstraints1.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.gridy = 0;
			jLabelStatus = new JLabel();
			jLabelStatus.setText("Status:");
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(jLabelStatus, gridBagConstraints1);
			jPanel1.add(getJProgressBar(), gridBagConstraints);
		}
		return jPanel1;
	}


	@Override
	public void notifyListener(int anEvent, RunnableJob notifyingJob) {
		jProgressBar.setValue(notifyingJob.percentComplete());
        log.debug(notifyingJob.percentComplete());		
		jProgressBar.validate();
	}
	
	/**
	 * Sets the message on the status bar with an up to 30 character string.
	 * 
	 * @param aMessage the message to display on the status bar.
	 */
	public void setStatusMessage(String aMessage) { 
		int maxLength = 30;
		if (aMessage.length()<maxLength) { maxLength = aMessage.length(); } 
		jLabelStatus.setText("Status: " + aMessage.substring(0, maxLength));
	}

	public void setSpecimenBrowseList(Specimen searchCriteria) { 
		Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		slb = new SpecimenBrowser(searchCriteria, true);
		if (ilb!=null) { jPanelCenter.remove(ilb); }
		if (ulb!=null) { jPanelCenter.remove(ulb); }
		jPanelCenter.removeAll();
		jPanelCenter.add(slb, BorderLayout.CENTER);
		jPanelCenter.revalidate();
		jPanelCenter.repaint();
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_ENABLE_BROWSE).equals("false")) { 
		    jMenuItemBrowseSpecimens.setEnabled(false);
		    jMenuItemBrowseImages.setEnabled(false);
		} else { 
		    jMenuItemBrowseSpecimens.setEnabled(true);
		    jMenuItemBrowseImages.setEnabled(true);
		}
        setStatusMessage("Found " + slb.getRowCount() + " matching specimens");
		Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * This method initializes jMenuItemBrowseSpecimens	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemBrowseSpecimens() {
		if (jMenuItemBrowseSpecimens == null) {
			jMenuItemBrowseSpecimens = new JMenuItem();
			jMenuItemBrowseSpecimens.setText("Browse Specimens");
			jMenuItemBrowseSpecimens.setMnemonic(KeyEvent.VK_B);
			try { 
				jMenuItemBrowseSpecimens.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/butterfly_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemBrowseImages.");
				log.error(e);
			}
			jMenuItemBrowseSpecimens.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Create a SpecimenBrowser jpanel and replace the 
					// the content of the center of jPanelCenter with it.
					//TODO: extend beyond switching between ilb and slb.
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					slb = new SpecimenBrowser();
					if (ilb!=null) { jPanelCenter.remove(ilb); }
					if (ulb!=null) { jPanelCenter.remove(ulb); }
					jPanelCenter.removeAll();
					jPanelCenter.add(slb, BorderLayout.CENTER);
					jPanelCenter.revalidate();
					jPanelCenter.repaint();
					if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_ENABLE_BROWSE).equals("false")) { 
					    jMenuItemBrowseSpecimens.setEnabled(true);
					    jMenuItemBrowseImages.setEnabled(true);
					} else { 
					    jMenuItemBrowseSpecimens.setEnabled(true);
					    jMenuItemBrowseImages.setEnabled(true);
					}
					setStatusMessage("Found " + slb.getRowCount() + " specimens");
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					System.gc();
				}
			});
		}
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_ENABLE_BROWSE).equals("false")) { 
			jMenuItemBrowseSpecimens.setEnabled(false);
		}
		return jMenuItemBrowseSpecimens;
	}

	/**
	 * This method initializes jPanelCenter	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelCenter() {
		if (jPanelCenter == null) {
			jLabelCount = new JLabel();
			jLabelCount.setText("");
			jPanelCenter = new JPanel();
			jPanelCenter.setLayout(new BorderLayout());
			jPanelCenter.add(jLabelCount, BorderLayout.SOUTH);
		}
		return jPanelCenter;
	}

	public void setCount(String recordCountText) { 
		if (jLabelCount!=null) { 
			jLabelCount.setText(recordCountText);
		}
	}
	
	/**
	 * This method initializes jMenuItem3	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemEditTemplates() {
		if (jMenuItemEditTemplates == null) {
			jMenuItemEditTemplates = new JMenuItem();
			jMenuItemEditTemplates.setText("Edit Templates");
			jMenuItemEditTemplates.setMnemonic(KeyEvent.VK_T);
			jMenuItemEditTemplates.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PositionTemplateEditor templateEditor = new PositionTemplateEditor();
					templateEditor.pack();
					templateEditor.setVisible(true);
				}
			});
		}
		return jMenuItemEditTemplates;
	}

	/**
	 * This method initializes jMenuItemBrowseImages	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemBrowseImages() {
		if (jMenuItemBrowseImages == null) {
			jMenuItemBrowseImages = new JMenuItem();
			jMenuItemBrowseImages.setText("Browse Image Files");
			jMenuItemBrowseImages.setMnemonic(KeyEvent.VK_I);
			try { 
				jMenuItemBrowseImages.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/image_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemBrowseImages.");
				log.error(e);
			}
			jMenuItemBrowseImages.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// Create a ImageListBrowser jpanel and replace the 
					// the content of the center of jPanelCenter with it.
					//TODO: extend beyond switching between ilb and slb.
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					ilb = new ImageListBrowser();
					if (slb!=null) { jPanelCenter.remove(slb); }
					if (ulb!=null) { jPanelCenter.remove(ulb); }
					jPanelCenter.removeAll();
					jPanelCenter.add(ilb, BorderLayout.CENTER);
					jPanelCenter.revalidate();
					jPanelCenter.repaint();
					if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_ENABLE_BROWSE).equals("false")) { 
					    jMenuItemBrowseSpecimens.setEnabled(false);
					    jMenuItemBrowseImages.setEnabled(false);
					} else { 
					    jMenuItemBrowseSpecimens.setEnabled(true);
					    jMenuItemBrowseImages.setEnabled(true);
					}
					setStatusMessage("Found " + ilb.getRowCount() + " images.");
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					System.gc();
				}
			});
		}
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_ENABLE_BROWSE).equals("false")) { 
			jMenuItemBrowseImages.setEnabled(false);
		}
		return jMenuItemBrowseImages;
	}

	/**
	 * This method initializes jMenuItemPreprocessOneDir	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemPreprocessOne() {
		if (jMenuItemPreprocessOneDir == null) {
			jMenuItemPreprocessOneDir = new JMenuItem();
			jMenuItemPreprocessOneDir.setText("Preprocess A Directory");
			jMenuItemPreprocessOneDir.setMnemonic(KeyEvent.VK_P);
			jMenuItemPreprocessOneDir.setEnabled(true);
			try { 
				jMenuItemPreprocessOneDir.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/barcode_icon_16px.jpg")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemScanOneBarcode.");
				log.error(e.getLocalizedMessage());
			}			
			jMenuItemPreprocessOneDir.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobAllImageFilesScan scan = new JobAllImageFilesScan(
							JobAllImageFilesScan.SCAN_SELECT,
							new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE))
							);
					(new Thread(scan)).start();
				}
			});
		}
		return jMenuItemPreprocessOneDir;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuData() {
		if (jMenuData == null) {
			jMenuData = new JMenu();
			jMenuData.setText("Data");
			jMenuData.setMnemonic(KeyEvent.VK_D);
			jMenuData.add(getJMenuItemSearch());
			jMenuData.add(getJMenuItemVerbatimTranscription());
			jMenuData.add(getJMenuItemVerbatimClassification());
			jMenuData.add(getJMenuItemBrowseImages());
			jMenuData.add(getJMenuItemBrowseSpecimens());
		}
		return jMenuData;
	}

	/**
	 * This method initializes jMenu1	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuQualityControl() {
		if (jMenuQualityControl == null) {
			jMenuQualityControl = new JMenu();
			jMenuQualityControl.setText("QualityControl");
			jMenuQualityControl.setMnemonic(KeyEvent.VK_Q);
			jMenuQualityControl.add(getJMenuItemValidateImageNoDB());
			jMenuQualityControl.add(getJMenuItemQCBarcodes());
			jMenuQualityControl.add(getJMenuItemReconcileFiles());
		}
		return jMenuQualityControl;
	}

	private JMenuItem getJMenuItemVerbatimTranscription() {
		if (jMenuItemVerbatimTranscription == null) {
			jMenuItemVerbatimTranscription = new JMenuItem();
			jMenuItemVerbatimTranscription.setText("Verbatim Transcription");
			jMenuItemVerbatimTranscription.setEnabled(true);
			jMenuItemVerbatimTranscription.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					VerbatimToTranscribeDialog s = new VerbatimToTranscribeDialog();
					s.setVisible(true);
				}
			});
		}
		return jMenuItemVerbatimTranscription;
	}	
	
	private JMenuItem getJMenuItemVerbatimClassification() {
		if (jMenuItemVerbatimClassification == null) {
			jMenuItemVerbatimClassification = new JMenuItem();
			jMenuItemVerbatimClassification.setText("Fill in from Verbatim");
			jMenuItemVerbatimClassification.setEnabled(true);
			jMenuItemVerbatimClassification.addActionListener(new ActionListener(){
				public void actionPerformed(java.awt.event.ActionEvent e) {
					VerbatimListDialog s = new VerbatimListDialog();
					s.setVisible(true);
				}
			});
		}
		return jMenuItemVerbatimClassification;
	}	
	
	/**
	 * This method initializes jMenuItem4	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemValidateImageNoDB() {
		if (jMenuItemCheckForABarcode == null) {
			jMenuItemCheckForABarcode = new JMenuItem();
			jMenuItemCheckForABarcode.setText("Check for Barcodes in Image File");
			jMenuItemCheckForABarcode.setMnemonic(KeyEvent.VK_C);
			try { 
				jMenuItemCheckForABarcode.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/barcode_icon_16px.jpg")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemCheckForABarcode.");
				log.error(e);
			}
			jMenuItemCheckForABarcode.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobSingleBarcodeScan scan = new JobSingleBarcodeScan(false);
					scan.registerListener((RunnerListener)thisMainFrame);
					(new Thread(scan)).start();
				}
			});			
		}
		return jMenuItemCheckForABarcode;
	}
	

	private JMenuItem getJMenuItemReconcileFiles() { 
		if (jMenuItemReconcileFiles == null) { 
			jMenuItemReconcileFiles = new JMenuItem();
			jMenuItemReconcileFiles.setText("Reconcile image files with database");
			jMenuItemReconcileFiles.setMnemonic(KeyEvent.VK_R);
			jMenuItemReconcileFiles.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobFileReconciliation r = new JobFileReconciliation();
					(new Thread(r)).start();
				}
			});
		}
		return jMenuItemReconcileFiles;
 	}

	/**
	 * This method initializes jMenuItem5	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemQCBarcodes() {
		if (jMenuItemQCBarcodes == null) {
			jMenuItemQCBarcodes = new JMenuItem();
			jMenuItemQCBarcodes.setText("QC Barcodes");
			jMenuItemQCBarcodes.setMnemonic(KeyEvent.VK_B);
			jMenuItemQCBarcodes.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Running barcode QC checks");
					String[] missingBarcodes = SpecimenLifeCycle.getMissingBarcodes();
					ilb = new ImageListBrowser(true);
					if (slb!=null) { jPanelCenter.remove(slb); } 
					if (ulb!=null) { jPanelCenter.remove(ulb); }
					jPanelCenter.removeAll();
					jPanelCenter.add(ilb, BorderLayout.CENTER);
					jPanelCenter.revalidate();
					jPanelCenter.repaint();
					Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					log.debug(missingBarcodes.length);
					if (missingBarcodes.length>0) {
						Counter errorCount = new Counter();
						for (int i=0; i<missingBarcodes.length; i++) { 
							BarcodeBuilder builder = Singleton.getSingletonInstance().getBarcodeBuilder();
							BarcodeMatcher matcher = Singleton.getSingletonInstance().getBarcodeMatcher();
							String previous = builder.makeFromNumber(matcher.extractNumber(missingBarcodes[i]) - 1);							
							String previousFile = ""; 
							String previousPath = "";
							SpecimenLifeCycle sls = new SpecimenLifeCycle();
							Specimen imagePattern = new Specimen();
							imagePattern.setBarcode(previous);
							List<Specimen> result = sls.findByExample(imagePattern);
							if (result!=null && (!result.isEmpty())) {
								Set<ICImage> images = result.get(0).getICImages();

								if (images!=null && (!images.isEmpty()) ){
									Iterator<ICImage> it = images.iterator();
									if (it.hasNext()) { 
										ICImage image = it.next();
										previousFile = image.getFilename();
										previousPath = image.getPath();
									}
								}
							} 
							RunnableJobError err = new RunnableJobError(previousFile, missingBarcodes[i], previousPath, "", "Barcode not found", null, null, null, 
									RunnableJobError.TYPE_BARCODE_MISSING_FROM_SEQUENCE,
									previousFile,
									previousPath);
							errorCount.appendError(err);
						}
						String report = "There are at least " + missingBarcodes.length + " barcodes missing from the sequence.\nMissing numbers are shown below.\nIf two or more numbers are missing in sequence, only the first will be listed here.\n\nFiles with mismmatched barcodes are shown in main window.\n";
						RunnableJobReportDialog errorReportDialog = new RunnableJobReportDialog(
								Singleton.getSingletonInstance().getMainFrame(),
								report, 
								errorCount.getErrors(), 
								RunnableJobErrorTableModel.TYPE_MISSING_BARCODES,
								"QC Barcodes Report");
						errorReportDialog.setVisible(true);
					} else { 
						JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "No barcodes are missing from the sequence.\nAny missmatches are shown in table.", "Barcode QC Report", JOptionPane.OK_OPTION);	
					}
					System.gc();
				}
			});
		}
		return jMenuItemQCBarcodes;
	}

	/**
	 * This method initializes jMenuItem6	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemSearch() {
		if (jMenuItemSearch == null) {
			jMenuItemSearch = new JMenuItem();
			jMenuItemSearch.setText("Search");
			jMenuItemSearch.setEnabled(true);
			jMenuItemSearch.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SearchDialog s = new SearchDialog(thisMainFrame);
					s.setVisible(true);
				}
			});
		}
		return jMenuItemSearch;
	}

	/**
	 * This method initializes jMenuItemChangePassword	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemChangePassword() {
		if (jMenuItemChangePassword == null) {
			jMenuItemChangePassword = new JMenuItem();
			jMenuItemChangePassword.setText("Change My Password");
			jMenuItemChangePassword.setMnemonic(KeyEvent.VK_M);
			try { 
				jMenuItemChangePassword.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/key_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemChangePassword.");
				log.error(e);
			}
			jMenuItemChangePassword.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (Singleton.getSingletonInstance().getUser()!=null) { 
						ChangePasswordDialog cpd = new ChangePasswordDialog(thisMainFrame, Singleton.getSingletonInstance().getUser());
						cpd.setVisible(true);
					}
				}
			});
		}
		return jMenuItemChangePassword;
	}

	/**
	 * This method initializes jMenuItemCreateLabels	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemCreateLabels() {
		if (jMenuItemCreateLabels == null) {
			jMenuItemCreateLabels = new JMenuItem();
			jMenuItemCreateLabels.setText("Create Unit Tray Labels");
			jMenuItemCreateLabels.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					UnitTrayLabelBrowser lb = new UnitTrayLabelBrowser();
					lb.pack();
					lb.setVisible(true);
				}
			});
		}
		return jMenuItemCreateLabels;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItem() {
		if (jMenuItemStats == null) {
			jMenuItemStats = new JMenuItem();
			jMenuItemStats.setText("Statistics");
			jMenuItemStats.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SpecimenLifeCycle sls = new SpecimenLifeCycle();
					JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
							sls.findSpecimenCount(),
							"Record counts", 
					JOptionPane.INFORMATION_MESSAGE);
					
				}
			});
		}
		return jMenuItemStats;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemRepeatOCR() {
		if (jMenuItemRepeatOCR == null) {
			jMenuItemRepeatOCR = new JMenuItem();
			jMenuItemRepeatOCR.setText("Redo OCR for All");
			try { 
				jMenuItemRepeatOCR.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/reload_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemRepeatOCR.");
				log.error(e);
			}
			jMenuItemRepeatOCR.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobRepeatOCR r = new JobRepeatOCR();
					(new Thread(r)).start();
				}
			});
		}
		return jMenuItemRepeatOCR;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemListRunningJobs() {
		if (jMenuItemListRunningJobs == null) {
			jMenuItemListRunningJobs = new JMenuItem();
			jMenuItemListRunningJobs.setText("List Running Jobs");
			jMenuItemListRunningJobs.setMnemonic(KeyEvent.VK_L);
			try { 
				jMenuItemListRunningJobs.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/tools_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemListRunningJobs.");
				log.error(e);
			}
			jMenuItemListRunningJobs.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					RunnableJobFrame jobFrame = new RunnableJobFrame();
					jobFrame.setVisible(true);
				}
			});
		}
		return jMenuItemListRunningJobs;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemRedoOCROne() {
		if (jMenuItemRedoOCROne == null) {
			jMenuItemRedoOCROne = new JMenuItem();
			jMenuItemRedoOCROne.setText("Redo OCR for A Directory");
			jMenuItemRedoOCROne.setMnemonic(KeyEvent.VK_R);
			try { 
				jMenuItemRedoOCROne.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/reload_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemRedoOCROne.");
				log.error(e);
			}
			jMenuItemRedoOCROne.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File target = new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE));
					JobRepeatOCR r = new JobRepeatOCR(JobRepeatOCR.SCAN_SELECT, target);
					(new Thread(r)).start();
				}
			});
		}
		return jMenuItemRedoOCROne;
	}
	
	private JMenuItem getJMenuItemCleanupDirectory() {
		if (jMenuItemCleanupDirectory == null) {
			jMenuItemCleanupDirectory = new JMenuItem();
			jMenuItemCleanupDirectory.setText("Cleanup Deleted Images");
			try { 
				jMenuItemCleanupDirectory.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/bb_trsh_icon_16px.png")));
			} catch (Exception e) { 
				System.out.println("Can't open icon file for jMenuItemCleanupDirectory.");
				System.out.println(e.getLocalizedMessage());
			}			
			jMenuItemCleanupDirectory.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobCleanDirectory r = new JobCleanDirectory(
							JobCleanDirectory.SCAN_SELECT,
							new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE))
					        );
					(new Thread(r)).start();
				}
			});
		}
		return jMenuItemCleanupDirectory;
	}	
	
	private JMenuItem getJMenuItemRecheckTemplates() {
		if (jMenuItemRecheckTemplates == null) {
			jMenuItemRecheckTemplates = new JMenuItem();
			jMenuItemRecheckTemplates.setText("Recheck cases of WholeImageOnly");
			jMenuItemRecheckTemplates.setMnemonic(KeyEvent.VK_W);
			try { 
				jMenuItemRecheckTemplates.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/reload_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemRecheckTemplates.");
				log.error(e);
			}			
			jMenuItemRecheckTemplates.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobRecheckForTemplates r = new JobRecheckForTemplates(
							JobRecheckForTemplates.SCAN_SELECT,
							new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE))
					        );
					(new Thread(r)).start();
				}
			});
		}
		return jMenuItemRecheckTemplates;
	}		
	
	private JMenuItem getJMenuItemRecheckAllTemplates() {
		if (jMenuItemRecheckAllTemplates == null) {
			jMenuItemRecheckAllTemplates = new JMenuItem();
			jMenuItemRecheckAllTemplates.setText("Recheck All cases of WholeImageOnly");
			try { 
				jMenuItemRecheckAllTemplates.setIcon(new ImageIcon(this.getClass().getResource("/edu/harvard/mcz/imagecapture/resources/reload_icon_16px.png")));
			} catch (Exception e) { 
				log.error("Can't open icon file for jMenuItemRecheckAllTemplates.");
				log.error(e);
			}			
			jMenuItemRecheckAllTemplates.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JobRecheckForTemplates r = new JobRecheckForTemplates();
					(new Thread(r)).start();
				}
			});
		}
		return jMenuItemRecheckAllTemplates;
	}		
	
}  //  @jve:decl-index=0:visual-constraint="21,12"
