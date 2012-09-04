package edu.harvard.mcz.imagecapture.data;

import edu.harvard.mcz.imagecapture.interfaces.ValueLister;

/** LifeStage authority list of values of LifeStage to populate picklists.
 * 
 * @author Paul J. Morris
 *
 */
public class LifeStage implements ValueLister {

	public static String[] getLifeStageValues() { 
		String[] lifestages = {"Adult","Non-Adult","Pupa","Larva","Egg","pupal shelter","puparium","NotApplicable"};
		return lifestages;
	}

	public String[] getValues() {
		return getLifeStageValues();
	}

	
	
}
