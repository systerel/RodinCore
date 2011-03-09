package org.eventb.core.seqprover.xprover.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;

/**
 * Base class for tests of the XProver extension to the sequent prover.
 * <p>
 * Provides various utility methods for writing tests.
 * </p>
 * 
 * @author lvoisin
 */
public abstract class XProverTests {

	static final FormulaFactory ff = FormulaFactory.getDefault();

	static final Type ty_S = ff.makeGivenType("S");

	static final Expression id_A = freeIdent("A", POW(ty_S));

	static final Expression id_x = freeIdent("x", ty_S);

	static final Expression id_y = freeIdent("y", ty_S);

	static final Expression id_z = freeIdent("z", ty_S);

	static final Predicate px = in(id_x, id_A);

	static final Predicate py = in(id_y, id_A);

	static final Predicate pz = in(id_z, id_A);

	static Type POW(Type t) {
		return ff.makePowerSetType(t);
	}

	static Expression freeIdent(String name, Type t) {
		return ff.makeFreeIdentifier(name, null, t);
	}

	static Predicate in(Expression left, Expression right) {
		return ff.makeRelationalPredicate(Predicate.IN, left, right, null);
	}

	static List<Predicate> mList(Predicate... preds) {
		return Arrays.asList(preds);
	}

	static IProverSequent mSequent(List<Predicate> hyps, List<Predicate> sels,
			Predicate goal) {

		// Compute the type environment of the sequent
		ITypeEnvironment typenv = ff.makeTypeEnvironment();
		for (Predicate hyp : hyps) {
			typenv.addAll(hyp.getFreeIdentifiers());
		}
		typenv.addAll(goal.getFreeIdentifiers());

		return ProverFactory.makeSequent(typenv, hyps, sels, goal);
	}

	static void assertSameSet(String msg, List<Predicate> exp,
			Set<Predicate> act) {

		if (exp.size() != act.size() || !act.containsAll(exp)) {
			fail(msg + ": expected " + exp + ", but got " + act);
		}
	}

	static void assertSuccess(IReasonerOutput output,
			List<Predicate> neededHyps, Predicate neededGoal) {

		if (output instanceof IProofRule) {
			IProofRule rule = (IProofRule) output;
			assertEquals("Generated rule should have no antecedent", 0, rule
					.getAntecedents().length);
			assertEquals("Unexpected needed goal", neededGoal, rule.getGoal());
			assertSameSet("Unexpected needed hypotheses", neededHyps, rule
					.getNeededHyps());
		} else if (output instanceof IReasonerFailure) {
			IReasonerFailure failure = (IReasonerFailure) output;
			fail("Reasoner should have succeeded, but it failed with reason "
					+ failure.getReason());
		} else {
			fail("Reasoner should have succeeded, "
					+ "but returned an unknown kind of output.");
		}
	}

	static void assertFailure(IReasonerOutput output, String expReason) {
		assertTrue("Reasoner should have failed",
				output instanceof IReasonerFailure);
		IReasonerFailure failure = (IReasonerFailure) output;
		if (expReason != null) {
			assertEquals("Unexpected failure message", expReason, failure
					.getReason());
		}
	}

}
