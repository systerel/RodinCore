/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.rodinp.core.indexer;

/**
 * Common protocol for query propagation.
 * <p>
 * Implementors of this interface are intended to be used in calls to
 * {@link IIndexQuery#getOccurrences(IDeclaration, IPropagator)}, to propagate
 * the call to declarations other than the given one. These other declarations
 * are supposed to be extracted from the occurrences of the given one.
 * </p>
 * <p>
 * This can be particularly intended to deal with cases where various
 * declarations represent the same entity. The problem is that each declaration
 * is indexed separately, thus having separate occurrences. The propagation
 * mechanism allows to get all occurrences of the entity, whatever the
 * underlying declaration. It relies on the hypothesis that there is a way to
 * link occurrences of a declaration to the other target declarations.
 * </p>
 * <p>
 * The way declarations are linked to each other through occurrences depends on
 * indexers implementation. Such a link may or may not exist.
 * </p>
 * <p>
 * This interface is intended to be implemented by clients.
 * </p>
 * 
 * @author Nicolas Beauger
 * 
 * @since 1.0
 */
public interface IPropagator {

	/**
	 * Returns the declaration to propagate the query on, from the given
	 * occurrence. The propagator decides whether to propagate or not, and what
	 * to propagate on.
	 * 
	 * @param occurrence
	 *            an occurrence found while performing a propagating query
	 * @param query
	 *            the calling query
	 * @return the declaration of the element to propagate the query on, or
	 *         <code>null</code> if the occurrence involves no propagation
	 * @see IIndexQuery#getOccurrences(IDeclaration, IPropagator)
	 */
	IDeclaration getRelativeDeclaration(IOccurrence occurrence,
			IIndexQuery query);
}
