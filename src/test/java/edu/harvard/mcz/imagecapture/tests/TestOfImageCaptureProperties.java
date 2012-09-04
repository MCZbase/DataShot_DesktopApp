/**
 * testOfImageCaptureProperties.java
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

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.Singleton;
import junit.framework.TestCase;

/** testOfImageCaptureProperties
 * 
 * @author Paul J. Morris
 *
 */
public class TestOfImageCaptureProperties extends TestCase {

	String imageBase;
	 
	/**
	 * @param name
	 */
	public TestOfImageCaptureProperties(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		// store the pre-existing value of KEY_IMAGEBASE
		imageBase = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);	
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		// restore the pre-existing value of KEY_IMAGEBASE
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(ImageCaptureProperties.KEY_IMAGEBASE, imageBase);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.ImageCaptureProperties#getPathBelowBase(java.io.File)}.
	 * Tests generation of path from base given a path including base and a filename.
	 * @see edu.harvard.mcz.imagecapture.ImageCaptureProperties#KEY_IMAGEBASE 
	 */
	public void testGetPathBelowBaseForFile() {
		// Test removing filename by testing with an existing file.
		// Won't work if full doesn't exist as a file on the filesystem.
		// Tests only the case of the current filesystem - run test on 
		// both a unix machine and a windows machine to validate this behavior
		// on both filesystems.  
		File full = new File(this.getClass().getResource(AllTests.FILE_VALID_BARCODE).getFile());
		String fullstring = full.getPath();
		// Create a base path from the actual path to this file, leaving out one directory
		String base = (new File(full.getParent())).getParent();
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(AllTests.FILE_VALID_BARCODE_PATH, ImageCaptureProperties.getPathBelowBase(full, "/"));		
		// test round trip re-assembly
		assertEquals(fullstring,
				ImageCaptureProperties.assemblePathWithBase(
						ImageCaptureProperties.getPathBelowBase(full, "/"),
						AllTests.FILE_VALID_BARCODE_FILENAME));
	}
	
	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.ImageCaptureProperties#getPathBelowBase(java.io.File)}.
	 * Tests generation of path from base given a path from root.
	 * @see edu.harvard.mcz.imagecapture.ImageCaptureProperties#KEY_IMAGEBASE 
	 */
	public void testGetPathBelowBase() {
		
		// Uses ImageCaptureProperties.getPathBelowBase(file, separator) which is intended only
		// for use here in unit testing.
		
		// Goal is to retrieve:
		// File imageFile = new File(IMAGEBASE + image.getPath() + image.getFilename());
		// So, given absolute path to a file, need to remove the filename and IMAGEBASE, leaving just
		// a path to store in ICImage.Path.
		// IMAGEBASE should end in a separator, image.getPath should not start with a separator, but end with one,
		// and image.getFilename should not start with a separator.  
		// image.getPath will need conversion to/from local separator.  
		
		// Test unix path  - will work with non-existant directory names.
		final String result = "test_files/test/";
		String base = "/mount/images/";
		String fullstring = "/mount/images/test_files/test/";
		File full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(result, ImageCaptureProperties.getPathBelowBase(full, "/"));

		// Test for variants of presence/absence of path terminator in base and file
		
		base = "/mount/images";
		fullstring = "/mount/images/test_files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(result, ImageCaptureProperties.getPathBelowBase(full, "/"));		

		base = "/mount/images/";
		fullstring = "/mount/images/test_files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(result, ImageCaptureProperties.getPathBelowBase(full, "/"));	
		
		base = "/mount/images";
		fullstring = "/mount/images/test_files/test";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(result, ImageCaptureProperties.getPathBelowBase(full, "/"));			

		// test for a space in path name
		base = "/mount/images/dir name/";
		fullstring = "/mount/images/dir name/test_files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(result, ImageCaptureProperties.getPathBelowBase(full, "/"));	

		// test for a space in path name
		base = "/mount/images/dir name/";
		fullstring = "/mount/images/dir name/test files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals("test files/test/", ImageCaptureProperties.getPathBelowBase(full, "/"	));	

		// test for non-canonical base path
		base = "/mount/images/../images/";
		fullstring = "/mount/images/../images/test_files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(result, ImageCaptureProperties.getPathBelowBase(full, "/"	));	
				
		// test for image in base directory
		base = "/home/mole/stuff/MCZ/mcz/insects/testImages/base/";
		fullstring = "/home/mole/stuff/MCZ/mcz/insects/testImages/base/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals("", ImageCaptureProperties.getPathBelowBase(full, "/"	));	
		
		// Test windows path 
		base = "Z:\\images\\";
		fullstring = "Z:\\images\\test_files\\test\\";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals("test_files\\test\\", ImageCaptureProperties.getPathBelowBase(full, "\\"));
		
		base = "Z:\\images\\test_files\\test\\";
		fullstring = "Z:\\images\\test_files\\test\\";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals("", ImageCaptureProperties.getPathBelowBase(full, "\\"));		
		
/*     Note: This test won't run, as there isn't a way to tell a non-existent file from 
 *     a non-existent directory.  To do this, run the test testGetPathBelowBaseForFile() 
 *     on both Windows and unix machines.
 *  
 		base = "Z:\\images\\";
		fullstring = "Z:\\images\\test_files\\test\\image.img";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals("test_files\\test\\", ImageCaptureProperties.getPathBelowBase(full, "\\"));
*/		
		// Test Windows path on Unix
		// Behave like a windows system and create a path from base
		base = "Z:\\images\\";
		fullstring = "Z:\\images\\test_files\\test\\";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		// first, generate the path below base as windows path
		assertEquals("test_files\\test\\", ImageCaptureProperties.getPathBelowBase(full, "\\"));
		String winPath = ImageCaptureProperties.getPathBelowBase(full, "\\");
		// second, test round trip re-assembly on unix
		base = "/media/images";
		fullstring = "/media/images/test_files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(fullstring + AllTests.FILE_VALID_BARCODE_FILENAME,
				ImageCaptureProperties.assemblePathWithBase(winPath,AllTests.FILE_VALID_BARCODE_FILENAME,"/"));
		
		// Test Unix path on Windows
		// Behave like a Unix System and create a path from base
		base = "/media/images/";
		fullstring = "/media/images/test_files/test/";
		full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		// first, generate the path below base as unix path
		assertEquals("test_files/test/", ImageCaptureProperties.getPathBelowBase(full, "/"));
		String unixPath = ImageCaptureProperties.getPathBelowBase(full, "/");
		// second, test round trip re-assembly on Windows
		base = "Z:\\images\\";
		fullstring = "Z:\\images\\test_files\\test\\";
		//full = new File(fullstring);
		Singleton.getSingletonInstance().getProperties().getProperties().setProperty(
				ImageCaptureProperties.KEY_IMAGEBASE, 
				base);
		assertEquals(fullstring + AllTests.FILE_VALID_BARCODE_FILENAME,
				ImageCaptureProperties.assemblePathWithBase(unixPath,AllTests.FILE_VALID_BARCODE_FILENAME,"\\"));
				
		
	}

}
