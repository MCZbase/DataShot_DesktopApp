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
import org.hibernate.Session;
import org.hibernate.SessionException;

import edu.harvard.mcz.imagecapture.PositionTemplate;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class ICImage.
 * Refactored from generated EJB home class to use with local singleton HibernateUtil for sessions instead of EJB.
 * Renamed from Home to LifeCycle to prevent overwriting by Hibernate Tools
 * @see edu.harvard.mcz.imagecapture.data.ICImage
 * @author Hibernate Tools
 */
public class ICImageLifeCycle {

	private static final Log log = LogFactory.getLog(ICImageLifeCycle.class);


	public void persist(ICImage transientInstance) throws SaveFailedException {
		log.debug("persisting ICImage instance");
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
				throw new SaveFailedException("Save to Image table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(ICImage instance) throws SaveFailedException {
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
				throw new SaveFailedException("Save to image table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ICImage instance) throws SaveFailedException {
		log.debug("attaching clean ICImage instance");
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
				throw new SaveFailedException("Save to image table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(ICImage persistentInstance) throws SaveFailedException {
		log.debug("deleting ICImage instance");
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
				throw new SaveFailedException("Delete from image table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ICImage merge(ICImage detachedInstance) throws SaveFailedException {
		log.debug("merging ICImage instance");
		try {
			ICImage result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = (ICImage) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to image table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ICImage findById(java.lang.Long id) {
		log.debug("getting ICImage instance with id: " + id);
		try {
			ICImage instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (ICImage) session.get("edu.harvard.mcz.imagecapture.data.ICImage", id);
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
	public List<ICImage> findByExample(ICImage instance) {
		log.debug("finding ICImage instance by example");
		try {
		    List<ICImage> results = null;	
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
				results = (List<ICImage>) session.createCriteria(
				"edu.harvard.mcz.imagecapture.data.ICImage").add(
						create(instance)).list();
				log.debug("find by example successful, result size: "
						+ results.size());
				session.getTransaction().commit();
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

	/**
	 * @return list of all image files sorted by filename
	 */
	@SuppressWarnings("unchecked")
	public List<ICImage> findAll() {
		log.debug("finding all Images");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<ICImage> results = null;
			try { 
			    results = (List<ICImage>) session.createQuery("From ICImage i order by i.filename").list();
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
	
	/**
	 * Find images with a particular path.
	 * 
	 * @return list of all image files with a particular path.
	 */
	@SuppressWarnings("unchecked")
	public List<ICImage> findAllInDir(String path) {
		log.debug("finding all Images with path " + path);
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<ICImage> results = null;
			try { 
			    results = (List<ICImage>) session.createQuery("From ICImage i where Path = '"+ path +"' order by i.filename").list();
			    log.debug("find in directory successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find in dir failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find in dir failed", re);
			throw re;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static List<ICImage> findMismatchedImages() {
		log.debug("finding Images with barcode missmatches ");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<ICImage> results = null;
			try { 
			    results = (List<ICImage>) session.createQuery("From ICImage i where (rawBarcode <> rawExifBarcode or (rawBarcode is null) or (rawExifBarcode is null)) order by i.filename").list();
			    log.debug("find missmatches successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find missmatches failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find missmatches failed", re);
			throw re;
		}
	}		
	
	
	@SuppressWarnings("unchecked")
	public static List<ICImage> findDrawerImages(String aDrawerNumber) {
		log.debug("finding Images for drawer " + aDrawerNumber);
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<ICImage> results = null;
			try { 
			    results = (List<ICImage>) session.createQuery("From ICImage i where SpecimenId is null and DrawerNumber = '"+ aDrawerNumber.trim()+"' order by i.filename").list();
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
	public static List<ICImage> findNotDrawerNoTemplateImages() {
		log.debug("finding Images for template " + PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS);
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<ICImage> results = null;
			try { 
			    results = (List<ICImage>) session.createQuery("From ICImage i where SpecimenId is not null and TemplateId = '"+ PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS.trim()+"' order by i.filename").list();
			    log.debug("find all whole image only records with specimens successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find all whole image only records failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find all whole image only records failed", re);
			throw re;
		}
	}		
	
	/** Obtain an array of distinct values of paths suitable for populating a picklist.
	 * Example usage: 
	 * <code>
	        ICImageLifeCycle ils = new ICImageLifeCycle();
	 		JComboBox jComboPaths = new JComboBox(ils.getDistinctPaths());
		    jComboPaths.setEditable(true);
		    TableColumn pathColumn = jTableImages.getColumnModel().getColumn(ICImageListTableModel.COL_PATH);
		    pathColumn.setCellEditor(new DefaultCellEditor(jComboPaths));
	   </code>
	 * 
	 * @return a string array containing the distinct values of IMAGE path with a "" as the 
	 * first item in the array.
	 */
	@SuppressWarnings("unchecked")
	public String[] getDistinctPaths() { 
		ArrayList<String> types = new ArrayList<String>();
		types.add("");    // put blank at top of list.
		try {
			String sql = "Select distinct path from ICImage im where im.path is not null order by im.path  ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createQuery(sql);
				Iterator i = q.iterate();
				while (i.hasNext()) { 
					String value = (String)i.next();
					types.add(value);
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			String[] result = (String[]) types.toArray(new String[]{});
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return new String[]{};
		}
	}	
	
}
