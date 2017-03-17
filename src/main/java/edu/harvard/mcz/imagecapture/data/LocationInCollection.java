/**
 * 
 */
package edu.harvard.mcz.imagecapture.data;

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.Singleton;

/**Controled vocabulary for Specimen.locationInCollection, storage and workflow indicator 
 * for types, general collection, slides, etc.
 * @author Paul J. Morris
 *
 */
public class LocationInCollection {

	protected static final String GENERAL = "General Lepidoptera Collection";
	public static final String GENERALANT = "General Ant Collection";
	protected static final String PALEARCTIC = "Palaearctic Lepidoptera Collection";
	
	/**
	 * Obtain a list of default location in collection values to use for picklists of collections.
	 * 
	 * @return a string array of values for the location in collection suitable for populating a picklist.
	 */
	public static String[] getLocationInCollectionValues() { 
		
		//TODO: make list available as configuration (or from a database table)
		
		String configuredcollection = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_SPECIFIC_COLLECTION);
		String coll = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION);

		if (configuredcollection==null || configuredcollection.trim().length()==0) { 
			if (coll.equals(ImageCaptureProperties.COLLECTION_ETHZENT)) { 
				String [] values = { "Palaearctic Lepidoptera Collection" };
				return values;
			} else { 
				String [] values = {GENERAL, GENERALANT, "Type Collection", "Nabokov Collection", "Slide Collection"}; 
				return values;
			} 
		} else { 
			if (coll.equals(ImageCaptureProperties.COLLECTION_ETHZENT)) { 
				String [] values = { configuredcollection, "Palaearctic Lepidoptera Collection" };
				return values;
			} else { 
				String [] values = { configuredcollection, GENERAL, GENERALANT, "Type Collection", "Nabokov Collection", "Slide Collection"}; 
				return values;
			} 
		}
	}
	
	/** 
	 * Obtain the configured value for the default collection, either the default value
	 * implied by ImageCaptureProperties.KEY_COLLECTION or, if provided, the specific value
	 * obtained from ImageCaptureProperties.KEY_SPECIFIC_COLLECTION.
	 * 
	 * @return string value for the default location in collection.
	 */
	public static String getDefaultLocation()  {
		String value = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_SPECIFIC_COLLECTION);
		if (value!=null && value.trim().length()>0) { 
			return value;
		}
		String coll = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION);
		if (coll.equals(ImageCaptureProperties.COLLECTION_ETHZENT)) { 
			return PALEARCTIC;
		} else { 
			return GENERAL;
		}
	}
	
}
