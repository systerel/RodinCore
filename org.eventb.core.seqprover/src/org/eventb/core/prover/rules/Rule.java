package org.eventb.core.prover.rules;

import org.eventb.core.prover.sequent.IProverSequent;

public interface Rule {
	
	public String name();
	public boolean isApplicable(IProverSequent S);
	public IProverSequent[] apply(IProverSequent S);

}