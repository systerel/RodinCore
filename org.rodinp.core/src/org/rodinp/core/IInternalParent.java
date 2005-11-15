/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

/**
 * Common protocol for elements that contain internal elements.
 * 
 * @author Laurent Voisin
 */
public interface IInternalParent extends IParent, IRodinElement {

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
	InternalElement getInternalElement(String childType, String childName);

}
