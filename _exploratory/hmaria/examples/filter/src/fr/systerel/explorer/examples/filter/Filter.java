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

package fr.systerel.explorer.examples.filter;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * A very simple filter that filters out all projects that contain the string
 * "example" in their name
 * 
 */
public class Filter extends ViewerFilter {

	public Filter() {
		// do nothing
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject) {
			if (((IProject) element).getName().contains("example")) {
				return false;
			}
		}
		return true;
	}

}
