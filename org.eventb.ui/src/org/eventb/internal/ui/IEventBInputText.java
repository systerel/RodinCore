/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Text;

/**
 * @author htson
 *         <p>
 *         This is the interface for Event-B Input Text
 */
public interface IEventBInputText extends IPropertyChangeListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event);

	/**
	 * Getting the Text Widget contains inside this Event-B Input Text.
	 * <p>
	 * 
	 * @return the Text Widget contains inside this Event-B Input Text
	 */
	public Text getTextWidget();

	/**
	 * Setting the focus to the contained Text.
	 */
	public void setFocus();
	
	public void dispose();

}
