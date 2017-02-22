/**
 * TestOfUnitTrayLabelParser.java
 * edu.harvard.mcz.imagecapture.tests
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
package edu.harvard.mcz.imagecapture.tests;

import edu.harvard.mcz.imagecapture.ImageCaptureApp;
import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.UnitTrayLabelParser;
import junit.framework.TestCase;

/** TestOfUnitTrayLabelParser
 * 
 * @author Paul J. Morris
 *
 */
public class TestOfUnitTrayLabelParser extends TestCase {

	private String originalRegexDrawerNumber;
	
	/**
	 * @param name
	 */
	public TestOfUnitTrayLabelParser(String name) {
		super(name);
	}
	
	

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Store the currently locally configured regex for the drawer number, 
		originalRegexDrawerNumber = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER);
		// then set to the default value (which the tests expect) while running the tests.
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER, ImageCaptureApp.REGEX_DRAWERNUMBER);
	}



	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		// Reset the regex for the drawer number to not lose local properties settings from testing.
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_REGEX_DRAWERNUMBER, originalRegexDrawerNumber);
	}



	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.UnitTrayLabelParser#UnitTrayLabelParser(java.lang.String)}.
	 */
	public void testUnitTrayLabelParser_Species() {
		String label = "    Lycaenidae: Theclinae \n    Ministrymon una\n    (Hewitson, 1873)\n  115.13\n";    
        UnitTrayLabelParser parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Hewitson, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "    Lycaenidae: Theclinae: Tribe \n    Ministrymon una\n    (Hewitson, 1873)\n  115.13\n";
		parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("Tribe",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Hewitson, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // tribe on second line
        label = "    Lycaenidae: Theclinae: \n Tribe \n    Ministrymon una\n    (Hewitson, 1873)\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("Tribe",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Hewitson, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // Tribe on first line with questionable genus
        label = "    Lycaenidae: Theclinae: Tribe \n    Ministrymon? una\n    Hewitson, 1873\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("Tribe",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("Hewitson, 1873",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // indeterminate with drawer x
        label = "    Lycaenidae : Theclinae  \n    Ministrymon sp. 2\n  \n  x\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("sp.",parser.getSpecificEpithet());
        assertEquals("2",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("",parser.getAuthorship());
        assertEquals("x",parser.getDrawerNumber());           
        // Pathological case of a trailing colon in the higher taxonomy.
        label = "    Lycaenidae: Theclinae:  \n    Ministrymon? una\n    Hewitson, 1873\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("Hewitson, 1873",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // Pathological case of a trailing colon in the higher taxonomy.
        label = "    Lycaenidae : Theclinae :  \n    Ministrymon? una\n    Hewitson, 1873\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("Hewitson, 1873",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());                  
	}
	
	/** 
	 * Unit tests for parsing some variations of a trinomial subspecies epithet.
	 */
	public void testUnitTrayLabelParser_Subpecies() {
		String label = "    Lycaenidae: Theclinae \n    Ministrymon una una\n    (Hewitson, 1873)\n  115.13\n";    
        UnitTrayLabelParser parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("una",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Hewitson, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "    Lycaenidae: Theclinae : Tribe\n    Ministrymon una  subsp.\n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("Tribe",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "Lycaenidae: Theclinae: \n    Ministrymon?  una  subsp. \n(Smith and Jones, 1873)  \n  115.13 \n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "    Lycaenidae: Theclinae : Tribe\n    Ministrymon longspecific \n subspecific \n    (Smith and Jones, 1873)\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("Tribe",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("longspecific",parser.getSpecificEpithet());
        assertEquals("subspecific",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "    Lycaenidae: Theclinae \n    Ministrymon longspecific \n subspecific \n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("longspecific",parser.getSpecificEpithet());
        assertEquals("subspecific",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "    Lycaenidae: Theclinae \n    Ministrymon longspecific var. \n variety \n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("longspecific",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("var.",parser.getInfraspecificRank()); 
        assertEquals("variety",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "    Lycaenidae: Theclinae \n    Ministrymon longspecific \n var. variety \n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("longspecific",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("var.",parser.getInfraspecificRank()); 
        assertEquals("variety",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());        
	} 

	public void testUnitTrayLabelParser_trinomial() { 
        String label = "Lycaenidae: Theclinae: \n    Ministrymon una var. varietal \n(Smith and Jones, 1873)  \n  115.13 \n";    
        UnitTrayLabelParser parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());     
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("var.",parser.getInfraspecificRank()); 
        assertEquals("varietal",parser.getInfraspecificEpithet());         
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "Lycaenidae: Theclinae: \n    Ministrymon una forma formname \n(Smith and Jones, 1873)  \n  115.13 \n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());     
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("forma",parser.getInfraspecificRank()); 
        assertEquals("formname",parser.getInfraspecificEpithet());         
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());  
        // Test to with a subspecies trinomial.
        label = "Lycaenidae: Theclinae: \n    Ministrymon una subspecies \n(Smith and Jones, 1873)  \n  115.13 \n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());     
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subspecies",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet());         
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());           
	}
	
	/**
	 * Unit tests for parsing some pathologies in the formation of labels.
	 */
	public void testUnitTrayLabelParser_Pathologies() { 
        String label = "Lycaen idae: Theclinae: \n    Ministrymon?  una  subsp. \n(Smith and Jones, 1873)  \n  115.13 \n";    
        UnitTrayLabelParser parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());     
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());   
        label = "Lycaenidae: Thecl inae \n    Ministrymon?  una  subsp. \n   (Smith and Jones, 1873)  \n115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());  
        // Space after first letter of generic name
        label = "Lycaenidae: Theclinae \n    M inistrymon  una  \n   (Smith and Jones, 1873)  \n115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // Space after first letter of generic name
        label = "Lycaenidae: Theclinae \n    M inistrymon  una subspecies \n   (Smith and Jones, 1873)  \n115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subspecies",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // Space after first letter of generic name
        label = "Lycaenidae: Theclinae \n    M inistrymon  una var. variety \n   (Smith and Jones, 1873)  \n115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("var.",parser.getInfraspecificRank()); 
        assertEquals("variety",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());          
        // no drawer number
        label = "Lycaenidae: Theclinae \n    Ministrymon?  una  subsp. \n   (Smith and Jones, 1873)  \n\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("",parser.getDrawerNumber());  
        label = "Lycaenidae: Theclinae \n    Ministrymon?  una  subsp. \n   (Smith and Jones, 1873)  \n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("",parser.getDrawerNumber());   
        label = "Lycaenidae: Theclinae \n    Ministrymon?  una  subsp. \n   (Smith and Jones, 1873)";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon?",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("subsp.",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("",parser.getDrawerNumber());  
        // other missing elements
        label = "Lycaenidae: Theclinae \n    Ministrymon  una ";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("",parser.getAuthorship());
        assertEquals("",parser.getDrawerNumber());  
        label = "Lycaenidae: Theclinae";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("",parser.getGenus());
        assertEquals("",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("",parser.getAuthorship());
        assertEquals("",parser.getDrawerNumber()); 
        // bad OCR of first line
        label = "    Lycae22222 Thecl2222 \n    Ministrymon una  \n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycae22222",parser.getFamily());
        assertEquals("Thecl2222",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "  A \n  Lycae22222 Thecl2222 \n    Ministrymon una  \n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("A",parser.getFamily());
        assertEquals("",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        label = "  A  \n    Ministrymon una  \n    (Smith and Jones, 1873)\n  115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("A",parser.getFamily());
        assertEquals("",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());        
        // leading first line before higher taxon names line
        label = "  A \n  Lycaenidae: Theclinae \n    Ministrymon una  \n    (Smith and Jones, 1873)\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // blank line before species group name line
        label = " \n Ministrymon una  \n    (Smith and Jones, 1873)\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("",parser.getFamily());
        assertEquals("",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());        
        // no line before species group name line
        label = "Ministrymon una  \n    (Smith and Jones, 1873)\n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Ministrymonuna",parser.getFamily());
        assertEquals("",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymon",parser.getGenus());
        assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // no line before species group name line and no author line
        label = "Ministrymon sp. \n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Ministrymonsp.",parser.getFamily());
        assertEquals("",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("",parser.getGenus());
        assertEquals("",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("Ministrymon sp.",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
        // no space in species group name
        label = "Ministrymon sp. \n  115.13\n";
        parser = new UnitTrayLabelParser(label);
        label = "Lycaenidae: Theclinae \n    Ministrymonuna \n   (Smith and Jones, 1873)  \n115.13\n";    
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Ministrymonuna",parser.getGenus());
        assertEquals("",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Smith and Jones, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());   
        // tribe on second line, colon missing
        label = "    Lycaenidaxx Theclinae: \n Tribe \n    M inistrymon una\n    (Hewitson, 1873)\n  115.15\n";
        parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidaxx",parser.getFamily());
        //assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        //assertEquals("Ministrymon",parser.getGenus());
        //assertEquals("una",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Hewitson, 1873)",parser.getAuthorship());
        assertEquals("115.15",parser.getDrawerNumber());
	}
	
	public void testGenusLeadingBracket() { 
		String label = "    Lycaenidae: Theclinae \n    [Aricia agestis\n    (Hewitson, 1873)\n  115.13\n";    
        UnitTrayLabelParser parser = new UnitTrayLabelParser(label);
        assertEquals("Lycaenidae",parser.getFamily());
        assertEquals("Theclinae",parser.getSubfamily());
        assertEquals("",parser.getTribe());
        assertEquals("Aricia",parser.getGenus());
        assertEquals("agestis",parser.getSpecificEpithet());
        assertEquals("",parser.getSubspecificEpithet());
        assertEquals("",parser.getInfraspecificRank()); 
        assertEquals("",parser.getInfraspecificEpithet()); 
        assertEquals("(Hewitson, 1873)",parser.getAuthorship());
        assertEquals("115.13",parser.getDrawerNumber());
	}

}
