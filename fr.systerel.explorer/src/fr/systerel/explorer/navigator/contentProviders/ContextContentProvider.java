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

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.ITheorem;
import org.eventb.ui.projectexplorer.TreeNode;

import fr.systerel.explorer.model.ModelContext;
import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.model.ModelMachine;

/**
 * The content provider for Contexts.
 * Shows the content of a Context.
 * @author Maria Husmann
 *
 */
public class ContextContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IContextFile) {
			IContextFile ctx = (IContextFile) parentElement;
			ArrayList<TreeNode<?>> list = new ArrayList<TreeNode<?>>();
			list.add(new TreeNode<ICarrierSet>("Carrier Sets", ctx,
					ICarrierSet.ELEMENT_TYPE));
			list.add(new TreeNode<IConstant>("Constants", ctx,
					IConstant.ELEMENT_TYPE));
			list.add(new TreeNode<IAxiom>("Axioms", ctx, IAxiom.ELEMENT_TYPE));
			list
					.add(new TreeNode<ITheorem>("Theorems", ctx,
							ITheorem.ELEMENT_TYPE));

			Object[] children = new Object[list.size() +1];
			list.toArray(children);
			children[list.size()] = ModelController.getContext(ctx);
			return children;
		}
		return new Object[] {};
		
	}

	public Object getParent(Object element) {
        if (element instanceof TreeNode) {
        	return ((TreeNode<?>) element).getParent();
        }
        if (element instanceof ModelContext) {
        	return ((ModelContext) element).getInternalContext();
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
