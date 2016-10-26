/**
 * TemplatePickerDialog.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2016 President and Fellows of Harvard College
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.ICImageLifeCycle;
import edu.harvard.mcz.imagecapture.exceptions.NoSuchTemplateException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.exceptions.UnreadableFileException;

import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;

/**
 * @author mole
 *
 */
public class TemplatePickerDialog extends JDialog {
	private static final Log log = LogFactory.getLog(TemplatePickerDialog.class);
	
	private final JPanel contentPanel = new JPanel();
	private JLabel lblTemplate = null;
	private JComboBox comboBoxTemplatePicker = null;
	private JLabel lblFileName = null;
	private ICImage imageToTemplate = null;
	private JLabel labelBarcodeImage = null;

	public TemplatePickerDialog() {
		init(null);
	} 	
	
	/**
	 * Create the dialog.
	 */
	public TemplatePickerDialog(ICImage image) {
		init(image);
	} 
	
	protected void init(ICImage image) { 
		setBounds(100, 100, 450, 300);
		StringBuffer title = new StringBuffer();
		title.append("Change Template");
		if (image!=null) { 
			title.append(" for ").append(image.getFilename());
		}
		this.setTitle(title.toString());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			lblTemplate = new JLabel("Template");
			lblTemplate.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblTemplate, BorderLayout.NORTH);
		}
		{
			comboBoxTemplatePicker = new JComboBox<String>();
			comboBoxTemplatePicker.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					PositionTemplate defaultTemplate;
					try {
						defaultTemplate = new PositionTemplate((String)comboBoxTemplatePicker.getSelectedItem());


						File fileToCheck = new File(ImageCaptureProperties.assemblePathWithBase(imageToTemplate.getPath(), imageToTemplate.getFilename()));

						BufferedImage imagefile = ImageIO.read(fileToCheck);

						int x = defaultTemplate.getBarcodeULPosition().width;    
						int y =  defaultTemplate.getBarcodeULPosition().height; 
						int w = defaultTemplate.getBarcodeSize().width;  
						int h = defaultTemplate.getBarcodeSize().height;
						setBarcodeImage(imagefile.getSubimage(x, y, w, h));

					} catch (NullPointerException e1) {
						log.error(e1.getMessage());
					} catch (NoSuchTemplateException e1) {
						log.error(e1.getMessage());
					} catch (IOException e1) {
						log.error(e1.getMessage());
					}
				}


				
			} );
			contentPanel.add(comboBoxTemplatePicker, BorderLayout.SOUTH);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				lblFileName = new JLabel("FileName");
				panel.add(lblFileName, BorderLayout.NORTH);
			}
			{
				labelBarcodeImage = new JLabel("Catalog Number Barcode");
				labelBarcodeImage.setIcon(new ImageIcon(TemplatePickerDialog.class.getResource("/edu/harvard/mcz/imagecapture/resources/gnome-mime-image.png")));
				panel.add(labelBarcodeImage, BorderLayout.CENTER);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						ICImageLifeCycle ils = new ICImageLifeCycle();
						try {
							String newTemplateID = (String)comboBoxTemplatePicker.getSelectedItem();
							if (newTemplateID!=null) { 
							   imageToTemplate.setTemplateId(newTemplateID);
							   ils.attachDirty(imageToTemplate);
							   setVisible(false);
							}
						} catch (SaveFailedException e1) {
							log.error(e1.getMessage(),e1);
						}
					} 
					
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
					
				});
				buttonPane.add(cancelButton);
			}
		}
		if (image!=null) { 
            try {
				boolean result = setupForImage(image);
			} catch (UnreadableFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected boolean setupForImage(ICImage image) throws UnreadableFileException { 
		boolean result = false;
		imageToTemplate = image;
		
		StringBuffer heading = new StringBuffer();
		heading.append("Current Template:");
		heading.append(" ").append(image.getTemplateId());
		
		StringBuffer filename = new StringBuffer();
		filename.append(image.getPath()).append(File.pathSeparator).append(image.getFilename());
		this.lblFileName.setText(filename.toString());
		lblTemplate.setText(heading.toString());
		comboBoxTemplatePicker.addItem(image.getTemplateId());
		File imageFile = new File(ImageCaptureProperties.assemblePathWithBase(image.getPath(), image.getFilename()));
		
		BufferedImage bufImage = null;
		int imageWidth = 0;
		try { 
		    bufImage = ImageIO.read(imageFile);
		    imageWidth = bufImage.getWidth();
		} catch (IOException e) {
			throw new UnreadableFileException("IOException trying to read " + imageFile.getName());
		}
		
		if (imageFile.exists()) { 
			List<PositionTemplate> templates = PositionTemplate.getTemplates();
			ListIterator<PositionTemplate> i = templates.listIterator();
			while (i.hasNext()) {
				PositionTemplate template = i.next();
				if (!template.getTemplateId().equals(PositionTemplate.TEMPLATE_NO_COMPONENT_PARTS))  { 
					int templateWidth = -1;
					try { 
						templateWidth = (int) template.getImageSize().getWidth();
					} catch (NullPointerException e) {
						log.debug(e.getMessage());
					} 
					if (imageWidth==templateWidth) { 
						comboBoxTemplatePicker.addItem(template.getTemplateId());
					}
				}
			}
		}
		
		return result;
	}
	
	protected void setBarcodeImage(BufferedImage subimage) {
			labelBarcodeImage.setIcon(new ImageIcon(subimage));
	} 
}
