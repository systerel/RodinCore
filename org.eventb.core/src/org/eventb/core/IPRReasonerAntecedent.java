package org.eventb.core;

import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRReasonerAntecedent extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prReasonerAntecedent"); //$NON-NLS-1$
		
	IAntecedent getAnticident() throws RodinDBException;
	void setAnticident(IAntecedent antecedent) throws RodinDBException;
}
