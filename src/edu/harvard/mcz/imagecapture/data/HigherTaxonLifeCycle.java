package edu.harvard.mcz.imagecapture.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionException;

import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import static org.hibernate.criterion.Example.create;

public class HigherTaxonLifeCycle {

	private static final Log log = LogFactory.getLog(HigherTaxonLifeCycle.class);


	public void persist(HigherTaxon transientInstance) throws SaveFailedException {
		log.debug("persisting HigherTaxon instance");
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
				throw new SaveFailedException("Save to HigherTaxon table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(HigherTaxon instance) throws SaveFailedException {
		log.debug("attaching dirty ICImage instance");
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
				throw new SaveFailedException("Save to HigherTaxon table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(HigherTaxon instance) throws SaveFailedException {
		log.debug("attaching clean HigherTaxon instance");
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
				throw new SaveFailedException("Save to HigherTaxon table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(HigherTaxon persistentInstance) throws SaveFailedException {
		log.debug("deleting HigherTaxon instance");
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
				throw new SaveFailedException("Delete from HigherTaxon table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public HigherTaxon merge(HigherTaxon detachedInstance) throws SaveFailedException {
		log.debug("merging ICImage instance");
		try {
			HigherTaxon result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = (HigherTaxon) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to HigherTaxon table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public HigherTaxon findById(java.lang.Long id) {
		log.debug("getting HigherTaxon instance with id: " + id);
		try {
			HigherTaxon instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (HigherTaxon) session.get("edu.harvard.mcz.imagecapture.data.HigherTaxon", id);
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
	public List<HigherTaxon> findByExample(HigherTaxon instance) {
		log.debug("finding ICImage instance by example");
		try {
		    List<HigherTaxon> results = null;	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				results = (List<HigherTaxon>) session.createCriteria(
				"edu.harvard.mcz.imagecapture.data.HigherTaxon").add(
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
	 * @return list of all higher files sorted by family
	 */
	@SuppressWarnings("unchecked")
	public List<HigherTaxon> findAll() {
		log.debug("finding all Higher Taxa");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<HigherTaxon> results = null;
			try { 
			    results = (List<HigherTaxon>) session.createQuery("From HigherTaxon ht order by ht.family").list();
			    log.debug("find all successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find all failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public static String[] selectDistinctFamily() { 
		List<String> result = new ArrayList();
		try {
			String sql = " Select distinct family from HigherTaxon ht where ht.family is not null ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				while (i.hasNext()) {
					String value = (String)i.next();
					result.add(value);
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return (String[]) result.toArray(new String[]{});
		} catch (RuntimeException re) {
			log.error(re);
			return new String[]{};
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String[] selectDistinctSubfamily(String family) { 
		List<String> result = new ArrayList();
		try {
			String sql = "";
			if (family==null || family.equals("")) { 
				sql = " Select distinct subfamily from HigherTaxon ht where ht.subfamily is not null order by subfamily ";
			} else {
				sql = " Select distinct subfamily from HigherTaxon ht where ht.family = '"+family.trim()+"' and ht.subfamily is not null order by subfamily ";
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				if (!i.hasNext()) {
					// No results, try without where family='?'  
					sql = " Select distinct subfamily from HigherTaxon ht where ht.subfamily is not null ";
					q = session.createQuery(sql);
					i = q.iterate();	
				}
				while (i.hasNext()) {
					String value = (String)i.next();
					result.add(value);
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			} 
			return (String[]) result.toArray(new String[]{});
		} catch (RuntimeException re) {
			log.error(re);
			return new String[]{};
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static String[] selectDistinctTribe(String subfamily) { 
		List<String> result = new ArrayList();
		try {
			String sql = " Select distinct tribe from HigherTaxon ht where ht.subfamily = '"+subfamily.trim()+"' and ht.tribe is not null ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				while (i.hasNext()) {
					String value = (String)i.next();
					result.add(value);
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return (String[]) result.toArray(new String[]{});
		} catch (RuntimeException re) {
			log.error(re);
			return new String[]{};
		}
	}		
	
	@SuppressWarnings("unchecked")
	public boolean isMatched(String aFamily, String aSubfamily) { 
		boolean result = false;
		try {
			String sql = "Select distinct family, subfamily from HigherTaxon ht  where soundex(ht.family) = soundex('" + aFamily + "') and soundex(ht.subfamily) = soundex('" + aSubfamily + "')  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				if (i.hasNext()) {
					result = true;
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			} 
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return false;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public boolean isMatched(String aFamily, String aSubfamily, String aTribe) { 
		boolean result = false;
		try {
			String sql = "Select distinct family, subfamily from HigherTaxon ht where soundex(ht.family) = soundex('" + aFamily + "') and soundex(ht.subfamily) = soundex('" + aSubfamily + "') and soundex(ht.tribe) = soundex('" + aTribe + "')  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				if (i.hasNext()) {
					result = true;
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return false;
		}
	}		

	/**
	 * Find the first soundex match to family in the Higher Taxon authority file.
	 * 
	 * @param aFamily
	 * @return a String containing the matched family name, null 
	 * if no match or a connection problem.
	 */
	@SuppressWarnings("unchecked")
	public String findMatch(String aFamily) { 
		String result = null;
		try {
			String sql = "Select distinct family from HigherTaxon ht  where soundex(ht.family) = soundex('" + aFamily + "')  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator results = q.list().iterator();
				if (results.hasNext()) {
					// retrieve one row.
				    result = (String)results.next();
				    // store the family and subfamily from that row in the array to return.
				    log.debug(result);
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			}
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return null;
		}
	}
	
	/**
	 * Find the first soundex match to both family and subfamily in the Higher Taxon authority file.
	 * 
	 * @param aFamily
	 * @param aSubfamily
	 * @return a String array with the 0th element being the family name and the 1st element being the subfamily name, null 
	 * if no match or a connection problem.
	 */
	@SuppressWarnings("unchecked")
	public String[] findMatch(String aFamily, String aSubfamily) { 
		String[] result = null;
		try {
			String sql = "Select distinct family, subfamily from HigherTaxon ht  where soundex(ht.family) = soundex('" + aFamily + "') and soundex(ht.subfamily) = soundex('" + aSubfamily + "')  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator results = q.list().iterator();
				if (results.hasNext()) {
	                // create a two element string array.
					result = new String[2];
					// retrieve one row.
				    Object[] row = (Object[]) results.next();
				    // store the family and subfamily from that row in the array to return.
				    result[0] = (String)row[0];
				    result[1] = (String)row[1];
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			} 
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return null;
		}
	}

	/** Return the first soundex match to family, subfamily, and tribe from the higher taxon authority file.
	 * 
	 * @param aFamily  the text string to check for a matching family name.
	 * @param aSubfamily the text string to check for a matching subfamily name.
	 * @param aTribe the text string to check for a matching tribe name.
	 * @return a string array of {family,subfamily,tribe} from the database, or null if no match or a connection
	 * problem.
	 */
	@SuppressWarnings("unchecked")
	public String[] findMatch(String aFamily, String aSubfamily, String aTribe) { 
		String[] result = null;
		try {
			String sql = "Select distinct family, subfamily, tribe from HigherTaxon ht  where " + 
			              "soundex(ht.family) = soundex('" + aFamily + "') and " + 
			              "soundex(ht.subfamily) = soundex('" + aSubfamily + "')and " + 
			              "soundex(ht.tribe) = soundex('" + aTribe + "')  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator results = q.list().iterator();
				if (results.hasNext()) {
	                // create a two element string array.
					result = new String[3];
					// retrieve one row.
				    Object[] row = (Object[]) results.next();
				    // store the family, subfamily, and tribe from that row in the array to return.
				    result[0] = (String)row[0];
				    result[1] = (String)row[1];
				    result[2] = (String)row[2];
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			} finally { 
			    try { session.close(); } catch (SessionException e) { }
			} 
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return null;
		}
	}
	
}
