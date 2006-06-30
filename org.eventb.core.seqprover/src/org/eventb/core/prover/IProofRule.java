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
	
	final int CONFIDENCE_PENDING = 0;
	final int CONFIDENCE_REVIEWED = 500;
	final int CONFIDENCE_DISCHARGED = 1000;
	
	int getRuleConfidence();
	

}