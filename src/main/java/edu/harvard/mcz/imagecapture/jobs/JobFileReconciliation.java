/**
 * JobFileReconciliation.java
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
package edu.harvard.mcz.imagecapture.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.RunnableJobReportDialog;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.interfaces.RunStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;

/** JobFileReconciliation, walk through the image directories and find all files that have a filename
 * pattern that matches that of an image file that should be in the database, check if they are, and
 * report if they are not present in the database.
 * 
 * @author Paul J. Morris
 *
 */
public class JobFileReconciliation implements RunnableJob, Runnable {
	
	private static final Log log = LogFactory.getLog(JobFileReconciliation.class);

	Counter resultCounter = null;
	
	private int runStatus = RunStatus.STATUS_NEW;
	private Date startDate = null;
	
	private ArrayList<RunnerListener> listeners = null;
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#cancel()
	 */
	@Override
	public boolean cancel() {
		runStatus = RunStatus.STATUS_TERMINATED;
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
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#registerListener(edu.harvard.mcz.imagecapture.interfaces.RunnerListener)
	 */
	@Override
	public boolean registerListener(RunnerListener jobListener) {
		if (listeners==null) { listeners = new ArrayList<RunnerListener>(); } 
		return listeners.add(jobListener);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#start()
	 */
	@Override
	public void start() {
		startDate = new Date();
		runStatus = RunStatus.STATUS_RUNNING;
		Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
		resultCounter = new Counter();
		reconcileFiles();
		report(resultCounter);
		done();
	}

	private void reconcileFiles() { 
		// find the place to start
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
		            // recurse through directory tree from place to start
		            checkFiles(startPoint, resultCounter);
				}
			}
		}
	}
	
	private void checkFiles(File placeToStart, Counter counter) { 
        // get a list of files in placeToStart
		File[] containedFiles = placeToStart.listFiles();
		if (containedFiles!=null) { 
			for (int i=0; i< containedFiles.length; i++) {
				if (runStatus!=RunStatus.STATUS_TERMINATED) { 
					File fileToCheck = containedFiles[i];
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Reconciling: " + fileToCheck.getName());
					// Check to see if this is a directory
					if (fileToCheck.isDirectory()) {
						if (fileToCheck.getName().equals("thumbs")) { 
							log.debug("Skipping thumbnail directory: " + fileToCheck.getPath() + File.separator + fileToCheck.getName());
						} else { 
							if (fileToCheck.canRead()) { 
								checkFiles(fileToCheck, counter);
								counter.incrementDirectories();
							} else { 
								counter.incrementDirectoriesFailed();
							}
						}
					} else {
						// fileToCheck is a file.

						// does file to check match pattern of an image file.
						if (fileToCheck.getName().matches(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEREGEX))) { 
							// it is an image file.
							counter.incrementFilesSeen();
							// Check to see if this file is in the database.
							ICImage dbImage = new ICImage();
							dbImage.setFilename(fileToCheck.getName());
							dbImage.setPath(ImageCaptureProperties.getPathBelowBase(fileToCheck));
							ICImageLifeCycle ils = new ICImageLifeCycle();
							List<ICImage> matches = ils.findByExample(dbImage);
							if (matches!=null && matches.size()==1) {
								//if it is, increment the found counter
								counter.incrementFilesDatabased();
							} else if (matches!=null && matches.size()>1) {
								StringBuffer barcode = new StringBuffer();
								Iterator<ICImage> ri = matches.iterator();
								while (ri.hasNext()) { 
									ICImage match = ri.next();
									barcode.append(match.getSpecimen().getBarcode()).append(" ");
								}
								counter.incrementFilesDatabased();
								log.error("File with more than one database match by name and path");
								RunnableJobError error =  new RunnableJobError(fileToCheck.getName(), barcode.toString().trim(),
										ImageCaptureProperties.getPathBelowBase(fileToCheck), "", "More than one database (Image) record for this file.",
										null, null,
										null, RunnableJobError.TYPE_DUPLICATE);
								counter.appendError(error);	
							} else {
								StringBuffer barcode = new StringBuffer();
								if (matches!=null) { 
									Iterator<ICImage> ri = matches.iterator();
									while (ri.hasNext()) { 
										ICImage match = ri.next();
										barcode.append(match.getSpecimen().getBarcode()).append(" ");
									}
								}
								counter.incrementFilesFailed();
								RunnableJobError error =  new RunnableJobError(fileToCheck.getName(), barcode.toString().trim(),
										ImageCaptureProperties.getPathBelowBase(fileToCheck), "", "No database (Image) record for this file.",
										null, null,
										null, RunnableJobError.TYPE_SAVE_FAILED);
								counter.appendError(error);			
							}
						} 
					} // end if directory else file 
				}  // end status terminated check
			} // end for loop
		} // end contained files not null check
	}
	
	private void report(Counter counter) { 
        String report = "Image file to database image record reconciliation.\n";
        report += "Scanned  " + counter.getDirectories() + " directories.\n";
        report += "Found  " + counter.getFilesSeen() + " image files.\n";
        report += "Found  " + counter.getFilesDatabased() + " image file database records.\n";
        report += "Found " + counter.getFilesFailed() + " image files not in the database.\n";
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("File Reconciliation check complete");
		RunnableJobReportDialog errorReportDialog = new RunnableJobReportDialog(Singleton.getSingletonInstance().getMainFrame(),
				report, 
				counter.getErrors(), 
				RunnableJobErrorTableModel.TYPE_FILE_RECONCILIATION, 
				"Image File/Image Record reconciliation Results");
		errorReportDialog.setVisible(true);
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
	
	/**
	 * Cleanup when job is complete.
	 */
	private void done() { 
		Singleton.getSingletonInstance().getJobList().removeJob((RunnableJob)this);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getName()
	 */
	@Override
	public String getName() {
		return "Reconcile Image Files with Database";
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startDate;
	}

}
