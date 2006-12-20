package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Arrays;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

public class ImpE extends HypothesisReasoner {
	
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

		final Predicate toShow = Lib.impLeft(pred);
		final Predicate toAssume = Lib.impRight(pred);
		final Set<Predicate> addedHyps = Lib.breakPossibleConjunct(toAssume);
		addedHyps.addAll(Lib.breakPossibleConjunct(toShow));
		return new IAntecedent[] {
				ProverFactory.makeAntecedent(toShow),
				ProverFactory.makeAntecedent(
						sequent.goal(),
						addedHyps,
						ProverFactory.makeDeselectHypAction(Arrays.asList(pred)))
		};
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "⇒ hyp (" + pred + ")";
	}

}
