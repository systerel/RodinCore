/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * Strongly inspired by org.eclipse.jdt.internal.core.JavaElementInfo.java which is
 * 
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResource;
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
			setChildren(new RodinElement[] { child });
		} else {
			if (!includesChild(child)) {
				setChildren(growAndAddToArray(this.children, child));
			}
		}
	}

	public void addChildBefore(RodinElement child, RodinElement sibling) {
		if (sibling == null) {
			addChild(child);
			return;
		}
		int length = children.length;
		for (int i = 0; i < length; ++i) {
			if (children[i].equals(sibling)) {
				RodinElement[] newChildren = new RodinElement[length + 1];
				System.arraycopy(children, 0, newChildren, 0, i);
				newChildren[i] = child;
				System.arraycopy(children, i, newChildren, i+1, length - i);
				children = newChildren;
				return;
			}
		}
	}

	public RodinElement[] getChildren() {
		return this.children;
	}

	private int getIndex(RodinElement child) {
		final int length = children.length;
		for (int i = 0; i < length; i++) {
			if (child.equals(children[i])) {
				return i;
			}
		}
		return length;
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
			setChildren(removeAndShrinkArray(this.children, child));
		}
	}

	// Returns true if a change was made to the children order.
	public boolean reorderBefore(RodinElement child, RodinElement nextSibling) {
		int srcIndex = getIndex(child);
		int dstIndex;
		if (nextSibling == null) {
			dstIndex = children.length;
		} else {
			dstIndex = getIndex(nextSibling);
		}
		if (srcIndex == dstIndex || srcIndex == dstIndex - 1) {
			return false;
		}

		if (srcIndex < dstIndex) {
			-- dstIndex;
			System.arraycopy(children, srcIndex + 1, children, srcIndex, dstIndex - srcIndex);
		} else {
			System.arraycopy(children, dstIndex, children, dstIndex + 1, srcIndex - dstIndex);
		}
		children[dstIndex] = child;
		return true;
	}

	public void replaceChild(RodinElement source, RodinElement dest) {
		for (int i = 0; i < children.length; i++) {
			if (source.equals(children[i])) {
				children[i] = dest;
				return;
			}
		}
	}

	public void setChildren(RodinElement[] children) {
		this.children = children;
	}
}
