/**
 * LabelEncoder.java
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

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.ByteMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import edu.harvard.mcz.imagecapture.JobAllImageFilesScan;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabelLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.PrintFailedException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;

/** LabelEncoder
 * 
 * @author Paul J. Morris
 *
 */
public class LabelEncoder {
	
	private static final Log log = LogFactory.getLog(LabelEncoder.class);
	
	private UnitTrayLabel label;
	
	public LabelEncoder (UnitTrayLabel aLabel)  {
		label = aLabel;
	}

	private ByteMatrix getQRCodeMatrix() { 
		ByteMatrix result = null;
		QRCodeWriter writer = new QRCodeWriter();
		try {
			String data = label.toJSONString();
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();  // set ErrorCorrectionLevel here
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			result = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200, hints);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public Image getImage() { 
		ByteMatrix barcode = getQRCodeMatrix();
		byte[][] bca = barcode.getArray();
		byte[] data = new byte[200*4*200*4];
		int z=0;
		for (int x=0; x<200; x++) { 
			for (int y=0; y<200; y++)  {
				data[z++] = bca[x][y];
			}
		}
		Image image = null;
		try {
			image = Image.getInstance(200, 200, 1, 8, data);
			image.scalePercent(50f);
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		data = null;
		return image;
	}
	
	@SuppressWarnings("hiding")
	public static boolean printList(List<UnitTrayLabel> taxa) throws PrintFailedException { 
		boolean result = false;
		UnitTrayLabel label = new UnitTrayLabel();
		LabelEncoder encoder = new LabelEncoder(label);
		Image image = encoder.getImage();
		int counter = 0;
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream("labels.pdf"));
			document.setPageSize(PageSize.LETTER);
			document.open();
			
			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100f);
			//table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
			float[] cellWidths = { 30f, 20f, 30f, 20f } ;
			table.setWidths(cellWidths);
			
			UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
			if (taxa==null) { 
			   taxa = uls.findAll();
			}
			Iterator<UnitTrayLabel> i = taxa.iterator();
			PdfPCell cell = null;
			PdfPCell cell_barcode = null;
			// Create two lists of 12 cells, the first 6 of each representing
			// the left hand column of 6 labels, the second 6 of each 
			// representing the right hand column.  
			// cells holds the text for each label, cells_barcode the barcode.
			ArrayList<PdfPCell> cells = new ArrayList<PdfPCell>(12);
			ArrayList<PdfPCell> cells_barcode = new ArrayList<PdfPCell>(12);
			for (int x=0; x<12; x++) { 
				cells.add(null);
				cells_barcode.add(null);
			}
			int cellCounter = 0;
			while (i.hasNext()) {
				// Loop through all of the taxa (unit tray labels) found to print 
				label = i.next();
				for (int toPrint=0; toPrint<label.getNumberToPrint(); toPrint++) {
					// For each taxon, loop through the number of requested copies 
					// Generate a text and a barcode cell for each, and add to array for page
					log.debug("Label " + toPrint + " of " + label.getNumberToPrint() );
					cell = new PdfPCell();
					cell.setBorderColor(Color.LIGHT_GRAY);
					cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
					cell.disableBorderSide(PdfPCell.RIGHT);
					cell.setPaddingLeft(3);

					String higherNames = "";
					if (label.getTribe().trim().length()>0) { 
						higherNames = label.getFamily() + ": " + label.getSubfamily()+ ": " + label.getTribe();	
					} else { 
					    higherNames = label.getFamily() + ": " + label.getSubfamily();
					} 
					Paragraph higher = new Paragraph();
					higher.setFont(new Font(Font.TIMES_ROMAN, 11, Font.NORMAL));
					higher.add(new Chunk(higherNames));
					cell.addElement(higher);

					Paragraph name = new Paragraph();
					Chunk genus = new Chunk(label.getGenus().trim() + " ");
					genus.setFont(new Font(Font.TIMES_ROMAN, 11, Font.ITALIC));
					Chunk species = new Chunk(label.getSpecificEpithet().trim());
					Chunk normal = null;  // normal font prefix to preceed specific epithet (nr. <i>epithet</i>)
					if (label.getSpecificEpithet().contains(".")|| label.getSpecificEpithet().contains("[")) {
						if (label.getSpecificEpithet().startsWith("nr. ")) { 
							normal = new Chunk("nr. ");
							normal.setFont(new Font(Font.TIMES_ROMAN, 11, Font.NORMAL));
							species = new Chunk(label.getSpecificEpithet().trim().substring(4));
							species.setFont(new Font(Font.TIMES_ROMAN, 11, Font.ITALIC));	
						} else { 
						    species.setFont(new Font(Font.TIMES_ROMAN, 11, Font.NORMAL));
						}
					} else { 	
						species.setFont(new Font(Font.TIMES_ROMAN, 11, Font.ITALIC));
					} 
					String s = "";
					if (label.getSubspecificEpithet().trim().length()>0) { s=" "; } else { s = ""; }
					Chunk subspecies = new Chunk(s + label.getSubspecificEpithet().trim());
					if (label.getSubspecificEpithet().contains(".")|| label.getSubspecificEpithet().contains("[")) {
						subspecies.setFont(new Font(Font.TIMES_ROMAN, 11, Font.NORMAL));
					} else { 	
						subspecies.setFont(new Font(Font.TIMES_ROMAN, 11, Font.ITALIC));
					} 
					if (label.getInfraspecificRank().trim().length()>0) { s=" "; } else { s = ""; }
					Chunk infraRank = new Chunk(s + label.getInfraspecificRank().trim());
					infraRank.setFont(new Font(Font.TIMES_ROMAN, 11, Font.NORMAL));

					if (label.getInfraspecificEpithet().trim().length()>0) { s=" "; } else { s = ""; }
					Chunk infra = new Chunk(s + label.getInfraspecificEpithet().trim());
					infra.setFont(new Font(Font.TIMES_ROMAN, 11, Font.ITALIC));
					if (label.getUnNamedForm().trim().length()>0) { s=" "; } else { s = ""; }
					Chunk unNamed = new Chunk(s + label.getUnNamedForm().trim());
					unNamed.setFont(new Font(Font.TIMES_ROMAN, 11, Font.NORMAL));

					name.add(genus);
					if (normal!=null) { 
						name.add(normal);
					}
					name.add(species);
					name.add(subspecies);
					name.add(infraRank);
					name.add(infra);
					name.add(unNamed);
					cell.addElement(name);

					Paragraph authorship = new Paragraph();
					authorship.setFont(new Font(Font.TIMES_ROMAN, 10, Font.NORMAL));
					if (label.getAuthorship()!=null && label.getAuthorship().length()>0 ) { 
						Chunk c_authorship = new Chunk (label.getAuthorship());
						authorship.add(c_authorship);
					}
					cell.addElement(authorship);
					//cell.addElement(new Paragraph(" "));
					if (label.getDrawerNumber()!=null && label.getDrawerNumber().length()>0) {
						Paragraph drawerNumber = new Paragraph();
						drawerNumber.setFont(new Font(Font.TIMES_ROMAN,10,Font.NORMAL));
					   Chunk c_drawerNumber = new Chunk(label.getDrawerNumber());
					   drawerNumber.add(c_drawerNumber);
					   cell.addElement(drawerNumber);
					} else { 
						if (label.getCollection()!=null && label.getCollection().length()>0) {
							Paragraph collection = new Paragraph();
							collection.setFont(new Font(Font.TIMES_ROMAN,10,Font.NORMAL));
						   Chunk c_collection = new Chunk(label.getCollection());
						   collection.add(c_collection);
						   cell.addElement(collection);
						} 	
					}

					cell_barcode = new PdfPCell();
					cell_barcode.setBorderColor(Color.LIGHT_GRAY);
					cell_barcode.disableBorderSide(PdfPCell.LEFT);
					cell_barcode.setVerticalAlignment(PdfPCell.ALIGN_TOP);

					encoder = new LabelEncoder(label);
					image = encoder.getImage();
					image.setAlignment(Image.ALIGN_TOP);
					cell_barcode.addElement(image);

					cells.add(cellCounter, cell);
					cells_barcode.add(cellCounter,cell_barcode);
					
					cellCounter++;
					// If we have hit a full set of 12 labels, add them to the document
					// in two columns, filling left column first, then right
					if (cellCounter==12) {
						// add a page of 12 cells in columns of two.
						for (int x=0;x<6;x++) {
							if (cells.get(x)==null) {
								PdfPCell c = new PdfPCell();
								c.setBorder(0);
								table.addCell(c);
						        table.addCell(c);
							} else { 
					            table.addCell(cells.get(x));
					            table.addCell(cells_barcode.get(x));
							}
							if (cells.get(x+6)==null) {
								PdfPCell c = new PdfPCell();
								c.setBorder(0);
								table.addCell(c);
						        table.addCell(c);
							} else { 
					            table.addCell(cells.get(x+6));
					            table.addCell(cells_barcode.get(x+6));
							}
						} 
						// Reset to begin next page
						cellCounter = 0;
						document.add(table);
						table = new PdfPTable(4);
						table.setWidthPercentage(100f);
						table.setWidths(cellWidths);
						for (int x=0;x<12;x++) { 
							cells.set(x, null);
							cells_barcode.set(x, null);
						}
				    }
				} // end loop through toPrint (for a taxon)
				counter ++;
			} // end while results has next (for all taxa requested)
			// get any remaining cells in pairs
			for (int x=0;x<6;x++) {
				if (cells.get(x)==null) {
					PdfPCell c = new PdfPCell();
					c.setBorder(0);
					table.addCell(c);
			        table.addCell(c);
				} else { 
		            table.addCell(cells.get(x));
		            table.addCell(cells_barcode.get(x));
				}
				if (cells.get(x+6)==null) {
					PdfPCell c = new PdfPCell();
				    c.setBorder(0);
					table.addCell(c);
			        table.addCell(c);
				} else { 
		            table.addCell(cells.get(x+6));
		            table.addCell(cells_barcode.get(x+6));
				}
			} 
			// add any remaining cells
			document.add(table);
			try { 
			    document.close();
			} catch (Exception e) { 
				throw new PrintFailedException("No labels to print." + e.getMessage());
			}
			// Check to see if there was content in the document.
			if (counter==0) { 
				result = false;
			} else { 
			    // Printed to pdf ok.
				result = true;
				// Increment number printed.
				i = taxa.iterator();
				while (i.hasNext()) { 
					label = i.next();
					for (int toPrint=0; toPrint<label.getNumberToPrint(); toPrint++) {
						label.setPrinted(label.getPrinted() + 1);
					}
					label.setNumberToPrint(0);
                    try {
						uls.attachDirty(label);
					} catch (SaveFailedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PrintFailedException("File not found.");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new PrintFailedException("Error buiding PDF document.");
		} catch (OutOfMemoryError e ) { 
			System.out.println("Out of memory error. " + e.getMessage());
			System.out.println("Failed.  Too many labels.");
			throw new PrintFailedException("Ran out of memory, too many labels at once.");
		} 
		return result;
	}
	
	public static void main (String[] args) {
		try {
			UnitTrayLabelLifeCycle uls = new UnitTrayLabelLifeCycle();
			List<UnitTrayLabel> taxa = uls.findAll();
			boolean ok = LabelEncoder.printList(taxa);
		} catch (PrintFailedException e) {
			System.out.println("Failed to print all.  " + e.getMessage());
		}
		
        System.out.println("Done.");
	}
}
