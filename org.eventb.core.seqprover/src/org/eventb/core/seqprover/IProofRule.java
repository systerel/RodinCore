package org.eventb.core.seqprover;

import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;


public interface IProofRule extends IReasonerOutput{

	/**
	 * Returns the name of this proof rule this should be used for display.
	 * 
	 * @return the display name of this proof rule
	 */
	String getDisplayName();
	
	
	/**
	 * Returns the confidence of this proof rule.
	 * 
	 * @return the confidence of this proof rule (see {@see IConfidence})
	 */
	int getConfidence();
	
	/**
	 * Applies this rule to the given proof sequent.
	 * 
	 * @param sequent
	 *            proof sequent to apply the rule to
	 * @return array of proof sequents produced by this rule.
	 */
	public abstract IProverSequent[] apply(IProverSequent sequent);

	Set<Hypothesis> getNeededHypotheses();

	void addFreeIdents(ITypeEnvironment typEnv);

}