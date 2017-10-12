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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.date.DateUtils;
import org.filteredpush.qc.date.EventResult;

import edu.harvard.mcz.imagecapture.data.Collector;
import edu.harvard.mcz.imagecapture.data.CollectorLifeCycle;
import edu.harvard.mcz.imagecapture.data.Number;
import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
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
	
	// protected SpecimenLifeCycle sls = null;
	
	public FieldLoader() { 
		init();
	}
	
	protected void init() {
		// sls = new SpecimenLifeCycle();
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
		
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		
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
		
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		
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
	 * @param allowUpdateExistingVerbatim if true can load can overwrite the value in an existing verbatim field.
	 * 
	 * @return true if one or more fields were updated.
	 * 
	 * @throws LoadException on an error (particularly from inability to map keys in data to fields in Specimen.
	 */
	public boolean loadFromMap(String barcode, Map<String,String> data, String newWorkflowStatus, boolean allowUpdateExistingVerbatim) throws LoadException { 
		boolean result = false;
		
		ArrayList<String> knownFields = new ArrayList<String>();
		HashMap<String,String> knownFieldsLowerUpper = new HashMap<String,String>();
		Method[] specimenMethods = Specimen.class.getDeclaredMethods();
		for (int j=0; j<specimenMethods.length; j++) { 
			if (specimenMethods[j].getName().startsWith("set") &&
					specimenMethods[j].getParameterTypes().length==1 &&  
				    specimenMethods[j].getParameterTypes()[0].getName().equals(String.class.getName()) 
			) { 
				String actualCase = specimenMethods[j].getName().replaceAll("^set", ""); 
			    knownFields.add(specimenMethods[j].getName().replaceAll("^set", ""));
			    knownFieldsLowerUpper.put(actualCase.toLowerCase(), actualCase);
			    log.debug(actualCase);
			}
		}
		// List of input fields that will need to be parsed into relational tables
		ArrayList<String> toParseFields = new ArrayList<String>();
		toParseFields.add("collectors");
		toParseFields.add("numbers");
		
		Specimen pattern = new Specimen();
		pattern.setBarcode(barcode);
		
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		
		// Retrieve existing record for update (thus not blanking existing fields, and allowing for not updating fields with values, or appending comments). 
		List<Specimen> matches = sls.findByExample(pattern);
		if (matches!=null && matches.size()==1) { 
			Specimen match = matches.get(0);		
		 
			// Check if the existing record is still in an updatable state for this remote update workflow.
			if (!WorkFlowStatus.allowsClassifiedUpdate(match.getWorkFlowStatus())) { 
				throw new LoadTargetMovedOnException();
			} else { 	

				boolean foundData = false;
				
				Iterator<String> i = data.keySet().iterator();
				while (i.hasNext()) { 
					String keyOrig = i.next();
					String key = keyOrig.toLowerCase();
					String actualCase = knownFieldsLowerUpper.get(key);
					if (toParseFields.contains(key) || 
						(
						actualCase!=null && knownFields.contains(actualCase) && !key.equals("barcode") && MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, key)
						)
					) { 
						String datavalue = data.get(keyOrig);
						Method setMethod;
						try {

							if (key.equals("questions")) {
							    setMethod = Specimen.class.getMethod("set" + actualCase, String.class);
							    Method getMethod = Specimen.class.getMethod("get" + actualCase, null);
							    String currentValue = (String) getMethod.invoke(match, null);
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
										CollectorLifeCycle cls = new CollectorLifeCycle();
										
										Collector col = new Collector();
										col.setSpecimen(match);
										col.setCollectorName(collector);
										// only try to add the collector if an identical one doesn't exist (prevents exception from constraint).
										List<Collector> existing = cls.findByExample(col);
										if (existing==null || existing.size()==0) {
										    cls.persist(col);
									    }
									}
								}
							} else if (key.equals("numbers")) { 
								String[] numbers = datavalue.split("\\|", 0);
								for (int j=0; j<numbers.length; j++) { 
									String numberKV = numbers[j];
									if (numberKV.trim().length()>0) {
										String number;
										String numberType;
										if (numberKV.contains(":")) { 
											String[] numberBits = numberKV.split(":",2);
											number = numberBits[0];
											numberType = numberBits[1];
										} else { 
											number=numberKV;
											numberType = "Unknown";
										}
										NumberLifeCycle nls = new NumberLifeCycle();
										Number num = new Number();
										num.setSpecimen(match);
										num.setNumber(number);
										num.setNumberType(numberType);
										// only add the number if an identical one doesn't exist.
										List<Number> existing = nls.findByExample(num);
										if (existing==null || existing.size()==0) {
									   	    nls.persist(num);
										}
									}
								}
								
							} else { 
							    setMethod = Specimen.class.getMethod("set" + actualCase, String.class);
							    Method getMethod = Specimen.class.getMethod("get" + actualCase, null);
							    String currentValue = (String) getMethod.invoke(match, null);
							    if (currentValue==null || currentValue.trim().length()==0) { 
							    	// TODO: Handle ISO date formatting variants
							    	if (key.equalsIgnoreCase("ISODate")) { 
							    		EventResult parseResult = DateUtils.extractDateFromVerbatimER(datavalue);
							    		if (parseResult.getResultState().equals(EventResult.EventQCResultState.DATE) || parseResult.getResultState().equals(EventResult.EventQCResultState.RANGE)) { 
							    			datavalue = parseResult.getResult();
							    		}
							    	}
							    	log.debug("Set: " + actualCase + " = " + datavalue );
								    setMethod.invoke(match, datavalue);
								    foundData = true;
							    } else if (MetadataRetriever.isFieldVerbatim(Specimen.class,key) && allowUpdateExistingVerbatim) { 
								    setMethod.invoke(match, datavalue);
								    foundData = true;
							    } else { 
							    	log.error("Skipped set" + actualCase + " = " + datavalue );
							    }
							}

						} catch (NoSuchMethodException e) {
							throw new LoadException(e.getMessage(),e);
						} catch (SaveFailedException e) {
							throw new LoadException(e.getMessage(),e);
						} catch (SecurityException e) {
							throw new LoadException(e.getMessage());
						} catch (IllegalAccessException e) {
							throw new LoadException(e.getMessage());
						} catch (IllegalArgumentException e) {
							throw new LoadException(e.getMessage());
						} catch (InvocationTargetException e) {
							throw new LoadException(e.getMessage(),e);
						}
					} else {
					    log.error("Key: " + key);
					    log.error("Key (actual case of method): " + actualCase);
						log.error("knownFields.contains(actualCase): " + knownFields.contains(actualCase));
						log.error("toParseFields.contains(key): " + toParseFields.contains(key));
						log.error("isFieldExternallyUpdatable:" +  MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, key));
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
	
	/**
	 * Test the headers of a file for conformance with the expectations of loadFromMap
	 * 
	 * @param headers the headers to check against allowed fields in List<String> form.
	 * @return a HeaderCheckResult object containing a result (true) and a message, the
	 *    result is true if there are no unmatched fields in the load, currently exceptions
	 *    are thrown instead of any false cases for result.
	 *
	 * @throws LoadException if no barcode field is found, if no data fields are found, or if
	 *    one or more unknown (not mapped to DataShot specimen) fields are found. 
	 *    
	 * @see HeaderCheckResult.loadFromMap  
	 */
	public HeaderCheckResult checkHeaderList(List<String> headers) throws LoadException {
		HeaderCheckResult result = new HeaderCheckResult();
		
		ArrayList<String> knownFields = new ArrayList<String>();
		HashMap<String,String> knownFieldsLowerUpper = new HashMap<String,String>();
		Method[] specimenMethods = Specimen.class.getDeclaredMethods();
		for (int j=0; j<specimenMethods.length; j++) { 
			if (specimenMethods[j].getName().startsWith("set") &&
					specimenMethods[j].getParameterTypes().length==1 &&  
				    specimenMethods[j].getParameterTypes()[0].getName().equals(String.class.getName()) 
			) { 
				String actualCase = specimenMethods[j].getName().replaceAll("^set", ""); 
			    knownFields.add(specimenMethods[j].getName().replaceAll("^set", ""));
			    knownFieldsLowerUpper.put(actualCase.toLowerCase(), actualCase);
			    log.debug(actualCase);
			}
		}
		// List of input fields that will need to be parsed into relational tables
		ArrayList<String> toParseFields = new ArrayList<String>();
		toParseFields.add("collectors");
		toParseFields.add("numbers");
		
		Iterator<String> i = headers.iterator();
		boolean containsBarcode = false;
		boolean containsAField = false;
		boolean containsUnknownField = false;
		while (i.hasNext()) { 
			String keyOrig = i.next();
			String key = keyOrig.toLowerCase();
			if (key.equals("barcode")) {
				containsBarcode = true;
				result.addToMessage(keyOrig); 
			} else { 
				if (key.startsWith("_")) { 
					result.addToMessage("[" + keyOrig+ "=Skipped]");
				} else { 
					String actualCase = knownFieldsLowerUpper.get(key);
					if (toParseFields.contains(key) || 
							(
									actualCase!=null && knownFields.contains(actualCase) && MetadataRetriever.isFieldExternallyUpdatable(Specimen.class, key)
									)
							) { 		
						result.addToMessage(keyOrig); 
						containsAField=true;
					} else { 
						result.addToMessage("[" + keyOrig + "=Unknown]");
						containsUnknownField = true;
					}
				}
			}
		}
		
		if (!containsBarcode) { 
			throw new LoadException("Header does not contain a barcode field.  \nFields:" + result.getMessage());
		}
		if (!containsAField) { 
			throw new LoadException("Header contains no recognized data fields. \nFields: " + result.getMessage());
		}
		if (containsUnknownField) { 
			throw new LoadException("Header contains at least one unknown field. \nFields: " + result.getMessage());
		}
		result.setResult(true);
		
		return result;
	}
	
}
