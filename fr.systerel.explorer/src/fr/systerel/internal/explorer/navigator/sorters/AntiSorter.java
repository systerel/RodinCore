/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.sorters;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * A sorter that does not sort at all. The elements are sorted by the CNF
 * alphabetically by default, which we do not want. This overrides the sorting
 * and keeps them in the order they appear.
 * 
 * For that, we suppose that the sorting algorithm used by the CNF is stable and
 * just say that all elements are equal.
 */
public class AntiSorter extends ViewerSorter {

	public AntiSorter() {
		// do nothing
	}

	public AntiSorter(Collator collator) {
		super(collator);
	}

	/**
	 * All elements are equal, so that they do not get sorted.
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return 0;
	}

}
