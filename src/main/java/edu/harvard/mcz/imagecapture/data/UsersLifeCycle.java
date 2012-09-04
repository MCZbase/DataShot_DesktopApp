package edu.harvard.mcz.imagecapture.data;

// Generated Feb 5, 2009 5:23:55 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;

import edu.harvard.mcz.imagecapture.exceptions.NoSuchValueException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Users.
 * @see Users
 * @author Hibernate Tools
 */
public class UsersLifeCycle {

	private static final Log log = LogFactory.getLog(UsersLifeCycle.class);
	
	@SuppressWarnings("unchecked")
	public String getFullNameForUserName(String aUsername) throws NoSuchValueException { 
		String returnValue = "";
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try { 
		    List<Users> results = (List<Users>) session.createQuery("from Users as u where u.username = '"+aUsername+"'").list();
		    session.getTransaction().commit();
		    if (results.size()>0) { 
				returnValue = results.get(0).getFullname();
			} else { 
				throw new NoSuchValueException("Couldn't find a person with a username of [" + aUsername + "]");
			}
		} catch (HibernateException e) { 
			session.getTransaction().rollback();
			session.close();
			log.error("Can't retrieve username. " + e.getMessage());
		}
		
		return returnValue;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isUserAdministrator(int aUserID) { 
		boolean returnValue = false;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try { 
		    List<Users> results = (List<Users>) session.createQuery("from Users as u where role = '" + Users.ROLE_ADMINISTRATOR + "' and u.userid = '"+aUserID+"'").list();
		    session.getTransaction().commit();
		    if (results.size()==1) { 
				returnValue = results.get(0).getRole().equals("Administrator");				
			}
		} catch (HibernateException e) { 
			session.getTransaction().rollback();
			session.close();
			log.error("Can't retrieve username. " + e.getMessage());
		}
		
		return returnValue;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isUserChiefEditor(int aUserID)
	{ 
		boolean returnValue = false;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try { 
		    List<Users> results = (List<Users>) session.createQuery("from Users as u where u.userid = '"+aUserID+"'").list();
		    session.getTransaction().commit();
		    if (results.size()==1) { 
				returnValue = results.get(0).isUserRole(Users.ROLE_CHIEF_EDITOR);				
			}
		} catch (HibernateException e) { 
			session.getTransaction().rollback();
			session.close();
			log.error("Can't retrieve username. " + e.getMessage());
		}
		
		return returnValue;
		
	}

	public void persist(Users transientInstance) throws SaveFailedException {
		log.debug("persisting Users instance");
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
				throw new SaveFailedException("Save to Users table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Users instance) throws SaveFailedException {
		log.debug("attaching dirty Users instance");
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
				throw new SaveFailedException("Save to Users table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Users instance) {
		log.debug("attaching clean Users instance");
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

	public void delete(Users persistentInstance) throws SaveFailedException {
		log.debug("deleting Users instance");
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
				throw new SaveFailedException("Delete from Users table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Users merge(Users detachedInstance) throws SaveFailedException {
		log.debug("merging Users instance");
		try {
			Users result = detachedInstance;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				result = (Users) session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
				throw new SaveFailedException("Save to Users table failed. " + e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Users findById(java.lang.Integer id) {
		log.debug("getting Users instance with id: " + id);
		try {
			Users instance = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				instance = (Users) session.get("edu.harvard.mcz.imagecapture.data.Users", id);
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
	public List<Users> findByExample(Users instance) {
		log.debug("finding Users instance by example");
		try {
			List<Users> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<Users>) session.createCriteria("edu.harvard.mcz.imagecapture.data.Users").add(
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
	public static List<Users> findAll() {
		log.debug("finding all Users");
		try {
			List<Users> results = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				results = (List<Users>) session.createQuery("from Users u order by u.username ").list();
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
}
