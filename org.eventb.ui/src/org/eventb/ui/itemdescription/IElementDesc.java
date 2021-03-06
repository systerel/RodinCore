/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.itemdescription;

import org.eventb.ui.IImplicitChildProvider;
import org.rodinp.core.IInternalElementType;

/**
 * Common protocol for describing how to render elements in the Event-B UI.
 * 
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IElementDesc {

	/**
	 * Tells whether this element UI description is a valid registered element
	 * description, or if it corresponds to a <code>null</code> instance.
	 * 
	 * @return <code>true</code> is this description is a valid registered UI
	 *         description, <code>false</code> otherwise.
	 */
	boolean isValid();

	/**
	 * Get the attribute description given its <code>id</code>. Returns
	 * <code>null</code> if no such attribute type can be carried by the element
	 * considered by the current element description, or if there is no
	 * attribute description for it.
	 * 
	 * @param attributeTypeId
	 *            the id of the attribute type
	 * @return the UI description of the attribute, or <code>null</code> if such
	 *         an attribute is not allowed or is not described.
	 */
	IAttributeDesc getAttributeDescription(String attributeTypeId);

	/**
	 * Return an array of descriptions which correspond to all attributes which
	 * the element of this description can carry.
	 * 
	 * @return an array of {@link IAttributeDesc}. This must not be
	 *         <code>null</code>.
	 * 
	 */
	IAttributeDesc[] getAttributeDescriptions();

	/**
	 * Get the description corresponding to the attribute which cares about the
	 * automatic naming of the element.
	 * 
	 * @return the attribute description of the automatic naming attribute
	 */
	IAttributeDesc getAutoNameAttribute();

	/**
	 * Return the automatic name prefix.
	 * 
	 * @return the string prefix for the automatic naming of the element
	 */
	String getAutoNamePrefix();

	/**
	 * Return the prefix that shall be displayed before the first child of the
	 * element corresponding to this description which has the given type, or
	 * the default prefix for the current element if the given parameter type
	 * <code>childType</code> is <code>null</code>.
	 * 
	 * @return the prefix that shall be displayed before the first child of the
	 *         element with <code>childType</code> type, or the default prefix
	 *         for the element if <code>childType</code> is <code>null</code>
	 */
	String getPrefix(IInternalElementType<?> childType);

	/**
	 * Return the suffix that shall be display after the last child of the
	 * element corresponding to this description.
	 * 
	 * @return suffix that shall be display after the last child of the element
	 */
	String getChildrenSuffix();

	/**
	 * Return the ordered list of child element types of the element type
	 * concerned by this description.
	 * 
	 * @return the ordered list of element types which are in a child
	 *         relationship with the element type of the current element
	 *         description
	 */
	public IInternalElementType<?>[] getChildTypes();

	/**
	 * Return the element type concerned by this description or
	 * <code>null</code> if this description does not correspond to an internal
	 * element.
	 * 
	 * @return the element type concerned by this description or
	 *         <code>null</code> if this description does not correspond to an
	 *         internal element.
	 */
	public IInternalElementType<?> getElementType();

	/**
	 * Returns the implicit child provider for the given child type. If no
	 * provider was contributed to the <code>editorItems</code> extension point,
	 * then a default implementation that provides no child is returned.
	 * 
	 * @return the provider for implicit children of the given type
	 */
	public IImplicitChildProvider getImplicitChildProvider(
			IInternalElementType<?> childType);

}
