/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
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
