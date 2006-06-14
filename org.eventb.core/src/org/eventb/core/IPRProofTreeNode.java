package org.eventb.core;

import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRProofTreeNode extends IUnnamedInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".proofTreeNode"; //$NON-NLS-1$
	
		public IPRProofTreeNode[] getChildProofTreeNodes() throws RodinDBException;
		
		public IPRProofRule getRule() throws RodinDBException;
}
