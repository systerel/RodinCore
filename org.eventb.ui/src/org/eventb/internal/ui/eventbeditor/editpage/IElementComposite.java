/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - fixed expanding
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.editpage;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         Interface for composite for editing an element. Each element
 *         composite contains a Edit Row {@link EditRow} for the element and a
 *         list of Edit Sections {@link ISectionComposite} for children of this
 *         element.
 *         </p>
 *         <p>
 *         This interface is NOT intended for clients to implement.
 *         </p>
 */
public interface IElementComposite {

	/**
	 * Return the corresponding Edit Page.
	 * 
	 * @return an Edit Page
	 */
	public EditPage getPage();

	/**
	 * Collapse the edit composite, i.e. hide the children of this composite.
	 */
	public void folding();

	/**
	 * Dispose this element composite. This is done recursively, i.e. the
	 * children is disposed first, then this parent itself.
	 */
	public void dispose();

	/**
	 * Return the element associated with this element composite.
	 * 
	 * @return the associated Rodin element.
	 */
	public IRodinElement getElement();

	/**
	 * Check to see if the element composite is expanded or collapsed.
	 * 
	 * @return <code>true</code> if the composite is expanded. Return
	 *         <code>false</code> if it is collapsed.
	 */
	public boolean isExpanded();

	/**
	 * Get the main composite widget.
	 * 
	 * @return the composite widget of this element composite.
	 */
	public Composite getComposite();

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
	public boolean select(IRodinElement element, boolean selected);

	/**
	 * Recursively expand the editing composite for the input element
	 * 
	 * @param element
	 *            a Rodin element.
	 */
	public void recursiveExpand(IRodinElement element);

	/**
	 * Refresh the information display in the composite.
	 * 
	 * @param element a Rodin element
	 */
	// TODO Check if the element is really necessary.
	public void refresh(IRodinElement element);

	/**
	 * Refresh the edit composite recursively when the input element is removed.
	 * 
	 * @param element
	 *            a Rodin element.
	 */
	public void elementRemoved(IRodinElement element);

	/**
	 * Refresh the edit composite recursively when the input element is added.
	 * 
	 * @param element
	 *            a Rodin element.
	 */
	public void elementAdded(IRodinElement element);

	/**
	 * Refresh the edit composite recursively when there are some changes for
	 * children of input type of the input element.
	 * 
	 * @param element
	 *            a Rodin element.
	 * @param type
	 *            a Rodin element type.
	 */
	public void childrenChanged(IRodinElement element, IElementType<?> type);

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
	public void edit(IInternalElement element, IAttributeType attributeType,
			int charStart, int charEnd);

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
	public void refresh(IRodinElement element, Set<IAttributeType> set);

	/**
	 * Set the expand status of the element composite.
	 * 
	 * @param isExpanded
	 *            <code>true</code> if the element composite is going to expand,
	 *            <code>false</code> otherwise.
	 * @param recursive
	 *            <code>true</code> to propagate expand state recursively
	 */
	public void setExpand(boolean isExpanded, boolean recursive);
	
	/**
	 * Returns the instance of IElementDescRegistry used.
	 * 
	 * @return an instance of IElementDescRegistry
	 */
	public IElementDescRegistry getElemDescRegistry();
	
}
