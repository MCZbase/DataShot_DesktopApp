package edu.harvard.mcz.imagecapture.data;

import edu.harvard.mcz.imagecapture.interfaces.ValueLister;

/** LifeStage authority list of values of LifeStage to populate picklists.
 * 
 * @author Paul J. Morris
 *
 */
public class LifeStage implements ValueLister {

	public static String[] getLifeStageValues() { 
		String[] lifestages = {
				"adult","callow","egg","juvenile","larva",
				"naiad","non-adult","nymph","pharate","pupa",
				"teneral adult", "teneral nymph",
				"1st instar","2nd instar","3rd instar","4th instar","5th instar","6th instar","7th instar"
				};
		return lifestages;
	}

	public String[] getValues() {
		return getLifeStageValues();
	}

	
	
}
