/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

/**
 * Implements saving of a proof in the proof file, maintaining the proof status
 * at the same time. Instances must be run while locking all files of the
 * corresponding proof component.
 * 
 * @author Laurent Voisin
 */
class CommitProofOperation implements IWorkspaceRunnable {

	private final ProofAttempt pa;
	private final boolean manual;
	private final boolean simplify;

	CommitProofOperation(ProofAttempt pa, boolean manual, boolean simplify) {
		this.pa = pa;
		this.manual = manual;
		this.simplify = simplify;
	}

	@Override
	public void run(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask("Committing proof", 1 + 3 + 4);
			final IProofTree proofTree = getProofTree(pm);
			if (pm.isCanceled())
				return;
			commitProof(proofTree, pm);
			commitStatus(proofTree.getProofDependencies(), pm);
		} finally {
			pm.done();
		}
	}

	// Consumes one tick of the given progress monitor
	private IProofTree getProofTree(IProgressMonitor pm)
			throws RodinDBException {
		final IProofTree proofTree = pa.getProofTree();
		if (simplify) {
			final IProgressMonitor spm = new SubProgressMonitor(pm, 1);
			return simplifyIfClosed(proofTree, spm);
		} else {
			pm.worked(1);
			return proofTree;
		}
	}
	
	private static IProofTree simplifyIfClosed(IProofTree proofTree,
			IProgressMonitor pm) {
		try {
			if (proofTree.isClosed()) {
				final IProofMonitor monitor = new ProofMonitor(pm);
				final IProofTree simplified = simplify(proofTree, monitor);
				if (simplified != null) {
					return simplified;
				}
			}
			return proofTree;
		} finally {
			pm.done();
		}
	}

	// Consumes three ticks of the given progress monitor
	private void commitProof(IProofTree proofTree, IProgressMonitor pm)
			throws RodinDBException {
		final IPRProof proof = pa.getProof();
		if (!proof.exists()) {
			proof.create(null, new SubProgressMonitor(pm, 1));
		} else {
			pm.worked(1);
		}
		proof.setProofTree(proofTree, new SubProgressMonitor(pm, 1));
		proof.setHasManualProof(manual, new SubProgressMonitor(pm, 1));
	}

	// Consumes four ticks of the given progress monitor
	public void commitStatus(IProofDependencies proofDeps, IProgressMonitor pm)
			throws RodinDBException {
		final SubMonitor monitor = SubMonitor.convert(pm);
		final IPSStatus status = pa.getStatus();
		status.copyProofInfo(monitor.newChild(1));
		status.setBroken(pa.isBroken(), monitor.newChild(1));
		final Long poStamp = pa.getPoStamp();
		if (poStamp != null) {
			status.setPOStamp(poStamp, monitor.newChild(1));
		} else {
			monitor.worked(1);
		}
		status.setContextDependent(proofDeps.isContextDependent(),
				monitor.newChild(1));
	}
}