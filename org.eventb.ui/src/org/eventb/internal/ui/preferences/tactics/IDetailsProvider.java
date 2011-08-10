/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import org.eclipse.swt.widgets.Composite;

/**
 * Provides details concerning an selected element. The details are a list of
 * strings.
 * 
 * @see DetailedList#setDetailsProvider(IDetailsProvider)
 */
public interface IDetailsProvider {

	/**
	 * Puts the details of the given element to be displayed in the given parent
	 * composite.
	 * 
	 * @param element
	 *            the element to retrieve details from
	 * @param parent
	 *            the parent details composite
	 */
	void putDetails(String element, Composite parent);

	/**
	 * Clears all details
	 */
	void clear();
}
