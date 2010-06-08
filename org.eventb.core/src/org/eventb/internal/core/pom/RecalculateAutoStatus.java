/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - used proof components
 *******************************************************************************/
package org.eventb.internal.core.pom;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.core.Messages;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.IInternalElement;
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

	private static final String REC_AUTO = "Recalculate auto status"; //$NON-NLS-1$
	
	private RecalculateAutoStatus() {
		// Nothing to do.
	}

	public static void run(IRodinFile prFile, IRodinFile psFile,
			IPSStatus[] pos, IProgressMonitor monitor) throws RodinDBException {
		final Set<IPSStatus> set = new HashSet<IPSStatus>(Arrays.asList(pos));
		run(set, monitor);
	}

	
	public static void run(Set<IPSStatus> pos, IProgressMonitor monitor)
			throws RodinDBException {
		final SubMonitor pm = SubMonitor.convert(monitor, pos.size());
		final Set<IProofComponent> prComps = new HashSet<IProofComponent>();
		try {
			for (IPSStatus status : pos) {
				if (pm.isCanceled()) {
					makeAllConsistent(prComps);
					throw new OperationCanceledException();
				}
				final IProofComponent pc = getProofComponent(status);
				processPo(pc, status, pm.newChild(1, SubMonitor.SUPPRESS_NONE));
				prComps.add(pc);
			}
			saveAll(prComps);
		} finally {
			monitor.done();
		}
	}

	private static void processPo(IProofComponent pc, IPSStatus status,
			SubMonitor pm) throws RodinDBException {
		
		final String poName = status.getElementName();
		if (pc.getProofAttempt(poName, REC_AUTO) != null) {
			// another attempt for REC_AUTO exists: don't process this PO
			return;
		}

		pm.beginTask(poName + ":", 10); //$NON-NLS-1$

		pm.subTask(Messages.progress_RecalculateAutoStatus_loading);
		final IProofAttempt pa = pc.createProofAttempt(poName, REC_AUTO, pm.newChild(1));
		try {
			
			final IProofTree autoProofTree = pa.getProofTree();

			pm.subTask(Messages.progress_RecalculateAutoStatus_proving);
			autoTactic().apply(autoProofTree.getRoot(), new ProofMonitor(pm.newChild(7)));

			pm.subTask(Messages.progress_RecalculateAutoStatus_saving);
			// Update the tree if it was discharged
			if (autoProofTree.isClosed()) {
				pa.commit(false, true, pm.newChild(2));
				
				if (DEBUG) {
					if (status.getHasManualProof()) {
						System.out.println("Proof " + poName + " is now automatic."); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			} else {
				if (DEBUG) {
					if (!status.getHasManualProof()) {
						System.out.println("Proof " + poName + " is now manual."); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				
				status.setHasManualProof(true, null);
			}
		} finally {
			pa.dispose();
			pm.done();
		}
	}

	private static ITactic autoTactic() {
		return EventBPlugin.getAutoTacticPreference()
				.getSelectedComposedTactic();
	}

	private static void makeAllConsistent(Set<IProofComponent> prComps)
			throws RodinDBException {
		for (IProofComponent pc : prComps) {
			pc.makeConsistent(null);
		}
	}

	private static void saveAll(Set<IProofComponent> prComps)
			throws RodinDBException {
		for (IProofComponent pc : prComps) {
			pc.save(null, false);
		}
	}
	
	private static IProofComponent getProofComponent(IInternalElement element) {
		final IProofManager pm = EventBPlugin.getProofManager();
		final IEventBRoot root = (IEventBRoot) element.getRoot();
		final IPRRoot prRoot = root.getPRRoot();
		return pm.getProofComponent(prRoot);
	}

}
