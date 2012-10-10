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
 * Element and attribute relationships are defined using
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
	 * Returns the parent types that this element type is in child relationship
	 * with.
	 */
	IInternalElementType<?>[] getParentTypes();

	/**
	 * Returns the child types to which this element type is in parent
	 * relationship with.
	 */
	IInternalElementType<?>[] getChildTypes();

	/**
	 * Returns all attribute types that this element type can carry.
	 */
	IAttributeType[] getAttributeTypes();

	/**
	 * Tells if this type can be a parent type of the given child element type.
	 * 
	 * @param childType
	 *            the child type
	 * @return <code>true</code> if there exists a parent-child relationship
	 *         between the current type and the given element type,
	 *         <code>false</code> otherwise
	 */
	boolean canParent(IInternalElementType<?> childType);

	/**
	 * Tells if the current element can carry the given attribute type.
	 * 
	 * @param attributeType
	 *            the attribute type
	 * @return <code>true</code> if there exists a parent-child relationship
	 *         between the current type and the given attribute type,
	 *         <code>false</code> otherwise
	 */
	boolean isElementOf(IAttributeType attributeType);

}
