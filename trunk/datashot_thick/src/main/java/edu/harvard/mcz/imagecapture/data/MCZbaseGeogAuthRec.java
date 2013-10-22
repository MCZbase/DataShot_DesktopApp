/**
 * MCZbaseGeogAuthRec.java
 * edu.harvard.mcz.imagecapture.data
 * Copyright © 2013 President and Fellows of Harvard College
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 * 
 * Proxy object for MCZbase/Arctos GEOG_AUTH_REC table 
 * (higher geography authority file)
 *
	CREATE TABLE MCZBASE_GEOG_AUTH_REC (
		    GEOG_AUTH_REC_ID bigint NOT NULL primary key auto_increment,  
		    CONTINENT_OCEAN VARCHAR(70), 
		    COUNTRY VARCHAR(70), 
		    STATE_PROV VARCHAR(75), 
		    COUNTY VARCHAR(50), 
		    QUAD VARCHAR(30), 
		    FEATURE VARCHAR(50), 
		    ISLAND VARCHAR(50), 
		    ISLAND_GROUP VARCHAR(50), 
		    SEA VARCHAR(50), 
		    VALID_CATALOG_TERM_FG int NOT NULL, 
		    SOURCE_AUTHORITY VARCHAR(45) NOT NULL, 
		    HIGHER_GEOG VARCHAR(255), 
		    OCEAN_REGION VARCHAR(50), 
		    OCEAN_SUBREGION VARCHAR(50)
		);	
 
 *
 */
public class MCZbaseGeogAuthRec {
	private static final Log log = LogFactory.getLog(MCZbaseGeogAuthRec.class);
	
	private Long geog_auth_rec_id; // GEOG_AUTH_REC_ID
    private String continent_ocean; // CONTINENT_OCEAN VARCHAR(70), 
    private String country; // COUNTRY VARCHAR(70), 
    private String state_prov;  // STATE_PROV VARCHAR(75), 
    private String county; //COUNTY VARCHAR(50), 
    private String quad;   // QUAD VARCHAR(30), 
    private String feature;  //FEATURE VARCHAR(50), 
    private String island;  //ISLAND VARCHAR(50), 
    private String island_group; //ISLAND_GROUP VARCHAR(50), 
    private String sea;   //SEA VARCHAR(50), 
    private int valid_catalog_term_fg; //VALID_CATALOG_TERM_FG int NOT NULL, 
    private String source_authority;  //SOURCE_AUTHORITY VARCHAR(45) NOT NULL, 
    private String higher_geog; //HIGHER_GEOG VARCHAR(255), 
    private String ocean_region; //OCEAN_REGION VARCHAR(50), 
    private String ocean_subregion; //OCEAN_SUBREGION VARCHAR(50)	
	/**
	 * @return the geog_auth_rec_id
	 */
	public Long getGeog_auth_rec_id() {
		return geog_auth_rec_id;
	}
	/**
	 * @param geog_auth_rec_id the geog_auth_rec_id to set
	 */
	public void setGeog_auth_rec_id(Long geog_auth_rec_id) {
		this.geog_auth_rec_id = geog_auth_rec_id;
	}
	/**
	 * @return the continent_ocean
	 */
	public String getContinent_ocean() {
		return continent_ocean;
	}
	/**
	 * @param continent_ocean the continent_ocean to set
	 */
	public void setContinent_ocean(String continent_ocean) {
		this.continent_ocean = continent_ocean;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return the state_prov
	 */
	public String getState_prov() {
		return state_prov;
	}
	/**
	 * @param state_prov the state_prov to set
	 */
	public void setState_prov(String state_prov) {
		this.state_prov = state_prov;
	}
	/**
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}
	/**
	 * @param county the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
	}
	/**
	 * @return the quad
	 */
	public String getQuad() {
		return quad;
	}
	/**
	 * @param quad the quad to set
	 */
	public void setQuad(String quad) {
		this.quad = quad;
	}
	/**
	 * @return the feature
	 */
	public String getFeature() {
		return feature;
	}
	/**
	 * @param feature the feature to set
	 */
	public void setFeature(String feature) {
		this.feature = feature;
	}
	/**
	 * @return the island
	 */
	public String getIsland() {
		return island;
	}
	/**
	 * @param island the island to set
	 */
	public void setIsland(String island) {
		this.island = island;
	}
	/**
	 * @return the island_group
	 */
	public String getIsland_group() {
		return island_group;
	}
	/**
	 * @param island_group the island_group to set
	 */
	public void setIsland_group(String island_group) {
		this.island_group = island_group;
	}
	/**
	 * @return the sea
	 */
	public String getSea() {
		return sea;
	}
	/**
	 * @param sea the sea to set
	 */
	public void setSea(String sea) {
		this.sea = sea;
	}
	/**
	 * @return the valid_catalog_term_fg
	 */
	public int getValid_catalog_term_fg() {
		return valid_catalog_term_fg;
	}
	/**
	 * @param valid_catalog_term_fg the valid_catalog_term_fg to set
	 */
	public void setValid_catalog_term_fg(int valid_catalog_term_fg) {
		this.valid_catalog_term_fg = valid_catalog_term_fg;
	}
	/**
	 * @return the source_authority
	 */
	public String getSource_authority() {
		return source_authority;
	}
	/**
	 * @param source_authority the source_authority to set
	 */
	public void setSource_authority(String source_authority) {
		this.source_authority = source_authority;
	}
	/**
	 * @return the higher_geog
	 */
	public String getHigher_geog() {
		return higher_geog;
	}
	/**
	 * @param higher_geog the higher_geog to set
	 */
	public void setHigher_geog(String higher_geog) {
		this.higher_geog = higher_geog;
	}
	/**
	 * @return the ocean_region
	 */
	public String getOcean_region() {
		return ocean_region;
	}
	/**
	 * @param ocean_region the ocean_region to set
	 */
	public void setOcean_region(String ocean_region) {
		this.ocean_region = ocean_region;
	}
	/**
	 * @return the ocean_subregion
	 */
	public String getOcean_subregion() {
		return ocean_subregion;
	}
	/**
	 * @param ocean_subregion the ocean_subregion to set
	 */
	public void setOcean_subregion(String ocean_subregion) {
		this.ocean_subregion = ocean_subregion;
	}
	
	public String toString() { 
		if (this.higher_geog==null) { 
			return super.toString();
		} else { 
			return this.higher_geog;
		}
	}
	
}
