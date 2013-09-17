/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - redesign of symbol table
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

/**
 * Protocol for managing attributes of symbols.
 * <p>
 * This is a simplified variant of {@link org.rodinp.core.IInternalElement}
 * </p>
 *
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 * @since 1.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAttributedSymbol {

	/**
	 * Returns an array of the types of all attributes currently attached to
	 * this element. If this element doesn't carry any attribute, an empty array
	 * is returned.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @return the types of all attributes of this element
	 */
	IAttributeType[] getAttributeTypes();

	/**
	 * Returns the value of the attribute with the given boolean type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @return the value of the attribute with the given type
	 * @throws IllegalArgumentException
	 *             if no attribute value is stored for the attribute type
	 */
	boolean getAttributeValue(IAttributeType.Boolean type);

	/**
	 * Returns the value of the attribute with the given handle type and carried
	 * by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @return the value of the attribute with the given type
	 * @throws IllegalArgumentException
	 *             if no attribute value is stored for the attribute type
	 */
	IRodinElement getAttributeValue(IAttributeType.Handle type);

	/**
	 * Returns the value of the attribute with the given integer type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @return the value of the attribute with the given type
	 * @throws IllegalArgumentException
	 *             if no attribute value is stored for the attribute type
	 */
	int getAttributeValue(IAttributeType.Integer type);

	/**
	 * Returns the value of the attribute with the given long integer type and
	 * carried by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @return the value of the attribute with the given type
	 * @throws IllegalArgumentException
	 *             if no attribute value is stored for the attribute type
	 */
	long getAttributeValue(IAttributeType.Long type);

	/**
	 * Returns the value of the attribute with the given string type and carried
	 * by this element.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @return the value of the attribute with the given type
	 * @throws IllegalArgumentException
	 *             if no attribute value is stored for the attribute type
	 */
	String getAttributeValue(IAttributeType.String type);

	/**
	 * Tells whether this element carries an attribute with the given type.
	 * <p>
	 * The file containing this element is opened by this operation.
	 * </p>
	 * 
	 * @param type
	 *            type of the attribute
	 * @return <code>true</code> iff this element carries an attribute with the
	 *         given type
	 */
	boolean hasAttribute(IAttributeType type);

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
	 * @throws IllegalArgumentException
	 *             if the attribute value for the attribute type cannot be set
	 */
	void setAttributeValue(IAttributeType.Boolean type, boolean newValue);

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
	 * @throws IllegalArgumentException
	 *             if the attribute value for the attribute type cannot be set
	 */
	void setAttributeValue(IAttributeType.Handle type, IRodinElement newValue);

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
	 * @throws IllegalArgumentException
	 *             if the attribute value for the attribute type cannot be set
	 */
	void setAttributeValue(IAttributeType.Integer type, int newValue);

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
	 * @throws IllegalArgumentException
	 *             if the attribute value for the attribute type cannot be set
	 */
	void setAttributeValue(IAttributeType.Long type, long newValue);

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
	 * @throws IllegalArgumentException
	 *             if the attribute value for the attribute type cannot be set
	 */
	void setAttributeValue(IAttributeType.String type, String newValue);

}
