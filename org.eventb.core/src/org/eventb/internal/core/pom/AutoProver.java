package org.eventb.internal.core.pom;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRProofTree;
import org.eventb.core.IPRSequent;
import org.eventb.core.basis.PRProofTree;
import org.eventb.core.seqprover.IProofTree;

import org.eventb.core.seqprover.tactics.Tactics;

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

	public AutoProver() {
		// Nothing to do.
	}
	
	public void run(IPRFile prFile) throws CoreException {
		final IPRSequent[] pos = (IPRSequent[]) prFile.getSequents();
		boolean dirty = false;
		for (IPRSequent po : pos) {
			if (! po.isClosed()) {

				// System.out.println("AutoProver tried for "+po);
				
				// Go ahead even if a proof was previously attempted
				IProofTree tree = po.makeFreshProofTree();
				run(tree);
				// Update the tree if it was discharged
				final IPRProofTree proofTree = po.getProofTree();
				if (tree.isClosed()) {
					po.updateProofTree(tree);
					((PRProofTree)proofTree).setAutomaticallyGenerated();
					prFile.save(null, false);
					dirty = false;
				}
				// If the auto prover made 'some' progress, and no
				// proof was previously attempted update the proof
				else if (tree.getRoot().hasChildren() && 
						(proofTree == null ||
								proofTree.isAutomaticallyGenerated()))
				{
					po.updateProofTree(tree);
					((PRProofTree)proofTree).setAutomaticallyGenerated();
					// in this case no need to save immediately.
					dirty = true;
				}
			}
		}
		if (dirty) prFile.save(null, false);
	}
	
	private void run(IProofTree pt) {
		if (! enabled)
			return;
		Tactics.autoProver(null,timeOutDelay).apply(pt.getRoot());
		
//		// First try applying an internal tactic
//		Tactics.norm().apply(pt.getRoot());
//		if (pt.isClosed())
//			return;
//		
//		// Then, try with the legacy provers.
//		// pt.getRoot().pruneChildren();
//		final int MLforces = ExternalML.Input.FORCE_0 | ExternalML.Input.FORCE_1;
//		BasicTactics.onAllPending(Tactics.externalML(MLforces, timeOutDelay, null)).apply(pt.getRoot());
//		if (! pt.isClosed()) {
//			BasicTactics.onAllPending(Tactics.externalPP(false, timeOutDelay, null)).apply(pt.getRoot());
//		}
	}
	
}
