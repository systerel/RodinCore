package org.eventb.internal.core.pom;

import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.externalReasoners.ExternalML;
import org.eventb.core.prover.tactics.Tactics;
import org.rodinp.core.RodinDBException;

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
	
	public void run(IPRFile prFile) throws RodinDBException {
		final IPRSequent[] pos = (IPRSequent[]) prFile.getSequents();
		for (IPRSequent po : pos) {
			if (! po.isDischarged()) {
				IProofTree tree = po.makeProofTree();
				run(tree);
				if (tree.isDischarged()) {
					po.updateStatus(tree);
					prFile.save(null, false);
				}
			}
		}
	}
	
	private void run(IProofTree pt) {
		if (! enabled)
			return;
		
		// First try applying an internal tactic
		Tactics.norm().apply(pt.getRoot());
		if (pt.isDischarged())
			return;
		
		// Then, try with the legacy provers.
		pt.getRoot().pruneChildren();
		final int MLforces = ExternalML.Input.FORCE_0 | ExternalML.Input.FORCE_1;
		Tactics.externalML(MLforces, timeOutDelay, null).apply(pt.getRoot());
		if (! pt.isDischarged()) {
			Tactics.externalPP(false, timeOutDelay, null).apply(pt.getRoot());
		}
	}
	
}
