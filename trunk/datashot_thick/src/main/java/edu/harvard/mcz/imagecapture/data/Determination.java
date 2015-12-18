package edu.harvard.mcz.imagecapture.data;

// Generated Jan 23, 2009 8:12:35 AM by Hibernate Tools 3.2.2.GA

/**
 * Determination generated by hbm2java
 */
public class Determination implements java.io.Serializable {

	private static final long serialVersionUID = 1284175685743514257L;
	
	private Long determinationId;
	private Specimen specimen;
	private String genus;
	private String specificEpithet;
	private String subspecificEpithet;
	private String infraspecificEpithet;
	private String infraspecificRank;
	private String authorship;
	private String unNamedForm;
	private String identifiedBy;
	private String typeStatus;
	private String speciesNumber;
	private String verbatimText;
	
	private String natureOfId;
	private String dateIdentified;
	private String remarks;	
	
	public Determination() {
		typeStatus = Specimen.STATUS_NOT_A_TYPE;
		natureOfId = NatureOfId.EXPERT_ID;
	}

	public Determination(Specimen specimen) {
		this.specimen = specimen;
		typeStatus = Specimen.STATUS_NOT_A_TYPE;
		natureOfId = NatureOfId.EXPERT_ID;
	}

	public Determination(Specimen specimen, String genus,
			String specificEpithet, String subspecificEpithet,
			String infraspecificEpithet, String infraspecificRank,
			String authorship, String unNamedForm,
			String identificationQualifier, String identifiedBy, String typeStatus, String speciesNumber) {
		this.specimen = specimen;
		this.genus = genus;
		this.specificEpithet = specificEpithet;
		this.subspecificEpithet = subspecificEpithet;
		this.infraspecificEpithet = infraspecificEpithet;
		this.infraspecificRank = infraspecificRank;
		this.authorship = authorship;
		this.unNamedForm = unNamedForm;
		this.identifiedBy = identifiedBy;
		this.typeStatus = typeStatus;
		this.speciesNumber = speciesNumber;
	}

	public Long getDeterminationId() {
		return this.determinationId;
	}

	public void setDeterminationId(Long determinationId) {
		this.determinationId = determinationId;
	}

	public Specimen getSpecimen() {
		return this.specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	public String getGenus() {
		return this.genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getSpecificEpithet() {
		return this.specificEpithet;
	}

	public void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet;
	}

	public String getSubspecificEpithet() {
		return this.subspecificEpithet;
	}

	public void setSubspecificEpithet(String subspecificEpithet) {
		this.subspecificEpithet = subspecificEpithet;
	}

	public String getInfraspecificEpithet() {
		return this.infraspecificEpithet;
	}

	public void setInfraspecificEpithet(String infraspecificEpithet) {
		this.infraspecificEpithet = infraspecificEpithet;
	}

	public String getInfraspecificRank() {
		return this.infraspecificRank;
	}

	public void setInfraspecificRank(String infraspecificRank) {
		this.infraspecificRank = infraspecificRank;
	}

	public String getAuthorship() {
		return this.authorship;
	}

	public void setAuthorship(String authorship) {
		this.authorship = authorship;
	}

	public String getUnNamedForm() {
		return this.unNamedForm;
	}

	public void setUnNamedForm(String unNamedForm) {
		this.unNamedForm = unNamedForm;
	}

	public String getIdentifiedBy() {
		return this.identifiedBy;
	}

	public void setIdentifiedBy(String identifiedBy) {
		this.identifiedBy = identifiedBy;
	}
	
	public String getTypeStatus() { 
		return this.typeStatus;
	}

	public void setTypeStatus(String typeStatus) { 
		this.typeStatus = typeStatus;
	}

	/**
	 * @return the speciesNumber
	 */
	public String getSpeciesNumber() {
		return speciesNumber;
	}

	/**
	 * @param speciesNumber the speciesNumber to set
	 */
	public void setSpeciesNumber(String speciesNumber) {
		this.speciesNumber = speciesNumber;
	}

	/**
	 * @return the verbatimText
	 */
	public String getVerbatimText() {
		return verbatimText;
	}

	/**
	 * @param verbatimText the verbatimText to set
	 */
	public void setVerbatimText(String verbatimText) {
		this.verbatimText = verbatimText;
	}

	/**
	 * @return the natureOfId
	 */
	public String getNatureOfId() {
		return natureOfId;
	}

	/**
	 * @param natureOfId the natureOfId to set
	 */
	public void setNatureOfId(String natureOfId) {
		this.natureOfId = natureOfId;
	}

	/**
	 * @return the dateIdentified
	 */
	public String getDateIdentified() {
		return dateIdentified;
	}

	/**
	 * @param dateIdentified the dateIdentified to set
	 */
	public void setDateIdentified(String dateIdentified) {
		this.dateIdentified = dateIdentified;
	}

	/**
	 * @return the identificationRemarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param identificationRemarks the remarks to set
	 */
	public void setRemarks(String identificationRemarks) {
		this.remarks = identificationRemarks;
	}
	
}