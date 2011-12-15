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
import org.eventb.core.seqprover.ProverLib;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofSawyer {

	private final IProofTree proofTree;

	public ProofSawyer(IProofTree proofTree) {
		this.proofTree = proofTree;
	}

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
	public IProofTree simplify(IProofMonitor monitor) {
		final SawyerTree sawyerTree = new SawyerTree(proofTree.getRoot());
		sawyerTree.init();
		sawyerTree.saw();
		final IProofTree sawed = sawyerTree.toProofTree(monitor);
		if (sawed != null && ProverLib.deepEquals(proofTree, sawed)) {
			return null;
		}
		return sawed;
	}
}
