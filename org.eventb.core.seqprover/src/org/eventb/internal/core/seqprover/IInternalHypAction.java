package org.eventb.internal.core.seqprover;

import org.eventb.core.seqprover.IHypAction;

public interface IInternalHypAction extends IHypAction {
	
	IInternalProverSequent perform(IInternalProverSequent seq);
	
	void processDependencies(ProofDependenciesBuilder proofDeps);

}
