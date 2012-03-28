/**
 * TestOfUserRoles.java
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

import edu.harvard.mcz.imagecapture.data.Users;
import junit.framework.TestCase;

/**
 * @author Paul J. Morris
 *
 */
public class TestOfUserRoles extends TestCase {

	/**
	 * Test method for {@link edu.harvard.mcz.imagecapture.data.Users#isUserRole(java.lang.String)}.
	 */
	public void testIsUserRole() {
		Users user = new Users();
		user.setRole(Users.ROLE_DATAENTRY);
		assertTrue(user.isUserRole(Users.ROLE_DATAENTRY));
		assertFalse(user.isUserRole(Users.ROLE_FULL));
		assertFalse(user.isUserRole(Users.ROLE_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_CHIEF_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_ADMINISTRATOR));
		user.setRole(Users.ROLE_FULL);
		assertTrue(user.isUserRole(Users.ROLE_DATAENTRY));
		assertTrue(user.isUserRole(Users.ROLE_FULL));
		assertFalse(user.isUserRole(Users.ROLE_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_CHIEF_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_ADMINISTRATOR));
		user.setRole(Users.ROLE_EDITOR);
		assertTrue(user.isUserRole(Users.ROLE_DATAENTRY));
		assertTrue(user.isUserRole(Users.ROLE_FULL));
		assertTrue(user.isUserRole(Users.ROLE_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_CHIEF_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_ADMINISTRATOR));
		user.setRole(Users.ROLE_CHIEF_EDITOR);
		assertTrue(user.isUserRole(Users.ROLE_DATAENTRY));
		assertTrue(user.isUserRole(Users.ROLE_FULL));
		assertTrue(user.isUserRole(Users.ROLE_EDITOR));
		assertTrue(user.isUserRole(Users.ROLE_CHIEF_EDITOR));
		assertFalse(user.isUserRole(Users.ROLE_ADMINISTRATOR));
		user.setRole(Users.ROLE_ADMINISTRATOR);
		assertTrue(user.isUserRole(Users.ROLE_DATAENTRY));
		assertTrue(user.isUserRole(Users.ROLE_FULL));
		assertTrue(user.isUserRole(Users.ROLE_EDITOR));
		assertTrue(user.isUserRole(Users.ROLE_CHIEF_EDITOR));
		assertTrue(user.isUserRole(Users.ROLE_ADMINISTRATOR));
	}

}
