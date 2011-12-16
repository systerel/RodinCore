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
package org.eventb.internal.core.seqprover.proofSimplifier2;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofSawyer {

	/**
	 * Simplify the proof tree by removing:
	 * <ul>
	 * <li>useless hyp actions
	 * <li>useless proof steps
	 * </ul>
	 * 
	 * @param monitor
	 *            a monitor to report progress and support cancellation
	 * @return the simplified proof tree, or <code>null</code> if no
	 *         simplification occurred
	 */
	public IProofTree simplify(IProofTree proofTree, IProofMonitor monitor) {
		if (!proofTree.isClosed()) {
			throw new IllegalArgumentException(
					"Cannot simplify a non closed proof tree");
		}
		final SawyerTree sawyerTree = new SawyerTree(proofTree.getRoot());
		sawyerTree.init();
		sawyerTree.saw();
		return sawyerTree.toProofTree(monitor);
	}
}
