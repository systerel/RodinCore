package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.proofBuilder.IProofSkeleton;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofTreeNode extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prProofTreeNode"; //$NON-NLS-1$
	
		// TODO : Make this and ProofTreeNode resemble each other.
		
		public IPRProofTreeNode[] getChildNodes() throws RodinDBException;
		
//		public IPRProofRule getPRRule() throws RodinDBException;
		
		public IProofRule getRule() throws RodinDBException;
		
		public void setComment(String comment) throws RodinDBException;
		
		public String getComment() throws RodinDBException;

		public IProofSkeleton getSkeleton(IProgressMonitor monitor) throws RodinDBException;
}
