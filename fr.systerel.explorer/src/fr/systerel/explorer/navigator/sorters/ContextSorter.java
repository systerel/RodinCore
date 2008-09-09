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
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.ITheorem;
import org.eventb.ui.projectexplorer.TreeNode;

/**
 * A sorter for the <code>ContextContentProvider</code>
 * @author Maria Husmann
 *
 */
public class ContextSorter extends ViewerSorter {

	@Override
	public int category(Object element) {
		if (element instanceof TreeNode) {
			if (((TreeNode<?>)element).getType().equals(ICarrierSet.ELEMENT_TYPE)) {
				return 1;
			}
			if (((TreeNode<?>)element).getType().equals(IConstant.ELEMENT_TYPE)) {
				return 2;
			}
			if (((TreeNode<?>)element).getType().equals(IAxiom.ELEMENT_TYPE)) {
				return 3;
			}
			if (((TreeNode<?>)element).getType().equals(ITheorem.ELEMENT_TYPE)) {
				return 4;
			}
		}
		return 5;
	}

}
