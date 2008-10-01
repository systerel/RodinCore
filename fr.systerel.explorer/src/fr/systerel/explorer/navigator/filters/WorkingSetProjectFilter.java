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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.navigator.CommonViewer;
import org.rodinp.core.IRodinProject;

/**
 * Filters out all RodinProject that are obtained from the root,
 * if the WorkingSetContentProvider is active.
 * This is done because otherwise the projects would be shown twice.
 * Once inside the working sets and once directly under the root.
 */
public class WorkingSetProjectFilter extends ViewerFilter {
	public WorkingSetProjectFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(parentElement instanceof IWorkspaceRoot) {
			if(element instanceof IRodinProject) {
				if (viewer instanceof CommonViewer) {
					CommonViewer cv =(CommonViewer) viewer;
					if (cv.getNavigatorContentService().isActive("fr.systerel.explorer.navigator.content.workingSet")) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
