package edu.harvard.mcz.imagecapture.data;

/**
 * HigherTaxon, authority file of Family, Subfamily, and Tribe names.
 * 
 * @author Paul J. Morris
 *
 */
public class HigherTaxon implements java.io.Serializable {
	
	private static final long serialVersionUID = -5729385642306510832L;
	private Integer id;
	private String family;
	private String subfamily;
	private String tribe;

	public HigherTaxon() {
	}

	public HigherTaxon(String family, String subfamily, String tribe) {
         this.family = family;
         this.subfamily = subfamily;
         this.tribe = tribe;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(String subfamily) {
		this.subfamily = subfamily;
	}

	public String getTribe() {
		return tribe;
	}

	public void setTribe(String tribe) {
		this.tribe = tribe;
	}




}
