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
package fr.systerel.internal.explorer.navigator.sorters;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * A sorter that doesn't sort. The elements are sorted by the CNF alphabetically
 * by default, which we don't want. This overrides the sorting and keeps them in
 * the order they appear.
 * 
 * The CNF demands a subclass of <code>ViewerSorter</code>.
 * 
 */
public class AntiSorter extends ViewerSorter {

	/**
	 * 
	 */
	public AntiSorter() {
		// do nothing
	}

	/**
	 * @param collator
	 */
	public AntiSorter(Collator collator) {
		super(collator);
		// do nothing
	}
	

	/**
	 * Don't sort!! 
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		return -1;
	}

}
