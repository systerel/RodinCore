/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.internal.core.ProofMonitor;

/**
 * This class implements the operation to rebuild of a proof.
 * 
 * @author Nicolas Beauger
 * @since 1.3
 */
public class ProofRebuilder extends ProofModifier {

	private static final String REBUILDER = "Rebuilder"; //$NON-NLS-1$

	public ProofRebuilder(IPRProof proof, FormulaFactory factory) {
		super(proof, factory, REBUILDER);
	}

	@Override
	protected boolean makeNewProof(IProofAttempt pa,
			IProofSkeleton originalSkeleton, IProgressMonitor monitor) {
		final Object result = BasicTactics.rebuildTac(originalSkeleton).apply(
				pa.getProofTree().getRoot(), new ProofMonitor(monitor));
		return (result == null);
	}

}
