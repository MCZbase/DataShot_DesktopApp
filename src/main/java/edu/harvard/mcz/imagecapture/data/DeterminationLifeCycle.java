package edu.harvard.mcz.imagecapture.data;

// Generated Jan 23, 2009 8:12:35 AM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Determination.
 * @see edu.harvard.mcz.imagecapture.data.Determination
 * @author Hibernate Tools
 */
public class DeterminationLifeCycle {

	private static final Log log = LogFactory.getLog(DeterminationLifeCycle.class);


	public void persist(Determination transientInstance) throws SaveFailedException {
		log.debug("persisting Determination instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			   session.persist(transientInstance);
			   log.debug("persist successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save determination record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }	
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Determination instance) throws SaveFailedException {
		log.debug("attaching dirty Determination instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			    session.saveOrUpdate(instance);
			    log.debug("attach successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save determination record. " + e.getMessage());
			}			    
			try { session.close(); } catch (SessionException e) { }	
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Determination instance) throws SaveFailedException {
		log.debug("attaching clean Determination instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				session.lock(instance, LockMode.NONE);
				log.debug("attach successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save determination record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }	
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Determination persistentInstance) throws SaveFailedException {
		log.debug("deleting Determination instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				session.delete(persistentInstance);
			    log.debug("delete successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save determination record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }	
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Determination merge(Determination detachedInstance) throws SaveFailedException {
		log.debug("merging Determination instance");
		try {
			Determination result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			   result = (Determination) session.merge(detachedInstance);
			   log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save determination record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }			   
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Determination findById(java.lang.Long id) {
		log.debug("getting Determination instance with id: " + id);
		try {
			Determination instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (Determination) session.get(
						"edu.harvard.mcz.imagecapture.data.Determination",
						id);
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
	public List<Determination> findByExample(Determination instance) {
		log.debug("finding Determination instance by example");
		try {
			List<Determination> results = null; 
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<Determination>)session.createCriteria(
				"edu.harvard.mcz.imagecapture.data.Determination")
				.add(create(instance)).list();
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
}
