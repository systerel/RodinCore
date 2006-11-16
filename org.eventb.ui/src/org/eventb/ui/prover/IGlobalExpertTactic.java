/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.ui.prover;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.pm.IUserSupport;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the common interface for global proof tactics.
 */
public interface IGlobalExpertTactic extends IGlobalTactic {

	/**
	 * Apply the tactic.
	 * <p>
	 * 
	 * @param userSupport
	 *            the current user support
	 * @param input
	 *            the (optional) string input
	 * @throws RodinDBException
	 *             exceptions can be throws when applying tactics.
	 */
	public void apply(IUserSupport userSupport, String input,
			IProgressMonitor monitor) throws RodinDBException;

}
