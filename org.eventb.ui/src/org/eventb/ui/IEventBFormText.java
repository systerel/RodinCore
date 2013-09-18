/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.ui;

import org.eclipse.ui.forms.widgets.FormText;

/**
 * @author htson
 *         <p>
 *         This is the interface for Event-B FormText.
 *         </p>
 * @since 1.0
 */
public interface IEventBFormText {

	/**
	 * Getting the actual FormText contains inside this Event-B FormText.
	 * <p>
	 * 
	 * @return the FormText contains in this Event-B FormText.
	 */
	public FormText getFormText();
	
	/**
	 * Dispose the control, should remove any extra resources/listeners that are
	 * located to this Event-B Form Text.
	 */
	public void dispose();
	
}
