/**
 * Singleton.java
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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.Users;
import edu.harvard.mcz.imagecapture.data.UsersLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchValueException;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeMatcher;


/** Thread safe singleton object for imagecapture application.
 * <BR>
 * Usage:
 * <pre> 
      Singleton.getSingletonInstance.set{Method}(aRelevantObject) // store single instance to singleton.
      Singleton.getSingletonInstance.get{Method}().doStuffWithObject() // retrieve single instance from singleton.
   </pre>
 * 
 * @author Paul J. Morris
 *
 */
public class Singleton {
	
	private static final Log log = LogFactory.getLog(Singleton.class);
	
	// Eagerly create for thread safety.
	private static Singleton singletonInstance = new Singleton();
	
	private  ImageCaptureProperties properties = null;
	private  MainFrame mainFrame;
	private  String username;
	private  String userFullName;
	private  Users user = null;
	private  BarcodeMatcher barcodeMatchedr = null;
	private  BarcodeBuilder barcodeBuilder = null;
	private  RunnableJobTableModel jobList = null;
	private  int characterWidth = 10;

	/** Private constructor to prevent the creation
	 * of multiple instances of Singleton objects.  
	 */
	private Singleton() { 
	}

	/** Use this method to access the Singleton.
	 * 
	 * @return the sole Singleton instance.
	 */
	public static Singleton getSingletonInstance() { 
		return singletonInstance;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	/** Store a single instance of a MainFrame to be referenced from 
	 * elsewhere in the program.  
	 * 
	 * @param aMainFrame sole instance of MainFrame to be referenced  
	 */
	public void setMainFrame(MainFrame aMainFrame) {
		mainFrame = aMainFrame;
	}
	
	public  ImageCaptureProperties getProperties() { 
		if (properties==null) { 
			// load a default properties if we haven't been given one yet.
			properties = new ImageCaptureProperties();
		}
		return properties;
	}
	
	public  void setProperties(ImageCaptureProperties anImageCaptureProperties) {
		properties = anImageCaptureProperties;
	}
	
	/**
	 * Store the db username (for the currently authenticated user)
	 * and look up and store the fullname that goes with this username.
	 * A call on this method will make appropriate values available for 
	 * both getCurrentUsername() and getUserFullName().  
	 * 
	 * @param currentUsername
	 */
	public void setCurrentUsername(String currentUsername) {
		user = null;
		userFullName = null;
		username = null;
		UsersLifeCycle u = new UsersLifeCycle();
		try {
			// Look up in the database the name of the person that
			// goes with this username.
			String tempUserFullName = u.getFullNameForUserName(currentUsername);
			Users pattern = new Users();
			pattern.setFullname(tempUserFullName);
			pattern.setUsername(currentUsername);
			try { 
				ArrayList<Users> matches =  (ArrayList<Users>) u.findByExample(pattern); 
				if (matches.size()==1) { 
					user = (Users) matches.get(0);
					userFullName = u.getFullNameForUserName(currentUsername);
					username = currentUsername;
				}
			} catch (Exception e) { 
				// shouldn't end up here.
				log.error(e);
				user = null;
				userFullName = null;
				username = null;
			}
		} catch (NoSuchValueException e) {
			// No such username was found.
			
			//TODO: Redo this now that usernames are fake.  
			
			// Most likely case is we need to create a new record
			// in the Users table and set Users.fullname, as this
			// is a new user that has been added to the DBMS, but not
			// to the project's Users (username-fullname mapping) table.
			boolean failed = true;
			String aFullName = ""; 
			while (failed && aFullName.equals("")) { 
				Users newUser = new Users();
				newUser.setUsername(username);
				UserDialog userDialog = new UserDialog(newUser);
				userDialog.pack();
				userDialog.setVisible(true);
				aFullName = userDialog.getUser().getFullname();
				failed =  userDialog.getWasCancled();
				userDialog.dispose();
			} 
			userFullName = aFullName;
			// TODO: Make this bombproof.  
		}
	}
	
	/**
	 * 
	 * @return the db username of what should be the currently 
	 * authenticated user.  
	 * 
	 */
	public String getCurrentUsername() {
		return username;
	}

	/**
	 * Note that there is no setUserFullName() method, the singleton
	 * value of userFullName is set automatically through a call to 
	 * setCurrentUsername().
	 * 
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}

	/** Note that there is no setUser() method.  The value is set automatically
	 * through a call to setCurrentUsername();
	 *  
	 * @return the current user
	 */
	public Users getUser() {
		return user;
	}

	public void unsetCurrentUser() {
		user = null;
		username = null;
		userFullName = null;
	}

	/**
	 * @return the barcodeMatchedr
	 */
	public BarcodeMatcher getBarcodeMatcher() {
		return barcodeMatchedr;
	}

	/**
	 * @param barcodeMatcher the barcodeMatchedr to set
	 */
	public void setBarcodeMatcher(BarcodeMatcher barcodeMatcher) {
		this.barcodeMatchedr = barcodeMatcher;
	}

	/**
	 * @return the barcodeBuilder
	 */
	public BarcodeBuilder getBarcodeBuilder() {
		return barcodeBuilder;
	}

	/**
	 * @param barcodeBuilder the barcodeBuilder to set
	 */
	public void setBarcodeBuilder(BarcodeBuilder barcodeBuilder) {
		this.barcodeBuilder = barcodeBuilder;
	}

	public void setJobList(RunnableJobTableModel jobList) {
		this.jobList = jobList;
	}

	public RunnableJobTableModel getJobList() {
		return jobList;
	}

	public void setCharacterWidth(int characterWidth) {
		if (characterWidth > 8) { 
		    this.characterWidth = characterWidth;
		} else { 
			this.characterWidth = 8;
		}
	}

	public int getCharacterWidth() {
		return characterWidth;
	}
	
}
