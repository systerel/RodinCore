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
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;

/**
 * @author htson
 * This is the interface for Event-B Input Text
 */
public interface IEventBInputText
	extends IPropertyChangeListener
{
	
	/**
	 *  This call back is used when the font is change in the Preferences.
	 */
	public void propertyChange(PropertyChangeEvent event);
	
	
	/**
	 * @return the content of the Text inside.
	 */
	public String getText();
	
	
	/**
	 * Setting the layout of the Text.
	 * @param gd Any grid data
	 */
	public void setLayoutData(GridData gd);
	
	
	/**
	 * Setting the focus to the contained Text.
	 *
	 */
	public void setFocus();
	
	
	/**
	 * Setting the content of the Text.
	 * @param str Any string
	 */
	public void setText(String string);
	
	
	/**
	 * Adding a listener for when the focus is changed.
	 * @param listener a FocusListener
	 */
	public void addFocusListener(FocusListener listener);
	
	
	/**
	 * Adding a listener for any modification of the Text.
	 * @param listener a ModifyListener
	 */
	public void addModifyListener(ModifyListener listener);

}
