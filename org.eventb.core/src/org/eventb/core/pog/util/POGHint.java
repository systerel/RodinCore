/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPOSequent;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for all hints. Every hint must have the facility to store itself in a
 * proof obligation.
 * 
 * @author Stefan Hallerstede
 *
 */
public abstract class POGHint {
	
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
	public abstract void create(IPOSequent sequent, String name, IProgressMonitor monitor) 
	throws RodinDBException;
	
}
