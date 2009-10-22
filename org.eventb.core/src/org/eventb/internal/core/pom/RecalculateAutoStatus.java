/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pom;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.PSWrapper;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * This class implements a run method that reruns the current auto prover on ALL
 * given IPSStatus elements. The main aim of this method is to update the "has
 * manual proof" status to reflect the current auto provers selection. In case a
 * proof was previously automatically discharged and is no more, it will be
 * marked as manually created.
 * 
 * This is intended to be used as a post-development operation to estimate the
 * percentage of automated proofs over a changing auto prover. It is recommended
 * to restore to the default autoprovers before running this.
 * 
 * WARNING : This code is not yet mature. WARNING : The run method will discard
 * all proofs (manual or otherwise) that can now be discharged automatically.
 * 
 * @author Farhad Mehta
 * 
 */
public final class RecalculateAutoStatus {

	public static boolean DEBUG;

	private RecalculateAutoStatus() {
		// Nothing to do.
	}

	public static void run(IRodinFile prFile, IRodinFile psFile, IPSStatus[] pos,
			IProgressMonitor monitor) throws RodinDBException {
		try {
			monitor.beginTask("auto-proving", pos.length);
			for (IPSStatus status : pos) {
				if (monitor.isCanceled()) {
					prFile.makeConsistent(null);
					psFile.makeConsistent(null);
					throw new OperationCanceledException();
				}
				IProgressMonitor subMonitor = new SubProgressMonitor(monitor,
						2, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				processPo(prFile, status, subMonitor);
			}
			prFile.save(null, false, true);
			psFile.save(null, false, false);
		} finally {
			monitor.done();
		}
	}

	private static boolean processPo(IRodinFile prFile, IPSStatus status,
			IProgressMonitor pm) throws RodinDBException {

		try {
			pm.beginTask(status.getElementName() + ":", 3);

			pm.subTask("loading");
			final IPOSequent poSequent = status.getPOSequent();
			final IProverSequent sequent = POLoader.readPO(poSequent);
			final IProofTree autoProofTree = ProverFactory.makeProofTree(sequent,
					poSequent);
			pm.worked(1);

			pm.subTask("proving");
			autoTactic().apply(autoProofTree.getRoot(), new ProofMonitor(pm));
			pm.worked(1);

			pm.subTask("saving");
			final IPRProof prProof = status.getProof();
			// Update the tree if it was discharged
			if (autoProofTree.isClosed()) {
				prProof.setProofTree(autoProofTree, null);
				
				if (DEBUG) {
					if (status.getHasManualProof()) {
						System.out.println("Proof " + status.getElementName() + " is now automatic.");
					}
				}
				
				PSWrapper.updateStatus(status, new SubProgressMonitor(pm, 1));				
				status.setHasManualProof(false, null);
			} else {
				if (DEBUG) {
					if (!status.getHasManualProof()) {
						System.out.println("Proof " + status.getElementName() + " is now manual.");
					}
				}
				
				status.setHasManualProof(true, null);
			}
			return true;

		} finally {
			pm.done();
		}
	}

	private static ITactic autoTactic() {
		return EventBPlugin.getAutoTacticPreference()
				.getSelectedComposedTactic();
	}

}
