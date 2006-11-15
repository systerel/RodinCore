/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * @author Laurent Voisin
 */
public interface IAttributedElement {

	/**
	 * Returns an array of the types of all attributes currently attached to
	 * this element. If this element doesn't carry any attribute, an empty array
	 * is returned.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return the types of all attributes of this element
	 */
	IAttributeType[] getAttributeTypes(IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given boolean type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	boolean getAttributeValue(IAttributeType.Boolean type,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given handle type and carried
	 * by this element.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	IRodinElement getAttributeValue(IAttributeType.Handle type,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given integer type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	int getAttributeValue(IAttributeType.Integer type, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given long integer type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>long</code>
	 * @return the value of the attribute with the given type
	 */
	long getAttributeValue(IAttributeType.Long type, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given string type and carried
	 * by this element.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource
	 * @return the value of the attribute with the given type
	 */
	String getAttributeValue(IAttributeType.String type,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Tells whether this element carries an attribute with the given type.
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return <code>true</code> iff this element carries an attribute with
	 *         the given type
	 */
	boolean hasAttribute(IAttributeType type, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Removes the attribute with the given type from this element.
	 * <p>
	 * If the attribute didn't exist previously, this method returns directly.
	 * No exception is thrown.
	 * </p>
	 * <p>
	 * The file containing this element is opened using the given progress
	 * monitor, if it was not already open.
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
	 * value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. The
	 * file containing this element is opened using the given progress monitor,
	 * if it was not already open.
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
	 * value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. The
	 * file containing this element is opened using the given progress monitor,
	 * if it was not already open.
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
	 * value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. The
	 * file containing this element is opened using the given progress monitor,
	 * if it was not already open.
	 * </p>
	 * <p>
	 * The attribute type must have been declared with the <code>integer</code>
	 * kind.
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
	 * given value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. The
	 * file containing this element is opened using the given progress monitor,
	 * if it was not already open.
	 * </p>
	 * <p>
	 * The attribute type must have been declared with the <code>long</code>
	 * kind.
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
	 * value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. The
	 * file containing this element is opened using the given progress monitor,
	 * if it was not already open.
	 * </p>
	 * <p>
	 * The attribute type must have been declared with the <code>string</code>
	 * kind.
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
