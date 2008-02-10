package org.eventb.core.tests.pom;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.core.seqprover.tactics.BasicTactics;

public class TracingReasoner extends EmptyInputReasoner {

	public static final String REASONER_ID = "org.eventb.core.tests.tracingReasoner";

	public static final String TACTIC_ID = "org.eventb.core.tests.tracingTactic";

	public static class TracingTactic implements ITactic {

		private static final ITactic tacticInstance = BasicTactics.reasonerTac(
				new TracingReasoner(), new EmptyInput());

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			return tacticInstance.apply(ptNode, pm);
		}
	}

	private static List<Predicate> traces;

	public static void startTracing() {
		traces = new ArrayList<Predicate>();
	}

	public static void stopTracing() {
		traces = null;
	}

	public static Predicate[] getTraces() {
		if (traces == null) {
			return null;
		}
		Predicate[] result = new Predicate[traces.size()];
		return traces.toArray(result);
	}

	private static void trace(Predicate pred) {
		if (traces != null) {
			traces.add(pred);
		}
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		final Predicate goal = seq.goal();
		trace(goal);
		if (check(goal)) {
			return ProverFactory.makeProofRule(this, input, seq.goal(), seq
					.goal().toString(), new IAntecedent[0]);
		}
		return ProverFactory.reasonerFailure(this, input,
				"Can't discharge this goal");

	}

	/**
	 * Test if the given predicate is of the form:
	 * 
	 * <pre>
	 *    X ∈ ℕ
	 * </pre>
	 * 
	 * where <code>X</code> is an integer literal.
	 * 
	 * @param pred
	 *            the predicate to test
	 * @return <code>true</code> iff the test is successful
	 * 
	 */
	private boolean check(Predicate pred) {
		if (pred.getTag() == Predicate.IN) {
			final RelationalPredicate rp = (RelationalPredicate) pred;
			return rp.getLeft().getTag() == Predicate.INTLIT
					&& rp.getRight().getTag() == Predicate.NATURAL;
		}
		return false;
	}

}
