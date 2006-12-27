/**
 * 
 */
package org.eventb.internal.core;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSFile;
import org.eventb.core.IPSStatus;
import org.eventb.core.IPSWrapper;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.IProofDependencies;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.eventb.internal.core.pom.AutoPOM;
import org.eventb.internal.core.pom.POLoader;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * 
 */
public class PSWrapper implements IPSWrapper {

	private final IPSFile psFile;

	private final IPRFile prFile;

	public PSWrapper(IPSFile psFile) {
		this.psFile = psFile;
		prFile = psFile.getPRFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofLoader#getPSStatuses()
	 */
	public IPSStatus[] getPSStatuses() throws RodinDBException {
		return psFile.getStatuses();
	}

	public IPSFile getPSFile() {
		return psFile;
	}

	public IProofTree getFreshProofTree(IPSStatus psStatus) throws RodinDBException {
		final IPOSequent poSequent = psStatus.getPOSequent();
		IProverSequent newSeq = POLoader.readPO(poSequent);
		return ProverFactory.makeProofTree(newSeq, poSequent);
	}

	public void setProofTree(final IPSStatus status, final IProofTree pt,
			IProgressMonitor monitor) throws CoreException {
		// TODO add lock for po and pr file
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor mon) throws CoreException {
				try {
					mon.beginTask("Saving Proof", 2);
					status.getProof().setProofTree(pt,
							new SubProgressMonitor(mon, 1));
					AutoPOM.updateStatus(((IPSStatus) status.getMutableCopy()),
							new SubProgressMonitor(mon, 1));
				} finally {
					mon.done();
				}
			}
		}, status.getSchedulingRule(), monitor);
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
		psFile.save(monitor, force);
		prFile.save(monitor, force);
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
		status.setProofConfidence(null);
		status.setBroken(broken, null);
	}

	public IProofSkeleton getProofSkeleton(IPSStatus status, IProgressMonitor monitor) throws RodinDBException {
		final IPRProof prProof = status.getProof();
		if (prProof.exists()) {
			final IProofSkeleton proofSkeleton = prProof.getSkeleton(
					FormulaFactory.getDefault(), monitor);
			return proofSkeleton;
		}
		return null;
	}

}
