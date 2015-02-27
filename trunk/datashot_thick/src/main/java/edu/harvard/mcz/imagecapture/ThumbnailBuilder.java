/**
 * ThumbnailBuilder.java
 * edu.harvard.mcz.imagecapture
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
package edu.harvard.mcz.imagecapture;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.imgscalr.Scalr;

/**
 * @author mole
 *
 */
public class ThumbnailBuilder implements Runnable {
	private static final Log log = LogFactory.getLog(ThumbnailBuilder.class);
	
	File startPoint;
	int thumbnailCounter;

	public ThumbnailBuilder(File aStartPoint) { 
			startPoint = aStartPoint;
			thumbnailCounter = 0;
	}
	
	public int getThumbnailCount() { 
		return thumbnailCounter;
	}

	public static File getThumbFileForFile(File file) { 
		File thumbsDir = new File(file.getParentFile().getPath() + File.separator + "thumbs");
		File result = new File(thumbsDir.getPath().concat(File.separator).concat(file.getName()));
		return result;
	}
	
	@Override
	public void run() {
			int existsCounter = 0;
			// mkdir thumbs ; mogrify -path thumbs -resize 80x120 *.JPG					    
			if (startPoint.isDirectory() &&  (!startPoint.getName().equals("thumbs"))) { 
				File thumbsDir = new File(startPoint.getPath() + File.separator + "thumbs");
				log.debug(thumbsDir.getPath());
				if (!thumbsDir.exists()) { 
					thumbsDir.mkdir();
					thumbsDir.setWritable(true);
					log.debug("Creating " + thumbsDir.getPath());
				}
				File[] potentialFilesToThumb = startPoint.listFiles();
				List<File> filesToThumb = new ArrayList<File>();
				int filesToThumbCount = 0;
				for (int i=0; i<potentialFilesToThumb.length; i++) { 
					if (potentialFilesToThumb[i].getName().endsWith(".JPG")) { 
						filesToThumb.add(potentialFilesToThumb[i]);
						filesToThumbCount++;
					}
				}
				if (filesToThumbCount>0) { 
					int targetWidth = 100;
					int targetHeight = 150;
					
					Iterator<File> i = filesToThumb.iterator();
					while (i.hasNext()) { 
						File file = i.next();
						File output = new File(thumbsDir.getPath().concat(File.separator).concat(file.getName()));
						if (!output.exists()) { 
							// don't overwrite existing thumnails
							try {
								BufferedImage img = ImageIO.read(file); 
								BufferedImage thumbnail = Scalr.resize(img, Scalr.Method.BALANCED, Scalr.Mode.FIT_TO_WIDTH, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
								// img.createGraphics().drawImage(ImageIO.read(file).getScaledInstance(targetWidth, targetHeigh, Image.SCALE_SMOOTH),0,0,null);
								ImageIO.write(thumbnail, "jpg", output);
								thumbnailCounter++;
							} catch (IOException e1) {
								log.error(e1.getMessage(),e1);
			                    JOptionPane.showMessageDialog(null,e1.getMessage() + "\n", "Error", JOptionPane.ERROR_MESSAGE);
							}
						} else { 
							existsCounter ++;
						}
					}
					
				} else {
					String message = "No *.JPG files found in " + startPoint.getPath();
					log.debug(message);
				}
			}
			String exists = "";
			if (existsCounter>0) { exists = "\nSkipped "+ existsCounter +" existing thumbnails."; }  
			JOptionPane.showMessageDialog(null,"Done building " + thumbnailCounter + " thumbnails in ./thumbs/" + exists, "Thumbnails Built.", JOptionPane.INFORMATION_MESSAGE);
	} 
			
}
