/**
 * VerbatimCaptureDialog.java
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
import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.data.ICImage;
import edu.harvard.mcz.imagecapture.data.Specimen;
import edu.harvard.mcz.imagecapture.data.WorkFlowStatus;
import edu.harvard.mcz.imagecapture.exceptions.ImageLoadException;
import edu.harvard.mcz.imagecapture.exceptions.SaveFailedException;
import edu.harvard.mcz.imagecapture.interfaces.DataChangeListener;
import edu.harvard.mcz.imagecapture.loader.Verbatim;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import java.awt.GridLayout;

/**
 * @author mole
 *
 */
public class VerbatimCaptureDialog extends JDialog implements DataChangeListener {
	
	private static final long serialVersionUID = 4462958599102371519L;

	private static final Log log = LogFactory.getLog(VerbatimCaptureDialog.class);
	
	private Specimen specimen = null;
	private SpecimenControler specimenControler = null;
	
	private final JPanel contentPanel = new JPanel();
	
	private JLabel lblBarcode;
	private JLabel lblCurrentid;
	
	private ImageZoomPanel imagePanelPinLabels = null;   // the specimen labels from the pin.
	
	private JTextArea textFieldVerbLocality;
	private JTextField textFieldVerbDate;
	private JTextField textFieldVerbCollector;
	private JTextField textFieldVerbCollection;
	private JTextField textFieldVerbNumbers;
	private JTextArea textFieldVerbUnclassifiedText;
	private JTextField textFieldQuestions;
	private JComboBox<String> comboBoxWorkflowStatus;

	private JButton btnPrevious;
	private JButton btnNext;
	
	/**
	 * Create the dialog.
	 */
	public VerbatimCaptureDialog() {
		init();
	} 
	
	/**
	 * Create the dialog for a specimen.
	 * 
	 * @param targetSpecimen
	 */
	public VerbatimCaptureDialog(Specimen targetSpecimen, SpecimenControler targetSpecimenControler) { 
		specimen = targetSpecimen;
		specimenControler = targetSpecimenControler;
		specimenControler.addListener(this);
		init();
		if (specimen!=null) { 
		   setValues();
		}
	}
	
	protected void setValues() { 
		lblBarcode.setText(specimen.getBarcode());
		lblCurrentid.setText(specimen.assembleScientificName());
		
		textFieldVerbLocality.setText(specimen.getVerbatimLocality());
		textFieldVerbDate.setText(specimen.getDateNos());
		textFieldVerbCollector.setText(specimen.getVerbatimCollector());
		textFieldVerbCollection.setText(specimen.getVerbatimCollection());
		textFieldVerbNumbers.setText(specimen.getVerbatimNumbers());
		textFieldVerbUnclassifiedText.setText(specimen.getVerbatimUnclassifiedText());
		
		comboBoxWorkflowStatus.setSelectedItem(specimen.getWorkFlowStatus());
	
		try { 
		
			Iterator<ICImage> i = specimen.getICImages().iterator();
			ICImage image = null;
			boolean gotImg = false;
			while (i.hasNext() && !gotImg) {
				image = i.next();
				gotImg = true;
			}
		
			String path = image.getPath();
			if (path == null) { path = ""; } 
			File anImageFile = new File(ImageCaptureProperties.assemblePathWithBase(path, image.getFilename()));
			
			PositionTemplate defaultTemplate = PositionTemplate.findTemplateForImage(image);
			BufferedImage imagefile = ImageIO.read(anImageFile);
			int x = defaultTemplate.getLabelPosition().width;    
			int y =  defaultTemplate.getLabelPosition().height; 
			int w = defaultTemplate.getLabelSize().width;  
			int h = defaultTemplate.getLabelSize().height;
			setPinLabelImage(imagefile.getSubimage(x, y, w, h));
			fitPinLabels();
		} catch (ImageLoadException e) {
			log.error(e.getMessage(), e);
			System.out.println(e.getMessage());	
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			System.out.println(e.getMessage());	
		}	
		
		if (specimenControler!=null) { 
			btnNext.setEnabled(specimenControler.hasNextSpecimenInTable());
		    btnPrevious.setEnabled(specimenControler.hasPreviousSpecimenInTable());
		} else {
			btnNext.setEnabled(false);
			btnPrevious.setEnabled(false);
		}
		
	}
	
	protected void init() { 
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Transcribe Verbatim Data");
		setMinimumSize(new Dimension(1020,640));
		setBounds(100, 100, 1020, 640);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			
			JLabel lblVerbatimDataFor = new JLabel("Verbatim Data for:");
			panel.add(lblVerbatimDataFor);
			lblBarcode = new JLabel("Barcode");
			panel.add(lblBarcode);
			lblCurrentid = new JLabel("CurrentID");
			panel.add(lblCurrentid);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.WEST);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] {0, 0, 0, 0};
			gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
			gbl_panel.columnWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
			panel.setLayout(gbl_panel);
			
			JLabel lblNewLabel = new JLabel("Locality");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.NORTHEAST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			panel.add(lblNewLabel, gbc_lblNewLabel);
			
			textFieldVerbLocality = new JTextArea();
			textFieldVerbLocality.setRows(3);
			GridBagConstraints gbc_textFieldVerbLocality = new GridBagConstraints();
			gbc_textFieldVerbLocality.gridheight = 2;
			gbc_textFieldVerbLocality.gridwidth = 2;
			gbc_textFieldVerbLocality.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbLocality.fill = GridBagConstraints.BOTH;
			gbc_textFieldVerbLocality.gridx = 1;
			gbc_textFieldVerbLocality.gridy = 0;
			panel.add(textFieldVerbLocality, gbc_textFieldVerbLocality);
			textFieldVerbLocality.setColumns(10);
			
			JLabel lblVerbatimDate = new JLabel("Date");
			GridBagConstraints gbc_lblVerbatimDate = new GridBagConstraints();
			gbc_lblVerbatimDate.anchor = GridBagConstraints.EAST;
			gbc_lblVerbatimDate.insets = new Insets(0, 0, 5, 5);
			gbc_lblVerbatimDate.gridx = 0;
			gbc_lblVerbatimDate.gridy = 2;
			panel.add(lblVerbatimDate, gbc_lblVerbatimDate);
			
			textFieldVerbDate = new JTextField();
			GridBagConstraints gbc_textFieldVerbDate = new GridBagConstraints();
			gbc_textFieldVerbDate.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbDate.gridwidth = 2;
			gbc_textFieldVerbDate.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldVerbDate.gridx = 1;
			gbc_textFieldVerbDate.gridy = 2;
			panel.add(textFieldVerbDate, gbc_textFieldVerbDate);
			textFieldVerbDate.setColumns(10);
			
			JLabel lblCollector = new JLabel("Collector");
			GridBagConstraints gbc_lblCollector = new GridBagConstraints();
			gbc_lblCollector.anchor = GridBagConstraints.EAST;
			gbc_lblCollector.insets = new Insets(0, 0, 5, 5);
			gbc_lblCollector.gridx = 0;
			gbc_lblCollector.gridy = 3;
			panel.add(lblCollector, gbc_lblCollector);
			
			textFieldVerbCollector = new JTextField();
			GridBagConstraints gbc_textFieldVerbCollector = new GridBagConstraints();
			gbc_textFieldVerbCollector.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbCollector.gridwidth = 2;
			gbc_textFieldVerbCollector.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldVerbCollector.gridx = 1;
			gbc_textFieldVerbCollector.gridy = 3;
			panel.add(textFieldVerbCollector, gbc_textFieldVerbCollector);
			textFieldVerbCollector.setColumns(10);
			
			JLabel lblNewLabel_1 = new JLabel("Collection");
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 0;
			gbc_lblNewLabel_1.gridy = 4;
			panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
			
			textFieldVerbCollection = new JTextField();
			GridBagConstraints gbc_textFieldVerbCollection = new GridBagConstraints();
			gbc_textFieldVerbCollection.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbCollection.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldVerbCollection.gridx = 2;
			gbc_textFieldVerbCollection.gridy = 4;
			panel.add(textFieldVerbCollection, gbc_textFieldVerbCollection);
			textFieldVerbCollection.setColumns(10);
			
			JLabel lblNumbers = new JLabel("Numbers");
			GridBagConstraints gbc_lblNumbers = new GridBagConstraints();
			gbc_lblNumbers.anchor = GridBagConstraints.EAST;
			gbc_lblNumbers.insets = new Insets(0, 0, 5, 5);
			gbc_lblNumbers.gridx = 0;
			gbc_lblNumbers.gridy = 5;
			panel.add(lblNumbers, gbc_lblNumbers);
			
			textFieldVerbNumbers = new JTextField();
			GridBagConstraints gbc_textFieldVerbNumbers = new GridBagConstraints();
			gbc_textFieldVerbNumbers.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbNumbers.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldVerbNumbers.gridx = 2;
			gbc_textFieldVerbNumbers.gridy = 5;
			panel.add(textFieldVerbNumbers, gbc_textFieldVerbNumbers);
			textFieldVerbNumbers.setColumns(10);
			
			JLabel lblNewLabel_2 = new JLabel("Other Text");
			GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
			gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_2.gridx = 0;
			gbc_lblNewLabel_2.gridy = 6;
			panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
			
			textFieldVerbUnclassifiedText = new JTextArea();
			textFieldVerbUnclassifiedText.setRows(3);
			GridBagConstraints gbc_textFieldVerbUnclassifiedText = new GridBagConstraints();
			gbc_textFieldVerbUnclassifiedText.gridheight = 2;
			gbc_textFieldVerbUnclassifiedText.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldVerbUnclassifiedText.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldVerbUnclassifiedText.gridx = 2;
			gbc_textFieldVerbUnclassifiedText.gridy = 6;
			panel.add(textFieldVerbUnclassifiedText, gbc_textFieldVerbUnclassifiedText);
			textFieldVerbUnclassifiedText.setColumns(10);
			
			JLabel lblQuestions = new JLabel("Questions");
			GridBagConstraints gbc_lblQuestions = new GridBagConstraints();
			gbc_lblQuestions.anchor = GridBagConstraints.EAST;
			gbc_lblQuestions.insets = new Insets(0, 0, 5, 5);
			gbc_lblQuestions.gridx = 0;
			gbc_lblQuestions.gridy = 8;
			panel.add(lblQuestions, gbc_lblQuestions);
			
			textFieldQuestions = new JTextField();
			GridBagConstraints gbc_textFieldQuestions = new GridBagConstraints();
			gbc_textFieldQuestions.insets = new Insets(0, 0, 5, 5);
			gbc_textFieldQuestions.fill = GridBagConstraints.HORIZONTAL;
			gbc_textFieldQuestions.gridx = 2;
			gbc_textFieldQuestions.gridy = 8;
			panel.add(textFieldQuestions, gbc_textFieldQuestions);
			textFieldQuestions.setColumns(30);
			
			JLabel lblWorkflowStatus = new JLabel("Workflow Status");
			GridBagConstraints gbc_lblWorkflowStatus = new GridBagConstraints();
			gbc_lblWorkflowStatus.anchor = GridBagConstraints.EAST;
			gbc_lblWorkflowStatus.insets = new Insets(0, 0, 5, 5);
			gbc_lblWorkflowStatus.gridx = 0;
			gbc_lblWorkflowStatus.gridy = 9;
			panel.add(lblWorkflowStatus, gbc_lblWorkflowStatus);
			
			comboBoxWorkflowStatus = new JComboBox<String>(WorkFlowStatus.getVerbatimWorkFlowStatusValues());
			GridBagConstraints gbc_comboBoxWorkflowStatus = new GridBagConstraints();
			gbc_comboBoxWorkflowStatus.insets = new Insets(0, 0, 5, 0);
			gbc_comboBoxWorkflowStatus.gridwidth = 2;
			gbc_comboBoxWorkflowStatus.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBoxWorkflowStatus.gridx = 2;
			gbc_comboBoxWorkflowStatus.gridy = 9;
			panel.add(comboBoxWorkflowStatus, gbc_comboBoxWorkflowStatus);
			
			JPanel panel_1 = new JPanel();
			GridBagConstraints gbc_panel_1 = new GridBagConstraints();
			gbc_panel_1.gridwidth = 2;
			gbc_panel_1.insets = new Insets(0, 0, 0, 5);
			gbc_panel_1.fill = GridBagConstraints.BOTH;
			gbc_panel_1.gridx = 2;
			gbc_panel_1.gridy = 11;
			panel.add(panel_1, gbc_panel_1);
			GridBagLayout gbl_panel_1 = new GridBagLayout();
			gbl_panel_1.columnWidths = new int[] {150, 143, 0};
			gbl_panel_1.rowHeights = new int[]{25, 0, 0, 0, 0};
			gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0};
			gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			panel_1.setLayout(gbl_panel_1);
			
			JButton btnPartiallyIllegible = new JButton("Partially Illegible");
			btnPartiallyIllegible.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					appendToQuestions(Verbatim.PARTLY_ILLEGIBLE);
					
				}
			});
			GridBagConstraints gbc_btnPartiallyIllegible = new GridBagConstraints();
			gbc_btnPartiallyIllegible.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnPartiallyIllegible.anchor = GridBagConstraints.NORTH;
			gbc_btnPartiallyIllegible.insets = new Insets(0, 0, 5, 5);
			gbc_btnPartiallyIllegible.gridx = 0;
			gbc_btnPartiallyIllegible.gridy = 0;
			panel_1.add(btnPartiallyIllegible, gbc_btnPartiallyIllegible);
			
			JButton btnNewButton = new JButton("No Locality Data");
			btnNewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textFieldVerbLocality.setText(Verbatim.NO_LOCALITY_DATA);
				}
			});
			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.anchor = GridBagConstraints.NORTHWEST;
			gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
			gbc_btnNewButton.gridx = 1;
			gbc_btnNewButton.gridy = 0;
			panel_1.add(btnNewButton, gbc_btnNewButton);
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			
			JButton btnNewButton_1 = new JButton("Entirely Illegible");
			btnNewButton_1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					appendToQuestions(Verbatim.ENTIRELY_ILLEGIBLE);
				}
			});
			GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
			gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnNewButton_1.anchor = GridBagConstraints.NORTH;
			gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
			gbc_btnNewButton_1.gridx = 0;
			gbc_btnNewButton_1.gridy = 1;
			panel_1.add(btnNewButton_1, gbc_btnNewButton_1);
			
			JButton btnNoDateData = new JButton("No Date Data");
			btnNoDateData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textFieldVerbDate.setText("[No date data]");
				}});
			GridBagConstraints gbc_btnNoDateData = new GridBagConstraints();
			gbc_btnNoDateData.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnNoDateData.insets = new Insets(0, 0, 5, 5);
			gbc_btnNoDateData.gridx = 1;
			gbc_btnNoDateData.gridy = 1;
			panel_1.add(btnNoDateData, gbc_btnNoDateData);
			
			JButton btnLabelTruncatedIn = new JButton("Label Truncated in Image");
			btnLabelTruncatedIn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					appendToQuestions(Verbatim.TRUNCATED_BY_IMAGE);
				}
			});
			GridBagConstraints gbc_btnLabelTruncatedIn = new GridBagConstraints();
			gbc_btnLabelTruncatedIn.insets = new Insets(0, 0, 5, 5);
			gbc_btnLabelTruncatedIn.anchor = GridBagConstraints.NORTHWEST;
			gbc_btnLabelTruncatedIn.gridx = 0;
			gbc_btnLabelTruncatedIn.gridy = 2;
			panel_1.add(btnLabelTruncatedIn, gbc_btnLabelTruncatedIn);
			
			JButton btnNoCollectorData = new JButton("No Collector Data");
			btnNoCollectorData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					textFieldVerbCollector.setText("[No collector data]");
				}
			});
			GridBagConstraints gbc_btnNoCollectorData = new GridBagConstraints();
			gbc_btnNoCollectorData.insets = new Insets(0, 0, 5, 5);
			gbc_btnNoCollectorData.gridx = 1;
			gbc_btnNoCollectorData.gridy = 2;
			panel_1.add(btnNoCollectorData, gbc_btnNoCollectorData);
			
			JButton btnNoPinLabels = new JButton("No Pin Labels");
			btnNoPinLabels.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					appendToQuestions(Verbatim.NO_PIN_LABELS);
				}
			});
			GridBagConstraints gbc_btnNoPinLabels = new GridBagConstraints();
			gbc_btnNoPinLabels.insets = new Insets(0, 0, 0, 5);
			gbc_btnNoPinLabels.gridx = 0;
			gbc_btnNoPinLabels.gridy = 3;
			panel_1.add(btnNoPinLabels, gbc_btnNoPinLabels);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			
			panel.add(getImagePanePinLabels());
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			btnPrevious = new JButton("Previous");
			btnPrevious.setEnabled(false);
			if (specimenControler!=null && specimenControler.isInTable()) {
				btnPrevious.setEnabled(true);
			}
			btnPrevious.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					save();
					if (specimenControler.previousSpecimenInTable()) { 
					   specimen = specimenControler.getSpecimen();
					   setValues();
					}
				} });
			buttonPane.add(btnPrevious);
			
			btnNext = new JButton("Next");
			btnNext.setEnabled(false);
			if (specimenControler!=null && specimenControler.isInTable()) { 
				btnNext.setEnabled(true);
			}
			btnNext.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					save();
					if (specimenControler.nextSpecimenInTable()) { 
					   specimen = specimenControler.getSpecimen();
					   setValues();
					}
				} });
			buttonPane.add(btnNext);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (save()) { 
							setVisible(false);
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
	}
	
	public void fitPinLabels() { 
		imagePanelPinLabels.zoomToFit();
	}	
	
	private ImageZoomPanel getImagePanePinLabels() {
		if (imagePanelPinLabels == null) {
			imagePanelPinLabels = new ImageZoomPanel();
		}
		return imagePanelPinLabels;
	}		
	
	public void setPinLabelImage(Image anImage) { 
		imagePanelPinLabels.setImage((BufferedImage) anImage);
		imagePanelPinLabels.zoomToFit();
		this.pack();
		if (imagePanelPinLabels.getPreferredSize().height>500 || imagePanelPinLabels.getPreferredSize().width>500) { 
			imagePanelPinLabels.setPreferredSize(new Dimension(500,500));
		} 
		imagePanelPinLabels.setMaximumSize(new Dimension(500,500));
	}	
	
	protected boolean save() { 
		boolean result = false;
		
		try {
			specimen.setVerbatimLocality(textFieldVerbLocality.getText());
			specimen.setDateNos(textFieldVerbDate.getText());
			specimen.setVerbatimCollector(textFieldVerbCollector.getText());
			specimen.setVerbatimCollection(textFieldVerbCollection.getText());
			specimen.setVerbatimNumbers(textFieldVerbNumbers.getText());
			specimen.setVerbatimUnclassifiedText(textFieldVerbUnclassifiedText.getText());
			StringBuffer questions = new StringBuffer();
			questions.append(specimen.getQuestions());
			if (textFieldQuestions.getText()!=null && textFieldQuestions.getText().trim().length()>0) { 
				if (!questions.toString().contains(textFieldQuestions.getText())) { 
					questions.append(Verbatim.SEPARATOR).append(textFieldQuestions.getText());
				}
			}
			String workflowstatus = (String) comboBoxWorkflowStatus.getSelectedItem();
			specimen.setWorkFlowStatus(workflowstatus);
			specimen.setQuestions(questions.toString());
			specimenControler.save();
			result = true;
		} catch (SaveFailedException e) {
			log.error(e.getMessage(), e);
			// TODO: Notify user
		}
		
		
		return result;
	}

	protected void appendToQuestions(String newQuestion) { 
		if (!textFieldQuestions.getText().contains(newQuestion)) { 
			StringBuffer questions = new StringBuffer();
			questions.append(textFieldQuestions.getText()).append(Verbatim.SEPARATOR).append(newQuestion);
			textFieldQuestions.setText(questions.toString());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.mcz.imagecapture.interfaces.DataChangeListener#notifyDataHasChanged()
	 */
	@Override
	public void notifyDataHasChanged() {
		// TODO Auto-generated method stub
		
	}
}
