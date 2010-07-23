/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.Region.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.ArrayList;

import org.rodinp.core.IParent;
import org.rodinp.core.IRegion;
import org.rodinp.core.IRodinElement;

/**
 * @see IRegion
 */

public class Region implements IRegion {

	/**
	 * A collection of the top level elements that have been added to the region
	 */
	protected ArrayList<IRodinElement> rootElements;

	/**
	 * Creates an empty region.
	 * 
	 * @see IRegion
	 */
	public Region() {
		rootElements = new ArrayList<IRodinElement>(1);
	}

	/**
	 * @see IRegion#add(IRodinElement)
	 */
	@Override
	public void add(IRodinElement element) {
		if (! contains(element)) {
			// "new" element added to region
			removeAllChildren(element);
			rootElements.add(element);
			rootElements.trimToSize();
		}
	}

	/**
	 * @see IRegion
	 */
	@Override
	public boolean contains(IRodinElement element) {
		for (IRodinElement root : rootElements) {
			if (root.equals(element) || root.isAncestorOf(element)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see IRegion
	 */
	@Override
	public IRodinElement[] getElements() {
		IRodinElement[] result = new IRodinElement[rootElements.size()];
		return rootElements.toArray(result);
	}

	/**
	 * @see IRegion#remove(IRodinElement)
	 */
	@Override
	public boolean remove(IRodinElement element) {
		removeAllChildren(element);
		return rootElements.remove(element);
	}

	/**
	 * Removes any children of this element that are contained within this
	 * region as this parent is about to be added to the region.
	 * 
	 * <p>
	 * Children are all children, not just direct children.
	 */
	private void removeAllChildren(IRodinElement element) {
		if (element instanceof IParent) {
			ArrayList<IRodinElement> newRootElements = new ArrayList<IRodinElement>();
			for (IRodinElement currentRoot: rootElements) {
				if (! element.equals(currentRoot) && ! element.isAncestorOf(currentRoot)) {
					newRootElements.add(currentRoot);
				}
			}
			rootElements = newRootElements;
		}
	}

	/**
	 * Returns a printable representation of this region.
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		IRodinElement[] roots = getElements();
		buffer.append('[');
		for (int i = 0; i < roots.length; i++) {
			buffer.append(roots[i].getElementName());
			if (i < (roots.length - 1)) {
				buffer.append(", "); //$NON-NLS-1$
			}
		}
		buffer.append(']');
		return buffer.toString();
	}
}
