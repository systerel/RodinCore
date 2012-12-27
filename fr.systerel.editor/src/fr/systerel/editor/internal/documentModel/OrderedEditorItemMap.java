/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.documentModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.emf.api.itf.ILElement;

/**
 * Groups of editor items corresponding to a given element.
 */
public class OrderedEditorItemMap {

	private final Map<FSPAIR<ILElement, IRodinElement>, EditorElement> items = new HashMap<FSPAIR<ILElement, IRodinElement>, EditorElement>();
	private final ArrayList<FSPAIR<ILElement, IRodinElement>> order = new ArrayList<FSPAIR<ILElement, IRodinElement>>();
	
	
	private static class FSPAIR<S extends ILElement, T extends IRodinElement> {

		public S getFirst() {
			return firstElement;
		}

		public T getSecond() {
			return secondElement;
		}

		@Override
		public String toString() {
			return firstElement.getElement().getElementName() + "/" + secondElement.getElementName();
		}

		private S firstElement;
		private T secondElement;

		private FSPAIR(S firstElement, T secondElement) {
			this.firstElement = firstElement;
			this.secondElement = secondElement;
		}
		
		public static FSPAIR<ILElement, IRodinElement> getKey(ILElement element) {
			return new FSPAIR<ILElement, IRodinElement>(element, element.getElement());
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (!(other instanceof FSPAIR))
				return false;
			final FSPAIR<?,?> otherPair = ((FSPAIR<?,?>) other);
			if (secondElement == null && firstElement != null)
				return firstElement == otherPair.getFirst();
			if (firstElement == null && secondElement != null)
				return secondElement.equals(otherPair.getSecond());
			if (firstElement != null && secondElement != null)
				return firstElement == otherPair.getFirst()
						&& secondElement.equals(otherPair.getSecond());
			return super.equals(otherPair);
		}

		@Override
		public int hashCode() {
			if (firstElement == null)
				return super.hashCode();
            return firstElement.hashCode() * 31 + secondElement.hashCode();
		}

	}
	
	public void remove(ILElement element) {
		final FSPAIR<ILElement, IRodinElement> key = FSPAIR.getKey(element);
		items.remove(key);
		order.remove(key);
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
		final IInternalElement internalElement = (IInternalElement) element
				.getElement();
		final FSPAIR<ILElement, IRodinElement> key = new FSPAIR<ILElement, IRodinElement>(element,
				internalElement);
		EditorElement el = items.get(key);
		if (element.isImplicit() && element.getParent() == null)
			System.out.println("STOP!");
		if (el == null) {
			el = new EditorElement(element);
			items.put(key, el);
			order.add(key);
		}
		return el;
	}

	public EditorElement get(ILElement element) {
		return items.get(FSPAIR.getKey(element));
	}
	
	public Collection<EditorElement> getItems() {
		final List<EditorElement> l = new ArrayList<EditorElement>();
		for (FSPAIR<ILElement, IRodinElement> e : order) {
			l.add(items.get(e));
		}
		return l;
	}
	
	public void clear() {
		items.clear();
		order.clear();
	}

	public void remove(EditorElement el) {
		// TODO Auto-generated method stub
		
	}
	
}
