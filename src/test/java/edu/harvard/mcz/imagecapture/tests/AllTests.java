package edu.harvard.mcz.imagecapture.tests;
/**
 * AllTests.java
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
import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * AllTests invokes the suite of unit tests for edu.harvard.mcz.imagecapture classes.
 * 
 * AllTests includes class constants for resources within edu.harvard.mcz.imagecapture.tests.resources
 * that are used repeatedly in the unit tests themselves.
 * 
 * @author Paul J. Morris
 *
 */
public class AllTests {
	
	// Filename constants for tests
	public static final String FILE_VALID_BARCODE_FILENAME = "141515321.jpg";
	public static final String FILE_VALID_BARCODE_PATH = "resources" + File.separator;
	public static final String FILE_VALID_BARCODE = FILE_VALID_BARCODE_PATH + FILE_VALID_BARCODE_FILENAME;
	public static final String BARCODE_IN_FILE_VALID_BARCODE =  "141515321";
	public static final String FILE_INVALID_NAME = FILE_VALID_BARCODE_PATH + "nosuchfile.bad";
	public static final String FILE_EMPTY = FILE_VALID_BARCODE_PATH + "emptyfile.jpg";

	/**
	 * Run the JUnit tests (but not integration tests).
	 * @return Test
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests in edu.harvard.mcz.imagecapture.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestBarcodeScanning.class);
		suite.addTestSuite(TestOfSingleton.class);
		// suite.addTestSuite(TestOfPositionTemplate.class);
		// suite.addTestSuite(TestOfCandidateImageFile.class);
		// suite.addTestSuite(TestOfTemplateDetection.class);
		suite.addTestSuite(TestOfUnitTrayLabel.class);
		suite.addTestSuite(TestOfUnitTrayLabelParser.class);
		suite.addTestSuite(TestOfSpecimen.class);
		suite.addTestSuite(TestOfImageCaptureProperties.class);
		suite.addTestSuite(TestMCZENTBarcodeMatching.class);
		suite.addTestSuite(TestETHZBarcodeMatching.class);
		suite.addTestSuite(TestofHashUtility.class);
		suite.addTestSuite(TestOfUserRoles.class);
		suite.addTestSuite(TestOfPasswordComplexity.class);
		//$JUnit-END$
		return suite;
	}

}
