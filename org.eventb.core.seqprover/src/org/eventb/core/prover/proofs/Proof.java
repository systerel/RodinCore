package org.eventb.core.prover.proofs;

import org.eventb.core.prover.sequent.ISequent;

public abstract class Proof {
	
	public abstract ISequent ofSequent();
	
//	public boolean discharges(SequentI PO){
//		if (PO == ofSequent()) return true;
//		if (! PO.goal().equals(this.ofSequent().goal())) return false;
//		if (! PO.typeEnvironment().containsAll(this.ofSequent().typeEnvironment())) return false;
//		if (! PO.hypotheses().containsAll(this.ofSequent().hypotheses())) return false;
//		return true;	
//	}
	
	@Override
	public  String toString(){
		return "Proof:"+this.getClass().getSimpleName()+this.ofSequent().toString();
	}
	
	
	

}
