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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import edu.harvard.mcz.imagecapture.data.BulkMedia;
import edu.harvard.mcz.imagecapture.data.UnitTrayLabel;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
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
	private String dateCreated = null;

	private static final Log log = LogFactory.getLog(CandidateImageFile.class);

	/**
	 * Constructor which detects the template to be used with the candidate image file.
	 * 
	 * @param aFile
	 * @throws UnreadableFileException
	 */
	public CandidateImageFile(File aFile) throws UnreadableFileException  {
		if (!aFile.canRead()) { 
			try {
				throw new UnreadableFileException("Can't read file " + aFile.getCanonicalPath());
			} catch (IOException e) {
				throw new UnreadableFileException("IOException on trying to get filename.");
			}
		}
		PositionTemplate template = new PositionTemplate();
		
		// detect template to use.
		MCZBarcodePositionTemplateDetector detector = new MCZBarcodePositionTemplateDetector();
		
		String templateName = detector.detectTemplateForImage(aFile);
		
		try {
			template = new PositionTemplate(templateName);
		} catch (NoSuchTemplateException e) {
			log.error("Position template detector returned an unknown template name: " + templateName + ".", e);
		}
		setFile(aFile, template);
	}
	
	/**
	 * Self standing starting point to extract barcodes from image files. 
	 * 
	 * @param args command line arguments, run with none or -h for help.
	 */
	public static void main(String[] args) { 
		// Load properties
		ImageCaptureProperties properties = new ImageCaptureProperties();
		Singleton.getSingletonInstance().setProperties(properties);
		log.debug("Properties loaded");
		
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
		options.addOption("f","file",true,"Check one file for Barcodes.");
		options.addOption("h","help",false,"Get help.");
		options.addOption("u","ui",false,"Launch UI to check a directory.");
		try { 
			CommandLine cmd = parser.parse(options, args);
			boolean hasFile = cmd.hasOption("file");
			boolean hasHelp = cmd.hasOption("help");
			if (hasFile) { 
		        int exit = 1;
				String filename = cmd.getOptionValue("file");
				String line = CandidateImageFile.parseOneFile(filename); 
				if (line!=null) { 
					System.out.println(line);
					exit = 0;
				}
				System.exit(exit);
			} else if (hasHelp) { 
				throw new ParseException("No option specified.");
			} else { 
				// by default, run ui.
				try {
					showBulkMediaGUI();
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "CandidateImageFile", "Check files for a barcodes.", options, "Specify filename to check.  \nDefault if no options are selected is to launch a GUI.", true );
			System.exit(1);
		}
	}
	
	protected static void showBulkMediaGUI() { 
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	BulkMediaFrame frame = new BulkMediaFrame();
				frame.pack();
				frame.setVisible(true);;
            }
        });
	}
	
	protected static String parseOneFile(String filename) {
		String result = null;  
		File f = new File(filename);
		try {
			CandidateImageFile file = new CandidateImageFile(f,new PositionTemplate(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS));
			String exif = file.getExifUserCommentText();
			String scan = file.getBarcodeText();
			if (scan.startsWith("{\"m1p\":")) { 
				result = '"' + f.getName() + "\",\"" + exif + '"' ;
			} else { 
				if (scan.equals(exif)) { 
				    result = '"' + f.getName() + "\",\"" + scan + '"' ;
				} else { 
				    result = '"' + f.getName() + "\",\"" + exif + '"' ;
				}
			}
		} catch (UnreadableFileException e) {
			System.out.println("Unable To Read  " + filename);
		} catch (NoSuchTemplateException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	protected static BulkMedia parseOneFileToBulkMedia(String filename) {
		BulkMedia result = new BulkMedia();  
		File f = new File(filename);
		try {
			CandidateImageFile file = new CandidateImageFile(f,new PositionTemplate(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS));
			String exif = file.getExifUserCommentText();
			String scan = file.getBarcodeText();
			String madeDate = file.getExifDateCreated();
			log.debug(madeDate);
			if (madeDate!=null)  {
				result.setMadeDate(madeDate);
			}
			String barcode = null;
			result.setOriginalFilename(f.getName());
			if (scan.startsWith("{\"m1p\":")) {
				barcode = exif;
			} else { 
				if (scan.equals(exif)) { 
					barcode = scan;
				} else { 
					barcode = exif;
				}
			}
			if (barcode.startsWith("MCZ-ENT")) { 
				result.setCatalogNumber("MCZ:Ent:" + barcode.substring(7).replaceFirst("^0*", ""));
			}
			if (filename.startsWith("http://")) { 
				result.setMedia_URI(filename);
			} else { 
			    File preview_file = ThumbnailBuilder.getThumbFileForFile(f);
				// TODO: Add preview uri.
				if (!result.setURI(f)) { 
			        System.out.println("Can't extract URI from path for " + filename);
				}
				if (!result.setPreviewURI(preview_file)) { 
			        System.out.println("Can't extract URI from path for preview file " + preview_file.getName());
				}
			}
			log.debug(madeDate);
		} catch (UnreadableFileException e) {
			log.error(e.getMessage());
			System.out.println("Unable To Read  " + filename);
		} catch (NoSuchTemplateException e) {
			log.error(e.getMessage());
		}
		log.debug(result.toString());
		return result;
	}	
	
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
		if (!aFile.canRead()) { 
			try {
				throw new UnreadableFileException("Can't read file " + aFile.getCanonicalPath());
			} catch (IOException e) {
				throw new UnreadableFileException("IOException on trying to get filename.");
			}
		}
	}

	/**
	 * Constructor with no parameters to use to access convenience static methods.
	 * Must follow with setFile() to use for processing images.
	 * @see setFile();
	 */
	public CandidateImageFile() { 
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

	/**
	 * Utility inner class to carry text and status values from barcode reader 
	 * methods.
	 * 
	 * @author mole
	 *
	 */
	private class TextStatus { 
		String text;
		int status;
		/**
		 * @param text
		 * @param status
		 */
		public TextStatus(String text, int status) {
			super();
			this.text = text;
			this.status = status;
		}
		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}
		/**
		 * @param text the text to set
		 */
		public void setText(String text) {
			this.text = text;
		}
		/**
		 * @return the status
		 */
		public int getStatus() {
			return status;
		}
		/**
		 * @param status the status to set
		 */
		public void setStatus(int status) {
			this.status = status;
		}



	}


	/**
	 * Check a LuminanceSource for a barcode, handle exceptions, and return
	 * an object containing the text read (or an error message) and the corresponding
	 * value to use for barcodeStatus.
	 * 
	 * @param source LuminanceSource to check for a barcode.
	 * @return a TextStatus object containing the barcodeStatus value and the 
	 * text found (or the error message).
	 */
	private TextStatus checkSourceForBarcode(LuminanceSource source, boolean generateDebugImage) { 
		TextStatus returnValue = new TextStatus("",RESULT_NOT_CHECKED);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

		if (generateDebugImage) { 
			try {
				int h = bitmap.getBlackMatrix().getHeight();
				int w = bitmap.getBlackMatrix().getWidth();
				BufferedImage temp = new BufferedImage(h, w, BufferedImage.TYPE_BYTE_GRAY);
				Graphics g = temp.getGraphics();
				g.setColor(Color.WHITE);
				g.drawRect(0, 0, w, h);
				g.setColor(Color.BLACK);
				for (int i=0; i<h; i++) { 
					for (int j=0; j<w; j++) { 
						try {
							if (bitmap.getBlackMatrix().get(i, j)) { 
								g.setColor(Color.BLACK);
								g.drawRect(i, j, 1, 1);
							} else { 
								g.setColor(Color.WHITE);
								g.drawRect(i, j, 1, 1);
							}
						} catch (ArrayIndexOutOfBoundsException e) { 
							// 
						}
					}
				}

				ImageIO.write(temp, "png", new File("TempBarcodeCrop.png"));
			} catch (NotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}

		Result result;
		try {
			QRCodeReader reader = new QRCodeReader();
			Hashtable<DecodeHintType, Object> hints = null;
			hints = new Hashtable<DecodeHintType, Object>(3);
			hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 	
			//Probable bug in xzing, reader.decode can throw ArrayIndexOutOfBoundsException
			//as well as the expected ReaderException.  It looks like there's an assumption 
			//hidden in the bitmapMatrix that the height and width are the same.
			result = reader.decode(bitmap,hints);
			returnValue.setText(result.getText());
			returnValue.setStatus(RESULT_BARCODE_SCANNED);
		} catch (ReaderException e) {
			returnValue.setText(e.toString() + " " + e.getMessage());
			returnValue.setStatus(RESULT_ERROR);
		} catch (ArrayIndexOutOfBoundsException e) { 
			returnValue.setText(e.toString() + " " + e.getMessage());
			returnValue.setStatus(RESULT_ERROR);
		}
		return returnValue;
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
				log.debug(candidateFile.getCanonicalFile());
			} catch (IOException e) {
				error = e.toString() + " " + e.getMessage();
				returnValue = error;
			}
			if (image == null) {
				returnValue =  "Could not decode image. " + error;
				barcodeStatus = RESULT_ERROR;
			} else { 
				// Could read image.  Try reading barcode from templated location.
				if (image.getWidth() >= positionTemplate.getUtBarcodePosition().width && image.getWidth() == Math.round(positionTemplate.getImageSize().getWidth())) {
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

					// **** First try, straight check of template area.
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
						log.debug("Looking for barcode in raw image crop.");
						TextStatus checkResult = checkSourceForBarcode(source,true);
						returnValue = checkResult.getText();
						barcodeStatus = checkResult.getStatus();
						log.debug(returnValue);
						log.debug("barcodeStatus=" + barcodeStatus);

						// If failed, try again in with a sequence of other checks

						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// *** Second try:  Try again with a rescaled image
							double scalingWidth = 400;
							try { 
								scalingWidth = Double.parseDouble(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGERESCALE));
								if (scalingWidth < 1) { scalingWidth = 400; } 
							} catch (NumberFormatException e) { 
								log.error(e.getMessage());
							}
							if (width>scalingWidth) { 
						        log.debug("Looking for barcode with scaled image crop: " +  Double.toString(scalingWidth) + "px.");
								Double scale = scalingWidth / width;
								int scaledHeight = (int) (height * scale); 
								BufferedImage cropped = image.getSubimage(left, top, width, height);
								int initialH = cropped.getHeight();
								int initialW = cropped.getWidth();
								BufferedImage scaled = new BufferedImage(initialW, initialH, cropped.getType());
								AffineTransform rescaleTransform = new AffineTransform();
								rescaleTransform.scale(scale, scale);
								AffineTransformOp scaleOp = new AffineTransformOp(rescaleTransform, AffineTransformOp.TYPE_BILINEAR);
								scaled = scaleOp.filter(cropped, scaled);								
								source = new BufferedImageLuminanceSource(scaled);
								checkResult = checkSourceForBarcode(source,true);
								returnValue = checkResult.getText();
								barcodeStatus = checkResult.getStatus();
								log.debug(returnValue);
								log.debug("barcodeStatus=" + barcodeStatus);
							}
						}
						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// ***** Third Try: Try again with a sharpened image
							//
							// Note: contrast enhancement doesn't appear to help, likely 
							// because zxing is converting to binary bitmap at some threshold.
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
								source = new BufferedImageLuminanceSource(sharpened, left,  top, width, height);										
								inBounds = true;
							} catch (IllegalArgumentException e) { 
								inBounds = false;
								returnValue = e.toString() + " " + e.getMessage();
								barcodeStatus = RESULT_ERROR;
							}
							if (inBounds) { 
								checkResult = checkSourceForBarcode(source,false);
								returnValue = checkResult.getText();
								barcodeStatus = checkResult.getStatus();
							}
							log.debug(returnValue);
							log.debug("barcodeStatus=" + barcodeStatus);
						} 
						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// **** Fourth Try: Try again with a different box
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
							if (inBounds) { 
								checkResult = checkSourceForBarcode(source,false);
								returnValue = checkResult.getText();
								barcodeStatus = checkResult.getStatus();
							}
						}
						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// **** Fifth try: Try again with a different box
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
							if (inBounds) { 
								checkResult = checkSourceForBarcode(source,false);
								returnValue = checkResult.getText();
								barcodeStatus = checkResult.getStatus();
							}
						}
						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// ***** Sixth Try: Try again with a different box
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
							if (inBounds) { 
								checkResult = checkSourceForBarcode(source,false);
								returnValue = checkResult.getText();
								barcodeStatus = checkResult.getStatus();
							}
						}
						if (barcodeStatus == RESULT_ERROR || returnValue.equals(""))  {
							// **** Seventh Try:  Try again with a different box
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
							if (inBounds) { 
								checkResult = checkSourceForBarcode(source,false);
								returnValue = checkResult.getText();
								barcodeStatus = checkResult.getStatus();
							}
						}
					} // In bounds
				} else {
					// image is narrower than templated area.
					log.debug("Skipping Template.  ImageWidth="+image.getWidth()+"; TemplateWidth="+Math.round(positionTemplate.getImageSize().getWidth()));
				}
				
			} // image is readable
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
				Metadata metadata = JpegMetadataReader.readMetadata(candidateFile, JpegMetadataReader.ALL_READERS);
				

				
				// [Exif] User Comment
				Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor((ExifSubIFDDirectory) exifDirectory);
				exifComment = descriptor.getUserCommentDescription();
				log.debug("Exif UserComment = " + exifComment);
			} catch (JpegProcessingException e2) {
				log.error("Error reading exif metadata.");
				log.error(e2.getMessage());
			} catch (NullPointerException e1) {
				log.error("Error reading exif metadata, ExifSubIFDDirectory not found.");
				log.error(e1.getMessage());
			} catch (IOException e) {
				log.error("Error reading file for exif metadata.");
				log.error(e.getMessage());
			} catch (ImageProcessingException e) {
				log.error("Error processing file for metadata.");
				log.error(e.getMessage());
			}
			if (exifComment==null || exifComment.trim().length()==0) { 
				// Try to see if there is an xmp dc:description block
				Metadata metadata;
				try {
					metadata = JpegMetadataReader.readMetadata(candidateFile, JpegMetadataReader.ALL_READERS);

					XmpDirectory xmpDirectory = (XmpDirectory)metadata.getFirstDirectoryOfType(XmpDirectory.class);
					if (xmpDirectory!=null && xmpDirectory.getXMPMeta()!=null) { 
					log.debug(xmpDirectory.getXMPMeta().dumpObject());
					/* 
				    http://purl.org/dc/elements/1.1/ = "dc:"	(0x80000000 : SCHEMA_NODE)
					    dc:description	(0x1e00 : ARRAY | ARRAY_ORDERED | ARRAY_ALTERNATE | ARRAY_ALT_TEXT)
						    [1] = "MCZ-ENT00597110"	(0x50 : HAS_QUALIFIER | HAS_LANGUAGE)
								?xml:lang = "x-default"	(0x20 : QUALIFIER)
					 */
					XMPMeta xmpMeta = xmpDirectory.getXMPMeta();
					try {
						String description = xmpMeta.getArrayItem("http://purl.org/dc/elements/1.1/", "dc:description", 1).getValue();
						log.debug(description);
						if (description!=null && description.trim().length()>0) { 
							exifComment = description;
						}
					} catch (NullPointerException e1) {
						log.error(e1.getMessage(),e1);
					} catch (XMPException e) {
						log.error(e.getMessage(),e);
					}				
					}
				} catch (JpegProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			// cache the comment if one was found, otherwise an empty string.
			exifCommentText = exifComment;
		} 
		
		return exifCommentText;
	}
	
	
	public String getExifDateCreated() { 
		// Actual read attempt is only invoked once,
		// subsequent calls return cached value.
		if (dateCreated==null) { 
			// read, or re-read the file for a comment
			Date date = null;
			try {
				Metadata metadata = JpegMetadataReader.readMetadata(candidateFile);
				// [Exif] date 
				Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME);
				if (date==null) {
				    date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
				}
				if (date==null) {
				    date = exifDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);
				}
				log.debug("Exif DateTime = " + SimpleDateFormat.getDateInstance().format(date));
			} catch (JpegProcessingException e2) {
				log.error("Error reading exif metadata.");
				log.error(e2.getMessage());
			} catch (IOException e) {
				log.error("Error reading file for exif metadata.");
				log.error(e.getMessage());
			}
			// cache the date if one was found, otherwise null.
			if (date!=null) {
				// date format shown on the media bulkloader example page.
				SimpleDateFormat format = new SimpleDateFormat("dd MMMMM yyyy");
			   dateCreated = format.format(date);
			}
		} 
		return dateCreated;
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
			CandidateImageFile temp = new CandidateImageFile();
			TextStatus checkResult = temp.checkSourceForBarcode(source, true);
			returnValue = checkResult.getText();
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
						if (inBounds) { 
							CandidateImageFile temp = new CandidateImageFile();
							TextStatus checkResult = temp.checkSourceForBarcode(source, true);
							returnValue = checkResult.getText();
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
	 * Attempts read of relevant crop from image, then attempts this with crop area scaled down, then attempts it 
	 * with crop area sharpened.  Does not include shifts of location of crop area.
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
					int width =  positionTemplate.getUtBarcodeSize().width; 
					int height =  positionTemplate.getUtBarcodeSize().height; 
					LuminanceSource source = null;
					boolean inBounds = false;
					try { 
						log.debug("Trying: " + left + " " + right + " " + top + " " + bottom);
						log.debug("Trying: " + left + " " + top + " w=" + width + " h=" + height);
						source = new BufferedImageLuminanceSource(image, left,  top,  width, height);
						inBounds = true;
					} catch (IllegalArgumentException e) { 
						inBounds = false;
						returnValue = "";
					} 		
					if (inBounds) { 
						BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

						try {
							int h = bitmap.getBlackMatrix().getHeight();
							int w = bitmap.getBlackMatrix().getWidth();
							BufferedImage temp = new BufferedImage(h, w, BufferedImage.TYPE_BYTE_GRAY);
							Graphics g = temp.getGraphics();
							g.setColor(Color.WHITE);
							g.drawRect(0, 0, w, h);
							g.setColor(Color.BLACK);
							for (int i=0; i<h; i++) { 
								for (int j=0; j<w; j++) { 
									try {
										if (bitmap.getBlackMatrix().get(i, j)) { 
											g.setColor(Color.BLACK);
											g.drawRect(i, j, 1, 1);
										} else { 
											g.setColor(Color.WHITE);
											g.drawRect(i, j, 1, 1);
										}
									} catch (ArrayIndexOutOfBoundsException e) { 
										// 
									}
								}
							}

							ImageIO.write(temp, "png", new File("TempBarcodeCrop.png"));
						} catch (NotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						Result result;
						try {
							QRCodeReader reader = new QRCodeReader();
							Hashtable<DecodeHintType, Object> hints = null;
							hints = new Hashtable<DecodeHintType, Object>(3);
							hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE); 
							result = reader.decode(bitmap,hints);
							returnValue= result.getText();
							log.debug(returnValue);
						} catch (ReaderException e) {
							e.printStackTrace();
							log.debug(e.getMessage());
							returnValue = "";
						}
						if (returnValue.equals(""))  {
							// Try again with a scaled down image
							double scalingWidth = 400;
							try { 
								scalingWidth = Double.parseDouble(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGERESCALE));
								if (scalingWidth < 1) { scalingWidth = 400; } 
							} catch (NumberFormatException e) { 
								log.error(e.getMessage());
							}
							if (width>scalingWidth) { 
								Double scale = scalingWidth / width;
								int scaledHeight = (int) (height * scale); 
								BufferedImage cropped = image.getSubimage(left, top, width, height);
								int initialH = cropped.getHeight();
								int initialW = cropped.getWidth();
								BufferedImage scaled = new BufferedImage(initialW, initialH, cropped.getType());
								AffineTransform rescaleTransform = new AffineTransform();
								rescaleTransform.scale(scale, scale);
								AffineTransformOp scaleOp = new AffineTransformOp(rescaleTransform, AffineTransformOp.TYPE_BILINEAR);
								scaled = scaleOp.filter(cropped, scaled);								
								source = new BufferedImageLuminanceSource(scaled);
								CandidateImageFile temp = new CandidateImageFile();
								TextStatus checkResult = temp.checkSourceForBarcode(source,true);
								returnValue = checkResult.getText();
								int barcodeStatus = checkResult.getStatus();
								log.debug(returnValue);
								log.debug("barcodeStatus=" + barcodeStatus);
							}	
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
				if (image.getWidth() > positionTemplate.getBarcodeULPosition().width && image.getWidth() == Math.round(positionTemplate.getImageSize().getWidth())) {
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
						log.debug(image.getWidth() + "," + image.getHeight()  + ": " + left + " " + top + " " + width + "," + height );
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
					// image is narrower than templated area.
					returnValue = "Image is different size from Template.";
					log.debug("ImageWidth="+image.getWidth()+"; TemplateWidth="+Math.round(positionTemplate.getImageSize().getWidth()));
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
