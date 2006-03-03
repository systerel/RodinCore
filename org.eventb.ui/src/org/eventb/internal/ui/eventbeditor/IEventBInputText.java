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

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Text;

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
	public Text getTextWidget();
	
	
	/**
	 * Setting the focus to the contained Text.
	 *
	 */
	public void setFocus();
	
}
