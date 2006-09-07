package org.eventb.core.seqprover;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.HypothesesManagement.Action;


public interface IProofRule extends IReasonerOutput{

	
	Predicate getGoal();

	
	Set<Hypothesis> getNeededHyps();

	/**
	 * Returns the confidence of this proof rule as returned by the reasoner.
	 * 
	 * @return the confidence of this proof rule (see {@see IConfidence})
	 */
	int getConfidence();
	
	/**
	 * Returns the name of this proof rule this should be used for display.
	 * 
	 * @return the display name of this proof rule
	 */
	String getDisplayName();
	
	IAnticident[] getAnticidents();
	
	/**
	 * Applies this rule to the given proof sequent.
	 * 
	 * @param sequent
	 *            proof sequent to apply the rule to
	 * @return array of proof sequents produced by this rule.
	 */
	// public abstract IProverSequent[] apply(IProverSequent sequent);
	

	public interface IAnticident {
		Predicate getGoal();
		Set<Predicate> getAddedHyps();
		FreeIdentifier[] getAddedFreeIdents();
		List<Action> getHypAction();
	}	

}