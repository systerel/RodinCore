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
 * Common protocol for elements that can contain internal elements.
 * <p>
 * Such elements are Rodin files and internal elements themselves (which are
 * descendants of a Rodin file).
 * </p>
 * <p>
 * For each Rodin file, the database provides two versions:
 * <ul>
 * <li>a stable snapshot that corresponds to the contents of the Rodin file on
 * disk, and which is read-only.</li>
 * <li>a buffered copy of the Rodin file in memory which is read-write.</li>
 * </ul>
 * As a consequence, there are two kinds of handles for these elements, stable
 * snapshot handles and mutable handles.
 * </p>
 * 
 * @author Laurent Voisin
 */
public interface IInternalParent extends IParent, IRodinElement {

	/**
	 * Creates and returns a new child internal element with the given type and
	 * name. This element must thus belong to a mutable copy of a Rodin file.
	 * 
	 * <p>
	 * If there already exists a child element with the same type and name, no
	 * new element is created and a database exception is thrown.
	 * </p>
	 * 
	 * TODO explain better use of nextSibling
	 * 
	 * @param type
	 *            type of the internal element to create
	 * @param name
	 *            name of the internal element to create
	 * @param nextSibling
	 *            succesor node of the internal element to create. Must be a
	 *            child of this element or <code>null</code> (in that latter
	 *            case, the new element will be the last child of this element).
	 * @param monitor
	 *            a progress monitor
	 * @exception RodinDBException
	 *                if the element could not be created. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>This Rodin element is read-only (READ_ONLY)</li>
	 *                <li>The given type is invalid
	 *                (INVALID_INTERNAL_ELEMENT_TYPE)</li>
	 *                <li>There already exists a child element with the given
	 *                type and name (NAME_COLLISION)</li>
	 *                <li>The given sibling is invalid (INVALID_SIBLING)</li>
	 *                </ul>
	 * @return a new internal element in this element with the specified type
	 *         and name
	 */
	IInternalElement createInternalElement(IInternalElementType type, String name,
			IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns a handle to a child internal element with the given type and
	 * name.
	 * 
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param childType
	 *            type of the child element
	 * @param childName
	 *            name of the child element
	 * @return the child internal element with the given type and name or
	 *         <code>null</code> if the given element type is unknown
	 */
	IInternalElement getInternalElement(IInternalElementType childType,
			String childName);

	/**
	 * Returns a handle to a child internal element with the given type, name.
	 * 
	 * <p>
	 * The given position must always be <code>1</code>.
	 * </p>
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param childType
	 *            type of the child element
	 * @param childName
	 *            name of the child element
	 * @param occurrenceCount
	 *            must be <code>1</code>
	 * @return the child internal element with the given type and name
	 *         or <code>null</code> if the given element type is unknown.
	 * @throws IllegalArgumentException
	 *             if the given position is not <code>1</code>.
	 * @deprecated As there are no duplicate elements anymore, the occurrence
	 *             count has become deprecated.
	 */
	@Deprecated
	IInternalElement getInternalElement(IInternalElementType childType,
			String childName, int occurrenceCount)
			throws IllegalArgumentException;
	
	/**
	 * Returns a handle to this element in the snapshot of its Rodin file.
	 * 
	 * <p>
	 * This is a handle-only method. The element may or may not be present.
	 * </p>
	 * 
	 * @return this element in the snapshot of its Rodin file
	 */
	IInternalParent getSnapshot();

	/**
	 * Returns a handle to this element in the mutable copy of its Rodin file.
	 * 
	 * <p>
	 * This is a handle-only method. The element may or may not be present.
	 * </p>
	 * 
	 * @return this element in the mutable copy of its Rodin file
	 */
	IInternalParent getMutableCopy();

	/**
	 * Returns whether this is a handle in a file snapshot.
	 * <p>
	 * This is a handle-only method. The element may or may not be present.
	 * </p>
	 * 
	 * @return <code>true</code> iff the corresponding element is or belongs
	 *         to the stable snapshot of a Rodin file
	 */
	boolean isSnapshot();
	
}
