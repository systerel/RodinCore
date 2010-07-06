/**
 * 
 */
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner;

/**
 * Reasoner that returns a forward inference to free existentially bound variables in a hypothesisn existential variable
 * 
 * @author Farhad Mehta
 *
 */
public class ExF extends ForwardInfReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".exF";
	
	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner#getDisplay(org.eventb.core.ast.Predicate)
	 */
	@Override
	protected String getDisplay(Predicate pred) {
		return "∃ hyp ("+pred+")";
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner#getForwardInf(org.eventb.core.seqprover.IProverSequent, org.eventb.core.ast.Predicate)
	 */
	@ProverRule("XST_L") 
	@Override
	protected IForwardInfHypAction getForwardInf(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {
		
		if (! Lib.isExQuant(pred)) {
			throw new IllegalArgumentException("Predicate "+ pred +" is not existentially quantified.");
		}
		
		final QuantifiedPredicate exQ = (QuantifiedPredicate) pred;
		final BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(exQ);
		
		// The type environment is cloned since makeFresh.. adds directly to the
		// given type environment
		// TODO : Change implementation
		final ITypeEnvironment newTypenv = sequent.typeEnvironment().clone();
		final FreeIdentifier[] freeIdents = 
			Lib.ff.makeFreshIdentifiers(boundIdentDecls, newTypenv);
		
		Predicate instantiatedEx = exQ.instantiate(freeIdents, Lib.ff);
		
		return ProverFactory.makeForwardInfHypAction(
				Collections.singleton(pred), freeIdents,
				Lib.breakPossibleConjunct(instantiatedEx));
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.core.seqprover.IReasoner#getReasonerID()
	 */
	public String getReasonerID() {
		return REASONER_ID;
	}

}
