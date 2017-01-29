/**
 * JobCleanDirectory.java
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
package edu.harvard.mcz.imagecapture.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.RunnableJobReportDialog;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.data.HigherTaxonLifeCycle;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.data.LocationInCollection;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import edu.harvard.mcz.imagecapture.interfaces.CollectionReturner;
import edu.harvard.mcz.imagecapture.interfaces.DrawerNameReturner;
import edu.harvard.mcz.imagecapture.interfaces.RunStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;
import edu.harvard.mcz.imagecapture.interfaces.TaxonNameReturner;

/** 
 * JobCleanDirectory, scan a directory for image files that have been deleted, and remove the related image records.
 * 
 * @author Paul J. Morris
 *
 */
public class JobCleanDirectory implements RunnableJob, Runnable {

	private static final Log log = LogFactory.getLog(JobCleanDirectory.class);
	
	/**
	 * Open a dialog and scan a specific directory.
	 */
	public static final int SCAN_SELECT = 1;
	/**
	 * From a file, scan a specific directory.
	 */
	public static final int SCAN_SPECIFIC = 2;
	
	private int scan = SCAN_SELECT;     // default scan a user selected directory
	Counter counter = null;	  // For reporting results
	private File startPointSpecific = null;  // place to start for scan_specific
	private int runStatus = RunStatus.STATUS_NEW;
	private Date startDate = null;
	private int percentComplete = 0;
	
	ArrayList<RunnerListener> listeners = null;

	/**
	 * Default constructor, launch a dialog.  JobCleanDirectory only
	 * works on a directory within the mount path, and must find the
	 * target directory (as readable) before running, otherwise image 
	 * records for files that do exist could be removed when the 
	 * file system is not mounted (at the configured location).
	 */
	public JobCleanDirectory() { 
		init(SCAN_SELECT,null);
	}
	
	/**
	 * Create a clean images job to bring up dialog to pick a specific directory  
	 * on which to clean up image records.  
	 * <BR>
	 * Behavior:
	 * <BR>
	 * whatToScan=SCAN_SELECT, startAt is used as starting point for directory chooser dialog.
	 * whatToScan=SCAN_SPECIFIC, startAt is used as starting point for repeat (if null falls back to SCAN_SELECT).
	 * <BR> 
	 *
	 * @param whatToScan one of SCAN_SPECIFIC, SCAN_SELECT
	 * @param startAt null or a directory starting point.
	 */
	public JobCleanDirectory(int whatToScan, File startAt) {
		init(whatToScan, startAt);
	}
		
	/**
	 * Setup initial parameters before run.
	 * 
	 * @param whatToScan one of SCAN_SPECIFIC, SCAN_SELECT
	 * @param startAt null or a directory starting point.
	 */
	public void init(int whatToScan, File startAt) {
		listeners = new ArrayList<RunnerListener>();
		scan = SCAN_SELECT;
		// store startPoint as base for dialog if SCAN_SELECT, or directory to scan if SCAN_SPECIFIC
		if (startAt!=null && startAt.canRead()) {
			startPointSpecific = startAt;
		} 
		if (whatToScan==SCAN_SPECIFIC) {
			if ((startAt!=null) && startAt.canRead()) { 
				scan = SCAN_SPECIFIC;
			} else { 
				scan = SCAN_SELECT;
			}
		}
		runStatus = RunStatus.STATUS_NEW;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#cancel()
	 */
	@Override
	public boolean cancel() {
		runStatus = RunStatus.STATUS_TERMINATED;
		log.debug("JobCleanDirectory " + this.toString() +  "  recieved cancel signal");
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStatus()
	 */
	@Override
	public int getStatus() {
		return runStatus;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#percentComplete()
	 */
	@Override
	public int percentComplete() {
		return percentComplete;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#registerListener(edu.harvard.mcz.imagecapture.interfaces.RunnerListener)
	 */
	@Override
	public boolean registerListener(RunnerListener jobListener) {
		return listeners.add(jobListener);
	}

	private List<File> getFileList()  {
		ArrayList<File> files = new ArrayList<File>();
		String pathToCheck = "";
		// Find the path in which to include files.
		File imagebase = null;   // place to start the scan from, imagebase directory for SCAN_ALL
		File startPoint = null;
		// If it isn't null, retrieve the image base directory from properties, and test for read access.
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE)==null) {
			JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "Can't start scan.  Don't know where images are stored.  Set imagbase property.", "Can't Scan.", JOptionPane.ERROR_MESSAGE);	
		} else { 
			imagebase = new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE));
			if (imagebase!=null) { 
				if (imagebase.canRead()) {
					startPoint = imagebase;
				} else {
					// If it can't be read, null out imagebase
					imagebase = null;
				}
			}
			if (scan==SCAN_SPECIFIC && startPointSpecific!=null && startPointSpecific.canRead()) {
				// A scan start point has been provided, don't launch a dialog.
				startPoint = startPointSpecific;  
			}
			if (imagebase==null || scan==SCAN_SELECT) {
				// launch a file chooser dialog to select the directory to scan
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (scan==SCAN_SELECT && startPointSpecific!=null && startPointSpecific.canRead()) { 
					fileChooser.setCurrentDirectory(startPointSpecific);  
				} else { 
					if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTPATH)!=null) { 
						fileChooser.setCurrentDirectory(new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTPATH)));
					} 
				}
				int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					log.debug("Selected base directory: " + file.getName() + ".");
					startPoint = file;
				} else {
					//TODO: handle error condition
					log.error("Directory selection cancelled by user.");
				}
			}

			// Check that startPoint is or is within imagebase.
			if (!ImageCaptureProperties.isInPathBelowBase(startPoint)) { 
				String base = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
						ImageCaptureProperties.KEY_IMAGEBASE);
				log.error("Tried to scan directory ("+ startPoint.getPath() +") outside of base image directory (" + base + ")");
				String message = "Can't scan and cleanup files outside of base image directory (" + base + ")";
				JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), message, "Can't Scan outside image base directory.", JOptionPane.YES_NO_OPTION);	
			} else if (ImageCaptureProperties.getPathBelowBase(startPoint).trim().length()==0) {
				String base = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
						ImageCaptureProperties.KEY_IMAGEBASE);
				log.error("Tried to scan directory ("+ startPoint.getPath() +") which is the base image directory.");
				String message = "Can only scan and cleanup files in a selected directory within the base directory  (" + base + ").\nYou must select some subdirectory within the base directory to cleanup.";
				JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), message, "Can't Cleanup image base directory.", JOptionPane.YES_NO_OPTION);	
				
		    } else { 
				if (!startPoint.canRead()) {
					JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "Can't start scan.  Unable to read selected directory: " + startPoint.getPath(), "Can't Scan.", JOptionPane.YES_NO_OPTION);	
				} else {
					pathToCheck = ImageCaptureProperties.getPathBelowBase(startPoint);

					// retrieve a list of image records in the selected directory
					ICImageLifeCycle ils = new ICImageLifeCycle();
					List<ICImage> images = ils.findAllInDir(pathToCheck);
					Iterator<ICImage> iter = images.iterator();
					while (iter.hasNext()) { 
						ICImage image = iter.next();
						File imageFile = new File(ImageCaptureProperties.assemblePathWithBase(image.getPath(), image.getFilename()));
						files.add(imageFile);
						counter.incrementFilesSeen();
					}

				}
			}
		}

		log.debug("Found " + files.size() + " Image files in directory to check.");

		return files;
	}


	private void cleanupFile(File file) { 
		log.debug(file);
		String filename = file.getName();
		
		if (file.exists()) { 
			log.debug(file);
			counter.incrementFilesExisting();
		} else {
			String targetPath = ImageCaptureProperties.getPathBelowBase(file.getParentFile());
			ICImageLifeCycle ils = new ICImageLifeCycle();
			ICImage pattern = new ICImage();
			pattern.setPath(targetPath);
			pattern.setFilename(file.getName());
			log.debug(targetPath);
			log.debug(file.getName());
			List<ICImage> images = ils.findByExample(pattern);
			Iterator<ICImage> iter = images.iterator();
			log.debug(images.size());
			while (iter.hasNext()) { 
				ICImage image = iter.next();
				log.debug(image.getPath());
				log.debug(image.getFilename());
				try {
					ils.delete(image);
					counter.incrementFilesFailed();
				} catch (SaveFailedException e) {
					log.error(e.getMessage(),e);
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#start()
	 */
	@Override
	public void start() {
		startDate = new Date();
		Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
		counter = new Counter();
		// Obtain a list of image file records for the selected directory.
		List<File> files = getFileList();
		log.debug("CleanDirectoryJob started" + this.toString());
		int i = 0;
		while (i < files.size()  && runStatus != RunStatus.STATUS_TERMINATED) {
			// Find out how far along the process is
			Float seen = 0.0f + i;
			Float total = 0.0f + files.size();
			percentComplete = (int) ((seen/total)*100);
			setPercentComplete(percentComplete);
			// Repeat the OCR for the present file.
			cleanupFile(files.get(i));
			i++;
		} 
		if (runStatus != RunStatus.STATUS_TERMINATED) { 
			setPercentComplete(100);
		}
		Singleton.getSingletonInstance().getMainFrame().notifyListener(runStatus, this);
		report();
		done();
	}

	private void setPercentComplete(int aPercentage) { 
		//set value
		percentComplete = aPercentage;
		//notify listeners
		Singleton.getSingletonInstance().getMainFrame().notifyListener(percentComplete, this);
		Iterator<RunnerListener> i = listeners.iterator();
		while (i.hasNext()) { 
			i.next().notifyListener(percentComplete, this);
		}
	}	
	
	/**
	 * Cleanup when job is complete.
	 */
	private void done() { 
		Singleton.getSingletonInstance().getJobList().removeJob((RunnableJob)this);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#stop()
	 */
	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		start();
	}

	private void report() { 
		String report = "Results of directory cleanup on Image files.\n";
		report += "Found  " + counter.getFilesSeen() + " image file database records.\n";
		report += "Didn't remove " + counter.getFilesExisting() + " image records where file exists.\n";
		report += "Removed " + counter.getFilesFailed() + " image records where file does not exist.\n";
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Directory cleanup complete.");
		RunnableJobReportDialog errorReportDialog = new RunnableJobReportDialog(Singleton.getSingletonInstance().getMainFrame(),report, counter.getErrors());
		errorReportDialog.setVisible(true);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getName()
	 */
	@Override
	public String getName() {
		return "Cleanup deleted images from a directory.";
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startDate;
	}	

}