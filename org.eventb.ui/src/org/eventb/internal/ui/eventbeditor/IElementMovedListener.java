/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.HashMap;

import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         Interface for element moved listener.
 */
public interface IElementMovedListener {

	/**
	 * Call-back method when there are some element that has been moved.
	 * <p>
	 * 
	 * @param moved
	 *            A set of mapping from old element to new element
	 */
	public void elementMoved(HashMap<IRodinElement, IRodinElement> moved);

}
