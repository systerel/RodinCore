package org.eventb.internal.core.pom;

import org.eventb.core.prover.IProofTree;
import org.eventb.core.prover.tactics.ITactic;
import org.eventb.core.prover.tactics.Tactics;

public class AutoProver {
	
	private ITactic tactic;

	public AutoProver(){
		this.tactic = Tactics.norm();
	}
	
	public void run(IProofTree pt){
		tactic.apply(pt.getRoot());
	}

}
