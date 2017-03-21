/**
 * ExternalHistory.java
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
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class ExternalHistory {

	private static final Log log = LogFactory.getLog(ExternalHistory.class);
	
    private Long externalHistoryId;
    private Specimen specimen;
    private String externalWorkflowProcess;
    private Date externalWorkflowDate;
	/**
	 * @return the externalHistoryId
	 */
	public Long getExternalHistoryId() {
		return externalHistoryId;
	}
	/**
	 * @param externalHistoryId the externalHistoryId to set
	 */
	public void setExternalHistoryId(Long externalHistoryId) {
		this.externalHistoryId = externalHistoryId;
	}
	/**
	 * @return the externalWorkflowProcess
	 */
	public String getExternalWorkflowProcess() {
		return externalWorkflowProcess;
	}
	/**
	 * @param externalWorkflowProcess the externalWorkflowProcess to set
	 */
	public void setExternalWorkflowProcess(String externalWorkflowProcess) {
		this.externalWorkflowProcess = externalWorkflowProcess;
	}
	/**
	 * @return the externalWorkflowDate
	 */
	public Date getExternalWorkflowDate() {
		return externalWorkflowDate;
	}
	/**
	 * @param externalWorkflowDate the externalWorkflowDate to set
	 */
	public void setExternalWorkflowDate(Date externalWorkflowDate) {
		this.externalWorkflowDate = externalWorkflowDate;
	}
	/**
	 * @return the specimen
	 */
	public Specimen getSpecimen() {
		return specimen;
	}
	/**
	 * @param specimen the specimen to set
	 */
	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}
	
}
