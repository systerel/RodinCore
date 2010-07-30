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

package fr.systerel.explorer.examples.contentProvider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IInvariant;
import org.rodinp.core.RodinDBException;

/**
 * This is a very simple content provider that provides for each invariant the
 * predicate as String. It can be used together with the
 * <code>org.eclipse.jface.viewers.LabelProvider</code> that creates the label
 * with the toString() function of the elements.
 * 
 */
public class ContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IInvariant) {
			Object[] result =  new Object[1];
			try {
				//get the predicate String.
				result[0] = ((IInvariant) parentElement).getPredicateString();
				return result;
			} catch (RodinDBException e) {
				// do nothing. 
			}
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		// do nothing
		return null;
	}

	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// do nothing

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}
