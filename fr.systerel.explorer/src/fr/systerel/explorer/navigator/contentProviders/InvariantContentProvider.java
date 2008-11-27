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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IInvariant;

import fr.systerel.explorer.model.IModelElement;
import fr.systerel.explorer.model.ModelController;

/**
 * The content provider for Invariant elements
 * 
 */
public class InvariantContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(IInvariant.ELEMENT_TYPE, false);
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
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
}
