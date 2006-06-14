package org.eventb.core.prover.rules;


public interface IProofRule {

	/**
	 * Returns the name of this proof rule.
	 * 
	 * @return the name of this proof rule
	 */
	// TODO : rename to getDisplayName
	String getName();

	String getRuleID();

}