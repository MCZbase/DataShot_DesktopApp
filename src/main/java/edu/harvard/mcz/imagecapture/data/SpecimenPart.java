package edu.harvard.mcz.imagecapture.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

/**
 * Proxy object for SpecimenPart
 * 
 * @author mole
 *
 *
 */
public class SpecimenPart {
	
	public static final String[] PARTNAMES = {"whole animal", "partial animal"};
	public static final String[] PRESERVENAMES = {"pinned", "pointed"};
	
	private static final Log log = LogFactory.getLog(SpecimenPart.class);
	
	private Long specimenPartId; 
    private Specimen specimenId;
    private String partName = "whole animal";
    private String preserveMethod = "pinned";
    private int lotCount = 1;  // Coll_Object.lot_count
    private String lotCountModifier;  // Coll_Object.lot_count_modifier 
    private Collection<SpecimenPartAttribute> attributeCollection;

    public SpecimenPart() { 
    }
    
	/**
	 * @param specimenPartId
	 * @param specimenId
	 * @param partName
	 * @param preserveMethod
	 * @param lotCount
	 * @param lotCountModifier
	 * @param attributeCollection
	 */
	public SpecimenPart(Long specimenPartId, Specimen specimenId,
			String partName, String preserveMethod, int lotCount,
			String lotCountModifier,
			Collection<SpecimenPartAttribute> attributeCollection) {
		super();
		this.specimenPartId = specimenPartId;
		this.specimenId = specimenId;
		this.partName = partName;
		this.preserveMethod = preserveMethod;
		this.lotCount = lotCount;
		this.lotCountModifier = lotCountModifier;
		this.attributeCollection = attributeCollection;
	}

	/**
	 * @return the specimenPartId
	 */
	public Long getSpecimenPartId() {
		return specimenPartId;
	}

	/**
	 * @param specimenPartId the specimenPartId to set
	 */
	public void setSpecimenPartId(Long specimenPartId) {
		this.specimenPartId = specimenPartId;
	}

	/**
	 * @return the specimenId
	 */
	public Specimen getSpecimenId() {
		return specimenId;
	}

	/**
	 * @param specimenId the specimenId to set
	 */
	public void setSpecimenId(Specimen specimenId) {
		this.specimenId = specimenId;
	}

	/**
	 * @return the partName
	 */
	public String getPartName() {
		return partName;
	}

	/**
	 * @param partName the partName to set
	 */
	public void setPartName(String partName) {
		this.partName = partName;
	}

	/**
	 * @return the preserveMethod
	 */
	public String getPreserveMethod() {
		return preserveMethod;
	}

	/**
	 * @param preserveMethod the preserveMethod to set
	 */
	public void setPreserveMethod(String preserveMethod) {
		this.preserveMethod = preserveMethod;
	}

	/**
	 * @return the lotCount
	 */
	public int getLotCount() {
		return lotCount;
	}

	/**
	 * @param lotCount the lotCount to set
	 */
	public void setLotCount(int lotCount) {
		this.lotCount = lotCount;
	}

	/**
	 * @return the lotCountModifier
	 */
	public String getLotCountModifier() {
		return lotCountModifier;
	}

	/**
	 * @param lotCountModifier the lotCountModifier to set
	 */
	public void setLotCountModifier(String lotCountModifier) {
		this.lotCountModifier = lotCountModifier;
	}

	/**
	 * @return the attributeCollection
	 */
	public Collection<SpecimenPartAttribute> getAttributeCollection() {
		if (attributeCollection==null) { 
			attributeCollection = new ArrayList<SpecimenPartAttribute>();
			SpecimenPartAttributeLifeCycle spals = new SpecimenPartAttributeLifeCycle();
			SpecimenPartAttribute example = new SpecimenPartAttribute();
			example.setSpecimenPartId(this);
			example.setAttributeType(null);
			example.setAttributeValue(null);
			example.setAttributeUnits(null);
			example.setAttributeRemark(null);
			example.setAttributeDeterminer(null);
			example.setAttributeDate(null);
			//attributeCollection.addAll(spals.findByExample(example));
			attributeCollection.addAll(spals.findBySpecimenPart(this));
		}
		return attributeCollection;
	}

	/**
	 * @param attributeCollection the attributeCollection to set
	 */
	public void setAttributeCollection(
			Collection<SpecimenPartAttribute> attributeCollection) {
		this.attributeCollection = attributeCollection;
	}    
    
	/**
	 * Obtain human readable list of attribute types and values for the 
	 * specimen part attributes associated with this specimen part.
	 * 
	 * @return string containing concatenated list of attribute types, values, and units.  
	 * If attribute collection is empty, returns an empty string.
	 */
	public String getPartAttributeValuesConcat() { 
		StringBuffer result = new StringBuffer();
		Iterator<SpecimenPartAttribute> i = getAttributeCollection().iterator();
		int counter = 0;
		while (i.hasNext()) { 
			SpecimenPartAttribute attribute = i.next();
			if (counter>0) { 
				result.append(", ");
			}
			result.append(attribute.getAttributeType()).append(':').append(attribute.getAttributeValue());
			if (attribute.getAttributeUnits()!=null) { 
			    result.append(attribute.getAttributeUnits());
			}
			counter++;
			log.debug(counter);
		}
		log.debug(result.toString());
		return result.toString();
	}
	
}