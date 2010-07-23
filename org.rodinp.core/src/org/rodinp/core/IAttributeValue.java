/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for manipulating attribute values in a generic way.
 * <p>
 * Attributes are attached to internal elements (that is elements that are
 * stored in a file element) and provide non-structural information pertaining
 * to that element (for instance the target of a refines clause).
 * </p>
 * <p>
 * Every attribute value contains an attribute type and an object specifying the
 * actual value. Attribute values are immutable objects. The correspondence
 * between the attribute type and the actual class of the value instance is
 * given by the following table.
 * <table>
 * <tr>
 * <th>Attribute type</th>
 * <th>Value type</th>
 * </tr>
 * <tr>
 * <td>{@link IAttributeType.Boolean}</td>
 * <td>{@link java.lang.Boolean}</td>
 * </tr>
 * <tr>
 * <td>{@link IAttributeType.Handle}</td>
 * <td>{@link IRodinElement}</td>
 * </tr>
 * <tr>
 * <td>{@link IAttributeType.Integer}</td>
 * <td>{@link java.lang.Integer}</td>
 * </tr>
 * <tr>
 * <td>{@link IAttributeType.Long}</td>
 * <td>{@link java.lang.Long}</td>
 * </tr>
 * <tr>
 * <td>{@link IAttributeType.String}</td>
 * <td>{@link java.lang.String}</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Instances of attribute values can be created either from their corresponding
 * type using methods <code>makeValue()</code> or obtained from the database
 * using {@link IInternalElement#getAttributeValues()}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * 
 * @see IAttributeType.Boolean#makeValue(boolean)
 * @see IAttributeType.Handle#makeValue(IRodinElement)
 * @see IAttributeType.Integer#makeValue(int)
 * @see IAttributeType.Long#makeValue(long)
 * @see IAttributeType.String#makeValue(java.lang.String)
 * @see IInternalElement#getAttributeValues()
 * @see IInternalElement#setAttributeValue(IAttributeValue, IProgressMonitor)
 * @since 1.0
 */
public interface IAttributeValue {

	/**
	 * Common protocol for boolean attribute values.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 */
	interface Boolean extends IAttributeValue {

		@Override
		IAttributeType.Boolean getType();

		@Override
		java.lang.Boolean getValue();

	}

	/**
	 * Common protocol for handle attribute values.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 */
	interface Handle extends IAttributeValue {

		@Override
		IAttributeType.Handle getType();

		@Override
		IRodinElement getValue();

	}

	/**
	 * Common protocol for integer attribute values.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 */
	interface Integer extends IAttributeValue {

		@Override
		IAttributeType.Integer getType();

		@Override
		java.lang.Integer getValue();

	}

	/**
	 * Common protocol for long integer attribute values.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 */
	interface Long extends IAttributeValue {

		@Override
		IAttributeType.Long getType();

		@Override
		java.lang.Long getValue();

	}

	/**
	 * Common protocol for string attribute values.
	 * <p>
	 * This interface is not intended to be implemented by clients.
	 * </p>
	 */
	interface String extends IAttributeValue {

		@Override
		IAttributeType.String getType();

		@Override
		java.lang.String getValue();

	}

	/**
	 * Returns the type of this attribute value.
	 * 
	 * @return the type of this attribute value
	 */
	IAttributeType getType();

	/**
	 * Returns the value of this attribute value.
	 * 
	 * @return the value of this attribute value
	 */
	Object getValue();

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * Two attribute values are considered equal iff they contain the same
	 * attribute type and their values are equal.
	 * </p>
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> iff this object and the given object are equal
	 *         attribute values
	 * @see Object#equals(Object)
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * Returns a hash code value for this attribute value, compatible with
	 * equality as defined by {@link #equals(Object)}.
	 * 
	 * @return a hash code value for this attribute value
	 */
	@Override
	int hashCode();

}
