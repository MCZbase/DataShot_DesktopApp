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
	
	public boolean load(String barcode, String verbatimLocality, String verbatimDate, String questions) throws LoadException { 
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
				
				// append any questions to current questions.
				if (questions!=null && questions.trim().length() > 0 ) { 
					String currentQuestions = match.getQuestions();
					if (currentQuestions==null) { currentQuestions = ""; } 
					if (currentQuestions.trim().length()>0) { currentQuestions = currentQuestions + " | "; }
					match.setQuestions(currentQuestions + questions);
				}
				
				// match.setWorkFlowStatus(WorkFlowStatus.STAGE_VERBATIM);

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
		
		
		return false;
	}
	
	public boolean loadFromMap(String barcode, Map<String,String> data, String newWorkflowStatus) throws LoadException { 
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
					if (knownFields.contains(key)) { 
						String datavalue = data.get(key);

						Method setMethod;
						try {
							setMethod = Specimen.class.getMethod("set" + key, String.class);

							Method getMethod = Specimen.class.getMethod("get" + key, null);

							String currentValue = (String) getMethod.invoke(match, null);

							if (currentValue==null || currentValue.trim().length()==0) { 
								setMethod.invoke(match, datavalue);
								foundData = true;
							}

						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else { 
						// TODO: Field not known 
					}
				}

				if (foundData) { 
					try {
						
						//match.setWorkFlowStatus(newWorkflowStatus);
						
						
						sls.attachDirty(match);
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
