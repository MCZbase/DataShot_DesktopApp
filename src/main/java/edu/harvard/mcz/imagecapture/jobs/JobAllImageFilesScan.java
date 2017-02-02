/**
 * JobAllImageFilesScan.java
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.CandidateImageFile;
import edu.harvard.mcz.imagecapture.ImageCaptureApp;
import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.RunnableJobReportDialog;
import edu.harvard.mcz.imagecapture.MCZBarcodePositionTemplateDetector;
import edu.harvard.mcz.imagecapture.PositionTemplate;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.UnitTrayLabelParser;
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
import edu.harvard.mcz.imagecapture.exceptions.SpecimenExistsException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import edu.harvard.mcz.imagecapture.interfaces.CollectionReturner;
import edu.harvard.mcz.imagecapture.interfaces.DrawerNameReturner;
import edu.harvard.mcz.imagecapture.interfaces.PositionTemplateDetector;
import edu.harvard.mcz.imagecapture.interfaces.RunStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;
import edu.harvard.mcz.imagecapture.interfaces.TaxonNameReturner;

/** Check all image files either under the image root directory or in a selected directory
 * and add records for files that aren't yet known to the database that contain barcode
 * information and add corresponding specimen records for new specimens.  
 *   
 * @author Paul J. Morris
 * 
 */
public class JobAllImageFilesScan implements RunnableJob, Runnable{
	
	private static final Log log = LogFactory.getLog(JobAllImageFilesScan.class);

	/**
	 * Scan all of image base directory tree.
	 */
	public static final int SCAN_ALL = 0;  
	/**
	 * Open a dialog and scan a specific directory.
	 */
	public static final int SCAN_SELECT = 1;
	/**
	 * From a file, scan a specific directory.
	 */
	public static final int SCAN_SPECIFIC = 2;
	
	private int scan = SCAN_ALL;     // default scan all
	private File startPointSpecific = null;  // place to start for scan_specific
	private String firstFile = null;  // for scan_specific, the first file seen
	private String lastFile = null;   // for scan_specific, the last file seen
	private int runStatus = RunStatus.STATUS_NEW;
	private int thumbnailCounter = 0;
	private int percentComplete = 0;
	private Date startTime = null;
	
	private ArrayList<RunnerListener> listeners = null;
	
	/**
	 * Default constructor, creates a job to scan all of imagebase, unless imagebase is 
	 * unreadable or undefined, in which case a directory chooser dialog is launched.
	 */
	public JobAllImageFilesScan() { 
		scan = SCAN_ALL;
		startPointSpecific = null;
		runStatus = RunStatus.STATUS_NEW;
		init();
	}
	
	/**
	 * Create a scan job to bring up dialog to pick a specific directory to scan, or
	 * to scan a specific directory specified by startAt.
	 * <BR>
	 * Behavior:
	 * <BR>
	 * whatToScan=SCAN_ALL, startAt is ignored, equivalent to default constructor.
	 * whatToScan=SCAN_SELECT, startAt is used as starting point for directory chooser dialog.
	 * whatToScan=SCAN_SPECIFIC, startAt is used as starting point for scan (if null falls back to SCAN_SELECT).
	 * <BR> 
	 *
	 * @param whatToScan one of SCAN_ALL, SCAN_SPECIFIC, SCAN_SELECT
	 * @param startAt null or a directory starting point.
	 */
	public JobAllImageFilesScan(int whatToScan, File startAt) {
		scan = SCAN_SELECT;
		// store startPoint as base for dialog if SCAN_SELECT, or directory to scan if SCAN_SPECIFIC
		if (startAt!=null && startAt.canRead()) {
			startPointSpecific = startAt;
		} 
		if (whatToScan==SCAN_ALL) {
			// equivalent to default constructor
			scan = SCAN_ALL;
			startPointSpecific = null;
		} 
		if (whatToScan==SCAN_SPECIFIC) {
			if ((startAt!=null) && startAt.canRead()) { 
				scan = SCAN_SPECIFIC;
			} else { 
				scan = SCAN_SELECT;
			}
		}
		runStatus = RunStatus.STATUS_NEW;
		init();
	}
	
	protected void init() { 
		listeners = new ArrayList<RunnerListener>();
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#cancel()
	 */
	@Override
	public boolean cancel() {
		runStatus = RunStatus.STATUS_TERMINATED;
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#getStatus()
	 */
	@Override
	public int getStatus() {
		return runStatus;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#registerListener(edu.harvard.mcz.imagecapture.RunnerListener)
	 */
	@Override
	public boolean registerListener(RunnerListener jobListener) {
		if (listeners==null) { init(); } 
		return listeners.add(jobListener);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#start()
	 */
	@Override
	public void start() {
		startTime = new Date();
		Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
		runStatus = RunStatus.STATUS_RUNNING;
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
				//TODO: Filechooser to pick path, then save (if SCAN_ALL) imagebase property. 
				//Perhaps.  Might be undesirable behavior.
				//Probably better to warn that imagebase is null;
			}

			// TODO: Check that startPoint is or is within imagebase.
			// Check that fileToCheck is within imagebase.
			if (!ImageCaptureProperties.isInPathBelowBase(startPoint)) { 
				String base = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
						ImageCaptureProperties.KEY_IMAGEBASE);
				log.error("Tried to scan directory ("+ startPoint.getPath() +") outside of base image directory (" + base + ")");
				String message = "Can't scan and database files outside of base image directory (" + base + ")";
				JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), message, "Can't Scan outside image base directory.", JOptionPane.YES_NO_OPTION);	
			} else { 

				// run in separate thread and allow cancellation and status reporting

				// walk through directory tree

				if (!startPoint.canRead()) {
					JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "Can't start scan.  Unable to read selected directory: " + startPoint.getPath(), "Can't Scan.", JOptionPane.YES_NO_OPTION);	
				} else {
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Scanning " + startPoint.getPath());
					Counter counter = new Counter();
					// count files to scan
					countFiles(startPoint, counter);
					setPercentComplete(0);
					Singleton.getSingletonInstance().getMainFrame().notifyListener(runStatus, this);
					counter.incrementDirectories();
					// scan
					if (runStatus!=RunStatus.STATUS_TERMINATED) { 
					    checkFiles(startPoint, counter);
					}
					// report
					String report = "Scanned " + counter.getDirectories() + " directories.\n";
					report += "Created thumbnails in " + thumbnailCounter + " directories";
					if (thumbnailCounter==0) { 
					   report += " (May still be in progress)";
					} 
					report += ".\n";
					if (startPointSpecific==null) { 
						report += "Starting with the base image directory (Preprocess All).\n";	
					} else { 
						report += "Starting with " +  startPoint.getName() + " (" + startPoint.getPath() + ")\n" ;
						report += "First file: " + firstFile + " Last File: " + lastFile + "\n";
					}
					report += "Scanned  " + counter.getFilesSeen() + " files.\n";
					report += "Created  " + counter.getFilesDatabased() + " new image records.\n";
					if (counter.getFilesUpdated()>0) { 
					    report += "Updated  " + counter.getFilesUpdated() + " image records.\n";
						
					}
					report += "Created  " + counter.getSpecimens() + " new specimen records.\n";
					if (counter.getSpecimensUpdated()>0) { 
					    report += "Updated  " + counter.getSpecimensUpdated() + " specimen records.\n";
						
					}					
					report += "Found " + counter.getFilesFailed() + " files with problems.\n";
					//report += counter.getErrors();
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Preprocess scan complete");
					setPercentComplete(100);
					Singleton.getSingletonInstance().getMainFrame().notifyListener(runStatus, this);
					RunnableJobReportDialog errorReportDialog = new RunnableJobReportDialog(Singleton.getSingletonInstance().getMainFrame(),report, counter.getErrors(), "Preprocess Results");
					errorReportDialog.setVisible(true);
					//JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), report, "Preprocess complete", JOptionPane.ERROR_MESSAGE);
				} // can read directory
			}
			
			SpecimenLifeCycle sls = new SpecimenLifeCycle();
			Singleton.getSingletonInstance().getMainFrame().setCount(sls.findSpecimenCount());
		} // Imagebase isn't null
		done();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#stop()
	 */
	@Override
	public boolean stop() {
		runStatus = RunStatus.STATUS_TERMINATED;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int percentComplete() {
		return percentComplete;
	}
	
	private void countFiles(File startPoint, Counter counter) { 
		// count files to preprocess
		File[] containedFiles = startPoint.listFiles();
		if (containedFiles!=null) { 
			for (int i=0; i< containedFiles.length; i++) {
				File fileToCheck = containedFiles[i];
				if (fileToCheck.isDirectory()) { 
					if (fileToCheck.canRead()) { 
						countFiles(fileToCheck, counter);
					}
				} else { 
					counter.incrementTotal();
				}
			}
		}
	}
	
	protected class ThumbnailBuilderInternal implements Runnable, RunnableJob{

		File startPoint;
		
		String thumbHeight;
		String thumbWidth;
		
		private Date thumbStartTime = null;
		private int thumbRunStatus = RunStatus.STATUS_NEW;
		private int thumbPercentComplete = 0;
		
		private ArrayList<RunnerListener> thumbListeners = null;

		public ThumbnailBuilderInternal(File aStartPoint) { 
			startPoint = aStartPoint;
			thumbHeight = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_THUMBNAIL_HEIGHT);
			thumbWidth = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_THUMBNAIL_WIDTH);
			thumbInit();
		}
		
		public ThumbnailBuilderInternal(File aStartPoint, int thumbHeightPixels, int thumbWidthPixels) { 
			startPoint = aStartPoint;
			thumbHeight = Integer.toString(thumbHeightPixels);
			thumbWidth = Integer.toString(thumbWidthPixels);
			if (thumbHeightPixels < 10) { 
			    thumbHeight = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_THUMBNAIL_HEIGHT);
			}
			if (thumbWidthPixels < 10) { 
			    thumbWidth = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_THUMBNAIL_WIDTH);
			}
			thumbInit();
		}		
		
		protected void thumbInit() { 
			thumbListeners = new ArrayList<RunnerListener>();
		}

		@Override
		public void run() {
			thumbStartTime = new Date();
			thumbRunStatus = RunStatus.STATUS_RUNNING;
			setThumbPercentComplete(0);
			Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
			// mkdir thumbs ; mogrify -path thumbs -resize 80x120 *.JPG					    
			if (startPoint.isDirectory() &&  (!startPoint.getName().equals("thumbs"))) { 
				File thumbsDir = new File(startPoint.getPath() + File.separator + "thumbs");
				log.debug(thumbsDir.getPath());
				if (!thumbsDir.exists()) { 
					thumbsDir.mkdir();
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Creating " + thumbsDir.getPath());
				}
				// Runtime executes mogrify directly, not through a shell, thus expand list of files to pass
				// rather than passing *.JPG
				File[] potentialFilesToThumb = startPoint.listFiles();
				StringBuffer filesToThumb = new StringBuffer();
				int filesToThumbCount = 0;
				for (int i=0; i<potentialFilesToThumb.length; i++) { 
					if (potentialFilesToThumb[i].getName().endsWith(".JPG")) { 
						filesToThumb.append(potentialFilesToThumb[i].getName()).append(" ");
						filesToThumbCount++;
					}
				}
				if (filesToThumbCount>0) { 
					boolean makeWithJava = false;

					String mogrify = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_MOGRIFY_EXECUTABLE);
							if (mogrify==null || mogrify.trim().length()>0) { 
								makeWithJava = true;
							} else { 		
								String runCommand = mogrify + " -path thumbs -resize " + thumbWidth +"x"+ thumbHeight+" " + filesToThumb.toString();


								Runtime r = Runtime.getRuntime();
								log.debug(runCommand);
								try {
									String[] env = {""};
									Process proc = r.exec(runCommand,env,startPoint);
									InputStream stderr = proc.getErrorStream();
									InputStreamReader isrstderr = new InputStreamReader(stderr);
									BufferedReader br = new BufferedReader(isrstderr);
									String line = null;
									while ((line = br.readLine()) != null) { 
										log.debug("stderr:" + line);
									} 
									int exitVal = proc.waitFor();
									log.debug("Mogrify Process exitValue: " + exitVal);
									if (exitVal==0) { 
										thumbnailCounter++;
										String message = "Finished creating thumbnails in: " + startPoint.getPath();
										Singleton.getSingletonInstance().getMainFrame().setStatusMessage(message); 
										log.debug(message);
									} else { 
										log.error("Error returned running " + runCommand);
										makeWithJava = true;
									}
								} catch (IOException e) {
									log.error("Error running: " + runCommand);
									e.printStackTrace();
									Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Error creating thumbnails " + e.getMessage());
									makeWithJava = true;
								} catch (InterruptedException e) {
									log.error("Mogrify process interupted");
									e.printStackTrace();
								}

							} 

					if (makeWithJava) { 
						for (int i=0; i<potentialFilesToThumb.length; i++) {
							try {
								log.debug("Attempting thumbnail generation with java in " + startPoint.getPath());
								log.debug("Attempting thumbnail generation with java to " + thumbsDir.getPath());
								ArrayList<String> makeFrom = new ArrayList<String>();
								List<File> files = Arrays.asList(startPoint.listFiles());
								Iterator<File> it = files.iterator();
								int creationCounter = 0;
								int totalFiles = files.size();
								while (it.hasNext() && thumbRunStatus != RunStatus.STATUS_CANCEL_REQUESTED) { 
									File file = it.next();
									if (!file.isDirectory() && file.exists() && file.canRead()) {
										// file must exist and be readable to make thumbnail
										if (file.getName().matches(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEREGEX))) {
											// only try to make thumbnails of files that match the image file pattern.
											makeFrom.add(file.getPath());
											log.debug(file.getPath());
											File target = new File(thumbsDir.getPath() + File.separatorChar + file.getName());
											if (!target.exists()) { 
												BufferedImage img = new BufferedImage(Integer.parseInt(thumbWidth), Integer.parseInt(thumbHeight), BufferedImage.TYPE_INT_RGB);
												img.createGraphics().drawImage(ImageIO.read(file).getScaledInstance(80, 120, Image.SCALE_SMOOTH),0,0,null);
												ImageIO.write(img, "jpg", target);
												creationCounter++;
											}
										}
										setThumbPercentComplete( (int) (((float)creationCounter/totalFiles)*100) );
									}
								}
								String message = "Finished creating thumbnails (" + creationCounter +") in: " + startPoint.getPath();
								Singleton.getSingletonInstance().getMainFrame().setStatusMessage(message); 
							} catch (IOException e) {
								log.error("Thumbnail generation with thumbnailator library failed");
								log.error(e.getMessage());
							}
						}
					}
                    
				} else {
					String message = "No *.JPG files found in " + startPoint.getPath();
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage(message);
					log.debug(message);
				}
			}
			String message = "Thumbnail Generation Complete.";
			Singleton.getSingletonInstance().getMainFrame().setStatusMessage(message);
			Singleton.getSingletonInstance().getJobList().removeJob((RunnableJob)this);
		}

		/* (non-Javadoc)
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#start()
		 */
		@Override
		public void start() {
			run();
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
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#cancel()
		 */
		@Override
		public boolean cancel() {
			thumbRunStatus = RunStatus.STATUS_CANCEL_REQUESTED;
			return false;
		}

		/* (non-Javadoc)
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStatus()
		 */
		@Override
		public int getStatus() {
			return thumbRunStatus;
		}

		/* (non-Javadoc)
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#percentComplete()
		 */
		@Override
		public int percentComplete() {
			return thumbPercentComplete;
		}

		protected void setThumbPercentComplete(int aPercentage) { 
			//set value
			thumbPercentComplete = aPercentage;
			log.debug(thumbPercentComplete);
			//notify listeners
			Iterator<RunnerListener> i = thumbListeners.iterator();
			while (i.hasNext()) { 
				i.next().notifyListener(thumbPercentComplete, this);
			}
		}			
		
		/* (non-Javadoc)
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#registerListener(edu.harvard.mcz.imagecapture.interfaces.RunnerListener)
		 */
		@Override
		public boolean registerListener(RunnerListener aJobListener) {
			if (thumbListeners==null) { thumbInit(); } 
			return thumbListeners.add(aJobListener);
		}

		/* (non-Javadoc)
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getName()
		 */
		@Override
		public String getName() {
			return "Thumbnail Generation in: " + startPoint;
		}

		/* (non-Javadoc)
		 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
		 */
		@Override
		public Date getStartTime() {
			return thumbStartTime;
		} 
		
	}  // end class ThumbnailBuilder

	private void checkFiles(File startPoint, Counter counter) {
		// pick jpeg files
		// for each file check name against database, if not found, check barcodes, scan and parse text, create records.
		log.debug("Scanning directory: " + startPoint.getPath());
		File[] containedFiles = startPoint.listFiles();
		log.debug("Directory contains  " + containedFiles.length + " entries.");
		if (containedFiles.length>0) {
			// create thumbnails in a separate thread
			(new Thread(new ThumbnailBuilderInternal(startPoint))).start();
		}
		for (int i=0; i< containedFiles.length; i++) {
			if (runStatus != RunStatus.STATUS_TERMINATED) { 
				log.debug("Scanning directory: " + startPoint.getPath());
				File fileToCheck = containedFiles[i];
				Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Scanning: " + fileToCheck.getName());
				log.debug("Scanning: " + fileToCheck.getName());
				if (fileToCheck.isDirectory()) { 
					if (fileToCheck.canRead()) {
						// Skip thumbs directories
						if (!fileToCheck.getName().equals("thumbs")) { 
						   checkFiles(fileToCheck, counter);
						   counter.incrementDirectories();
						} 
					} else { 
						counter.incrementDirectoriesFailed();
					}
				} else { 
					// check JPEG files for barcodes 
					if (!fileToCheck.getName().matches(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEREGEX))) {
						log.debug("Skipping file [" + fileToCheck.getName() +  "], doesn't match expected filename pattern " + Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEREGEX) );
					} else { 
						if (firstFile==null) { firstFile = fileToCheck.getName(); } 
						lastFile = fileToCheck.getName();
						Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_LASTPATH, fileToCheck.getPath());
						String filename = fileToCheck.getName();
						counter.incrementFilesSeen();
						log.debug("Checking image file: " + filename); 
						// scan file for barcodes and ocr of unit tray label text
						CandidateImageFile scannableFile = null;
						try {
							PositionTemplateDetector detector = new MCZBarcodePositionTemplateDetector();
							boolean isSpecimenImage = false;
							boolean isDrawerImage = false;
							boolean reattach = false;  // image is detached instance and should be reattached instead of persisted denovo.
							try {
								// Check for an existing image record.
								ICImageLifeCycle imageCont = new ICImageLifeCycle();
								ICImage tryMe = new ICImage();
								tryMe.setFilename(filename);
								String path = ImageCaptureProperties.getPathBelowBase(fileToCheck);
								tryMe.setPath(path);
								List <ICImage> matches = imageCont.findByExample(tryMe);
								log.debug(matches.size());
								if (matches!=null && matches.size()==1
										&& matches.get(0).getRawBarcode()==null
										&& matches.get(0).getRawExifBarcode()==null
										&& (matches.get(0).getDrawerNumber()==null || matches.get(0).getDrawerNumber().trim().length()==0)
										) {
									// likely case for a failure to read data out of the image file 
									// try to update the image file record.
									try {
										tryMe = imageCont.merge(matches.get(0));
										matches.remove(0);
										reattach = true;
										log.debug(tryMe);
									} catch (SaveFailedException e) {
										log.error(e.getMessage(),e);
									}
								} else if (matches!=null && matches.size()==1 && matches.get(0).getSpecimen()==null) {
									// likely case for a failure to create a specimen record in a previous run
									// try to update the image file record
									try {
										tryMe = imageCont.merge(matches.get(0));
										matches.remove(0);
										reattach = true;
										log.debug(tryMe);
									} catch (SaveFailedException e) {
										log.error(e.getMessage(),e);
									}
								}								
								if (matches!=null && matches.size()==0) {
									// No database record for this file.
									
									// ** Identify the template.
									String templateId = detector.detectTemplateForImage(fileToCheck);
									log.debug("Detected Template: " + templateId);
									PositionTemplate template = new PositionTemplate(templateId);
									// Found a barcode in a templated position in the image.
									// ** Scan the file based on this template.
									scannableFile = new CandidateImageFile(fileToCheck, template);
									String barcode = scannableFile.getBarcodeText(template);
									if (scannableFile.getBarcodeStatus()!=CandidateImageFile.RESULT_BARCODE_SCANNED) {
										log.error("Error scanning for barcode: " + barcode);
										barcode = "";
									}
									System.out.println("Barcode=" + barcode);
									String exifComment = scannableFile.getExifUserCommentText();
									TaxonNameReturner parser = null;
									String rawOCR = "";
									UnitTrayLabel labelRead = null;
									String state = WorkFlowStatus.STAGE_0;
									labelRead = scannableFile.getLabelQRText(template);
									if (labelRead==null) { 
										try { 
											labelRead = scannableFile.getLabelQRText(new PositionTemplate("Test template 2"));
										} catch (NoSuchTemplateException e) {
											try { 
												labelRead = scannableFile.getLabelQRText(new PositionTemplate("Small template 2"));
											} catch (NoSuchTemplateException e1) {
												log.error("Neither Test template 2 nor Small template 2 found");
											}
										}
									}
									if (labelRead!=null) { 
										rawOCR = labelRead.toJSONString();
										state = WorkFlowStatus.STAGE_1;
										parser = (TaxonNameReturner)labelRead;
									} else {
										PositionTemplate shifted = null;
										try {
										    shifted = new PositionTemplate("Test template 2");
									    } catch (NoSuchTemplateException e) {
										    try { 
										       shifted = new PositionTemplate("Small template 2");
										    } catch (NoSuchTemplateException e1) {
											    log.error("Neither Test template 2 nor Small template 2 found");
										    }
									    }
									    if (shifted != null) {
									    	int x = 5;
									    	int xmax = 9;
										    Dimension utpos = shifted.getUtBarcodePosition();
									    	while (x < xmax) { 
										        utpos.setSize(new Dimension(utpos.width +x, utpos.height));
										        shifted.setUtBarcodePosition(utpos);
										        labelRead = scannableFile.getLabelQRText(shifted);
										        x++;
										        if (labelRead !=null ) { 
										        	x = xmax;
										        	log.debug("Failover found: " + labelRead.getFamily() + " "  + labelRead.getSubfamily() + " " + labelRead.getGenus());
										        }
									    	} 
									    } 
										try { 
											rawOCR = scannableFile.getLabelOCRText(template);
										} catch (OCRReadException e) { 
											log.error(e);
											rawOCR = "";
											log.error("Couldn't OCR file." + e.getMessage());
											RunnableJobError error =  new RunnableJobError(filename, "OCR Failed",
													barcode, exifComment, "Couldn't find text to OCR",
													null, null,
													e, RunnableJobError.TYPE_NO_TEMPLATE);
											counter.appendError(error);
										}
										if (labelRead==null) { 
										    if (rawOCR==null) { rawOCR = ""; } 
										    state = WorkFlowStatus.STAGE_0;
										    parser = new UnitTrayLabelParser(rawOCR);
											RunnableJobError error =  new RunnableJobError(filename, "Failover to OCR.",
													barcode, exifComment, "Couldn't read Taxon barcode, failed over to OCR only.",
													(TaxonNameReturner)parser, null,
											null, RunnableJobError.TYPE_FAILOVER_TO_OCR);
											counter.appendError(error);
										}  else { 
											state = WorkFlowStatus.STAGE_1;
											parser = labelRead;
										}
									}

									// Test: is exifComment a barcode:

									// Case 1: This is an image of papers associated with a container (a unit tray or a box).
									// This case can be identified by there being no barcode data associated with the image.
									// Action: 
									// A) Check the exifComment to see what metadata is there, if blank, User needs to fix.
									//    exifComment may contain a drawer number, identifying this as a drawer image.  Save as such.
									// Options: A drawer, for which number is captured.  A unit tray, capture ?????????.  A specimen
									// where barcode wasn't read, allow capture of barcode and treat as Case 2.
									// B) Create an image record and store the image metadata (with a null specimen_id). 

									// Case 2: This is an image of a specimen and associated labels or an image assocated with 
									// a specimen with the specimen's barcode label in the image.
									// This case can be identified by there being a barcode in a templated position or there 
									// being a barcode in the exif comment tag.  
									// Action: 
									// A) Check if a specimen record exists, if not, create one from the barcode and OCR data.
									// B) Create an image record and store the image metadata.

									if (Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(exifComment) 
											||  Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(barcode)) { 
										isSpecimenImage = true;
										System.out.println("Specimen Image");
									} else { 
										if (exifComment.matches(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER))) { 
											isDrawerImage = true;
											System.out.println("Drawer Image");
										} else {
											if (templateId.equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) { 
												log.debug("Image doesn't appear to contain a barcode in a templated position.");
												counter.incrementFilesFailed();
												RunnableJobError error =  new RunnableJobError(filename, barcode,
														barcode, exifComment, "Image doesn't appear to contain a barcode in a templated position.",
														null, null,
														null, RunnableJobError.TYPE_NO_TEMPLATE);
												counter.appendError(error);
											}
											// Nothing found.  Need to ask.
											// TODO: list failures on completion.
											counter.incrementFilesFailed();
										}
									}

									String rawBarcode = barcode;
									if (isSpecimenImage) {
										if (!rawBarcode.equals(exifComment)) { 
											// Use the exifComment if it is a barcode
											boolean barcodeInImageMetadata = false;
											if (Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(exifComment))  { 
												barcodeInImageMetadata = true;
											}
											// Log the missmatch
											if (barcodeInImageMetadata || Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
												// If so configured, or if image metadata contains a barcode that doesn't match the barcode in the image
												// report on barcode/comment missmatch as an error condition.
												try { 
													RunnableJobError error =  new RunnableJobError(filename, barcode,
														barcode, exifComment, "Barcode/Comment missmatch.",
														parser, (DrawerNameReturner) parser,
														null, RunnableJobError.TYPE_MISMATCH);
													counter.appendError(error);
												} catch (Exception e) { 
													// we don't want an exception to stop processing 
													log.error(e);
												}
											} else {
												// Just write into debug log
												// This would normally the case where the image metadata doesn't contain a barcode but the image does, and reporting of this state as an error has been turned off. 
												log.debug("Barcode/Comment missmatch: ["+barcode+"]!=["+exifComment+"]");
											}
										}
										Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Creating new specimen record.");
										Specimen s = new Specimen();
										if ((!Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(barcode)) 
												&& Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(exifComment)) {
											// special case: coudn't read QR code barcode from image, but it was present in exif comment.
											s.setBarcode(exifComment);
											barcode = exifComment;
										} else { 
											if (!Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(barcode)) {
												// Won't be able to save the specimen record if we end up here.
												log.error("Neither exifComment nor QR Code barcode match the expected pattern for a barcode, but isSpecimenImage got set to true.");
											}
											s.setBarcode(barcode);
										}
										s.setWorkFlowStatus(state);
										
										if (labelRead!=null) { 
											//  We got json data from a barcode.  
											s.setFamily(parser.getFamily());
											s.setSubfamily(parser.getSubfamily());
											s.setTribe(parser.getTribe());
										} else { 
                                            // We failed over to OCR, try lookup in DB.
											s.setFamily("");  // make sure there's a a non-null value in family.
											if (parser.getTribe().trim().equals("")) {	
												HigherTaxonLifeCycle hls = new HigherTaxonLifeCycle();
												if (hls.isMatched(parser.getFamily(), parser.getSubfamily()))  {
													// If there is a match, use it.
													String[] higher = hls.findMatch(parser.getFamily(), parser.getSubfamily());
													s.setFamily(higher[0]);
													s.setSubfamily(higher[1]);
												} else { 
													// otherwise use the raw OCR output.
													s.setFamily(parser.getFamily());
													s.setSubfamily(parser.getSubfamily());
												}
												s.setTribe("");
											} else { 
												HigherTaxonLifeCycle hls = new HigherTaxonLifeCycle();
												if (hls.isMatched(parser.getFamily(), parser.getSubfamily(),parser.getTribe()))  {
													String[] higher = hls.findMatch(parser.getFamily(), parser.getSubfamily(),parser.getTribe());
													s.setFamily(higher[0]);
													s.setSubfamily(higher[1]);
													s.setTribe(higher[2]);
												} else { 
													s.setFamily(parser.getFamily());
													s.setSubfamily(parser.getSubfamily());
													s.setTribe(parser.getTribe());
												}					
											}
										}
										if (state.equals(WorkFlowStatus.STAGE_0)) { 
											// Look up likely matches for the OCR of the higher taxa in the HigherTaxon authority file.
											
											if (!parser.getFamily().equals(""))  {
												// check family against database (with a soundex match)
												HigherTaxonLifeCycle hls = new HigherTaxonLifeCycle();
												String match = hls.findMatch(parser.getFamily()); 
												if (match!=null && !match.trim().equals("")) { 
													s.setFamily(match);
												}
											}
										}
										// trim family to fit (in case multiple parts of taxon name weren't parsed
										// and got concatenated into family field.
										if (s.getFamily().length()>40) { 
											s.setFamily(s.getFamily().substring(0,40));
										}

										s.setGenus(parser.getGenus());
										s.setSpecificEpithet(parser.getSpecificEpithet());
										s.setSubspecificEpithet(parser.getSubspecificEpithet());
										s.setInfraspecificEpithet(parser.getInfraspecificEpithet());
										s.setInfraspecificRank(parser.getInfraspecificRank());
										s.setAuthorship(parser.getAuthorship());
										s.setDrawerNumber(((DrawerNameReturner)parser).getDrawerNumber());
										s.setCollection(((CollectionReturner)parser).getCollection());
										s.setCreatingPath(ImageCaptureProperties.getPathBelowBase(fileToCheck));
										s.setCreatingFilename(fileToCheck.getName());
										if (parser.getIdentifiedBy()!=null && parser.getIdentifiedBy().length()>0) {
											s.setIdentifiedBy(parser.getIdentifiedBy());
										}
										log.debug(s.getCollection());

										// TODO: non-general workflows
										
										// TODO: Refactor special case handling of non-general workflows
										
										// ********* Special Cases **********
										if (s.getWorkFlowStatus().equals(WorkFlowStatus.STAGE_0)) { 
											// ***** Special case, images in ent-formicidae 
											//       get family set to Formicidae if in state OCR.
											if (path.contains("formicidae")) { 
												s.setFamily("Formicidae");
											}
										}
										s.setLocationInCollection(LocationInCollection.getDefaultLocation());
										if (s.getFamily().equals("Formicidae")) { 
											// ***** Special case, families in Formicidae are in Ant collection
										    s.setLocationInCollection(LocationInCollection.GENERALANT);
										}
										// ********* End Special Cases **********
										
										
										s.setCreatedBy(ImageCaptureApp.APP_NAME + " " + ImageCaptureApp.APP_VERSION);
										SpecimenLifeCycle sh = new SpecimenLifeCycle();
										try { 
											// *** Save a database record of the specimen.
											sh.persist(s);
											counter.incrementSpecimens();
	                						s.attachNewPart();
										} catch (SpecimenExistsException e) {
											log.debug(e.getMessage());
											// Expected case on scanning a second image for a specimen.
											// Doesn't need to be reported as a parsing error.
											// 
											// Look up the existing record to link this specimen to it.
											try { 
												Specimen checkSpecimen = new Specimen();
												checkSpecimen.setBarcode(barcode);
												List <Specimen> checkResult = sh.findByExample(checkSpecimen);
												if (checkResult.size()==1) { 
													s = checkResult.get(0);
												} 
											} catch (Exception e2) { 
												s = null; // so that saving the image record doesn't fail on trying to save linked transient specimen record.
												String errorMessage = "Linking Error: \nFailed to link image to existing specimen record.\n";
												RunnableJobError error =  new RunnableJobError(filename, barcode,
														rawBarcode, exifComment, errorMessage,
														(TaxonNameReturner)parser, (DrawerNameReturner)parser,
														e2, RunnableJobError.TYPE_SAVE_FAILED);
											}
										} catch (SaveFailedException e) { 
											// Couldn't save for some reason other than the
											// specimen record already existing.  Check for possible 
											// save problems resulting from parsing errors.
											log.debug(e.getMessage());
											try { 
												Specimen checkSpecimen = new Specimen();
												checkSpecimen.setBarcode(barcode);
												List <Specimen> checkResult = sh.findByExample(checkSpecimen);
												if (checkResult.size()==1) { 
													s = checkResult.get(0);
												} 
												String badParse = "";
												// Drawer number with length limit (and specimen that fails to save at over this length makes
												// a good canary for labels that parse very badly.
												if (((DrawerNameReturner)parser).getDrawerNumber().length()>MetadataRetriever.getFieldLength(Specimen.class, "DrawerNumber")) {
													badParse = "Parsing problem. \nDrawer number is too long: " + s.getDrawerNumber() + "\n";
												}
												RunnableJobError error =  new RunnableJobError(filename, barcode,
														rawBarcode, exifComment, badParse,
														(TaxonNameReturner)parser, (DrawerNameReturner)parser,
														null, RunnableJobError.TYPE_BAD_PARSE);
												counter.appendError(error);
											} catch (Exception err) {
												log.error(e);
												log.error(err);
												// TODO: Add a general error handling/inform user class.

												String badParse = "";
												// Drawer number with length limit (and specimen that fails to save at over this length makes
												// a good canary for labels that parse very badly.
												if (s.getDrawerNumber()==null) {
													badParse = "Parsing problem. \nDrawer number is null: \n";
												} else { 
													if (s.getDrawerNumber().length()>MetadataRetriever.getFieldLength(Specimen.class, "DrawerNumber")) {
														// This was an OK test for testing OCR, but in production ends up in records not being 
														// created for files, which ends up being a larger quality control problem than records 
														// with bad OCR.  

														// Won't fail this way anymore - drawer number is now enforced in Specimen.setDrawerNumber()
														badParse = "Parsing problem. \nDrawer number is too long: " + s.getDrawerNumber() + "\n";
													}
												} 
												RunnableJobError error =  new RunnableJobError(filename, barcode,
														rawBarcode, exifComment, badParse,
														(TaxonNameReturner)parser, (DrawerNameReturner)parser,
														err, RunnableJobError.TYPE_SAVE_FAILED);
												counter.appendError(error);
												counter.incrementFilesFailed();
												s = null;
											}
										}
										if (s!=null) { 
											tryMe.setSpecimen(s);
										}
									} 
									tryMe.setRawBarcode(rawBarcode);
									if (isDrawerImage) { 
										tryMe.setDrawerNumber(exifComment);
									} else { 
										tryMe.setRawExifBarcode(exifComment);
										tryMe.setDrawerNumber(((DrawerNameReturner)parser).getDrawerNumber());
									}
									tryMe.setRawOcr(rawOCR);
									tryMe.setTemplateId(template.getTemplateId());
									tryMe.setPath(path);
									// TODO: Create md5hash of image file, persist with image 
									if (tryMe.getMd5sum()==null || tryMe.getMd5sum().length()==0) { 
										try {
											tryMe.setMd5sum(DigestUtils.md5Hex(new FileInputStream(fileToCheck)));
										} catch (FileNotFoundException e) {
											log.error(e.getMessage());
										} catch (IOException e) {
											log.error(e.getMessage());
										}
									}
									try {
										if (reattach) {
											// Update image file record
											imageCont.attachDirty(tryMe);
										    log.debug("Updated " + tryMe.toString());
										    counter.incrementFilesUpdated();
										} else { 
										   // *** Save a database record of the image file.
										   imageCont.persist(tryMe);
										   log.debug("Saved " + tryMe.toString());
										   counter.incrementFilesDatabased();
										}
									} catch (SaveFailedException e) {
										// TODO Auto-generated catch block
										log.error(e.getMessage(),e);
										counter.incrementFilesFailed();
										String failureMessage = "Failed to save image record.  " + e.getMessage();
										RunnableJobError error =  new RunnableJobError(filename, "Save Failed",
												tryMe.getFilename(), tryMe.getPath(), failureMessage,
												null, null,
												null, RunnableJobError.TYPE_SAVE_FAILED);
										counter.appendError(error);
									}
									if (isSpecimenImage) {
											if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
												// If so configured, log as error
												if (!tryMe.getRawBarcode().equals(tryMe.getRawExifBarcode())) { 
													log.error("Warning: Scanned Image has missmatch between barcode and comment.");
												}
											}
									}
								} else {
									if (matches==null) { 
										counter.incrementFilesFailed();
										String failureMessage = "Probable bad data in database.  Null match searching for image file.  Notify the database administrator.";
										RunnableJobError error =  new RunnableJobError(filename, "Bad Data",
												tryMe.getFilename(), tryMe.getPath(), failureMessage,
												null, null,
												null, RunnableJobError.TYPE_SAVE_FAILED);
										counter.appendError(error);
									} else { 
										// found an already databased file (where we have barcode/specimen or drawer number data).
										log.debug("Record exists, skipping file " + filename);
										counter.incrementFilesExisting();
									}
								}
							} catch (NoSuchTemplateException e) {
								log.error("Detected Template for image doesn't exist. " + e.getMessage());
							} 



						} catch (UnreadableFileException e) {
							counter.incrementFilesFailed();
							log.error("Couldn't read file." + e.getMessage());
							//} catch (OCRReadException e) {
							//	counter.incrementFilesFailed();
							//	log.error("Couldn't OCR file." + e.getMessage());
						}
					}
				} 
				// report progress
				Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Scanned: " + fileToCheck.getName());
				Float seen = 0.0f + counter.getFilesSeen();
				Float total = 0.0f + counter.getTotal();
				// thumbPercentComplete = (int) ((seen/total)*100);
				setPercentComplete( (int) ((seen/total)*100) );
			} 
			Singleton.getSingletonInstance().getMainFrame().notifyListener(runStatus, this);
		}

	}
	
	private void setPercentComplete(int aPercentage) { 
		//set value
		percentComplete = aPercentage;
		log.debug(percentComplete);
		//notify listeners
		Singleton.getSingletonInstance().getMainFrame().notifyListener(percentComplete, this);
		Iterator<RunnerListener> i = listeners.iterator();
		while (i.hasNext()) { 
			i.next().notifyListener(percentComplete, this);
		}
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
		if (scan==SCAN_ALL) 
		   return "Preprocess all image files";
		else 
		   return "Preprocess images in " + startPointSpecific;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startTime;
	}
	
}

