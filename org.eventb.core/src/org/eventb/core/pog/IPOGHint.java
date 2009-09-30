/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.rodinp.core.RodinDBException;

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
	 * @param sequent the proof obligation to which this hint is to be attached
	 * @param name the element name to use for this hint
	 * @param monitor 
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	public void create(IPOSequent sequent, String name, IProgressMonitor monitor) 
	throws RodinDBException;

}
