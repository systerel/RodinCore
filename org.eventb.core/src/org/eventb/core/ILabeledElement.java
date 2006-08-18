/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for elements that carry a label. A label is a name that the user
 * associates with the element. It does not need to be unique. In particular, it is 
 * not the same as the element name maintained by the Rodin database. The element name
 * is unique and serves to identify a Rodin element. A label cannot be used to identify
 * an element.
 * <p>
 * The name is manipulated as a bare string of characters.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ILabeledElement extends IInternalElement {

	/**
	 * Sets the label contained in this element.
	 * <p>
	 * Throws an 
	 * </p>
	 * 
	 * @param label
	 *            the label for the element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setLabel(String label, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the label contained in this element.
	 * 
	 * @return the identifier of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getLabel(IProgressMonitor monitor) throws RodinDBException;
	
}
