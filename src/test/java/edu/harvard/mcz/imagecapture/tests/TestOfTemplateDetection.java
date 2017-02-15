/**
 * TestOfTemplateDetection.java
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

import edu.harvard.mcz.imagecapture.DefaultPositionTemplateDetector;
import edu.harvard.mcz.imagecapture.PositionTemplate;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;
import junit.framework.TestCase;

/** TestOfTemplateDetection tests the ability of the DefaultPositionTemplateDetector to correctly 
 * detect templates for images with barcodes in standard positions.
 * 
 * @author Paul J. Morris
 *
 */
@Category(IntegrationTest.class)
public class TestOfTemplateDetection extends TestCase {

	/**
	 * @param name
	 */
	public TestOfTemplateDetection(String name) {
		super(name);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.DefaultPositionTemplateDetector#detectTemplateForImage(java.io.File)}.
	 */
	public void testDetectTemplateForImage() {
		DefaultPositionTemplateDetector detector = new DefaultPositionTemplateDetector();
		try {
			detector.detectTemplateForImage(new File(AllTests.FILE_INVALID_NAME));
			fail("Failed to throw UnreadableFileException for invalid file");
		} catch (UnreadableFileException e) {
			// pass
		}
        try {
			String template = detector.detectTemplateForImage(new File(this.getClass().getResource(AllTests.FILE_EMPTY).getFile()));
			assertEquals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS, template);
		} catch (UnreadableFileException e) {
			fail("Threw unexpected UnreadableFileException.  " + e.getMessage() );
		}
		
		// TODO: Test templates with example images here.
		
	}

}
