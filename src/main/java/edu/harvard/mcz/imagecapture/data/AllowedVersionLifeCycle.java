/**
 * AllowedVersionLifeCycle.java
 *
 * Copyright © 2017 President and Fellows of Harvard College
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
package edu.harvard.mcz.imagecapture.data;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

import edu.harvard.mcz.imagecapture.ImageCaptureApp;

/**
 * @author mole
 *
 */
public class AllowedVersionLifeCycle {

	private static final Log log = LogFactory.getLog(AllowedVersionLifeCycle.class);
	
	/**
	 * Check to see if the allowed versions in the database include the version
	 * of the current ImageCaptureApp.  
	 * 
	 * @return true if an AllowedVersion.version in the database is found at the
	 * beginning of ImageCaptureApp.APP_VERSION, otherwise false.
	 */
	public static boolean isCurrentAllowed() throws HibernateException {
		boolean result = false;
		AllowedVersionLifeCycle als = new AllowedVersionLifeCycle();
		
		List<AllowedVersion> allowedVersions = als.findAll();
		Iterator<AllowedVersion> i = allowedVersions.iterator();
		while (i.hasNext()) { 
			String version = i.next().getVersion();
			String currentVersion = ImageCaptureApp.APP_VERSION;
			if (version!=null && currentVersion!=null &&
					version.length()<=currentVersion.length() &&
					currentVersion.startsWith(version)) 
			{ 
				result = true;
			}
		}
		
		
		return result;
	}
	
	/**
	 * Provide a human readable list of the allowed versions listed in the database.
	 * 
	 * @return string listing allowed versions according to database.
	 */
	public static String listAllowedVersions() { 
		StringBuilder allowed = new StringBuilder();
		AllowedVersionLifeCycle als = new AllowedVersionLifeCycle();
		try { 
			List<AllowedVersion> allowedVersions = als.findAll();
			Iterator<AllowedVersion> i = allowedVersions.iterator();
			String separator = "";
			while (i.hasNext()) {
				allowed.append(separator).append(i.next().getVersion());
				separator=", ";
			}
		} catch (Exception e) { 
			log.error(e.getMessage());
		}
		return allowed.toString();
	}
	
	public List<AllowedVersion> findAll() {
		log.debug("finding all AllowedVersions");
		try {
			List<AllowedVersion> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			
			session.beginTransaction();
			try { 
				results = (List<AllowedVersion>) session.createQuery("SELECT a FROM AllowedVersion a").list();
				session.getTransaction().commit();
				log.debug("find all successful, result size: "
						+ results.size());
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw e;
			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	
}
