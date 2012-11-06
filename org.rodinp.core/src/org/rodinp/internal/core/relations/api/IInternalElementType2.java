/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added item relations API methods
 *******************************************************************************/

package org.rodinp.internal.core.relations.api;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for defining and traversing internal element types and
 * attribute types. Element types are the types associated to Rodin internal
 * elements. These types are contributed to the Rodin database through extension
 * point <code>org.rodinp.core.internalElementTypes</code>.
 * <p>
 * This interface also allows to retrieve the possible relationships between
 * internal elements (child-parent) and between elements and attributes. These
 * relationships are defined through the
 * <code>org.rodinp.core.itemRelations</code> extension point.
 * </p>
 * <p>
 * Element type instances are guaranteed to be unique. Hence, two element types
 * can be compared directly using identity (<code>==</code>). Instances can be
 * obtained using the static factory method
 * <code>RodinCore.getInternalElementType()</code>.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @author Laurent Voisin
 * @see RodinCore#getInternalElementType(String)
 * @since 1.0
 */
public interface IInternalElementType2<T extends IInternalElement> extends
		IInternalElementType<T> {

	/**
	 * Returns the types of the internal elements that can parent an element of
	 * this type.
	 * 
	 * @return the types of the internal elements that can parent an element of
	 *         this type
	 */
	IInternalElementType<?>[] getParentTypes();

	/**
	 * Returns the types of the internal elements that can occur as children of
	 * an internal element of this type.
	 * 
	 * @return the types of the internal elements that can occur as children of
	 *         an internal element of this type
	 */
	IInternalElementType<?>[] getChildTypes();

	/**
	 * Returns the types of the attributes that internal elements of this type
	 * can carry.
	 * 
	 * @return the types of the attributes that internal elements of this type
	 *         can carry
	 */
	IAttributeType[] getAttributeTypes();

	/**
	 * Tells whether an internal element of this type can parent an internal
	 * element of the given type.
	 * 
	 * @param childType
	 *            an internal element type
	 * @return <code>true</code> iff an internal element of this type can parent
	 *         an internal element of the given type
	 */
	boolean canParent(IInternalElementType<?> childType);

	/**
	 * Tells whether an internal element of this type can carry an attribute of
	 * the given type.
	 * 
	 * @param attributeType
	 *            an attribute type
	 * @return <code>true</code> iff an internal element of this type can carry
	 *         an attribute of the given type
	 */
	boolean canCarry(IAttributeType attributeType);

}
