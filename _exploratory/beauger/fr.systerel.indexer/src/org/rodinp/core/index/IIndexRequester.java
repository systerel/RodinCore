/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.index;

import java.util.concurrent.CancellationException;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;

public interface IIndexRequester {

	/**
	 * Returns the currently indexed user-defined name of the given element.
	 * 
	 * @param element
	 *            the element for which to get the name.
	 * @return the name of the element if it was found in the index, else an
	 *         empty String.
	 * @see #isUpToDate()
	 */
	String getIndexName(IInternalElement element);

	/**
	 * Returns the currently indexed occurrences at which the given element was
	 * found.
	 * 
	 * @param element
	 *            the element for which to get the occurrences.
	 * @return the indexed occurrences of the element.
	 * @see #isUpToDate()
	 */
	IOccurrence[] getOccurrences(IInternalElement element);

	/**
	 * Returns the currently indexed elements having the given user-defined
	 * name.
	 * 
	 * @param project
	 *            the project in which to search.
	 * @param name
	 *            the researched name.
	 * @return the found elements with the given name.
	 * @see #isUpToDate()
	 */
	IInternalElement[] getElements(IRodinProject project, String name);

	/**
	 * Returns <code>true</code> when the indexing system is up to date, else
	 * blocks util it becomes up to date. Throws a CancellationException if the
	 * indexing is canceled.
	 * 
	 * Calling this method before any other request makes the result valid.
	 * 
	 * @return true when the indexing system is currently busy.
	 */
	boolean isUpToDate() throws CancellationException;

}