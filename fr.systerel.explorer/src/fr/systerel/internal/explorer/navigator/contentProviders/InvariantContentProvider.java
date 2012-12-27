/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.contentProviders;

import org.eventb.core.IInvariant;

import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * The content provider for Invariant elements
 * 
 */
public class InvariantContentProvider extends AbstractContentProvider {

	public InvariantContentProvider() {
		super(IInvariant.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {
		IModelElement model = ModelController.getModelElement(element);
		if (model != null) {
			return model.getParent(true);
		}
		return null;
	}

}
