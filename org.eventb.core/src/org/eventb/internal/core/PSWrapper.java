/**
 * 
 */
package org.eventb.internal.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * 
 */
public class PSWrapper implements IPSWrapper {
	
	private static class StampedProofTree {
		long poStamp;
		IProofTree tree;

		StampedProofTree(long poStamp, IProofTree tree) {
			this.poStamp = poStamp;
			this.tree = tree;
		}
	}

	final IPSFile psFile;

	final IPRFile prFile;
	
	final Map<IPSStatus, StampedProofTree> loadedTrees;

	public PSWrapper(IPSFile psFile) {
		this.psFile = psFile;
		this.prFile = psFile.getPRFile();
		this.loadedTrees = new HashMap<IPSStatus, StampedProofTree>();
	}

	public IPSFile getPSFile() {
		return psFile;
	}

	public IPSStatus[] getPSStatuses() throws RodinDBException {
		return psFile.getStatuses();
	}

	private void createFreshProofTree(IPSStatus psStatus)
			throws RodinDBException {
		final IPOSequent poSequent = psStatus.getPOSequent();
		final long poStamp = poSequent.getPOStamp(); 
		final IProverSequent rootSeq = POLoader.readPO(poSequent);
		final IProofTree pt = ProverFactory.makeProofTree(rootSeq, poSequent);
		loadedTrees.put(psStatus, new StampedProofTree(poStamp, pt));
	}
	
	public IProofTree getProofTree(IPSStatus psStatus) {
		StampedProofTree ps = loadedTrees.get(psStatus);
		if (ps == null) {
			return null;
		}
		return ps.tree;
	}

	public IProofTree getFreshProofTree(IPSStatus psStatus)
			throws RodinDBException {
		createFreshProofTree(psStatus);
		return getProofTree(psStatus);
	}

	public IProofSkeleton getProofSkeleton(IPSStatus status,
			IProgressMonitor monitor) throws RodinDBException {
		final IPRProof prProof = status.getProof();
		if (prProof.exists()) {
			final IProofSkeleton proofSkeleton = prProof.getSkeleton(
					FormulaFactory.getDefault(), monitor);
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
	
	public void makeFresh(IProgressMonitor monitor) throws RodinDBException {

		if (!prFile.exists())
			prFile.create(true, monitor);
		// project.createRodinFile(prFile.getElementName(), true, monitor);

		// Create a fresh PS file
		// TODO : modify once signatures are implemented
		if (!psFile.exists())
			psFile.create(true, monitor);
		else {
			IPSStatus[] statuses = psFile.getStatuses();
			for (IPSStatus status : statuses) {
				status.delete(true, monitor);
			}
		}
	}

	public void makeConsistent() throws RodinDBException {
		prFile.makeConsistent(null);
		psFile.makeConsistent(null);
	}

	public void save(IProgressMonitor monitor, boolean force)
			throws RodinDBException {
		psFile.save(monitor, force, false);
		prFile.save(monitor, force, true);
	}

	public void clean() throws RodinDBException {
		if (psFile.exists()) {
			psFile.delete(true, null);
		}

		// Don't delete the PR file, it contains user proofs.
	}

	public IPRProof getPRProof(String name) {
		return prFile.getProof(name);
	}

	public IPSStatus getPSStatus(String name) {
		return psFile.getStatus(name);
	}

	public void updateStatus(IPSStatus status, IProgressMonitor monitor)
			throws RodinDBException {
		FormulaFactory ff = FormulaFactory.getDefault();

		IProverSequent seq = POLoader.readPO(status.getPOSequent());
		final IPRProof prProof = status.getProof();
		final boolean broken;
		if (prProof.exists()) {
			IProofDependencies deps = prProof.getProofDependencies(ff, monitor);
			broken = !ProverLib.proofReusable(deps, seq);
		} else {
			broken = false;
		}
		// status.setProofConfidence(null);
		status.copyProofInfo(null);
		status.setBroken(broken, null);
	}

	public void updateStatus(final IPSStatus psStatus,
			final boolean hasManualProof, final IProgressMonitor monitor)
			throws RodinDBException {

		final StampedProofTree spt = loadedTrees.get(psStatus);
		if (spt == null) {
			return;
		}

		final IPSStatus psHandle = (IPSStatus) psStatus.getMutableCopy();
		final IPRProof proof = psStatus.getProof();
		final IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor pm) throws RodinDBException {
				try {
					pm.beginTask("Saving Proof", 4);
					proof.setProofTree(spt.tree, new SubProgressMonitor(pm, 1));
					proof.setHasManualProof(hasManualProof,
							new SubProgressMonitor(pm, 1));
					AutoPOM.updateStatus(psHandle,
							new SubProgressMonitor(pm, 1));
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

}
