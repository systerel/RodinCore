package org.eventb.core;

import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRReasonerAnticident extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prReasonerAnticident"); //$NON-NLS-1$
		
	IAnticident getAnticident() throws RodinDBException;
	void setAnticident(IAnticident anticident) throws RodinDBException;
}
