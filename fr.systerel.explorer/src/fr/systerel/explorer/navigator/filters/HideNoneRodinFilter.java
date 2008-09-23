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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class HideNoneRodinFilter extends ViewerFilter {

	public HideNoneRodinFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof IProject) {
			IProject project = (IProject) element;
		try {
			if (project.isAccessible()) {
				if(project.hasNature("org.rodinp.core.rodinnature")) {
					return true;
				} else return false;
			} else return false;
		}catch(CoreException e) {
			e.printStackTrace();
			}
		}
		return true;
	}

}
