/**
 * GenusSpeciesCount.java
 * edu.harvard.mcz.imagecapture.data
 * Copyright © 2016 President and Fellows of Harvard College
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
package edu.harvard.mcz.imagecapture.struct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class GenusSpeciesCount {
	private static final Log log = LogFactory.getLog(GenusSpeciesCount.class);
	
	private Long count;
	private String genus;
	private String specificEpithet;
	
	/**
	 * @param count
	 * @param genus
	 * @param specificEpithet
	 */
	public GenusSpeciesCount(Long count, String genus, String specificEpithet) {
		this.count = count;
		this.genus = genus;
		this.specificEpithet = specificEpithet;
	}
	/**
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}
	/**
	 * @return the genus
	 */
	public String getGenus() {
		return genus;
	}
	/**
	 * @param genus the genus to set
	 */
	public void setGenus(String genus) {
		this.genus = genus;
	}
	/**
	 * @return the specificEpithet
	 */
	public String getSpecificEpithet() {
		return specificEpithet;
	}
	/**
	 * @param specificEpithet the specificEpithet to set
	 */
	public void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet;
	}
	
	
}
