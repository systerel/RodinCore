/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - generic attribute manipulation
 *******************************************************************************/
package org.rodinp.internal.core.relations.api;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for all attribute types contributed by clients.
 * <p>
 * Attributes are attached to internal elements (that is elements that are
 * stored in a file element) and provide non-structural information pertaining
 * to that element (for instance the target of a refines clause).
 * </p>
 * <p>
 * Every attribute is associated with an attribute type, which contains the
 * following information:
 * <ul>
 * <li>the id of the attribute type (which is unique),</li>
 * <li>the human-readable name of the attribute type,</li>
 * <li>the kind of the attribute, that is the Java type of the associated
 * attribute values.</li>
 * </ul>
 * The correspondence between attribute values and kinds is the following:
 * <ul>
 * <li><code>boolean</code>: {@link IAttributeType.Boolean}</li>
 * <li><code>IRodinElement</code>: {@link IAttributeType.Handle}</li>
 * <li><code>int</code>: {@link IAttributeType.Integer}</li>
 * <li><code>long</code>: {@link IAttributeType.Long}</li>
 * <li><code>String</code>: {@link IAttributeType.String}</li>
 * </ul>
 * </p>
 * <p>
 * Attribute type instances are guaranteed to be unique. Hence, two attribute
 * types can be compared directly using identity (<code>==</code>). Instances
 * can be obtained using the static factory methods from <code>RodinCore</code>.
 * </p>
 * <p>
 * Moreover, this interface provides a protocol to retrieve and check the
 * relationships existing between attribute and elements types.
 * </p>
 * <p>
 * One can retrieve the element type instances that can carry the manipulated
 * attribute type by using the <code>getElementTypes()</code> method.
 * </p>
 * <p>
 * One can also verify that the manipulated attribute type can be carried by a
 * given element type using <code>isAttributeOf()</code>.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @author Laurent Voisin
 * 
 * @see RodinCore#getAttributeType(java.lang.String)
 * @see RodinCore#getBooleanAttrType(java.lang.String)
 * @see RodinCore#getHandleAttrType(java.lang.String)
 * @see RodinCore#getIntegerAttrType(java.lang.String)
 * @see RodinCore#getLongAttrType(java.lang.String)
 * @see RodinCore#getStringAttrType(java.lang.String)
 * @since 1.0
 */
public interface IAttributeType2 extends IAttributeType {

	/**
	 * Returns the types of elements that can carry this attribute type.
	 */
	IInternalElementType<?>[] getElementTypes();

	/**
	 * Tells whether this attribute type can be carried by an element of the
	 * given type.
	 * 
	 * @param elementType
	 *            an element type
	 * @return <code>true</code> iff this attribute type can be carried by an
	 *         element of the given type
	 */
	boolean isAttributeOf(IInternalElementType<?> elementType);

}
