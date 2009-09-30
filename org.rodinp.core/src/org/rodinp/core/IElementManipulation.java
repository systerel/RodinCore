/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation as
 * 			org.eclipse.jdt.core.ISourceManipulation
 *     ETH Zurich - adaptation from JDT to Rodin
 *     Systerel - added clear() method
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for Rodin elements that support manipulations such as copy,
 * move, rename, and delete.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IElementManipulation extends IRodinElement {

	/**
	 * Copies this element to the given container.
	 * 
	 * @param container
	 *            the destination container
	 * @param sibling
	 *            the sibling element before which the copy should be inserted,
	 *            or <code>null</code> if the copy should be inserted as the
	 *            last child of the container
	 * @param rename
	 *            the new name for the element, or <code>null</code> if the copy
	 *            retains the name of this element
	 * @param replace
	 *            <code>true</code> if any existing child in the container with
	 *            the target name should be replaced, and <code>false</code> to
	 *            throw an exception in the event of a name collision
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @exception RodinDBException
	 *                if this element could not be copied. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element, container element, or sibling does
	 *                not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>A <code>CoreException</code> occurred while updating
	 *                an underlying resource</li>
	 *                <li>The container is of an incompatible type
	 *                (INVALID_DESTINATION)</li>
	 *                <li>The sibling is not a child of the given container
	 *                (INVALID_SIBLING)</li>
	 *                <li>The new name is invalid (INVALID_NAME)</li>
	 *                <li>A child in the container already exists with the same
	 *                name (NAME_COLLISION) and <code>replace</code> has been
	 *                specified as <code>false</code></li>
	 *                <li>The container or this element is read-only (READ_ONLY)
	 *                </li>
	 *                <li>The destination element is a root element
	 *                (ROOT_ELEMENT)</li>
	 *                </ul>
	 * 
	 * @exception IllegalArgumentException
	 *                if container is <code>null</code>
	 */
	void copy(IRodinElement container, IRodinElement sibling, String rename,
			boolean replace, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Removes all attributes and children of this element, forcing if specified
	 * and necessary.
	 * 
	 * @param force
	 *            a flag controlling whether underlying resources that are not
	 *            in sync with the local file system will be tolerated (same as
	 *            the force flag in IResource operations).
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @exception RodinDBException
	 *                if this element could not be cleared. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>A <code>CoreException</code> occurred while updating
	 *                an underlying resource (CORE_EXCEPTION)</li>
	 *                <li>This element is read-only (READ_ONLY)</li>
	 *                </ul>
	 */
	void clear(boolean force, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Deletes this element, forcing if specified and necessary.
	 * 
	 * @param force
	 *            a flag controlling whether underlying resources that are not
	 *            in sync with the local file system will be tolerated (same as
	 *            the force flag in IResource operations).
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @exception RodinDBException
	 *                if this element could not be deleted. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>A <code>CoreException</code> occurred while updating
	 *                an underlying resource (CORE_EXCEPTION)</li>
	 *                <li>This element is read-only (READ_ONLY)</li>
	 *                <li>This element is a root element (ROOT_ELEMENT)</li>
	 *                </ul>
	 */
	void delete(boolean force, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Moves this element to the given container.
	 * 
	 * @param container
	 *            the destination container
	 * @param sibling
	 *            the sibling element before which the element should be
	 *            inserted, or <code>null</code> if the element should be
	 *            inserted as the last child of the container
	 * @param rename
	 *            the new name for the element, or <code>null</code> if the
	 *            element retains its name
	 * @param replace
	 *            <code>true</code> if any existing child in the container with
	 *            the target name should be replaced, and <code>false</code> to
	 *            throw an exception in the event of a name collision
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @exception RodinDBException
	 *                if this element could not be moved. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element, container element, or sibling does
	 *                not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>A <code>CoreException</code> occurred while updating
	 *                an underlying resource</li>
	 *                <li>The container is of an incompatible type
	 *                (INVALID_DESTINATION)</li>
	 *                <li>The sibling is not a child of the given container
	 *                (INVALID_SIBLING)</li>
	 *                <li>The new name is invalid (INVALID_NAME)</li>
	 *                <li>A child in the container already exists with the same
	 *                name (NAME_COLLISION) and <code>replace</code> has been
	 *                specified as <code>false</code></li>
	 *                <li>The container or this element is read-only (READ_ONLY)
	 *                </li>
	 *                <li>This element or the destination element is a root
	 *                element (ROOT_ELEMENT)</li>
	 *                </ul>
	 * 
	 * @exception IllegalArgumentException
	 *                if container is <code>null</code>
	 */
	void move(IRodinElement container, IRodinElement sibling, String rename,
			boolean replace, IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Renames this element to the given name.
	 * 
	 * @param name
	 *            the new name for the element
	 * @param replace
	 *            <code>true</code> if any existing element with the target name
	 *            should be replaced, and <code>false</code> to throw an
	 *            exception in the event of a name collision
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @exception RodinDBException
	 *                if this element could not be renamed. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>A <code>CoreException</code> occurred while updating
	 *                an underlying resource</li>
	 *                <li>The new name is invalid (INVALID_NAME)</li>
	 *                <li>A child in the container already exists with the same
	 *                name (NAME_COLLISION) and <code>replace</code> has been
	 *                specified as <code>false</code></li>
	 *                <li>This element is read-only (READ_ONLY)</li>
	 *                <li>This element is a root element (ROOT_ELEMENT)</li>
	 *                </ul>
	 */
	void rename(String name, boolean replace, IProgressMonitor monitor)
			throws RodinDBException;

}
