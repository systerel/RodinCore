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

/**
 * This interface is the public toolkit for the indexing system.
 * <p>
 * It allows indexers to declare elements, record their occurrences and export
 * them.
 * <p>
 * This interface is NOT intended to be implemented by clients.
 * 
 * @see IIndexer
 * 
 * @author Nicolas Beauger
 */
public interface IIndexingToolkit {

	/**
	 * Declares the given element with the given name.
	 * <p>
	 * The given element must be local to the file being indexed, that is, a
	 * call to element.getRodinFile() must return the file being indexed.
	 * <p>
	 * The same element cannot be declared more than once.
	 * <p>
	 * 
	 * @param element
	 *            the element to declare.
	 * @param name
	 *            the public name of the element, the one known by the user.
	 * @throws IllegalArgumentException
	 *             if the element's file is not the one being indexed.
	 * @throws IllegalArgumentException
	 *             if the element has already been declared within the same file
	 *             indexing pass.
	 */
	void declare(IInternalElement element, String name);

	/**
	 * Adds an occurrence of the given element, of the given kind, at the given
	 * location.
	 * <p>
	 * The location must point to a place inside the file being indexed.
	 * <p>
	 * The element must be either local to the file or imported. The latter case
	 * is equivalent to saying that the element must be contained in the ones
	 * returned by {@link #getImports()}.
	 * 
	 * @param element
	 *            the element to which to add an occurrence.
	 * @param kind
	 *            the kind of the occurrence.
	 * @param location
	 *            the location inside the file being indexed.
	 * @throws IllegalArgumentException
	 *             if the location is not valid.
	 * @throws IllegalArgumentException
	 *             if the element is neither local nor imported.
	 */
	void addOccurrence(IInternalElement element, IOccurrenceKind kind,
			IRodinLocation location);

	/**
	 * Returns the elements visible from other files.
	 * <p>
	 * It is thus allowed to add occurrences of these elements and to export
	 * them.
	 * 
	 * @return the imported elements.
	 */
	IDeclaration[] getImports();

	/**
	 * Exports the given element, making it visible to dependent files.
	 * 
	 * @param element
	 *            the element to export.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given element is neither declared in the current file
	 *             nor imported.
	 */
	void export(IInternalElement element);

}