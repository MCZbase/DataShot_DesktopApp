/**
 * BulkMedia.java
 * edu.harvard.mcz.imagecapture.data
 * Copyright © 2015 President and Fellows of Harvard College
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.Singleton;

/**
 * Class to prepare records for MCZbase Media Bulkloader
 * 
 * @author mole
 *
 */
public class BulkMedia {
	private static final Log log = LogFactory.getLog(BulkMedia.class);
	
	private String media_uri;
	private String mime_type;
	private String media_type;
	private String media_relationships;
	private List<String> media_labels;
	private String preview_uri;
	
	private String urlBase = "http://mczbase.mcz.harvard.edu/specimen_images/";
	private String fileBase = "images";
	
	public BulkMedia() { 
		media_type = "image";
		mime_type = "image/jpeg";
		init();
	}
	public BulkMedia(String media_type, String mime_type) { 
		this.media_type = media_type;
		this.mime_type = mime_type;
		init();
	}
	
	protected void init() { 
		media_labels = new ArrayList<String>();
		fileBase = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE);
		urlBase = Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASEURI);
	}
	
	public void setCatalogNumber(String guid) { 
		media_relationships = "shows cataloged_item=" + guid;
	}
	public void setOriginalFilename(String filename) { 
		media_labels.add("original filename=" + filename);
	}
	public void setMadeDate(String madeDate) { 
		media_labels.add("made date=" + madeDate);
	}
	
	// given a file, determine what URI it would be served up under, assuming urlBase and fileBase
	public boolean setURI(File file) { 
		boolean result = false;
		
		int location = file.getPath().indexOf(fileBase);
		log.debug(file.getPath());
		log.debug(fileBase);
		log.debug(location);
		if (location!=-1) {
			String rest = "";
			if (java.io.File.separator.equals("\\")) { 
			   // on windows, convert file path to uri path.
		       rest = file.getPath().substring(location + fileBase.length()).replace('\\', '/');
			} else { 
		       rest = file.getPath().substring(location + fileBase.length());
			}
			if (rest.startsWith("/") && urlBase.endsWith("/")) {
				// strip off leading separator to prevent occurrence of duplicated separators
				rest = rest.substring(1);
			}
			media_uri = urlBase + rest;
		    result = true;
		}
		return result;
	}
	
	public boolean setPreviewURI(File file) { 
		boolean result = false;
		
		int location = file.getPath().indexOf(fileBase);
		log.debug(file.getPath());
		log.debug(fileBase);
		log.debug(location);
		if (location!=-1) {
			String rest = "";
			if (java.io.File.separator.equals("\\")) { 
			   // on windows, convert file path to uri path.
		       rest = file.getPath().substring(location + fileBase.length()).replace('\\', '/');
			} else { 
		       rest = file.getPath().substring(location + fileBase.length());
			}
			if (rest.startsWith("/") && urlBase.endsWith("/")) {
				// strip off leading separator to prevent occurrence of duplicated separators
				rest = rest.substring(1);
			}
			preview_uri = urlBase + rest;
		    result = true;
		}
		return result;
	}	
	
	public void setMedia_URI(String uri) {
		this.media_uri = uri;
	}
	
	public static List<String> getHeaders() { 
		List<String> headers = new ArrayList<String>();
		headers.add("MEDIA_URI");
		headers.add("MIME_TYPE");
		headers.add("MEDIA_TYPE");
		headers.add("MEDIA_RELATIONSHIPS");
		headers.add("PREVIEW_URI");
		headers.add("MEDIA_LABELS");
		return headers;
	}
	
	public List<String> getData() { 
		List<String> row = new ArrayList<String>();
		row.add(media_uri);
		row.add(mime_type);
		row.add(media_type);
		row.add(media_relationships);
		row.add(preview_uri);
		StringBuffer ml = new StringBuffer();
		Iterator<String> i = media_labels.iterator();
		int count = 0;
		while (i.hasNext()) { 
			if (count>0) { 
				ml.append(";");
			}
			ml.append(i.next());
			count++;
		}
		row.add(ml.toString());
		return row;
	}	
	
}
