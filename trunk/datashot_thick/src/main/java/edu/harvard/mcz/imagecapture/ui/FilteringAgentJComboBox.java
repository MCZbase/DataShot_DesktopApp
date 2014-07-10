/**
 * FilteringGeogJComboBox
 * edu.harvard.mcz.imagecapture.ui
 * 
 * Modified from: 
 * FilteringJComboBox.java
 * edu.harvard.mcz.precapture.ui
 * Copyright © 2012 President and Fellows of Harvard College
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
 * Author: mole
 */
package edu.harvard.mcz.imagecapture.ui;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.harvard.mcz.imagecapture.ImageCaptureProperties;
import edu.harvard.mcz.imagecapture.Singleton;
import edu.harvard.mcz.imagecapture.data.AgentNameComboBoxModel;
import edu.harvard.mcz.imagecapture.data.HigherGeographyComboBoxModel;
import edu.harvard.mcz.imagecapture.data.MCZbaseAuthAgentName;
import edu.harvard.mcz.imagecapture.data.MCZbaseAuthAgentNameLifeCycle;
import edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRec;
import edu.harvard.mcz.imagecapture.data.MCZbaseGeogAuthRecLifeCycle;

/**
 * @author mole
 *
 */
public class FilteringAgentJComboBox extends JComboBox<MCZbaseAuthAgentName> implements FocusListener {
	private static final long serialVersionUID = -7988464282872345110L;
	private static final Log log = LogFactory.getLog(FilteringAgentJComboBox.class);
	
	private AgentNameComboBoxModel cachedModel = null;
    private int lastTextLength;
	
	/** 
	 * Default no argument constructor, constructs a new FilteringJComboBox instance.
	 */
	public FilteringAgentJComboBox() {
        super.setModel(new AgentNameComboBoxModel());
        init();
	}
	
	public void setHGModel(AgentNameComboBoxModel model) { 
		super.setModel(model);
	}

	/**
	 * 
	 * 
	 * @param valueList
	 */
    public FilteringAgentJComboBox(AgentNameComboBoxModel model) {
        super(model);
        init();
    } 
    
    private void init() { 
    	// listen for loss of focus on the text field
    	this.getEditor().getEditorComponent().addFocusListener(this);
        this.setEditable(true);
        final JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent keyEvent) {
            	log.debug(keyEvent);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        filter(textfield.getText(), true);
                    }
                });
            }
        });

    }

    public void setAgentNameModel(AgentNameComboBoxModel model) { 
    	super.setModel(model);
    }
    
    public void resetFilter(boolean changePopupState) { 
    	filter(null, changePopupState);
    }
    
    protected void filter(String enteredText, boolean changePopupState) {
    	log.debug(enteredText);
    	log.debug("changePopupState:" + changePopupState);
    	if (enteredText == null || enteredText.length() == 0 || enteredText.length() < lastTextLength) {
    		// If entry is blank, show full list.
    		if (cachedModel==null || (enteredText!=null && enteredText.length() < lastTextLength)) { 
    			log.debug("Querying for new list");
    		    MCZbaseAuthAgentNameLifeCycle uls = new MCZbaseAuthAgentNameLifeCycle();
    		    cachedModel = new AgentNameComboBoxModel(uls.findAll());
    		} else { 
    			log.debug("Reusing old agent list lenght = " + cachedModel.getSize());
    		}
    		super.setModel(cachedModel);
    	}
    	if (!changePopupState) { 
    		this.firePopupMenuCanceled();
    	}
    	if (changePopupState && !this.isPopupVisible()) {
    		this.showPopup();
    	}

    	int lengthThreshold = Integer.valueOf(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_FILTER_LENGTH_THRESHOLD));
    	if (enteredText != null && enteredText.length() >= lengthThreshold) {

    		log.debug("Filtering on " + enteredText);

    		boolean isExactMatch = false;
    		AgentNameComboBoxModel filterArray = new AgentNameComboBoxModel();
    		filterArray.removeAllElements();
    		log.debug("Model size: " + super.getModel().getSize());
    		for (int i = 0; i < super.getModel().getSize(); i++) {
    			if (((AgentNameComboBoxModel) super.getModel())
    					.getDataElementAt(i).toString().toLowerCase()
    					.contains(enteredText.toLowerCase())) {
    				filterArray.addElement((MCZbaseAuthAgentName)((AgentNameComboBoxModel) super
    						.getModel()).getDataElementAt(i));
    			}
    			if (((AgentNameComboBoxModel) super.getModel())
    					.getDataElementAt(i).toString()
    					.equalsIgnoreCase(enteredText)) {
    				isExactMatch = true;
    				super.getModel().setSelectedItem(
    						((AgentNameComboBoxModel) super.getModel())
    						.getDataElementAt(i));
    			}
    		}
    		if (filterArray.getSize() > 0) {
    			AgentNameComboBoxModel model = (AgentNameComboBoxModel) this
    					.getModel();
    			model.removeAllElements();
    			Iterator<MCZbaseAuthAgentName> i = filterArray.getModel().iterator();
    			while (i.hasNext()) {
    				model.addElement(i.next());
    			}
    			JTextField textfield = (JTextField) this.getEditor()
    					.getEditorComponent();
    			textfield.setText(enteredText);
    			super.setModel(model);
    		}
    		log.debug("Filtered Model size: " + super.getModel().getSize());
    		if (changePopupState) { 
    			this.hidePopup();
    			if (isExactMatch) {
    				super.firePopupMenuCanceled();
    			} else {
    				this.showPopup();
    			}
    		} 
    	}
    	if (enteredText!=null) { 
           lastTextLength = enteredText.length();
    	} else { 
    	   lastTextLength = 0;
    	}
    }

	public void focusGained(FocusEvent e) {
		super.getModel().setSelectedItem("");
		JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.setText("");
	}

	public void focusLost(FocusEvent e) {
		// when focus is lost on the text field (editor box part of the combo box),
		// set the value of the text field to the selected item on the list, if any.
		log.debug(e.toString());
		if (super.getModel().getSelectedItem()!=null) { 
		    log.debug(super.getModel().getSelectedItem().toString());
		    //JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
            //textfield.setText(super.getModel().getSelectedItem().toString());
		    this.getEditor().setItem(super.getModel().getSelectedItem().toString());
		}
	}
    
}