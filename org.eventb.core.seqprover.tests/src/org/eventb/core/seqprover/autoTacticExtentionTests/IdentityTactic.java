package org.eventb.core.seqprover.autoTacticExtentionTests;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * A tactic extension to test with.
 * 
 * 
 * @author fmehta
 *
 */
public class IdentityTactic implements ITactic {
	
	public static final String TACTIC_ID = "org.eventb.core.seqprover.tests.identityTac";

	// This is the same as the name declared in the extension point
	public static final String TACTIC_NAME = "Identity Tactic";
	
	// This is the same as the description declared in the extension point
	public static final String TACTIC_DESC = "This tactic does nothing but succeeds";
	
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		// Do nothing
		return null;
	}
	
	
	/**
	 * A tactic extension (as an internal class) to test with. 
	 * 
	 * @author fmehta
	 *
	 */
	public static class FailTactic implements ITactic{
		
		public static final String TACTIC_ID = "org.eventb.core.seqprover.tests.failTac";

		// This is the same as the name declared in the extension point
		public static final String TACTIC_NAME = "Fail Tactic";

		public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
			// Do nothing
			return "Failure";
		}
		
		
	}

}
