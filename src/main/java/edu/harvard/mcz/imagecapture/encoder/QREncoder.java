/**
 * QREncoder.java
 * edu.harvard.mcz.imagecapture.encoder
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
package edu.harvard.mcz.imagecapture.encoder;

import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;

/** QREncoder
 * 
 * @author Paul J. Morris
 *
 */
public class QREncoder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		UnitTrayLabel label = new UnitTrayLabel();
		label.setDrawerNumber("112.5");
		label.setFamily("Family");
		label.setSubfamily("");
		label.setTribe("");
		label.setGenus("Generic");
		label.setSpecificEpithet("Specific");
		label.setSubspecificEpithet("subspecificEpithet");
		label.setInfraspecificEpithet("");
		label.setInfraspecificRank("");
		label.setAuthorship("");

		LabelEncoder encoder = new LabelEncoder(label);
		
		//TODO: iText for pdf?
		
		
	} 

}
