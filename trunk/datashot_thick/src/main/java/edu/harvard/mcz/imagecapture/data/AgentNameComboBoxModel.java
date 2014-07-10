/**
 * HigherGeographyComboBoxModel.java
 * edu.harvard.mcz.imagecapture.data
 * Copyright © 2013 President and Fellows of Harvard College
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
package edu.harvard.mcz.imagecapture.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class AgentNameComboBoxModel implements ComboBoxModel<MCZbaseAuthAgentName> {
	private static final long serialVersionUID = -4856432985354519476L;

	private static final Log log = LogFactory.getLog(AgentNameComboBoxModel.class);

	ArrayList<MCZbaseAuthAgentName> model;
	ArrayList<ListDataListener> dataListeners;
	MCZbaseAuthAgentName selectedItem = null;	
	
	public AgentNameComboBoxModel() {
        model = new ArrayList<MCZbaseAuthAgentName>();
        // add a blank row
        model.add(new MCZbaseAuthAgentName());
        dataListeners = new ArrayList<ListDataListener>();		
	}
	
	/**
	 * @param model
	 */
	public AgentNameComboBoxModel(List<MCZbaseAuthAgentName> model) {
		super();
		if (model.isEmpty()) { 
			model = new ArrayList<MCZbaseAuthAgentName>();
		}
		this.model = (ArrayList<MCZbaseAuthAgentName>) model;
		if (model==null) { 
            model = new ArrayList<MCZbaseAuthAgentName>();
            // add a blank row.
            model.add(0,new MCZbaseAuthAgentName());
		}
        dataListeners = new ArrayList<ListDataListener>();		
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return model.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public MCZbaseAuthAgentName getElementAt(int index) {
		return this.model.get(index);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void addListDataListener(ListDataListener l) {
		this.dataListeners.add(l);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	@Override
	public void removeListDataListener(ListDataListener l) {
		this.dataListeners.remove(l);
	}

	private void notifyListeners() { 
		Iterator<ListDataListener> i = dataListeners.iterator();
		while (i.hasNext()) { 
			i.next().contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, model.size()));
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	@Override
	public void setSelectedItem(Object anItem) {
		log.debug("Trying to set selected item: " + anItem);
		if (anItem!=null){ 
			Iterator<MCZbaseAuthAgentName> i = model.iterator();
			boolean done = false;
			while (i.hasNext() && !done) {
				MCZbaseAuthAgentName higher = i.next();
				try { 
					if (
							higher.getAgent_name()!= null 
							&& 
							higher.getAgent_name().equals( ((MCZbaseAuthAgentName)anItem).getAgent_name() )
							) 
					{
						log.debug("Selected agent is: " + higher.getAgent_name());
						selectedItem = higher;
						done = true;
						notifyListeners();
					}
				} catch (ClassCastException e) { 
					// Selected item is a string, we need to match to the corresponding object's agent name value.
					if (
							higher.getAgent_name()!= null 
							&& 
							higher.getAgent_name().equals(anItem)
							) 
					{
						log.debug("Selected agent is: " + higher.getAgent_name());
						selectedItem = higher;
						done = true;	
						notifyListeners();
					}
				}
			}		
			if (done==false) { 
				// entry isn't on list
				try {
				    model.add( (MCZbaseAuthAgentName)anItem );
				    selectedItem = (MCZbaseAuthAgentName)anItem;
				} catch (ClassCastException e) {
					// provided object was a string
					MCZbaseAuthAgentName name = new MCZbaseAuthAgentName();
					name.setAgent_name(anItem.toString());
					model.add(name);
					selectedItem = name;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	@Override
	public Object getSelectedItem() {
		if (this.selectedItem==null) {
			return null;
		}
		//return this.selectedItem.getHigher_geog();
		return this.selectedItem;
	}

	/**
	 * Obtain the agent name string for the currently selected agent name 
	 * authority record object.
	 * 
	 * @return a string representation of the agent name from the 
	 * currently selected item, or null if there is no currently selected item. 
	 */
	public String getSelectedItemAgentName() { 
		if (this.selectedItem==null) { 
			return null;
		}
		return this.selectedItem.getAgent_name();
	}
	
	/**
	 * 
	 */
	public void removeAllElements() {
		this.model.clear();
	}

	/**
	 * @param i
	 * @return
	 */
	public Object getDataElementAt(int i) {
		return model.get(i);
	}

	/**
	 * @param dataElementAt
	 */
	public void addElement(MCZbaseAuthAgentName dataElementAt) {
	   model.add(dataElementAt);
		
	}

	/**
	 * @return
	 */
	public List<MCZbaseAuthAgentName> getModel() {
		return this.model;
	}

}