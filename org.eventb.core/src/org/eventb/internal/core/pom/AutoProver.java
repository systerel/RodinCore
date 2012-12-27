/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored for using the Proof Manager API
 *     Systerel - refactored code to improve maintainability
 *     Systerel - added proof simplification on commit
 *     Systerel - fixed bar progression
 *     Systerel - added simplify proof preference
 *******************************************************************************/
package org.eventb.internal.core.pom;

import static org.eclipse.core.runtime.SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK;
import static org.eclipse.core.runtime.SubProgressMonitor.SUPPRESS_SUBTASK_LABEL;
import static org.eventb.core.seqprover.IConfidence.PENDING;
import static org.eventb.internal.core.preferences.PreferenceUtils.getSimplifyProofPref;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.preferences.autotactics.IAutoPostTacticManager;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

/**
 * @author Laurent Voisin
 *
 */
public final class AutoProver {
	
	public static final String AUTO_PROVER = "auto-prover";

	private static final IAutoPostTacticManager AUTOTACTIC_MANAGER = EventBPlugin
			.getAutoPostTacticManager();

	public static boolean isEnabled() {
		return AUTOTACTIC_MANAGER.getAutoTacticPreference().isEnabled();
	}

	private AutoProver() {
		// Nothing to do.
	}
	
	public static void run(IProofComponent pc, IPSStatus[] pos,
			IProgressMonitor monitor) throws RodinDBException {
		boolean dirty = false;
		try {
			monitor.beginTask("auto-proving", pos.length + 1);
			for (IPSStatus status : pos) {
				if (monitor.isCanceled()) {
					pc.makeConsistent(null);
					throw new OperationCanceledException();
				}
				final IProgressMonitor subMonitor = new SubProgressMonitor(
						monitor, 1, PREPEND_MAIN_LABEL_TO_SUBTASK);
				dirty |= processPo(pc, status, subMonitor);
			}
			dirty = true;
			if (dirty) {
				final IProgressMonitor subMonitor = new SubProgressMonitor(
						monitor, 1, SUPPRESS_SUBTASK_LABEL);
				pc.save(subMonitor, false);
			}
		} finally {
			monitor.done();
		}
	}

	public static void run(IPSStatus[] pos, IProgressMonitor monitor)
			throws RodinDBException {
		final SubMonitor subMonitor = SubMonitor.convert(monitor,
				"auto-proving", pos.length);
		final IProofManager pm = EventBPlugin.getProofManager();
		try {
			for (IPSStatus status : pos) {
				final IPSRoot psRoot = (IPSRoot) status.getRoot();
				final IProofComponent pc = pm
						.getProofComponent(psRoot);

				run(pc, new IPSStatus[] {status}, subMonitor.newChild(1));
			}
		} finally {
			monitor.done();
		}
	}
	
	private static boolean processPo(IProofComponent pc, IPSStatus status,
			IProgressMonitor pm) throws RodinDBException {
		
		final String poName = status.getElementName();
		try {
			pm.beginTask(poName + ":", 3);
			final IProofAttempt pa = load(pc, poName, pm);
			try {
				prove(pa, pm);
				return commit(pa, pm);
			} finally {
				pa.dispose();
			}
		} finally {
			pm.done();
		}
	}

	// Consumes one tick of the given progress monitor
	private static IProofAttempt load(IProofComponent pc, String poName,
			IProgressMonitor pm) throws RodinDBException {
		pm.subTask("loading");
		final SubProgressMonitor spm = new SubProgressMonitor(pm, 1);
		return pc.createProofAttempt(poName, AUTO_PROVER, spm);
	}
	
	// Consumes one tick of the given progress monitor
	private static void prove(IProofAttempt pa, IProgressMonitor pm) {
		pm.subTask("proving");
		final IEventBRoot poRoot = pa.getComponent().getPORoot();
		final ITactic tactic = AUTOTACTIC_MANAGER.getSelectedAutoTactics(poRoot);
		final SubProgressMonitor spm = new SubProgressMonitor(pm, 1);
		tactic.apply(pa.getProofTree().getRoot(), new ProofMonitor(spm));
	}

	// Consumes one tick of the given progress monitor
	private static boolean commit(IProofAttempt pa, IProgressMonitor pm)
			throws RodinDBException {
		pm.subTask("committing");
		if (shouldCommit(pa)) {
			pa.commit(false, getSimplifyProofPref(), new SubProgressMonitor(pm, 1));
			return true;
		}
		pm.worked(1);
		return false;
	}

	private static boolean shouldCommit(IProofAttempt pa)
			throws RodinDBException {
		final IProofTree pt = pa.getProofTree();
		if (pt.isClosed()) {
			// The auto-prover discharged the PO.
			return true;
		}
		if (pt.getRoot().hasChildren()) {
			// The auto prover made 'some' progress
			final IPSStatus prevStatus = pa.getStatus();
			final boolean wasAuto = !prevStatus.getHasManualProof();
			final boolean wasUseless = prevStatus.getConfidence() <= PENDING;
			if (wasAuto && wasUseless) {
				return true;
			}
		}
		return false;
	}
}
