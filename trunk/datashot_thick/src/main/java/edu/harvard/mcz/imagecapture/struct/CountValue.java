/**
 * CountValue.java
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
 * Data Structure to hold counts and values for arbitrary select count(*), field from table queries
 * for arbitrary fields. 
 * 
 * @author mole
 *
 */
public class CountValue {
	private static final Log log = LogFactory.getLog(CountValue.class);
	
	private Integer count;
	private String value;
	
	
	/**
	 * @param count
	 * @param value
	 */
	public CountValue(Integer count, String value) {
		this.count = count;
		this.value = value;
	}
	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
