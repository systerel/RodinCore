/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An inteface for status change listeners.
 */
public interface IStatusChangedListener {

	/**
	 * A call-back when the status (saved/unsaved) of an element has been
	 * changed.
	 * <p>
	 * 
	 * @param element
	 *            the element whose status has been changed.
	 */
	public void statusChanged(IRodinElement element);

}
