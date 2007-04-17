package org.eventb.core.seqprover.tacticExtentionTests;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

public class IdentityTactic implements ITactic {
	
	public static final String TACTIC_ID = "org.eventb.core.seqprover.tests.identityTac";

	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		// Do nothing
		return null;
	}

}
