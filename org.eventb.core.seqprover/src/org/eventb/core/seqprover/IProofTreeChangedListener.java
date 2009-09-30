/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.seqprover;

/**
 * Common protocol for proof tree change listeners.
 * <p>
 * A listener receives notification of changes to a proof tree maintained by the
 * sequent prover.
 * </p>
 * <p>
 * This interface may be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public interface IProofTreeChangedListener {

	/**
	 * Notifies that the observed proof tree has changed. The specific details
	 * of the change are described by the given delta.
	 * 
	 * @param delta
	 *            description of the change to the observed proof tree
	 */
	void proofTreeChanged(IProofTreeDelta delta);

}
