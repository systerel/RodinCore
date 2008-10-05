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
	 * Returns the user-visible name for the given element.
	 * 
	 * @param element
	 *            the element for which to get the name.
	 * @return the name of the element if it was found in the index, else an
	 *         empty String.
	 * @see #isUpToDate()
	 */
	// TODO Rather return a declaration ?
	String getIndexName(IInternalElement element);

	/**
	 * Returns the occurrences of the given element.
	 * 
	 * @param element
	 *            the element for which to get the occurrences.
	 * @return the indexed occurrences of the element.
	 * @see #isUpToDate()
	 */
	// TODO Rather pass a declaration ?
	IOccurrence[] getOccurrences(IInternalElement element);

	/**
	 * Returns all the elements that have been declared with the given
	 * user-visible name.
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
	 * blocks until it becomes up to date. Throws a CancellationException if the
	 * indexing has been canceled.
	 * 
	 * Calling this method before any other request makes the result valid.
	 * 
	 * @return true when the indexing system is currently busy.
	 */
	// TODO What's the point in always returning true ?
	boolean isUpToDate() throws CancellationException;

}