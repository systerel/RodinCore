/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.pm;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.RodinDBException;

class CommitProofOperation implements IWorkspaceRunnable {

	private final ProofAttempt pa;
	private final boolean manual;

	CommitProofOperation(ProofAttempt pa, boolean manual) {
		this.pa = pa;
		this.manual = manual;
	}

	public void run(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask("Committing proof", 2 + 3);
			commitProof(pm);
			commitStatus(pm);
		} finally {
			pm.done();
		}
	}

	// Consumes two ticks of the given progress monitor
	private void commitProof(IProgressMonitor pm) throws RodinDBException {
		final IPRProof proof = pa.getProof();
		final IProofTree proofTree = pa.getProofTree();
		proof.setProofTree(proofTree, new SubProgressMonitor(pm, 1));
		proof.setHasManualProof(manual, new SubProgressMonitor(pm, 1));
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