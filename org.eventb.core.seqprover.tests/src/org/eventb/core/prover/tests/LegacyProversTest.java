package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExtReasonerOutput;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.UnSuccessfulExtReasonerOutput;
import org.eventb.core.prover.externalReasoners.LegacyProvers;
import org.eventb.core.prover.sequent.IProverSequent;

public class LegacyProversTest extends TestCase {
	
	private static IExternalReasoner legacyProvers = new LegacyProvers();
	
	/**
	 * Checks that the legacy provers failed to prove the given sequent.
	 * 
	 * @param sequent
	 *            the sequent to prove
	 */
	private static void assertFailure(IProverSequent sequent, LegacyProvers.Input input) {
		IExtReasonerOutput extReaOut = legacyProvers.apply(sequent, input);
		assertTrue(extReaOut instanceof UnSuccessfulExtReasonerOutput);
	}

	/**
	 * Checks that the legacy provers succeeded in proving the given sequent.
	 * 
	 * @param sequent
	 *            the sequent to prove
	 */
	private static void assertSuccess(IProverSequent sequent, LegacyProvers.Input input) {
		Predicate newGoalPredicate = TestLib.chkProofFormat_getNewGoalPred(
				sequent, legacyProvers, input);
		assertTrue(newGoalPredicate.equals(Lib.True));
	}
	
	IProverSequent[] failures = {
			TestLib.genSeq("1=1 |- 2=1"),
			TestLib.genSeq("x∈ℤ|- x∈ℕ"),
	};

	IProverSequent[] success = {
			TestLib.genSeq("|- 0 = 0"),
			TestLib.genSeq("x∈ℕ|- x∈ℕ"),
			TestLib.genSeq("1=1 |- 1=1"),
			TestLib.genSeq("1=1 |- 2=2"),
			TestLib.genSeq("x∈ℕ|- x∈ℤ"),
			TestLib.genSeq("x∈ℤ;; x>0 |- x≠0 "),
			TestLib.genSeq("(∀n·n∈ℕ ⇒ n∈A) |- (∃n·n∈ℕ ∧ n∈A) "),
			TestLib.genSeq("A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; x∈A ;; x∈B |- x∈A∩B"),
			// The next sequent is provable by PP, but not by ML
			TestLib.genSeq("0 ≤ a ∧ 1 < b |- a mod b < b"),
			TestLib.genSeq("x∈ℕ;; x=z ;; y=z |- x=z"),
	};

	public void testLegacyProverApply() {	

		final LegacyProvers.Input input = new LegacyProvers.Input();
		for (IProverSequent sequent : success){
			assertSuccess(sequent, input);
		}
		for (IProverSequent sequent : failures){
			assertFailure(sequent, input);
		}
	}

	public void testTimeOut() {
		IProverSequent sequent = TestLib.genSeq("0 ≤ a ∧ 1 < b |- a mod b < b");

		// Very short timeout so that the provers are not launched
		LegacyProvers.Input input = new LegacyProvers.Input(1);
		assertFailure(sequent, input);

		// Invalid timeout so that the provers are not launched
		input = new LegacyProvers.Input(-1);
		assertFailure(sequent, input);

		// Reasonable timeout so that the provers can succeed
		input = new LegacyProvers.Input(30 * 1000);
		assertSuccess(sequent, input);
	}
	
}
