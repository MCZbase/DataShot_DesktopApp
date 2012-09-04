/**
 * TestofHashUtility.java
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

import edu.harvard.mcz.imagecapture.utility.HashUtility;
import junit.framework.TestCase;

/** TestofHashUtility
 * 
 * @author Paul J. Morris
 *
 */
public class TestofHashUtility extends TestCase {

	/**
	 * @param name
	 */
	public TestofHashUtility(String name) {
		super(name);
	}

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.utility.HashUtility#getSHA1Hash(java.lang.String)}.
	 */
	public void testGetSHA1Hash() {
		String string = "a";
		String hash = "86f7e437faa5a7fce15d1ddcb9eaeaea377667b8";
		assertEquals(hash, HashUtility.getSHA1Hash(string));
		string = "The quick red fox jumped over the lazy brown dog";
		hash = "73119267f916971817361c4ae8ed06111f3deaee";
        assertEquals(hash, HashUtility.getSHA1Hash(string));
        string = "䶑";  // utf-16 U+4D91  (sneeze)
        hash = "94aa69d55a9c75485a6a8604aacff8fb8e529112";  // hash returned by MySQL select sha1('䶑');
        assertEquals(hash, HashUtility.getSHA1Hash(string));
        string = "~{|}[\\]^_@<=>?@'$";   // some basic latin characters with \ escaped as \\
        hash = "a89252809482882a6820cdbe7d8ec4bc978491aa";
        assertEquals(hash, HashUtility.getSHA1Hash(string));
	} 

}
