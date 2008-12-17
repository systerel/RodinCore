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
 *     Systerel - Moved method declarations from IAttributedElement
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
@SuppressWarnings("deprecation")
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

	/**
	 * Returns an array of the types of all attributes currently attached to
	 * this element. If this element doesn't carry any attribute, an empty array
	 * is returned.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return the types of all attributes of this element
	 */
	IAttributeType[] getAttributeTypes() throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given boolean type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	boolean getAttributeValue(IAttributeType.Boolean type)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given handle type and carried
	 * by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	IRodinElement getAttributeValue(IAttributeType.Handle type)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given integer type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	int getAttributeValue(IAttributeType.Integer type) throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given long integer type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>long</code>
	 * @return the value of the attribute with the given type
	 */
	long getAttributeValue(IAttributeType.Long type) throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given string type and carried
	 * by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	String getAttributeValue(IAttributeType.String type)
			throws RodinDBException;

	/**
	 * Tells whether this element carries an attribute with the given type.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return <code>true</code> iff this element carries an attribute with
	 *         the given type
	 */
	boolean hasAttribute(IAttributeType type) throws RodinDBException;

	/**
	 * Removes the attribute with the given type from this element. If the
	 * attribute didn't exist previously, this method returns directly, no
	 * exception is thrown.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute to remove
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void removeAttribute(IAttributeType type, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given boolean type to the given
	 * value. If the specified attribute didn't exist, it is created.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void setAttributeValue(IAttributeType.Boolean type, boolean newValue,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given handle type to the given
	 * value. If the specified attribute didn't exist, it is created.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void setAttributeValue(IAttributeType.Handle type, IRodinElement newValue,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given integer type to the given
	 * value. If the specified attribute didn't exist, it is created.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void setAttributeValue(IAttributeType.Integer type, int newValue,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given long integer type to the
	 * given value. If the specified attribute didn't exist, it is created.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void setAttributeValue(IAttributeType.Long type, long newValue,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given string type to the given
	 * value. If the specified attribute didn't exist, it is created.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void setAttributeValue(IAttributeType.String type, String newValue,
			IProgressMonitor monitor) throws RodinDBException;

}