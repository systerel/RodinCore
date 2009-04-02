/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added method for marker refresh
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Set;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         Interface for composite for editing children of the same type of an
 *         element. Each section composite contains a list of Element Composite
 *         {@link IElementComposite}, each Element Composite corresponds to a
 *         child.
 *         </p>
 *         <p>
 *         This interface is NOT intended for clients to implement.
 *         </p>
 */
public interface ISectionComposite {

	/**
	 * Dispose this section composite. This is done recursively, i.e. the
	 * Element Composite children is disposed first, then this Section Composite
	 * parent itself.
	 */
	void dispose();

	/**
	 * Get the element type (the type of the children) of this section
	 * composite.
	 * 
	 * @return the element type for the children elements.
	 */
	IElementType<?> getElementType();

	/**
	 * Refresh the information display in the composite.
	 * 
	 * @param element a Rodin element
	 */
	// TODO Check if the element is really necessary.
	void refresh(IRodinElement element);

	/**
	 * Refresh the edit composite recursively when the input element is removed.
	 * 
	 * @param element
	 *            a Rodin element.
	 */
	void elementRemoved(IRodinElement element);

	/**
	 * Refresh the edit composite recursively when the input element is added.
	 * 
	 * @param element
	 *            a Rodin element.
	 */
	void elementAdded(IRodinElement element);

	/**
	 * Refresh the edit composite recursively when there are some changes for
	 * children of input type of the input element.
	 * 
	 * @param element
	 *            a Rodin element.
	 * @param type
	 *            a Rodin element type.
	 */
	void childrenChanged(IRodinElement element,
			IElementType<?> type);

	/**
	 * Select / Deselect an Rodin element.
	 * 
	 * @param element
	 *            a Rodin element
	 * @param selected
	 *            <code>true</code> if the element is selected.
	 *            <code>false</code> if the element is de-selected.
	 * @return <code>true</code> if the operation is successful, i.e. if the
	 *         element is indeed selected or de-selected accordingly to the
	 *         input <code>selected</code>value. Return <code>false</code>
	 *         otherwise.
	 */
	boolean select(IRodinElement element, boolean selected);

	/**
	 * Recursively expand the editing composite for the input element
	 * 
	 * @param element
	 *            a Rodin element.
	 */
	void recursiveExpand(IRodinElement element);

	/**
	 * Edit the element for a certain attribute given the attributeType, the
	 * start and end positions.
	 * 
	 * @param element
	 *            an internal element.
	 * @param attributeType
	 *            an attribute type {@link IAttributeType}.
	 * @param charStart
	 *            start position.
	 * @param charEnd
	 *            end position.
	 */
	void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd);

	/**
	 * Call the method before refresh the section several times.
	 * */
	public void disableMarkerRefresh();
	
	/**
	 * Call the method after refresh the section several times.
	 * */
	public void enableMarkerRefresh();
	
	/**
	 * Refresh the information display in the composite if there are some
	 * changes for attribute of input type within the input element.
	 * 
	 * @param element
	 *            a Rodin element.
	 * @param set
	 *            a set of attribute types, must not be <code>null</code>.
	 *            When this is an empty set, the operation is the same as
	 *            {@link #refresh(IRodinElement)}
	 */
	void refresh(IRodinElement element, Set<IAttributeType> set);

	/**
	 * Recursively extend the section composite.
	 */
	void recursiveExpand();

	/**
	 * Recursively collapse the section composite.
	 */
	void recursiveCollapse();

}
