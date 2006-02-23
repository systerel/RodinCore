package org.eventb.internal.core.pom;

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.tactics.Tactics;

public class AutoProver {
	
	// Default delay for automatic proofs: 2 seconds
	private static long timeOutDelay = 2 * 1000;
	
	private static boolean enabled = true;
	
	public AutoProver() {
		// Nothing to do.
	}
	
	public void run(IProofTree pt) {
		if (! enabled)
			return;
		
		// First try applying an internal tactic
		Tactics.norm().apply(pt.getRoot());
		if (pt.isDischarged())
			return;
		
		// Then, try with the legacy provers.
		Tactics.legacyProvers(timeOutDelay).apply(pt.getRoot());
	}

	public static void setTimeOutDelay(long value) {
		timeOutDelay = value;
	}

	public static void enable() {
		enabled = true;
	}
	
	public static void disable() {
		enabled = false;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
	
}
