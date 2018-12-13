/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover;

import org.eclipse.core.runtime.IStatus;

/**
 * Common protocol for reporting the result of checking an auto tactic.
 * 
 * @author Laurent Voisin
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IAutoTacticCheckResult {

	/**
	 * Returns the descriptor of the auto tactic.
	 * 
	 * @return the descriptor of the auto tactic
	 */
	ITacticDescriptor getDescriptor();

	/**
	 * Returns the result of the check as an Eclipse status.
	 * 
	 * @return the result of the check
	 */
	IStatus getStatus();

	/**
	 * Tells whether the status is fresh, that is just obtained from the check as
	 * opposed to taken from a cache of a previous check.
	 * 
	 * @return whether the status is fresh
	 */
	boolean isFresh();

}
