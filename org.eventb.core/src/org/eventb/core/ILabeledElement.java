/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
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
 * The attribute storing the label is <i>optional</i>. This means if the attribute
 * is not present, the value should be interpreted as <i>undefined</i>.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ILabeledElement extends IInternalElement {
	
	/**
	 * Tests whether the label is defined or not.
	 * 
	 * @return whether the label is defined or not
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean hasLabel() throws RodinDBException;

	/**
	 * Sets the label contained in this element.
	 * 
	 * @param label
	 *            the label for the element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setLabel(String label, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns the label contained in this element.
	 * 
	 * @return the label of this element as a string
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	String getLabel() throws RodinDBException;
	
}
