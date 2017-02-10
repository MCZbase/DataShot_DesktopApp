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

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Number.
 * @see edu.harvard.mcz.imagecapture.data.Number
 * @author Hibernate Tools
 */
public class NumberLifeCycle {

	private static final Log log = LogFactory.getLog(NumberLifeCycle.class);

	public void persist(Number transientInstance) throws SaveFailedException {
		log.debug("persisting Number instance");
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
				throw new SaveFailedException("Save to number table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Number instance) throws SaveFailedException {
		log.debug("attaching dirty Number instance");
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
				throw new SaveFailedException("Save to number table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Number instance) throws SaveFailedException {
		log.debug("attaching clean Number instance");
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
				throw new SaveFailedException("Save to number table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Number persistentInstance) throws SaveFailedException {
		log.debug("deleting Number instance");
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
				throw new SaveFailedException("Delete from number table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Number merge(Number detachedInstance) throws SaveFailedException {
		log.debug("merging Number instance");
		try {
			Number result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = (Number) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to number table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Number findById(java.lang.Long id) {
		log.debug("getting Number instance with id: " + id);
		try {
			Number instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (Number) session.get("edu.harvard.mcz.imagecapture.data.Number", id);
				if (instance == null) {
					log.debug("get successful, no instance found");
				} else {
					log.debug("get successful, instance found");
				}
				session.getTransaction().commit();
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
	public List<Number> findByExample(Number instance) {
		log.debug("finding Number instance by example");
		try {
			List<Number> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				results = (List<Number>) session.createCriteria("edu.harvard.mcz.imagecapture.data.Number").add(
						create(instance)).list();
				log.debug("find by example successful, result size: "
						+ results.size());
				session.getTransaction().commit();
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
	 * Retrieve, as a string array suitable for populating a pick list,
	 * the distinct values of Number.numberTypes.  
	 * Example usage: 
	 * <code>
	 		JComboBox jComboNumberTypes = new JComboBox(NumberLifeCycle.getDistinctTypes());
		    jComboNumberTypes.setEditable(true);
		    TableColumn typeColumn = jTableNumbers.getColumnModel().getColumn(NumberTableModel.COLUMN_TYPE);
		    typeColumn.setCellEditor(new DefaultCellEditor(jComboNumberTypes));
	   </code>
	 * 
	 * @return an array of strings consisting of { "", "Unknown", select distinct numberType from Number }
	 */
	@SuppressWarnings("unchecked")
	public static String[] getDistinctTypes() { 
		ArrayList<String> types = new ArrayList<String>();
		if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_SHOW_ALL_NUMBER_TYPES).equals("false")) {
		    return Number.getNumberTypeValues().toArray(new String[]{});	
		} else { 
		types.add("");    // put blank at top of list.
		types.add("Unknown");  // follow with "Unknown", see below.
		try {
			String sql = "Select distinct numberType from Number num where num.numberType is not null order by num.numberType  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				while (i.hasNext()) { 
					String value = (String)i.next();
					// add, only if value isn't the "Unknown" put at top of list above.
					if (!value.equals("Unknown")) {  
					    types.add(value);
					} 
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			String[] result = (String[]) types.toArray(new String[]{});
		} catch (RuntimeException re) {
			log.error(re);
			types = new ArrayList<String>();
		}
		}
		return (String[]) types.toArray(new String[]{});
	}

}
