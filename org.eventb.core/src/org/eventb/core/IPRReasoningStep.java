package org.eventb.core;

import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRReasoningStep extends IInternalElement {
		public String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".reasoningStep"; //$NON-NLS-1$
		
		public ReasonerOutputSucc getReasonerOutput() throws RodinDBException;
		
		public void setReasonerOutput(ReasonerOutputSucc reasonerOutput) throws RodinDBException;	
}

