package org.eventb.core;

import org.eventb.core.seqprover.IProofRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofRule extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".proofRule"; //$NON-NLS-1$
		
		//@Deprecated
		// public String getRuleID() throws RodinDBException;
		
		public IProofRule getProofRule() throws RodinDBException;
		
		public void setProofRule(IProofRule rule) throws RodinDBException;	
}
