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
public interface IIndexingBridge {

	/**
	 * Returns the root element of the file which is to be indexed.
	 * 
	 * @return the root element of the file to index.
	 */
	IInternalElement getRootToIndex();

	/**
	 * Returns declarations that have been recorded so far. This method is
	 * particularly intended to be used by serial indexers, so that they can get
	 * declarations from previous indexers that worked on the same file.
	 * 
	 * @return all recorded declarations.
	 */
	public IDeclaration[] getDeclarations();

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
	 * @return the declaration created for this element
	 */
	IDeclaration declare(IInternalElement element, String name);

	/**
	 * Adds an occurrence of the given declared element, of the given kind, at
	 * the given location.
	 * <p>
	 * The location must point to a place inside the file being indexed.
	 * </p>
	 * <p>
	 * The element must be either local to the file or imported. The latter case
	 * is equivalent to saying that the element must be contained in the ones
	 * returned by {@link #getImports()}.
	 * </p>
	 * 
	 * @param declaration
	 *            the declaration of the element to which to add an occurrence.
	 * @param kind
	 *            the kind of the occurrence.
	 * @param location
	 *            the location inside the file being indexed.
	 * @throws IllegalArgumentException
	 *             if the location is not valid.
	 * @throws IllegalArgumentException
	 *             if the element is neither local nor imported.
	 */
	void addOccurrence(IDeclaration declaration, IOccurrenceKind kind,
			IInternalLocation location);

	/**
	 * Exports the given element, making it visible to dependent files.
	 * 
	 * @param declaration
	 *            the declaration of the element to export, must be declared in
	 *            the current file or imported
	 */
	void export(IDeclaration declaration);

	/**
	 * Tells whether this task has been cancelled.
	 * <p>
	 * This method should be regularly called by indexers to stop as soon as
	 * possible when cancel is requested.
	 * </p>
	 * 
	 * @return <code>true</code> iff this task has been cancelled.
	 */
	boolean isCancelled();
}