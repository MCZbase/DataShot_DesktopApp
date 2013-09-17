package edu.harvard.mcz.imagecapture.data;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Proxy object for SpecimenPartAttribute
 * 
 * @author mole
 *
 */
public class SpecimenPartAttribute {
	
	private static final Log log = LogFactory.getLog(SpecimenPart.class);

	private Long specimenPartAttributeId; 
    private SpecimenPart specimenPartId;
    private String attributeType = "caste";
	private String attributeValue;
	private String attributeUnits = "";
	private String attributeRemark;
	private String attributeDeterminer; 
    private Date attributeDate;

	/**
	 * @param specimenPartAttributeId
	 * @param specimenPartId
	 * @param attributeType
	 * @param attributeValue
	 * @param attributeUnits
	 * @param attributeRemark
	 * @param attributeDeterminer
	 * @param attributeDate
	 */
	public SpecimenPartAttribute(Long specimenPartAttributeId,
			SpecimenPart specimenPartId, String attributeType,
			String attributeValue, String attributeUnits,
			String attributeRemark, String attributeDeterminer,
			Date attributeDate) {
		super();
		this.specimenPartAttributeId = specimenPartAttributeId;
		this.specimenPartId = specimenPartId;
		this.attributeType = attributeType;
		this.attributeValue = attributeValue;
		this.attributeUnits = attributeUnits;
		this.attributeRemark = attributeRemark;
		this.attributeDeterminer = attributeDeterminer;
		this.attributeDate = attributeDate;
		log.debug(specimenPartAttributeId);
	}

	/**
	 * 
	 */
	public SpecimenPartAttribute() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the specimenPartAttributeId
	 */
	public Long getSpecimenPartAttributeId() {
		return specimenPartAttributeId;
	}

	/**
	 * @param specimenPartAttributeId the specimenPartAttributeId to set
	 */
	public void setSpecimenPartAttributeId(Long specimenPartAttributeId) {
		this.specimenPartAttributeId = specimenPartAttributeId;
	}

	/**
	 * @return the specimenPartId
	 */
	public SpecimenPart getSpecimenPartId() {
		return specimenPartId;
	}

	/**
	 * @param specimenPartId the specimenPartId to set
	 */
	public void setSpecimenPartId(SpecimenPart specimenPartId) {
		this.specimenPartId = specimenPartId;
	}

	/**
	 * @return the attributeType
	 */
	public String getAttributeType() {
		return attributeType;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * @return the attributeUnits
	 */
	public String getAttributeUnits() {
		return attributeUnits;
	}

	/**
	 * @param attributeUnits the attributeUnits to set
	 */
	public void setAttributeUnits(String attributeUnits) {
		this.attributeUnits = attributeUnits;
	}

	/**
	 * @return the attributeRemark
	 */
	public String getAttributeRemark() {
		return attributeRemark;
	}

	/**
	 * @param attributeRemark the attributeRemark to set
	 */
	public void setAttributeRemark(String attributeRemark) {
		this.attributeRemark = attributeRemark;
	}

	/**
	 * @return the attributeDeterminer
	 */
	public String getAttributeDeterminer() {
		return attributeDeterminer;
	}

	/**
	 * @param attributeDeterminer the attributeDeterminer to set
	 */
	public void setAttributeDeterminer(String attributeDeterminer) {
		this.attributeDeterminer = attributeDeterminer;
	}

	/**
	 * @return the prepTypeAttributeDate
	 */
	public Date getAttributeDate() {
		return attributeDate;
	}

	/**
	 * @param prepTypeAttributeDate the prepTypeAttributeDate to set
	 */
	public void setAttributeDate(Date prepTypeAttributeDate) {
		this.attributeDate = prepTypeAttributeDate;
	}
}
