/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public abstract class MoveHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// Get the selection from the current active page.
		ISelection selection = EventBUIPlugin.getActivePage().getSelection();
		
		// If there is no selection then do nothing.
		if (selection == null)
			return null;
		
		// If the selection is not a structured selection then do nothing.
		if (!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection ssel = (IStructuredSelection) selection;

		IRodinElement [] elements = getRodinElements(ssel);
		if (elements == null)
			return "Invalid selected elements";

		// Now, the list of elements should have the same type and has the same
		// parent.
		
		IInternalElement firstElement = (IInternalElement) elements[0];
		IInternalElement lastElement = (IInternalElement) elements[elements.length - 1];
		IRodinElement parent = firstElement.getParent();
		IInternalElementType<?> type = firstElement.getElementType();

		if (parent != null && parent instanceof IInternalElement) {
			try {
				IInternalElement[] children = ((IInternalElement) parent)
						.getChildrenOfType(type);
				assert (children.length > 0);
				IInternalElement prevElement = null;
				for (int i = 0; i < children.length; ++i) {
					if (children[i].equals(firstElement))
						break;
					prevElement = children[i];
				}
				IInternalElement nextElement = null;
				for (int i = children.length - 1; i >= 0; --i) {
					if (children[i].equals(lastElement))
						break;
					nextElement = children[i];
				}
				
				if (getDirection()) {
					if (prevElement != null) {
						prevElement.move(parent, nextElement, null, false,
								new NullProgressMonitor());
					}
				} else {
					if (nextElement != null) {
						nextElement.move(parent, firstElement, null, false,
								new NullProgressMonitor());
					}
				}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	protected abstract boolean getDirection();

	/**
	 * Get the list of Rodin elements contained in the selection. The elements
	 * must have the same parent and must be a consecutive children of the same
	 * type. If not, return <code>null</code>.
	 * 
	 * @param ssel
	 *            a structured selection.
	 * @return a collection of rodin elements or <code>null</code>.
	 */
	private IRodinElement[] getRodinElements(IStructuredSelection ssel) {
		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();
		IParent parent = null;
		IElementType<?> type = null;
		for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (!(obj instanceof IRodinElement))
				return null;
			IRodinElement element = (IRodinElement) obj;
			if (parent == null) {
				parent = (IParent) element.getParent();
				type = element.getElementType();
				elements.add(element);
			} else {
				if (!element.getParent().equals(parent)) {
					return null;
				} else {
					elements.add(element);
				}
			}
		}
		
		// No elements
		if (parent == null)
			return null;
		
		IRodinElement[] array = elements.toArray(new IRodinElement[elements.size()]);
		try {
			IRodinElement [] children = parent.getChildrenOfType(type);
			int i = 0;
			for (i = 0; i < children.length; i++) {
				if (children[i].equals(array[0]))
					break;
			}
			if (i + array.length > children.length)
				return null;
			for (int j = 1; j < array.length; j++) {
				if (!array[j].equals(children[i+j])) {
					return null;
				}
			}
		} catch (RodinDBException e) {
			return null;
		}
		return array;
	}

}
