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

import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.ModelContext;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * The content provider for Constant elements
 */
public class ConstantsContentProvider extends AbstractContentProvider {

	public ConstantsContentProvider() {
		super(IConstant.ELEMENT_TYPE);
	}

	@Override
	public Object getParent(Object element) {
		// there is no ModelElement for constants.
		if (element instanceof IConstant) {
			IConstant cst = (IConstant) element;
			IContextRoot ctx = (IContextRoot) cst.getRoot();
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

}
