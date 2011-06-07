/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import static fr.systerel.editor.internal.documentModel.DocumentElementUtils.getSibling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

/**
 * Groups of editor items corresponding to a given element.
 */
public class OrderedEditorItemMap {

	private final Map<IInternalElement, EditorElement> items = new HashMap<IInternalElement, EditorElement>();
	private final ArrayList<IInternalElement> order = new ArrayList<IInternalElement>();
	
	public EditorElement addItem(ILElement element) {
		final ILElement sibling = getSibling(element);
		// there is no sibling or no sibling found
		if (sibling == null || items.get(sibling) == null) {
			return getOrCreate(element);
		}
		int pos = -1;
		for (int i = 0; i < order.size(); i++) {
			if (order.get(i) == sibling.getElement()) {
				pos = i;
				break;
			}
		}
		if (pos == -1) {
			throw new IllegalStateException("Sibling should have a position");
		}
		final EditorElement el = new EditorElement(element);
		final IInternalElement internalElement = element.getElement();
		items.put(internalElement, el);
		order.add(pos, internalElement);
		return el;
	}
	
	public void remove(IInternalElement element) {
		items.remove(element);
		order.remove(element);
	}

	/**
	 * Returns the registered editor item or creates a new one at the end of the
	 * list
	 * 
	 * @param element
	 *            the ILElement to search for editor item
	 * @return the editor item associated with the given element or a newly
	 *         added one
	 */
	public EditorElement getOrCreate(ILElement element) {
		final IInternalElement internalElement = (IInternalElement) element.getElement();
		EditorElement el = items.get(internalElement);
		if (el == null) {
			el = new EditorElement(element);
			items.put(internalElement, el);
			order.add(internalElement);
		}
		return el;
	}
	
	public EditorElement get(IRodinElement element) {
		return items.get(element);
	}
	
	public Collection<EditorElement> getItems() {
		return items.values();
	}
	
	public void clear() {
		items.clear();
		order.clear();
	}
	
}
