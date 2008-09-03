/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed deprecated methods (contents and occurrence count)
 *     Systerel - added method getNextSibling()
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for all internal elements.
 * <p>
 * Internal elements are elements of the database that are stored within Rodin
 * files (i.e., are descendants thereof).
 * </p>
 * 
 * @author Laurent Voisin
 */
//TODO document IInternalElement
public interface IInternalElement extends IRodinElement, IInternalParent,
		IElementManipulation, IAttributedElement {

	/**
	 * Creates this internal element in the database. As a side effect, all
	 * ancestors of this element are open if they were not already.
	 * 
	 * @param nextSibling
	 *            sibling before which this element should be created (must have
	 *            the same parent as this element), or <code>null</code> to
	 *            create this element in the last position
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if the element could not be created. Reasons include:
	 *                <ul>
	 *                <li>The parent of this element does not exist
	 *                (ELEMENT_DOES_NOT_EXIST)</li>
	 *                <li>The parent of this element is read-only (READ_ONLY)</li>
	 *                <li>There already exists a child element with the given
	 *                type and name (NAME_COLLISION)</li>
	 *                <li>The given sibling is invalid (INVALID_SIBLING)</li>
	 *                <li> A <code>CoreException</code> occurred while
	 *                accessing an underlying resource
	 *                </ul>
	 */
	void create(IInternalElement nextSibling, IProgressMonitor monitor)
			throws RodinDBException;
	
	/**
	 * Creates a new Rodin Problem marker for the given attribute of this
	 * element.
	 * <p>
	 * The new marker is attached to the underlying resource of this element.
	 * Its marker type is {@link RodinMarkerUtil#RODIN_PROBLEM_MARKER}.
	 * </p>
	 * 
	 * @param attributeType
	 *            type of the attribute to mark, or <code>null</code> if no
	 *            attribute should be marked
	 * @param problem
	 *            problem to attach to the new marker
	 * @param args
	 *            arguments to the problem
	 * @exception RodinDBException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li>This element does not exist.</li>
	 *                </ul>
	 * @see RodinMarkerUtil
	 */
	void createProblemMarker(IAttributeType attributeType,
			IRodinProblem problem, Object... args) throws RodinDBException;

	/**
	 * Creates a new Rodin Problem marker for the given String attribute of this
	 * element, and located at the given position in the attribute.
	 * <p>
	 * The new marker is attached to the underlying resource of this element.
	 * Its marker type is {@link RodinMarkerUtil#RODIN_PROBLEM_MARKER}.
	 * </p>
	 * 
	 * @param attributeType
	 *            type of the attribute to mark, or <code>null</code> if no
	 *            attribute should be marked
	 * @param charStart
	 *            start position (zero-relative and inclusive), or a negative
	 *            value to indicate its absence
	 * @param charEnd
	 *            end position (zero-relative and exclusive), or a negative
	 *            value to indicate its absence
	 * @param problem
	 *            problem to attach to the new marker
	 * @param args
	 *            arguments to the problem
	 * @exception RodinDBException
	 *                if this method fails. Reasons include:
	 *                <ul>
	 *                <li>This element does not exist.</li>
	 *                <li>The specified attribute is not set for this element,
	 *                although start and end positions have been specified.</li>
	 *                <li>The start and end positions are specified together
	 *                with a <code>null</code> attribute id.</li>
	 *                <li>The start and end positions are specified with an
	 *                attribute id whose kind is not <code>string</code>.</li>
	 *                <li>The start or end position is negative, but not both.</li>
	 *                <li>The end position is less than or equal to the start
	 *                position.</li>
	 *                </ul>
	 * @see RodinMarkerUtil
	 */
	void createProblemMarker(IAttributeType.String attributeType, int charStart,
			int charEnd, IRodinProblem problem, Object... args)
			throws RodinDBException;

	/* (non-Javadoc)
	 * @see org.rodinp.core.IRodinElement#getElementType()
	 */
	IInternalElementType<? extends IInternalElement> getElementType();
	
	/**
	 * Returns the Rodin file containing this internal element.
	 * <p>
	 * This is a handle-only method.
	 * </p>
	 * 
	 * @return the Rodin file containing this element
	 */
	IRodinFile getRodinFile();

	/**
	 * Returns the internal element that follows this element in the children
	 * list of its parent or <code>null</code> if this element is the last
	 * sibling.
	 * <p>
	 * This method is useful when one needs to create an element just after
	 * another. It allows to get the parameter to be passed to the
	 * <code>create()</code> for properly ordering the created element.
	 * </p>
	 * 
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * 
	 * @return the next sibling of this internal element or <code>null</code>
	 *         if this element is the last child of its parent
	 */
	IInternalElement getNextSibling() throws RodinDBException;

}