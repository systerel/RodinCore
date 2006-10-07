package org.eventb.internal.core.pom;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRSequent;
import org.eventb.core.basis.PRProofTree;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.core.seqprover.tactics.Tactics;
import org.eventb.internal.core.ProofMonitor;
import org.rodinp.core.RodinDBException;

import com.b4free.rodin.core.B4FreeTactics;
import com.b4free.rodin.core.ExternalML;

public class AutoProver {
	
	private static boolean enabled = true;
	
	// Default delay for automatic proofs: 2 seconds
	private static long timeOutDelay = 2 * 1000;
	
	public static void disable() {
		enabled = false;
	}
	
	public static void enable() {
		enabled = true;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setTimeOutDelay(long value) {
		timeOutDelay = value;
	}

	private AutoProver() {
		// Nothing to do.
	}
	
	protected static void run(IPRFile prFile, IProgressMonitor monitor) throws CoreException {
		if (! enabled)
			return;
		final IPRSequent[] pos = prFile.getSequents();
		boolean dirty = false;
		try{
			monitor.beginTask("Auto-proving", pos.length);
			for (IPRSequent po : pos) {
				if (monitor.isCanceled()) {
					prFile.makeConsistent(null);
					throw new OperationCanceledException();
				}
				IProgressMonitor subMonitor = new SubProgressMonitor(
						monitor, 
						1, 
						SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK
				);
				dirty |= processPo(prFile, po, subMonitor);
			}
			// monitor.worked(1);
			if (dirty) prFile.save(null, false);
		}
		finally
		{
			monitor.done();
		}
	}

	private static boolean processPo(IPRFile prFile, IPRSequent po,
			IProgressMonitor pm) throws RodinDBException {
		
		try {
			pm.beginTask(po.getName() + ":", 3);
			
			pm.subTask("loading");
			IPRProofTree proofTree = po.getProofTree();
			if (proofTree == null)
				proofTree = prFile.createProofTree(po.getName());
			pm.worked(1);
			
			if (po.isProofBroken() || (!proofTree.isClosed())) {
				IProofTree tree = ProverFactory.makeProofTree(POLoader.readPO(po.getPOSequent()));

				pm.subTask("proving");
				autoTactic().apply(tree.getRoot(), new ProofMonitor(pm));
				pm.worked(1);
				
				pm.subTask("saving");
				// Update the tree if it was discharged
				if (tree.isClosed()) {
					// po.updateProofTree(tree);
					proofTree.setProofTree(tree);
					po.updateStatus();
					// if (proofTree == null) proofTree = po.getProofTree();
					proofTree.setAutomaticallyGenerated();
					prFile.save(null, false);
					return false;
				}
				// If the auto prover made 'some' progress, and no
				// proof was previously attempted update the proof
				if (tree.getRoot().hasChildren() && 
						(
								(! proofTree.proofAttempted()) 
								|| (proofTree.isAutomaticallyGenerated() && !proofTree.isClosed())
						))
				{
					// po.updateProofTree(tree);
					proofTree.setProofTree(tree);
					po.updateStatus();
					proofTree.setAutomaticallyGenerated();
					
					((PRProofTree)proofTree).setAutomaticallyGenerated();
					// in this case no need to save immediately.
					return true;
				}
			}
			return false;
		} finally {
			pm.done();
		}
	}
	
//	private void run(IProofTree pt) {
//		if (! enabled)
//			return;
//		B4FreeTactics.autoProver(null,timeOutDelay).apply(pt.getRoot());
//		
////		// First try applying an internal tactic
////		B4FreeTactics.norm().apply(pt.getRoot());
////		if (pt.isClosed())
////			return;
////		
////		// Then, try with the legacy provers.
////		// pt.getRoot().pruneChildren();
////		final int MLforces = ExternalML.Input.FORCE_0 | ExternalML.Input.FORCE_1;
////		BasicTactics.onAllPending(B4FreeTactics.externalML(MLforces, timeOutDelay, null)).apply(pt.getRoot());
////		if (! pt.isClosed()) {
////			BasicTactics.onAllPending(B4FreeTactics.externalPP(false, timeOutDelay, null)).apply(pt.getRoot());
////		}
//	}
	
	public static ITactic autoTactic(){
		final int MLforces = 
			ExternalML.Input.FORCE_0 |
			ExternalML.Input.FORCE_1;
		return BasicTactics.compose(
				Tactics.lasoo(),
				BasicTactics.onAllPending(Tactics.norm()),
				BasicTactics.onAllPending(
						B4FreeTactics.externalML(MLforces, timeOutDelay)), // ML
				BasicTactics.onAllPending(
						B4FreeTactics.externalPP(true, timeOutDelay)), // P1
				BasicTactics.onAllPending(
						B4FreeTactics.externalPP(false, timeOutDelay)) // PP
				);
	}

}
