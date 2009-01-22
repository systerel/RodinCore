/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
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
import org.rodinp.core.IInternalElementType;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;

public abstract class AbstractContentProvider implements ITreeContentProvider {

	protected static final Object[] NO_OBJECT = new Object[0];

	public AbstractContentProvider() {
		super();
	}

	protected abstract IInternalElementType<?> getElementType();
	
	
	
	public Object[] getChildren(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(getElementType(), false);
		}
		return NO_OBJECT;
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