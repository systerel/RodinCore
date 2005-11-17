/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for elements that contain internal elements.
 * 
 * @author Laurent Voisin
 */
public interface IInternalParent extends IParent, IRodinElement {

	/**
	 * Creates and returns a new child internal element with the given type and
	 * name. As a side effect, the containing file is opened, if it was not
	 * already.
	 * 
	 * <p>
	 * A new internal element is always created by this method, whether there
	 * already exists an element with the same name or not.
	 * </p>
	 * 
	 * @param type
	 *            type of the internal element to create
	 * @param name
	 *            name of the internal element to create. Should be
	 *            <code>null</code> if the new element is unnamed.
	 * @param nextSibling
	 *            succesor node of the internal element to create. Must be a
	 *            child of this element or <code>null</code> (in that latter
	 *            case, the new element will be the last child of this element).
	 * @param monitor
	 *            the given progress monitor
	 * @exception RodinDBException
	 *                if the element could not be created. Reasons include:
	 *                <ul>
	 *                <li> This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                creating an underlying resource
	 *                <li> The given type is unknown
	 *                </ul>
	 * @return a new internal element in this element with the specified type and name
	 */
	IInternalElement createInternalElement(String type, String name,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns a handle to a child internal element with the given type and
	 * name. This is a handle-only method. The child element may or may not
	 * be present.
	 * 
	 * @param childType
	 *            type of the child element
	 * @param childName
	 *            name of the child element
	 * @return the child internal element with the given type and name or
	 *         <code>null</code> if the given element type is unknown.
	 */
	IInternalElement getInternalElement(String childType, String childName);

}
