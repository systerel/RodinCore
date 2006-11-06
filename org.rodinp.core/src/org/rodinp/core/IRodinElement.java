/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.core.IRodinElement.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * Common protocol for all elements provided by the Rodin database. Rodin
 * database elements are exposed to clients as handles to the actual underlying
 * element. The Rodin database may hand out any number of handles for each
 * element. Handles that refer to the same element are guaranteed to be equal,
 * but not necessarily identical.
 * <p>
 * Methods annotated as "handle-only" do not require underlying elements to
 * exist. Methods that require underlying elements to exist throw a
 * <code>RodinDBException</code> when an underlying element is missing.
 * {@link RodinDBException#isDoesNotExist()} can be used to recognize this
 * common special case.
 * </p>
 * <p>
 * All methods that manipulate element types expect that the element type is
 * represented by a canonical String. This restriction should not be a problem
 * for clients, as most of the time the element types manipulated come from
 * constant Strings (which are interned by the Java compiler). However, in case
 * a client needs to dynamically create an element type name, this client needs
 * to make it canonical before passing it to the Rodin database (see
 * {@link String#intern()}).
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IRodinElement extends IAdaptable {

	/**
	 * Constant representing a Rodin database (workspace level object).
	 * A Rodin element with this type can be safely cast to <code>IRodinDB</code>.
	 */
	String RODIN_DATABASE = RodinCore.PLUGIN_ID + ".database";

	/**
	 * Constant representing a Rodin project.
	 * A Rodin element with this type can be safely cast to <code>IRodinProject</code>.
	 */
	String RODIN_PROJECT = RodinCore.PLUGIN_ID + ".project";
	
	/**
	 * Creates a new Rodin Problem marker for this element.
	 * <p>
	 * The new marker is attached to the underlying resource of this element.
	 * Its marker type is {@link RodinMarkerUtil#RODIN_PROBLEM_MARKER}.
	 * </p>
	 * 
	 * @param problem
	 *            problem to attach to the new marker
	 * @param args
	 *            arguments to the problem
	 * @exception RodinDBException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li> This element does not exist.</li>
	 *                </ul>
	 * @see RodinMarkerUtil
	 */
	void createProblemMarker(IRodinProblem problem, Object... args)
			throws RodinDBException;
	
	/**
	 * Returns whether this Rodin element exists in the database.
	 * <p>
	 * Rodin elements are handle objects that may or may not be backed by an
	 * actual element. Rodin elements that are backed by an actual element are
	 * said to "exist", and this method returns <code>true</code>. For Rodin
	 * elements, it is always the case that if the element exists, then its
	 * parent also exists (provided it has one). Moreover, for any element which
	 * is not a stable file snapshot, the parent includes the element as one of
	 * its children. It is therefore possible to navigate to any existing Rodin
	 * element (except stable file snapshots) from the root of the Rodin
	 * database along a chain of existing Rodin elements. On the other hand,
	 * a stable snapshot file never shows up among the children of its parent
	 * element.
	 * </p>
	 * <p>
	 * The fact that an openable element exists gives no guarantee upon whether
	 * it can be opened.  For instance, a malformed Rodin file may exist, while
	 * it can never be opened by the database (because it contains invalid XML). 
	 * </p>
	 * <p>
	 * A call to this method might open the ancestors of this element, but will
	 * never open this element itself.
	 * </p>
	 * @return <code>true</code> if this element exists in the Rodin database,
	 *         and <code>false</code> if this element does not exist
	 */
	boolean exists();
	
	/**
	 * Returns the first ancestor of this Rodin element that has the given type.
	 * Returns <code>null</code> if no such an ancestor can be found.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param ancestorType
	 *            the given type (must be a canonical String)
	 * @return the first ancestor of this Rodin element that has the given type,
	 *         or <code>null</code> if no such an ancestor can be found
	 * @see String#intern()
	 */
	IRodinElement getAncestor(String ancestorType);

	/**
	 * Returns the resource that corresponds directly to this element, or
	 * <code>null</code> if there is no resource that corresponds to this
	 * element.
	 * <p>
	 * For example, the corresponding resource for an <code>IRodinFile</code>
	 * is its underlying <code>IFile</code>. The corresponding resource for
	 * an <code>IRodinProject</code> is its underlying <code>IProject</code>.
	 * Internal elements have no corresponding resource.
	 * </p>
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the corresponding resource, or <code>null</code> if none
	 */
	IResource getCorrespondingResource();

	/**
	 * Returns the name of this element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 *
	 * @return the element name
	 */
	String getElementName();

	/**
	 * Returns the type of this element as a canonical String.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the element type
	 */
	String getElementType();

	/**
	 * Returns a string representation of this element handle. The format of
	 * the string is not specified; however, the identifier is stable across
	 * workspace sessions, and can be used to recreate this handle via the 
	 * <code>RodinCore.create(String)</code> method.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the string handle identifier
	 * @see RodinCore#valueOf(java.lang.String)
	 */
	String getHandleIdentifier();

	/**
	 * Returns the Rodin database.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 *
	 * @return the Rodin database
	 */
	IRodinDB getRodinDB();

	/**
	 * Returns the Rodin project this element is contained in,
	 * or <code>null</code> if this element is not contained in any Rodin project
	 * (for instance, the <code>IRodinDB</code> is not contained in any Rodin 
	 * project).
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the containing Rodin project, or <code>null</code> if this element is
	 *   not contained in a Rodin project
	 */
	IRodinProject getRodinProject();

	/**
	 * Returns the first openable parent. If this element is openable, the element
	 * itself is returned. Returns <code>null</code> if this element doesn't have
	 * an openable parent.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the first openable parent or <code>null</code> if this element doesn't have
	 * an openable parent.
	 */
	IOpenable getOpenable();

	/**
	 * Returns the element directly containing this element,
	 * or <code>null</code> if this element has no parent.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the parent element, or <code>null</code> if this element has no parent
	 */
	IRodinElement getParent();

	/**
	 * Returns the path to the innermost resource enclosing this element. 
	 * The path returned is the full, absolute path to the underlying resource, 
	 * relative to the workbench. 
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the path to the innermost resource enclosing this element
	 */
	IPath getPath();
	
	/**
	 * Returns the innermost resource enclosing this element. 
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the innermost resource enclosing this element
	 */
	IResource getResource();
	
	/**
	 * Returns the scheduling rule associated with this Rodin element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the scheduling rule associated with this Rodin element
	 */
	ISchedulingRule getSchedulingRule();

	/**
	 * Returns the smallest underlying resource that contains
	 * this element, or <code>null</code> if this element is not contained
	 * in a resource.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the underlying resource, or <code>null</code> if none
	 */
	IResource getUnderlyingResource();

	/**
	 * Returns whether this element is an ancestor of the given element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param element
	 *            the element to test as a descendent
	 * @return <code>true</code> iff this element is an ancestor of the given
	 *         element
	 */
	boolean isAncestorOf(IRodinElement element);

	/**
	 * Returns whether this Rodin element is read-only. An element can be
	 * read-only for two reasons: either it corresponds to a resource which is
	 * read-only, or it is an internal element that belongs to a stable snapshot
	 * of a Rodin file, or the stable snapshot itself.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return <code>true</code> iff this element is read-only
	 */
	boolean isReadOnly();

}
