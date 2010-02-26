/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich.
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
 *     Systerel - replaced subProgressMonitor by SubMonitor and refactored 
 *******************************************************************************/
package org.eventb.internal.core.pom;

import static org.eventb.core.seqprover.IConfidence.PENDING;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

/**
 * @author Laurent Voisin
 *
 */
public final class AutoProver {
	
	public static final String AUTO_PROVER = "auto-prover";

	private static final IAutoTacticPreference PREF = EventBPlugin
			.getAutoTacticPreference();

	public static boolean isEnabled() {
		return PREF.isEnabled();
	}

	private AutoProver() {
		// Nothing to do.
	}
	
	public static void run(IProofComponent pc, IPSStatus[] pos,
			IProgressMonitor monitor) throws RodinDBException {
		boolean dirty = false;
		final SubMonitor subMonitor = SubMonitor.convert(monitor,100);
		subMonitor.beginTask("Auto-proving...", pos.length);
		for (IPSStatus status : pos) {
			if (monitor.isCanceled()) {
				pc.makeConsistent(null);
				throw new OperationCanceledException();
			}
			dirty |= processPo(pc, status, subMonitor.newChild(1));
		}
		dirty = true;
		if (dirty) {
			pc.save(null, false);
		}
	}

	private static boolean processPo(IProofComponent pc, IPSStatus status,
			IProgressMonitor pm) throws RodinDBException {
		
		final String poName = status.getElementName();
		final IProofAttempt pa = load(pc, poName, pm);
		try {
			prove(pa, poName, pm);
			return commit(pa, poName, pm);
		} finally {
			pa.dispose();
		}
	}

	// Consumes one tick of the given progress monitor
	private static IProofAttempt load(IProofComponent pc, String poName,
			IProgressMonitor pm) throws RodinDBException {
		pm.subTask(poName+" : loading");
		return pc.createProofAttempt(poName, AUTO_PROVER, pm);
	}
	
	// Consumes one tick of the given progress monitor
	private static void prove(IProofAttempt pa, String poName,
			IProgressMonitor pm) {
		pm.subTask(poName+" : proving");
		final ITactic tactic = PREF.getSelectedComposedTactic();
		tactic.apply(pa.getProofTree().getRoot(), new ProofMonitor(pm));
	}

	// Consumes one tick of the given progress monitor
	private static boolean commit(IProofAttempt pa,String poName, IProgressMonitor pm)
			throws RodinDBException {
		pm.subTask(poName+" : committing");
		if (shouldCommit(pa)) {
			pa.commit(false, true, new SubProgressMonitor(pm, 1));
			return true;
		}
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
