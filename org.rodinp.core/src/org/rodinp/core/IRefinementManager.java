/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Published interface for the refinement manager. The refinement manager can be
 * obtained by calling {@link RodinCore#getRefinementManager()}.
 * 
 * @author Nicolas Beauger
 * @since 1.4
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IRefinementManager {

	/**
	 * Returns the root element type associated to the given refinement id, or
	 * <code>null</code> if none.
	 * 
	 * @param refinementId
	 *            a fully qualified refinement id
	 * @return an element type, or <code>null</code>
	 */
	IInternalElementType<?> getRootType(String refinementId);

	/**
	 * Refines the given source root to the given target root. In case the
	 * refinement fails, <code>false</code> is returned and an error is logged
	 * when appropriate.
	 * <p>
	 * In the context of this plug-in, the notion of refinement is to be
	 * understood as an operation that fills a new component (the target) from
	 * an existing one (the source), without any constraint about the results.
	 * </p>
	 * <p>
	 * The given source root is not modified by this operation.
	 * </p>
	 * <p>
	 * The given target file is modified but not saved by this operation.
	 * </p>
	 * 
	 * @param sourceRoot
	 *            the source root to refine
	 * @param targetRoot
	 *            the target root, initially empty, intended to be filled by
	 *            refinement participants
	 * @see IRodinProject#getRodinFile(String)
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress report is
	 *            not desired
	 * @return refined root or <code>null</code>
	 * @throws RodinDBException
	 *             if a database request fails during the operation
	 * @since 1.4
	 */
	boolean refine(IInternalElement sourceRoot, IInternalElement targetRoot,
			IProgressMonitor monitor) throws RodinDBException;
}