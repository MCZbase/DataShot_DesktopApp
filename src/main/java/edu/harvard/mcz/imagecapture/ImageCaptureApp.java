/**
 * ImageCaptureApp.java
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
 * 
 * File last changed on $Date: 2012-01-06 18:51:55 -0500 (Fri, 06 Jan 2012) $ by $Author: mole $ in $Rev$.
 * $Id: ImageCaptureApp.java 305 2012-01-06 23:51:55Z mole $
 */
package edu.harvard.mcz.imagecapture;

import java.awt.Cursor;

/** for experimental chat support
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
*/
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeMatcher;

/**Main entry point for user interface of ImageCapture Java Application.
 * Creates a SingletonObject, loads the properties file, and opens a MainFrame
 * 
 * @see edu.harvard.mcz.imagecapture.MainFrame
 * @see edu.harvard.mcz.imagecapture.Singleton
 * @see edu.harvard.mcz.imagecapture.ImageCaptureProperties
 * 
 * @author Paul J. Morris
 *
 */
public class ImageCaptureApp {
	
	private static final Log log = LogFactory.getLog(ImageCaptureApp.class);
	
	public static final String APP_VERSION = "1.2.3-SNAPSHOT";
	public static final String APP_NAME = "RapidCapture";
	public static final String APP_DESCRIPTION = "Rapid capture of data from images of pin Labels and pinned insect \nspecimens developed for the MCZ Lepidoptera collection";
	public static final String APP_COPYRIGHT = "Copyright © 2009-2016 President and Fellows of Harvard College";
	public static final String APP_LICENSE = "This program is free software; you can redistribute it and/or modify \n " + 
    "it under the terms of Version 2 of the GNU General Public License \n" +
    "as published by the Free Software Foundation" +
    " \n " + 
    "This program is distributed in the hope that it will be useful,\n " + 
    "but WITHOUT ANY WARRANTY; without even the implied warranty of\n " + 
    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n " + 
    "GNU General Public License for more details.\n " + 
    "\n " + 
    "You should have received a copy of the GNU General Public License along\n " + 
    "with this program; if not, write to the Free Software Foundation, Inc.,\n " + 
    "51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.\n ";
    public static final String APP_CONTRIBUTORS = "Design: Brendan Haley, Linda Ford, Rodney Eastwood, Paul J. Morris.  Code: Paul J. Morris";
    public static final String APP_LIBRARIES = "Hibernate, Tesseract, ZXing, Log4J, drew.metadata.exif, iText";
    public static final String APP_REV = "$Rev$";  // ImageCapture.jar file built before commit will be one revision behind latest commit with changes to this file. 
    
    /** 
     * Use MCZEntBarcode class instead.
     *  
     */
    //public static final String REGEX_BARCODE = "^MCZ-ENT[0-9]{8}$";
    
    /**
     * Default regular expression for recognizing drawer numbers and unit tray numbers,
     * used to set default value of property ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER, use 
     * that property instead of this hard coded constant.
     * 
     * @see edu.harvard.mec.imagecapture.ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER 
     */
    public static final String REGEX_DRAWERNUMBER = "[0-9]{3}\\Q.\\E[0-9]+";
    /**
     * Default regular expression for recognizing image filenames in pattern decided on for project.  
     * Used to set the default value of property ImageCaptureProperties.KEY_IMAGEREGEX, use that 
     * property instead of this hard coded constant.
     * 
     * @see edu.harvard.mcz.imagecapture.ImageCaptureProperties.KEY_IMAGEREGEX
     */
    public static final String REGEX_IMAGEFILE = "^IMG_[0-9]{6}\\.JPG$";
    
    // ^([12][0-9]{3}((/[01][0-9]){1}(/[0-3][0-9])?)?)?\-?([12][0-9]{3}((/[01][0-9]){1}(/[0-3][0-9])?)?)??$
    
    /**
     * Match blank, or year or year/month or year/month/day.
     */
    public static final String REGEX_DATE = "^([12][0-9]{3}((/[01][0-9]){1}(/[0-3][0-9])?)?)?(\\-([12][0-9]{3}((/[01][0-9]){1}(/[0-3][0-9])?)?)?)??$";
      
    /**
     * Code for a normal exit, pass to ImageCaptureApp.exit(EXIT_NORMAL).
     */
    public static final int EXIT_NORMAL = 0;
    /**
     * Error code for an exit after a fatal error. 
     * Pass to ImageCaptureApp.exit(EXIT_ERROR);
     */
    public static final int EXIT_ERROR = 1;
    
	/**Main method for starting the application.
	 * 
	 * @param args are not used.
	 */
	public static void main(String[] args) {
		
	    log.debug(UIManager.getLookAndFeel());
	    log.debug(UIManager.getLookAndFeel().getID());
	    
	    if (UIManager.getLookAndFeel().getID().equals("Aqua")) { 
	    	// check for "Aqua"
	    	try {
	    		UIManager.setLookAndFeel(
	    				// OSX Aqua look and feel uses space on forms much too inefficiently
	    				// switch to the normal Java look and feel instead.
	    				UIManager.getCrossPlatformLookAndFeelClassName());
	    	} 
	    	catch (UnsupportedLookAndFeelException e) {
	    		log.error(e);
	    	}
	    	catch (ClassNotFoundException e) {
	    		log.error(e);
	    	}
	    	catch (InstantiationException e) {
	    		log.error(e);
	    	}
	    	catch (IllegalAccessException e) {
	    		log.error(e);
	    	}	
	    }
		
		System.out.println("Starting " + APP_NAME + " " + APP_VERSION);
		System.out.println(APP_COPYRIGHT);
		System.out.println(APP_LICENSE);
		log.debug("Starting " + APP_NAME + " " + APP_VERSION);
		
		// open UI and start
		MainFrame mainFrame = new MainFrame();
		Singleton.getSingletonInstance().setMainFrame(mainFrame);
		Singleton.getSingletonInstance().unsetCurrentUser();  
		Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Starting....");
		log.debug("User interface started");	
		
		// Force a login dialog by connecting to obtain record count;
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
	    mainFrame.setCount(sls.findSpecimenCount());
		
		// Load properties
		ImageCaptureProperties properties = new ImageCaptureProperties();
		Singleton.getSingletonInstance().setProperties(properties);
		log.debug("Properties loaded");
		
		// Set up a barcode (text read from barcode label for pin) matcher/builder
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION).equals(ImageCaptureProperties.COLLECTION_MCZENT)) { 
			// ** Configured for the MCZ Entomology Collection, use MCZ assumptions.
		    MCZENTBarcode barcodeTextBuilderMatcher = new MCZENTBarcode();
		    Singleton.getSingletonInstance().setBarcodeBuilder((BarcodeBuilder)barcodeTextBuilderMatcher);
		    Singleton.getSingletonInstance().setBarcodeMatcher((BarcodeMatcher)barcodeTextBuilderMatcher);
		} else if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION).equals(ImageCaptureProperties.COLLECTION_ETHZENT)) { 
			// ** Configured for the ETHZ Entomology Collection, use MCZ assumptions.
		    ETHZBarcode barcodeTextBuilderMatcher = new ETHZBarcode();
		    Singleton.getSingletonInstance().setBarcodeBuilder((BarcodeBuilder)barcodeTextBuilderMatcher);
		    Singleton.getSingletonInstance().setBarcodeMatcher((BarcodeMatcher)barcodeTextBuilderMatcher);
		} else { 
			log.error("Configured collection not recognized.  Unable to Start");
			System.exit(EXIT_ERROR);
		}
		
		// Setup to store a list of running RunnableJobs.
		Singleton.getSingletonInstance().setJobList(new RunnableJobTableModel());
		
		Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		mainFrame.setState(MainFrame.STATE_RUNNING);
		
        // Experimental chat support, working on localhost.
		
		/** 

			Context context = null;
			Hashtable contextProperties = new Hashtable(2);

			contextProperties.put(Context.PROVIDER_URL,"iiop://127.0.0.1:3700");
			contextProperties.put("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
			contextProperties.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
			contextProperties.put("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
			try {
				context = new InitialContext(contextProperties);
			} catch (NamingException ex) {
				ex.printStackTrace();
			}
			if (context!=null) { 
				ConnectionFactory connectionFactory;
				try {
					connectionFactory = (ConnectionFactory)context.lookup("jms/InsectChatTopicFactory");
					Topic chatTopic = (Topic)context.lookup("jms/InsectChatTopic");
					TopicConnection connection = (TopicConnection) connectionFactory.createConnection();
					TopicSession session = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
					TopicSubscriber subscriber = session.createSubscriber(chatTopic);
					connection.start();
					while (true) {
						Message m = subscriber.receive(1);
						if (m != null) {
							if (m instanceof TextMessage) {
								TextMessage message = (TextMessage) m;
								String originator = message.getStringProperty("Originator");
								String text = message.getText();
								System.out.println("Message: " + originator + ": " + text);
							} else {
								break;
							}
						}
					}
				} catch (NamingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
        */
		
	}
	/**
	 * Initiate actions to be taken on shutting down the application.
	 */
	public static void cleanUp() { 
		try {
			Singleton.getSingletonInstance().getProperties().saveProperties();
		} catch (Exception e) {
			System.out.println("Properties file save failed.  "  + e.getLocalizedMessage());
		}
	}
	
	/**
	 * Shut down the application.  Calls cleanUp() on normal exit.  Constants EXIT_NORMAL and EXIT_ERROR 
	 * are available to be passed as the parameter status.  
	 * 
	 * @param status 0 for normal exit, positive integer for error condition.  
	 */
	public static void exit(int status) { 
		if (status==EXIT_NORMAL) { 
			cleanUp();
		}
		System.exit(status);
	}

}
