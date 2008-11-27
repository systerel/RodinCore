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
package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IPSStatus;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * 
 * The content provider for proof obligations
 * 
 */
public class POContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(IPSStatus.ELEMENT_TYPE, false);
		}

		return new Object[0];
	}

	// proof obligations can have multiple parents. return none at all.
	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
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
