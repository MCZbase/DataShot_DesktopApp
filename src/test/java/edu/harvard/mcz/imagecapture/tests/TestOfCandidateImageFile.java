/**
 * TestOfCandidateImageFile.java
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

import org.junit.experimental.categories.Category;

import edu.harvard.mcz.imagecapture.CandidateImageFile;
import edu.harvard.mcz.imagecapture.PositionTemplate;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import junit.framework.TestCase;

/** TestOfCandidateImageFile tests construction and some capabilities of CandidateImageFile.
 * 
 * @see edu.harvard.mcz.imagecapture.CandidateImageFile
 * @author Paul J. Morris
 *
 */
@Category(IntegrationTest.class)
public class TestOfCandidateImageFile extends TestCase {

	/**
	 * @param name
	 */
	public TestOfCandidateImageFile(String name) {
		super(name);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.CandidateImageFile#CandidateImageFile(java.io.File, edu.harvard.mcz.imagecapture.PositionTemplate)}.
	 */
	public void testCandidateImageFile() {
		CandidateImageFile file;
		try {
			file = new CandidateImageFile(new File(this.getClass().getResource(AllTests.FILE_EMPTY).getFile()), new PositionTemplate());
			file.getClass(); // added to suppress FindBugs DLS_DEAD_LOCAL_STORE
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException. " + e.getMessage());
		}
		try {
			file = new CandidateImageFile(new File(AllTests.FILE_INVALID_NAME), new PositionTemplate());
			file.getClass(); // added to suppress FindBugs DLS_DEAD_LOCAL_STORE
			fail("Failed to throw UnreadableFileException for nonexistent file.");
		} catch (UnreadableFileException e) {
			// pass
		}
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.CandidateImageFile#isFileReadable()}.
	 */
	public void testIsFileReadable() {
		CandidateImageFile file = null;
		try {
			file = new CandidateImageFile(new File(this.getClass().getResource(AllTests.FILE_EMPTY).getFile()), new PositionTemplate());
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException. " + e.getMessage());
		}
		boolean ok = false;
		try {
			ok = file.isFileReadable();
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException. " + e.getMessage());
		}
		assertTrue(ok);
		try {
			file = new CandidateImageFile(new File(AllTests.FILE_INVALID_NAME), new PositionTemplate());
			file.getClass();  // added to suppress FindBugs DLS_DEAD_LOCAL_STORE
			fail("Failed to throw UnreadableFileException for invalid filename");
			try {
				ok = file.isFileReadable();
				fail("Failed to throw UnreadableFileException for invalid filename");
			} catch (UnreadableFileException e) {
				// pass - sort of, but we shouldn't have been able to get here.
			}	
		} catch (UnreadableFileException e) {
			// pass
		}	
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.CandidateImageFile#setFile(java.io.File, edu.harvard.mcz.imagecapture.PositionTemplate)}. 
	 */
	public void testSetFile()  {
		CandidateImageFile file;
		try {
			file = new CandidateImageFile(new File(this.getClass().getResource(AllTests.FILE_EMPTY).getFile()), new PositionTemplate());
			try {
				file.setFile(new File(AllTests.FILE_INVALID_NAME), new PositionTemplate());
				fail("Failed to throw UnreadableFileException for invalid filename.");
			} catch (UnreadableFileException e) {
				// pass
			}
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException. " + e.getMessage());
		}

	}

}
