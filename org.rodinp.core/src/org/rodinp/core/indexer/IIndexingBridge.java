/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core.indexer;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.location.IInternalLocation;

/**
 * Common protocol for reporting the result of indexing a Rodin file.
 * <p>
 * When a file is indexed, an instance of this interface is passed to the
 * indexer which gets its inputs and reports its results through its methods.
 * </p>
 * <p>
 * Two methods allow the indexer to query its inputs:
 * <ul>
 * <li><code>getRootToIndex</code> returns the root element of the file which is
 * currently indexed.</li>
 * <li><code>getImports</code> returns the declarations exported by the files on
 * which the current file depends.</li>
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
 * Please note that, when indexing completes, declarations that have no
 * associated occurrence are simply ignored (i.e., not stored in the index).
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IIndexer
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface IIndexingBridge {

	/**
	 * Returns the root element of the file which is to be indexed.
	 * 
	 * @return the root element of the file to index
	 */
	IInternalElement getRootToIndex();

	/**
	 * Returns declarations that have been recorded so far. This method is
	 * particularly intended to be used by indexers that are called in sequence,
	 * so that they can get declarations from indexers run previously on the
	 * same file.
	 * 
	 * @return all declarations recorded so far
	 */
	public IDeclaration[] getDeclarations();

	/**
	 * Returns the declarations visible from files on which the current file
	 * depends. The declarations returned are all those that were exported by
	 * the last indexing of every file on which the current file depends.
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
	 * @return the declaration created for this element
	 * @throws IllegalArgumentException
	 *             if the given element is not a descendant of the root being
	 *             indexed or has already been declared
	 */
	IDeclaration declare(IInternalElement element, String name);

	/**
	 * Adds an occurrence of the given declaration with the given kind, and at
	 * the given location.
	 * <p>
	 * The location must point to an element inside the file being indexed.
	 * </p>
	 * <p>
	 * The given declaration must be either local to the file or imported. The
	 * latter case is equivalent to saying that the declaration must be
	 * contained in the ones returned by {@link #getImports()}.
	 * </p>
	 * 
	 * @param declaration
	 *            the declaration of the occurrence
	 * @param kind
	 *            the kind of the occurrence
	 * @param location
	 *            the location of the occurrence
	 * @throws IllegalArgumentException
	 *             if the location is not valid or if the declaration is neither
	 *             local, nor imported
	 */
	void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IInternalLocation location);

	/**
	 * Exports the given element, making it visible to dependent files.
	 * 
	 * @param declaration
	 *            the declaration of the element to export, must be declared in
	 *            the current file or imported
	 * @throws IllegalArgumentException
	 *             if the given declaration is neither local nor imported
	 */
	void export(IDeclaration declaration);

	/**
	 * Tells whether this task has been cancelled.
	 * <p>
	 * This method should be regularly called by indexers to stop as soon as
	 * possible when cancel is requested.
	 * </p>
	 * 
	 * @return <code>true</code> iff this task has been cancelled
	 */
	boolean isCancelled();
}