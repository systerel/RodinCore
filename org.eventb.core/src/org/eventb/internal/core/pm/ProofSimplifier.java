/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.eventb.internal.core.ProofMonitor;

/**
 * This class implements the operation to reuse and simplify of a proof.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSimplifier extends ProofModifier {

	private static final String SIMPLIFIER = "Simplifier"; //$NON-NLS-1$

	public ProofSimplifier(IPRProof proof) {
		super(proof, SIMPLIFIER, true);
	}

	@Override
	protected boolean makeNewProof(IProofAttempt pa, IProofSkeleton skel,
			IProgressMonitor monitor) {
		final ProofMonitor pm = new ProofMonitor(monitor);
		return ProofBuilder.reuse(pa.getProofTree().getRoot(), skel, pm);
	}

}
