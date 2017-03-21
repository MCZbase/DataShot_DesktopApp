/**
 * ExternalHistoryLifeCycle.java
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

/**
 * @author mole
 *
 */
public class ExternalHistoryLifeCycle {

	private static final Log log = LogFactory.getLog(ExternalHistoryLifeCycle.class);
	
	
	public void persist(ExternalHistory transientInstance) throws SaveFailedException {
		if (transientInstance.getExternalWorkflowProcess()!="") { 
			log.debug("persisting ExternalHistory instance");
			try {
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				try { 
					session.persist(transientInstance);
					session.getTransaction().commit();
					log.debug("persist successful");
				} catch (HibernateException e) { 
					session.getTransaction().rollback();
					log.error(e.getMessage());
					throw new SaveFailedException("Unable to save externalHistory record. " + e.getMessage());
				}
				try { session.close(); } catch (SessionException e) { }
			} catch (RuntimeException re) {
				log.error("persist failed", re);
				throw re;
			}
		}
	}

	public void attachDirty(ExternalHistory instance) throws SaveFailedException {
		log.debug("attaching dirty ExternalHistory instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				session.saveOrUpdate(instance);
				session.getTransaction().commit();
				log.debug("attach successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save externalHistory record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ExternalHistory instance) {
		log.debug("attaching clean ExternalHistory instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				session.lock(instance, LockMode.NONE);
				session.getTransaction().commit();
				log.debug("attach successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(ExternalHistory persistentInstance) throws SaveFailedException {
		log.debug("deleting ExternalHistory instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				session.delete(persistentInstance);
				session.getTransaction().commit();
				log.debug("delete successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to Delete externalHistory record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ExternalHistory merge(ExternalHistory detachedInstance) throws SaveFailedException {
		log.debug("merging ExternalHistory instance");
		try {
			ExternalHistory result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			    result = (ExternalHistory) session.merge(detachedInstance);
			    session.getTransaction().commit();
			    log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save externalHistory record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ExternalHistory findById(java.lang.Long id) {
		log.debug("getting ExternalHistory instance with id: " + id);
		try {
			ExternalHistory instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (ExternalHistory) session.get("edu.harvard.mcz.imagecapture.data.ExternalHistory", id);
				session.getTransaction().commit();
				if (instance == null) {
					log.debug("get successful, no instance found");
				} else {
					log.debug("get successful, instance found");
				}
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}	
	
}
