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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
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
	
	public FieldLoader() { 
		init();
	}
	
	protected void init() {
		sls = new SpecimenLifeCycle();
	}
	
	/**
	 * Given a barcode number and a value for verbatimUnclassifiedText, update the verbatim value for the matching
	 * Specimen record.
	 * 
	 * @param barcode must match exactly one Specimen record.
	 * @param verbatimUnclassifiedText value for this field in Specimen.
	 * @param questions value to append to this field in Specimen.
	 * @param overwriteExisting if true, overwrite any value of verbatimUnclassifiedText in the matching Specimen record.
	 * @return if the new value was saved 
	 * 
	 * @throws LoadException on an error
	 */
	public boolean load(String barcode, String verbatimUnclassifiedText, String questions, boolean overwriteExisting) throws LoadException { 
		boolean result = false;
		
		Specimen pattern = new Specimen();
		pattern.setBarcode(barcode);
		
		List<Specimen> matches = sls.findByExample(pattern);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);
			if (!WorkFlowStatus.allowsVerbatimUpdate(match.getWorkFlowStatus())) { 
				throw new LoadTargetMovedOnException();
			} else { 	

				if (match.getVerbatimUnclassifiedText()==null || match.getVerbatimUnclassifiedText().trim().length()==0 || overwriteExisting) {
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
					result = true;
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
	 * Does not overwrite any existing non-empty values.
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
	 * @throws LoadException on an error.
	 */
	public boolean load(String barcode, String verbatimLocality, String verbatimDate, String verbatimCollector, String verbatimCollection, String verbatimNumbers, String verbatimUnclassifiedText, String questions) throws LoadException { 
		boolean result = false;
		
		Specimen pattern = new Specimen();
		pattern.setBarcode(barcode);
		
		List<Specimen> matches = sls.findByExample(pattern);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);
			if (!WorkFlowStatus.allowsVerbatimUpdate(match.getWorkFlowStatus())) { 
				throw new LoadTargetMovedOnException();
			} else { 	

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
		
		ArrayList<String> knownFields = new ArrayList<String>();
		Method[] specimenMethods = Specimen.class.getDeclaredMethods();
		for (int j=0; j<specimenMethods.length; j++) { 
			if (specimenMethods[j].getName().startsWith("set") &&
					specimenMethods[j].getParameterTypes().length==1 &&  
				    specimenMethods[j].getParameterTypes()[0].getName().equals(String.class.getName()) 
			) { 
			    knownFields.add(specimenMethods[j].getName().replaceAll("^set", ""));
			}
		}
		// List of input fields that will need to be parsed into relational tables
		ArrayList<String> toParseFields = new ArrayList<String>();
		toParseFields.add("collectors");
		toParseFields.add("numbers");
		// TODO: Add support for parsing collectors and numbers from input data
//		knownFields.addAll(toParseFields);
		
		
		Specimen pattern = new Specimen();
		pattern.setBarcode(barcode);
		
		List<Specimen> matches = sls.findByExample(pattern);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);		
		
			if (!WorkFlowStatus.allowsVerbatimUpdate(match.getWorkFlowStatus())) { 
				throw new LoadTargetMovedOnException();
			} else { 	

				boolean foundData = false;
				
				Iterator<String> i = data.keySet().iterator();
				while (i.hasNext()) { 
					String key = i.next().toLowerCase();
					if (knownFields.contains(key) && !key.equals("barcode") && MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, key)) { 
						String datavalue = data.get(key);

						Method setMethod;
						try {
							setMethod = Specimen.class.getMethod("set" + key, String.class);

							Method getMethod = Specimen.class.getMethod("get" + key, null);

							String currentValue = (String) getMethod.invoke(match, null);

							if (key.equals("questions")) {
								// append
								if (currentValue !=null && currentValue.trim().length()>0) { 
									datavalue = currentValue + " | " + datavalue;
								}
								setMethod.invoke(match, datavalue);
								foundData = true;
							} else if (key.equals("collectors")) { 
								String[] collectors = datavalue.split("\\|", 0);
								for (int j=0; j<collectors.length; j++) { 
									String collector = collectors[j];
									if (collector.trim().length()>0) { 
										Collector col = new Collector();
										col.setSpecimen(match);
										col.setCollectorName(collector);
										// TODO: persist 
									}
								}
							} else if (key.equals("numbers")) { 
								String[] numbers = datavalue.split("\\|", 0);
								for (int j=0; j<numbers.length; j++) { 
									String numberKV = numbers[j];
									if (numberKV.trim().length()>0) { 
										// TODO: Split into number type and number, then add a number to specimen.
									}
								}
								
							} else { 
							    if (currentValue==null || currentValue.trim().length()==0) { 
								   setMethod.invoke(match, datavalue);
								   foundData = true;
							    } else if (MetadataRetriever.isFieldVerbatim(Specimen.class,key) && allowUpdateExistingVerbatim) { 
								   setMethod.invoke(match, datavalue);
								   foundData = true;
							    }
							}

						} catch (NoSuchMethodException e) {
							throw new LoadException(e.getMessage());
						} catch (SecurityException e) {
							throw new LoadException(e.getMessage());
						} catch (IllegalAccessException e) {
							throw new LoadException(e.getMessage());
						} catch (IllegalArgumentException e) {
							throw new LoadException(e.getMessage());
						} catch (InvocationTargetException e) {
							throw new LoadException(e.getMessage());
						}
					} else { 
						throw new LoadException("Column " + key + " is not a field of Specimen.");
					}
				}

				if (foundData) { 
					try {
						match.setWorkFlowStatus(newWorkflowStatus);
						
						sls.attachDirty(match);
						result = true;
					} catch (SaveFailedException e) {
						log.error(e.getMessage(), e);
						throw new LoadTargetSaveException();
					}
				}
			}			
			
		}
		
		return result;
	}
}
