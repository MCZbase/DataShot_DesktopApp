/**
 * testBarcodeScanning.java
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

import java.io.File;

import edu.harvard.mcz.imagecapture.CandidateImageFile;
import edu.harvard.mcz.imagecapture.PositionTemplate;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;

import junit.framework.TestCase;

/**  Test cases for reading barcodes from image files.
 * 
 * @author Paul J. Morris
 *
 */
public class TestBarcodeScanning extends TestCase {

	/**
	 * @param name
	 */
	public TestBarcodeScanning(String name) {
		super(name);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.CandidateImageFile#getBarcodeText()}.
	 */
	public void testGetBarcodeText() {
		CandidateImageFile file;
		try {
			file = new CandidateImageFile(new File(this.getClass().getResource(AllTests.FILE_VALID_BARCODE).getFile()), new PositionTemplate());
			assertEquals(AllTests.BARCODE_IN_FILE_VALID_BARCODE,file.getBarcodeText());
			assertEquals(CandidateImageFile.RESULT_BARCODE_SCANNED, file.getBarcodeStatus());
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException. " + e.getMessage());
		}
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.CandidateImageFile#getBarcodeStatus()}.
	 */
	public void testGetBarcodeStatus() {
		CandidateImageFile file;
		try {
			file = new CandidateImageFile(new File(this.getClass().getResource(AllTests.FILE_EMPTY).getFile()), new PositionTemplate());
			assertEquals(CandidateImageFile.RESULT_ERROR,file.getBarcodeStatus());
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException. " + e.getMessage());
		}
	}

}
