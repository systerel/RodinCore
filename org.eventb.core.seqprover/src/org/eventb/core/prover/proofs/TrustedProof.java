package org.eventb.core.prover.proofs;

import org.eventb.core.prover.sequent.ISequent;

public class TrustedProof extends Proof{
	
	private ISequent sequent;
	
	public TrustedProof(ISequent S){
		this.sequent = S;
	}
	
	@Override
	public ISequent ofSequent(){
		return this.sequent;
	}

}
