/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import static org.eventb.core.seqprover.ProverLib.simplify;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

class CommitProofOperation implements IWorkspaceRunnable {

	private final ProofAttempt pa;
	private final boolean manual;
	private final boolean simplify;

	CommitProofOperation(ProofAttempt pa, boolean manual, boolean simplify) {
		this.pa = pa;
		this.manual = manual;
		this.simplify = simplify;
	}

	public void run(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask("Committing proof", 4 + 3);
			commitProof(pm);
			commitStatus(pm);
		} finally {
			pm.done();
		}
	}

	// Consumes four ticks of the given progress monitor
	private void commitProof(IProgressMonitor pm) throws RodinDBException {
		final IPRProof proof = pa.getProof();
		if (!proof.exists()) {
			proof.create(null, new SubProgressMonitor(pm, 1));
		} else {
			pm.worked(1);
		}
		final IProofTree proofTree;
		if (simplify) {
			proofTree = simplifyIfClosed(pa.getProofTree(),
					new SubProgressMonitor(pm, 1));
		} else {
			proofTree = pa.getProofTree();
			pm.worked(1);
		}
		proof.setProofTree(proofTree, new SubProgressMonitor(pm, 1));
		proof.setHasManualProof(manual, new SubProgressMonitor(pm, 1));
	}

	private static IProofTree simplifyIfClosed(IProofTree proofTree,
			final IProgressMonitor pm) {
		if (proofTree.isClosed()) {
			final IProofMonitor monitor = new ProofMonitor(pm);
			final IProofTree simplified = simplify(proofTree, monitor);
			if (simplified != null) {
				proofTree = simplified;
			}
		}
		pm.done();
		return proofTree;
	}

	// Consumes three ticks of the given progress monitor
	public void commitStatus(IProgressMonitor pm) throws RodinDBException {
		final IPSStatus status = pa.getStatus();
		status.copyProofInfo(new SubProgressMonitor(pm, 1));
		status.setBroken(pa.isBroken(), new SubProgressMonitor(pm, 1));
		final Long poStamp = pa.getPoStamp();
		if (poStamp != null) {
			status.setPOStamp(poStamp, new SubProgressMonitor(pm, 1));
		}
	}
}