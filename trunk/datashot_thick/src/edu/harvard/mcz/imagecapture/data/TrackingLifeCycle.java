package edu.harvard.mcz.imagecapture.data;

// Generated Jan 23, 2009 8:12:35 AM by Hibernate Tools 3.2.2.GA

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Tracking.
 * @see edu.harvard.mcz.imagecapture.data.Tracking
 * @author Hibernate Tools
 */
public class TrackingLifeCycle {

	private static final Log log = LogFactory.getLog(TrackingLifeCycle.class);


	public void persist(Tracking transientInstance) throws SaveFailedException {
		log.debug("persisting Tracking instance");
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
				throw new SaveFailedException("Save to tracking table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Tracking instance) {
		log.debug("attaching dirty Tracking instance");
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
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Tracking instance) throws SaveFailedException {
		log.debug("attaching clean Tracking instance");
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
				throw new SaveFailedException("Save to tracking table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Tracking persistentInstance) throws SaveFailedException {
		log.debug("deleting Tracking instance");
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
				throw new SaveFailedException("Save to tracking table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Tracking merge(Tracking detachedInstance) throws SaveFailedException {
		log.debug("merging Tracking instance");
		try {
			Tracking result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {  
				result = (Tracking) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to tracking table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Tracking findById(java.lang.Long id) {
		log.debug("getting Tracking instance with id: " + id);
		try {
			Tracking instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (Tracking) session.get("edu.harvard.mcz.imagecapture.data.Tracking", id);
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
	public List<Tracking> findByExample(Tracking instance) {
		log.debug("finding Tracking instance by example");
		try {
			List<Tracking> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<Tracking>) session.createCriteria(
							"edu.harvard.mcz.imagecapture.data.Tracking")
							.addOrder(Order.asc("eventDateTime"))
							.add(create(instance)).list();
			    log.debug("find by example successful, result size: " +  results.size());
			    session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				System.out.println(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	
	
	
	@SuppressWarnings("unchecked")
	public List<Specimen> findSpecimensByUser(String user) {
		log.debug("finding all Tracking instances");
		try {
			List<Specimen> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			results = session.createQuery("select Specimen From Tracking t join Specimen where user = " + user + " order by t.eventDateTime ").list();
			log.debug("find all successful, result size: "
					+ results.size());
			System.out.println("find all successful, result size: "
					+ results.size());
			session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				System.out.println(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/** 
	 * Find the event tracking records for a specimen by its SpecimenId.  
	 * Given a Specimen.SpecimenId = Tracking.SpecimenId, return a list of Trackings with that SpecimenId.  
	 * 
	 * @param specimenId the SpecimenId for the specimen to find tracking records for.
	 * @return a list of the tracking records for that specimen.
	 */
	@SuppressWarnings("unchecked")
	public List<Tracking> findBySpecimenId(Long specimenId) {
		log.debug("finding all Tracking instances");
		try {
			List<Tracking> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			results = session.createQuery("From Tracking t where SpecimenId = " + Long.toString(specimenId) + " order by t.eventDateTime ").list();
			log.debug("find all successful, result size: "
					+ results.size());
			System.out.println("find all successful, result size: "
					+ results.size());
			session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				System.out.println(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/**
	 * Find the event tracking records for a specimen. 
	 * 
	 * Doesn't appear to return result in Oracle.  Mapping problem?  Works in MySQL. 
	 *  
	 * @param aSpecimen
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Tracking> findBySpecimen(Specimen aSpecimen) {
		log.debug("finding Tracking instance by example");
		try {
			// Find by example won't navigate out into associations.  
			// Need to nest createCriteria for related table.
			List<Tracking> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<Tracking>) session.createCriteria(
							"edu.harvard.mcz.imagecapture.data.Tracking")
							.addOrder(Order.asc("eventDateTime"))
							.createCriteria("specimen")
							.add(create(aSpecimen))
							.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
							.list();
			    log.debug("find by specimen successful, result size: " +  results.size());
			    session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				System.out.println(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}	
	

	@SuppressWarnings("unchecked")
	public List<Tracking> findAll() {
		log.debug("finding all Tracking instances");
		try {
			List<Tracking> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			results = session.createQuery("From Tracking t order by t.eventDateTime").list();
			log.debug("find all successful, result size: "
					+ results.size());
			System.out.println("find all successful, result size: "
					+ results.size());
			session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				System.out.println(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
	/** Obtain a list of the distinct users who have one or more entries in the tracking table.  This list 
	 * may or may not correspond to the list of users in the Users table.  
	 * 
	 * @return a string array of users, with a leading "" element, suitable for populating a pick list.
	 */
	public String[] getDistinctUsers() { 
		ArrayList<String> collections = new ArrayList<String>();
		collections.add("");    // put blank at top of list.
		try {
			String sql = "Select distinct user from Tracking t where t.user is not null order by t.user  ";
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
