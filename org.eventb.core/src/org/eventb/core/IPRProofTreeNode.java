package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofTreeNode extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".prProofTreeNode"; //$NON-NLS-1$
	
		// TODO : Make this and ProofTreeNode resemble each other.
		
		public IPRProofTreeNode[] getChildProofTreeNodes() throws RodinDBException;
		
		public IPRProofRule getRule() throws RodinDBException;
		
		public void setComment(String comment) throws RodinDBException;
		
		public String getComment() throws RodinDBException;
}
