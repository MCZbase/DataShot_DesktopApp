/**
 * TestBarcodeMatching.java
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

import edu.harvard.mcz.imagecapture.ETHZBarcode;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeMatcher;
import junit.framework.TestCase;

/** TestBarcodeMatching
 * 
 * @author Paul J. Morris
 *
 */
public class TestETHZBarcodeMatching extends TestCase {

	/**
	 * @param name
	 */
	public TestETHZBarcodeMatching(String name) {
		super(name);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.MCZENTBarcode#extractNumber(java.lang.String)}.
	 */
	public void testExtractNumber() {
		BarcodeMatcher matcher = new ETHZBarcode();
		assertEquals(Integer.valueOf(1),matcher.extractNumber("ETHZ-ENT0000001"));
		assertNull(matcher.extractNumber("ETHZ-ENTaaaaaaaaa"));
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.MCZENTBarcode#matchesPattern(java.lang.String)}.
	 */
	public void testMatchesPattern() {
		BarcodeMatcher matcher = new ETHZBarcode();
		assertTrue(matcher.matchesPattern("ETHZ-ENT0123456"));
		assertTrue(matcher.matchesPattern("ETHZ-ENT0000000"));
		assertTrue(matcher.matchesPattern("ETHZ-ENT0000001"));
		assertTrue(matcher.matchesPattern("ETHZ-ENT9999999"));
		assertTrue(matcher.matchesPattern("ETHZ-ENT1111111"));
		assertTrue(matcher.matchesPattern("ETHZ-ENT2222222"));		
		assertTrue(matcher.matchesPattern("ETHZ-ENT3333333"));		
		assertTrue(matcher.matchesPattern("ETHZ-ENT4444444"));		
		assertTrue(matcher.matchesPattern("ETHZ-ENT5555555"));		
		assertTrue(matcher.matchesPattern("ETHZ-ENT6666666"));		
		assertTrue(matcher.matchesPattern("ETHZ-ENT7777777"));		
		assertTrue(matcher.matchesPattern("ETHZ-ENT8888888"));		
		assertFalse(matcher.matchesPattern("not a barcode"));
		assertFalse(matcher.matchesPattern("ETHZ-ENT123"));
		assertFalse(matcher.matchesPattern("ETZHENT0123456"));
		assertFalse(matcher.matchesPattern("ETHZ-TST0123456"));
		assertFalse(matcher.matchesPattern("ETHZ-ENT0123456\n"));
		assertFalse(matcher.matchesPattern("MCZ-ENT0123456"));
		assertFalse(matcher.matchesPattern("E"));
		assertFalse(matcher.matchesPattern("ET"));
		assertFalse(matcher.matchesPattern("ETZ"));
		assertFalse(matcher.matchesPattern("ETZH"));
		assertFalse(matcher.matchesPattern("ETHZ-"));
		assertFalse(matcher.matchesPattern("ETHZ-E"));
		assertFalse(matcher.matchesPattern("ETHZ-EN"));
		assertFalse(matcher.matchesPattern("ETHZ-ENT"));
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.MCZENTBarcode#matchFoundIn(java.lang.String)}.
	 */
	public void testMatchFoundIn() {
		BarcodeMatcher matcher = new ETHZBarcode();
		assertTrue(matcher.matchFoundIn("ETHZ-ENT0123456"));
		assertFalse(matcher.matchFoundIn("not a barcode"));
		assertFalse(matcher.matchFoundIn(""));
		assertFalse(matcher.matchFoundIn(null));
		assertFalse(matcher.matchFoundIn("ETHZ-ENT123"));
		assertFalse(matcher.matchFoundIn("MCZENT01234567"));
		assertFalse(matcher.matchFoundIn("ETHZENT0123456"));
		assertFalse(matcher.matchFoundIn("ETHZ-TST0123456"));
		assertTrue(matcher.matchFoundIn("ETHZ-ENT0123456\n"));
		assertTrue(matcher.matchFoundIn("\nETHZ-ENT0123456\n"));
		assertTrue(matcher.matchFoundIn(":ETHZ-ENT0123456a"));
		assertFalse(matcher.matchFoundIn("MCZENT0123456\n"));
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.MCZENTBarcode#makeFromNumber(java.lang.Integer)}.
	 */
	public void testMakeFromNumber() {
		BarcodeBuilder builder = new ETHZBarcode();
		assertEquals("ETHZ-ENT0123456", builder.makeFromNumber(Integer.valueOf(123456)));
		assertEquals("ETHZ-ENT0000001", builder.makeFromNumber(Integer.valueOf(1)));
		assertEquals("ETHZ-ENT0000000", builder.makeFromNumber(Integer.valueOf(0)));
		assertNull(builder.makeFromNumber(null));
		assertNull(builder.makeFromNumber(Integer.MAX_VALUE));
		assertNull(builder.makeFromNumber(Integer.MIN_VALUE));
		assertNull(builder.makeFromNumber(Integer.valueOf(-1)));
	}
	
	public void testMakeAndMatch() {
		BarcodeBuilder builder = new ETHZBarcode();
		BarcodeMatcher matcher = new ETHZBarcode();
		for (int i=1; i<9999999; i = i * 2) { 
		   assertTrue(matcher.matchesPattern(builder.makeFromNumber(i-1)));
		   assertTrue(matcher.matchesPattern(builder.makeFromNumber(i)));
		   if (i+1 <= 9999999) { 
		       assertTrue(matcher.matchesPattern(builder.makeFromNumber(i+1)));
		   } else { 
			   assertFalse(matcher.matchesPattern(builder.makeFromNumber(i+1)));
		   }
		}
		for (int i=9999000; i<=9999999; i = i + 1) { 
		   assertTrue(matcher.matchesPattern(builder.makeFromNumber(i-1)));
		   assertTrue(matcher.matchesPattern(builder.makeFromNumber(i)));
		   if (i+1 <= 9999999) { 
		       assertTrue(matcher.matchesPattern(builder.makeFromNumber(i+1)));
		   } else { 
			   assertFalse(matcher.matchesPattern(builder.makeFromNumber(i+1)));
		   }
		}		
	}

}
