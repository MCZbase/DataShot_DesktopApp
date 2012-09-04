/**
 * TaxonNameReturner.java
 * edu.harvard.mcz.imagecapture.interfaces
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
package edu.harvard.mcz.imagecapture.interfaces;

/**
 * Interface for classes that are able to return atomic taxon name components. 
 * Intended as the return interface from a taxon name parser, and thus doesn't
 * include methods to set a string to parse or to parse the string, these can be
 * included in the instantiation of a concrete instance of a parser class, for
 * example:
 * TaxonNameReturner parser = new ConcreteTaxonNameParser(aStringToParse);
 * String authorship = parser.getAuthorship();
 * 
 * @author Paul J. Morris
 *
 */
public interface TaxonNameReturner {

	/**
	 * @return the authorship
	 */
	public String getAuthorship();

	/**
	 * @return the family
	 */
	public String getFamily();

	/**
	 * @return the subfamily
	 */
	public String getSubfamily();

	/**
	 * @return the tribe
	 */
	public String getTribe();

	/**
	 * @return the genus
	 */
	public String getGenus();

	/**
	 * @return the specificEpithet
	 */
	public String getSpecificEpithet();

	/**
	 * @return the subspecificEpithet
	 */
	public String getSubspecificEpithet();

	/**
	 * @return the infraspecificEpithet
	 */
	public String getInfraspecificEpithet();

	/**
	 * @return the infraspecificRank
	 */
	public String getInfraspecificRank();

}