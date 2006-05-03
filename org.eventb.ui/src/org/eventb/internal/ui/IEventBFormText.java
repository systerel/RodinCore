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
import org.eclipse.ui.forms.widgets.FormText;

/**
 * @author htson
 *         <p>
 *         This is the interface for Event-B FormText
 */
public interface IEventBFormText extends IPropertyChangeListener {

	/**
	 * Getting the actual FormText contains inside this Event-B FormText
	 * <p>
	 * 
	 * @return the FormText contains in this Event-B FormText
	 */
	public FormText getFormText();
}
