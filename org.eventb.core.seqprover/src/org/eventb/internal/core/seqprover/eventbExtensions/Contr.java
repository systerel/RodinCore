package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Generates a proof rule to contradict the goal and falsify a given hypothesis simultaniously.
 * 
 * <p>
 * In case no hypothesis is given, the reasoner assumes the given hypothesis is 'true'.
 * </p>
 * 
 * @author Farhad Mehta
 *
 */
public class Contr extends HypothesisReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".contr";
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule( { "CONTRADICT_L", "CONTRADICT_R" })
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) {

		final Predicate newGoal;
		IHypAction deselect = null;
		if (pred == null) {
			newGoal = Lib.False;
		} else {
			newGoal = Lib.makeNeg(pred);
			deselect = ProverFactory.makeDeselectHypAction(Collections.singleton(pred));
		}
		return new IAntecedent[] {
				ProverFactory.makeAntecedent(
						newGoal,
						Lib.breakPossibleConjunct(Lib.makeNeg(sequent.goal())),
						deselect)
		};
	}

	@Override
	protected String getDisplay(Predicate pred) {
		if (pred == null) {
			return "ct goal";
		}
		return "ct hyp (" + pred + ")";
	}

}
