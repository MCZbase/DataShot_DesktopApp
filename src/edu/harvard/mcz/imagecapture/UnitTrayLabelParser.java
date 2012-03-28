/**
 * 
 */
package edu.harvard.mcz.imagecapture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.MetadataRetriever;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.interfaces.CollectionReturner;
import edu.harvard.mcz.imagecapture.interfaces.DrawerNameReturner;
import edu.harvard.mcz.imagecapture.interfaces.TaxonNameReturner;

/**Parses the text on an MCZ Lepidoptera unit tray label into atomic higher taxon and species group name
 * elements. 
 * 
 * @author Paul J. Morris
 *
 */
public class UnitTrayLabelParser implements TaxonNameReturner, DrawerNameReturner, CollectionReturner {
	
	private static final Log log = LogFactory.getLog(UnitTrayLabelParser.class);
	
	private String text;
	private String family;
	private String subfamily;
	private String tribe;
	private String genus;
	private String specificEpithet;
	private String subspecificEpithet;
	private String authorship;
	private String infraspecificEpithet;
	private String infraspecificRank;
	private String drawerNumber;
	private String collection;  // collection from which the material came
	
	/**
	 * Create a unit tray label parser and parse the text.  Call the get methods of the 
	 * instance to return the parsed text.
	 * 
	 * Usage:
	 * <code>
	 * Specimen s = new Specimen();
	 * UnitTrayLabelParser p = new UnitTrayLabelParser(aStringFromOCR);
	 * s.setFamily(p.getFamily());
	 * </code>
	 * 
	 * @param aStringToParse
	 */
	public UnitTrayLabelParser(String aStringToParse) { 
		text = aStringToParse;
		//System.out.print(aStringToParse);
		parse();
	}
	
	/**
	 * Identify atomic database field elements from their position in aStringToParse as
	 * given to the constructor (called by the constructor).  Invokes the protected set_ methods.   
	 */
	protected void parse() { 
		family = "";
		subfamily = "";
		tribe = "";
		genus = "";
		specificEpithet = "";
		subspecificEpithet = "";
		authorship = "";
		infraspecificEpithet = "";
		infraspecificRank = "";
		drawerNumber = "";
		
		// Can't parse text if it is null.
		if (text!=null) { 
			// trim out some likely OCR errors
			text = text.replace("|", "");
			text = text.replace("í", "i");  // ocr on utf8 filesystem
			//text = text.replace("Ã-", "i");   // "tilda A -" from ocr on windows filesystem

			
			String[] higherbits = text.split(":");
			int nameStartsAt = 0;
			// look for the higher taxon bits
			if (higherbits.length>0) {
				// get text before first colon
				// Check for extraneous leading line and remove if present.
				String[] possFamily = higherbits[0].trim().split("\n");
				if (possFamily.length==2) { 
				    setFamily(possFamily[1].trim());
				} else { 
					if (possFamily[0].trim().contains(" ")) {
						// Check for the case of a failed OCR of the separator colon.
						// Start by setting the family.
						setFamily(higherbits[0].trim());
						// now check.
						String[] bits = possFamily[0].trim().split(" ");
						if (bits.length==2 && bits[0].length()>4 && bits[1].length()>4) { 
							setFamily(bits[0].trim());
							setSubfamily(bits[1].trim());
						}
					} else { 
					   setFamily(higherbits[0].trim());
					}
				}
				nameStartsAt = 1;
			}
			if (higherbits.length>1) {
				if (higherbits.length>2) {
					// get first word after colon
					setSubfamily(higherbits[1].trim().split(" +")[0]);  // split on one or more spaces
				} else { 
					// get everything else on the first line
					// handles the pathological case of a space within the subfamily name.
					setSubfamily(higherbits[1].trim().split("\n")[0]);  
				}
				nameStartsAt = 1;
			}
			if (higherbits.length>2) {
				// Two colons, should be a tribe.
				String[] temp =higherbits[2].split("\n") ;
				if (temp.length > 0 && temp[0].trim().equals("")) {
					// second colon was followed by a newline character with no preceding text.
			        if (temp.length>1 && temp[1].trim().contains(" ")) { 
					    // likely pathological case of two colons and no tribe 'family:subfamily:\n genus'
					    // second line contains a genus/species
					    // 'family:subfamily:' ends with colon but has no tribe.
					    nameStartsAt = 2;   // split of higher bits still puts genus in next element.
					    // but there is no tribe to set.  
			        } else { 
						// Likely Tribe on second line, thus an extra leading newline.
			        	// family: subfamily: \n tribe \n genus species
						setTribe(higherbits[2].trim().split("[ \n]")[0]);  // split on space or new line
						nameStartsAt = 2;
						// trim the leading newline off the beginning of the stuff that follows the second colon.		
						higherbits[2] =  higherbits[2].trim();
			        }
				} else { 
					// Should be 'tribe \n genus'
					// get first word after second colon, if any
					setTribe(higherbits[2].trim().split("[ \n]")[0]);  // split on space or new line
					nameStartsAt = 2;
				} 
			}
			String[] lines = null;
			// Test for species group name spread on two lines.
			try {
				lines = higherbits[nameStartsAt].split("\n");
				if (lines.length>4) { 
					int st = 1;			
					if (lines[st].trim().contains(" ") &&
							lines[st+1].trim().matches(".*[A-Za-z]+.*") &&
							lines[st+2].trim().matches(".*[A-Za-z]+.*[0-9]+.*") &&
							lines[st+3].trim().matches(".*[0-9]+.*")
					) {
						higherbits[nameStartsAt] = lines[0] + "\n" + lines[st] + " " + lines[st+1] + "\n" + lines[st+2] + "\n" + lines[st+3];
					}					
				}
			} catch  (ArrayIndexOutOfBoundsException e) { 
				// expected if species group name is on one line.
			}
			// look for the species group name
			if (higherbits.length==nameStartsAt+1 && higherbits.length > 0) {
				lines = higherbits[nameStartsAt].split("\n");
				if (lines.length>1) { 
					String[] speciesGroupName = lines[1].trim().split(" +");  // split on one or more spaces
					// TODO: Test for trinomial flowing onto two lines with authorship on third. 
					try {
						parseSpeciesGroupName(speciesGroupName);						
						setAuthorship(lines[2].trim());
						setDrawerNumber(lines[3].trim());
					} catch (ArrayIndexOutOfBoundsException e) { 
						// unexpected, but possible if elements are missing
					}
				} 
			} else if (higherbits.length==1) {
				// Handle pathological case of no colon found in higher taxon name string.
				//System.out.println(higherbits[0]);
				lines = higherbits[0].split("\n");
				setFamily(lines[0].trim()); 
				String[] possFamily = higherbits[0].trim().split("\n");
				    log.debug(possFamily.length + possFamily[0]);
					if (possFamily[0].trim().contains(" ")) {
						// Check for the case of a failed OCR of the separator colon.
						// now check.
						String[] bits = possFamily[0].trim().split(" ");
						if (bits.length==2 && bits[0].length()>4 && bits[1].length()>4) { 
							setFamily(bits[0].trim());
							setSubfamily(bits[1].trim());
						}
					}
				try {
					setAuthorship(lines[lines.length-2].trim());
					setDrawerNumber(lines[lines.length-1].trim());	
					if (lines[lines.length-2].trim().matches(ImageCaptureApp.REGEX_DRAWERNUMBER)) { 
						setAuthorship(lines[lines.length-3].trim());
						setDrawerNumber(lines[lines.length-2].trim());
					} 
				} catch (ArrayIndexOutOfBoundsException e) { 
					// unexpected, but possible if elements are missing
					log.debug(e);
				}
				if (lines.length>1) { 
					try { 
					   String[] speciesGroupName = lines[lines.length-3].trim().split(" +");  // split on one or more spaces
                       parseSpeciesGroupName(speciesGroupName);
					}catch (ArrayIndexOutOfBoundsException e) { 
						// unexpected, but possible if elements are missing
						log.debug("Missing element in: " + higherbits[0].replace("\n", ":"));
					} 
					// TODO: Test for trinomial flowing onto two lines with authorship on third. 
				} 
			} // higherbits length == 1
			
			// recheck patterns in parse
			if (lines!=null) {
				int drawernumberOnLine = -1;
				int authorshipOnLine = -1;
				for (int i=0; i<lines.length; i++) {
					if (lines[i].trim().matches(ImageCaptureApp.REGEX_DRAWERNUMBER)) { 
						drawernumberOnLine = i;
					}
					//   \(?[A-Za-z& ]*,[0-9]{4}\)?
					if (lines[i].trim().matches("\\(?[A-Za-z& ]*, [0-9]{4}\\)?")) { 
						authorshipOnLine = i;
					}					
				}
				if (authorshipOnLine > 0 && !this.getAuthorship().equals(lines[authorshipOnLine].trim())) { 
					setAuthorship(lines[authorshipOnLine].trim());
				}
				if (drawernumberOnLine>0 && !getDrawerNumber().equals(lines[drawernumberOnLine].trim())) { 
					setDrawerNumber(lines[drawernumberOnLine].trim());
				}
			} 
		} // text is not null
		
	}

	/** given a species group name in a string array, parses the component genus, species, 
	 * subspecies, infraspecific, and infraspecific rank parts.
	 * @param speciesGroupName the species group name to parse.
	 */
	private void parseSpeciesGroupName(String[] speciesGroupName) {
		if (speciesGroupName[0].length()==1) {
			// Probably an incorrect OCR inserting a space after the first letter of the Generic name
			// Handle as special case by moving everything over by 1.
			speciesGroupName[0] = speciesGroupName[0] + speciesGroupName[1];
			if (speciesGroupName.length>2) {
				speciesGroupName[1] = speciesGroupName[2];
				if (speciesGroupName.length>3) {
					for (int i=3;i<speciesGroupName.length;i++) { 
						speciesGroupName[i-1] = speciesGroupName[i];	
						speciesGroupName[i] = "";
					}
				} else {
					speciesGroupName[2] = "";
				}
			}
		}
		setGenus(speciesGroupName[0]);
		if (speciesGroupName.length>1) {
			setSpecificEpithet(speciesGroupName[1]);
			if (speciesGroupName.length>2) {
				if (speciesGroupName[2].matches("^var.|forma|f.|form")) {
					try { 
						setInfraspecificEpithet(speciesGroupName[3]);
						setInfraspecificRank(speciesGroupName[2]);
					} catch (ArrayIndexOutOfBoundsException e) { 
						// Unexpected error found infrasubspecific indicator with no epithet 
						// following it.
						log.error("Parsing error: found infrasubspecific rank with no epithet in: " + speciesGroupName.toString().trim());
					}	
				}  else { 
					try { 
						setSubspecificEpithet(speciesGroupName[2]);
					} catch (ArrayIndexOutOfBoundsException e) { 
						//expected for species
					}		
				}
			}  // speciesgroupname .length > 2
		}  // speciesgroupname .length > 1
	} 

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getAuthorship()
	 */
	public String getAuthorship() {
		return authorship;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getFamily()
	 */
	public String getFamily() {
		return family;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getSubfamily()
	 */
	public String getSubfamily() {
		return subfamily;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getTribe()
	 */
	public String getTribe() {
		return tribe;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getGenus()
	 */
	public String getGenus() {
		return genus;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getSpecificEpithet()
	 */
	public String getSpecificEpithet() {
		return specificEpithet;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getSubspecificEpithet()
	 */
	public String getSubspecificEpithet() {
		return subspecificEpithet;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getInfraspecificEpithet()
	 */
	public String getInfraspecificEpithet() {
		return infraspecificEpithet;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.TaxonNameParser#getInfraspecificRank()
	 */
	public String getInfraspecificRank() {
		return infraspecificRank;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.DrawerNameReturner#getDrawerNumber()
	 */
	@Override
	public String getDrawerNumber() {
		return drawerNumber.trim();
	}

	/**
	 * @param family the family to set
	 */
	protected void setFamily(String family) {
		this.family = family.replace('\n', ' ').trim().replace('0', 'o').replaceAll("\\s", "").replaceAll("^1", "");
		// Truncate to database field size.
		// Truncating family is probably the most critical, as bad OCR where colons aren't read 
		// will end up here.
		if (this.family.length() > MetadataRetriever.getFieldLength(Specimen.class, "Family")) { 
			this.family = this.family.substring(0, MetadataRetriever.getFieldLength(Specimen.class, "Family"));	
		}
	}

	/**
	 * @param subfamily the subfamily to set
	 */
	protected void setSubfamily(String subfamily) {
		this.subfamily = subfamily.replace('\n', ' ').trim().replace('0', 'o').replaceAll("\\s", "");
		if (this.subfamily.length() > MetadataRetriever.getFieldLength(Specimen.class, "Subfamily")) { 
			this.subfamily = this.subfamily.substring(0, MetadataRetriever.getFieldLength(Specimen.class, "Subfamily"));	
		}
	}

	/**
	 * @param tribe the tribe to set
	 */
	protected void setTribe(String tribe) {
		this.tribe = tribe.replace('\n', ' ').trim().replace('0', 'o');
		if (this.tribe.length() > MetadataRetriever.getFieldLength(Specimen.class, "Tribe")) { 
			this.tribe = this.tribe.substring(0, MetadataRetriever.getFieldLength(Specimen.class, "Tribe"));	
		}
	}

	/**
	 * @param genus the genus to set
	 */
	protected void setGenus(String genus) {
		// strip off leading/trailing whitespace
		// strip off a leading [ 
		this.genus = genus.trim().replaceFirst("^\\[", "");
	}

	/**
	 * @param specificEpithet the specificEpithet to set
	 */
	protected void setSpecificEpithet(String specificEpithet) {
		this.specificEpithet = specificEpithet.trim();
	}

	/**
	 * @param subspecificEpithet the subspecificEpithet to set
	 */
	protected void setSubspecificEpithet(String subspecificEpithet) {
		this.subspecificEpithet = subspecificEpithet.trim();
	}

	/**
	 * @param authorship the authorship to set
	 */
	protected void setAuthorship(String authorship) {
		this.authorship = authorship.trim();
	}

	/**
	 * @param infraspecificEpithet the infraspecificEpithet to set
	 */
	protected void setInfraspecificEpithet(String infraspecificEpithet) {
		this.infraspecificEpithet = infraspecificEpithet.trim();
	}

	/**
	 * @param infraspecificRank the infraspecificRank to set
	 */
	protected void setInfraspecificRank(String infraspecificRank) {
		this.infraspecificRank = infraspecificRank.replace('\n', ' ').trim();
	}

	/**
	 * @param drawerNumber the drawerNumber to set
	 */
	protected void setDrawerNumber(String drawerNumber) {
		this.drawerNumber = drawerNumber.replace('\n', ' ').trim();
	}
	
	
	/**
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}

	
	@Override
	public String getCollection() {
		return collection;
	}
	

}
