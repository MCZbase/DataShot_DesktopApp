/**
 * JobVerbatimFieldLoad.java 
 *
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
package edu.harvard.mcz.imagecapture.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

import edu.harvard.mcz.imagecapture.jobs.Counter;
import edu.harvard.mcz.imagecapture.jobs.RunnableJobError;
import edu.harvard.mcz.imagecapture.jobs.RunnableJobErrorTableModel;
import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.RunnableJobReportDialog;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;
import edu.harvard.mcz.imagecapture.loader.ex.LoadException;

/**
 * @author mole
 *
 */
public class JobVerbatimFieldLoad  implements RunnableJob, Runnable {
	private static final Log log = LogFactory.getLog(JobVerbatimFieldLoad.class);
	
	private int runStatus = RunStatus.STATUS_NEW;
	private Date startDateTime = null;
	private int percentComplete = 0;
	private List<RunnerListener> listeners = null;
	private Counter counter = null;
	private StringBuffer errors = null;
	private File file = null;

	public JobVerbatimFieldLoad() { 
		init();
	} 
	
	public JobVerbatimFieldLoad(File fileToLoad) { 
		file = fileToLoad;
		init();
	} 	
	
	protected void init() { 
		listeners = new ArrayList<RunnerListener>();
		counter = new Counter();
		runStatus = RunStatus.STATUS_NEW;
		percentComplete = 0;
		startDateTime = null;
		errors = new StringBuffer();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		log.debug(this.toString());
		start();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#start()
	 */
	@Override
	public void start() {
		startDateTime = new Date();
		Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
		runStatus = RunStatus.STATUS_RUNNING;
		
		String selectedFilename = "";
		
		if (file==null) { 
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTLOADPATH)!=null) { 
				fileChooser.setCurrentDirectory(new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTLOADPATH)));
			} 

			int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
			} 
		}
		
		if (file!=null) { 
			log.debug("Selected file to load: " + file.getName() + ".");

			if (file.exists() && file.isFile() && file.canRead()) { 
				// Save location
				Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_LASTLOADPATH, file.getPath());
				selectedFilename = file.getName();

				String[] headers = new String[]{};
				
				CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);
			    int rows = 0;
			    try {
					rows = readRows(file, csvFormat);
			    } catch (FileNotFoundException e) {
			    	JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "Unable to load data, file not found: " + e.getMessage() , "Error: File Not Found", JOptionPane.OK_OPTION);	
			    	errors.append("File not found ").append(e.getMessage()).append("\n");	
			    	log.error(e.getMessage(), e);
			    } catch (IOException e) {
			    	errors.append("Error loading csv format, trying tab delimited: ").append(e.getMessage()).append("\n");	
			    	log.debug(e.getMessage());
			    	try { 
			    		// try reading as tab delimited format, if successful, use that format.
			    		CSVFormat tabFormat = CSVFormat.newFormat('\t')
			    				.withIgnoreSurroundingSpaces(true)
			    				.withHeader(headers)
			    				.withQuote('"');
			    		rows = readRows(file, tabFormat);
			    		csvFormat = tabFormat;
			    	} catch (IOException e1) {  
			    		errors.append("Error Loading data: ").append(e1.getMessage()).append("\n");	
			    		log.error(e.getMessage(), e1);
			    	}
			    }

				try {
					Reader reader = new FileReader(file);

					CSVParser csvParser = new CSVParser(reader, csvFormat);

					Map<String,Integer> csvHeader = csvParser.getHeaderMap();
					headers = new String[csvHeader.size()];
					int i = 0;
					for (String header: csvHeader.keySet()) {
						headers[i++] = header;
						log.debug(header);
					}

					boolean okToRun = true;
					//TODO: Work picking/checking responsibility into a FieldLoaderWizard
					List<String> headerList = Arrays.asList(headers);
					if (!headerList.contains("barcode")) { 
						log.error("Input file " + file.getName()+ " header does not contain required field 'barcode'.");
						// no barcode field, we can't match the input to specimen records.
						errors.append("Field \"barcode\" not found in csv file headers.  Unable to load data.").append("\n");
						okToRun = false;
					}
					
					if (okToRun) { 
						
						Iterator<CSVRecord> iterator = csvParser.iterator();

						FieldLoader fl = new FieldLoader();

						if (headerList.size()==3 && headerList.contains("verbatimUnclassifiedText") 
								&& headerList.contains("questions") && headerList.contains("barcode")) { 
							log.debug("Input file matches case 1: Unclassified text only.");
							// Allowed case 1a: unclassified text only

							int confirm = JOptionPane.showConfirmDialog(Singleton.getSingletonInstance().getMainFrame(),
									"Confirm load from file " + selectedFilename +  " (" + rows + " rows) with just barcode and verbatimUnclassifiedText", "Verbatim unclassified Field found for load", JOptionPane.OK_CANCEL_OPTION);	
							if (confirm == JOptionPane.OK_OPTION) { 
								String barcode = "";
								int lineNumber = 0;
								while (iterator.hasNext()) {
									lineNumber++;
									counter.incrementSpecimens();
									CSVRecord record = iterator.next();
									try { 
										String verbatimUnclassifiedText = record.get("verbatimUnclassifiedText");
										barcode = record.get("barcode");
										String questions = record.get("questions");

										fl.load(barcode, verbatimUnclassifiedText, questions, true);
										counter.incrementSpecimensUpdated();
									} catch (IllegalArgumentException e) {
										RunnableJobError error =  new RunnableJobError(file.getName(), 
												barcode, Integer.toString(lineNumber), 
												e.getClass().getSimpleName(), e, RunnableJobError.TYPE_LOAD_FAILED);
										counter.appendError(error);
										log.error(e.getMessage(), e);
									} catch (LoadException e) {
										RunnableJobError error =  new RunnableJobError(file.getName(), 
												barcode, Integer.toString(lineNumber), 
												e.getClass().getSimpleName(), e, RunnableJobError.TYPE_LOAD_FAILED);
										counter.appendError(error);
										log.error(e.getMessage(), e);
									}
									percentComplete = (int) ((lineNumber*100f)/rows);
									this.setPercentComplete(percentComplete);
								}
							} else { 
								errors.append("Load canceled by user.").append("\n");	
							}
						} else if (headerList.size()==4 && headerList.contains("verbatimUnclassifiedText") 
								&& headerList.contains("questions") && headerList.contains("barcode")
								&& headerList.contains("verbatimClusterIdentifier")) { 
							log.debug("Input file matches case 1: Unclassified text only (with cluster identifier).");
							// Allowed case 1b: unclassified text only (including cluster identifier)

							int confirm = JOptionPane.showConfirmDialog(Singleton.getSingletonInstance().getMainFrame(),
									"Confirm load from file " + selectedFilename +  " (" + rows + " rows) with just barcode and verbatimUnclassifiedText", "Verbatim unclassified Field found for load", JOptionPane.OK_CANCEL_OPTION);	
							if (confirm == JOptionPane.OK_OPTION) { 
								String barcode = "";
								int lineNumber = 0;
								while (iterator.hasNext()) {
									lineNumber++;
									counter.incrementSpecimens();
									CSVRecord record = iterator.next();
									try { 
										String verbatimUnclassifiedText = record.get("verbatimUnclassifiedText");
										String verbatimClusterIdentifier = record.get("verbatimClusterIdentifier"); 
										barcode = record.get("barcode");
										String questions = record.get("questions");

										fl.load(barcode, verbatimUnclassifiedText, verbatimClusterIdentifier, questions, true);
										counter.incrementSpecimensUpdated();
									} catch (IllegalArgumentException e) {
										RunnableJobError error =  new RunnableJobError(file.getName(), 
												barcode, Integer.toString(lineNumber), 
												e.getClass().getSimpleName(), e, RunnableJobError.TYPE_LOAD_FAILED);
										counter.appendError(error);
										log.error(e.getMessage(), e);
									} catch (LoadException e) {
										RunnableJobError error =  new RunnableJobError(file.getName(), 
												barcode, Integer.toString(lineNumber), 
												e.getClass().getSimpleName(), e, RunnableJobError.TYPE_LOAD_FAILED);
										counter.appendError(error);
										log.error(e.getMessage(), e);
									}
									percentComplete = (int) ((lineNumber*100f)/rows);
									this.setPercentComplete(percentComplete);
								}
							} else { 
								errors.append("Load canceled by user.").append("\n");	
							}
							
						} else if (headerList.size()==8 
								 && headerList.contains("verbatimUnclassifiedText") && headerList.contains("questions") && headerList.contains("barcode")
							     && headerList.contains("verbatimLocality") && headerList.contains("verbatimDate") && headerList.contains("verbatimNumbers")
							     && headerList.contains("verbatimCollector") && headerList.contains("verbatimCollection")
								) {
							// Allowed case two, transcription into verbatim fields, must be exact list of all
							// verbatim fields, not including cluster identifier or other metadata.
							log.debug("Input file matches case 2: Full list of verbatim fields.");

							int confirm = JOptionPane.showConfirmDialog(Singleton.getSingletonInstance().getMainFrame(),
									"Confirm load from file " + selectedFilename +  " (" + rows + " rows) with just barcode and verbatim fields.", "Verbatim Fields found for load", JOptionPane.OK_CANCEL_OPTION);	
							if (confirm == JOptionPane.OK_OPTION) { 

								String barcode = "";
								int lineNumber = 0;
								while (iterator.hasNext()) {
									lineNumber++;
									counter.incrementSpecimens();
									 CSVRecord record = iterator.next();
									try { 
										String verbatimLocality = record.get("verbatimLocality");
										String verbatimDate = record.get("verbatimDate");
										String verbatimCollector = record.get("verbatimCollector");
										String verbatimCollection = record.get("verbatimCollection");
										String verbatimNumbers = record.get("verbatimNumbers");
										String verbatimUnclasifiedText = record.get("verbatimUnclassifiedText");
										barcode = record.get("barcode");
										String questions = record.get("questions");

										fl.load(barcode, verbatimLocality, verbatimDate, verbatimCollector, verbatimCollection, verbatimNumbers, verbatimUnclasifiedText, questions);
										counter.incrementSpecimensUpdated();
									} catch (IllegalArgumentException e) {
										RunnableJobError error =  new RunnableJobError(file.getName(), 
												barcode, Integer.toString(lineNumber), 
												e.getClass().getSimpleName(), e, RunnableJobError.TYPE_LOAD_FAILED);
										counter.appendError(error);
										log.error(e.getMessage(), e);
									} catch (LoadException e) {
										RunnableJobError error =  new RunnableJobError(file.getName(), 
												barcode, Integer.toString(lineNumber), 
												e.getClass().getSimpleName(), e, RunnableJobError.TYPE_LOAD_FAILED);
										counter.appendError(error);
										log.error(e.getMessage(), e);
									}
									percentComplete = (int) ((lineNumber*100f)/rows);
									this.setPercentComplete(percentComplete);
								}
							} else { 
								errors.append("Load canceled by user.").append("\n");	
							}
							
						} else { 
							// allowed case three, transcription into arbitrary sets verbatim or other fields
							log.debug("Input file case 3: Arbitrary set of fields.");
							
							// Check column headers before starting run.
							boolean headersOK = false;
							
							try {
								HeaderCheckResult headerCheck = fl.checkHeaderList(headerList);
								if (headerCheck.isResult()) { 
									int confirm = JOptionPane.showConfirmDialog(Singleton.getSingletonInstance().getMainFrame(),
											"Confirm load from file " + selectedFilename +  " (" + rows + " rows) with headers: \n" + headerCheck.getMessage().replaceAll(":", ":\n") , "Fields found for load", JOptionPane.OK_CANCEL_OPTION);	
									if (confirm == JOptionPane.OK_OPTION) { 
										headersOK = true;
									} else { 
										errors.append("Load canceled by user.").append("\n");	
									}
								} else { 
									int confirm = JOptionPane.showConfirmDialog(Singleton.getSingletonInstance().getMainFrame(),
											"Problem found with headers in file, try to load anyway?\nHeaders: \n" + headerCheck.getMessage().replaceAll(":", ":\n") , "Problem in fields for load", JOptionPane.OK_CANCEL_OPTION);	
									if (confirm == JOptionPane.OK_OPTION) { 
										headersOK = true;
									} else { 
										errors.append("Load canceled by user.").append("\n");	
									}
								}
							} catch (LoadException e) { 
								errors.append("Error loading data: \n").append(e.getMessage()).append("\n");
								JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), e.getMessage().replaceAll(":", ":\n"), "Error Loading Data: Problem Fields", JOptionPane.ERROR_MESSAGE);
								
								log.error(e.getMessage(), e);
							} 
							
							if (headersOK) { 
								int lineNumber = 0;
								while (iterator.hasNext()) {
									lineNumber++;
									Map<String,String> data = new HashMap<String,String>();
									CSVRecord record = iterator.next();
									String barcode = record.get("barcode");
									Iterator<String> hi = headerList.iterator();
									boolean containsNonVerbatim = false;
									while (hi.hasNext()) {
										String header = hi.next();
										// Skip any fields prefixed by the underscore character _
										if (!header.equals("barcode") && !header.startsWith("_")) { 
											data.put(header, record.get(header));
											if (!header.equals("questions") && MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, header) && MetadataRetriever.isFieldVerbatim(Specimen.class, header) ) { 
												containsNonVerbatim = true;
											}
										}
									}
									if (data.size()>0) { 
										try {
											boolean updated = false;
											if (containsNonVerbatim) { 
												updated = fl.loadFromMap(barcode, data, WorkFlowStatus.STAGE_CLASSIFIED, true);
											} else { 
												updated = fl.loadFromMap(barcode, data, WorkFlowStatus.STAGE_VERBATIM, true);
											}
											counter.incrementSpecimens();
											if (updated) { 
											    counter.incrementSpecimensUpdated();
											}
										} catch (HibernateException e1) {
											// Catch (should just be development) problems with the underlying query 
											StringBuilder message = new StringBuilder();
											message.append("Query Error loading row (").append(lineNumber).append(")[").append(barcode).append("]").append(e1.getMessage());
											RunnableJobError err = new RunnableJobError(selectedFilename, barcode, Integer.toString(lineNumber), e1.getMessage(), e1,  RunnableJobError.TYPE_LOAD_FAILED);
											counter.appendError(err);
											log.error(e1.getMessage(), e1);
											
										} catch (LoadException e) {
											StringBuilder message = new StringBuilder();
											message.append("Error loading row (").append(lineNumber).append(")[").append(barcode).append("]").append(e.getMessage());
											
											RunnableJobError err = new RunnableJobError(selectedFilename, barcode, Integer.toString(lineNumber), e.getMessage(), e,  RunnableJobError.TYPE_LOAD_FAILED);
											
											counter.appendError(err);
											// errors.append(message.append("\n").toString());
											log.error(e.getMessage(), e);
										}
									}
									percentComplete = (int) ((lineNumber*100f)/rows);
									this.setPercentComplete(percentComplete);
								} 
							} else { 
								String message = "Can't load data, problem with headers.";	
								errors.append(message).append("\n");	
								log.error(message);
							}
						}
					} 
                    csvParser.close();
                    reader.close();
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(Singleton.getSingletonInstance().getMainFrame(), "Unable to load data, file not found: " + e.getMessage() , "Error: File Not Found", JOptionPane.OK_OPTION);	
					errors.append("File not found ").append(e.getMessage()).append("\n");	
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					errors.append("Error Loading data: ").append(e.getMessage()).append("\n");	
					log.error(e.getMessage(), e);
				}
			}
			
		} else {
			//TODO: handle error condition
			log.error("File selection cancelled by user.");
		}		
		
		report(selectedFilename);
		done();
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
		runStatus = RunStatus.STATUS_TERMINATED;
		log.debug(this.getClass().getSimpleName() + " " + this.toString() +  "  recieved cancel signal");
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
	public boolean registerListener(RunnerListener aJobListener) {
		return listeners.add(aJobListener);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getName()
	 */
	@Override
	public String getName() {
		return "Ingest Data obtained from an external process";
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startDateTime;
	}
	
	/**
	 * Cleanup when job is complete.
	 */
	protected void done() { 
	    runStatus = RunStatus.STATUS_DONE;
		notifyListeners(RunStatus.STATUS_DONE);
		Singleton.getSingletonInstance().getJobList().removeJob((RunnableJob)this);
	}	
	
	private void report(String selectedFilename) { 
		String report = "Results for loading data from file " + selectedFilename + ".\n";
		report += "Found  " + counter.getSpecimens() + " rows in input file.\n";
		report += "Examined " + counter.getSpecimens() + " specimens.\n";
		report += "Saved updated values for " + counter.getSpecimensUpdated() + " specimens.\n";
		report += errors.toString();
		Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Load data from file complete.");
		RunnableJobReportDialog errorReportDialog = new RunnableJobReportDialog(
				Singleton.getSingletonInstance().getMainFrame(),
				report, counter.getErrors(),
				RunnableJobErrorTableModel.TYPE_LOAD,
				"Load Data from file Report"
				);
		errorReportDialog.setVisible(true);
	}
	
	protected void notifyListeners(int anEvent) { 
		Singleton.getSingletonInstance().getMainFrame().notifyListener(anEvent, this);
		Iterator<RunnerListener> i = listeners.iterator();
		while (i.hasNext()) { 
			i.next().notifyListener(anEvent, this);
		}
	}
	
	protected void setPercentComplete(int aPercentage) { 
		//set value
		percentComplete = aPercentage;
		log.debug(percentComplete);
		//notify listeners
        notifyListeners(percentComplete);
	}		
	
	/**
	 * Attempt to read file with a given CSV format, and if successful, return
	 * the number of rows in the file.
	 * 
	 * @param file to check for csv rows.
	 * @param formatToTry the CSV format to try to read the file with.
	 * @return number of rows in the file.
	 * @throws IOException on a problem reading the header.
	 * @throws FileNotFoundException on not finding the file.
	 */
	protected int readRows(File file, CSVFormat formatToTry) throws IOException, FileNotFoundException {
		int rows = 0;
    	Reader reader = new FileReader(file);

		CSVParser csvParser = new CSVParser(reader, formatToTry);
		Iterator<CSVRecord> iterator = csvParser.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			rows++;
		}
        csvParser.close();
        reader.close();
        return rows;
	}

}
