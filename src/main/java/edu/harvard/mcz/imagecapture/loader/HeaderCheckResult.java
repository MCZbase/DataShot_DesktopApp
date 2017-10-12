/**
 * HeaderCheckResult.java
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
package edu.harvard.mcz.imagecapture.loader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class HeaderCheckResult {

	private static final Log log = LogFactory.getLog(HeaderCheckResult.class);
	
	private boolean result;
	private StringBuilder message;
	
	public HeaderCheckResult() { 
		result = false;
		message = new StringBuilder();
	}
	
	/**
	 * @return the result
	 */
	public boolean isResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}
	/**
	 * @return the message as a string.
	 */
	public String getMessage() {
		if (message==null) { message = new StringBuilder(); } 
		return message.toString();
	}
	/**
	 * @param message the message to append to the message for this HeaderCheckResult
	 */
	public void addToMessage(String message) {
		if (this.message==null) { this.message = new StringBuilder(); } 
		if (this.message.length()>0) {this.message.append(":"); } 
		this.message.append(message);
	}
		
}
