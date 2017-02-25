/**
 * Barcode.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2009 President and Fellows of Harvard College
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
package edu.harvard.mcz.imagecapture;

import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeMatcher;

/** Recognition and construction of text strings found in MCZ-ENT barcode labels.  This class deals with the text
 * of the decoded barcode, which is expected be in the form MCZ-ENT[0-9]{8}.  This class doesn't decode or encode 
 * the text into a QRCode barcode, that is done with calls to the ZXing library.  
 * 
 * @author Paul J. Morris
 *
 */
public class MCZENTBarcode implements BarcodeMatcher, BarcodeBuilder {
	
	public static final String PATTERN = "MCZ-ENT[0-9]{8}";
	public static final String PREFIX = "MCZ-ENT";
	public static final int DIGITS = 8;
	
	private static final Log log = LogFactory.getLog(MCZENTBarcode.class); 
	
	public MCZENTBarcode() { 
		log.debug("Instantiating " + this.getClass().toString());
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.BarcodeBuilder#getNumber(java.lang.String)
	 */
	public Integer extractNumber(String aBarcode) {
		Integer result = null;
		if (matchesPattern(aBarcode)) { 
			result = Integer.valueOf(aBarcode.substring(aBarcode.length()-8,aBarcode.length()));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.BarcodeMatcher#matchesPattern(java.lang.String)
	 */
	public boolean matchesPattern(String aBarcode) { 
		boolean result = false;
		try { 
			result = aBarcode.matches("^"+PATTERN +"$");
		} catch (NullPointerException e) { 
			// if aBarcode was null, treat result as false.
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.BarcodeMatcher#matchFoundIn(java.lang.String)
	 */
	public boolean matchFoundIn(String aBarcode) { 
		boolean result = false;
		if (aBarcode!=null) {
			Pattern p = Pattern.compile(PATTERN);
			result = p.matcher(aBarcode).find();
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.BarcodeBuilder#makeFromNumber(java.lang.Integer)
	 */
	public String makeFromNumber(Integer aNumber) { 
		String result = null;
		if (aNumber!=null) { 
			if (aNumber.toString().length()<=DIGITS) { 
				if (aNumber>=0) { 
					String digits = Integer.valueOf(DIGITS).toString();
					result = PREFIX + String.format("%0"+ digits + "d", aNumber);
				}
			}
		}
		return result;
	}

	@Override
	public String makeGuidFromBarcode(String barcode) {
		String result = barcode;
		if (barcode.startsWith(PREFIX)) { 
			result = "MCZ:Ent:" + barcode.substring(7).replaceFirst("^0*", "");
		}
		return result;
	}
}
