package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerInput;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.sequent.IProverSequent;

public abstract class LegacyProversTest extends TestCase {
	
	/**
	 * Checks that the given legacy prover failed to prove the given sequent.
	 * 
	 * @param sequent
	 *            the sequent to prove
	 */
	protected static void assertFailure(IProverSequent sequent,
			IExternalReasoner prover, IExtReasonerInput input) {
		IExtReasonerOutput extReaOut = prover.apply(sequent, input);
		assertTrue(extReaOut instanceof UnSuccessfulExtReasonerOutput);
	}

	/**
	 * Checks that the given legacy prover succeeded in proving the given sequent.
	 * 
	 * @param sequent
	 *            the sequent to prove
	 */
	protected static void assertSuccess(IProverSequent sequent,
			IExternalReasoner prover, IExtReasonerInput input) {
		Predicate newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(
				sequent, prover, input);
		assertTrue(newGoalPredicate.equals(Lib.True));
	}
	
}
