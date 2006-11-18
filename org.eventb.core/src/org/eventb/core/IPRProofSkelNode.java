package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofSkelNode extends IInternalElement, ICommentedElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProofSkelNode"); //$NON-NLS-1$
	
	void setSkeleton(IProofSkeleton skel, IProofStoreCollector store, IProgressMonitor monitor) throws RodinDBException;

	IProofSkeleton getSkeleton(IProofStoreReader store)  throws RodinDBException;
}
