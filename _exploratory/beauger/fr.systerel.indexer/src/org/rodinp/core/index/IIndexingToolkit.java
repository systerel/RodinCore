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
import org.rodinp.core.IRodinFile;

/**
 * Common protocol for reporting the result of indexing a Rodin file.
 * <p>
 * When a file is indexed, an instance of this interface is passed to the
 * indexer which gets its inputs and reports its results through its methods.
 * </p>
 * <p>
 * Two methods allow the indexer to query its inputs:
 * <ul>
 * <li><code>getRodinFile</code> returns the file which is currently indexed.</li>
 * <li><code>getImports</code> returns the declarations exported by the files
 * on which the current file depends.</li>
 * </ul>
 * </p>
 * <p>
 * Three methods allow the indexer to report its results:
 * <ul>
 * <li><code>declare</code> reports a new declaration in the current file.</li>
 * <li><code>addOccurrence</code> reports an occurrence in the current file.</li>
 * <li><code>export</code> indicates that a declaration is to be exported to
 * files that depend on the current file.</li>
 * </ul>
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IIndexer
 * 
 * @author Nicolas Beauger
 */
public interface IIndexingToolkit {

	/**
	 * Returns the Rodin file which is currently being indexed.
	 * 
	 * @return the Rodin file to index
	 */
	IRodinFile getRodinFile();

	/**
	 * Returns the declarations visible from files on which the current file
	 * depends. The declarations returned are all those that were exported by
	 * the last indexing of a file on which the current file depends.
	 * <p>
	 * The returned declarations can subsequently be exported (so they come
	 * visible to dependent files) and the indexer can report occurrences for
	 * them.
	 * </p>
	 * 
	 * @return the imported declarations
	 */
	IDeclaration[] getImports();

	/**
	 * Declares the given element with the given name. The name represents the
	 * user-visible name of the element, not the element name in the Rodin
	 * database. An element must not be declared more than once in an indexing
	 * run.
	 * 
	 * @param element
	 *            the element to declare, must belong to the current file
	 * @param name
	 *            the user-visible name of the element
	 */
	// TODO return the declaration created for this element
	void declare(IInternalElement element, String name);

	/**
	 * Adds an occurrence of the given element, of the given kind, at the given
	 * location.
	 * <p>
	 * The location must point to a place inside the file being indexed.
	 * </p>
	 * <p>
	 * The element must be either local to the file or imported. The latter case
	 * is equivalent to saying that the element must be contained in the ones
	 * returned by {@link #getImports()}.
	 * </p>
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
	// TODO pass declaration rather than element
	void addOccurrence(IInternalElement element, IOccurrenceKind kind,
			IRodinLocation location);

	/**
	 * Exports the given element, making it visible to dependent files.
	 * 
	 * @param element
	 *            the element to export, must be declared in the current file or
	 *            imported
	 */
	// TODO pass declaration rather than element
	void export(IInternalElement element);

}