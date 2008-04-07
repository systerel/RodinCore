/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import org.eclipse.core.runtime.CoreException;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

/**
 * @author htson
 *         <p>
 *         The interface for Attribute relationship UI specification registry.
 *         This registry stores information how element's attributes are
 *         displayed and/or edited.
 *         </p>
 *         <p>
 *         This interface is NOT intended to be implemented by clients. Instead,
 *         clients should get the default instance of this through
 *         {@link EventBUIPlugin}.
 *         </p>
 */
public interface IAttributeRelUISpecRegistry {

	/**
	 * Create an internal element given its parent and its type, along with the
	 * possible next sibling. The default value for its attribute is also
	 * created if applicable.
	 * 
	 * @param editor
	 *            an Event-B Editor
	 * @param parent
	 *            the internal parent of the new element
	 * @param type
	 *            the type of the new element
	 * @param sibling
	 *            the next sibling of the new element. The element is added to
	 *            the end of its parent children list if this is
	 *            <code>null</code>.
	 * @return the newly created element
	 * @throws CoreException
	 *             if some problems occurred.
	 */
	public abstract <T extends IInternalElement> T createElement(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> type, IInternalElement sibling)
			throws CoreException;

	/**
	 * Get the number of attributes for an element type.
	 * 
	 * @param type
	 *            an element type
	 * @return the number of attributes for an element type.
	 */
	public abstract int getNumberOfAttributes(IElementType<?> type);

	/**
	 * Get the list of edit composites for an element type. The number of edit
	 * composites is the same as the number of attributes.
	 * 
	 * @param type
	 *            an element type
	 * @return the list of edit composites for attributes of element with the
	 *         input element type.
	 */
	public abstract IEditComposite[] getEditComposites(IElementType<?> type);

	/**
	 * Return the default prefix for the input attribute
	 * 
	 * @param attributeID
	 *            the string ID represent the attribute
	 * @return the string represent the default prefix for this attribute or
	 *         <code>null</code> if none is defined.
	 */
	public abstract String getDefaultPrefix(String attributeID);

	/**
	 * Return the corresponding type for the input attribute.
	 * 
	 * @param attributeID
	 *            the string ID represent the attribute
	 * @return the corresponding internal element type or
	 *         <code>null</code> if none is defined.
	 */
	public abstract IInternalElementType<?> getType(String attributeID);

}