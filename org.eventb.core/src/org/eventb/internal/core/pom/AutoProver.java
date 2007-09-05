/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pom;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.PSWrapper;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

final class AutoProver {

	public static boolean isEnabled() {
		return EventBPlugin.getPOMTacticPreference().isEnabled();
	}

	private AutoProver() {
		// Nothing to do.
	}
	
	protected static void run(IPRFile prFile, IPSFile psFile, IPSStatus[] pos,
			IProgressMonitor monitor) throws RodinDBException {
		if (!isEnabled())
			return;
		boolean dirty = false;
		try {
			monitor.beginTask("auto-proving", pos.length);
			for (IPSStatus status : pos) {
				if (monitor.isCanceled()) {
					prFile.makeConsistent(null);
					psFile.makeConsistent(null);
					throw new OperationCanceledException();
				}
				IProgressMonitor subMonitor = new SubProgressMonitor(
						monitor, 
						2, 
						SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK
				);
				dirty |= processPo(prFile, status, subMonitor);
			}
			// monitor.worked(1);
			dirty = true;
			if (dirty) {
				prFile.save(null, false, true);
				psFile.save(null, false, false);
			}
		} finally {
			monitor.done();
		}
	}

	private static boolean processPo(IPRFile prFile, IPSStatus status,
			IProgressMonitor pm) throws RodinDBException {
		
		try {
			pm.beginTask(status.getElementName() + ":", 3);
			
			final boolean proofValid = ! status.isBroken();
			final int proofConfidence = status.getConfidence();
			
			// TODO: should also test for attempts
			if (proofValid && proofConfidence > IConfidence.PENDING) {
				// There is already a proof (maybe partial), skip it
				return false;
			}
			
			pm.subTask("loading");
			final IPOSequent poSequent = status.getPOSequent();
			IProofTree autoProofTree = ProverFactory.makeProofTree(
					POLoader.readPO(poSequent),
					poSequent
			);
			pm.worked(1);

			pm.subTask("proving");
			autoTactic().apply(autoProofTree.getRoot(), new ProofMonitor(pm));
			pm.worked(1);

			pm.subTask("saving");
			IPRProof prProof = status.getProof();
			// Update the tree if it was discharged
			if (autoProofTree.isClosed()) {
				prProof.setProofTree(autoProofTree, null);
				PSWrapper.updateStatus(status,new SubProgressMonitor(pm,1));
				status.setHasManualProof(false,null);
				prFile.save(null, false, true);
				return true;
			}
			// If the auto prover made 'some' progress, and no
			// proof was previously attempted update the proof
			if (autoProofTree.getRoot().hasChildren() && 
					(
							// ( status.getProofConfidence() > IConfidence.UNATTEMPTED) || 
							(! status.getHasManualProof() && !(proofConfidence > IConfidence.PENDING))
					))	

			{
				prProof.setProofTree(autoProofTree, null);
				PSWrapper.updateStatus(status,new SubProgressMonitor(pm,1));
				status.setHasManualProof(false,null);
				// in this case no need to save immediately.
				return true;
			}
			return false;
		} finally {
			pm.done();
		}
	}
	
	private static ITactic autoTactic(){
		return EventBPlugin.getPOMTacticPreference()
				.getSelectedComposedTactic();
	}

}
