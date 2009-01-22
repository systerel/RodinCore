/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - in project+name search, return an array of IDeclaration
 *******************************************************************************/
package org.rodinp.core.indexer;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;

/**
 * Common protocol for performing queries to the indexing system.
 * <p>
 * Query results may be obsolete if the indexing system has performed a new
 * indexing since they were computed. Clients should call
 * {@link #waitUpToDate()} before performing queries in order to get the most
 * accurate result.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 */
public interface IIndexQuery {

	/**
	 * Returns the declaration for the given element.
	 * 
	 * @param element
	 *            the element for which to get the declaration
	 * @return the declaration of the given element or <code>null</code> if not
	 *         found
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	IDeclaration getDeclaration(IInternalElement element)
			throws InterruptedException;

	/**
	 * Returns all occurrences of the given declaration.
	 * 
	 * @param declaration
	 *            the declaration for which occurrences are desired
	 * @return the indexed occurrences of the given declaration
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	IOccurrence[] getOccurrences(IDeclaration declaration)
			throws InterruptedException;

	/**
	 * Returns all elements declared within the given project with the given
	 * user-visible name.
	 * 
	 * @param project
	 *            the project in which to search
	 * @param name
	 *            the name to search for
	 * @return the elements found in the given project with the given name
	 * @throws InterruptedException
	 * @see #waitUpToDate()
	 */
	IDeclaration[] getDeclarations(IRodinProject project, String name)
			throws InterruptedException;

	/**
	 * Returns when the indexing system is up to date, else blocks until it
	 * becomes up to date. Throws an <code>InterruptedException</code> if the
	 * indexing has been canceled.
	 * <p>
	 * To ensure accurate results, clients should call this method before
	 * querying the index, while locking the appropriate part of the Rodin
	 * database.
	 * </p>
	 * 
	 * @throws InterruptedException
	 */
	void waitUpToDate() throws InterruptedException;

}