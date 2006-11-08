/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.IParent.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core;

/**
 * Common protocol for Rodin elements that contain other Rodin elements.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IParent {

	/**
	 * Returns the immediate children of this element. Unless otherwise
	 * specified by the implementing element, the children are 
	 * ordered.
	 * 
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return the immediate children of this element
	 */
	IRodinElement[] getChildren() throws RodinDBException;

	/**
	 * Returns the immediate children of this element that are of the given
	 * type. The order on children is maintained while extracting them.
	 * 
	 * @param type
	 *            type of the children to retrieve
	 * @return the immediate children of this element that are of the given type
	 * @exception RodinDBException
	 *                if the given type is invalid, this element does not exist,
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 */
	IRodinElement[] getChildrenOfType(IElementType type) throws RodinDBException;

	/**
	 * Returns whether this element <b>may</b> have one or more immediate
	 * children. This is a convenience method, and may be more efficient than
	 * testing whether <code>getChildren</code> is an empty array.
	 * <p>
	 * If this method returns <code>false</code>, one can be sure that this
	 * element doesn't have any child. On the contrary, if <code>true</code>
	 * is returned, this means that the answer is not known (this element may or
	 * may not have children).
	 * </p>
	 * 
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return <code>true</code> if this element may have immediate children,
	 *         <code>false</code> otherwise
	 */
	boolean hasChildren() throws RodinDBException;

}
