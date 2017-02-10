/** 
 *  Copyright © 2009 President and Fellows of Harvard College
 *  @author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture.data;

// Generated Jan 23, 2009 8:12:35 AM by Hibernate Tools 3.2.2.GA

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SessionException;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Example;
import org.hibernate.metadata.ClassMetadata;

import edu.harvard.mcz.imagecapture.MCZENTBarcode;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SpecimenExistsException;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.struct.CountValue;
import edu.harvard.mcz.imagecapture.struct.GenusSpeciesCount;
import edu.harvard.mcz.imagecapture.struct.VerbatimCount;
import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class Specimen.
 * @see edu.harvard.mcz.imagecapture.data.Specimen
 * @author Hibernate Tools
 */
public class SpecimenLifeCycle {

	private static final Log log = LogFactory.getLog(SpecimenLifeCycle.class);

	/**Log who changed this specimen when and what its current status is to the 
	 * tracking table.  
	 * 
	 * @param specimen the specimen for which the change is to be logged to the tracking table.
	 */
	protected void track(Specimen specimen) { 
    	Tracking t = new Tracking();
    	t.setEventType(specimen.getWorkFlowStatus());
    	t.setSpecimen(specimen);
    	t.setUser(Singleton.getSingletonInstance().getUserFullName());
    	TrackingLifeCycle tls = new TrackingLifeCycle();
    	try {
			tls.persist(t);
		} catch (SaveFailedException e) {
			// TODO Handle save error in UI
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/** Save a new specimen record, and add an entry in the tracking table.
	 * 
	 * @param transientInstance instance of a Specimen that doesn't have a matching
	 * database record which is to be saved as a new record in the database.
	 * 
	 * @throws SaveFailedException
	 * @throws SpecimenExistsException 
	 */
	public void persist(Specimen transientInstance) throws SaveFailedException, SpecimenExistsException {
		log.debug("persisting Specimen instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			   session.persist(transientInstance);
			   session.getTransaction().commit();
			   log.debug("persist successful");
			   track(transientInstance);
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				String message = e.getMessage();
				String pmessage = "";
				try {
					pmessage = e.getCause().getMessage();
				} catch (Exception ex) { 
					// expected if e doesn't have a cause.
				}
				log.error("persist failed", e);
				String existsPattern = "^Duplicate entry '.*' for key 'Barcode'$";
				if (message.matches(existsPattern) || pmessage.matches(existsPattern) ) { 
					 throw new SpecimenExistsException("Specimen record exists for " + transientInstance.getBarcode());
				} else { 
				     throw new SaveFailedException("Unable to save specimen " + transientInstance.getBarcode());
				}
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	/**Save or update an existing specimen record.
	 * 
	 * @param instance of a Specimen that that is to be saved.
	 * @throws SaveFailedException
	 */
	public void attachDirty(Specimen instance)  throws SaveFailedException {
		log.debug("attaching dirty Specimen instance");
		Iterator<Collector> i = instance.getCollectors().iterator();
		while (i.hasNext()) { 
			Collector col = i.next();
			if (col.getCollectorName()==null || col.getCollectorName().trim().length()==0 || col.getCollectorName().equals("[remove]")) { 
				instance.getCollectors().remove(col);
				CollectorLifeCycle cls = new CollectorLifeCycle();
				cls.delete(col);
			}
		}
		Iterator<Number> in = instance.getNumbers().iterator();
		while (in.hasNext()) { 
			Number num = in.next();
			if ( (num.getNumber()==null || num.getNumber().trim().length()==0 ) && 
				 (num.getNumberType()==null || num.getNumberType().length()==0)
				) { 
				instance.getNumbers().remove(num);
				NumberLifeCycle nls = new NumberLifeCycle();
				nls.delete(num);
			}
		}		
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try {
			   if (!instance.getLatLong().isEmpty()) { 
				   LatLong ll = instance.getLatLong().iterator().next();
				   if (ll.getSpecimenId()==null) { 
					   instance.getLatLong().iterator().next().setSpecimenId(instance);
				   }
			   }
			   session.saveOrUpdate(instance); 
			   session.getTransaction().commit();
			   log.debug("attach successful");
			   track(instance);
			} catch (HibernateException e) { 
			   session.getTransaction().rollback();
			   log.error("attach failed", e);
			   throw new SaveFailedException("Unable to save specimen " + instance.getBarcode());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	/** Re-associate a transient instance with a session.
	 * 
	 * @param instance
	 */
	public void attachClean(Specimen instance) {
		try { 
			log.debug(instance.getSpecimenParts().size());
		    log.debug(((SpecimenPart)instance.getSpecimenParts().toArray()[0]).getPartAttributeValuesConcat());
		} catch (Exception e) {
			 log.debug(e.getMessage());
		} 
		log.debug("attaching clean Specimen instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			    session.lock(instance, LockMode.NONE);
			    session.flush();
			    session.getTransaction().commit();
			    log.debug("attach successful");
				log.debug(instance.getSpecimenParts().size());
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
			    log.debug(e.getMessage(),e);
			    log.debug("Trying to attach dirty");
				try {
					attachDirty(instance);
				} catch (SaveFailedException e1) {
				    log.error("attach failed", e1);
					e1.printStackTrace();
				}
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}				
		try { 
		    log.debug(instance.getSpecimenParts().size());
		    log.debug(((SpecimenPart)instance.getSpecimenParts().toArray()[0]).getPartAttributeValuesConcat());
		} catch (Exception e) { 
			log.debug(e.getMessage());
		}
	}

	public void delete(Specimen persistentInstance) {
		log.debug("deleting Specimen instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
			    session.delete(persistentInstance);
			    session.getTransaction().commit();
			    log.debug("delete successful");
			} catch (HibernateException e) {
				session.getTransaction().rollback();
				log.error("delete failed", e);	
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	/** Update db record and log current status of record.
	 * 
	 * @param detachedInstance
	 * @return the current specimen record.
	 */
	public Specimen merge(Specimen detachedInstance) {
		log.debug("merging Specimen instance");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			try { 
				Specimen result = (Specimen)session.merge(detachedInstance);
				session.getTransaction().commit();
				log.debug("merge successful");
				try { session.close(); } catch (SessionException e) { }
				track(result);
				return result;
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				try { session.close(); } catch (SessionException e1) { }
				log.error("merge failed", e);
		        throw e;		
			}
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Specimen findById(java.lang.Long id) {
		log.debug("getting Specimen instance with id: " + id);
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Specimen instance = null;
			try { 
				instance = (Specimen)session.get("edu.harvard.mcz.imagecapture.data.Specimen", id);
				session.getTransaction().commit();
				if (instance == null) {
					log.debug("get successful, no instance found");
				} else {
					log.debug("get successful, instance found");
					log.debug(instance.getSpecimenParts().size());
				}
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error("get failed", e);
			}
			try { session.close(); } catch (SessionException e) { }
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Specimen> findAll() {
		log.debug("finding all Specimens");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<Specimen> results = null;
			try { 
			    results = (List<Specimen>) session.createQuery("From Specimen s order by s.barcode").list();
			    log.debug("find all successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find all failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("Find all failed.  ", re);
			throw re;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Specimen> findAllPage(int startAt, int fetchSize) {
		log.debug("finding " + fetchSize + " Specimens from " + startAt + "." );
		try {
			if (startAt < 0 || fetchSize < 0) { throw new RuntimeException("Negative value given for page size"); } 
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<Specimen> results = null;
			try { 
				Query query = session.createQuery("From Specimen s order by s.barcode");
				query.setFirstResult(startAt);
				query.setFetchSize(fetchSize);
			    results = (List<Specimen>) query.list();
			    log.debug("find all paged successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find all failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("Find all paged failed. ", re);
			throw re;
		}
	}	

	/** Find specimens with values matching those found in an example specimen instance, including links out 
	 * to related entities.  Like matching is enabled, so strings containing '%' will generate like where 
	 * criteria with the % as a wild card.  Matches are case insensitive.
	 * 
	 * @param instance 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Specimen> findByExampleLike(Specimen instance) {
		log.debug("finding Specimen instance by example with trackings, collectors, and images and like criteria");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<Specimen> results = null;
			try { 
				Criteria criteria = session.createCriteria("edu.harvard.mcz.imagecapture.data.Specimen");
				instance.setFlagAncilaryAlsoInMCZbase(null);
				instance.setFlagInBulkloader(null);
				instance.setFlagInMCZbase(null);
				
				Example example = create(instance).enableLike().ignoreCase();
				// Note: Criteria in example instance can be excluded here, or by setting their
				// values to null.  See the invocation of Specimen.clearDefaults in 
				// SearchDialog.getJButtonSearch()'s actionPerformed method.
				//example.excludeProperty("flagInBulkloader");
				criteria.add(example);
				
				criteria.setFetchMode("trackings", FetchMode.SELECT);
				criteria.setFetchMode("specimenParts", FetchMode.SELECT);
				criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				if (instance.getTrackings()!=null && instance.getTrackings().size()>0) {
					criteria.createCriteria("trackings",Criteria.INNER_JOIN).add(create(instance.getTrackings().toArray()[0]).enableLike());
				}
				if (instance.getICImages()!=null && instance.getICImages().size()>0) { 
					criteria.createCriteria("ICImages",Criteria.INNER_JOIN).add(create(instance.getICImages().toArray()[0]).enableLike().ignoreCase());
				}
				if (instance.getCollectors()!=null && instance.getCollectors().size()>0) { 
					criteria.createCriteria("collectors",Criteria.INNER_JOIN).add(create(instance.getCollectors().toArray()[0]).enableLike().ignoreCase());
				}
			    results = (List<Specimen>) criteria.list();
			    log.debug("find by example successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find by example failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
for (int i=0; i<results.size(); i++) { 		    
   try {
      log.debug("Parts: " + results.get(i).getSpecimenParts().size());
      log.debug("Parts: " + ((SpecimenPart)results.get(i).getSpecimenParts().toArray()[0]).getPartAttributeValuesConcat());
      log.debug("Part Attribute: " + ((SpecimenPartAttribute)((SpecimenPart)results.get(i).getSpecimenParts().toArray()[0]).getAttributeCollection().toArray()[0]).getSpecimenPartAttributeId());
   } catch (Exception e) {
	   log.debug(e.getMessage());
   }
}
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public List<Specimen> findByExample(Specimen instance) {
		log.debug("finding Specimen instance by example");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			List<Specimen> results = null;
			try { 
				Criteria criteria = session.createCriteria("edu.harvard.mcz.imagecapture.data.Specimen");
				criteria.add(create(instance));
				if (instance.getTrackings()!=null && instance.getTrackings().size()>0) {
					criteria.createCriteria("trackings").add(create(instance.getTrackings().toArray()[0]));;
				}
				if (instance.getICImages()!=null && instance.getICImages().size()>0) { 
					criteria.createCriteria("ICImages").add(create(instance.getICImages().toArray()[0]));
				}
			    results = (List<Specimen>) criteria.list();
			    log.debug("find by example successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find by example failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	@SuppressWarnings("unchecked")
	public String findSpecimenCount() {
			StringBuffer result = new StringBuffer();
			try {
				String sql = "Select count(*), workFlowStatus from Specimen group by workFlowStatus ";
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				try { 
					result.append("Specimen records: \n");
					session.beginTransaction();
					Iterator results = session.createQuery(sql).list().iterator();
					while ( results.hasNext() ) {
					    Object[] row = (Object[]) results.next();
					    Long count = (Long) row[0];
					    String status = (String) row[1];
						result.append(" " + status + "=" + count.toString() + "\n");
					}
					session.getTransaction().commit();
				} catch (HibernateException e) { 
					session.getTransaction().rollback();
					log.error(e.getMessage());
				}
				try { session.close(); } catch (SessionException e) { }
				return result.toString();
			} catch (RuntimeException re) {
				log.error(re);
				return result.toString();
			}
		}
	
	
	@SuppressWarnings("unchecked")
	/**
	 * Obtain counts of the number of specimens needing verbatim transcription by genus and species. 
	 * 
	 * @return
	 */
	public List<GenusSpeciesCount> countSpecimensForVerbatim() {
			ArrayList<GenusSpeciesCount> result = new ArrayList<GenusSpeciesCount>();
			try {
				String sql = "Select count(S), genus, specificEpithet from Specimen S where S.workFlowStatus = 'Taxon Entered' group by S.genus, S.specificEpithet ";
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				try { 
					session.beginTransaction();
					Iterator results = session.createQuery(sql).list().iterator();
					while ( results.hasNext() ) {
					    Object[] row = (Object[]) results.next();
					    Long count = Long.parseLong(row[0].toString());
					    String genus = (String) row[1];
					    String specificEpithet = (String) row[2];
						result.add(new GenusSpeciesCount(count, genus, specificEpithet));
					}
					session.getTransaction().commit();
				} catch (HibernateException e) { 
					session.getTransaction().rollback();
					log.error(e.getMessage());
				}
				try { session.close(); } catch (SessionException e) { }
			} catch (RuntimeException re) {
				log.error(re);
			}
			return result;
		}	
	
	/**
	 * Get counts of distinct values of verbatim data that has not yet been classified.
	 * 
	 * @return List of counts and distinct verbatim field values.
	 */
	public List<VerbatimCount> countDistinctVerbatimValues() {
		ArrayList<VerbatimCount> result = new ArrayList<VerbatimCount>();
		try {
			String sql = "Select count(S), S.verbatimLocality, S.dateNos, S.verbatimCollector, S.verbatimCollection, "
					+ "S.verbatimNumbers, S.verbatimUnclassifiedText "
					+ "from Specimen S "
					+ "where S.workFlowStatus = 'Verbatim Entered' "
					+ "group by S.verbatimLocality, S.dateNos, S.verbatimCollector, S.verbatimCollection, "
					+ "S.verbatimNumbers, S.verbatimUnclassifiedText ";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Iterator results = session.createQuery(sql).list().iterator();
				while ( results.hasNext() ) {
				    Object[] row = (Object[]) results.next();
				    Long count = Long.parseLong(row[0].toString());
				    String verbatimLocality = (String) row[1];
				    String dateNos = (String) row[2];
				    String verbatimCollector = (String) row[3];
				    String verbatimCollection = (String) row[4];
				    String verbatimNumbers = (String) row[5];
				    String verbatimUnclassifiedText = (String) row[6];
					result.add(new VerbatimCount(count.intValue(), verbatimLocality, dateNos, verbatimCollector, verbatimCollection, verbatimNumbers, verbatimUnclassifiedText));
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
		} catch (RuntimeException re) {
			log.error(re);
		}
		return result;
	}	
	
	
	
	@SuppressWarnings("unchecked")
	public List<Specimen> findForVerbatim(String genus, String specificEpithet, String workflowStatus) {
		log.debug("finding Specimen instances for verbatim capture");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			List<Specimen> results = null;
			try { 
				session.beginTransaction();
				Query query  =  session.createQuery("From Specimen as s where s.genus = ? and s.specificEpithet = ? and s.workFlowStatus = ? ");
				query.setParameter(0, genus);
				query.setParameter(1, specificEpithet);
				query.setParameter(2, workflowStatus);
				results = (List<Specimen>) query.list();
			    log.debug("find query successful, result size: " + results.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find by example failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public List<CountValue> findTaxaFromVerbatim(VerbatimCount verbatim) {
		log.debug("finding counts of taxa for verbatim values");
		List<CountValue> result = new ArrayList<CountValue>();
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query query  =  session.createQuery("Select count(s), s.genus, s.specificEpithet "
						+ "From Specimen as s "
						+ "where s.verbatimLocality = ? and s.dateNos = ? "
						+ "   and s.verbatimCollector = ? and s.verbatimCollection = ? "
						+ "   and s.verbatimNumbers = ? and s.verbatimUnclassifiedText = ? "
						);
				query.setParameter(0, verbatim.getVerbatimLocality());
				query.setParameter(1, verbatim.getVerbatimDate());
				query.setParameter(2, verbatim.getVerbatimCollector());
				query.setParameter(3, verbatim.getVerbatimCollection());
				query.setParameter(4, verbatim.getVerbatimNumbers());
				query.setParameter(5, verbatim.getVerbatimUnclassfiedText());
				Iterator i = query.list().iterator();
				while (i.hasNext()) { 
					Object[] row = (Object[]) i.next();
				    Long count = Long.parseLong(row[0].toString());
				    String genus = (String) row[1];
				    String specificEpithet = (String) row[2];
				    StringBuffer taxon = new StringBuffer().append(genus).append(" ").append(specificEpithet);
				    result.add(new CountValue(count.intValue(), taxon.toString().trim()));
				}
			    log.debug("count query successful, result size: " + result.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find for verbatim failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("find for verbatim failed", re);
			throw re;
		}
	}	
	
	@SuppressWarnings("unchecked")
	/**
	 * Find specimen records that are currently in state verbatim captured and which have 
	 * the provided values for verbatim fields.
	 * 
	 * @param verbatim
	 * @return
	 */
	public List<Specimen> findSpecimensFromVerbatim(VerbatimCount verbatim) {
		log.debug("finding specimens from verbatim values");
		List<Specimen> result = new ArrayList<Specimen>();
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query query  =  session.createQuery("From Specimen as s "
						+ "where s.verbatimLocality = ? and s.dateNos = ? "
						+ "   and s.verbatimCollector = ? and s.verbatimCollection = ? "
						+ "   and s.verbatimNumbers = ? and s.verbatimUnclassifiedText = ? "
						+ "   and s.workFlowStatus = '" + WorkFlowStatus.STAGE_VERBATIM + "' "
						);
				query.setParameter(0, verbatim.getVerbatimLocality());
				query.setParameter(1, verbatim.getVerbatimDate());
				query.setParameter(2, verbatim.getVerbatimCollector());
				query.setParameter(3, verbatim.getVerbatimCollection());
				query.setParameter(4, verbatim.getVerbatimNumbers());
				query.setParameter(5, verbatim.getVerbatimUnclassfiedText());
				result = query.list();

			    log.debug("specimen query successful, result size: " + result.size());
			    session.getTransaction().commit();
		    } catch (HibernateException e) {
		    	session.getTransaction().rollback();
		    	log.error("find specimens for verbatim failed", e);	
		    }
		    try { session.close(); } catch (SessionException e) { }
			return result;
		} catch (RuntimeException re) {
			log.error("find specimens for verbatim failed", re);
			throw re;
		}
	}		
	
	public int getFieldSize(String fieldName) { 
		int returnValue = 0; 
//		Specimen s = new Specimen();
		ClassMetadata specimenMetadata = HibernateUtil.getSessionFactory().getClassMetadata(Specimen.class);
		String[] p = specimenMetadata.getPropertyNames();
		for (int i=0; i < p.length; i++ ) { 
			System.out.println(p[i]);
			specimenMetadata.getPropertyValue(p[i], "length", EntityMode.MAP);
		}
		
//		Object[] propertyValues = specimenMetadata.getPropertyValues(s, EntityMode.POJO);
//		String[] propertyNames = specimenMetadata.getPropertyNames();
//		Type[] propertyTypes = specimenMetadata.getPropertyTypes();
//
//		// get a Map of all properties which are not collections or associations
//		Map namedValues = new HashMap();
//		for ( int i=0; i<propertyNames.length; i++ ) {
//		        System.out.println( propertyNames[i] + " : " + propertyValues[i] );
//		   
//		}
		
		
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	public String[] getDistinctCountries() { 
		ArrayList<String> collections = new ArrayList<String>();
		collections.add("");    // put blank at top of list.
		try {
			String sql = "Select distinct country from Specimen spe where spe.country is not null order by spe.country  ";
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
	
	@SuppressWarnings("unchecked")
	public String[] getDistinctCollections() { 
		ArrayList<String> collections = new ArrayList<String>();
		collections.add("");    // put blank at top of list.
		try {
			String sql = "Select distinct collection from Specimen spe where spe.collection is not null order by spe.collection  ";
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

	@SuppressWarnings("unchecked")
	public String[] getDistinctQuestions() { 
		ArrayList<String> collections = new ArrayList<String>();
		collections.add("");    // put blank at top of list.
		try {
			String sql = "Select distinct questions from Specimen spe where spe.questions is not null order by spe.questions  ";
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
	
	/** Barcodes are assigned in order (from 50,000).  Find barcode numbers that are missing
	 * from the sequence of specimen records.
	 * 
	 * @return a string array containing a list of barcodes that are missing from the sequence.
	 */
	public static String[] getMissingBarcodes() { 
		ArrayList<String> missing = new ArrayList<String>();
		try {
			String sql = "select " +
					"        (cast(substr(a.barcode,-8) as decimal(8,0)) + 1) " +
					"        from Specimen a " +
					"        where " +
					"        not exists " +
					"        (" +
					"            select 1 from Specimen b " +
					"            where " +
					"            cast(substr(b.barcode,-8) as decimal(8,0)) = (cast(substr(a.barcode,-8) as decimal(8,0)) + 1)" +
					"        ) " +
					"        and " +
					"        cast(substr(a.barcode,-8) as decimal(8,0)) not in " +
					"        ( " +
					"          select max(cast(substr(c.barcode,-8) as decimal(8,0))) from Specimen c where cast(substr(a.barcode,-8) as decimal(8,0)) > 49999 " +
					"        ) " +
					"        order by 1";
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			try { 
				session.beginTransaction();
				Query q = session.createSQLQuery(sql);
				List results = q.list();
				Iterator i = results.iterator();
				while (i.hasNext()) { 
					BigDecimal value = (BigDecimal)i.next();
					BarcodeBuilder builder = Singleton.getSingletonInstance().getBarcodeBuilder();
                    missing.add(builder.makeFromNumber(value.toBigInteger().intValue())); 
                    log.debug(value);
				}
				session.getTransaction().commit();
			} catch (HibernateException e) { 
				session.getTransaction().rollback();
				log.error(e.getMessage());
			}
			try { session.close(); } catch (SessionException e) { }
			String[] result = (String[]) missing.toArray(new String[]{});
			return result;
		} catch (RuntimeException re) {
			log.error(re);
			return new String[]{};
		}
	}		
	
	
	
	public static void main(String[] args) { 
		SpecimenLifeCycle s = new SpecimenLifeCycle();
		s.getFieldSize("");
	}
	
}
