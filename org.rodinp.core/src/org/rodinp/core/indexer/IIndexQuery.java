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

import java.util.Collection;
import java.util.Set;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.location.IInternalLocation;

/**
 * Common protocol for performing queries to the indexing system.
 * <p>
 * Queries return either declarations ({@link IDeclaration}) or occurrences (
 * {@link IOccurrence}). Basic queries consist of the following:
 * <ul>
 * <li>{@link #getDeclaration(IInternalElement)}</li>
 * <li>{@link #getDeclarations(IRodinFile)}</li>
 * <li>{@link #getVisibleDeclarations(IRodinFile)}</li>
 * <li>{@link #getDeclarations(IRodinProject, String)}</li>
 * <li>{@link #getOccurrences(IDeclaration)}</li>
 * <li>{@link #getOccurrences(IDeclaration, IPropagator)}</li>
 * </ul>
 * </p>
 * <p>
 * A basic query result might be too coarse. Queries provide filters on
 * declarations and occurrences to get a more precise result.
 * </p>
 * <p>
 * Declarations can be filtered out with:
 * <ul>
 * <li>a name</li>
 * <li>an element type</li>
 * </ul>
 * Occurrences can be filtered out with:
 * <ul>
 * <li>a file</li>
 * <li>an occurrence kind</li>
 * <li>a containing location</li>
 * </ul>
 * Further filtering is left to clients.
 * </p>
 * <p>
 * As an example of combining basic queries and filters, we may write the
 * following code:
 * 
 * <pre>
 * IIndexQuery query = RodinCore.makeIndexQuery();
 * query.waitUpToDate();
 * 
 * IDeclaration declMySet = query.getDeclaration(mySet);
 * if (declMySet != null) { // the element is indexed
 * 	Set&lt;IOccurrence&gt; occsMySet = query.getOccurrences(declMySet);
 * 	query.filterFile(occsMySet, myFile);
 * 	// process occurrences of mySet in myFile
 * }
 * </pre>
 * 
 * </p>
 * <p>
 * Query results may be obsolete if the indexing system has performed a new
 * indexing since they were computed. Clients should call
 * {@link #waitUpToDate()} before performing queries, as illustrated in the
 * above example, in order to get the most accurate result.
 * </p>
 * <p>
 * A query can be obtained through <code>RodinCore.makeIndexQuery()</code>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 0.9.2
 * @author Nicolas Beauger
 */
public interface IIndexQuery {

	/**
	 * Returns when the indexing system is up to date, else blocks until it
	 * becomes up to date.
	 * <p>
	 * To ensure accurate results, clients should call this method before
	 * querying the index, while locking the appropriate part of the Rodin
	 * database.
	 * </p>
	 * 
	 */
	void waitUpToDate();

	/**
	 * Returns the declaration for the given element. If the element is not
	 * indexed, <code>null</code> is returned.
	 * 
	 * @param element
	 *            the element for which the declaration is desired
	 * @return the declaration of the given element or <code>null</code> if not
	 *         found
	 * 
	 */
	IDeclaration getDeclaration(IInternalElement element);

	/**
	 * Returns the declarations of all indexed elements that occur in the given
	 * file. A returned declaration can belong to the given file. Alternatively,
	 * a returned declaration can belong to another file, as soon as it has an
	 * associated occurrence in the given file.
	 * 
	 * @param file
	 *            the file to search in
	 * @return the declarations that have an occurrence in the given file
	 * @since 0.9.3
	 */
	Set<IDeclaration> getDeclarations(IRodinFile file);

	/**
	 * Returns the declarations of all indexed elements that are visible in the
	 * given file. In other words, it returns <code>getDeclarations(file)</code>
	 * plus visible (imported) elements that do not occur in <code>file</code>,
	 * but could.
	 * 
	 * @param file
	 *            the file to search in
	 * @return the declarations that are visible in the given file
	 * @since 0.9.3
	 */
	Set<IDeclaration> getVisibleDeclarations(IRodinFile file);

	/**
	 * Returns all elements declared within the given project with the given
	 * user-visible name.
	 * 
	 * @param project
	 *            the project to search in
	 * @param name
	 *            the name to search for
	 * @return the declarations found in the given project with the given name
	 * 
	 */
	Set<IDeclaration> getDeclarations(IRodinProject project, String name);

	/**
	 * Returns all occurrences of the given declaration.
	 * 
	 * @param declaration
	 *            the declaration for which occurrences are desired
	 * @return the indexed occurrences of the given declaration
	 * 
	 */
	Set<IOccurrence> getOccurrences(IDeclaration declaration);

	/**
	 * Returns all occurrences of the given declaration and propagates the call
	 * on declarations provided by the given propagator.
	 * <p>
	 * Initially, the propagator is called on each occurrence of the given
	 * declaration. If the propagator returns a non null declaration on some of
	 * them, all occurrences of these declarations are added to the result, and
	 * the propagator is called again on the new occurrences. The process stops
	 * when the propagator returns only <code>null</code> values.
	 * </p>
	 * <p>
	 * This method is particularly intended to deal with cases where various
	 * elements represent the same entity.
	 * </p>
	 * 
	 * @param declaration
	 *            the declaration for which occurrences are desired
	 * @param propagator
	 *            the propagator to propagate the call with
	 * @return a set of propagated declarations
	 * @see IPropagator
	 * @since 0.9.3
	 */
	Set<IOccurrence> getOccurrences(IDeclaration declaration,
			IPropagator propagator);

	/**
	 * Returns all occurrences of the given declarations.
	 * 
	 * @param declarations
	 *            the declarations for which occurrences are desired
	 * @return the indexed occurrences of the given declarations
	 * 
	 * @since 0.9.3
	 */
	Set<IOccurrence> getOccurrences(Collection<IDeclaration> declarations);

	/**
	 * Returns the declarations associated with the given occurrences.
	 * 
	 * @param occurrences
	 *            the occurrences for which declarations are desired
	 * @return the declarations corresponding to the given occurrences
	 * 
	 * @since 0.9.3
	 */
	Set<IDeclaration> getDeclarations(Collection<IOccurrence> occurrences);

	/**
	 * Filters out the given declarations, keeping only the ones that refer to
	 * an element of the given type.
	 * 
	 * @param declarations
	 *            a set of declarations
	 * @param type
	 *            the type to filter with
	 * @since 0.9.3
	 */
	void filterType(Set<IDeclaration> declarations, IInternalElementType<?> type);

	/**
	 * Filters out the given declarations, keeping only the ones with the given
	 * name.
	 * 
	 * @param declarations
	 *            a set of declarations
	 * @param name
	 *            the name to filter with
	 * @since 0.9.3
	 */
	void filterName(Set<IDeclaration> declarations, String name);

	/**
	 * Filters out the given occurrences, keeping only the ones of the given
	 * kind.
	 * 
	 * @param occurrences
	 *            a set of occurrences
	 * @param kind
	 *            the kind to filter with
	 * @since 0.9.3
	 */
	void filterKind(Set<IOccurrence> occurrences, IOccurrenceKind kind);

	/**
	 * Filters out the given occurrences, keeping only the ones located in the
	 * given file.
	 * 
	 * @param occurrences
	 *            a set of occurrences
	 * @param file
	 *            the file to filter with
	 * @since 0.9.3
	 */
	void filterFile(Set<IOccurrence> occurrences, IRodinFile file);

	/**
	 * Filters out the given occurrences, keeping only the ones included in the
	 * given location.
	 * 
	 * @param occurrences
	 *            a set of occurrences
	 * @param location
	 *            the location to filter with
	 * @since 0.9.3
	 */
	void filterLocation(Set<IOccurrence> occurrences, IInternalLocation location);

}