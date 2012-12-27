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
package fr.systerel.internal.explorer.navigator.filters;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;

/**
 * This class filters out all contexts that have a machine as grandparent
 * in the navigator tree.
 *
 */
public class ContextFilter extends ViewerFilter {
	public ContextFilter() {
		// do nothing
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IContextRoot) {
			if(parentElement instanceof TreePath) {
				TreePath path = (TreePath) parentElement;
				// if the "grandparent" of this context is a machine, don't show it.
				if (path.getSegmentCount() >= 2) {
					if (path.getSegment(path.getSegmentCount() -2) instanceof IMachineRoot) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
