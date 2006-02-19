package org.eventb.core.prover.rules;

import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;

public class MngHyp extends ProofRule {

	private Action action;
	
	public MngHyp(Action action){
		super("["+action.toString()+"]");
		this.action = action;
	}
	
	public boolean isApplicable(IProverSequent S) {
	// TODO Maybe replace with a check to see if this rule actually does something.
		return true;
	}

	public IProverSequent[] apply(IProverSequent S) {
		// if (! isApplicable(S)) return null;
		IProverSequent[] anticident = new IProverSequent[1];
		anticident[0] = this.action.perform(S);
		return anticident;
	}
}
