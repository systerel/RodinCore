package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofTreeNode extends IInternalElement {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prProofTreeNode"); //$NON-NLS-1$
	
	// TODO : Make this and ProofTreeNode resemble each other.

	IPRProofTreeNode[] getChildNodes() throws RodinDBException;

//	public IPRProofRule getPRRule() throws RodinDBException;

	IProofRule getRule() throws RodinDBException;

	void setComment(String comment) throws RodinDBException;

	String getComment() throws RodinDBException;

	IProofSkeleton getSkeleton(IProgressMonitor monitor) throws RodinDBException;

	void setProofTreeNode(IProofTreeNode proofTreeNode) throws RodinDBException;
}
