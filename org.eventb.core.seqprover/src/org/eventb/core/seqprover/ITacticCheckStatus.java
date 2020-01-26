/*******************************************************************************
 * Copyright (c) 2018, 2020 Systerel and others.
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
 * Common protocol for reporting the result of checking a tactic.
 * 
 * @author Laurent Voisin
 * 
 * @since 3.3
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITacticCheckStatus extends IStatus {

	/**
	 * Returns the descriptor of the tactic.
	 * 
	 * @return the descriptor of the tactic
	 */
	ITacticDescriptor getDescriptor();

	/**
	 * Tells whether the status is fresh, that is just obtained from the check as
	 * opposed to taken from a cache of a previous check.
	 * 
	 * @return whether the status is fresh
	 */
	boolean isFresh();

}
