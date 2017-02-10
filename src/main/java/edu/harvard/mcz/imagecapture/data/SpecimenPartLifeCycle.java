/**
 * SpecimenPartLifeCycle.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SpecimenExistsException;

/**
 * @author mole
 *
 */
public class SpecimenPartLifeCycle {
	private static final Log log = LogFactory.getLog(SpecimenPartLifeCycle.class);
	
	
	/** Save a new specimen record, and add an entry in the tracking table.
	 * 
	 * @param transientInstance instance of a Specimen that doesn't have a matching
	 * database record which is to be saved as a new record in the database.
	 * 
	 * @throws SaveFailedException
	 * @throws SpecimenExistsException 
	 */
	public void persist(SpecimenPart transientInstance) throws SaveFailedException {
		log.debug("persisting SpecimenPart instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			   session.persist(transientInstance);
			   session.getTransaction().commit();
			   log.debug("persist successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error("persist failed", e);
				throw new SaveFailedException("Unable to save specimenPart ");
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}	
	
	/** Re-associate a transient instance with a session.
	 * 
	 * @param instance
	 */
	public void attachClean(SpecimenPart instance) {
		log.debug("attaching clean SpecimenPart instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			    session.lock(instance, LockMode.NONE);
			    session.flush();
			    session.getTransaction().commit();
			    log.debug("attach successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error("attach failed", e);
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}				
	}	
	
	/**Save or update an existing specimen part record.
	 * 
	 * @param instance of a SpecimenPart that that is to be saved.
	 * @throws SaveFailedException
	 */
	public void attachDirty(SpecimenPart instance)  throws SaveFailedException {
		log.debug("attaching dirty SpecimenPart instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
			   session.saveOrUpdate(instance); 
			   session.getTransaction().commit();
			   log.debug("attach successful");
			} catch (HibernateException e) { 
			   session.getTransaction().rollback();
			   log.error("attach failed", e);
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}	
	
	
	/** Update db record and log current status of record.
	 * 
	 * @param detachedInstance
	 * @return the current specimen record.
	 */
	public SpecimenPart merge(SpecimenPart detachedInstance) {
		log.debug("merging SpecimenPart instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				SpecimenPart result = (SpecimenPart)session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
				try { session.close(); } catch (SessionException e) { }
				return result;
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				try { session.close(); } catch (SessionException e1) { }
				log.error("merge failed", e);
		        throw e;		
			}
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	/**Save or update an existing specimen part record.
	 * 
	 * @param instance of a SpecimenPart that that is to be removed.
	 * @throws SaveFailedException
	 */
	public void remove(SpecimenPart instance)  throws SaveFailedException {
		log.debug("attaching dirty SpecimenPart instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
			   session.delete(instance); 
			   session.getTransaction().commit();
			   log.debug("delete successful");
			} catch (HibernateException e) { 
			   session.getTransaction().rollback();
			   log.error("delete failed", e);
			   throw new SaveFailedException("Unable to delete.");
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}	
	
}
