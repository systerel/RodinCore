/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
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
	
	protected final IInternalElementType<?> type;

	public AbstractContentProvider(IInternalElementType<?> type) {
		this.type = type;
	}
	
	@Override
	public Object[] getChildren(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getChildren(type, false);
		}
		return NO_OBJECT;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// ignore
	
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

}