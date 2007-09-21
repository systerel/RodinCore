package org.eventb.internal.ui.obligationexplorer.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;

public final class TreeSupports {

	public static Object[] treeSelectionToSet(TreeViewer treeViewer,
			IStructuredSelection ssel) {
		Collection<Object> selectedObjects = new ArrayList<Object>();
		ITreeContentProvider contentProvider = (ITreeContentProvider) treeViewer
				.getContentProvider();
		Object[] objects = ssel.toArray();
		for (Object obj : objects) {
			if (selectedObjects.contains(obj))
				continue;
			if (containAncester(selectedObjects, obj, contentProvider))
				continue;
			else
				addAndRemoveChildren(obj, selectedObjects, contentProvider);
		}
		return selectedObjects.toArray(new Object[selectedObjects.size()]);
	}

	private static void addAndRemoveChildren(Object obj,
			Collection<Object> selectedObjects,
			ITreeContentProvider contentProvider) {
		Object[] tmp = selectedObjects.toArray(new Object[selectedObjects.size()]);
		for (Object selectedObject : tmp) {
			if (isAncester(obj, selectedObject, contentProvider)) {
				selectedObjects.remove(selectedObject);
			}
		}
		selectedObjects.add(obj);
	}

	private static boolean containAncester(Collection<Object> selectedObjects,
			Object obj, ITreeContentProvider contentProvider) {
		for (Object selectedObject : selectedObjects) {
			if (isAncester(selectedObject, obj, contentProvider)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isAncester(Object selectedObject, Object obj, ITreeContentProvider contentProvider) {
		obj = contentProvider.getParent(obj);
		while (obj != null) {
			if (selectedObject.equals(obj))
				return true;
			obj = contentProvider.getParent(obj);
		}
		return false;
	}

}
