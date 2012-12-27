/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.JavaElementInfo
 *     Systerel - added creation of new internal element child
 *******************************************************************************/
package org.rodinp.internal.core;

import java.util.Collection;

import org.eclipse.core.resources.IResource;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.RodinElement;

/**
 * Holds cached structure and properties for a Rodin element. Subclassed to
 * carry properties for specific kinds of elements.
 */
public class RodinElementInfo {

	/**
	 * Shared empty collection used for efficiency.
	 */
	static IResource[] NO_NON_RODIN_RESOURCES = new IResource[] {};

	/**
	 * Collection of handles of immediate children of this object. This is an
	 * empty array if this element has no children.
	 */
	private RodinElement[] children;

	public RodinElementInfo() {
		this.children = RodinElement.NO_ELEMENTS;
	}

	public void addChild(RodinElement child) {
		if (this.children == RodinElement.NO_ELEMENTS) {
			this.children = new RodinElement[] { child };
			childAdded(child);
		} else {
			if (!includesChild(child)) {
				this.children = growAndAddToArray(this.children, child);
				childAdded(child);
			}
		}
	}

	public void addChildBefore(RodinElement child, RodinElement nextSibling) {
		if (nextSibling == null) {
			addChild(child);
		} else {
			final int idx = getChildIndex(nextSibling);
			final int length = children.length;
			final RodinElement[] array = new RodinElement[length + 1];
			System.arraycopy(children, 0, array, 0, idx);
			array[idx] = child;
			System.arraycopy(children, idx, array, idx+1, length-idx);
			this.children = array;
			childAdded(child);
		}
	}

	public void changeChild(RodinElement source, RodinElement dest) {
		final int idx = getChildIndex(source);
		children[idx] = dest;
		childAdded(dest);
	}

	public boolean containsChild(RodinElement element) {
		RodinElement[] cachedChildren = children;
		for (RodinElement child: cachedChildren) {
			if (child.equals(element))
				return true;
		}
		return false;
	}
	
	private int getChildIndex(RodinElement element) {
		final int length = children.length;
		for (int i = 0; i < length; ++i) {
			if (element.equals(children[i]))
				return i;
		}
		return -1;
	}
	
	public RodinElement[] getChildren() {
		return this.children;
	}

	/**
	 * Adds the new element to a new array that contains all of the elements of
	 * the old array. Returns the new array.
	 */
	protected RodinElement[] growAndAddToArray(RodinElement[] array,
			RodinElement addition) {
		RodinElement[] old = array;
		array = new RodinElement[old.length + 1];
		System.arraycopy(old, 0, array, 0, old.length);
		array[old.length] = addition;
		return array;
	}

	/**
	 * Returns <code>true</code> if this child is in my children collection
	 */
	protected boolean includesChild(RodinElement childToLookup) {
		for (RodinElement child: this.children) {
			if (child.equals(childToLookup)) {
				return true;
			}
		}
		return false;
	}

	public void moveChildBefore(RodinElement child, RodinElement nextSibling) {
		final int childIdx = getChildIndex(child);
		final int nextIdx;
		if (nextSibling == null) {
			nextIdx = children.length;
		} else {
			nextIdx = getChildIndex(nextSibling);
		}

		if (childIdx < nextIdx) {
			final int sublen = nextIdx-childIdx-1;
			System.arraycopy(children, childIdx+1, children, childIdx, sublen);
			children[nextIdx-1] = child;
		} else {
			final int sublen = childIdx-nextIdx;
			System.arraycopy(children, nextIdx, children, nextIdx+1, sublen);
			children[nextIdx] = child;
		}
	}
	
	/**
	 * Returns an array with all the same elements as the specified array except
	 * for the element to remove. Assumes that the deletion is contained in the
	 * array.
	 */
	protected RodinElement[] removeAndShrinkArray(RodinElement[] array,
			RodinElement deletion) {
		RodinElement[] old = array;
		array = new RodinElement[old.length - 1];
		int j = 0;
		for (int i = 0; i < old.length; i++) {
			if (!old[i].equals(deletion)) {
				array[j] = old[i];
			} else {
				System.arraycopy(old, i + 1, array, j, old.length - (i + 1));
				return array;
			}
			j++;
		}
		return array;
	}

	public void removeChild(RodinElement child) {
		if (includesChild(child)) {
			this.children = removeAndShrinkArray(this.children, child);
		}
	}

	public void setChildren(RodinElement[] children) {
		this.children = children;
		childrenSet();
	}

	public void setChildren(Collection<? extends IRodinElement> children) {
		final int length = children.size();
		if (length == 0) {
			this.children = RodinElement.NO_ELEMENTS;
		} else {
			this.children = children.toArray(new RodinElement[length]);
		}
		childrenSet();
	}

	protected void childrenSet() {
		// do nothing by default
	}
	
	protected void childAdded(RodinElement newChild) {
		// do nothing by default
	}

}
