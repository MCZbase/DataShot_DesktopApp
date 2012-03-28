/**
 * TestOfUnitTrayLabel.java
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

import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import junit.framework.TestCase;

/** TestOfUnitTrayLabel
 * 
 * @author Paul J. Morris
 *
 */
public class TestOfUnitTrayLabel extends TestCase {

	/**
	 * @param name
	 */
	public TestOfUnitTrayLabel(String name) {
		super(name);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.data.UnitTrayLabel#toJSONString()}.
	 */
	public void testToJSONString() {
		UnitTrayLabel u = new UnitTrayLabel();
		u.setFamily("Family");
		String json = u.toJSONString();
		assertEquals("{ \"f\":\"Family\", \"b\":\"\", \"t\":\"\", \"g\":\"\", \"s\":\"\", \"u\":\"\", \"i\":\"\", \"r\":\"\", \"a\":\"\", \"d\":\"\" }", json);
		u.setSubfamily("Subfamily");
		u.setTribe("Tribe");
		u.setGenus("Genus");
		u.setSpecificEpithet("specific");
		u.setAuthorship("(Author, 1800)");
		u.setDrawerNumber("111.1");
		u.setCollection("Rod Eastwood Collection");
		json = u.toJSONString();
		assertEquals("{ \"f\":\"Family\", \"b\":\"Subfamily\", \"t\":\"Tribe\", \"g\":\"Genus\", \"s\":\"specific\", \"u\":\"\", \"i\":\"\", \"r\":\"\", \"a\":\"(Author, 1800)\", \"d\":\"111.1\", \"c\":\"Rod Eastwood Collection\" }", json);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.data.UnitTrayLabel#createFromJSONString(java.lang.String)}.
	 */
	public void testCreateFromJSONString() {
		UnitTrayLabel u = new UnitTrayLabel();
		u.setFamily("Family");
		String json = u.toJSONString();
		UnitTrayLabel u1 = UnitTrayLabel.createFromJSONString(json);
		assertEquals(u.getFamily(),u1.getFamily());
		assertEquals(u.getTribe(),u1.getTribe());
		u.setSubfamily("Subfamily");
		u.setTribe("Tribe");
		u.setGenus("Genus");
		u.setSpecificEpithet("specific");
		u.setAuthorship("(Author, 1800");
		u.setDrawerNumber("111.1");
		json = u.toJSONString();
		u1 = UnitTrayLabel.createFromJSONString(json);
		assertEquals(u.getFamily(),u1.getFamily());
		assertEquals(u.getTribe(),u1.getTribe());
		assertEquals(u.getSubfamily(),u1.getSubfamily());
		assertEquals(u.getGenus(),u1.getGenus());
		assertEquals(u.getSpecificEpithet(),u1.getSpecificEpithet());
		assertEquals(u.getInfraspecificEpithet(),u1.getInfraspecificEpithet());
		assertEquals(u.getInfraspecificRank(), u1.getInfraspecificRank());
		assertEquals(u.getSubspecificEpithet(), u1.getSubspecificEpithet());
		assertEquals(u.getAuthorship(), u1.getAuthorship());
		assertEquals(u.getDrawerNumber(), u1.getDrawerNumber());
		// Check for case with some quotation marks in fields
		u.setFamily("Family");
		u.setSubfamily("Subfamily");
		u.setTribe("Tribe");
		u.setGenus("Genus");
		u.setSpecificEpithet("sp.'1' ");
		u.setAuthorship("(Author & \"author\", [1800]");
		u.setDrawerNumber("111.1");
		json = u.toJSONString();
		u1 = UnitTrayLabel.createFromJSONString(json);
		assertEquals(u.getFamily(),u1.getFamily());
		assertEquals(u.getTribe(),u1.getTribe());
		assertEquals(u.getSubfamily(),u1.getSubfamily());
		assertEquals(u.getGenus(),u1.getGenus());
		assertEquals(u.getSpecificEpithet(),u1.getSpecificEpithet());
		assertEquals(u.getInfraspecificEpithet(),u1.getInfraspecificEpithet());
		assertEquals(u.getInfraspecificRank(), u1.getInfraspecificRank());
		assertEquals(u.getSubspecificEpithet(), u1.getSubspecificEpithet());
		assertEquals(u.getAuthorship(), u1.getAuthorship());
		assertEquals(u.getDrawerNumber(), u1.getDrawerNumber());
		u.setFamily("Family");
		u.setSubfamily("Subfamily");
		u.setTribe("Tribe");
		u.setGenus("Genus");
		u.setSpecificEpithet("species");
		u.setInfraspecificEpithet("infraspecificEpithet");
		u.setInfraspecificRank("infraspecifcRank");
		u.setSubspecificEpithet("subspecificEpithet");
		u.setAuthorship("(Author, 1800)");
		u.setDrawerNumber("111.1");
		json = u.toJSONString();
		u1 = UnitTrayLabel.createFromJSONString(json);
		assertEquals(u.getFamily(),u1.getFamily());
		assertEquals(u.getTribe(),u1.getTribe());
		assertEquals(u.getSubfamily(),u1.getSubfamily());
		assertEquals(u.getGenus(),u1.getGenus());
		assertEquals(u.getSpecificEpithet(),u1.getSpecificEpithet());
		assertEquals(u.getInfraspecificEpithet(),u1.getInfraspecificEpithet());
		assertEquals(u.getInfraspecificRank(), u1.getInfraspecificRank());
		assertEquals(u.getSubspecificEpithet(), u1.getSubspecificEpithet());
		assertEquals(u.getAuthorship(), u1.getAuthorship());
		assertEquals(u.getDrawerNumber(), u1.getDrawerNumber());		
		u.setFamily("Family");
		u.setSubfamily("Subfamily");
		u.setTribe("Tribe");
		u.setGenus("Genus");
		u.setSpecificEpithet("species");
		u.setInfraspecificEpithet("infraspecificEpithet");
		u.setInfraspecificRank("infraspecifcRank");
		u.setSubspecificEpithet("subspecificEpithet");
		//TODO: Support embedded curly brackets
		//u.setAuthorship("(Author, {1800})");
		u.setAuthorship("(Author, [1800])");
		u.setDrawerNumber("111.1");
		u.setCollection("Rod Eastwood Collection");
		json = u.toJSONString();
		u1 = UnitTrayLabel.createFromJSONString(json);
		assertEquals(u.getFamily(),u1.getFamily());
		assertEquals(u.getTribe(),u1.getTribe());
		assertEquals(u.getSubfamily(),u1.getSubfamily());
		assertEquals(u.getGenus(),u1.getGenus());
		assertEquals(u.getSpecificEpithet(),u1.getSpecificEpithet());
		assertEquals(u.getInfraspecificEpithet(),u1.getInfraspecificEpithet());
		assertEquals(u.getInfraspecificRank(), u1.getInfraspecificRank());
		assertEquals(u.getSubspecificEpithet(), u1.getSubspecificEpithet());
		assertEquals(u.getAuthorship(), u1.getAuthorship());
		assertEquals(u.getDrawerNumber(), u1.getDrawerNumber());	
		assertEquals(u.getCollection(), u1.getCollection());
	}

}
