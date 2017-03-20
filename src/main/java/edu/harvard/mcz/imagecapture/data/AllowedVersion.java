/**
 * AllowedVersion.java
 *
 * Copyright © 2017 President and Fellows of Harvard College
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture.data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
@Entity
@Table(name = "allowed_version", catalog = "lepidoptera", schema = "")
@NamedQueries({
    @NamedQuery(name = "AllowedVersion.findAll", query = "SELECT a FROM AllowedVersion a")
    })
public class AllowedVersion {

	private static final Log log = LogFactory.getLog(AllowedVersion.class);
	
	@Id
    @GeneratedValue(generator="AllowedNumberSeq")
    @SequenceGenerator(name="AllowedNumberSeq",sequenceName="SEQ_ALLOWEDVERSIONID", allocationSize=1)
    @Basic(optional = false)
    @Column(name = "allowed_version_id", nullable = false)
	private Long allowedVersionId;
	
	@Column(name="version", nullable=false)
	private String version;
	/**
	 * @return the allowedVersionId
	 */
	public Long getAllowedVersionId() {
		return allowedVersionId;
	}
	/**
	 * @param allowedVersionId the allowedVersionId to set
	 */
	public void setAllowedVersionId(Long allowedVersionId) {
		this.allowedVersionId = allowedVersionId;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	
}
