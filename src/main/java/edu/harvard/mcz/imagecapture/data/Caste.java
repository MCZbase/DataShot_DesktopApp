/**
 * 
 */
package edu.harvard.mcz.imagecapture.data;

/**
 * Sex provides an authority file of values for the field Sex that can be used to populate picklists.
 * @author Paul J. Morris
 *
 */
public class Caste {
	
	public static String[] getCasteValues() {
		String[] values = {
		        "1st instar larva", "2nd instar larva", "3rd instar larva", 
		        "dealiated adult", "drone", "female", "female alate", "female reproductive", 
		        "juvenile", "larva", "larval case", "major", "male", "male alate", 
		        "minor", "nymph", "other", "pupa", "queen", "slave", "slave-maker", 
		        "soldier", "unknown", "worker",			
		         };
		return values;
	}

}
