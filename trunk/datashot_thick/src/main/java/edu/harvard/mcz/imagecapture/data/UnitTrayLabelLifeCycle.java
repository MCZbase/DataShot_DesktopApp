/**
 * UnitTrayLabelLifeCycle.java
 * edu.harvard.mcz.imagecapture.data
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
package edu.harvard.mcz.imagecapture.data;

import static org.hibernate.criterion.Example.create;

import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

/** UnitTrayLabelLifeCycle
 * 
 * @author Paul J. Morris
 *
 */
public class UnitTrayLabelLifeCycle  {
	
private static final Log log = LogFactory.getLog(UnitTrayLabelLifeCycle.class);
	

	public void persist(UnitTrayLabel transientInstance) throws SaveFailedException {
		log.debug("persisting UnitTrayLabel instance");
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
				throw new SaveFailedException("Save to UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(UnitTrayLabel instance) throws SaveFailedException {
		log.debug("attaching dirty UnitTrayLabel instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try  {
				session.saveOrUpdate(instance);
				session.getTransaction().commit();
				log.debug("attach successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public void attachClean(UnitTrayLabel instance) {
		log.debug("attaching clean UnitTrayLabel instance");
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

	public void delete(UnitTrayLabel persistentInstance) throws SaveFailedException {
		log.debug("deleting UnitTrayLabel instance");
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
				throw new SaveFailedException("Delete from UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public UnitTrayLabel merge(UnitTrayLabel detachedInstance) throws SaveFailedException {
		log.debug("merging UnitTrayLabel instance");
		try {
			UnitTrayLabel result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = (UnitTrayLabel) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to UnitTrayLabel table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public UnitTrayLabel findById(java.lang.Integer id) {
		log.debug("getting UnitTrayLabel instance with id: " + id);
		try {
			UnitTrayLabel instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (UnitTrayLabel) session.get("edu.harvard.mcz.imagecapture.data.UnitTrayLabel", id);
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

	@SuppressWarnings("unchecked")
	public List<UnitTrayLabel> findByExample(UnitTrayLabel instance) {
		log.debug("finding UnitTrayLabel instance by example");
		try {
			List<UnitTrayLabel> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<UnitTrayLabel>) session.createCriteria("edu.harvard.mcz.imagecapture.data.UnitTrayLabel").add(
						create(instance)).list();
				session.getTransaction().commit();
				log.debug("find by example successful, result size: "
						+ results.size());
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());

			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UnitTrayLabel> findAll() {
		log.debug("finding all UnitTrayLabel");
		try {
			List<UnitTrayLabel> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<UnitTrayLabel>) session.createQuery("from UnitTrayLabel u order by u.ordinal, u.family, u.subfamily, u.tribe, u.genus, u.specificEpithet ").list();
				session.getTransaction().commit();
				log.debug("find by example successful, result size: "
						+ results.size());
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
	
	public Integer findMaxOrdinal() { 
		log.debug("finding max ordinal in UnitTrayLabel");
		Integer result = 0;
		
		try {

			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				SQLQuery query = session.createSQLQuery("select max(Ordinal) from UNIT_TRAY_LABEL");
				List queryresult = query.list();
				if (!queryresult.isEmpty()) { 
					// MySQL returns an integer, Oracle returns a BigDecimal
					// Need to cast from either in a system independent way.
					// NOTE: This will fail if maximum value of ordinal exceeds the size of Integer.
					String temp  = queryresult.get(0).toString();
					result = Integer.valueOf(temp);
					log.debug(result);
				}
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}

		} catch (RuntimeException re) {
			log.error("find max ordinal failed", re);
			throw re;
		}
		
		
		return result;
	}

}
