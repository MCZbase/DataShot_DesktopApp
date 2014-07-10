/**
 * 
 */
package edu.harvard.mcz.imagecapture.data;

/**
 * Sex provides an authority file of values for the field Sex that can be used to populate picklists.
 * @author Paul J. Morris
 *
 */
public class Sex {
	
	public static String[] getSexValues() {
		String[] values = {"", "Male", "Female", "NotApplicable", "Gynandromorph", "Intersex","unknown"};
		return values;
	}

}
