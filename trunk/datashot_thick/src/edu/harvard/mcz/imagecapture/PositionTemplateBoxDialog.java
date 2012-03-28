/**
 * PositionTemplateBoxDialog.java
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

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;

import edu.harvard.mcz.imagecapture.exceptions.BadTemplateException;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/** PositionTemplateBoxDialog
 * 
 * @author Paul J. Morris
 *
 */
public class PositionTemplateBoxDialog extends JDialog {

	private static final long serialVersionUID = 6798207249250029852L;
	
	private static final int RESULT_SAVE = 0;
	private static final int RESULT_CANCEL = 1;
	
	private JPanel jContentPane = null;
	// maximum bounds in units of pixels
	private int maxX = 0;  // image width
	private int maxY = 0;  // image height
	// 0,0  (upper left corner of image)
	private static final int minX = 0;  
	private static final int minY = 0;
	
	private Dimension ul = null;   // upper left corner of box  //  @jve:decl-index=0:
	private Dimension size = null; // height,width dimensions of box  //  @jve:decl-index=0:
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFieldDescription = null;
	private JLabel jLabel1 = null;
	private JTextField jTextFieldULX = null;
	private JLabel jLabel2 = null;
	private JTextField jTextFieldULY = null;
	private JLabel jLabel3 = null;
	private JLabel jLabel4 = null;
	private JTextField jTextFieldSizeWidth = null;
	private JTextField jTextFieldSizeHeight = null;
	private JPanel jPanel1 = null;
	private JButton jButton = null;
	private JButton jButtonSave = null;
	private JLabel jLabel6 = null;
	private JLabel jLabelImageHeight = null;
	private JLabel jLabelImageWidth1 = null;
	private JLabel jLabelImageHeight1 = null;
	private JLabel jLabelImageWidth = null;
	private PositionTemplateBoxDialog thisDialog = null;
	private int resultCode = RESULT_CANCEL;

	private JLabel jLabelFeedback = null;
	
	/** Don't call this constructor, use PositionTemplateBoxDialog(Frame owner, Dimension ImageSize, Dimension aULToChange, Dimension aSizeToChange,  String description) 
	 * and provide values to edit instead.  Protected so that it can be overridden in a class that extends this dialog (presumbably to create a new box with some default values).   
	 * 
	 * @param owner
	 */
	protected PositionTemplateBoxDialog(Frame owner) { 
		super(owner);
		thisDialog = this;
	}
	
    /** Dialog to obtain new values for the upper left corner and size of a rectangular box drawn on an image 
     * identified by the upper left corner of the box and the height and width of the box in all in units of 
     * pixels.  
     * 
     * @param owner the parent frame of this dialog.
     * @param imageSize the height and width of the image onto which this box is placed
     * @param aULToChange the upper left corner of the box, in units of pixels of the image
     * @param aSizeToChange the height and width of the box, in units of pixels of the image 
     * @param description a text description of the box to display on the dialog.
     * @throws BadTemplateException if a dimension parameter has a height or width less than or equal to zero (aULtoChange can be 0).
     */
	public PositionTemplateBoxDialog(Frame owner, Dimension imageSize, Dimension aULToChange, Dimension aSizeToChange,  String description) throws BadTemplateException {
		super(owner,true);  // create as modal over parent frame.
		thisDialog = this;
		// store values (and throw exception if they are out of range)
		setImageSize(imageSize);
		setUL(aULToChange);
		setSize(aSizeToChange);
		// set up the form
		initialize();
		// display values on form
		jLabelImageWidth.setText(Integer.toString(maxX));
		jLabelImageHeight.setText(Integer.toString(maxY));
		jLabelImageWidth1.setText(Integer.toString(maxX));
		jLabelImageHeight1.setText(Integer.toString(maxY));
		
		jTextFieldULX.setText(Integer.toString(getUL().width));
		jTextFieldULY.setText(Integer.toString(getUL().height));
		jTextFieldSizeWidth.setText(Integer.toString(getSize().width));
		jTextFieldSizeHeight.setText(Integer.toString(getSize().height));
		
		jTextFieldDescription.setText(description);
		jLabelFeedback.setText("");
		
		this.pack();
		Dimension screenSize = owner.getSize();
	    this.setLocation((screenSize.width -   this.getWidth()) / 2 , 
                (screenSize.height - this.getHeight()) / 2 );	
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(342, 200);
		this.setTitle("Edit a template component");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/** Get the upper left coordinate of the box in pixels on the
	 * original image.
	 * 
	 * @return ul the upper left coordinate of the box.
	 */
	public Dimension getUL() {
		return ul;
	}

	/**
	 * @param ul the upper left coordinate of the box to set
	 * @throws BadTemplateException if ul has a height or width less than zero (zero is ok).
	 */
	public void setUL(Dimension ul) throws BadTemplateException {
		if (ul.width < 0 || ul.height <0) { 
			throw new BadTemplateException("Upper left coordinate can't be less than 0.");
		}
		this.ul = ul;
	}

	/**
	 * Get the height and width of the box in pixels.
	 * 
	 * @return the size of the box
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 * @throws BadTemplateException if the size has a height or width less than or equal to 0.
	 */
	public void setSize(Dimension size) {
		if (size.width <= 0 || size.height <=0) { 
			try {
				throw new BadTemplateException("Box size can't be 0 or less.");
			} catch (BadTemplateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.size = size;
	}

	/**
	 * @param imageSize the imageSize to set
	 * @throws BadTemplateException 
	 */
	public void setImageSize(Dimension imageSize) throws BadTemplateException {
		if (imageSize.width <= 0 || imageSize.height <=0) { 
			throw new BadTemplateException("Image height and width can't be 0 or less.");
		}
		this.maxX = imageSize.width;
		this.maxY = imageSize.height;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.gridx = 1;
			gridBagConstraints71.gridy = 7;
			jLabelFeedback = new JLabel();
			jLabelFeedback.setText("JLabel");
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 2;
			gridBagConstraints61.gridy = 2;
			jLabelImageWidth = new JLabel();
			jLabelImageWidth.setText("JLabel");
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.gridx = 2;
			gridBagConstraints51.gridy = 5;
			jLabelImageHeight1 = new JLabel();
			jLabelImageHeight1.setText("JLabel");
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.gridx = 2;
			gridBagConstraints41.gridy = 4;
			jLabelImageWidth1 = new JLabel();
			jLabelImageWidth1.setText("JLabel");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 2;
			gridBagConstraints31.gridy = 3;
			jLabelImageHeight = new JLabel();
			jLabelImageHeight.setText("JLabel");
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.gridy = 1;
			jLabel6 = new JLabel();
			jLabel6.setText("Image (max)");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 6;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.fill = GridBagConstraints.BOTH;
			gridBagConstraints9.gridy = 5;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.gridx = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.gridy = 4;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.anchor = GridBagConstraints.EAST;
			gridBagConstraints7.gridy = 5;
			jLabel4 = new JLabel();
			jLabel4.setText("Height");
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.EAST;
			gridBagConstraints6.gridy = 4;
			jLabel3 = new JLabel();
			jLabel3.setText("Width");
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 3;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.anchor = GridBagConstraints.EAST;
			gridBagConstraints4.gridy = 3;
			jLabel2 = new JLabel();
			jLabel2.setText("Upper Left Y");
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.gridy = 2;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints2.gridy = 2;
			jLabel1 = new JLabel();
			jLabel1.setText("Upper Left X");
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.ipadx = 0;
			gridBagConstraints1.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.gridy = 0;
			jLabel = new JLabel();
			jLabel.setText("Edit a template component.");
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(jLabel, gridBagConstraints);
			jPanel.add(getJTextFieldDescription(), gridBagConstraints1);
			jPanel.add(jLabel1, gridBagConstraints2);
			jPanel.add(getJTextFieldULX(), gridBagConstraints3);
			jPanel.add(jLabel2, gridBagConstraints4);
			jPanel.add(getJTextFieldULY(), gridBagConstraints5);
			jPanel.add(jLabel3, gridBagConstraints6);
			jPanel.add(jLabel4, gridBagConstraints7);
			jPanel.add(getJTextFieldSizeWidth(), gridBagConstraints8);
			jPanel.add(getJTextFieldSizeHeight(), gridBagConstraints9);
			jPanel.add(getJPanel1(), gridBagConstraints10);
			jPanel.add(jLabel6, gridBagConstraints21);
			jPanel.add(jLabelImageHeight, gridBagConstraints31);
			jPanel.add(jLabelImageWidth1, gridBagConstraints41);
			jPanel.add(jLabelImageHeight1, gridBagConstraints51);
			jPanel.add(jLabelImageWidth, gridBagConstraints61);
			jPanel.add(jLabelFeedback, gridBagConstraints71);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFieldDescription	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldDescription() {
		if (jTextFieldDescription == null) {
			jTextFieldDescription = new JTextField();
			jTextFieldDescription.setEditable(false);
			jTextFieldDescription.setText("");
		}
		return jTextFieldDescription;
	}

	/**
	 * This method initializes jTextFieldULX	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldULX() {
		if (jTextFieldULX == null) {
			jTextFieldULX = new JTextField();
		}
		return jTextFieldULX;
	}

	/**
	 * This method initializes jTextFieldULY	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldULY() {
		if (jTextFieldULY == null) {
			jTextFieldULY = new JTextField();
		}
		return jTextFieldULY;
	}

	/**
	 * This method initializes jTextFieldSizeWidth	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldSizeWidth() {
		if (jTextFieldSizeWidth == null) {
			jTextFieldSizeWidth = new JTextField();
		}
		return jTextFieldSizeWidth;
	}

	/**
	 * This method initializes jTextFieldSizeHeight	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFieldSizeHeight() {
		if (jTextFieldSizeHeight == null) {
			jTextFieldSizeHeight = new JTextField();
		}
		return jTextFieldSizeHeight;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 1;
			gridBagConstraints11.gridy = 0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJButton(), new GridBagConstraints());
			jPanel1.add(getJButtonSave(), gridBagConstraints11);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Cancel");
			jButton.setMnemonic(KeyEvent.VK_C);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thisDialog.setVisible(false);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButtonSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setText("Change");
			jButtonSave.setMnemonic(KeyEvent.VK_H);
			jButtonSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						setUL(new Dimension(Integer.valueOf(jTextFieldULX.getText()), Integer.valueOf(jTextFieldULY.getText())));
						setSize(new Dimension(Integer.valueOf(jTextFieldSizeWidth.getText()), Integer.valueOf(jTextFieldSizeHeight.getText())));
						if (validateValues()) { 
							resultCode = RESULT_SAVE;
						} 
					} catch (NumberFormatException e1) {
						// failed...
					} catch (BadTemplateException e1) {
						// failed...
					}
					if (resultCode!=RESULT_SAVE) { 
						jLabelFeedback.setText("Can't save those values.");
					} else { 
						jLabelFeedback.setText("");
						thisDialog.setVisible(false);
					}
				}
			});
		}
		return jButtonSave;
	}
	
	
	/**
	 * validate form fields
	 * 
	 * @return true if valid, false otherwise.
	 */
	private boolean validateValues() { 
		boolean result = true;
		// Check that UL coordinate is on image.
		if (ul.height<minY || ul.height>maxY ) { result = false; } 
		if (ul.width <minX || ul.width >maxX ) { result = false; }
		// check that size is smaller that image
		if (size.height<=minY || size.height>maxY ) { result = false; } 
		if (size.width <=minX || size.width >maxX ) { result = false; }
		// check that box fits in image
		if (ul.height + size.height > maxY ) { result = false; } 
		if (ul.width + size.width   > maxX ) { result = false; }		
		return result;
	}

	/**
	 * Result state of the dialog.  
	 * 
	 * @return RESULT_SAVE if user closed by pressing save, RESULT_CANCEL otherwise.
	 */
	public int getResult() { 
		return resultCode;
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
