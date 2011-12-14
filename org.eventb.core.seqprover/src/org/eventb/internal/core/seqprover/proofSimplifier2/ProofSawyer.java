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

	private final IProofTree proofTree;

	public ProofSawyer(IProofTree proofTree) {
		this.proofTree = proofTree;
	}

	// TODO monitors
	public IProofTree simplify(IProofMonitor monitor) {
		return __simplify(false, monitor);
	}

	/**
	 * For TESTING purposes only.
	 */
	public IProofTree __simplify(boolean makeProofTree, IProofMonitor monitor) {
		final SawyerTree sawyerTree = new SawyerTree(proofTree.getRoot());
		sawyerTree.init();
		final int initialSize = sawyerTree.getSize();
		sawyerTree.saw();
		final int sawedSize = sawyerTree.getSize();
		if (sawedSize < initialSize || makeProofTree) {
			return sawyerTree.toProofTree(monitor);
		} else {
			return null;
		}
	}
}
