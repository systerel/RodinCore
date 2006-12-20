package org.eventb.internal.core.seqprover;

import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProverSequent;

public interface IInternalHypAction extends IHypAction {
	
	IProverSequent perform(IProverSequent seq);

}
