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

/**
 * Provides details concerning an selected element. The details are a list of
 * strings.
 * 
 * @see DetailedList#setDetailsProvider(IDetailsProvider)
 */
public interface IDetailsProvider {

	/**
	 * Returns the details to be displayed
	 * 
	 * @param element
	 *            the element to retrieve details from
	 * @return an string array of details
	 */
	public String[] getDetails(String element);

}
