package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
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
		
	IAntecedent getAntecedent(IProofStoreReader store, IProgressMonitor monitor) throws RodinDBException;
	void setAntecedent(IAntecedent antecedent, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;
}
