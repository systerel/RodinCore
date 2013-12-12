/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.IOpenable
 *     Systerel - documentation update for isOpen()
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for Rodin elements that must be opened before they can be
 * navigated or modified. Opening a file element involves loading its contents.
 * <p>
 * To reduce complexity in clients, elements are automatically opened by the
 * Rodin database as element children are accessed. The Rodin database maintains
 * an LRU cache of open elements, and automatically closes elements as they are
 * swapped out of the cache to make room for other elements. Elements with
 * unsaved changes are never removed from the cache, and thus, if the client
 * maintains many open elements with unsaved changes, the LRU cache can grow in
 * size (in this case the cache is not bounded). However, as elements are saved,
 * the cache will shrink back to its original bounded size.
 * </p>
 * <p>
 * To open an element, all openable parent elements must be open. The Rodin
 * database automatically opens parent elements, as it automatically opens
 * elements. Opening an element may provide access to direct children and other
 * descendants, but does not automatically open any descendents which are
 * themselves {@link IOpenable}. For example, opening a file provides access to
 * all its internal elements, but opening a project does not open all files
 * in the project.
 * </p>
 *
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IOpenable {

	/**
	 * Closes this element and unloads it. Closing an element which is not open
	 * has no effect.
	 * 
	 * <p>
	 * Note: although {@link #close} is exposed in the API, clients are not
	 * expected to open and close elements - the Rodin database does this
	 * automatically as elements are accessed.
	 * 
	 * @exception RodinDBException
	 *                if an error occurs closing this element
	 */
	void close() throws RodinDBException;

	/**
	 * Returns <code>true</code> if this element is open and:
	 * <ul>
	 * <li>it has unsaved changes, or
	 * <li>one of its descendants has unsaved changes
	 * </ul>
	 * 
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return <code>true</code> if this element is open and:
	 *         <ul>
	 *         <li>it has unsaved changes, or
	 *         <li>one of its descendants has unsaved changes
	 *         </ul>
	 */
	boolean hasUnsavedChanges() throws RodinDBException;

	/**
	 * Returns whether the element is consistent with its underlying resource.
	 * The element is consistent when opened, and is consistent if the
	 * underlying resource has not been modified since it was last consistent.
	 * 
	 * <p>
	 * NOTE: Child consistency is not considered. For example, a project
	 * responds <code>true</code> when it knows about all of its files
	 * present in its underlying folder. However, one or more of the files
	 * could be inconsistent.
	 * </p>
	 * 
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return true if the element is consistent with its underlying resource,
	 *         false otherwise.
	 * @see IOpenable#makeConsistent(IProgressMonitor)
	 */
	boolean isConsistent() throws RodinDBException;

	/**
	 * Returns whether this openable is open. If this openable doesn't exist,
	 * this method will always return <code>false</code> (as it can't be
	 * opened).
	 * <p>
	 * <strong>Important Note:</strong> In the case of a Rodin project, this
	 * method returns whether the project contents have been loaded in the Rodin
	 * database cache. This is very different from telling whether the project
	 * is open or closed (as seen by the end-user). The latter information is
	 * rather reported by the {@link IRodinElement#exists()} method: an open
	 * project does exists, while a closed project is considered not to exist
	 * anymore.
	 * </p>
	 * 
	 * @return <code>true</code> iff this openable is open
	 */
	boolean isOpen();

	/**
	 * Makes this element consistent with its underlying resource by updating
	 * the element's structure and attributes as necessary.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if the element is unable to access the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                </ul>
	 * @see IOpenable#isConsistent()
	 */
	void makeConsistent(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Opens this element and all parent elements that are not already open. For
	 * Rodin files, the contents of the underlying resource is loaded into the
	 * database.
	 * 
	 * <p>
	 * Note: although {@link #open} is exposed in the API, clients are not
	 * expected to open and close elements - the Rodin database does this
	 * automatically as elements are accessed.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if an error occurs accessing the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                </ul>
	 */
	public void open(IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Saves any changes in this element to its underlying resource via a
	 * workspace resource operation. This has no effect if it has no unsaved
	 * changed.
	 * <p>
	 * The <code>force</code> parameter controls how this method deals with
	 * cases where the workbench is not completely in sync with the local file
	 * system. If <code>false</code> is specified, this method will only
	 * attempt to overwrite a corresponding file in the local file system
	 * provided it is in sync with the workbench. This option ensures there is
	 * no unintended data loss; it is the recommended setting. However, if
	 * <code>true</code> is specified, an attempt will be made to write a
	 * corresponding file in the local file system, overwriting any existing one
	 * if need be. In either case, if this method succeeds, the resource will be
	 * marked as being local (even if it wasn't before).
	 * <p>
	 * As a result of this operation, the element is consistent with its
	 * underlying resource.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @param force
	 *            it controls how this method deals with cases where the
	 *            workbench is not completely in sync with the local file system
	 * @exception RodinDBException
	 *                if an error occurs accessing the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>This Rodin element is read-only (READ_ONLY)</li>
	 *                </ul>
	 */
	public void save(IProgressMonitor monitor, boolean force)
			throws RodinDBException;

	/**
	 * Saves any changes in this element to its underlying resource via a
	 * workspace resource operation. This has no effect if it has no unsaved
	 * changed.
	 * <p>
	 * The <code>force</code> parameter controls how this method deals with
	 * cases where the workbench is not completely in sync with the local file
	 * system. If <code>false</code> is specified, this method will only
	 * attempt to overwrite a corresponding file in the local file system
	 * provided it is in sync with the workbench. This option ensures there is
	 * no unintended data loss; it is the recommended setting. However, if
	 * <code>true</code> is specified, an attempt will be made to write a
	 * corresponding file in the local file system, overwriting any existing one
	 * if need be. In either case, if this method succeeds, the resource will be
	 * marked as being local (even if it wasn't before).
	 * <p>
	 * As a result of this operation, the element is consistent with its
	 * underlying resource.
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @param force
	 *            it controls how this method deals with cases where the
	 *            workbench is not completely in sync with the local file system
	 * @param keepHistory
	 *            a flag indicating whether or not to store the current contents
	 *            in the local history
	 * @exception RodinDBException
	 *                if an error occurs accessing the contents of its
	 *                underlying resource. Reasons include:
	 *                <ul>
	 *                <li>This Rodin element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>This Rodin element is read-only (READ_ONLY)</li>
	 *                </ul>
	 */
	public void save(IProgressMonitor monitor, boolean force, boolean keepHistory)
			throws RodinDBException;
}
