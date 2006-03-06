package org.eventb.core.prover.rules;


public interface IProofRule {

	/**
	 * Returns the name of this proof rule.
	 * 
	 * @return the name of this proof rule
	 */
	String getName();

	/**
	 * Tells whether this rule is appliable to the given proof sequent.
	 * 
	 * @param sequent
	 *            proof sequent to test the rule on
	 * @return <code>true</code> iff this rule is appliable to the given
	 *         sequent.
	 */
	// Removed isApplicable() since there is no special reason to keep it.
	// boolean isApplicable(IProverSequent sequent);

}