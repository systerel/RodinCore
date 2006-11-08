package org.eventb.core;

import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;


/**
 * @author Farhad Mehta
 *
 */

public interface IPRReasonerInput extends IInternalElement, IReasonerInputSerializer {
		
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".prReasonerInput"); //$NON-NLS-1$

}
