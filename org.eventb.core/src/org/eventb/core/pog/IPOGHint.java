/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;

/**
 * Common protocol for hints handled by the proof obligation generator.
 * <p>
 * This interface needs to be implemented by clients that need to
 * contribute new kinds of hints.
 * </p>
 * The core of the proof obligation generator provides predicate selection hints
 * and predicate interval selection hints, noth represented in the database by
 * {@link IPOSelectionHint}.
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IPOGHint {
	
	/**
	 * Creates a representation of this hint in the database.
	 * 
	 * @param sequent
	 *            the proof obligation to which this hint is to be attached
	 * @param name
	 *            ignored parameter
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @throws CoreException
	 *             if there was a problem accessing the database, or if the POG
	 *             hint is not valid
	 */
	public void create(IPOSequent sequent, String name, IProgressMonitor monitor)
			throws CoreException;

}
