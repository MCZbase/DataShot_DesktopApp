package edu.harvard.mcz.imagecapture.data;

// Generated Jan 23, 2009 8:12:35 AM by Hibernate Tools 3.2.2.GA

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Collector.
 * @see edu.harvard.mcz.imagecapture.data.Collector
 * @author Hibernate Tools
 */
public class CollectorLifeCycle {

	private static final Log log = LogFactory.getLog(CollectorLifeCycle.class);

	public void persist(Collector transientInstance) throws SaveFailedException {
		if (transientInstance.getCollectorName()!="") { 
			log.debug("persisting Collector instance");
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
					throw new SaveFailedException("Unable to save collector record. " + e.getMessage());
				}
				try { session.close(); } catch (SessionException e) { }
			} catch (RuntimeException re) {
				log.error("persist failed", re);
				throw re;
			}
		}
	}

	public void attachDirty(Collector instance) throws SaveFailedException {
		log.debug("attaching dirty Collector instance");
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
				throw new SaveFailedException("Unable to save collector record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Collector instance) {
		log.debug("attaching clean Collector instance");
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

	public void delete(Collector persistentInstance) throws SaveFailedException {
		log.debug("deleting Collector instance");
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
				throw new SaveFailedException("Unable to Delete collector record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Collector merge(Collector detachedInstance) throws SaveFailedException {
		log.debug("merging Collector instance");
		try {
			Collector result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			    result = (Collector) session.merge(detachedInstance);
			    session.getTransaction().commit();
			    log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Unable to save collector record. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Collector findById(java.lang.Long id) {
		log.debug("getting Collector instance with id: " + id);
		try {
			Collector instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (Collector) session.get("edu.harvard.mcz.imagecapture.data.Collector", id);
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
	public List<Collector> findByExample(Collector instance) {
		log.debug("finding Collector instance by example");
		try {
			List<Collector> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<Collector>) session.createCriteria(
						"edu.harvard.mcz.imagecapture.data.Collector").add(
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
	
	public String[] getDistinctCollectors() { 
		ArrayList<String> collections = new ArrayList<String>();
		collections.add("");    // put blank at top of list.
		try {
			String sql = "Select distinct collectorName from Collector col where col.collectorName is not null order by col.collectorName ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				while (i.hasNext()) { 
					String value = (String)i.next();
					// add, only if value isn't the "" put at top of list above.
					if (!value.equals("")) {  
					    collections.add(value.trim());
					} 
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			String[] result = (String[]) collections.toArray(new String[]{});
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return new String[]{};
		}
	}

	
}
