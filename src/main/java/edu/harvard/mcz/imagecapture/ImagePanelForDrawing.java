/**
 * ImagePanelForDrawing.java
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;

/** ImagePanelForDrawing extends ImagePanel to allow drawing arbitrary boxes on top of
 * an image that is scaled to fit into an ImagePanel.
 * 
 * @author Paul J. Morris
 *
 */
public class ImagePanelForDrawing extends ImagePanel implements Serializable {

	private static final long serialVersionUID = 3323530495180883867L;


	/** Draw an outline box with dimensions scaled to fit onto the current zoom of the image.  Parameters
	 * are provided as pixel dimensions unscaled relative to the original image.   If any of the
	 * parameters are null, nothing will be drawn.
	 * 
	 * @param upperLeft position (in pixels of the original image) of the upper left corner of the box.
	 * @param size height and width (in pixels of the original image) of the box to draw.
	 * @param color outline color of the box to draw.
	 */
	public void drawBox(Dimension upperLeft, Dimension size, Color color) { 
		if (upperLeft!=null && size!=null && color!=null)  {
			Graphics2D g = (Graphics2D)super.getGraphics();
			double z = super.getZoom();
			int x = (int) (upperLeft.width * z);
			int y = (int) (upperLeft.height * z); 
			int width = (int) (size.width * z);
			int height = (int) (size.height * z);
			g.setColor(color);
			g.drawRect(x, y, width, height);
		} 
	}

	/**Draw an outline box with dimensions scaled to fit onto the current zoom of the image.  Parameters
	 * are provided as pixel dimensions unscaled relative to the original image.   If any of the
	 * parameters are null, nothing will be drawn.
	 * 
	 * @param upperLeft position (in pixels of the original image) of the upper left corner of the box.
	 * @param size height and width (in pixels of the original image) of the box to draw.
	 * @param color outline color of the box to draw.
	 * @param stroke pen stroke to use to draw the box outline
	 */
	public void drawBox(Dimension upperLeft, Dimension size, Color color, Stroke stroke) { 
		if (upperLeft!=null && size!=null && color!=null)  {
			Graphics2D g = (Graphics2D)super.getGraphics();
			double z = super.getZoom();
			int x = (int) (upperLeft.width * z);
			int y = (int) (upperLeft.height * z); 
			int width = (int) (size.width * z);
			int height = (int) (size.height * z);
			g.setStroke(stroke);
			g.setColor(color);
			g.drawRect(x, y, width, height);
		} 
	}	
	
	public void clearOverlay() { 
		super.clearOverlay();
	}
	
	
}
