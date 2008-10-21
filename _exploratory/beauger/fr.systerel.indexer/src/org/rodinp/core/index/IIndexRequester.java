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

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;

/**
 * Common protocol for performing requests to the indexing system.
 * <p>
 * Requests results may be obsolete if the indexing system is performing a new
 * indexing. Clients should call {@link #waitUpToDate()} before sending requests
 * in order to get the most accurate result.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 * 
 */
public interface IIndexRequester {

	/**
	 * Returns the declaration for the given element.
	 * 
	 * @param element
	 *            the element for which to get the declaration.
	 * @return the declaration of the element if it was found in the index.
	 * @throws InterruptedException
	 * @throws IllegalArgumentException
	 *             if the element is not known by the indexing system.
	 * @see #waitUpToDate()
	 */
	IDeclaration getDeclaration(IInternalElement element)
			throws InterruptedException;

	/**
	 * Returns the occurrences of the given element.
	 * 
	 * @param declaration
	 *            the declaration of the element for which to get the
	 *            occurrences.
	 * @return the indexed occurrences of the element.
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	IOccurrence[] getOccurrences(IDeclaration declaration)
			throws InterruptedException;

	/**
	 * Returns all elements declared with the given user-visible name.
	 * 
	 * @param project
	 *            the project in which to search.
	 * @param name
	 *            the researched name.
	 * @return the found elements with the given name.
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	IInternalElement[] getElements(IRodinProject project, String name)
			throws InterruptedException;

	/**
	 * Returns when the indexing system is up to date, else blocks until it
	 * becomes up to date. Throws a InterruptedException if the indexing has
	 * been canceled.
	 * 
	 * Calling this method before any other request makes the result valid.
	 * 
	 * @throws InterruptedException
	 */
	void waitUpToDate() throws InterruptedException;

}