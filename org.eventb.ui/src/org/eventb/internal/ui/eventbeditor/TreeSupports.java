/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This is an utility class supporting some operations for Editable Tree
 *         Viewer.
 */
public class TreeSupports {

	/**
	 * Get the event parent of an element in the tree.
	 * <p>
	 * 
	 * @param obj
	 *            an object
	 * @return the Event (Rodin element) which contains the object (directly or
	 *         indirectly)
	 */
	public static IInternalElement getEvent(Object obj) {
		if (obj instanceof IRodinElement) {
			IRodinElement rodinElement = (IRodinElement) obj;
			if (rodinElement instanceof IEvent) {
				return (IEvent) rodinElement;
			} else if (rodinElement instanceof IInternalElement) {
				return getEvent(((IInternalElement) rodinElement).getParent());
			} else
				return null; // should not happen
		} else
			return null; // should not happen
	}

	/**
	 * Find a particular element in the tree.
	 * <p>
	 * 
	 * @param tree
	 *            a tree
	 * @param element
	 *            a Rodin element
	 * @return The TreeItem corresponding to the input Rodin element or
	 *         <code>null</code>
	 */
	public static TreeItem findItem(Tree tree, IRodinElement element) {
		TreeItem[] items = tree.getItems();
		for (TreeItem item : items) {
			TreeItem temp = findItem(item, element);
			if (temp != null)
				return temp;
		}
		return null;
	}

	/**
	 * Utility method for finding element recursively.
	 * <p>
	 * 
	 * @param item
	 *            an TreeItem
	 * @param element
	 *            a RodinElement
	 * @return a TreeItem which is below the input item corresponding to the
	 *         input Rodin element or <code>null</code>
	 */
	private static TreeItem findItem(TreeItem item, IRodinElement element) {
		// UIUtils.debug("From " + item);
		Object object = item.getData();
		if (object == null)
			return null;
		if (object.equals(element)) {
			return item;
		} else {
			// UIUtils.debug("Recursively ...");
			TreeItem[] items = item.getItems();
			for (TreeItem i : items) {
				TreeItem temp = findItem(i, element);
				if (temp != null)
					return temp;
			}
		}
		// UIUtils.debug("... Not found");
		return null;
	}

	/**
	 * Getting the index of a TreeItem in a list of them.
	 * <p>
	 * 
	 * @param items
	 *            list of TreeItem
	 * @param item
	 *            a TreeItem
	 * @return the index of the tree item in the list or <code>-1</code>
	 */
	public static int getIndex(TreeItem[] items, TreeItem item) {
		for (int i = 0; i < items.length; i++) {
			if (item == items[i])
				return i;
		}
		return -1;
	}

	/**
	 * Find the previous TreeItem in tree.
	 * <p>
	 * 
	 * @param tree
	 *            a tree
	 * @param item
	 *            a TreeItem
	 * @return the previous TreeItem or <code>null</code>
	 */
	public static TreeItem findPrevItem(Tree tree, TreeItem item) {
		TreeItem parent = item.getParentItem();
		int index = 0;
		if (parent == null) {
			index = TreeSupports.getIndex(tree.getItems(), item);
			if (index != 0) {
				return tree.getItem(index - 1);
			}
		} else {
			index = TreeSupports.getIndex(parent.getItems(), item);
			if (index != 0) {
				return parent.getItem(index - 1);
			}
		}

		return null;
	}

	/**
	 * Find the next TreeItem in a tree.
	 * <p>
	 * 
	 * @param tree
	 *            a tree
	 * @param item
	 *            a TreeItem
	 * @return the next TreeItem or <code>null</code>
	 */
	public static TreeItem findNextItem(Tree tree, TreeItem item) {
		TreeItem parent = item.getParentItem();
		int index = 0;
		if (parent == null) {
			index = TreeSupports.getIndex(tree.getItems(), item);
			if (index != tree.getItemCount() - 1)
				return tree.getItem(index + 1);
		} else {
			index = TreeSupports.getIndex(parent.getItems(), item);
			if (index != parent.getItemCount() - 1)
				return parent.getItem(index + 1);

		}

		return null;
	}

}
