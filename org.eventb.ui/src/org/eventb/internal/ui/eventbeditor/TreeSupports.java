package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class TreeSupports {

	public static IInternalElement getEvent(Object obj) {
		if (obj instanceof IRodinElement) {
			IRodinElement rodinElement = (IRodinElement) obj;
			if (rodinElement instanceof IEvent) {
				return (IEvent) rodinElement;
			}
			else if (rodinElement instanceof IInternalElement) {
				return getEvent(((IInternalElement) rodinElement).getParent());
			}
			else return null; // should not happen
		}
		else return null; // should not happen
	}

	public static TreeItem findItem(Tree tree, IRodinElement element) {
		TreeItem [] items = tree.getItems();
		for (TreeItem item : items) {
			TreeItem temp = findItem(item, element);
			if (temp != null) return temp;
		}
		return null;
	}
	
	private static TreeItem findItem(TreeItem item, IRodinElement element) {
//		UIUtils.debug("From " + item);
		Object object = item.getData();
		if (object == null) return null;
		if (object.equals(element)) {
			return item;
		}
		else {
//			UIUtils.debug("Recursively ...");
			TreeItem [] items = item.getItems();
			for (TreeItem i : items) {
				TreeItem temp = findItem(i, element);
				if (temp != null) return temp;
			}
		}
//		UIUtils.debug("... Not found");
		return null;
	}
	
	public static int getIndex(TreeItem [] items, TreeItem item) {
		for (int i = 0; i < items.length; i++) {
			if (item == items[i]) return i;
		}
		return -1;
	}
	
	public static TreeItem findPrevItem(Tree tree, TreeItem item) {
		TreeItem parent = item.getParentItem();
		int index = 0;
		if (parent == null) {
			index = TreeSupports.getIndex(tree.getItems(), item);
			if (index != 0 ) {
				return tree.getItem(index - 1);
			}
		}
		else {
			index = TreeSupports.getIndex(parent.getItems(), item);
			if (index != 0 ) {
				return parent.getItem(index - 1);
			}
		}
		
		return null;
	}
	
	public static TreeItem findNextItem(Tree tree, TreeItem item) {
		TreeItem parent = item.getParentItem();
		int index = 0;
		if (parent == null) {
			index = TreeSupports.getIndex(tree.getItems(), item);
			if (index != tree.getItemCount() - 1)
				return tree.getItem(index + 1);
		}
		else {
			index = TreeSupports.getIndex(parent.getItems(), item);
			if (index != parent.getItemCount() - 1)
				return parent.getItem(index + 1);
			
		}
		
		return null;
	}
	
}
