/**
 * 
 */
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner;

/**
 * A reasoner that generates a forward inference to split a conjunctive hypothesis into its conjuncts and hides the
 * original conjunction 
 * 
 * @author Farhad Mehta
 *
 */
public final class ConjF extends ForwardInfReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".conjF";
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner#getDisplay(org.eventb.core.ast.Predicate)
	 */
	@Override
	protected String getDisplay(Predicate pred) {
		return "∧ hyp (" + pred + ")";
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner#getForwardInf(org.eventb.core.seqprover.IProverSequent, org.eventb.core.ast.Predicate)
	 */
	@ProverRule("AND_L")
	@Override
	protected IForwardInfHypAction getForwardInf(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {
		if (! Lib.isConj(pred)) {
			throw new IllegalArgumentException("Predicate "+ pred +" is not a conjunction");
		}
		
		return ProverFactory.makeForwardInfHypAction(Collections.singleton(pred), Lib.breakPossibleConjunct(pred));
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasoner#getReasonerID()
	 */
	public String getReasonerID() {
		return REASONER_ID;
	}

}
