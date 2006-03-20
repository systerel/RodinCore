package org.eventb.core.prover.tests;

import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.externalReasoners.ExternalPP;
import org.eventb.core.prover.externalReasoners.LegacyProvers;
import org.eventb.core.prover.sequent.IProverSequent;

public class ExternalPPTests extends LegacyProversTest {
	
	private static IExternalReasoner externalPP = new ExternalPP();
	
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
		final ExternalPP.Input PPInput = new ExternalPP.Input();
		for (IProverSequent sequent : success){
			assertSuccess(sequent, externalPP, PPInput);
		}
		for (IProverSequent sequent : failures){
			assertFailure(sequent, externalPP, PPInput);
		}
	}

	public void testTimeOut() {
		IProverSequent sequent = TestLib.genSeq("0 ≤ a ∧ 1 < b |- a mod b < b");

		// Very short timeout so that the provers are not launched
		LegacyProvers.Input input = new ExternalPP.Input(1);
		assertFailure(sequent, externalPP, input);

		// Invalid timeout so that the provers are not launched
		input = new ExternalPP.Input(-1);
		assertFailure(sequent, externalPP, input);

		// Reasonable timeout so that the provers can succeed
		input = new ExternalPP.Input(30 * 1000);
		assertSuccess(sequent, externalPP, input);
	}
	
}
