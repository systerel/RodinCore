/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed deprecated methods (occurrence count)
 *******************************************************************************/
package org.rodinp.core;


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
public interface IInternalParent extends IParent, IAttributedElement {

	/**
	 * Returns a handle to a child internal element with the given type and
	 * name.
	 * <p>
	 * This is a handle-only method. The child element may or may not be
	 * present.
	 * </p>
	 * 
	 * @param childType
	 *            type of the child element
	 * @param childName
	 *            name of the child element
	 * @return the child internal element with the given type and name
	 */
	<T extends IInternalElement> T getInternalElement(
			IInternalElementType<T> childType, String childName);

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
	 * Returns whether this element and the given element have the same attributes
	 * in the Rodin database. Two elements have the same attributes if and only
	 * if:
	 * <ul>
	 * <li>they both don't exist or they both exist and
	 * <li>they carry the same attributes (if any) with the same values.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param other
	 *            the element to test for similar attributes
	 * @return <code>true</code> iff this element and the given element
	 *         have the same attributes in the Rodin database
	 * @exception RodinDBException
	 *                if an error was encountered while comparing the elements
	 */
	boolean hasSameAttributes(IInternalParent other) throws RodinDBException;
	
	/**
	 * Returns whether this element and the given element have the same children
	 * in the Rodin database. Two elements have the same contents if and only
	 * if:
	 * <ul>
	 * <li>they both don't exist or they both exist and
	 * <li>their children (if any, and taken in order) have the same contents</li>
	 * </ul>
	 * </p>
	 * 
	 * @param other
	 *            the element to test for similar children
	 * @return <code>true</code> iff this element and the given element
	 *         contain the same children in the Rodin database
	 * @exception RodinDBException
	 *                if an error was encountered while comparing the elements
	 *                or their descendants.
	 */
	boolean hasSameChildren(IInternalParent other) throws RodinDBException;
	
	/**
	 * Returns whether this element and the given element have the same contents
	 * in the Rodin database. Two elements have the same contents if and only
	 * if:
	 * <ul>
	 * <li>they have the same element name and element type.</li>
	 * <li>they both don't exist or they both exist and:
	 * <ul>
	 * <li>they carry the same attributes (if any) with the same values;</li>
	 * <li>their children (if any, and taken in order) have the same contents.</li>
	 * </ul>
	 * </ul>
	 * </p>
	 * 
	 * @param other
	 *            the element to test for similar contents
	 * @return <code>true</code> iff this element and the given element
	 *         contain the same subtree in the Rodin database
	 * @exception RodinDBException
	 *                if an error was encountered while comparing the elements
	 *                or their descendants.
	 */
	boolean hasSameContents(IInternalParent other) throws RodinDBException;
	
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

	/**
	 * Returns a handle to the element which has the same relative path as this
	 * element, but relative to the given file. In particular, if this element
	 * is a file, then the given file is returned.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @param newFile
	 *            file in which to construct the new handle
	 * @return an element with the same path relative to the given file as this
	 *         element
	 */
	IInternalParent getSimilarElement(IRodinFile newFile);

}
