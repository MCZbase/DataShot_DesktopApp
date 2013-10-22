/**
 * MCZbaseGeogAuthRecLifeCycle.java
 * edu.harvard.mcz.imagecapture.data
 * Copyright © 2013 President and Fellows of Harvard College
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

import static org.hibernate.criterion.Example.create;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

/**
 * @author mole
 *
 */
public class MCZbaseGeogAuthRecLifeCycle {
	private static final Log log = LogFactory.getLog(MCZbaseGeogAuthRecLifeCycle.class);

	/**
	 * @return
	 */
	public List<MCZbaseGeogAuthRec> findAll() {
		log.debug("finding all Higher geographies");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<MCZbaseGeogAuthRec> results = null;
			try { 
			    results = (List<MCZbaseGeogAuthRec>) session.createQuery("from MCZbaseGeogAuthRec h order by h.higher_geog").setReadOnly(true).list();
			    log.debug("find all successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find all failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("Find all failed.  ", re);
			throw re;
		}
	}

	/**
	 * @param pattern
	 * @return
	 */
	public List<MCZbaseGeogAuthRec> findByExample(MCZbaseGeogAuthRec pattern) {
		log.debug("finding Higher Geographies by example");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<MCZbaseGeogAuthRec> results = null;
			try { 
				Criteria criteria = session.createCriteria("edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRec");
				criteria.add(create(pattern));
				//criteria.setReadOnly(true);
			    results = (List<MCZbaseGeogAuthRec>) criteria.list();
			    log.debug("find by example successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find by example failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	
}
