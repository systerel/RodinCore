package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofRule extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProofRule"); //$NON-NLS-1$

	IProofRule getProofRule(IProofStoreReader store) throws RodinDBException;

	void setProofRule(IProofRule rule, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;	
}
