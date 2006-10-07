package org.eventb.core.seqprover.reasoners;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class AllI extends EmptyInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".allI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput input, IProofMonitor pm){
		
		if (! Lib.isUnivQuant(seq.goal()))
			return ProverFactory.reasonerFailure(
					this,input,"Goal is not universally quantified");
		
		
		
		
		QuantifiedPredicate UnivQ = (QuantifiedPredicate)seq.goal();
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(UnivQ);
		
		// The type environment is cloned since makeFresh.. adds directly to the
		// given type environment
		// TODO : Change implementation
		ITypeEnvironment newITypeEnvironment = seq.typeEnvironment().clone();
		FreeIdentifier[] freeIdents = (Lib.ff).makeFreshIdentifiers(boundIdentDecls,newITypeEnvironment);		
		assert boundIdentDecls.length == freeIdents.length;
		
		IAnticident[] anticidents = new IAnticident[1];
		anticidents[0] = ProverFactory.makeAnticident(
				UnivQ.instantiate(freeIdents,Lib.ff),
				null,
				freeIdents,
				null);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∀ goal (frees "+displayFreeIdents(freeIdents)+")",
				anticidents
				);
			
//			
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "∀ goal (frees "+displayFreeIdents(freeIdents)+")";
//		
//		reasonerOutput.anticidents = new Anticident[1];
//		reasonerOutput.anticidents[0] = new ProofRule.Anticident(UnivQ.instantiate(freeIdents,Lib.ff));
//		reasonerOutput.anticidents[0].addedFreeIdentifiers = freeIdents;
//		assert reasonerOutput.anticidents[0].goal.isTypeChecked();
				
		return reasonerOutput;
	}
	
	private String displayFreeIdents(FreeIdentifier[] freeIdents) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < freeIdents.length; i++) {
				str.append(freeIdents[i].toString());
			if (i != freeIdents.length-1) str.append(",");
		}
		return str.toString();
	}

}
