 /**
 * BarcodeScanJob.java
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.CandidateImageFile;
import edu.harvard.mcz.imagecapture.DefaultPositionTemplateDetector;
import edu.harvard.mcz.imagecapture.ImageCaptureApp;
import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.ImageDisplayFrame;
import edu.harvard.mcz.imagecapture.PositionTemplate;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.SpecimenControler;
import edu.harvard.mcz.imagecapture.SpecimenDetailsViewPane;
import edu.harvard.mcz.imagecapture.UnitTrayLabelParser;
import edu.harvard.mcz.imagecapture.WhatsThisImageDialog;
import edu.harvard.mcz.imagecapture.data.HigherTaxonLifeCycle;
import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.data.LocationInCollection;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.BadTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.ImageLoadException;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchRecordException;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SpecimenExistsException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import edu.harvard.mcz.imagecapture.interfaces.CollectionReturner;
import edu.harvard.mcz.imagecapture.interfaces.DrawerNameReturner;
import edu.harvard.mcz.imagecapture.interfaces.RunStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;
import edu.harvard.mcz.imagecapture.interfaces.TaxonNameReturner;

/** Examines a single image file attempts to determine the correct template, tries to parse data from the image, launches 
 * a display of the barcode, labels, unit tray label, and specimen parts of the image, and if persistence is requested, 
 * stores the image and specimen data in the database and launches a UI for editing the specimen record. 
 * The image file to scan is selected by the user from a file picker dialog which is launched when starting the job.
 * <BR>
 * Usage:
 * <pre>
      JobSingleBarcodeScan s = new JobSingleBarcodeScan(true);
      s.start();
   </pre>  
 *
 * @author Paul J. Morris
 *
 */
public class JobSingleBarcodeScan implements RunnableJob, Runnable {
	
	private static final Log log = LogFactory.getLog(JobSingleBarcodeScan.class);
	
	private boolean persist = false;
	private Date startDate = null;
	private int percentComplete = 0;
	private int runStatus = RunStatus.STATUS_NEW;
	
	private ArrayList<RunnerListener> listeners = null;
	
	/**
	 * Default constructor, creates a single image job with persist=false, allows examination of image
	 * extracted barcode, and OCR of label data without making a database connection.
	 */
	public JobSingleBarcodeScan() { 
		init();
	}
	/**Constructor allowing specification of persistence.
	 * 
	 * @param persistResult if true enables connection to database to persist changes, and adds editable form
	 * showing the current specimen record in the database that matches the barcode extracted from the image, creating 
	 * the image and specimen records if needed.
	 */
	public JobSingleBarcodeScan(boolean persistResult) { 
		persist = persistResult;
		init();
	}	

	private void init() { 
		listeners = new ArrayList<RunnerListener>();
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
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#cancel()
	 */
	@Override
	public boolean cancel() {
		runStatus = RunStatus.STATUS_TERMINATED;
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
	public void start()  {
		startDate = new Date();
		Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
		setPercentComplete(0);
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Selecting file to check.");
		String rawOCR = "";   // to hold unparsed ocr output from unit tray label
		//Create a file chooser
		final JFileChooser fileChooser = new JFileChooser();
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTPATH)!=null) { 
			fileChooser.setCurrentDirectory(new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTPATH)));
		} 
		//FileNameExtensionFilter filter = new FileNameExtensionFilter("TIFF Images", "tif", "tiff");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "tif", "tiff", "jpg", "jpeg", "png");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
		setPercentComplete(10);
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Scanning file for barcode.");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File fileToCheck = fileChooser.getSelectedFile();
			Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_LASTPATH, fileToCheck.getPath());
			String filename = fileToCheck.getName();
			log.debug("Selected file " + filename + " to scan for barcodes");
			
			

						
			
			// scan selected file
			PositionTemplate defaultTemplate = null;
			// Figure out which template to use.
			DefaultPositionTemplateDetector detector = new DefaultPositionTemplateDetector();
			String template = "";
			try {
				template = detector.detectTemplateForImage(fileToCheck);
			} catch (UnreadableFileException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			setPercentComplete(20);	
			try {
				defaultTemplate = new PositionTemplate(template);
				log.debug("Set template to: " + defaultTemplate.getTemplateId());
			} catch (NoSuchTemplateException e1) {
				try { 
					defaultTemplate = new PositionTemplate(PositionTemplate.TEMPLATE_DEFAULT);
					log.error("Template not recongised, reset template to: " + defaultTemplate.getTemplateId());
				} catch (Exception e2) {
				   // We shouldn't end up here - we just asked for the default template by its constant.
				   log.fatal("PositionTemplate doesn't recognize TEMPLATE_DEFAULT");
				   log.trace(e2);
				   ImageCaptureApp.exit(ImageCaptureApp.EXIT_ERROR);
				} 
			} 
			// TODO: Store the template id for this image with the other image metadata so
            // that we don't have to check again.
			
			CandidateImageFile scannableFile;
			try {
				scannableFile = new CandidateImageFile(fileToCheck, defaultTemplate);

				String barcode = scannableFile.getBarcodeText(defaultTemplate);
				if (scannableFile.getBarcodeStatus()!=CandidateImageFile.RESULT_BARCODE_SCANNED) {
					log.error("Error scanning for barcode: " + barcode);
					barcode = "";
				}
				String exifComment = scannableFile.getExifUserCommentText();
				if (barcode.equals("") && Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(exifComment)) {
				    // There should be a template for this image, and it shouldn't be the TEMPLATE_NO_COMPONENT_PARTS 
					if (defaultTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) { 
						try { 
							// This will give us a shot at OCR of the text and display of the image parts.
							defaultTemplate = new PositionTemplate(PositionTemplate.TEMPLATE_DEFAULT);
							log.error("Barcode not recongised, but exif contains barcode, reset template to: " + defaultTemplate.getTemplateId());
						} catch (Exception e2) {
						   // We shouldn't end up here - we just asked for the default template by its constant.
						   log.fatal("PositionTemplate doesn't recognize TEMPLATE_DEFAULT");
						   log.trace(e2);
						   ImageCaptureApp.exit(ImageCaptureApp.EXIT_ERROR);
						} 
					}
				}
				
				log.debug("With template:" + defaultTemplate.getTemplateId());
				log.debug("Barcode=" + barcode);

				setPercentComplete(30);


				String warning = "";
				if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
					if (!barcode.equals(exifComment)) {
						warning = "Warning: non-matching QR code barcode and exif Comment";
						System.out.println(warning);	
					}
				}

				Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Loading image.");

				ImageDisplayFrame resultFrame = new ImageDisplayFrame();
					
				if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
					resultFrame.setBarcode("QR="+barcode + " Comment=" + exifComment + " " + warning);
				} else { 
					resultFrame.setBarcode("QR="+barcode);
				}

				try {
					resultFrame.loadImagesFromFile(fileToCheck, defaultTemplate, null);
				} catch (ImageLoadException e2) {
					System.out.println("Error loading image file.");
					System.out.println(e2.getMessage());
				} catch (BadTemplateException e2) {
					System.out.println("Template doesn't match image file.");
					System.out.println(e2.getMessage());
					try {
						try {
							try {
								template = detector.detectTemplateForImage(fileToCheck);
							} catch (UnreadableFileException e3) {
								// TODO Auto-generated catch block
								e3.printStackTrace();
							}
							defaultTemplate = new PositionTemplate(template);
						} catch (NoSuchTemplateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						resultFrame.loadImagesFromFile(fileToCheck, defaultTemplate, null);
					} catch (ImageLoadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadTemplateException e) {
						System.out.println("Template doesn't match image file.");
						System.out.println(e2.getMessage());
					}
				}

				UnitTrayLabel labelRead = null;
				try {
					// Read unitTrayLabelBarcode, failover to OCR and parse UnitTray Label
					rawOCR = "";
					labelRead = scannableFile.getLabelQRText(defaultTemplate);
					if (labelRead==null) { 
						try { 
							labelRead = scannableFile.getLabelQRText(new PositionTemplate("Test template 2"));
						} catch (NoSuchTemplateException e1) {
							try { 
								labelRead = scannableFile.getLabelQRText(new PositionTemplate("Small template 2"));
							} catch (NoSuchTemplateException e2) {
							     log.error("None of " + defaultTemplate.getName() + " Test template 2 or Small template 2 were found");
							}
						}
					} else { 
						log.debug("Translated UnitTrayBarcode to: " + labelRead.toJSONString());
					}
					if (labelRead!=null) { 
						rawOCR = labelRead.toJSONString();
					} else { 
						log.debug("Failing over to OCR with tesseract");
					    rawOCR = scannableFile.getLabelOCRText(defaultTemplate);
					}
					log.debug(rawOCR);
					resultFrame.setRawOCRLabel(rawOCR);
					setPercentComplete(40);
				} catch (Exception ex) { 
					System.out.println(ex.getMessage());	
				}

				setPercentComplete(50);

				resultFrame.pack();
				resultFrame.setVisible(true);
				resultFrame.centerSpecimen();
				resultFrame.center();
				setPercentComplete(60);

				if (persist) {
					// Check that fileToCheck is within imagebase.
					if (!ImageCaptureProperties.isInPathBelowBase(fileToCheck)) { 
						String base = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(
								ImageCaptureProperties.KEY_IMAGEBASE);
						log.error("Tried to scan file ("+ fileToCheck.getPath() +") outside of base image directory (" + base + ")");
						throw new UnreadableFileException("Can't scan and database files outside of base image directory (" + base + ")");
					}
					
					String state = WorkFlowStatus.STAGE_0;
					Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Connecting to database.");
					// try to parse the raw OCR					
					TaxonNameReturner parser = null;
					if (labelRead!=null) { 
						rawOCR = labelRead.toJSONString();
						state = WorkFlowStatus.STAGE_1;
						parser = (TaxonNameReturner)labelRead;
					} else { 
						log.debug("Failing over to OCR with tesseract");
					   rawOCR = scannableFile.getLabelOCRText(defaultTemplate);
						state = WorkFlowStatus.STAGE_0;
					   parser = new UnitTrayLabelParser(rawOCR);
					}
					

					// Case 1: This is an image of papers associated with a container (a unit tray or a box).
					// This case can be identified by there being no barcode data associated with the image.
					// Action: 
					// A) Check the exifComment to see what metadata is there, if blank, bring up a dialog.
					// Options: A drawer, for which number is captured.  A unit tray, capture ?????????.  A specimen
					// where barcode wasn't read, allow capture of barcode and treat as Case 2.
					// B) Create an image record and store the image metadata (with a null specimen_id).  

					boolean isSpecimenImage = false;
					boolean isDrawerImage = false;
					// Test: is exifComment a barcode:
					if (Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(exifComment) || Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(barcode)) { 
						isSpecimenImage = true;
						System.out.println("Specimen Image");
					} else { 
						if (exifComment.matches(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER))) { 
							isDrawerImage = true;
							System.out.println("Drawer Image");
						} else { 
							// Ask.
							System.out.println("Need to ask.");
							WhatsThisImageDialog askDialog = new WhatsThisImageDialog(resultFrame,fileToCheck);
							askDialog.setVisible(true);
							if (askDialog.isSpecimen()) { 
								isSpecimenImage = true;
								exifComment = askDialog.getBarcode();
							}
							if (askDialog.isDrawerImage()) { 
								isDrawerImage = true;
								exifComment = askDialog.getDrawerNumber();
							}
						}
					}

					// applies to both cases.
					ICImageLifeCycle imageCont = new ICImageLifeCycle();
					ICImage tryMe = new ICImage();
					tryMe.setFilename(filename);
					//String path = fileToCheck.getParentFile().getPath();
					String path = ImageCaptureProperties.getPathBelowBase(fileToCheck);              
					//String[] bits = rawOCR.split(":");
					List <ICImage> matches = imageCont.findByExample(tryMe);


					// Case 2: This is an image of a specimen and associated labels or an image assocated with 
					// a specimen with the specimen's barcode label in the image.
					// This case can be identified by there being a barcode in a templated position or there 
					// being a barcode in the exif comment tag.  
					// Action: 
					// A) Check if a specimen record exists, if not, create one from the barcode and OCR data.
					// B) Create an image record and store the image metadata.
					
					// Handle a potential failure case, existing image record without a linked specimen, but which 
					// should have one.
					if (matches.size()==1 && isSpecimenImage) { 
						ICImage existing = imageCont.findById(matches.get(0).getImageId());
						if (existing.getSpecimen()==null) { 
							// If the existing image record has no attached specimen, delete it. 
							// We will create it again from tryMe.
							try {
							    Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Removing existing unlinked image record.");
								imageCont.delete(existing);
							    matches.remove(0);
							} catch (SaveFailedException e) {
								log.error(e.getMessage(), e);
							}
						}
					}

					if (matches.size()==0) {
						String rawBarcode = barcode;
						if (isSpecimenImage) { 
							Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Creating new specimen record.");
							Specimen s = new Specimen();
							if ((!Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(barcode))
									&& Singleton.getSingletonInstance().getBarcodeMatcher().matchesPattern(exifComment)) { 
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
							if (!state.equals(WorkFlowStatus.STAGE_0)) {
								s.setFamily(parser.getFamily());
								s.setSubfamily(parser.getSubfamily());
								s.setTribe(parser.getTribe());
							} else { 
								s.setFamily("");
								// Look up likely matches for the OCR of the higher taxa in the HigherTaxon authority file.
								HigherTaxonLifeCycle hls = new HigherTaxonLifeCycle();
								if (parser.getTribe().trim().equals("")) {	
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
								if (!parser.getFamily().equals(""))  {
									// check family against database (with a soundex match)
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
								sh.persist(s);
							    s.attachNewPart();
							} catch (SpecimenExistsException e) {
								log.debug(e);
								JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
										filename + " " + barcode + " \n" + e.getMessage(), 
										"Specimen Exists, linking Image to existing record.", 
								JOptionPane.ERROR_MESSAGE);
								Specimen checkSpecimen = new Specimen();
								checkSpecimen.setBarcode(barcode);
								List <Specimen> checkResult = sh.findByExample(checkSpecimen);
								if (checkResult.size()==1) { 
									s = checkResult.get(0);
								} 
							} catch (SaveFailedException e) { 
								// Couldn't save, but for some reason other than the
								// specimen record already existing.
								log.debug(e);
								try { 
									Specimen checkSpecimen = new Specimen();
									checkSpecimen.setBarcode(barcode);
									List <Specimen> checkResult = sh.findByExample(checkSpecimen);
									if (checkResult.size()==1) { 
										s = checkResult.get(0);
									} 
									// Drawer number with length limit (and specimen that fails to save at over this length makes
									// a good canary for labels that parse very badly.
									String badParse = "";
									if (((DrawerNameReturner)parser).getDrawerNumber().length()>MetadataRetriever.getFieldLength(Specimen.class, "DrawerNumber")) {
										badParse = "Parsing problem. \nDrawer number is too long: " + s.getDrawerNumber() + "\n";
									}
									JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
											filename + " " + barcode + " \n" + badParse + e.getMessage(), 
											"Badly parsed OCR", 
									JOptionPane.ERROR_MESSAGE);
								} catch (Exception err) {
									log.error(e);
									log.error(err);
									// TODO: Add a general error handling/inform user class.
									// Cause of exception is not likely to be drawer number now that drawer number
									// length is enforced in Specimen.setDrawerNumber, but the text returned by the parser
									// might indicate very poor OCR as a cause.
									String badParse = ((DrawerNameReturner)parser).getDrawerNumber();
									JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
											filename + " " + barcode + "\n" + badParse + e.getMessage(), 
											"Save Failed", 
											JOptionPane.ERROR_MESSAGE);
									s = null;
								}
							}
							setPercentComplete(70);
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
						tryMe.setTemplateId(defaultTemplate.getTemplateId());
						tryMe.setPath(path);
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
							imageCont.persist(tryMe);
						} catch (SaveFailedException e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage());
							e.printStackTrace();
						}

						setPercentComplete(80);
						if (isSpecimenImage) {
							SpecimenControler controler = null;
							try {
								controler = new SpecimenControler(tryMe.getSpecimen());
								controler.setTargetFrame(resultFrame);
							} catch (NoSuchRecordException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SpecimenDetailsViewPane sPane = new SpecimenDetailsViewPane(tryMe.getSpecimen(), controler);
							resultFrame.addWest((JPanel)sPane);
							if (!tryMe.getRawBarcode().equals(tryMe.getRawExifBarcode())) { 
								if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
									// If so configured, warn about missmatch 
									sPane.setWarning("Warning: Scanned Image has missmatch between barcode and comment.");
								}
							}
						}
						resultFrame.center();
					} else { 
						// found one or more matching image records.
						setPercentComplete(80);
						Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Loading existing image record.");
						ICImage existing = imageCont.findById(matches.get(0).getImageId());
						System.out.println(existing.getRawBarcode());
						existing.setRawBarcode(barcode);
						if (isDrawerImage) { 
							existing.setDrawerNumber(exifComment);
						} else { 
							existing.setRawExifBarcode(exifComment);
						}
						existing.setTemplateId(defaultTemplate.getTemplateId());
						if (existing.getPath() == null || existing.getPath().equals("")) { 
							existing.setPath(path);
						}
						if (existing.getDrawerNumber()==null || existing.getDrawerNumber().equals("")) { 
							existing.setDrawerNumber(((DrawerNameReturner)parser).getDrawerNumber());
						}
						try {
							imageCont.attachDirty(existing);
						} catch (SaveFailedException e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage());
							e.printStackTrace();
						}
						if (isSpecimenImage) { 
							SpecimenControler controler = null;
							try {
								controler = new SpecimenControler(existing.getSpecimen());
								controler.setTargetFrame(resultFrame);
								System.out.println(existing.getSpecimen().getBarcode());
							} catch (NullPointerException e1) { 
								log.debug("Specimen barcode not set");
							} catch (NoSuchRecordException e) {
								// Failure case 
								log.error(e.getMessage(), e);
								JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), 
										filename + " " + barcode + "\n" + "Existing Image record with no Specimen Record. " + e.getMessage(), 
										"Save Failed.", 
										JOptionPane.ERROR_MESSAGE);
							}
							SpecimenDetailsViewPane sPane = new SpecimenDetailsViewPane(existing.getSpecimen(), controler);
							resultFrame.addWest((JPanel)sPane);
							resultFrame.center();
							resultFrame.setActiveTab(ImageDisplayFrame.TAB_LABELS);
							resultFrame.fitPinLabels();
							if (!existing.getRawBarcode().equals(existing.getRawExifBarcode())) { 
								if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REDUNDANT_COMMENT_BARCODE).equals("true")) {
									sPane.setWarning("Warning: Scanned Image has missmatch between barcode and comment.");
								}
							}
						}
						setPercentComplete(90);
					}
				}
			} catch (UnreadableFileException e1) {
				log.error("Unable to read selected file." + e1.getMessage());
			} catch (OCRReadException e) {
				log.error("Failed to OCR file." + e.getMessage());
			}
		} else { 
			System.out.println("No file selected from dialog."); 	
		}
		setPercentComplete(100);
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("");
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		Singleton.getSingletonInstance().getMainFrame().setCount(sls.findSpecimenCount());
		Singleton.getSingletonInstance().getJobList().removeJob((RunnableJob)this);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.Runnable#stop()
	 */
	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int percentComplete() {
		return percentComplete;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		start();
	}
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getName()
	 */
	@Override
	public String getName() {
		return "Scan a single file for barcodes";
	}
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startDate;
	}

}
