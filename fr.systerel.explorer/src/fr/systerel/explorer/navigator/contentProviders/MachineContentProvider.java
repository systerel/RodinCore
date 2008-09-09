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
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.ui.projectexplorer.TreeNode;

/**
 * The content provider for Machines.
 * Shows the content of a Machine.
 * @author Maria Husmann
 *
 */
public class MachineContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (!(parentElement instanceof IMachineFile)) {
			return new Object[] {};
		}
		IMachineFile mch = (IMachineFile) parentElement;

		ArrayList<TreeNode<?>> list = new ArrayList<TreeNode<?>>();
		list.add(new TreeNode<IVariable>("Variables", mch,
				IVariable.ELEMENT_TYPE));
		list.add(new TreeNode<IInvariant>("Invariants", mch,
				IInvariant.ELEMENT_TYPE));
		list
				.add(new TreeNode<ITheorem>("Theorems", mch,
						ITheorem.ELEMENT_TYPE));
		list.add(new TreeNode<IEvent>("Events", mch, IEvent.ELEMENT_TYPE));

		return list.toArray();

	}

	public Object getParent(Object element) {
        if (element instanceof TreeNode) {
        	return ((TreeNode<?>) element).getParent();
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
