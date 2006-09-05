package org.eventb.core;

import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRReasonerAnticident extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prReasonerAnticident"; //$NON-NLS-1$
		
		public Anticident getAnticident() throws RodinDBException;
		public void setAnticident(IAnticident anticident) throws RodinDBException;
}
