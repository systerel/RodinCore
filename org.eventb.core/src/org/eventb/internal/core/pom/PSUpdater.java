/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored for using the Proof Manager API
 *     Systerel - separation of file and root element
 *     Systerel - checked reasoner versions before reusing proofs
 *******************************************************************************/
package org.eventb.internal.core.pom;

import static org.eventb.internal.core.Util.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Implements the status update algorithm for the Proof Manager.
 * 
 * @author Laurent Voisin
 */
public class PSUpdater {

	// access to the proof files
	final IProofComponent pc;
	
	final IPSRoot psRoot;

	final int initialNbOfStatuses;
	
	// Statuses that are in the PS file but not yet updated
	final Set<IPSStatus> unusedStatuses;
	
	final List<IPSStatus> outOfDateStatuses = new ArrayList<IPSStatus>();

	final ElementSorter<IPSStatus> sorter = new ElementSorter<IPSStatus>();
	
	/**
	 * Variables that record proof reuses performance during the entire session.
	 * <p>
	 * Note: Their values are only updated if the <code>AutoPOM.PERF_PROOFREUSE</code> flag is set to
	 * <code>true</code>.
	 * </p> 
	 */
	protected static int totalPOs = 0;
	protected static int newPOs = 0;
	protected static int unchangedPOs = 0;
	protected static int unchangedPOsWithProofs = 0;
	protected static int recoverablePOs = 0;
	protected static int recoverablePOsWithProofs = 0;
	protected static int irrecoverablePOs = 0;
	protected static int irrecoverablePOsWithProofs = 0;

	public PSUpdater(IProofComponent pc, IProgressMonitor pm)
			throws RodinDBException {
		try {
			this.pc = pc;
			psRoot = pc.getPSRoot();
			IRodinFile psFile = psRoot.getRodinFile();
			if (psFile.exists()) {
				final IPSStatus[] ss = psRoot.getStatuses();
				initialNbOfStatuses = ss.length;
				unusedStatuses = new HashSet<IPSStatus>(Arrays.asList(ss));
			} else {
				psFile.create(false, pm);
				initialNbOfStatuses = 0;
				unusedStatuses = Collections.emptySet();
			}
		} finally {
			if (pm != null) {
				pm.done();
			}
		}
	}

	
	public void updatePO(IPOSequent poSequent, IProgressMonitor pm)
			throws CoreException {
		final String poName = poSequent.getElementName();
		final IPSStatus status = pc.getStatus(poName);
		unusedStatuses.remove(status);
		sorter.addItem(status);

		if (! status.exists()) {
			status.create(null, pm);
		}
		if (!hasSameStampAsPo(status) || status.isContextDependent()) {
			if (updateStatus(status, pm)) {
				outOfDateStatuses.add(status);
			}
		}
		else
		{
			if (AutoPOM.PERF_PROOFREUSE) {
				unchangedPOs++;
				if (status.exists() && status.getConfidence() != IConfidence.UNATTEMPTED)
				{
					unchangedPOsWithProofs++;
				}
			}
		}
		if (AutoPOM.PERF_PROOFREUSE) { totalPOs++; }		
	}
	
	public void cleanup(IProgressMonitor ipm) throws RodinDBException {
		final SubMonitor sMonitor = SubMonitor.convert(ipm, "Cleaning up proof statuses", initialNbOfStatuses);
		final ElementSorter.Mover<IPSStatus> mover = new ElementSorter.Mover<IPSStatus>() {
			@Override
			public void move(IPSStatus element, IPSStatus nextSibling)
					throws RodinDBException {
				element.move(psRoot, nextSibling, null, false, sMonitor.split(1));
			}
		};
		removeUnusedStatuses(sMonitor);
		sorter.sort(psRoot.getStatuses(), mover);
	}

	private void removeUnusedStatuses(final IProgressMonitor pm)
			throws RodinDBException {
		final int size = unusedStatuses.size();
		if (size != 0) {
			final SubMonitor spm = SubMonitor.convert(pm, size);
			final IRodinElement[] es = new IRodinElement[size];
			unusedStatuses.toArray(es);
			RodinCore.getRodinDB().delete(es, false, spm);
		}
	}

	// Returns true if the both the status and the corresponding PO sequent
	// carry the same stamp.
	private static boolean hasSameStampAsPo(IPSStatus psStatus)
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
	
	// Returns true if the new status is pending or less
	private boolean updateStatus(IPSStatus status, IProgressMonitor monitor)
			throws CoreException {

		final IPOSequent poSequent = status.getPOSequent();
		final IPRProof prProof = status.getProof();
		boolean broken;
		if (prProof.exists()) {
			final FormulaFactory ff;
			try {
				ff = pc.getSafeFormulaFactory();
				final IProverSequent seq = POLoader.readPO(poSequent, ff);
				broken = isBroken(seq, prProof, monitor);
			} catch (CoreException e) {
				log(e, "while updating status of proof " + prProof);
				broken = true;
			}
			if (AutoPOM.PERF_PROOFREUSE) 
			{
				if (broken) 
				{
					irrecoverablePOs++;
					if (prProof.getConfidence() != IConfidence.UNATTEMPTED)
					{
						irrecoverablePOsWithProofs++;
					}
				} 
				else 
				{
					recoverablePOs++;
					if (prProof.getConfidence() != IConfidence.UNATTEMPTED)
					{
						recoverablePOsWithProofs++;
					}
				}
			}
		} else {
			prProof.create(null, monitor);
			broken = false;
			if (AutoPOM.PERF_PROOFREUSE) newPOs++;
		}
		status.copyProofInfo(null);
		if (poSequent.hasPOStamp()) {
			status.setPOStamp(poSequent.getPOStamp(), null);
		}
		status.setBroken(broken, null);
		return broken == true || status.getConfidence() <= IConfidence.PENDING; 
	}

	// Check only the dependencies of the proof, not all of it.
	private static boolean isBroken(IProverSequent seq, IPRProof prProof,
			IProgressMonitor pm) {
		final SubMonitor sm = SubMonitor.convert(pm, 100);
		try {
			final FormulaFactory seqFactory = seq.getFormulaFactory();
			final FormulaFactory proofFactory = prProof.getFormulaFactory(sm
					.newChild(10));
			if (seqFactory != proofFactory) {
				return true;
			}
			final IProofDependencies deps = prProof
					.getProofDependencies(proofFactory, sm.newChild(75));
			final IProofSkeleton skeleton;
			if (deps.isContextDependent()) {
				skeleton = prProof.getSkeleton(proofFactory, sm.newChild(15));
			} else {
				sm.setWorkRemaining(0);
				skeleton = null;
			}
			return !ProverLib.isProofReusable(deps, seq, skeleton);
		} catch (Throwable e) {
			log(e, "while updating status of proof " + prProof);
			return true;
		} finally {
			if (pm != null) {
				pm.done();
			}
		}
	}

	public IPSStatus[] getOutOfDateStatuses() {
		IPSStatus[] result = new IPSStatus[outOfDateStatuses.size()];
		return outOfDateStatuses.toArray(result);
	}

}
