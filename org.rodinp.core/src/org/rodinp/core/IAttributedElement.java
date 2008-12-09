/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich and others.
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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for Rodin elements that can carry an attribute.
 * 
 * TODO finish documentation of this interface
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @deprecated This interface is not useful anymore as it corresponds to
 *             {@link IInternalElement}, now that Rodin files cannot carry any
 *             attribute (separation of files and root elements). Client should
 *             thus use directly the {@link IInternalElement} interface.
 * 
 * @author Laurent Voisin
 */
@Deprecated
public interface IAttributedElement extends IRodinElement {

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
