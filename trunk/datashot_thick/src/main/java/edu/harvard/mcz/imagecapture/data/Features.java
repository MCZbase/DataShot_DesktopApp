/**
 * 
 */
package edu.harvard.mcz.imagecapture.data;

/**
 * Features, values to populate picklist of possible features of specimens.
 * @deprecated  Likely to be highly variable with low repetition, should probably 
 * use free text fields instead.  
 * @author Paul J. Morris
 *
 */
public class Features {

	public static String[] getFeaturesValues() {
		String[] features = {"","abberation","melanic","eclosion defect", "runt", 
				"deformed", "faded colours", "scales stripped for venation",
				"greasy", "stained", "psocid damaged"};
		return features;
	}
}
