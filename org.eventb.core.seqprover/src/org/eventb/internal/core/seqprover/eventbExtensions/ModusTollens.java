package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

// TODO : Rename to impEcontrapositive to be uniform with impE
public class ModusTollens extends HypothesisReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".impE";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {

		if (pred == null) {
			throw new IllegalArgumentException("Null hypothesis");
		}
		if (!Lib.isImp(pred)) {
			throw new IllegalArgumentException(
					"Hypothesis is not an implication: " + pred);
		}

		FormulaFactory ff = FormulaFactory.getDefault();
		final Predicate toShow = ff.makeUnaryPredicate(Predicate.NOT, Lib
				.impRight(pred), null);
		final Predicate toAssume = ff.makeUnaryPredicate(Predicate.NOT, Lib
				.impLeft(pred), null);
		final Set<Predicate> addedHyps = Lib.breakPossibleConjunct(toAssume);
		addedHyps.addAll(Lib.breakPossibleConjunct(toShow));
		return new IAntecedent[] {
				ProverFactory.makeAntecedent(
						toShow,null,
						ProverFactory.makeDeselectHypAction(Arrays.asList(pred))),
				ProverFactory.makeAntecedent(sequent.goal(), addedHyps,
						ProverFactory
								.makeDeselectHypAction(Arrays.asList(pred))) };
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "⇒ hyp mt (" + pred + ")";
	}

}
