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
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.internal.core.PSWrapper;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

/**
 * This class implements a run method that reruns the current auto prover on ALL given IPSStatus elements. The main aim of this method is 
 * to update the "has manual proof" status to reflect the current auto provers selection. In case a proof was previously automatically
 * discharged and is no more, it will be marked as manually created.
 * 
 * This is intended to be used as a post-development operation to estimate the %age of automated proofs over a changing auto prover.
 * It is recommended to restore to the default autoprovers before runnning this.
 * 
 * WARNING : This code is not yet mature.
 * WARNING : The run method will discard all proofs (manual or otherwise) that can now be proven automatically.
 * 
 * @author Farhad Mehta
 *
 */
public final class RecalculateAutoStatus {

//	public static boolean isEnabled() {
//		return EventBPlugin.getPOMTacticPreference().isEnabled();
//	}

	private RecalculateAutoStatus() {
		// Nothing to do.
	}
	
	public static void run(IPRFile prFile, IPSFile psFile, IPSStatus[] pos,
			IProgressMonitor monitor) throws RodinDBException {
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
				processPo(prFile, status, subMonitor);
			}
			prFile.save(null, false, true);
			psFile.save(null, false, false);
		} finally {
			monitor.done();
		}
	}

	private static boolean processPo(IPRFile prFile, IPSStatus status,
			IProgressMonitor pm) throws RodinDBException {
		
		try {
			pm.beginTask(status.getElementName() + ":", 3);

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
			}
			else
			{
				status.setHasManualProof(true,null);
			}
			prFile.save(null, false, true);
			return true;
			
		} finally {
			pm.done();
		}
	}
	
	private static ITactic autoTactic(){
		// if (isEnabled())
			return EventBPlugin.getPOMTacticPreference().getSelectedComposedTactic();
		// else return BasicTactics.failTac("Auto Prover Disabled");
	}

}
