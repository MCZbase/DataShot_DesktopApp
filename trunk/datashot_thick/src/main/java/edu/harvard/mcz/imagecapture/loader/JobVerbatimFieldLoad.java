/**
 * JobVerbatimFieldLoad.java * edu.harvard.mcz.imagecapture.loader
 *
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
package edu.harvard.mcz.imagecapture.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.This;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunStatus;
import edu.harvard.mcz.imagecapture.interfaces.RunnableJob;
import edu.harvard.mcz.imagecapture.interfaces.RunnerListener;

/**
 * @author mole
 *
 */
public class JobVerbatimFieldLoad  implements RunnableJob, Runnable {
	private static final Log log = LogFactory.getLog(JobVerbatimFieldLoad.class);
	
	private int runStatus = RunStatus.STATUS_NEW;
	private Date startDateTime = null;
	private int percentComplete = 0;
	private List<RunnerListener> listeners = null;

	public JobVerbatimFieldLoad() { 
		listeners = new ArrayList<RunnerListener>();
		runStatus = RunStatus.STATUS_NEW;
		percentComplete = 0;
		startDateTime = null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		start();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#start()
	 */
	@Override
	public void start() {
		startDateTime = new Date();
		Singleton.getSingletonInstance().getJobList().addJob((RunnableJob)this);
		runStatus = RunStatus.STATUS_RUNNING;
		
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			log.debug("Selected file to load: " + file.getName() + ".");
			
			
			//TODO: ingest
			
		} else {
			//TODO: handle error condition
			log.error("File selection cancelled by user.");
		}		
		
		
		done();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#stop()
	 */
	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#cancel()
	 */
	@Override
	public boolean cancel() {
		runStatus = RunStatus.STATUS_TERMINATED;
		log.debug(this.getClass().getSimpleName() + " " + this.toString() +  "  recieved cancel signal");
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStatus()
	 */
	@Override
	public int getStatus() {
		return runStatus;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#percentComplete()
	 */
	@Override
	public int percentComplete() {
		return percentComplete;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#registerListener(edu.harvard.mcz.imagecapture.interfaces.RunnerListener)
	 */
	@Override
	public boolean registerListener(RunnerListener aJobListener) {
		return listeners.add(aJobListener);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getName()
	 */
	@Override
	public String getName() {
		return "Ingest Data obtained from an external process";
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.RunnableJob#getStartTime()
	 */
	@Override
	public Date getStartTime() {
		return startDateTime;
	}
	
	/**
	 * Cleanup when job is complete.
	 */
	protected void done() { 
	    runStatus = RunStatus.STATUS_DONE;
		notifyListeners(RunStatus.STATUS_DONE);
		Singleton.getSingletonInstance().getJobList().removeJob((RunnableJob)this);
	}	
	
	protected void notifyListeners(int anEvent) { 
		Singleton.getSingletonInstance().getMainFrame().notifyListener(anEvent, this);
		Iterator<RunnerListener> i = listeners.iterator();
		while (i.hasNext()) { 
			i.next().notifyListener(anEvent, this);
		}
	}
	
	protected void setPercentComplete(int aPercentage) { 
		//set value
		percentComplete = aPercentage;
		log.debug(percentComplete);
		//notify listeners
        notifyListeners(percentComplete);
	}		

}
