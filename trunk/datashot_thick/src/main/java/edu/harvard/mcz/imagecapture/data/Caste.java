/**
 * 
 */
package edu.harvard.mcz.imagecapture.data;

/**
 * Caste provides an authority file of values for the part attribute Caste that can be used to populate picklists.
 * @author Paul J. Morris
 *
 */
public class Caste {
	
	public static String[] getCasteValues() {
		String[] values = {
		        "drone", "female alate", "female reproductive", 
		        "major", "male - dealated", "male - wingless", "male alate", 
		        "media", "minor", "other", "queen", "queen - intermorph", 
		        "replete", 
		        "soldier", "unknown", "worker"	
		         };
		return values;
	}

}
