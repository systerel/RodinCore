package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.core.IEvent;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class TreeSupports {

	public static IInternalElement getEvent(Object obj) {
		IRodinElement rodinElement = ((Leaf) obj).getElement();
		if (rodinElement instanceof IEvent) {
			return (IEvent) rodinElement;
		}
		else if (rodinElement instanceof IInternalElement) {
			return (IInternalElement) ((IInternalElement) rodinElement).getParent();
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
		UIUtils.debug("From " + item);
		Leaf leaf = (Leaf) item.getData();
		if (leaf == null) return null;
		if (leaf.getElement().equals(element)) {
			UIUtils.debug("Found");
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
}
