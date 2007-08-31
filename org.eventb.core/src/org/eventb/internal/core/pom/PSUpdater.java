/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pom;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.rodinp.core.RodinDBException;

/**
 * Implements the status update algorithm for the Proof Manager.
 * 
 * @author Laurent Voisin
 */
public class PSUpdater {

	private static FormulaFactory ff = FormulaFactory.getDefault();
	
	// access to the PR and PS files
	final IPSWrapper psWrapper;
	
	// Statuses that are in the PS file but not yet updated
	final Set<IPSStatus> unusedStatuses;

	public PSUpdater(IPSWrapper psWrapper, IProgressMonitor pm)
			throws RodinDBException {
		try {
			this.psWrapper = psWrapper;
			final IPSFile psFile = psWrapper.getPSFile();
			if (psFile.exists()) {
				final IPSStatus[] ss = psWrapper.getPSStatuses();
				this.unusedStatuses = new HashSet<IPSStatus>(Arrays.asList(ss));
			} else {
				psFile.create(false, pm);
				this.unusedStatuses = Collections.emptySet();
			}
		} finally {
			if (pm != null) {
				pm.done();
			}
		}
	}

	public void updatePO(IPOSequent poSequent, IProgressMonitor pm)
			throws RodinDBException {
		final String poName = poSequent.getElementName();
		final IPSStatus status = psWrapper.getPSStatus(poName);
		unusedStatuses.remove(status);
		if (! status.exists()) {
			status.create(null, pm);
		}
		if (!hasSameStampAsPo(status)) {
			updateStatus(status, pm);
		}
	}
	
	public void cleanup(IProgressMonitor pm) throws RodinDBException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		final int size = unusedStatuses.size();
		try {
			pm.beginTask("Cleaning up unused proof statuses", size);
			for (IPSStatus psStatus: unusedStatuses) {
				psStatus.delete(false, new SubProgressMonitor(pm, 1));
			}
		} finally {
			pm.done();
		}
	}
	
	// Returns true if the both the status and the corresponding PO sequent
	// carry the same stamp.
	private boolean hasSameStampAsPo(IPSStatus psStatus)
			throws RodinDBException {
		if (! psStatus.exists() || ! psStatus.hasPOStamp()) {
			return false;
		}
		final IPOSequent poSequent = psStatus.getPOSequent();
		if (! poSequent.exists() || ! poSequent.hasPOStamp()) {
			return false;
		}
		return poSequent.getPOStamp() == psStatus.getPOStamp();
	}

	private void updateStatus(IPSStatus status, IProgressMonitor monitor)
			throws RodinDBException {

		final IPOSequent poSequent = status.getPOSequent();
		final IProverSequent seq = POLoader.readPO(poSequent);
		final IPRProof prProof = status.getProof();
		final boolean broken;
		if (prProof.exists()) {
			IProofDependencies deps = prProof.getProofDependencies(ff, monitor);
			broken = !ProverLib.proofReusable(deps, seq);
		} else {
			prProof.create(null, monitor);
			broken = false;
		}
		status.copyProofInfo(null);
		if (poSequent.hasPOStamp()) {
			status.setPOStamp(poSequent.getPOStamp(), null);
		}
		status.setBroken(broken, null);
	}

}
