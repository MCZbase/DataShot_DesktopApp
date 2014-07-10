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
public class MCZbaseAuthAgentNameLifeCycle {
	private static final Log log = LogFactory.getLog(MCZbaseAuthAgentNameLifeCycle.class);

	/**
	 * @return
	 */
	public List<MCZbaseAuthAgentName> findAll() {
		log.debug("finding all agent names");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<MCZbaseAuthAgentName> results = null;
			try { 
			    results = (List<MCZbaseAuthAgentName>) session.createQuery("from MCZbaseAuthAgentName h order by h.agent_name").setReadOnly(true).list();
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
	public List<MCZbaseAuthAgentName> findByExample(MCZbaseAuthAgentName pattern) {
		log.debug("finding agent names by example");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<MCZbaseAuthAgentName> results = null;
			try { 
				Criteria criteria = session.createCriteria("edu.harvard.mcz.imagecapture.data.MCZbaseAuthAgentName");
				criteria.add(create(pattern));
				//criteria.setReadOnly(true);
			    results = (List<MCZbaseAuthAgentName>) criteria.list();
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
