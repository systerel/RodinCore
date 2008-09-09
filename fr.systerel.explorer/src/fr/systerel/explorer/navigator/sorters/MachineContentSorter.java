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


package fr.systerel.explorer.navigator.sorters;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.ui.projectexplorer.TreeNode;

/**
 * A sorter for the <code>MachineContentProvider</code>
 * @author Maria Husmann
 *
 */
public class MachineContentSorter extends ViewerSorter {

	@Override
	public int category(Object element) {
		if (element instanceof TreeNode) {
			if (((TreeNode<?>)element).getType().equals(IVariable.ELEMENT_TYPE)) {
				return 1;
			}
			if (((TreeNode<?>)element).getType().equals(IInvariant.ELEMENT_TYPE)) {
				return 2;
			}
			if (((TreeNode<?>)element).getType().equals(ITheorem.ELEMENT_TYPE)) {
				return 3;
			}
			if (((TreeNode<?>)element).getType().equals(IEvent.ELEMENT_TYPE)) {
				return 4;
			}
		}
		return 5;
	}

}
