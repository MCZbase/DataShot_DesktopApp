/**
 * SpecimenControler.java
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
package edu.harvard.mcz.imagecapture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.SpecimenLifeCycle;
import edu.harvard.mcz.imagecapture.data.SpecimenListTableModel;
import edu.harvard.mcz.imagecapture.exceptions.BadTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.ImageLoadException;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchRecordException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SpecimenExistsException;
import edu.harvard.mcz.imagecapture.interfaces.DataChangeListener;

/**
 * Control actions that can be taken on Specimen objects.
 * 
 * @author Paul J. Morris
 *
 */
public class SpecimenControler {
	
	private static final Log log = LogFactory.getLog(SpecimenControler.class);
	
	private Specimen specimen = null;
	private JTable table = null;
	private TableModel model = null;   // model in which specimen can be found along with other specimens.
	private int currentRow = -1;       // row of this specimen in the table model.
	private boolean inTable = false;   // true if model/row apply and specimen one of list in a table model.
	private List<DataChangeListener> listeners;
	
	private ImageDisplayFrame resultFrame = null;
	
	public SpecimenControler(Specimen aSpecimen) throws NoSuchRecordException {
		if(aSpecimen==null) { 
			throw new NoSuchRecordException("Can't create a specimen controller with a null specimen");
		}
		specimen = aSpecimen;
		currentRow = -1;
		inTable = false;
	}

	/** Create a specimen controller for a specimen in a table.  If this constructor suceeds then isInTable()
	 * will return true.
	 * 
	 * @param aSpecimen the specimen
	 * @param aModel a SpecimenListTableModel containing the specimen
	 * @param aTable the table in which the SpecimenListTableModel is shown.
	 * @param theCurrentRow the row of the specimen in the view of the table.
	 * @throws NoSuchRecordException 
	 */
	public SpecimenControler(Specimen aSpecimen, SpecimenListTableModel aModel, JTable aTable, int theCurrentRow) throws NoSuchRecordException {
		if(aSpecimen==null) { 
			throw new NoSuchRecordException("Can't create a specimen controller with a null specimen");
		}
		log.debug(theCurrentRow + " " + aSpecimen);
		specimen = aSpecimen;
		if (aModel!=null) { 
			model = aModel;
			currentRow = theCurrentRow;
			table = aTable;
			inTable = true;
		}
	}	
	
	public SpecimenControler(Long aSpecimenID) throws NoSuchRecordException { 
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		specimen = sls.findById(aSpecimenID);
		if (specimen==null) { 
			throw new NoSuchRecordException("No specimen found with SpecimenId = [" + aSpecimenID .toString() + "]");
		}
		currentRow = -1;
		inTable = false;
	}
	
	public void setTargetFrame(ImageDisplayFrame targetFrame) { 
		if (targetFrame!=null)  {
			resultFrame = targetFrame;
		}		
	}
	
	public void displayInEditor(ImageDisplayFrame targetFrame)  {
		if (targetFrame!=null)  {
			resultFrame = targetFrame;
		}
		displayInEditor();
	}
	
	/** If the specimen for this controller is in a table model, switches out for next specimen in model.
	 * If the specimen isn't in a table model, does nothing.  If at last position in table model, does nothing.
	 * 
	 * @return true if specimen was changed, false if not, false if isInTable() is false.
	 */
	public boolean nextSpecimenInTable() { 
		boolean result = false;
		if (inTable && model!=null && currentRow > -1) {
			try { 
				Specimen temp =(Specimen) model.getValueAt(table.convertRowIndexToModel(currentRow+1), 0); 
				if (temp!=null) { 
			       specimen = (Specimen) model.getValueAt(table.convertRowIndexToModel(currentRow+1), 0); 
				}
			    currentRow = currentRow + 1;
			    result = true;
			} catch (IndexOutOfBoundsException e) { 
				log.debug(e);
			}
		}
		return result;
	}
	
	/** If the specimen for this controller is in a table model, switches out for previous specimen in model.
	 * If the specimen isn't in a table model, does nothing.  If at first position in table model, does nothing.
	 * 
	 * @return true if specimen was changed, false in not, false if isInTable() is false.
	 */
	public boolean previousSpecimenInTable() { 
		boolean result = false;
		if (inTable && model!=null && currentRow > -1 && currentRow > 0) {
			try { 
				Specimen temp =(Specimen) model.getValueAt(table.convertRowIndexToModel(currentRow-1), 0); 
				if (temp!=null) { 
			       specimen = (Specimen) model.getValueAt(table.convertRowIndexToModel(currentRow-1), 0); 
				}
			    currentRow = currentRow - 1;
			    result = true;
			} catch (IndexOutOfBoundsException e) { 
				log.debug(e);
			}
		}
		return result;
	}
		
	
	
	public boolean setSpecimen(Long aSpecimenID) throws NoSuchRecordException {
		boolean result = false;
		SpecimenLifeCycle sls = new SpecimenLifeCycle();
		specimen = sls.findById(aSpecimenID);
		if (specimen==null) { 
			throw new NoSuchRecordException("No specimen found with SpecimenId = [" + aSpecimenID .toString() + "]");
		} else {
			result = true;
		}
		return result;
	}
	
	public boolean save() throws SaveFailedException { 
	    boolean result = false;
		SpecimenLifeCycle s = new SpecimenLifeCycle();
		if (specimen.getSpecimenId()!=null) { 
		    s.attachDirty(specimen);
		} else { 
			try {
				s.persist(specimen);
			} catch (SpecimenExistsException e) {
				// convert special case used in preprocessing back to a save failed exception.
				throw new SaveFailedException(e.getMessage(),e);
			}
		}
		notifyListeners();
    	// reload the specimen
		// Why???
    	//specimen = s.findById(specimen.getSpecimenId());
		return result;
	}
	
	public void displayInEditor() { 
		boolean isNew = false;
		if (resultFrame==null) { 
		    resultFrame = new ImageDisplayFrame(specimen);
		    isNew = true;
		} 
		SpecimenDetailsViewPane p = new SpecimenDetailsViewPane(specimen, this);
		
		resultFrame.addWest(p);
	
		// Add images of the specimen and labels to the details editor.
		// TODO: Add box and drawer level images
		String drawerNumber = null;
		if (specimen.getDrawerNumber()!=null) { 
			drawerNumber = specimen.getDrawerNumber(); 
		} 
		List<ICImage> drawerImages = null;
		if (drawerNumber!=null && !drawerNumber.trim().equals("")) { 
		     drawerImages = ICImageLifeCycle.findDrawerImages(drawerNumber);
		     //TODO: check drawer number for trailing letter (e.g. 115.12a), then check 
		     // for drawer (115.12) and unit tray (115.12a) level images.
		}
		if (specimen.getICImages()!=null 
				&& specimen.getICImages().size()==1 
				&& (drawerImages==null || drawerImages.isEmpty())) {  

			ICImage image = specimen.getICImages().iterator().next();
			//TODO: stored path may need separator conversion for different systems. 
			//String startPointName = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
			String path = image.getPath();
			if (path == null) { path = ""; } 
			//File fileToCheck = new File(startPointName + path + image.getFilename());
			File fileToCheck = new File(ImageCaptureProperties.assemblePathWithBase(path, image.getFilename()));
			log.debug(fileToCheck.getPath());
			try {
				PositionTemplate template = PositionTemplate.findTemplateForImage(image);
				try {
					resultFrame.loadImagesFromFile(fileToCheck, template, image);
				} catch (BadTemplateException e) {
					// TODO:  is this the right action, or should this be a fatal error? 
					log.error("Unexptected BadTemplateException after template tests." + e.getMessage());
					throw new ImageLoadException("Problem finding template for this file.");
				}

			} catch (ImageLoadException e2) {
				System.out.println("Error loading image file.");
				System.out.println(e2.getMessage());
			}
		} else { 
			if (drawerImages==null || drawerImages.isEmpty()) {
				// Specimen has multiple images, but no drawer images 
				log.debug("Specimen with no drawer images: " + specimen.getBarcode());
				if (specimen.getICImages().size()==0) { 
					log.error("Specimen with no images: " + specimen.getBarcode());	
				} else { 
			       resultFrame.loadImagesFromFiles(specimen.getICImages());
				}
			} else { 
				Set<ICImage> images = specimen.getICImages();
				images.addAll(drawerImages);
				resultFrame.loadImagesFromFiles(images);
			}
		}
		resultFrame.pack();
		resultFrame.centerSpecimen();   // Specimen is expected to be at the center of the specimen part of the image.
		if (isNew) {
			resultFrame.center();
		}
		resultFrame.setVisible(true);
	}

	/**
	 * Determine if the specimen controled by this controller is part of a 
	 * list of specimens in a table model or not.  
	 * 
	 * @return true if specimen is part of a list in a table model.
	 */
	public boolean isInTable() { 
		return inTable;
	}
	
	public void addListener(DataChangeListener aListener) { 
		if (listeners==null) { 
		   listeners = new ArrayList<DataChangeListener>();	
		}
		log.debug("Added listener: " +  aListener);
		listeners.add(aListener);
	}
	
	public void notifyListeners() {
		if (listeners!=null)  {
			for (int i=0; i<listeners.size(); i++) { 
				listeners.get(i).notifyDataHasChanged();
			}
		}
	}
	
}
