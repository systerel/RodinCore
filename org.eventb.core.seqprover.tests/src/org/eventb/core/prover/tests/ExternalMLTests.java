package org.eventb.core.prover.tests;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.externalReasoners.ExternalML;
import org.eventb.core.prover.externalReasoners.LegacyProvers;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class ExternalMLTests extends LegacyProversTest {
	
	private static IExternalReasoner externalML = new ExternalML();
	
	IProverSequent[] failures = {
			TestLib.genSeq("1=1 |- 2=1"),
			TestLib.genSeq("x∈ℤ|- x∈ℕ"),
			// The next sequents are provable by PP, but not by ML
			TestLib.genSeq("x∈ℤ;; x>0 |- x≠0 "),
			TestLib.genSeq("0 ≤ a ∧ 1 < b |- a mod b < b"),
	};

	IProverSequent[] success = {
			TestLib.genSeq("|- 0 = 0"),
			TestLib.genSeq("x∈ℕ|- x∈ℕ"),
			TestLib.genSeq("1=1 |- 1=1"),
			TestLib.genSeq("1=1 |- 2=2"),
			TestLib.genSeq("x∈ℕ|- x∈ℤ"),
			TestLib.genSeq("(∀n·n∈ℕ ⇒ n∈A) |- (∃n·n∈ℕ ∧ n∈A) "),
			TestLib.genSeq("A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; x∈A ;; x∈B |- x∈A∩B"),
			TestLib.genSeq("x∈ℕ;; x=z ;; y=z |- x=y"),
	};

	public void testLegacyProverApply() {	
		final ExternalML.Input MLInput = new ExternalML.Input();
		for (IProverSequent sequent : success){
			assertSuccess(sequent, externalML, MLInput);
		}
		for (IProverSequent sequent : failures){
			assertFailure(sequent, externalML, MLInput);
		}
	}

	public void testTimeOut() {
		IProverSequent sequent = makeLongSequent();

		// Very short timeout so that the provers are not launched
		LegacyProvers.Input input = new ExternalML.Input(1L);
		assertFailure(sequent, externalML, input);

		// Invalid timeout so that the provers are not launched
		input = new ExternalML.Input(-1L);
		assertFailure(sequent, externalML, input);

		// Reasonable timeout so that the provers can succeed
		input = new ExternalML.Input(30 * 1000L);
		assertSuccess(sequent, externalML, input);
	}
	
	private IProverSequent makeLongSequent() {
		final FormulaFactory ff = FormulaFactory.getDefault();
		final ITypeEnvironment tenv = ff.makeTypeEnvironment();
		final Type INT = ff.makeIntegerType();
		for (int i = 0; i < 200; ++i) {
			String name = "x" + i;
			tenv.addName(name, INT);
		}
		final Set<Hypothesis> globalHyps = new HashSet<Hypothesis>();
		final Predicate goal = ff.makeRelationalPredicate(Formula.IN,
				ff.makeFreeIdentifier("x0", null, INT),
				ff.makeAtomicExpression(Formula.INTEGER, null),
				null);
		return new SimpleProverSequent(tenv, globalHyps, goal);
	}
	
}
