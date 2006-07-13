package org.eventb.core.prover;


public interface IProofRule {

	/**
	 * Returns the name of this proof rule this should be used for display.
	 * 
	 * @return the display name of this proof rule
	 */
	String getDisplayName();

	/**
	 * Returns the rule ID of this proof rule.
	 * 
	 * @return the rule ID of this proof rule
	 */
	String getRuleID();
	
	
	/**
	 * Returns the confidence of this proof rule.
	 * 
	 * @return the confidence of this proof rule (see {@see IConfidence})
	 */
	int getRuleConfidence();
	

}