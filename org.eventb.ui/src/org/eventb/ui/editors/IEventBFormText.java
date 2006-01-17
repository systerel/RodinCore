/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.ui.editors;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;

/**
 * @author htson
 * This is the interface for Event-B FormText
 */
public interface IEventBFormText
	extends IPropertyChangeListener
{
	
	/**
	 *  This call back is used when the font is change in the Preferences.
	 */
	public void propertyChange(PropertyChangeEvent event);


	/**
	 * Setting the content of the Form Text.
	 * @param str Any string
	 */
	public void setText(String str);


	/**
	 * Adding a hyperlink listener to the Form Text.
	 * @param listener Any HyperlinkAdapter
	 */
	public void addHyperlinkListener(HyperlinkAdapter listener);

	
	/**
	 * Removing a hyperlink listener from the FormText.
	 * @param listener Any HyperlinkAdapter
	 */
	public void removeHyperlinkListener(HyperlinkAdapter listener);

}
