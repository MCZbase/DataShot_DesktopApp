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

/**
 * @author mole
 * 
 * Proxy object for MCZbase/Arctos GEOG_AUTH_REC table 
 * (higher geography authority file)
 * Use database link agent_name@mczbase_auth and public synonym 
 * mczbase_agent_name_auth in production Oracle, table below in test MySQL.
 *    create or replace public synonym MCZBASE_AUTH_AGENT_NAME for agent_name@MCZBASE_AUTH;
 *    select * from MCZBASE_AUTH_AGENT_NAME; 
 * Use database link and view in production Oracle, table below in test MySQL.
 *
	CREATE TABLE MCZBASE_AUTH_AGENT_NAME (
		    AGENT_NAME_ID bigint NOT NULL primary key auto_increment,  
		    AGENT_ID bigint, 
		    AGENT_NAME_TYPE VARCHAR(18), 
		    DONOR_CARD_PRESENT_FG int, 
		    AGENT_NAME VARCHAR(184)
		);	
 
 *
 */
public class MCZbaseAuthAgentName {
	
	private int agent_name_id; // AGENT_NAME_ID bigint
    private int agent_id; // donor_card_present_fg int,
    private String agent_name_type; // agent_name_type varchar(18 char) 
    private Integer donor_card_present_fg; // donor_card_present_fg int
    private String agent_name; // agent_name VARCHAR(184 char)
    
	/**
	 * @return the agent_name_id
	 */
	public int getAgent_name_id() {
		return agent_name_id;
	}
	/**
	 * @param agent_name_id the agent_name_id to set
	 */
	public void setAgent_name_id(int agent_name_id) {
		this.agent_name_id = agent_name_id;
	}
	/**
	 * @return the agent_id
	 */
	public int getAgent_id() {
		return agent_id;
	}
	/**
	 * @param agent_id the agent_id to set
	 */
	public void setAgent_id(int agent_id) {
		this.agent_id = agent_id;
	}
	/**
	 * @return the agent_name_type
	 */
	public String getAgent_name_type() {
		return agent_name_type;
	}
	/**
	 * @param agent_name_type the agent_name_type to set
	 */
	public void setAgent_name_type(String agent_name_type) {
		this.agent_name_type = agent_name_type;
	}
	/**
	 * @return the donor_card_present_fg
	 */
	public Integer getDonor_card_present_fg() {
		return donor_card_present_fg;
	}
	/**
	 * @param donor_card_present_fg the donor_card_present_fg to set
	 */
	public void setDonor_card_present_fg(Integer donor_card_present_fg) {
		this.donor_card_present_fg = donor_card_present_fg;
	}
	/**
	 * @return the agent_name
	 */
	public String getAgent_name() {
		return agent_name;
	}
	/**
	 * @param agent_name the agent_name to set
	 */
	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}
	
	public String toString() { 
		if (this.agent_name==null) { 
			return "";
		} else { 
			return agent_name;
		}
	}
    
}
