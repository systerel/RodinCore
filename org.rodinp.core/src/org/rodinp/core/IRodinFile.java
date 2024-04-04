/*******************************************************************************
 * Copyright (c) 2005, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.ElementTypeManager;

/**
 * Represents an entire Rodin file. File elements need to be opened before they
 * can be navigated or manipulated.
 * 
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
 * <p>
 * Each file has a root element, which type is determined from the extension of
 * the Rodin file. The associations between file extensions and root element
 * types are declared with plugin.xml extensions.
 * </p>
 *
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRodinFile extends IOpenable, IParent,
		IElementManipulation, ISnapshotable {

	/**
	 * The element type of all Rodin Files.
	 */
	IElementType<IRodinFile> ELEMENT_TYPE = ElementTypeManager.getInstance()
			.getFileElementType();

	/**
	 * Creates this file in the database. As a side effect, all ancestors of
	 * this element are open if they were not already.
	 * <p>
	 * It is possible that this file already exists. The value of the
	 * <code>force</code> parameter effects the resolution of such a conflict:
	 * <ul>
	 * <li><code>true</code> - in this case the file is created anew with
	 * empty contents</li>
	 * <li><code>false</code> - in this case a <code>RodinDBException</code>
	 * is thrown</li>
	 * </ul>
	 * </p>
	 * 
	 * @param force
	 *            specify how to handle conflict is this element already exists
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if the element could not be created. Reasons include:
	 *                <ul>
	 *                <li> The parent of this element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>This file element exists and force is
	 *                <code>false</code> (NAME_COLLISION)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                creating an underlying resource
	 *                </ul>
	 */
	void create(boolean force, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Finds the elements in this file that correspond to the given element. An
	 * element A corresponds to an element B if:
	 * <ul>
	 * <li>A has the same element type and name as B.
	 * <li>The parent of A corresponds to the parent of B recursively up to
	 * their respective files.
	 * <li>A exists.
	 * </ul>
	 * Returns <code>null</code> if no such Rodin elements can be found or if
	 * the given element is not included in a file.
	 * 
	 * @param element
	 *            the given element
	 * @return the found elements in this file that correspond to the given
	 *         element
	 * @deprecated This method has never been implemented and always returns an
	 *             empty array.
	 */
	@Deprecated(since="1.10")
	IRodinElement[] findElements(IRodinElement element);

	/**
	 * Returns the bare name of this Rodin file.  The bare name of a Rodin file
	 * is its element name with the possible file extension removed.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the bare name of this Rodin file
	 * @see IResource#getFileExtension()
	 */
	String getBareName();
		
	@Override
	IFile getResource();

	/**
	 * Reverts this file. Reverting a file has the effect of forgetting any
	 * unsaved change and closing the file.
	 * 
	 * @exception RodinDBException
	 *                if an error occurs closing this element
	 */
	void revert() throws RodinDBException;

	/**
	 * Returns the root internal element of this file.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the root element of this Rodin file
	 * 
	 */
	IInternalElement getRoot();
	
	/**
	 * Returns the element type of the root element of this file.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the type of the root element of this Rodin file
	 * 
	 */
	IInternalElementType<?> getRootElementType();

	// Methods from ISnapshotable with specialized return types
	@Override
	IRodinFile getSnapshot();
	@Override
	IRodinFile getMutableCopy();

}