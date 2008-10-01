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


package fr.systerel.explorer.navigator.filters;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;

/**
 * This class filters out all contexts that have a machine as grandparent
 * in the navigator tree.
 *
 */
public class ContextFilter extends ViewerFilter {
	public ContextFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IContextFile) {
			if(parentElement instanceof TreePath) {
				TreePath path = (TreePath) parentElement;
				// if the "grandparent" of this context is a machine, don't show it.
				if (path.getSegment(path.getSegmentCount() -2) instanceof IMachineFile) {
					return false;
				}
			}
		}
		return true;
	}

}
