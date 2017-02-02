/**
 * JobVerbatimFieldLoad.java * edu.harvard.mcz.imagecapture.loader
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

	public JobVerbatimFieldLoad() { 
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
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTLOADPATH)!=null) { 
			fileChooser.setCurrentDirectory(new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_LASTLOADPATH)));
		} 
		
		int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			log.debug("Selected file to load: " + file.getName() + ".");

			if (file.exists() && file.isFile() && file.canRead()) { 
				// Save location
				Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_LASTLOADPATH, file.getPath());
				selectedFilename = file.getName();

				String[] headers = new String[]{};

				try {
					Reader reader = new FileReader(file);

					CSVFormat tabFormat = CSVFormat.newFormat('\t')
							.withIgnoreSurroundingSpaces(true)
							.withHeader(headers)
							.withQuote('"');

					CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers);

					CSVParser csvParser = new CSVParser(reader, csvFormat);

					Map<String,Integer> csvHeader = csvParser.getHeaderMap();
					headers = new String[csvHeader.size()];
					int i = 0;
					for (String header: csvHeader.keySet()) {
						headers[i++] = header;
						log.debug(header);
					}

					List<String> headerList = Arrays.asList(headers);
					if (!headerList.contains("barcode")) { 
						// no barcode field, we can't match the input to specimen records.
						errors.append("Field \"barcode\" not found in csv file headers.  Unable to load data.").append("\n");
					} else { 
						Iterator<CSVRecord> iterator = csvParser.iterator();

						FieldLoader fl = new FieldLoader();

						if (headerList.size()==3 && headerList.contains("verbatimUnclassifiedText") 
								&& headerList.contains("questions") && headerList.contains("barcode")) { 
							// Allowed case 1: unclassified text only

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
							}
							
						} else if (headerList.size()==8 
								 && headerList.contains("verbatimUnclassifiedText") && headerList.contains("questions") && headerList.contains("barcode")
							     && headerList.contains("verbatimLocality") && headerList.contains("verbatimDate") && headerList.contains("verbatimNumbers")
							     && headerList.contains("verbatimCollector") && headerList.contains("verbatimCollection")
								) {
							// allowed case two, transcription into verbatim fields, must be exact list of all verbatim fields.

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
							}

						} else { 
							// allowed case three, transcription into arbitrary sets verbatim or other fields
							// TODO: Support arbitrary column load, without overwriting for absent columns.
							while (iterator.hasNext()) {
								Map<String,String> data = new HashMap<String,String>();
								CSVRecord record = iterator.next();
							    String barcode = record.get("barcode");
							    Iterator<String> hi = headerList.iterator();
							    boolean containsNonVerbatim = false;
							    while (hi.hasNext()) {
							    	String header = hi.next();
							    	if (!header.equals("barcode")) { 
							            data.put(header, record.get(header));
							            if (!header.equals("questions") && MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, header) && MetadataRetriever.isFieldVerbatim(Specimen.class, header) ) { 
							            	containsNonVerbatim = true;
							            }
							    	}
							    }
							    if (data.size()>0) { 
							    	try {
							    		if (containsNonVerbatim) { 
										    fl.loadFromMap(barcode, data, WorkFlowStatus.STAGE_CLASSIFIED, true);
							    		} else { 
										    fl.loadFromMap(barcode, data, WorkFlowStatus.STAGE_VERBATIM, true);
							    		}
									} catch (LoadException e) {
										errors.append("Error loading row ").append(e.getMessage()).append("\n");	
										log.error(e.getMessage(), e);
									}
							    }
							}
						}
					} 

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
		report += "Saved values for " + counter.getSpecimensUpdated() + " specimens.\n";
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

}
