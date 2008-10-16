/*******************************************************************************
 * Copyright (c) 2008 Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Duesseldorf - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import java.util.ArrayList;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.ITheorem;
import org.eventb.ui.projectexplorer.AbstractRodinContentProvider;
import org.eventb.ui.projectexplorer.TreeNode;

public class ContextContentProvider extends AbstractRodinContentProvider {

	@Override
	public Object[] getChildren(final Object parent) {
		if (!(parent instanceof IContextRoot)) {
			return new Object[] {};
		}
		IContextRoot ctx = (IContextRoot) parent;
		ArrayList<TreeNode<?>> list = new ArrayList<TreeNode<?>>();
		list.add(new TreeNode<ICarrierSet>("Carrier Sets", ctx,
				ICarrierSet.ELEMENT_TYPE));
		list.add(new TreeNode<IConstant>("Constants", ctx,
				IConstant.ELEMENT_TYPE));
		list.add(new TreeNode<IAxiom>("Axioms", ctx, IAxiom.ELEMENT_TYPE));
		list
				.add(new TreeNode<ITheorem>("Theorems", ctx,
						ITheorem.ELEMENT_TYPE));

		return list.toArray();
	}

}
