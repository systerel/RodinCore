/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Thai Son Hoang
 * @author Laurent Voisin
 */
@Deprecated
public class PSWrapper implements IPSWrapper {
	
	private static class StampedProofTree {
		final long poStamp;
		final IProofTree tree;

		StampedProofTree(long poStamp, IProofTree tree) {
			this.poStamp = poStamp;
			this.tree = tree;
		}
	}

	private static FormulaFactory ff = FormulaFactory.getDefault();

	final IRodinFile psFile;

//	final IPRFile prFile;
	final IRodinFile prFile;
	
	final Map<IPSStatus, StampedProofTree> loadedTrees;

	public PSWrapper(IRodinFile psFile) {
		this.psFile = psFile;
		IPSRoot root = (IPSRoot) psFile.getRoot();
		this.prFile = root.getPRRoot().getRodinFile();
		this.loadedTrees = new HashMap<IPSStatus, StampedProofTree>();
	}

	public IRodinFile getPRFile() {
		return prFile;
	}

	public IRodinFile getPSFile() {
		return psFile;
	}

	public IPSStatus[] getPSStatuses() throws RodinDBException {
		IPSRoot root = (IPSRoot) psFile.getRoot();
		return root.getStatuses();
	}

	private IProofTree createFreshProofTree(IPSStatus psStatus)
			throws RodinDBException {
		final IPOSequent poSequent = psStatus.getPOSequent();
		final IProverSequent rootSeq = POLoader.readPO(poSequent);
		final IProofTree pt = ProverFactory.makeProofTree(rootSeq, poSequent);
		final long poStamp = poSequent.getPOStamp(); 
		loadedTrees.put(psStatus, new StampedProofTree(poStamp, pt));
		return pt;
	}
	
	public IProofTree getFreshProofTree(IPSStatus psStatus)
			throws RodinDBException {
		return createFreshProofTree(psStatus);
	}

	public IProofSkeleton getProofSkeleton(IPSStatus status,
			IProgressMonitor monitor) throws RodinDBException {
		final IPRProof prProof = status.getProof();
		if (prProof.exists()) {
			final IProofSkeleton proofSkeleton = prProof.getSkeleton(
					ff, monitor);
			return proofSkeleton;
		}
		return null;
	}

	@Deprecated
	public void setProofTree(final IPSStatus status, final IProofTree pt,
			IProgressMonitor monitor) throws RodinDBException {
			setProofTree(status, pt, true, monitor);
		}

	@Deprecated
	public void setProofTree(final IPSStatus status, final IProofTree pt,
			final boolean hasManualProof, IProgressMonitor monitor)
			throws RodinDBException {
		
		StampedProofTree spt = loadedTrees.get(status);
		if (spt == null || spt.tree != pt) {
			throw new IllegalArgumentException("Unexpected proof tree");
		}
		updateStatus(status, hasManualProof, monitor);
	}
	
	// TODO apparently unused
	public void makeFresh(IProgressMonitor monitor) throws RodinDBException {

		if (!prFile.exists())
			prFile.create(true, monitor);
		// project.createRodinFile(prFile.getElementName(), true, monitor);

		// Create a fresh PS file
		// TODO : modify once signatures are implemented
		if (!psFile.exists())
			psFile.create(true, monitor);
		else {
			IPSRoot root = (IPSRoot) psFile.getRoot();
			IPSStatus[] statuses = root.getStatuses();
			for (IPSStatus status : statuses) {
				status.delete(true, monitor);
			}
		}
	}

	// TODO apparently unused
	public void makeConsistent() throws RodinDBException {
		prFile.makeConsistent(null);
		psFile.makeConsistent(null);
	}

	public void save(IProgressMonitor monitor, boolean force)
			throws RodinDBException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask("Saving proof files", 2);
			prFile.save(new SubProgressMonitor(monitor, 1), force, true);
			psFile.save(new SubProgressMonitor(monitor, 1), force, false);
		} finally {
			monitor.done();
		}
	}

	public IPSStatus getPSStatus(String name) {
		IPSRoot root = (IPSRoot) psFile.getRoot();
		return root.getStatus(name);
	}

	public void updateStatus(final IPSStatus psStatus,
			final boolean hasManualProof, final IProgressMonitor monitor)
			throws RodinDBException {

		final StampedProofTree spt = loadedTrees.get(psStatus);
		if (spt == null) {
			throw new IllegalStateException("Unknown proof tree");
		}

		final IPSStatus psHandle = (IPSStatus) psStatus.getMutableCopy();
		final IPRProof proof = psStatus.getProof();
		final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor pm) throws RodinDBException {
				try {
					pm.beginTask("Saving Proof", 4);
					// TODO create Proof file if needed.
					proof.setProofTree(spt.tree, new SubProgressMonitor(pm, 1));
					proof.setHasManualProof(hasManualProof,
							new SubProgressMonitor(pm, 1));
					updateStatus(psHandle, new SubProgressMonitor(pm, 1));
					psHandle.setPOStamp(spt.poStamp, new SubProgressMonitor(pm,
							1));
				} finally {
					pm.done();
				}
			}
		};
		final ISchedulingRule rule = MultiRule.combine(psFile
				.getSchedulingRule(), prFile.getSchedulingRule());
		RodinCore.run(runnable, rule, monitor);	
	}

	//	 lock po & pr files before calling this method
	public static void updateStatus(IPSStatus status, IProgressMonitor pm)
			throws RodinDBException {

		final IPOSequent poSequent = status.getPOSequent();
		final IProverSequent seq =  POLoader.readPO(poSequent);
		final IPRProof prProof = status.getProof();
		final boolean broken;
		if (prProof.exists()) {
			IProofDependencies deps = prProof.getProofDependencies(ff, pm);
			broken = ! ProverLib.proofReusable(deps,seq);
		} else {
			broken = false;
		}
		status.copyProofInfo(null);
		if (poSequent.hasPOStamp()) {
			status.setPOStamp(poSequent.getPOStamp(), null);
		}
		status.setBroken(broken, null);
	}

}
