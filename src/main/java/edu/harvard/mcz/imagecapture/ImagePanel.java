/**
 * ImagePanel.java
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import edu.harvard.mcz.imagecapture.utility.MathUtility;

/** ImagePanel displays a zoomable image in a JPanel.  Support class for ImageZoomPanel,
 * which includes both the controls and the image in an ImagePanel.
 * 
 * @author Paul J. Morris
 * @see edu.harvard.mcz.imagecapture.ImageZoomPanel
 */
public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 8827456895472324699L;

	//image object
	protected Image image;
	private Image originalImage;
	// Zoom level, 1 is pixel for pixel, 
	// less than 1 for a reduced size image,
	// more than 1 for an enlarged image.
	private double zoom = 1.0;
	// proportion to zoom in or out in one step
	private double zoomFactor = 0.1;  // 10% by default
	
	// position of top left of underlying image in window,
	// default is 0,0
	int ul_x = 0;
	int ul_y = 0;

	public ImagePanel(Image anImage) {	
		image = anImage;
		originalImage = anImage;
	}

	public ImagePanel() {
		image = null;
		originalImage = null;
	}

	public void setImage(Image anImage) { 
		image = anImage;
		originalImage = anImage;
		ul_x = 0;
		ul_y = 0;
		this.repaint();
	}
	
	public void clearOverlay() { 
		image = originalImage;
		paintComponent(this.getGraphics());
	}

	public void paintComponent(Graphics g)   { 
		Graphics2D g2D = (Graphics2D)g;
		if (image==null) { 
			g2D.setColor(Color.GRAY);
			g2D.fillRect(0, 0, getWidth(), getHeight());
		} else { 
			g2D.setColor(Color.WHITE);
			g2D.fillRect(0, 0, getWidth(), getHeight());
		}

		if (MathUtility.equalTenPlaces(zoom,1.0)) { 
			g2D.getTransform().setToIdentity();
		}  else { 
			g2D.scale(zoom, zoom);
		} 
		g2D.drawImage(image, ul_x, ul_y, this); 
	}

	public Dimension getPreferredSize() {
		Dimension result = new Dimension(100,100);
		if (image!=null) { 
			result = new Dimension(
					(int)(image.getWidth(this) * zoom),
					(int)(image.getHeight(this) * zoom ));
		} 
		return result;
	}

	public void zoomIn() { 
		zoom = zoom + zoomFactor;
	}

	public void zoomOut() { 
		zoom = zoom - zoomFactor;
	}

	public void fullSize() { 
		zoom = 1.0;
		ul_x = 0;
		ul_y = 0;
	}

	/**
	 * Zoom this image to fit entirely into a parent viewport.
	 */
	public void zoomToFit() { 
		JViewport view =  (JViewport) this.getParent();
		if (view!=null) { 
		   zoomToFit(view.getWidth(), view.getWidth());
		} 
	}
	
	/**
	 * Scale this image to fit entirely within a rectangle
	 * defined by a width and height.
	 * 
	 * @param x width this image is to fit into
	 * @param y height this image is to fit into.
	 */
	public void zoomToFit(int x, int y) {  
		double windowHeight = y;
		double windowWidth = x;
		double imageHeight = image.getHeight(this);
		double imageWidth = image.getWidth(this);
		double zh = windowHeight/imageHeight;
		double zw = windowWidth/imageWidth;
		// pick smallest value (most zoom in). 
		if (zh<zw) { 
			zoom = zh;
		} else { 
			zoom = zw;
		}
	}

	/**
	 * Center this image in a parent JScrollPane.
	 */
	public void center() { 
		JScrollPane jScrollPane = (JScrollPane) ((JViewport) this.getParent()).getParent();
		if (jScrollPane!=null) { 
			if (this.getSize().height<jScrollPane.getHeight() 
					&& this.getSize().width<jScrollPane.getWidth()) {
				// This image is smaller than the scroll pane.
				// Move the viewport to the origin.
				jScrollPane.getViewport().setViewPosition(new Point(0,0));
				// Place the image in the center of the scroll pane
				//TODO: place image in center.
			} else { 
				// This image is larger than the scroll pane.
				// Move the viewport to be centered on the center of the image.
				// Get the dimensions of the the viewport
				int view_x = jScrollPane.getViewport().getWidth();
				int view_y = jScrollPane.getViewport().getHeight();
				// Find the offset of the upper left corner of a centered viewport
				// of the same size on the image at its current zoom.
				// Half the width of the image minus half the width of the viewport.
				int ul_x = (this.getPreferredSize().width / 2) - (view_x/2);
				int ul_y = (this.getPreferredSize().height/ 2) - (view_y/2);
				// move the upper left corner of the viewport to this point
				jScrollPane.getViewport().setViewPosition(new Point(ul_x,ul_y));
			}
		}
	}
	
	public void centerOn(Point aPoint) { 
		JScrollPane jScrollPane = (JScrollPane) ((JViewport) this.getParent()).getParent();
		if (jScrollPane!=null) { 
			if (this.getSize().height<jScrollPane.getHeight() 
					&& this.getSize().width<jScrollPane.getWidth()) {
				// This image is smaller than the scroll pane.
				// Move the viewport to the origin.
				jScrollPane.getViewport().setViewPosition(new Point(0,0));
				// Place the image in the center of the scroll pane
				//TODO: place image in center.
			} else { 
				// This image is larger than the scroll pane.
				// Move the viewport to be centered on the point specified.
				// Get the dimensions of the the viewport
				int view_x = jScrollPane.getViewport().getWidth();
				int view_y = jScrollPane.getViewport().getHeight();
				Point location = jScrollPane.getViewport().getViewPosition();
				// determine the offset between the center of the viewport and aPoint
				int ul_x = aPoint.x - (view_x/2);
				int ul_y = aPoint.y - (view_y/2);
				// move the upper left corner of the viewport to this point
				jScrollPane.getViewport().setViewPosition(new Point(ul_x,ul_y));
			}
		}
	}	
	
	public double getZoom() { 
		return zoom;
	}

	public Image getImage() {
		return image;
	}
	
	/**
	 * Convenience method to get the size of the current image.  Returns 0,0 if image is null or
	 * if its size isn't known.
	 * 
	 * @return size of image as a Dimension.
	 */
	public Dimension getImageSize() {
		Dimension result = null;
		if (image==null) { 
			result = new Dimension(0,0);
		} else { 
			result = new Dimension(image.getWidth(this),image.getHeight(this));
			// convert get values that came back as unknown yet (-1) to 0,0.
			if (result.height==-1 || result.width ==-1) { 
				result = new Dimension(0,0);
			}
		}
		return result;
	}

}
