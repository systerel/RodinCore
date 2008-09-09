/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.navigator.contentProviders;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;

/**
  * The abstract content provider for elements like Invariants, Axioms etc.
* @author Maria Husmann
 *
 */
public abstract class ElementsContentProvider implements ITreeContentProvider {

	protected Class<?> this_class;

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeNode) {
			return filter(((TreeNode<?>) parentElement).getChildren());
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
    	if (element instanceof IRodinElement) {
			return ((IRodinElement) element).getParent();
		}
      return null;
	}

	public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
  	// Do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	   	// Do nothing
		 
	}
	
	/**
	 * This filter lets pass only elements that are of type <code>this_class</code>.
	 * @param objects	The objects to filter
	 * @return			The filtered objects
	 */
	private Object[] filter(Object[] objects) {
		if (objects != null) {
			List<Object> list = new LinkedList<Object>();
			for (int i = 0; i < objects.length; i++) {
				if (this_class.isInstance(objects[i])) {
					list.add((objects[i]));
				}
			}
			return list.toArray();
		}
		return objects;
	}
	

}
