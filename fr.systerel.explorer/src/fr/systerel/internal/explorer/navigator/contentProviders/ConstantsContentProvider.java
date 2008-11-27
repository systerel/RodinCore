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
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * The content provider for Constant elements
 */
public class ConstantsContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(IConstant.ELEMENT_TYPE, false);
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		// there is no ModelElement for constants.
		if (element instanceof IConstant) {
			IConstant cst = (IConstant) element;
			IContextRoot ctx = (IContextRoot) cst.getRodinFile().getRoot();
			ModelContext context = ModelController.getContext(ctx);
			if (context != null) {
				return context.constant_node;
			}
		}
		if (element instanceof IElementNode) {
			return ((IElementNode) element).getParent();
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
