/*******************************************************************************
 * Copyright (c) 2005, 2017 ETH Zurich and others.
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

import static org.eventb.core.seqprover.IConfidence.PENDING;
import static org.eventb.internal.core.pom.AutoPOM.tryMakeConsistent;
import static org.eventb.internal.core.preferences.PreferenceUtils.getSimplifyProofPref;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
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
		final SubMonitor sMonitor = SubMonitor.convert(monitor, "auto-proving", pos.length + 1);
		boolean dirty = false;
		try {
			for (IPSStatus status : pos) {
				dirty |= processPo(pc, status, sMonitor.split(1));
			}
			dirty = true;
			if (dirty) {
				pc.save(sMonitor.split(1), false);
			} else {
				sMonitor.worked(1);
			}
		} catch(OperationCanceledException e) {
			tryMakeConsistent(pc);
			throw e;
		} finally {
			monitor.done();
		}
	}

	public static void run(IPSStatus[] pos, IProgressMonitor monitor)
			throws RodinDBException {
		final SubMonitor sMonitor = SubMonitor.convert(monitor,
				"auto-proving", pos.length);
		final IProofManager pm = EventBPlugin.getProofManager();
		try {
			for (IPSStatus status : pos) {
				final IPSRoot psRoot = (IPSRoot) status.getRoot();
				final IProofComponent pc = pm
						.getProofComponent(psRoot);

				run(pc, new IPSStatus[] {status}, sMonitor.split(1));
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
		final SubMonitor sMonitor = SubMonitor.convert(pm, 1);
		sMonitor.subTask("loading");
		return pc.createProofAttempt(poName, AUTO_PROVER, sMonitor.split(1));
	}
	
	// Consumes one tick of the given progress monitor
	private static void prove(IProofAttempt pa, IProgressMonitor pm) {
		final SubMonitor sMonitor = SubMonitor.convert(pm, 1);
		sMonitor.subTask("proving");
		final IEventBRoot poRoot = pa.getComponent().getPORoot();
		final ITactic tactic = AUTOTACTIC_MANAGER.getSelectedAutoTactics(poRoot);
		tactic.apply(pa.getProofTree().getRoot(), new ProofMonitor(sMonitor.split(1)));
	}

	// Consumes one tick of the given progress monitor
	private static boolean commit(IProofAttempt pa, IProgressMonitor pm)
			throws RodinDBException {
		final SubMonitor sMonitor = SubMonitor.convert(pm, 1);
		sMonitor.subTask("committing");
		if (shouldCommit(pa)) {
			pa.commit(false, getSimplifyProofPref(), sMonitor.split(1));
			return true;
		}
		sMonitor.worked(1);
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
