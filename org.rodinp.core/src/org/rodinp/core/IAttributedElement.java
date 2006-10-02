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
	 * Returns an array of the names of all attributes currently attached to
	 * this element. If this element doesn't carry any attribute, an empty array
	 * is returned.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * 
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return the names of all attributes of this element
	 */
	String[] getAttributeNames(IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given name and carried by
	 * this element.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>boolean</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>boolean</code>
	 * @return the value of the attribute with the given name
	 */
	boolean getBooleanAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given name and carried by
	 * this element.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>handle</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>handle</code>
	 * @return the value of the attribute with the given name
	 */
	IRodinElement getHandleAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given name and carried by
	 * this element.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>integer</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>integer</code>
	 * @return the value of the attribute with the given name
	 */
	int getIntegerAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given name and carried by
	 * this element.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>long</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>long</code>
	 * @return the value of the attribute with the given name
	 */
	long getLongAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Returns the value of the attribute with the given name and carried by
	 * this element.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>string</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element or the specified attribute does not exist
	 *                or if an exception occurs while accessing its
	 *                corresponding resource or if the given attribute has
	 *                another kind than <code>string</code>
	 * @return the value of the attribute with the given name
	 */
	String getStringAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Tells whether this element carries an attribute with the given name.
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute to test for existence
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return <code>true</code> iff this element carries an attribute with
	 *         the given name
	 */
	boolean hasAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Removes the attribute with the given name from this element.
	 * <p>
	 * If the attribute didn't exist previously, this method returns directly.
	 * No exception is thrown.
	 * </p>
	 * <p>
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute to test for existence
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 */
	void removeAttribute(String name, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given name to the given value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. 
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>boolean</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource or if the given
	 *                attribute has another kind than <code>boolean</code>
	 */
	void setBooleanAttribute(String name, boolean newValue,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given name to the given value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. 
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>handle</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource or if the given
	 *                attribute has another kind than <code>handle</code>
	 */
	void setHandleAttribute(String name, IRodinElement newValue,
			IProgressMonitor monitor) throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given name to the given value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. 
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>integer</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource or if the given
	 *                attribute has another kind than <code>integer</code>
	 */
	void setIntegerAttribute(String name, int newValue, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given name to the given value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. 
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>long</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource or if the given
	 *                attribute has another kind than <code>long</code>
	 */
	void setLongAttribute(String name, long newValue, IProgressMonitor monitor)
			throws RodinDBException;

	/**
	 * Sets the value of the attribute with the given name to the given value.
	 * <p>
	 * If the specified attribute didn't exist, it is created automatically. 
	 * The file containing this element is opened if it was not already.
	 * </p>
	 * <p>
	 * The attribute name must have been declared with the <code>string</code>
	 * kind.
	 * </p>
	 * 
	 * @param name
	 *            name of the attribute
	 * @param newValue
	 *            value to set
	 * @param monitor
	 *            progress monitor to use for opening the file
	 * @exception RodinDBException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource or if the given
	 *                attribute has another kind than <code>string</code>
	 */
	void setStringAttribute(String name, String newValue,
			IProgressMonitor monitor) throws RodinDBException;

}
