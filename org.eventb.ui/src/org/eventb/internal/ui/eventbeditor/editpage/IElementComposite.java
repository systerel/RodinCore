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

import org.eclipse.swt.widgets.Composite;
import org.rodinp.core.IElementType;
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
	 *            <code>false</code> if the element is deselected.
	 */
	public void select(IRodinElement element, boolean selected);

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

}
