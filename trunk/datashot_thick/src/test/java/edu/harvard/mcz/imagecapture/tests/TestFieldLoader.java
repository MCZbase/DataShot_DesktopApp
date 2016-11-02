/**
 * TestFieldLoader.java
 * edu.harvard.mcz.imagecapture.tests
 * Copyright © 2016 President and Fellows of Harvard College
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

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.harvard.mcz.imagecapture.loader.FieldLoader;
import edu.harvard.mcz.imagecapture.loader.ex.LoadException;
import edu.harvard.mcz.imagecapture.loader.ex.LoadTargetRecordNotFoundException;

/**
 * @author mole
 *
 */
public class TestFieldLoader {
	private static final Log log = LogFactory.getLog(TestFieldLoader.class);
	

	// TODO: Provide unit tests that work with mock objects.  
	// Tests here expect particular preconditions and fail if run repeatedly.
	
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//		
//		
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}
//
//	/**
//	 * Test method for {@link edu.harvard.mcz.imagecapture.loader.FieldLoader#load(java.lang.String, java.lang.String, java.lang.String)}.
//	 */
//	@Test
//	public void testLoad() {
//		FieldLoader f = new FieldLoader();
//		try {
//			f.load("1", "Africa", "1820","Should Fail");
//		} catch (LoadTargetRecordNotFoundException ex) { 
//			// expected exception
//		} catch (LoadException e) {
//			log.error(e.getMessage(),e);
//			fail(e.getMessage());
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			f.load("MCZ-ENT00525604", "Africa", "1820", "Should update first time");
//		} catch (LoadTargetRecordNotFoundException ex) { 
//			// expected exception
//		} catch (LoadException e) {
//			log.error(e.getMessage(),e);
//			fail(e.getMessage());
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link edu.harvard.mcz.imagecapture.loader.FieldLoader#loadFromMap(java.lang.String, java.util.Map)}.
//	 */
//	@Test
//	public void testLoadFromMap() {
//		fail("Not yet implemented");
//	}
//	
//	
	
}
