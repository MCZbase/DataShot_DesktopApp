/**
 * CandidateImageFile.java
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

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDescriptor;
import com.drew.metadata.exif.ExifDirectory;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import edu.harvard.mcz.imagecapture.exceptions.OCRReadException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;

/**
 * Image File that might contain text that can be extracted by OCR or a barcode that
 * can be extracted by barcode recognition. 
 * 
 * @author Paul J. Morris
 *
 */
public class CandidateImageFile {

	public static final int RESULT_NOT_CHECKED = 0;
	public static final int RESULT_BARCODE_SCANNED = 1;
	public static final int RESULT_ERROR = 2;

	private static final 	float[] matrix = { 0f, -.5f, 0f,
											 -.5f, 2.8f, -.5f,
											   0f, -.5f, 0f };

	private File candidateFile;
	private int barcodeStatus;
	private String exifCommentText = null;
	private String labelText = null;

	private static final Log log = LogFactory.getLog(CandidateImageFile.class);

	/**
	 * Constructor
	 * 
	 * @param aFile the image file that may contain a barcode or text.
	 * @param aTemplate the PositionTemplate to use to identify where a barcode or OCR text may occur
	 * in the image provided by aFile.
	 * 
	 * @throws UnreadableFileException if aFile cannot be read.
	 * @throws OCRReadException 
	 */
	public CandidateImageFile(File aFile, PositionTemplate aTemplate) throws UnreadableFileException { 
		setFile(aFile, aTemplate);
		//TODO: throw exception on problems with reading file
	}

	/** Test to see if the file provided in the constructor or the setFile method is readable.  This method
	 * is called from both the CandidateImageFile(File aFile, PositionTemplate aTemplate) constructor and the
	 * setFile(File aFile, PositionTemplate aTemplate) method, so it shouldn't be necessary to call it externally. 
	 * 
	 * @return true if file is readable, throws UnreadableFileException exception rather than returning false
	 * if file can't be read.    
	 * 
	 * @throws UnreadableFileException if file is null, or if it doesn't exist or if it can't be read.
	 */
	public boolean isFileReadable() throws UnreadableFileException { 
		boolean result = false;
		boolean test = false;
		try { 
			if (candidateFile==null) { 
				throw new UnreadableFileException ("No such file. CandidateImageFile given null for a filename.");
			}
			test = candidateFile.exists();
			if (test==false) { 
				throw new UnreadableFileException ("No such file as: " + candidateFile.getAbsolutePath());
			}
			test = candidateFile.canRead();
			if (test==false) { 
				throw new UnreadableFileException ("Can't read file: " + candidateFile.getAbsolutePath());
			}
			result = test;
		} catch (SecurityException e) { 
			throw new UnreadableFileException ("Can't read file: " + candidateFile.getAbsolutePath() + " Security problem." + e.getMessage());
		}
		return result;
	}

	/** Change the image file and position template.
	 * 
	 * @param aFile
	 * @param aTemplate
	 * @throws UnreadableFileException
	 * @throws OCRReadException 
	 */
	public void setFile(File aFile, PositionTemplate aTemplate) throws UnreadableFileException { 
		candidateFile = aFile;
		isFileReadable();
		// Set initial state
		barcodeStatus = RESULT_NOT_CHECKED;
		exifCommentText = null;
		labelText = null;
		// check the file for an exif comment
		getExifUserCommentText();  // scan for exif when handed file. 
		if (!aTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) {
			try { 
				if (getLabelQRText(aTemplate)==null) { ;
				getLabelOCRText(aTemplate);
				}
			} catch (OCRReadException e) { 
				log.error("Unable to OCR file: " + candidateFile.getName() + " " + e.getMessage());
			}
		}

	}

	/** If the image contains a taxon label text encoded in a QRCode barcode in the position specified
	 * for the Taxon/UnitTrayLabel Barcode by the PositionTemplate, return that text as a UnitTrayLabel
	 * object. 
	 * 
	 * @param positionTemplate, the template specifying the location of the barcode via getUtBarcodePosition.
	 * @return null or a UnitTrayLabel containing the parsed text of the taxon label read from the barcode.
	 */
	public UnitTrayLabel getLabelQRText(PositionTemplate positionTemplate) { 
		UnitTrayLabel resultLabel = null;
		String returnValue = "";
		if (positionTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) {
			// Check the entire image for a barcode and return.
			returnValue = getBarcodeText();
		} else { 
			// Check the part of the image specified by the template for the barcode.
			BufferedImage image = null;
			String error = "";
			barcodeStatus = RESULT_ERROR;
			try {
				image = ImageIO.read(candidateFile);
			} catch (IOException e) {
				error = e.toString() + " " + e.getMessage();
				returnValue = error;
			}
			if (image == null) {
				returnValue =  "Could not decode image. " + error;
				barcodeStatus = RESULT_ERROR;
			} else { 
				if (image.getWidth() > positionTemplate.getUtBarcodePosition().width) {
					// image might plausibly match template
					int left = positionTemplate.getUtBarcodePosition().width;    //* @param left x coordinate of leftmost pixels to decode
					int top =  positionTemplate.getUtBarcodePosition().height;  //* @param top y coordinate of topmost pixels to decode
					int right =  left + positionTemplate.getUtBarcodeSize().width;  //* @param right one more than the x coordinate of rightmost pixels to decode. That is, we will decode
					int width =  positionTemplate.getUtBarcodeSize().width; 
					//*  pixels whose x coordinate is in [left,right)
					int bottom =  top + positionTemplate.getUtBarcodeSize().height; //* @param bottom likewise, one more than the y coordinate of the bottommost pixels to decode
					int height =  positionTemplate.getUtBarcodeSize().height; 
					LuminanceSource source = null;
					boolean inBounds = false;
					try { 
						log.debug("Trying " + positionTemplate.getName() + ": " + left + " " + right + " " + top + " " + bottom);
						source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
						inBounds = true;
					} catch (IllegalArgumentException e) { 
						inBounds = false;
						returnValue = e.toString() + " " + e.getMessage();
						barcodeStatus = RESULT_ERROR;
						log.debug(returnValue);
					} 
					if (inBounds) { 
						BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
						Result result;
						try {
							QRCodeReader reader = new QRCodeReader();
							Hashtable<DecodeHintType, Object> hints = null;
							hints = new Hashtable<DecodeHintType, Object>(3);
							hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 	
							//Probable bug in xzing, reader.decode can throw ArrayIndexOutOfBoundsException
							//as well as the expected ReaderException. 
							result = reader.decode(bitmap,hints);
							returnValue= result.getText();
							barcodeStatus = RESULT_BARCODE_SCANNED;
						} catch (ReaderException e) {
							returnValue = e.toString() + " " + e.getMessage();
							barcodeStatus = RESULT_ERROR;
						} catch (ArrayIndexOutOfBoundsException e) { 
							returnValue = e.toString() + " " + e.getMessage();
							barcodeStatus = RESULT_ERROR;
						}
						log.debug(returnValue);
						log.debug("barcodeStatus=" + barcodeStatus);
						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// Try again with a sharpened image
							try { 
								log.debug("Trying again sharpened: " + left + " " + right + " " + top + " " + bottom);
								Kernel kernel = new Kernel(3,3,matrix);
								ConvolveOp convolver = new ConvolveOp(kernel,ConvolveOp.EDGE_NO_OP,null);
								BufferedImage sharpened = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
								sharpened = convolver.filter(image, sharpened);	
								File temp1 = new File("tempsharp.jpg");
								try {
									ImageIO.write(sharpened, "jpg", temp1);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								//These sharpening parameters are leading to a JRE fatal error in libmlib_image.so for some images
								//Fixed for linux JRE in 1.6 update 26.
								//RescaleOp contraster = new RescaleOp(0.8f, 20f, null);
								
// Sharpening appears to help, but not contrast enhancement. 
/* 
								float[] scales1 = { 1.1f, 1.1f, 1.1f }; 
								float[] offsets1 = { 20f, 20f, 20f };
								RescaleOp contraster = new RescaleOp(scales1,offsets1, null);
								BufferedImage contrasted = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
								contrasted = contraster.filter(sharpened, contrasted);

								for (int counter = 1;  counter < 30; counter++) {
									File temp = new File("temp"+counter+".jpg");
									float[] scales = { 1.1f, 1.1f, 1.1f}; 
									float[] offsets = { 0f+counter, 0f+counter, 0f+counter };
									contraster = new RescaleOp(scales, offsets, null);
									contrasted = contraster.filter(sharpened, contrasted);
									try {
										ImageIO.write(contrasted, "jpg", temp);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
*/
								source = new BufferedImageLuminanceSource(sharpened, left,  top, width, height);										
								inBounds = true;
							} catch (IllegalArgumentException e) { 
								inBounds = false;
								returnValue = e.toString() + " " + e.getMessage();
								barcodeStatus = RESULT_ERROR;
							} 
							bitmap = new BinaryBitmap(new HybridBinarizer(source));
							if (inBounds) { 
								try {
									QRCodeReader reader = new QRCodeReader();
									Hashtable<DecodeHintType, Object> hints = null;
									hints = new Hashtable<DecodeHintType, Object>(3);
									hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
									result = reader.decode(bitmap,hints);
									returnValue= result.getText();
									barcodeStatus = RESULT_BARCODE_SCANNED;
								} catch (ReaderException e) {
									returnValue = e.toString() + " " + e.getMessage();
									barcodeStatus = RESULT_ERROR;
								} catch (ArrayIndexOutOfBoundsException e) { 
									returnValue = e.toString() + " " + e.getMessage();
									barcodeStatus = RESULT_ERROR;
								}
							}
							log.debug(returnValue);
							log.debug("barcodeStatus=" + barcodeStatus);
							if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
								// Try again with a different box
								left = left - 10;
								right = right + 5;
								top = top + 5;
								bottom = bottom + 5;
								try { 
									log.debug("Trying again: " + left + " " + right + " " + top + " " + bottom);
									source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
									inBounds = true;
								} catch (IllegalArgumentException e) { 
									inBounds = false;
									returnValue = e.toString() + " " + e.getMessage();
									barcodeStatus = RESULT_ERROR;
								} 
								bitmap = new BinaryBitmap(new HybridBinarizer(source));
								if (inBounds) { 
									try {
										QRCodeReader reader = new QRCodeReader();
										Hashtable<DecodeHintType, Object> hints = null;
										hints = new Hashtable<DecodeHintType, Object>(3);
										hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
										result = reader.decode(bitmap,hints);
										returnValue= result.getText();
										barcodeStatus = RESULT_BARCODE_SCANNED;
									} catch (ReaderException e) {
										returnValue = e.toString() + " " + e.getMessage();
										barcodeStatus = RESULT_ERROR;
									} catch (ArrayIndexOutOfBoundsException e) { 
										returnValue = e.toString() + " " + e.getMessage();
										barcodeStatus = RESULT_ERROR;
									}
								}
								if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
									// Try again with a different box
									left = left +1;
									right = right - 1;
									top = top - 1;
									bottom = bottom +1;
									try { 
										log.debug("Trying again: " + left + " " + right + " " + top + " " + bottom);
										source = new BufferedImageLuminanceSource(image, left,  top, width, height);
										inBounds = true;
									} catch (IllegalArgumentException e) { 
										inBounds = false;
										returnValue = e.toString() + " " + e.getMessage();
										barcodeStatus = RESULT_ERROR;
									} 
									bitmap = new BinaryBitmap(new HybridBinarizer(source));
									if (inBounds) { 
										try {
											QRCodeReader reader = new QRCodeReader();
											Hashtable<DecodeHintType, Object> hints = null;
											hints = new Hashtable<DecodeHintType, Object>(3);
											hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
											result = reader.decode(bitmap,hints);
											returnValue= result.getText();
											barcodeStatus = RESULT_BARCODE_SCANNED;
										} catch (ReaderException e) {
											returnValue = e.toString() + " " + e.getMessage();
											barcodeStatus = RESULT_ERROR;
										} catch (ArrayIndexOutOfBoundsException e) { 
											returnValue = e.toString() + " " + e.getMessage();
											barcodeStatus = RESULT_ERROR;
										}
									}
									if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
										// Try again with a different box
										left = left +1;
										right = right - 1;
										top = top - 1;
										bottom = bottom +1;
										try { 
											log.debug("Trying agaub: " + left + " " + right + " " + top + " " + bottom);
											source = new BufferedImageLuminanceSource(image, left,  top, width, height);
											inBounds = true;
										} catch (IllegalArgumentException e) { 
											inBounds = false;
											returnValue = e.toString() + " " + e.getMessage();
											barcodeStatus = RESULT_ERROR;
										} 
										bitmap = new BinaryBitmap(new HybridBinarizer(source));
										if (inBounds) { 
											try {
												QRCodeReader reader = new QRCodeReader();
												Hashtable<DecodeHintType, Object> hints = null;
												hints = new Hashtable<DecodeHintType, Object>(3);
												hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
												result = reader.decode(bitmap,hints);
												returnValue= result.getText();
												barcodeStatus = RESULT_BARCODE_SCANNED;
											} catch (ReaderException e) {
												returnValue = e.toString() + " " + e.getMessage();
												barcodeStatus = RESULT_ERROR;
											} catch (ArrayIndexOutOfBoundsException e) { 
												returnValue = e.toString() + " " + e.getMessage();
												barcodeStatus = RESULT_ERROR;
											}
										}
									}
									if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
										// Try again with a different box
										left = left - 10;
										right = right + 20;
										top = top - 10;
										bottom = bottom +25;
										try { 
											log.debug("Trying again: " + left + " " + right + " " + top + " " + bottom);
											source = new BufferedImageLuminanceSource(image, left,  top, width, height);
											inBounds = true;
										} catch (IllegalArgumentException e) { 
											inBounds = false;
											returnValue = e.toString() + " " + e.getMessage();
											barcodeStatus = RESULT_ERROR;
										} 
										bitmap = new BinaryBitmap(new HybridBinarizer(source));
										if (inBounds) { 
											try {
												QRCodeReader reader = new QRCodeReader();
												Hashtable<DecodeHintType, Object> hints = null;
												hints = new Hashtable<DecodeHintType, Object>(3);
												hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
												result = reader.decode(bitmap,hints);
												returnValue= result.getText();
												barcodeStatus = RESULT_BARCODE_SCANNED;
											} catch (ReaderException e) {
												returnValue = e.toString() + " " + e.getMessage();
												barcodeStatus = RESULT_ERROR;
											} catch (ArrayIndexOutOfBoundsException e) { 
												returnValue = e.toString() + " " + e.getMessage();
												barcodeStatus = RESULT_ERROR;
											}
										}
									}
								}
							}
						}
					} 
				} 
			} 
		}
		if (!returnValue.equals("") && barcodeStatus == RESULT_BARCODE_SCANNED) { 
			log.debug("Found QR Barcode on unit tray label containing: " + returnValue);
			resultLabel = UnitTrayLabel.createFromJSONString(returnValue);
			labelText = returnValue;
		}
		return resultLabel;
	}

	/** Return the text found by OCR of the taxon label (getTextPosition) region of the
	 * image according to the specified template.
	 * 
	 * @param aTemplate
	 * @return a string 
	 * @throws OCRReadException
	 */
	public String getLabelOCRText(PositionTemplate aTemplate) throws OCRReadException {
		// Actual read attempt is only invoked once,
		// subsequent calls return cached value.
		if (labelText == null) { 
			BufferedImage image = null;
			String error = "";
			barcodeStatus = RESULT_ERROR;
			try {
				image = ImageIO.read(candidateFile);
			} catch (IOException e) {
				error = e.toString() + " " + e.getMessage();
				log.error(error);
			}
			if (image != null) {
				try { 
					int x = aTemplate.getTextPosition().width;    
					int y =  aTemplate.getTextPosition().height; 
					int w = aTemplate.getTextSize().width;  
					int h = aTemplate.getTextSize().height;

					// OCR and parse UnitTray Label
					ConvertTesseractOCR o = new ConvertTesseractOCR(image.getSubimage(x, y, w, h));
					labelText = "";
					try {
						labelText = o.getOCRText();
					} catch (OCRReadException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					}
				} catch (Exception ex) { 
					log.error("Exception thrown in OCR of unit tray label.");
					log.error(ex);	
					log.trace(ex);
					throw new OCRReadException(ex.getMessage());
				}
				if (labelText.equals("") ) { 
					try { 
						// try again
						int x = aTemplate.getTextPosition().width + 1;    
						int y =  aTemplate.getTextPosition().height  + 1 ; 
						int w = aTemplate.getTextSize().width + 1;  
						int h = aTemplate.getTextSize().height + 1;

						// OCR and parse UnitTray Label
						ConvertTesseractOCR o = new ConvertTesseractOCR(image.getSubimage(x, y, w, h));
						labelText = "";
						try {
							labelText = o.getOCRText();
						} catch (OCRReadException e) {
							log.error(e.getMessage());
							e.printStackTrace();
						}
					} catch (Exception ex) { 
						log.error("Exception thrown in OCR of unit tray label.");
						log.error(ex);	
						log.trace(ex);
						throw new OCRReadException(ex.getMessage());
					}
				}
			}
		}
		return labelText;
	} 

	/** Get the text, if any, from the Exif UserComment of the image file.
	 * 
	 * @return the content of the Exif UserComment decoded as a string.
	 */
	public String getExifUserCommentText() { 
		// Actual read attempt is only invoked once,
		// subsequent calls return cached value.
		if (exifCommentText==null) { 
			// read, or re-read the file for a comment
			String exifComment = "";
			try {
				Metadata metadata = JpegMetadataReader.readMetadata(candidateFile);
				// [Exif] User Comment
				Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
				ExifDescriptor descriptor = new ExifDescriptor(exifDirectory);
				try {
					exifComment = descriptor.getUserCommentDescription();
				} catch (MetadataException e1) {
					log.error("Error decoding exif metadata.");
					log.error(e1.getMessage());
				}
				System.out.println("Exif UserComment = " + exifComment);
			} catch (JpegProcessingException e2) {
				log.error("Error reading exif metadata.");
				log.error(e2.getMessage());
			}
			// cache the comment if one was found, otherwise an empty string.
			exifCommentText = exifComment;
		} 
		return exifCommentText;
	}

	/**
	 * Convenience method to check an image for a barcode.  Does not set any instance variables of CandidateImageFile, 
	 * and does not behave precisely as the getBarcodeText() methods.  Result state is not available from getBarcodeStatus()
	 * and both errors and the absence of a barcode in the image result in an empty string being returned.
	 * 
	 * @param image The BufferedImage to check for a barcode.
	 * @param positionTemplate The position template specifying where in the image to check for the barcode, if 
	 * TEMPLATE_NO_COMPONENT_PARTS, the entire image is checked for a barcode, otherwise only the part of the image
	 * specified by the template is checked.  
	 * @return the text of the barcode found in the barcode portion of the position template, or an empty string.
	 */
	public static String getBarcodeTextFromImage(BufferedImage image, PositionTemplate positionTemplate) { 
		String returnValue = "";
		if (positionTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) {
			// Check the entire image for a barcode and return.
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Result result;
			try {
				QRCodeReader reader = new QRCodeReader();
				Hashtable<DecodeHintType, Object> hints = null;
				hints = new Hashtable<DecodeHintType, Object>(3);
				hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
				result = reader.decode(bitmap,hints);
				returnValue= result.getText();
			} catch (ReaderException e) {
				returnValue = "";
			}
		} else { 
			// Check the part of the image specified by the template for the barcode.
			if (image != null) {
				if (image.getWidth() > positionTemplate.getBarcodeULPosition().width) {
					// image might plausibly match template
					int left = positionTemplate.getBarcodeULPosition().width;    //* @param left x coordinate of leftmost pixels to decode
					int top =  positionTemplate.getBarcodeULPosition().height;  //* @param top y coordinate of topmost pixels to decode
					int right =  left + positionTemplate.getBarcodeSize().width;  //* @param right one more than the x coordinate of rightmost pixels to decode. That is, we will decode
					int width =  positionTemplate.getBarcodeSize().width; 
					//*  pixels whose x coordinate is in [left,right)
					int bottom =  top + positionTemplate.getBarcodeSize().height; //* @param bottom likewise, one more than the y coordinate of the bottommost pixels to decode
					int height = positionTemplate.getBarcodeSize().height;
					LuminanceSource source = null;
					boolean inBounds = false;
					try { 
						source = new BufferedImageLuminanceSource(image, left,  top, width, height);
						inBounds = true;
					} catch (IllegalArgumentException e) { 
						inBounds = false;
						returnValue = "";
					} 		        		        
					if (inBounds) { 
						BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
						Result result;
						try {
							QRCodeReader reader = new QRCodeReader();
							Hashtable<DecodeHintType, Object> hints = null;
							hints = new Hashtable<DecodeHintType, Object>(3);
							hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
							result = reader.decode(bitmap,hints);
							returnValue= result.getText();
						} catch (ReaderException e) {
							returnValue = "";
						}
					} 
				}
			} 
		}
		return returnValue;
	}	
	/**
	 * Convenience method to check an image for a barcode.  Does not set any instance variables of CandidateImageFile, 
	 * and does not behave precisely as the getBarcodeText() methods.  Result state is not available from getBarcodeStatus()
	 * and both errors and the absence of a barcode in the image result in an empty string being returned.
	 * 
	 * @param image The BufferedImage to check for a barcode.
	 * @param positionTemplate The position template specifying where in the image to check for the barcode, if 
	 * TEMPLATE_NO_COMPONENT_PARTS, the entire image is checked for a barcode, otherwise only the part of the image
	 * specified by the template for the UnitTrayLabel is checked.  
	 * @return the text of the barcode found in the UnitTrayLabel (text) portion of the position template, or an empty string.
	 */
	public static String getBarcodeUnitTrayTextFromImage(BufferedImage image, PositionTemplate positionTemplate) { 
		String returnValue = "";
		if (positionTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) {
			// Check the entire image for a barcode and return.
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Result result;
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			try {
				QRCodeReader reader = new QRCodeReader();
				Hashtable<DecodeHintType, Object> hints = null;
				hints = new Hashtable<DecodeHintType, Object>(3);
				hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
				result = reader.decode(bitmap,hints);
				returnValue= result.getText();
			} catch (ReaderException e) {
				returnValue = "";
			}
		} else { 
			// Check the part of the image specified by the template for the barcode.
			if (image != null) {
				if (image.getWidth() > positionTemplate.getUtBarcodePosition().width) {
					// image might plausibly match template
					int left = positionTemplate.getUtBarcodePosition().width;    //* @param left x coordinate of leftmost pixels to decode
					int top =  positionTemplate.getUtBarcodePosition().height;  //* @param top y coordinate of topmost pixels to decode
					int right =  left + positionTemplate.getUtBarcodeSize().width;  //* @param right one more than the x coordinate of rightmost pixels to decode. That is, we will decode
					//*  pixels whose x coordinate is in [left,right)
					int bottom =  top + positionTemplate.getUtBarcodeSize().height; //* @param bottom likewise, one more than the y coordinate of the bottommost pixels to decode
					int width =  positionTemplate.getBarcodeSize().width; 
					int height =  positionTemplate.getBarcodeSize().height; 
					LuminanceSource source = null;
					boolean inBounds = false;
					try { 
						log.debug("Trying: " + left + " " + right + " " + top + " " + bottom);
						source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
						inBounds = true;
					} catch (IllegalArgumentException e) { 
						inBounds = false;
						returnValue = "";
					} 		
					if (inBounds) { 
						BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
						Result result;
						try {
							QRCodeReader reader = new QRCodeReader();
							Hashtable<DecodeHintType, Object> hints = null;
							hints = new Hashtable<DecodeHintType, Object>(3);
							hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
							result = reader.decode(bitmap,hints);
							returnValue= result.getText();
						} catch (ReaderException e) {
							returnValue = "";
						}
						if (returnValue.equals(""))  {
							// Try again with a sharpened image
							try { 
								log.debug("Trying sharpened: " + left + " " + right + " " + top + " " + bottom);
								Kernel kernel = new Kernel(3,3,matrix);
								ConvolveOp convolver = new ConvolveOp(kernel,ConvolveOp.EDGE_NO_OP,null);
								BufferedImage sharpened = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
								sharpened = convolver.filter(image, sharpened);
								source = new BufferedImageLuminanceSource(sharpened, left,  top,  width, height);
								inBounds = true;
							} catch (IllegalArgumentException e) { 
								inBounds = false;
								returnValue = e.toString() + " " + e.getMessage();
							} 
							bitmap = new BinaryBitmap(new HybridBinarizer(source));
							if (inBounds) { 
								try {
									QRCodeReader reader = new QRCodeReader();
									Hashtable<DecodeHintType, Object> hints = null;
									hints = new Hashtable<DecodeHintType, Object>(3);
									hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
									result = reader.decode(bitmap,hints);
									returnValue= result.getText();
								} catch (ReaderException e) {
									returnValue = "";
								}
							}
						}
						if (returnValue.equals(""))  {
							// Try again with a different box
							left = left - 10;
							right = right + 5;
							top = top + 5;
							bottom = bottom + 5;
							try { 
								log.debug("Trying: " + left + " " + right + " " + top + " " + bottom);
								source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
								inBounds = true;
							} catch (IllegalArgumentException e) { 
								inBounds = false;
								returnValue = e.toString() + " " + e.getMessage();
							} 
							bitmap = new BinaryBitmap(new HybridBinarizer(source));
							if (inBounds) { 
								try {
									QRCodeReader reader = new QRCodeReader();
									Hashtable<DecodeHintType, Object> hints = null;
									hints = new Hashtable<DecodeHintType, Object>(3);
									hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
									result = reader.decode(bitmap,hints);
									returnValue= result.getText();
								} catch (ReaderException e) {
									returnValue = "";
								}
							}
						}						
					} 
				}
			} 
		}
		return returnValue;
	}		


	/** 
	 * Scan part of an image, as specified by a the BarcodePosition of a PositionTemplate for a QR Code barcode.
	 * Check for error states with a call to getBarcodeStatus();
	 * 
	 * @param positionTemplate the position template to use to identify the part of the image that may contain 
	 * a barcode.
	 * @return a text string representing the content of the barcode, if any.   
	 */
	public String getBarcodeText(PositionTemplate positionTemplate) { 
		String returnValue = "";
		if (positionTemplate.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS)) {
			// Check the entire image for a barcode and return.
			returnValue = getBarcodeText();
		} else { 
			// Check the part of the image specified by the template for the barcode.
			BufferedImage image = null;
			String error = "";
			barcodeStatus = RESULT_ERROR;
			try {
				image = ImageIO.read(candidateFile);
			} catch (IOException e) {
				error = e.toString() + " " + e.getMessage();
				returnValue = error;
			}
			if (image == null) {
				returnValue =  "Could not decode image. " + error;
				barcodeStatus = RESULT_ERROR;
			} else { 
				if (image.getWidth() > positionTemplate.getBarcodeULPosition().width) {
					// image might plausibly match template
					int left = positionTemplate.getBarcodeULPosition().width;    //* @param left x coordinate of leftmost pixels to decode
					int top =  positionTemplate.getBarcodeULPosition().height;  //* @param top y coordinate of topmost pixels to decode
					int right =  left + positionTemplate.getBarcodeSize().width;  //* @param right one more than the x coordinate of rightmost pixels to decode. That is, we will decode
					//*  pixels whose x coordinate is in [left,right)
					int bottom =  top + positionTemplate.getBarcodeSize().height; //* @param bottom likewise, one more than the y coordinate of the bottommost pixels to decode
					int width =  positionTemplate.getBarcodeSize().width; 
					int height =  positionTemplate.getBarcodeSize().height; 
					LuminanceSource source = null;
					boolean inBounds = false;
					try { 
						source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
						inBounds = true;
					} catch (IllegalArgumentException e) { 
						inBounds = false;
						returnValue = e.toString() + " " + e.getMessage();
						barcodeStatus = RESULT_ERROR;
					} 
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					if (inBounds) { 
						Result result;
						try {
							QRCodeReader reader = new QRCodeReader();
							Hashtable<DecodeHintType, Object> hints = null;
							hints = new Hashtable<DecodeHintType, Object>(3);
							hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
							result = reader.decode(bitmap,hints);
							returnValue= result.getText();
							barcodeStatus = RESULT_BARCODE_SCANNED;
						}catch (IllegalArgumentException e) { 
							returnValue = e.toString() + " " + e.getMessage();
							barcodeStatus = RESULT_ERROR;
						} catch (ReaderException e) {
							returnValue = e.toString() + " " + e.getMessage();
							barcodeStatus = RESULT_ERROR;
						}
					} 
					// TODO: Try again, slightly displaced.
					if (barcodeStatus==RESULT_ERROR || returnValue.equals("")) { 
						left = left - 1;
						top =  top + 1;
						right =  right - 1; 
						//*  pixels whose x coordinate is in [left,right)
						bottom =  bottom + 1;
						source = null;
						inBounds = false;
						try { 
							source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
							inBounds = true;
						} catch (IllegalArgumentException e) { 
							inBounds = false;
							returnValue = e.toString() + " " + e.getMessage();
							barcodeStatus = RESULT_ERROR;
						} 
						if (inBounds) { 		
							bitmap = new BinaryBitmap(new HybridBinarizer(source));
							Result result;
							try {
								QRCodeReader reader = new QRCodeReader();
								Hashtable<DecodeHintType, Object> hints = null;
								hints = new Hashtable<DecodeHintType, Object>(3);
								hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
								result = reader.decode(bitmap,hints);
								returnValue= result.getText();
								barcodeStatus = RESULT_BARCODE_SCANNED;
							}catch (IllegalArgumentException e) { 
								returnValue = e.toString() + " " + e.getMessage();
								barcodeStatus = RESULT_ERROR;
							} catch (ReaderException e) {
								returnValue = e.toString() + " " + e.getMessage();
								barcodeStatus = RESULT_ERROR;
							}
						} 
						// TODO: Try again, slightly displaced.
						if (barcodeStatus==RESULT_ERROR || returnValue.equals("")) { 
							left = left - 1;
							top =  top + 1;
							right =  right - 1; 
							//*  pixels whose x coordinate is in [left,right)
							bottom =  bottom + 1;
							source = null;
							inBounds = false;
							try { 
								source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
								inBounds = true;
							} catch (IllegalArgumentException e) { 
								inBounds = false;
								returnValue = e.toString() + " " + e.getMessage();
								barcodeStatus = RESULT_ERROR;
							} 
							if (inBounds) { 
								bitmap = new BinaryBitmap(new HybridBinarizer(source));
								Result result;
								try {
									QRCodeReader reader = new QRCodeReader();
									Hashtable<DecodeHintType, Object> hints = null;
									hints = new Hashtable<DecodeHintType, Object>(3);
									hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
									result = reader.decode(bitmap,hints);
									returnValue= result.getText();
									barcodeStatus = RESULT_BARCODE_SCANNED;
								}catch (IllegalArgumentException e) { 
									returnValue = e.toString() + " " + e.getMessage();
									barcodeStatus = RESULT_ERROR;
								} catch (ReaderException e) {
									returnValue = e.toString() + " " + e.getMessage();
									barcodeStatus = RESULT_ERROR;
								}
							} 						

							// TODO: Try again, slightly displaced.
							if (barcodeStatus==RESULT_ERROR || returnValue.equals("")) { 
								left = left - 1;
								top =  top + 1;
								right =  right - 1; 
								//*  pixels whose x coordinate is in [left,right)
								bottom =  bottom + 1;
								source = null;
								inBounds = false;
								try { 
									source = new BufferedImageLuminanceSource(image, left,  top, width, height);
									inBounds = true;
								} catch (IllegalArgumentException e) { 
									inBounds = false;
									returnValue = e.toString() + " " + e.getMessage();
									barcodeStatus = RESULT_ERROR;
								} 
								if (inBounds) { 
									bitmap = new BinaryBitmap(new HybridBinarizer(source));
									Result result;
									try {
										QRCodeReader reader = new QRCodeReader();
										Hashtable<DecodeHintType, Object> hints = null;
										hints = new Hashtable<DecodeHintType, Object>(3);
										hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
										result = reader.decode(bitmap,hints);
										returnValue= result.getText();
										barcodeStatus = RESULT_BARCODE_SCANNED;
									}catch (IllegalArgumentException e) { 
										returnValue = e.toString() + " " + e.getMessage();
										barcodeStatus = RESULT_ERROR;
									} catch (ReaderException e) {
										returnValue = e.toString() + " " + e.getMessage();
										barcodeStatus = RESULT_ERROR;
									}
								} 						
							}
						}
					}

				} else { 
					// try scanning the entire image
					returnValue = getBarcodeText();
				}
			} 
		}
		return returnValue;
	}

	/** Scan the entire image for a QR Code barcode
	 * Check for error states with a call to getBarcodeStatus()  
	 * 
	 * @return a string representing the text of the barcode, if any.
	 */
	public String getBarcodeText() {
		String returnValue = "";
		BufferedImage image = null;
		String error = "";
		barcodeStatus = RESULT_ERROR;
		try {
			image = ImageIO.read(candidateFile);
		} catch (IOException e) {
			error = e.toString() + " " + e.getMessage();
			returnValue = error;
		}
		if (image == null) {
			returnValue =  "Could not decode image. " + error;
			barcodeStatus = RESULT_ERROR;
		} else { 
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Result result;
			try {
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
				QRCodeReader reader = new QRCodeReader();
				Hashtable<DecodeHintType, Object> hints = null;
				hints = new Hashtable<DecodeHintType, Object>(3);
				hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
				result = reader.decode(bitmap,hints);
				returnValue= result.getText();
				barcodeStatus = RESULT_BARCODE_SCANNED;
			} catch (ReaderException e) {
				returnValue = e.toString() + " " + e.getMessage();
				barcodeStatus = RESULT_ERROR;
			} catch (IllegalArgumentException e1) {
				// seen in MCZ-ENT00051680
				returnValue = e1.toString() + " " + e1.getMessage();
				barcodeStatus = RESULT_ERROR; 
			} catch (Exception e2) { 
				// Just in case reader.decode throws some other error, 
				// we should trap it rather than failing.
				returnValue =  "Unexpected error from  ZXing decoder: " + e2.getMessage();
				barcodeStatus = RESULT_ERROR; 
			}
		} 
		return returnValue;
	}

	public int getBarcodeStatus() { 
		return barcodeStatus;
	}

}
