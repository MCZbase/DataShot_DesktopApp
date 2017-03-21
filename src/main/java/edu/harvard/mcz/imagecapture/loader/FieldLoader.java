/**
 * FieldLoader.java
 * edu.harvard.mcz.imagecapture.loader
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.CollectorLifeCycle;
import edu.harvard.mcz.imagecapture.data.ExternalHistory;
import edu.harvard.mcz.imagecapture.data.ExternalHistoryLifeCycle;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.Number;
import edu.harvard.mcz.imagecapture.data.NumberLifeCycle;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.loader.ex.LoadException;
import edu.harvard.mcz.imagecapture.loader.ex.LoadTargetMovedOnException;
import edu.harvard.mcz.imagecapture.loader.ex.LoadTargetPopulatedException;
import edu.harvard.mcz.imagecapture.loader.ex.LoadTargetRecordNotFoundException;
import edu.harvard.mcz.imagecapture.loader.ex.LoadTargetSaveException;

/**
 * @author mole
 *
 */
public class FieldLoader {
	private static final Log log = LogFactory.getLog(FieldLoader.class);
	
	protected SpecimenLifeCycle sls = null;
	
	protected Map<String,String> knownFields;
	
	public FieldLoader() { 
		init();
	}
	
	/**
	 * Setup initial conditions, construct list of known fields into which data can be put.
	 */
	protected void init() {
		sls = new SpecimenLifeCycle();

		// Key: lower case of field, Value actual case of Field.
		knownFields = new HashMap<String,String>();
		Method[] specimenMethods = Specimen.class.getDeclaredMethods();
		for (int j=0; j<specimenMethods.length; j++) { 
			if (specimenMethods[j].getName().startsWith("set") &&
					specimenMethods[j].getParameterTypes().length==1 &&  
				    specimenMethods[j].getParameterTypes()[0].getName().equals(String.class.getName()) 
			) { 
			    knownFields.put(specimenMethods[j].getName().replaceAll("^set", "").toLowerCase(), specimenMethods[j].getName().replaceAll("^set", ""));
			}
		}
		// List of input fields that will need to be parsed into relational tables
		ArrayList<String> toParseFields = new ArrayList<String>();
		toParseFields.add("collectors");
		toParseFields.add("numbers");
		Iterator<String> ipf = toParseFields.iterator();
		while(ipf.hasNext()) { 
			String parseField = ipf.next();
			knownFields.put(parseField.toLowerCase(), parseField);
		}
		
		if (log.isDebugEnabled()) { 
		   Iterator<String> i = knownFields.keySet().iterator();
		   while (i.hasNext()) { 
			   log.debug(i.next());
		   }
		}		
	
	}
	
	/**
	 * Check whether or not a header is in the list of known fields.
	 * 
	 * @param possibleField header to check 
	 * @return true if possibleField is (case insensitive) in the list of known
	 *   fields, false if not.  Throws a null pointer exception if possibleField is null.
	 */
	public boolean isFieldKnown(String possibleField) { 
		return knownFields.containsKey(possibleField.toLowerCase());
	}
	
	/**
	 * Given a barcode number and a value for verbatimUnclassifiedText, update the verbatim value for the matching
	 * Specimen record, leaves the Specimen record in workflow state WorkFlowStatus.STAGE_VERBATIM.
	 * 
	 * @param barcode must match exactly one Specimen record.
	 * @param verbatimUnclassifiedText value for this field in Specimen.
	 * @param questions value to append to this field in Specimen.
	 * @param overwriteExisting if true, overwrite any value of verbatimUnclassifiedText in the matching Specimen record.
	 * @return if the new value was saved 
	 * 
	 * @throws LoadException on an error
	 */
	public boolean load(String barcode, String verbatimUnclassifiedText, String verbatimClusterIdentifier, String questions, boolean overwriteExisting) throws LoadException { 
		boolean result = false;
		
		List<Specimen> matches = sls.findByBarcode(barcode);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);
			if (!WorkFlowStatus.allowsVerbatimUpdate(match.getWorkFlowStatus())) { 
				throw new LoadTargetMovedOnException();
			} else { 	

				if (match.getVerbatimUnclassifiedText()==null || match.getVerbatimUnclassifiedText().trim().length()==0 || overwriteExisting) {
					match.setVerbatimUnclassifiedText(verbatimUnclassifiedText);
				}  else { 
					throw new LoadTargetPopulatedException(); 
				}
				
				match.setVerbatimClusterIdentifier(verbatimClusterIdentifier);
				
				// append any questions to current questions.
				if (questions!=null && questions.trim().length() > 0 ) { 
					String currentQuestions = match.getQuestions();
					if (currentQuestions==null) { currentQuestions = ""; } 
					if (currentQuestions.trim().length()>0) { currentQuestions = currentQuestions + " | "; }
					match.setQuestions(currentQuestions + questions);
				}
				
				match.setWorkFlowStatus(WorkFlowStatus.STAGE_VERBATIM);

				try {
					sls.attachDirty(match);
					result = true;
					logHistory(match,"Load:"+WorkFlowStatus.STAGE_VERBATIM+":VerbatimUnclassifiedText",new Date());
				} catch (SaveFailedException e) {
					log.error(e.getMessage(), e);
					throw new LoadTargetSaveException("Error saving updated target record: " + e.getMessage());
				}
			}
		} else { 
			throw new LoadTargetRecordNotFoundException();
		}
		
		return result;
	}
	
	/**
	 * Give a barcode number and the set of verbatim fields, attempt to set the values for those verbatim fields for a record.
	 * Does not overwrite existing non-empty values, does not modify record if any verbatim field contains a value.
	 * 
	 * @param barcode field, must match on exactly one Specimen record.
	 * @param verbatimLocality value for this field in Specimen.
	 * @param verbatimDate value for this field in Specimen.
	 * @param verbatimCollector value for this field in Specimen.
	 * @param verbatimCollection value for this field in Specimen.
	 * @param verbatimNumbers value for this field in Specimen.
	 * @param verbatimUnclassifiedText value for this field in Specimen.
	 * @param questions value to append to this field in Specimen.
	 * 
	 * @return true if record with the provided barcode number was updated.
	 * @throws LoadException on an error, including any existing value for any of the verbatim fields.
	 */
	public boolean load(String barcode, String verbatimLocality, String verbatimDate, String verbatimCollector, String verbatimCollection, String verbatimNumbers, String verbatimUnclassifiedText, String questions) throws LoadException { 
		boolean result = false;
		
		List<Specimen> matches = sls.findByBarcode(barcode);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);
			if (match.getWorkFlowStatus().equals(WorkFlowStatus.STAGE_0) || match.getWorkFlowStatus().equals(WorkFlowStatus.STAGE_1) ) { 
				if (match.getVerbatimLocality()==null || match.getVerbatimLocality().trim().length()==0) {
					match.setVerbatimLocality(verbatimLocality);
				}  else { throw new LoadTargetPopulatedException(); }
				
				if (match.getDateNos()==null || match.getDateNos().trim().length()==0) {
					match.setDateNos(verbatimDate);
				}  else { throw new LoadTargetPopulatedException(); }
				
				if (match.getVerbatimCollector()==null || match.getVerbatimCollector().trim().length()==0) {
					match.setVerbatimCollector(verbatimCollector);
				}  else { throw new LoadTargetPopulatedException(); }
				
				if (match.getVerbatimCollection()==null || match.getVerbatimCollection().trim().length()==0) {
					match.setVerbatimCollection(verbatimCollection);
				}  else { throw new LoadTargetPopulatedException(); }	
				
				if (match.getVerbatimNumbers()==null || match.getVerbatimNumbers().trim().length()==0) {
					match.setVerbatimNumbers(verbatimNumbers);
				}  else { throw new LoadTargetPopulatedException(); }						
				
				if (match.getVerbatimUnclassifiedText()==null || match.getVerbatimUnclassifiedText().trim().length()==0) {
					match.setVerbatimUnclassifiedText(verbatimUnclassifiedText);
				}  else { throw new LoadTargetPopulatedException(); }
				
				// append any questions to current questions.
				if (questions!=null && questions.trim().length() > 0 ) { 
					String currentQuestions = match.getQuestions();
					if (currentQuestions==null) { currentQuestions = ""; } 
					if (currentQuestions.trim().length()>0) { currentQuestions = currentQuestions + " | "; }
					match.setQuestions(currentQuestions + questions);
				}
				
				match.setWorkFlowStatus(WorkFlowStatus.STAGE_VERBATIM);

				try {
					sls.attachDirty(match);
					
					logHistory(match,"VerbatimFields:" + WorkFlowStatus.STAGE_VERBATIM + ":",new Date());
				} catch (SaveFailedException e) {
					log.error(e.getMessage(), e);
					throw new LoadTargetSaveException("Error saving updated target record: " + e.getMessage());
				}
			} else {
				throw new LoadTargetMovedOnException();
			}
		} else { 
			throw new LoadTargetRecordNotFoundException();
		}
		
		return result;
	}
	
	/**
	 * Give a barcode number and an arbitrary set of fields in Specimen, attempt to set the values for those fields for a record.
	 * 
	 * @param barcode field, must match on exactly one Specimen record.
	 * @param data map of field names and data values
	 * @param questions value to append to this field in Specimen.
	 * @param newWorkflowStatus to set Specimen.workflowStatus to.
	 * @return true if one or more fields were updated.
	 * 
	 * @throws LoadException on an error (particularly from inability to map keys in data to fields in Specimen.
	 */
	public boolean loadFromMap(String barcode, Map<String,String> data, String newWorkflowStatus, boolean allowUpdateExistingVerbatim) throws LoadException { 
		boolean result = false;
		log.debug(barcode);
		
		// Check that the proposed new state is allowed.
		if (newWorkflowStatus==null || 
				(!newWorkflowStatus.equals(WorkFlowStatus.STAGE_VERBATIM) && 
				 !newWorkflowStatus.equals(WorkFlowStatus.STAGE_CLASSIFIED))
			) 
		{ 
		   throw new LoadException("Trying to load into unallowed new state." + newWorkflowStatus);	
		}
		
		List<Specimen> matches = sls.findByBarcode(barcode);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);		
		
			if  (
					(newWorkflowStatus.equals(WorkFlowStatus.STAGE_VERBATIM)&& !WorkFlowStatus.allowsVerbatimUpdate(match.getWorkFlowStatus()))
					|| 
					(newWorkflowStatus.equals(WorkFlowStatus.STAGE_CLASSIFIED) && !WorkFlowStatus.allowsClassifiedUpdate(match.getWorkFlowStatus()))
				) 
			{
				// The target Specimen record has moved on past the state where it can be altered by a data load.
				throw new LoadTargetMovedOnException(barcode + " is in state " + match.getWorkFlowStatus() + " and can't be altered by this data load.");
			} else { 	
                // Target Specimen record is eligible to be updated by a data load.
				boolean foundData = false;
				boolean hasExternalWorkflowProcess = false;
				boolean hasExternalWorkflowDate = false;
				
				Iterator<String> i = data.keySet().iterator();
				String separator = "";
				StringBuilder keys = new StringBuilder();
				while (i.hasNext()) { 
					// Iterate through list of keys in input data
					String key = i.next();
					log.debug(knownFields.containsKey(key.toLowerCase()));
					// Check that key is known.
					if (!knownFields.containsKey(key.toLowerCase())) {  
						throw new LoadException("Column " + key + " is not a known field of Specimen.");
					}
					// Check that key allows updates
					if (knownFields.containsKey(key.toLowerCase()) 
							&& !key.equals("barcode") 
							&& (
							   key.toLowerCase().equals("collectors") || key.toLowerCase().equals("numbers") || 
							   MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, key)
							)
				       ) 
					{ 
						keys.append(separator).append(key);
						separator = ",";
						String datavalue = data.get(key);
						log.debug(key);
						log.debug(datavalue);

						Method setMethod;
						try {
							if (key.toLowerCase().equals("collectors")) { 
								// Special case, parse collectors to associated Collector table.
								datavalue = datavalue + "|";
								log.debug(datavalue);
								String[] collectors = datavalue.split("\\|", 0);
								log.debug(collectors.length);
								for (int j=0; j<collectors.length; j++) { 
									String collector = collectors[j];
									log.debug(collector);
									if (collector.trim().length()>0) {
										// Check to see if Collector exists
										Set<Collector> existingCollectors = match.getCollectors();
										Iterator<Collector> ic = existingCollectors.iterator();
										boolean exists = false;
										while (ic.hasNext()) { 
											Collector c = ic.next();
											if (c.getCollectorName().equals(collector)) { 
												exists = true;
											}
										}
										if (!exists) { 
											// only add if it isn't allready present.
											Collector col = new Collector();
											col.setSpecimen(match);
											col.setCollectorName(collector);
											match.getCollectors().add(col);
											foundData = true;
										}
									}
								}
							} else if (key.toLowerCase().equals("numbers")) { 
								// Special case, parse numbers to associated Number table.
								datavalue = datavalue + "|";
								String[] numbers = datavalue.split("\\|", 0);
								for (int j=0; j<numbers.length; j++) { 
									String numberKV = numbers[j];
									if (numberKV.trim().length()>0) {
										String number = numberKV;
										String numType = "unknown";
										if (numberKV.contains(":")) { 
											String[] numbits =  numberKV.split(":",0);
											number = numbits[0];
											numType= numbits[1];
											if (numType==null || numType.trim().length()==0) { 
												numType = "unknown";
											}
										}
										// check to see if number exists
										Set<Number> existingNumbers = match.getNumbers();
										Iterator<Number> ic = existingNumbers.iterator();
										boolean exists = false;
										while (ic.hasNext()) { 
											Number c = ic.next();
											if (c.getNumber().equals(number) || c.getNumberType().equals(numType)) { 
												exists = true;
											}
										}
										if (!exists) { 
											// only add if it isn't already present.
											Number num = new Number();
											num.setNumber(number);
											num.setNumberType(numType);
											num.setSpecimen(match);
											match.getNumbers().add(num);
											foundData = true;
										}
									}
								}
								
							} else { 
								// Find the Specimen get and set methods for the current key
								String keyProper = knownFields.get(key.toLowerCase());
								setMethod = Specimen.class.getMethod("set" + keyProper, String.class);
								Method getMethod = Specimen.class.getMethod("get" + keyProper, null);
								// Obtain the current value in the Specimen record for the field matching the current key.
								String currentValue = (String) getMethod.invoke(match, null);
								// Assess whether changes to existing data are allowed for that field, make them only if they are allowed.
								if (key.equals("externalworkflowprocess")) { hasExternalWorkflowProcess = true; }
								if (key.equals("externalworkflowdate")) { hasExternalWorkflowDate = true; }
								if (key.equals("questions")) {
									// append
									if (currentValue !=null && currentValue.trim().length()>0) { 
										datavalue = currentValue + " | " + datavalue;
									}
									setMethod.invoke(match, datavalue);
									foundData = true;
								} else if (key.equals("externalworkflowprocess") || key.equals("externalworkflowdate") || key.equals("verbatimclusteridentifier")) {
									// overwrite existing metadata
									setMethod.invoke(match, datavalue);
									foundData = true;
								} else { 
									// overwrite verbatim fields if update is allowed, otherwise no overwite of existing data.
									if (currentValue==null || currentValue.trim().length()==0) { 
										setMethod.invoke(match, datavalue);
										foundData = true;
									} else if (MetadataRetriever.isFieldVerbatim(Specimen.class,key) && allowUpdateExistingVerbatim) { 
										setMethod.invoke(match, datavalue);
										foundData = true;
									}
								}
							}

						} catch (NoSuchMethodException e) {
							log.error(e.getMessage(),e);
							throw new LoadException(e.toString() + " " + e.getMessage());
						} catch (SecurityException e) {
							log.error(e.getMessage(),e);
							throw new LoadException(e.getMessage());
						} catch (IllegalAccessException e) {
							log.error(e.getMessage(),e);
							throw new LoadException(e.getMessage());
						} catch (IllegalArgumentException e) {
							log.error(e.getMessage(),e);
							throw new LoadException(e.getMessage());
						} catch (InvocationTargetException e) {
							log.error(e.getMessage(),e);
							throw new LoadException(e.getMessage());
						}
					} else { 
						throw new LoadException("Column " + key + " is not an externally updatable field of Specimen.");
					}
				}

				if (foundData) { 
					try {
						// save the updated specimen record
						match.setWorkFlowStatus(newWorkflowStatus);
						log.debug("Updating:" + match.getBarcode() );
						sls.attachDirty(match);
						result = true;
						
						// If we were provided 
						String ewProcess = "ArbitraryFieldLoad:" + match.getWorkFlowStatus() + ":" + keys.toString();
						if (hasExternalWorkflowProcess) { 
							ewProcess = match.getExternalWorkflowProcess();
						}
						Date ewDate = new Date();
						if (hasExternalWorkflowDate) { 
							ewDate = match.getExternalWorkflowDate();
						}
						
						logHistory(match,ewProcess,ewDate);
						
					} catch (SaveFailedException e) {
						log.error(e.getMessage(), e);
						throw new LoadTargetSaveException();
					}
				}
			}			
			
		}
		
		return result;
	}
	
	protected void logHistory(Specimen match, String externalWorkflowProcess, Date externalWorkflowDate) { 
		try { 
			// log the external data import
			ExternalHistory history = new ExternalHistory();
			history.setExternalWorkflowProcess(externalWorkflowProcess);
			history.setExternalWorkflowDate(externalWorkflowDate);
			history.setSpecimen(match);
			ExternalHistoryLifeCycle els = new ExternalHistoryLifeCycle();
			els.attachDirty(history);
		} catch (SaveFailedException ex) { 
			log.error(ex.getMessage(),ex);
		}
	}
	
}
