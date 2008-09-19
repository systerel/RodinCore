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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.basis.PSStatus;

/**
 * @author Administrator
 *
 */
public class ObligationTextFilter extends ViewerFilter {

	/**
	 * 
	 */
	public ObligationTextFilter() {
		// TODO Auto-generated constructor stub
	}
	
	public static String text = "";
	//excluding or including the string?
	public static boolean exclude = false;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
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

}
