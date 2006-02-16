package org.eventb.core.prover;


import org.eventb.core.prover.sequent.IProverSequent;

public interface IExternalReasoner {

	String name();
	// boolean isApplicable(ProverSequent S,PluginInput I);
	IExtReasonerOutput apply(IProverSequent S,IExtReasonerInput I);
	// PluginInput defaultInput();
	
}
