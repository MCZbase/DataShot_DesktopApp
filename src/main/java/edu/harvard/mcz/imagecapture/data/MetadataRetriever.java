/**
 * MetadataRetriever.java
 * edu.harvard.mcz.imagecapture.data
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
package edu.harvard.mcz.imagecapture.data;

import java.awt.Color;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import edu.harvard.mcz.imagecapture.ImageCaptureApp;
import edu.harvard.mcz.imagecapture.MainFrame;

/** MetadataRetriever produces metadata (field lengths, tooltip texts, input masks, input verifiers)
 * for fields in tables in database.  
 * 
 * @author Paul J. Morris
 *
 */
public class MetadataRetriever {
	
	private static String repeat(String s, int count) { 
		StringBuffer result = new StringBuffer();
		for (int i=0; i<count; i++) { 
			result.append(s);
		}
		return result.toString();
	}
	
	/**Generates a MaskFormatter for a JFormattedTextField based upon the length (and potentially the type) of the
	 * underlying text field.  Doesn't work well for normal varchar() fields, as the JTextField appears to be full
	 * of spaces.   
	 * <BR>
	 * Usage:
	 * <pre>
	    JFormattedTextField jtext_for_fieldname = new JFormattedTextField(MetadataRetriever(tablename.class,"fieldname"));
	   </pre>
	 * 
	 * @param aTableClass
	 * @param fieldname
	 * @return a MaskFormatter for a jFormattedTextField
	 * 
	 * TODO: add field type recognition, currently returns only "****" masks. 
	 */
	@SuppressWarnings("unchecked")
	public static MaskFormatter getMask(Class aTableClass, String fieldname) { 
		MaskFormatter formatter = null;
		try {
			formatter = new MaskFormatter(repeat("*",getFieldLength(aTableClass,fieldname)));
		} catch (ParseException e) {
			// Shouldn't end up here unless tables have been redesigned and
			// MetadataRetriever.getFieldLength isn't returning a value.
			System.out.println("Bad Mask format string");
			e.printStackTrace();
		}
		return formatter;
	}
	
	/**
	 * Generates an InputVerifier for a JTextField
	 * <BR>
	 * Usage:
	 * <pre>
	    JTextField jText_for_fieldname = new JTextField();
	    jText_for_fieldname.addInputVerifier(MetadataRetriever.getInputVerifier(tablename.class,"fieldname",jText_for_fieldname));
	   </pre>
	 * 
	 * @param aTableClass table proxy object for fieldname 
	 * @param fieldname field for which to check the fieldlength
	 * @param field  JTextField to which the InputVerifier is being added.
	 * @return an InputVerifier for the JTextField
	 * TODO: implement tests for more than just length. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static InputVerifier getInputVerifier(final Class aTableClass, final String fieldname, final JTextField field)  {
		InputVerifier result = null;
		if (
		    (aTableClass == Specimen.class && (fieldname.equalsIgnoreCase("ISODate") || fieldname.equalsIgnoreCase("DateIdentified"))) 
		 || (aTableClass==Determination.class && fieldname.equalsIgnoreCase("DateIdentified")) 
		 ) { 
			result = new InputVerifier() {
				public boolean verify(JComponent comp) {
					boolean returnValue = true;
					JTextField textField = (JTextField)comp;
					String content = textField.getText();
					if (content.length() > getFieldLength(aTableClass, fieldname)) {
						returnValue = false;
					} else { 
						if (!content.matches(ImageCaptureApp.REGEX_DATE)) { 
							returnValue = false;
						}
					}
					return returnValue;
				}
				public boolean shouldYieldFocus(JComponent input) {
					boolean valid = super.shouldYieldFocus(input);
					if (valid) {
						field.setBackground(Color.WHITE);
					} else { 
						field.setBackground(MainFrame.BG_COLOR_ERROR);
					}
					field.revalidate();
					return valid;
				}
			};			
		}  else { 
		result = new InputVerifier() {
			public boolean verify(JComponent comp) {
				boolean returnValue = true;
				JTextField textField = (JTextField)comp;
				String content = textField.getText();
				if (content.length() > getFieldLength(aTableClass, fieldname)) {
					returnValue = false;
				}
				return returnValue;
			}
			public boolean shouldYieldFocus(JComponent input) {
				boolean valid = super.shouldYieldFocus(input);
				if (valid) {
					if (fieldname.equalsIgnoreCase("Inferences")) { 
						field.setBackground(MainFrame.BG_COLOR_ENT_FIELD);
					} else { 
					    field.setBackground(Color.WHITE);
					} 
				} else { 
					field.setBackground(MainFrame.BG_COLOR_ERROR);
				}
				field.revalidate();
				return valid;
			}
		};
		}
		return result;
	}
	
	/** Determine the length of a field from the class of the proxy object
	 * for the table and the name of the field.
	 * <BR>
	 * Usage:
	 * <pre>
	     int genusSize = MetadataRetriever.getFieldLength(Specimen.class, "genus");
	   </pre>
	 * 
	 * @param aTableClass the class of the proxy object over the table.
	 * @param fieldname the name of the field in that table (case insensitive).  
	 * @return the number of characters that can be put into the field.
	 */
	@SuppressWarnings("unchecked")
	public static int getFieldLength(Class aTableClass, String fieldname) {
		int length = 0;
		if (aTableClass==Specimen.class) { 
            if (fieldname.equalsIgnoreCase("Barcode")) {  length=20; }  
            if (fieldname.equalsIgnoreCase("DrawerNumber")) { length=10; } 
            if (fieldname.equalsIgnoreCase("TypeStatus")) { length=50; }
            if (fieldname.equalsIgnoreCase("TypeNumber")) { length=255; }
            if (fieldname.equalsIgnoreCase("CitedInPublication")) { length=900; }
            if (fieldname.equalsIgnoreCase("Features")) { length=50; }
            if (fieldname.equalsIgnoreCase("Family")) { length=40; }
            if (fieldname.equalsIgnoreCase("Subfamily")) { length=40; }
            if (fieldname.equalsIgnoreCase("Tribe")) { length=40; }
            if (fieldname.equalsIgnoreCase("Genus")) { length=40; }
            if (fieldname.equalsIgnoreCase("SpecificEpithet")) { length=40; }
            if (fieldname.equalsIgnoreCase("SubspecificEpithet")) { length=255; }
            if (fieldname.equalsIgnoreCase("InfraspecificEpithet")) { length=40; }
            if (fieldname.equalsIgnoreCase("InfraspecificRank")) { length=40; }
            if (fieldname.equalsIgnoreCase("Authorship")) { length=255; }
            if (fieldname.equalsIgnoreCase("UnNamedForm")) { length=50; }
            if (fieldname.equalsIgnoreCase("IdentificationQualifier")) { length=50; }
            if (fieldname.equalsIgnoreCase("IdentifiedBy")) { length=255; }
            if (fieldname.equalsIgnoreCase("DateIdentified")) { length=21; }
            if (fieldname.equalsIgnoreCase("NatureOfID")) { length=255; }
            if (fieldname.equalsIgnoreCase("Country")) { length=255; }
            if (fieldname.equalsIgnoreCase("PrimaryDivison")) { length=255; }
            if (fieldname.equalsIgnoreCase("SpecificLocality")) { length=65535; }
            if (fieldname.equalsIgnoreCase("VerbatimLocality")) { length=65535; }
            if (fieldname.equalsIgnoreCase("VerbatimElevation")) { length=255; }
            if (fieldname.equalsIgnoreCase("CollectingMethod")) { length=255; }
            if (fieldname.equalsIgnoreCase("ISODate")) { length=21; }
            if (fieldname.equalsIgnoreCase("DateNOS")) { length=32; }
            if (fieldname.equalsIgnoreCase("DateEmerged")) { length=32; }
            if (fieldname.equalsIgnoreCase("DateEmergedIndicator")) { length=50; }
            if (fieldname.equalsIgnoreCase("DateCollected")) { length=32; }
            if (fieldname.equalsIgnoreCase("DateCollectedIndicator")) { length=50; }
            if (fieldname.equalsIgnoreCase("Collection")) { length=255 ; }
            if (fieldname.equalsIgnoreCase("SpecimenNotes")) { length=65535; }
            if (fieldname.equalsIgnoreCase("LifeStage")) { length=50; }
            if (fieldname.equalsIgnoreCase("Sex")) { length=50; }
            if (fieldname.equalsIgnoreCase("PreparationType")) { length=50; }
            if (fieldname.equalsIgnoreCase("Habitat")) { length=900; }
            if (fieldname.equalsIgnoreCase("AssociatedTaxon")) { length=900; }
            if (fieldname.equalsIgnoreCase("Questions")) { length=900; }
            if (fieldname.equalsIgnoreCase("Inferences")) { length=900; }
            if (fieldname.equalsIgnoreCase("LocationInCollection")) { length=50; }
            if (fieldname.equalsIgnoreCase("WorkFlowStatus")) { length=30; }
            if (fieldname.equalsIgnoreCase("CreatedBy")) { length=255; }
            if (fieldname.equalsIgnoreCase("DateLastUpdated")) { length=0; }
            if (fieldname.equalsIgnoreCase("LastUpdatedBy")){ length=255;  }
            if (fieldname.equalsIgnoreCase("ValidDistributionFlag")) {  length = 1; }
		}
		if (aTableClass==Number.class) { 
			if (fieldname.equalsIgnoreCase("Number")) { length=50; }	
			if (fieldname.equalsIgnoreCase("NumberType")) { length=50; }	
		}
		if (aTableClass==Collector.class) { 
			if (fieldname.equalsIgnoreCase("CollectorName")) { length=255; }	
		}
		if (aTableClass==Determination.class) { 
            if (fieldname.equalsIgnoreCase("Genus")) { length=40; }
            if (fieldname.equalsIgnoreCase("SpecificEpithet")) { length=40; }
            if (fieldname.equalsIgnoreCase("SubspecificEpithet")) { length=255; }
            if (fieldname.equalsIgnoreCase("InfraspecificEpithet")) { length=40; }
            if (fieldname.equalsIgnoreCase("InfraspecificRank")) { length=40; }
            if (fieldname.equalsIgnoreCase("Authorship")) { length=255; }
            if (fieldname.equalsIgnoreCase("UnNamedForm")) { length=50; }
            if (fieldname.equalsIgnoreCase("IdentificationQualifier")) { length=50; }
            if (fieldname.equalsIgnoreCase("IdentifiedBy")) { length=255; }		
            if (fieldname.equalsIgnoreCase("DateIdentified")) { length=21; }
            if (fieldname.equalsIgnoreCase("TypeStatus")) { length=50; }
            if (fieldname.equalsIgnoreCase("NatureOfID")) { length=255; }
		}
		if (aTableClass==Users.class) { 
			if (fieldname.equalsIgnoreCase("username")) { length=50; }	
			if (fieldname.equalsIgnoreCase("fullname")) { length=50; }	
			if (fieldname.equalsIgnoreCase("description")) { length=255; }	
			if (fieldname.equalsIgnoreCase("role")) { length=20; }	
		}
		
		if (aTableClass==ICImage.class) { 
			if (fieldname.equalsIgnoreCase("rawBarcode")) { length=50; }	
			if (fieldname.equalsIgnoreCase("rawExifBarcode")) { length=50; }
			if (fieldname.equalsIgnoreCase("filename")) { length=50; }
			if (fieldname.equalsIgnoreCase("rawOcr")) { length=65535; }
			if (fieldname.equalsIgnoreCase("path")) { length=900; }
			if (fieldname.equalsIgnoreCase("uri")) { length=50; }
			if (fieldname.equalsIgnoreCase("templateId")) { length=50; }
			if (fieldname.equalsIgnoreCase("drawerNumber")) { length=10; }			
		}		
		
		return length;
	}
    
	/**
	 * Given a proxy class for a table and the name of a field return a help text for that field.
	 *  
	 * @param aTableClass
	 * @param fieldname
	 * @return a String containing a help text suitable for use as a tooltip or other context help display.
	 */
	@SuppressWarnings("unchecked")
	public static String getFieldHelp(Class aTableClass, String fieldname) {
		String help = "No help available";
		if (aTableClass==Specimen.class) { 
            if (fieldname.equalsIgnoreCase("Barcode")) {  help="The barcode of the specimen, in the form 'MCZ-ENT01234567'"; }  
            if (fieldname.equalsIgnoreCase("DrawerNumber")) { help="The drawer number from the specimen unit tray label."; } 
            if (fieldname.equalsIgnoreCase("TypeStatus")) { help="Not a type, or the kind of type (e.g. Holotype) that this specimen is.  Normal workflow uses value 'not a type'.  This type status refers to the type status of this specimen for the name in the specimen record (secondary types may have type status set in other determinations)."; }
            if (fieldname.equalsIgnoreCase("TypeNumber")) { help="The number from the type catalog number series that applies to this specimen"; }
            if (fieldname.equalsIgnoreCase("CitedInPublication")) { help="Publications this specimen is cited in, as found on labels.  Separate publications with a semicolon ' ; '"; }
            if (fieldname.equalsIgnoreCase("Features")) { help="Description of features of this specimen that aren't named forms, un-named forms, sex, or life stage.  Examples: features could include eclosion defect, runt (unusually small), deformed, faded colours, scales stripped for venation, greasy, stained, psocid damaged."; }
            if (fieldname.equalsIgnoreCase("Family")) { help="The family into which this specimen is placed, from the unit tray label"; }
            if (fieldname.equalsIgnoreCase("Subfamily")) { help="The subfamily into which this specimen is placed, from the unit tray label"; }
            if (fieldname.equalsIgnoreCase("Tribe")) { help="The tribe into which this specimen is placed, if any, from the unit tray label."; }
            if (fieldname.equalsIgnoreCase("Genus")) { help="The generic name from the unit tray label.  The current identification for non-primary types, the type name for primary types.  Example: 'Delias' from 'Delias argenthona Fabricius, 1793'"; }
            if (fieldname.equalsIgnoreCase("SpecificEpithet")) { help="The specific part of the species group name from the unit tray label. The current identification for non-primary types, the type name for primary types.  Include indicators of uncertanty associated with this part of the name such as nr, cf, ?.  May be 'sp.'  Example: 'argenthoma' from 'Delias argenthona Fabricius, 1793'"; }
            if (fieldname.equalsIgnoreCase("SubspecificEpithet")) { help="The subspecific part (if present) of the species group name from the unit tray label.  Include indicators of uncertanty associated with this part of the name such as nr, cf, ?.  May be 'ssp.'"; }
            if (fieldname.equalsIgnoreCase("InfraspecificEpithet")) { help="The form, varietal, or other part of a name with a rank below subspecies from the unit tray label"; }
            if (fieldname.equalsIgnoreCase("InfraspecificRank")) { help="The rank (e.g. form, variety) of the infraspecific name from the unit tray label."; }
            if (fieldname.equalsIgnoreCase("Authorship")) { help="The author of the species group name from the unit tray label.  Include year and parenthesies if present.  Example: 'Fabricius, 1793' from 'Delias argenthona Fabricius, 1793'"; }
            if (fieldname.equalsIgnoreCase("UnNamedForm")) { help="e.g. 'Winter form', informal descriptive name of the form of this specimen (for informal form names not regulated by the ICZN)."; }
            if (fieldname.equalsIgnoreCase("IdentificationQualifier")) { help="Don't use this field."; }
            if (fieldname.equalsIgnoreCase("IdentifiedBy")) { help="Name of the person, if known, who made this identification."; }
            if (fieldname.equalsIgnoreCase("DateIdentified")) { help="Date at which this identification was made, if known.  Use ISO format yyyy/mm/dd-yyyy/mm/dd."; }
            if (fieldname.equalsIgnoreCase("IdentificationRemarks")) { help="Remarks about this identification."; }
            if (fieldname.equalsIgnoreCase("NatureOfId")) { help="Nature of the Identification: expert ID=made by known expert; legacy=on label with no data on identifier. "; }
            if (fieldname.equalsIgnoreCase("Country")) { help="The country from which this specimen was collected.  Infer if you have specialist knowledge and annotate in Inferences"; }
            if (fieldname.equalsIgnoreCase("PrimaryDivison")) { help="The state, province, or other primary geopolitical division of the country from which this specimen was collected.  Infer if you have specialist knowlege and annotate in Inferences"; }
            if (fieldname.equalsIgnoreCase("SpecificLocality")) { help="Placenames, offsets, and other text describing where this specimen was collected.  Press button to use '[no specific locality data]' when there are no specific locality data."; }
            if (fieldname.equalsIgnoreCase("VerbatimLocality")) { help="Verbatim transcription of locality information found on this specimen's labels."; }
            if (fieldname.equalsIgnoreCase("VerbatimElevation")) { help="Verbatim transcription of elevation information, including units, found on this specimen's labels"; }
            if (fieldname.equalsIgnoreCase("CollectingMethod")) { help="If specified on a label, the method by which this specimen was collected."; }
            if (fieldname.equalsIgnoreCase("DateNOS")) { help="The default date field, a verbatim date associated with this specimen that isn't marked as either a date collected or date emerged, and might be either of these or some other date."; }
            if (fieldname.equalsIgnoreCase("ISODate")) { help="The date collected or the default date in ISO date format yyyy/mm/dd. Optionally, a range yyyy/mm/dd-yyyy/mm/dd"; }
            if (fieldname.equalsIgnoreCase("DateEmerged")) { help="The date at which this butterfly emerged."; }
            if (fieldname.equalsIgnoreCase("DateEmergedIndicator")) { help="The verbatim text from the label that indicates that this is a date emerged."; }
            if (fieldname.equalsIgnoreCase("DateCollected")) { help="The date at which this butterfly was collected from the wild."; }
            if (fieldname.equalsIgnoreCase("DateCollectedIndicator")) { help="The verbatim text from the label that indicates that this is a date collected."; }
            if (fieldname.equalsIgnoreCase("Collection")) { help="The name of a collection of which this specimen was a part.  Use a standard format rather than verbatim text." ; }
            if (fieldname.equalsIgnoreCase("SpecimenNotes")) { help="Notes from the labels about this specimen.  Use a semicolon ; to separate multiple notes."; }
            if (fieldname.equalsIgnoreCase("LifeStage")) { help="The life stage of this specimen."; }
            if (fieldname.equalsIgnoreCase("Sex")) { help="The sex of this specimen."; }
            if (fieldname.equalsIgnoreCase("PreparationType")) { help="The preparation type of this specimen."; }
            if (fieldname.equalsIgnoreCase("Habitat")) { help="Text from the labels descrbing the habitat from which this specimen was collected."; }
            if (fieldname.equalsIgnoreCase("AssociatedTaxon")) { help="If this specimen represents an associated taxon such as a host ant, put the actual identification of this specimen, to whatever level it is available here, and put the name of the butterfly from the unit tray label in the identification (i.e. genus, species, etc. fields)."; }
            if (fieldname.equalsIgnoreCase("Questions")) { help="Describe any questions or problems you have with the transcription of the data from this specimen."; }
            if (fieldname.equalsIgnoreCase("Inferences")) { help="If you have specialist knowledge about this specimen, briefly describe the basis for any inferences you are making in adding data to this record that isn't present on the specimen labels.  Example: 'Locality for this species known to be in Australia, pers obs.'"; }
            if (fieldname.equalsIgnoreCase("LocationInCollection")) { help="General Collection, Type Collection, or other major storage division of the Lepidoptera collection."; }
            if (fieldname.equalsIgnoreCase("WorkFlowStatus")) { help="The current state of this database record in the workflow."; }
            if (fieldname.equalsIgnoreCase("CreatedBy")) { help="The name of the person or automated process that created this record."; }
            if (fieldname.equalsIgnoreCase("DateLastUpdated")) { help="The date and time at which this record was most recently updated."; }
            if (fieldname.equalsIgnoreCase("LastUpdatedBy")){ help="The name of the person who most recenly updated this record.";  }
            if (fieldname.equalsIgnoreCase("ValidDistributionFlag")) {  help = "Uncheck if the locality does not reflect the collection of this specimen from nature (e.g. uncheck for specimens that came from a captive breeding program).  Leave checked if locality represents natural biological range. "; }
		}
		if (aTableClass==Number.class) { 
			if (fieldname.equalsIgnoreCase("Number")) { help="A number (including alphanumeric identifiers) found on a label of this specimen."; }	
			if (fieldname.equalsIgnoreCase("NumberType")) { help="If known, what sort of number this is and where it came from."; }	
		}
		if (aTableClass==Collector.class) { 
			if (fieldname.equalsIgnoreCase("CollectorName")) { help="The name of a collector."; }	
		}
		if (aTableClass==Determination.class) { 
			if (fieldname.equalsIgnoreCase("TypeStatus")) { help="Not a type, or the kind of type (e.g. Topotype) that this specimen is for this particular name."; }
			if (fieldname.equalsIgnoreCase("Genus")) { help="The generic name used in the identification."; }
            if (fieldname.equalsIgnoreCase("SpecificEpithet")) { help="The specific part of the species group name used in the identification."; }
            if (fieldname.equalsIgnoreCase("SubspecificEpithet")) { help="The subspecific part (if present) of the species group name used in the identification."; }
            if (fieldname.equalsIgnoreCase("InfraspecificEpithet")) { help="The form, varietal, or other part of a name with a rank below subspecies used in the identification."; }
            if (fieldname.equalsIgnoreCase("InfraspecificRank")) { help="The rank (e.g. form, variety, abberation, morph, lusus, natio) of the infrasubspecific name."; }
            if (fieldname.equalsIgnoreCase("Authorship")) { help="The author of the species group name used in the determination."; }
            if (fieldname.equalsIgnoreCase("UnNamedForm")) { help="e.g. 'Winter form', informal descriptive name of the form of this specimen (not regulated by the ICZN)."; }
            if (fieldname.equalsIgnoreCase("IdentificationQualifier")) { help="A question mark or other qualifier that indicates the identification of this specimen is uncertain."; }
            if (fieldname.equalsIgnoreCase("IdentifiedBy")) { help="Name of the person, if known, who made this identification."; }		
            if (fieldname.equalsIgnoreCase("DateIdentified")) { help="Date at which this identification was made, if known.  Use ISO Format yyyy/mm/dd-yyyy/mm/dd."; }
            if (fieldname.equalsIgnoreCase("IdentificationRemarks")) { help="Remarks about this identification."; }
            if (fieldname.equalsIgnoreCase("NatureOfId")) { help="Nature of the Identification: expert ID=made by known expert; legacy=on label with no data on identifier. "; }
		}
		if (aTableClass==Users.class) { 
			if (fieldname.equalsIgnoreCase("username")) { help="Database username of this person."; }	
			if (fieldname.equalsIgnoreCase("fullname")) { help="The full name of this person."; }	
			if (fieldname.equalsIgnoreCase("description")) { help="What this person's role in the project and specialties are."; }	
			if (fieldname.equalsIgnoreCase("role")) { help="Unused"; }	
		}
		
		return help;
	}
	
	/**
	 * Test to see whether a field allowed to be updated by an external process. 
	 * 
	 * @param aTableClass the class for the table in which the field occurs.
	 * @param fieldname the name of the field (case insensitive).
	 * 
	 * @return true if the field is allowed to be updated by an external process, false otherwise.
	 */
	public static boolean isFieldExternallyUpdatable(Class aTableClass, String fieldname) {
		boolean result = false;
		if (aTableClass==Specimen.class) { 
            if (fieldname.equalsIgnoreCase("TypeStatus")) { result=true; }
            if (fieldname.equalsIgnoreCase("TypeNumber")) { result=true; }
            if (fieldname.equalsIgnoreCase("CitedInPublication")) { result=true; }
            if (fieldname.equalsIgnoreCase("Features")) { result=true; }
			if (fieldname.equalsIgnoreCase("Higher_Geography")) { result = true; }
            if (fieldname.equalsIgnoreCase("SpecificLocality")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimLocality")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimCollector")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimCollection")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimNumbers")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimUnclassifiedText")) { result=true; }
            if (fieldname.equalsIgnoreCase("Minimum_Elevation")) { result=true; }
            if (fieldname.equalsIgnoreCase("Maximum_Elevation")) { result=true; }
            if (fieldname.equalsIgnoreCase("Elev_Units")) { result=true; }
            if (fieldname.equalsIgnoreCase("CollectingMethod")) { result=true; }
            if (fieldname.equalsIgnoreCase("ISODate")) { result=true; }
            if (fieldname.equalsIgnoreCase("DateNOS")) { result=true; }
            if (fieldname.equalsIgnoreCase("DateEmerged")) { result=true; }
            if (fieldname.equalsIgnoreCase("DateEmergedIndicator")) { result=true; }
            if (fieldname.equalsIgnoreCase("DateCollected")) { result=true; }
            if (fieldname.equalsIgnoreCase("DateCollectedIndicator")) { result=true; }
            if (fieldname.equalsIgnoreCase("Collection")) { result=true ; }
            if (fieldname.equalsIgnoreCase("SpecimenNotes")) { result=true; }
            if (fieldname.equalsIgnoreCase("LifeStage")) { result=true; }
            if (fieldname.equalsIgnoreCase("Sex")) { result=true; }
            if (fieldname.equalsIgnoreCase("PreparationType")) { result=true; }
            if (fieldname.equalsIgnoreCase("Habitat")) { result=true; }
            if (fieldname.equalsIgnoreCase("Microhabitat")) { result=true; }
            if (fieldname.equalsIgnoreCase("AssociatedTaxon")) { result=true; }
            if (fieldname.equalsIgnoreCase("Questions")) { result=true; }
            if (fieldname.equalsIgnoreCase("Inferences")) { result=true; }
            if (fieldname.equalsIgnoreCase("LocationInCollection")) { result=true; }
            if (fieldname.equalsIgnoreCase("ValidDistributionFlag")) {  result = true; }
		}
		if (aTableClass==Collector.class) { 
			if (fieldname.equalsIgnoreCase("CollectorName")) { result=true; }	
		}
		if (aTableClass==Determination.class) { 
            if (fieldname.equalsIgnoreCase("VerbatimText")) { result=true; }
		}
		if (aTableClass==Number.class) { 
            if (fieldname.equalsIgnoreCase("Number")) { result=true; }
            if (fieldname.equalsIgnoreCase("NumberType")) { result=true; }
		}
		
		return result;
	}	
	
	/**
	 * Test to see whether a field in a table is intended to hold verbatim values.
	 * 
	 * @param aTableClass the class for the table.
	 * @param fieldname the field to check (not case sensitive)
	 * @return true if the field is intended to hold verbatim data, false otherwise.
	 */
	public static boolean isFieldVerbatim(Class aTableClass, String fieldname) {
		boolean result = false;
		if (aTableClass==Specimen.class) { 
            if (fieldname.equalsIgnoreCase("VerbatimLocality")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimCollector")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimCollection")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimNumbers")) { result=true; }
            if (fieldname.equalsIgnoreCase("DateNOS")) { result=true; }
            if (fieldname.equalsIgnoreCase("VerbatimUnclassifiedText")) { result=true; }
		}
		if (aTableClass==Collector.class) { 
		}
		if (aTableClass==Determination.class) { 
            if (fieldname.equalsIgnoreCase("VerbatimText")) { result=true; }
		}
		if (aTableClass==Number.class) { 
		}
		
		return result;
	}		
		
	
}
