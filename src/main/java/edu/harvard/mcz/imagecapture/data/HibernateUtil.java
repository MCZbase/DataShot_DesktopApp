package edu.harvard.mcz.imagecapture.data;

import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.cfg.*;

import edu.harvard.mcz.imagecapture.LoginDialog;
import edu.harvard.mcz.imagecapture.MainFrame;
import edu.harvard.mcz.imagecapture.Singleton;

/**
 * Singleton class to obtain access to Hibernate sessions, used in the *LifeCycle classes.
 *  
 * Modified from the hibernate tutorial
 * http://www.hibernate.org/hib_docs/v3/reference/en-US/html/tutorial-firstapp.html
 * Changed singleton implementation to allow loading of credentials from config and dialog at runtime
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory = null;
	
	private static final Log log = LogFactory.getLog(HibernateUtil.class);
	
	public static void terminateSessionFactory() {
		try { 
			sessionFactory.getCurrentSession().cancelQuery();
			sessionFactory.getCurrentSession().clear();
			sessionFactory.getCurrentSession().close();
		} catch (Exception e) { 
		} finally { 
			try { 
				sessionFactory.close();
			} catch  (Exception e1) { 
			} finally { 
				sessionFactory = null;
			} 
		}
	}
	

	/**
	 *  Using the Hibernate configuration in Configuration from hibernate.cfg.xml
	 *  create a Hibernate sessionFactory.  Method is private as the the session factory 
	 *  should be a singleton, invoke getSessionFactory() to create or access a session.
	 *  
	 *  @see edu.harvard.mcz.imagecapture.data.HibernateUtil#getSessionFactory
	 */
	private static void createSessionFactory() {
		try {
			if (sessionFactory!=null) { 
			   terminateSessionFactory();
			}
		} catch (Exception e) { 
			log.error(e.getMessage());
		}
		try {
			// Create the Configuration from hibernate.cfg.xml
			Configuration config = new Configuration().configure();
			// Add authentication properties obtained from the user
			boolean success = false;
			LoginDialog loginDialog = new LoginDialog();
			while (!success && loginDialog.getResult()!=LoginDialog.RESULT_CANCEL) {
				// retrieve the connection parameters from hibernate.cfg.xml and load into the LoginDialog
				loginDialog.setConnection(config.getProperty("hibernate.connection.url"));
				loginDialog.setDialect(config.getProperty("hibernate.dialect"));
				loginDialog.setDriver(config.getProperty("hibernate.connection.driver_class"));
				// If the database username(schema) and password are present load them as well.
				String un = config.getProperty("hibernate.connection.username");
				if (un!=null) { 
					loginDialog.setSchemaName(config.getProperty("hibernate.connection.username"));
				}
				String pw = config.getProperty("hibernate.connection.password"); 
				if (pw!=null) { 
					loginDialog.setDBPassword(config.getProperty("hibernate.connection.password"));
				}					
	            // Display the LoginDialog as a modal dialog 
				loginDialog.setModalityType(ModalityType.APPLICATION_MODAL);
				loginDialog.setVisible(true);
				// Check authentication (starting with the database user(schema)/password.
				String username = "NotAuthenticated";
				if (loginDialog.getResult()==LoginDialog.RESULT_LOGIN) { 
					if (Singleton.getSingletonInstance().getMainFrame() != null) { 
			           Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					   Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Connecting to database");
					} 
					config.setProperty("hibernate.connection.password", loginDialog.getDBPassword());
					username = loginDialog.getSchemaName();
					config.setProperty("hibernate.connection.username", username);
					config.setProperty("hibernate.connection.url", loginDialog.getConnection());
					// Now create the SessionFactory from this configuration
					log.debug(config.getProperty("hibernate.connection.url"));
					try { 
						sessionFactory = config.buildSessionFactory();
					} catch (Throwable ex) {
						// Make sure you log the exception, as it might be swallowed
						System.out.println("Initial SessionFactory creation failed." + ex);
						throw new ExceptionInInitializerError(ex);
					}
					try { 
						// Check database authentication by beginning a transaction.
						Session session = sessionFactory.getCurrentSession();
						session.beginTransaction();
						session.close();
						// If an exception hasn't been thrown, dbuser/dbpassword has 
						// successfully authenticated against the database.
						// Now try authenticating the individual user by the email addresss/password that they provided.
						UsersLifeCycle uls = new UsersLifeCycle();
						Users userToLogin = new Users();
						userToLogin.setUsername(loginDialog.getUsername());
						userToLogin.setHash(loginDialog.getUserPasswordHash());
						List<Users> foundUser = uls.findByExample(userToLogin);
						if (foundUser.size()==1) {
 						    // There should be one and only one user returned.
							log.debug(foundUser.get(0).getHash());
							log.debug(loginDialog.getUserPasswordHash());
						    if (foundUser.get(0).getUsername().equals(loginDialog.getUsername()) && foundUser.get(0).getHash().equals(loginDialog.getUserPasswordHash())) {
							   // and that user must have exactly the username/password hash provided in the dialog. 
						       Singleton.getSingletonInstance().setCurrentUsername(userToLogin.getUsername());
						       success = true;
						       try { 
						           Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Connected as "+ foundUser.get(0).getFullname());
						           Singleton.getSingletonInstance().getMainFrame().setState(MainFrame.STATE_RUNNING);
						       } catch (NullPointerException ex) { 
								   // expected if we haven't instantiated a main frame.
						       }
						   } 
						} 
						if (!success) { 
							loginDialog = new LoginDialog();
							loginDialog.setStatus("Login failed: Incorrect Email and/or Password.");
							success = false;
							if (loginDialog.getUsername()!=null) { 
						        log.debug("Login failed for " + loginDialog.getUsername());
							}
							sessionFactory.close();
							sessionFactory = null;
							try { 
							    Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Login failed.");
							} catch (NullPointerException ex) { 
								// expected if we haven't instantiated a main frame.
							}
						}
					} catch (Throwable e) {
						e.printStackTrace();
						log.error(e);
						log.trace(e);
						System.out.println(e.getStackTrace());
						System.out.println("Initial SessionFactory creation failed." + e.getMessage());
						// TODO: don't reference UI if not running one...
						loginDialog = new LoginDialog();
						loginDialog.setStatus("Login failed: " + e.getCause());
						success = false;
						sessionFactory.close();
						sessionFactory = null;
						if (Singleton.getSingletonInstance().getMainFrame() != null) {
						    Singleton.getSingletonInstance().getMainFrame().setStatusMessage("Login failed.");
						} 
					}
					if (Singleton.getSingletonInstance().getMainFrame() != null) { 
					   Singleton.getSingletonInstance().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			ex.printStackTrace();
			System.out.println("Initial SessionFactory creation failed." + ex);
			System.out.println("Cause" + ex.getCause().getMessage());
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	
    /**
     * Call this method to obtain a Hibernate Session.
     * <BR>  
     * Usage:
     * <pre>
         Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	     session.beginTransaction();
	   </pre>
     * 
     * @return the Hibernate SessionFactory.
     */
	public static SessionFactory getSessionFactory() {
		if (sessionFactory==null) { 
			createSessionFactory();
		}
		return sessionFactory;
	}

}
	

