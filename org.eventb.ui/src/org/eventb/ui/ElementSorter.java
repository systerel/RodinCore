/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used ElementDescRegistry
 *******************************************************************************/
package org.eventb.ui;

import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.LABEL;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 *         This class extends
 *         <code>org.eclipse.jface.viewers.ViewerSorter</code> and provides a
 *         sorter for different elements which appeared in the UI
 * @since 1.0
 * 
 */
public class ElementSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);
		if (cat1 == cat2) {
			if (e1 instanceof IRodinProject || e1 instanceof IRodinFile) {
				final ElementDescRegistry registry = ElementDescRegistry
						.getInstance();
				final String label1 = registry.getValueAtColumn(
						(IRodinElement) e1, LABEL);
				final String label2 = registry.getValueAtColumn(
						(IRodinElement) e2, LABEL);
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
		return ElementDescRegistry.getInstance().getPriority(element);
	}

}
