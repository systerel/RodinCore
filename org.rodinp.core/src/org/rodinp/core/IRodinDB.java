/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.core.IJavaModel
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.internal.core.ElementTypeManager;

/**
 * Represents the root Rodin element corresponding to the workspace. Since there
 * is only one such root element, it is commonly referred to as <em>the</em>
 * Rodin database element. The Rodin database element needs to be opened before
 * it can be navigated or manipulated. The Rodin database element has no parent
 * (it is the root of the Rodin element hierarchy). Its children are
 * <code>IRodinProject</code>s.
 * <p>
 * This interface provides methods for performing copy, move, rename, and delete
 * operations on multiple Rodin elements.
 * </p>
 * <p>
 * An instance of one of these handles can be created via
 * <code>RodinCore.valueOf(workspace.getRoot())</code>.
 * </p>
 * 
 * @see RodinCore#valueOf(IWorkspaceRoot)
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRodinDB extends IRodinElement, IOpenable, IParent {

	/**
	 * The element type of the Rodin database.
	 */
	IElementType<IRodinDB> ELEMENT_TYPE = ElementTypeManager.getInstance()
			.getDatabaseElementType();

	/**
	 * Returns whether this Rodin database contains an
	 * <code>IRodinElement</code> whose resource is the given resource or a
	 * non-Rodin resource which is the given resource.
	 * <p>
	 * Note: no existency check is performed on the argument resource. If it is
	 * not accessible (see <code>IResource.isAccessible()</code>) yet but
	 * would be located in Rodin database range, then it will return
	 * <code>true</code>.
	 * </p>
	 * <p>
	 * If the resource is accessible, it can be reached by navigating the Rodin
	 * database down using the <code>getChildren()</code> and/or
	 * <code>getNonRodinResources()</code> methods.
	 * </p>
	 * 
	 * @param resource
	 *            the resource to check
	 * @return true if the resource is accessible through the Rodin database
	 */
	boolean contains(IResource resource);

	/**
	 * Copies the given elements to the specified container(s). If one container
	 * is specified, all elements are copied to that container. If more than one
	 * container is specified, the number of elements and containers must match,
	 * and each element is copied to its associated container.
	 * <p>
	 * Optionally, each copy can be positioned before a sibling element. If
	 * <code>null</code> is specified for a given sibling, the copy is
	 * inserted as the last child of its associated container.
	 * </p>
	 * <p>
	 * Optionally, each copy can be renamed. If <code>null</code> is specified
	 * for the new name, the copy is not renamed.
	 * </p>
	 * <p>
	 * Optionally, any existing child in the destination container with the same
	 * name can be replaced by specifying <code>true</code> for force.
	 * Otherwise an exception is thrown in the event that a name collision
	 * occurs.
	 * </p>
	 * 
	 * @param elements
	 *            the elements to copy
	 * @param containers
	 *            the container, or list of containers
	 * @param siblings
	 *            the list of sibling elements, any of which may be
	 *            <code>null</code>; or <code>null</code>
	 * @param renamings
	 *            the list of new names, any of which may be <code>null</code>;
	 *            or <code>null</code>
	 * @param replace
	 *            <code>true</code> if any existing child in a target
	 *            container with the target name should be replaced, and
	 *            <code>false</code> to throw an exception in the event of a
	 *            name collision
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if an element could not be copied. Reasons include:
	 *                <ul>
	 *                <li> There is no element to process
	 *                (NO_ELEMENTS_TO_PROCESS). The given elements is null or
	 *                empty</li>
	 *                <li> A specified element, container, or sibling does not
	 *                exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                updating an underlying resource</li>
	 *                <li> A container is of an incompatible type (<code>INVALID_DESTINATION</code>)</li>
	 *                <li> A sibling is not a child of it associated container (<code>INVALID_SIBLING</code>)</li>
	 *                <li> A new name is invalid (<code>INVALID_NAME</code>)</li>
	 *                <li> A child in its associated container already exists
	 *                with the same name and <code>replace</code> has been
	 *                specified as <code>false</code> (<code>NAME_COLLISION</code>)</li>
	 *                <li>The number of renamings does not match the number of
	 *                elements (<code>INVALID_RENAMING</code>)</li>
	 *                <li> A container or element is read-only (<code>READ_ONLY</code>)</li>
	 *                <li> A destination element is a root element (<code>ROOT_ELEMENT</code>)</li>
	 *                </ul>
	 */
	void copy(IRodinElement[] elements, IRodinElement[] containers,
			IRodinElement[] siblings, String[] renamings, boolean replace,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Deletes the given elements, forcing the operation if necessary and
	 * specified.
	 * 
	 * @param elements
	 *            the elements to delete
	 * @param force
	 *            a flag controlling whether underlying resources that are not
	 *            in sync with the local file system will be tolerated
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @exception RodinDBException
	 *                if an element could not be deleted. Reasons include:
	 *                <ul>
	 *                <li>There is no element to process (
	 *                <code>NO_ELEMENTS_TO_PROCESS</code>). The given elements
	 *                is null or empty</li>
	 *                <li>A specified element does not exist (
	 *                <code>ELEMENT_DOES_NOT_EXIST</code>)</li>
	 *                <li>A <code>CoreException</code> occurred while updating
	 *                an underlying resource</li>
	 *                <li>An element is read-only (<code>READ_ONLY</code>)</li>
	 *                <li>An element is a root element (<code>ROOT_ELEMENT</code>)</li>
	 *                </ul>
	 */
	void delete(IRodinElement[] elements, boolean force,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the Rodin project with the given name. This is a handle-only
	 * method. The project may or may not exist.
	 * 
	 * @param name
	 *            the name of the Rodin project
	 * @return the Rodin project with the given name
	 */
	IRodinProject getRodinProject(String name);

	/**
	 * Returns the Rodin projects in this Rodin database, or an empty array if
	 * there are none.
	 * 
	 * @return the Rodin projects in this Rodin database, or an empty array if
	 *         there are none
	 * @exception RodinDBException
	 *                if this request fails.
	 */
	IRodinProject[] getRodinProjects() throws RodinDBException;

	/**
	 * Returns an array of non-Rodin resources (that is, non-Rodin projects) in
	 * the workspace.
	 * <p>
	 * Non-Rodin projects include all projects that are closed (even if they
	 * have the Rodin nature).
	 * </p>
	 * 
	 * @return an array of non-Rodin projects (<code>IProject</code>s)
	 *         contained in the workspace.
	 * @throws RodinDBException
	 *             if this element does not exist or if an exception occurs
	 *             while accessing its corresponding resource
	 */
	IResource[] getNonRodinResources() throws RodinDBException;

	/**
	 * Returns the workspace associated with this Rodin database.
	 * 
	 * @return the workspace associated with this Rodin database
	 */
	IWorkspace getWorkspace();

	/**
	 * Returns the workspace root associated with this Rodin database.
	 * 
	 * @return the workspace root associated with this Rodin database
	 */
	IWorkspaceRoot getWorkspaceRoot();

	/**
	 * Moves the given elements to the specified container(s). If one container
	 * is specified, all elements are moved to that container. If more than one
	 * container is specified, the number of elements and containers must match,
	 * and each element is moved to its associated container.
	 * <p>
	 * Optionally, each element can be positioned before a sibling element. If
	 * <code>null</code> is specified for sibling, the element is inserted as
	 * the last child of its associated container.
	 * </p>
	 * <p>
	 * Optionally, each element can be renamed. If <code>null</code> is
	 * specified for the new name, the element is not renamed.
	 * </p>
	 * <p>
	 * Optionally, any existing child in the destination container with the same
	 * name can be replaced by specifying <code>true</code> for force.
	 * Otherwise an exception is thrown in the event that a name collision
	 * occurs.
	 * </p>
	 * 
	 * @param elements
	 *            the elements to move
	 * @param containers
	 *            the container, or list of containers
	 * @param siblings
	 *            the list of siblings element any of which may be
	 *            <code>null</code>; or <code>null</code>
	 * @param renamings
	 *            the list of new names any of which may be <code>null</code>;
	 *            or <code>null</code>
	 * @param replace
	 *            <code>true</code> if any existing child in a target
	 *            container with the target name should be replaced, and
	 *            <code>false</code> to throw an exception in the event of a
	 *            name collision
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if an element could not be moved. Reasons include:
	 *                <ul>
	 *                <li> There is no element to process
	 *                (NO_ELEMENTS_TO_PROCESS). The given elements is null or
	 *                empty</li>
	 *                <li> A specified element, container, or sibling does not
	 *                exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                updating an underlying resource</li>
	 *                <li> A container is of an incompatible type (<code>INVALID_DESTINATION</code>)</li>
	 *                <li> A sibling is not a child of it associated container (<code>INVALID_SIBLING</code>)</li>
	 *                <li> A new name is invalid (<code>INVALID_NAME</code>)</li>
	 *                <li> A child in its associated container already exists
	 *                with the same name and <code>replace</code> has been
	 *                specified as <code>false</code> (<code>NAME_COLLISION</code>)</li>
	 *                <li>The number of renamings does not match the number of
	 *                elements (<code>INVALID_RENAMING</code>)</li>
	 *                <li> A container or element is read-only (<code>READ_ONLY</code>)</li>
	 *                <li> A source or destination element is a root element (<code>ROOT_ELEMENT</code>)</li>
	 *                </ul>
	 */
	void move(IRodinElement[] elements, IRodinElement[] containers,
			IRodinElement[] siblings, String[] renamings, boolean replace,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Renames the given elements as specified.
	 * 
	 * @param elements
	 *            the elements to rename
	 * @param names
	 *            the list of new names. Must not be <code>null</code>. Must
	 *            have the same length as <code>elements</code>.
	 * @param replace
	 *            <code>true</code> if an existing element with the target
	 *            name should be replaced, and <code>false</code> to throw an
	 *            exception in the event of a name collision
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if an element could not be renamed. Reasons include:
	 *                <ul>
	 *                <li> There is no element to process
	 *                (NO_ELEMENTS_TO_PROCESS). The given elements is null or
	 *                empty</li>
	 *                <li> A specified element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                updating an underlying resource
	 *                <li> A new name is invalid (<code>INVALID_NAME</code>)
	 *                <li> A child already exists with the same name and
	 *                <code>replace</code> has been specified as
	 *                <code>false</code> (<code>NAME_COLLISION</code>)
	 *                <li>The number of renamings does not match the number of
	 *                elements (<code>INVALID_RENAMING</code>)</li>
	 *                <li> An element is read-only (<code>READ_ONLY</code>)</li>
	 *                <li>An element is a root element (<code>ROOT_ELEMENT</code>)</li>
	 *                </ul>
	 */
	void rename(IRodinElement[] elements, String[] names, boolean replace,
			IProgressMonitor monitor) throws RodinDBException;

}
