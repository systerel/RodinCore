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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.basis.PSStatus;

/**
 * A filter for Proof Obligation that filters according to a given text.
 *
 */
public class ObligationTextFilter extends ViewerFilter {

	/**
	 * 
	 */
	public ObligationTextFilter() {
		// do nothing
	}
	
	private String text = "";
	
	
	//excluding or including the string?
	private boolean exclude = false;

	/**
	 * Implements filtering based on proof obligation names.
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement,
			Object element) {

		if (element instanceof PSStatus) {
			final String filterString = text;
			final boolean excluding = exclude;
			if (filterString.length() == 0) {
				// This filter always match the PO name
				return !excluding;
			}
			final PSStatus sequent = (PSStatus) element;
			if (sequent.getElementName().contains(filterString))
				return !excluding;
			else
				return excluding;
		}
		return true;
	}

	public void setText(String text) {
		this.text = text;
	}

}
