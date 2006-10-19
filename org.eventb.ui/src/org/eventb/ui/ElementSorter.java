/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.ViewerSorter</code> and provides a
 *         sorter for different elements which appeared in the UI
 * 
 */
public class ElementSorter extends ViewerSorter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 == cat2) {
			if (e1 instanceof IRodinProject || e1 instanceof IRodinFile) {
				String label1 = ElementUIRegistry.getDefault().getLabel(e1);
				String label2 = ElementUIRegistry.getDefault().getLabel(e2);
				return label1.compareTo(label2);
			} else {
				return e1.getClass().toString().compareTo(
						e2.getClass().toString());
			}
		}
		return cat1 - cat2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
	 */
	@Override
	public int category(Object element) {
		return ElementUIRegistry.getDefault().getPriority(element);
	}

}
