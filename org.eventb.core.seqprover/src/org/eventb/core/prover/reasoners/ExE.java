package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class ExE implements Reasoner{
	
	public String getReasonerID() {
		return "exE";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		SinglePredInput input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new SinglePredInput((SerializableReasonerInput)reasonerInput);
		} 
		else input = (SinglePredInput) reasonerInput;

		Predicate exHypPred = input.getPredicate();
		Hypothesis exHyp = new Hypothesis(exHypPred);
		
		
		if (! seq.hypotheses().contains(exHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+exHyp);
		if (! Lib.isExQuant(exHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not existentially quantified:"+exHyp);
		
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		
		QuantifiedPredicate ExQ = (QuantifiedPredicate)exHypPred;
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(ExQ);
		
		// The type environment is cloned since makeFresh.. adds directly to the
		// given type environment
		// TODO : Change implementation
		ITypeEnvironment newITypeEnvironment = seq.typeEnvironment().clone();
		FreeIdentifier[] freeIdents = (Lib.ff).makeFreshIdentifiers(boundIdentDecls,newITypeEnvironment);
		
//		for (FreeIdentifier identifier : freeIdents) {
//			reasonerOutput.anticidents[0].addedFreeIdentifiers.addName(identifier.getName(),identifier.getType());
//		}
		
		assert boundIdentDecls.length == freeIdents.length;
		Predicate instantiatedEx = ExQ.instantiate(freeIdents,Lib.ff);
		assert instantiatedEx.isTypeChecked();
		reasonerOutput.display = "∃ hyp (frees "+displayFreeIdents(freeIdents)+")";
		reasonerOutput.anticidents[0].addedHypotheses.add(instantiatedEx);
		reasonerOutput.anticidents[0].addedFreeIdentifiers = freeIdents;
		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(exHyp));
		reasonerOutput.anticidents[0].subGoal = seq.goal();
				
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

//	public static class Input implements ReasonerInput{
//		
//		Hypothesis exHyp;
//		
//		public Input(Hypothesis exHyp){
//			this.exHyp = exHyp;
//		}
//		
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.exHyp = new Hypothesis(serializableReasonerInput.getPredicate("exHyp"));
//		}
//		
//		public SerializableReasonerInput genSerializable(){
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			serializableReasonerInput.putPredicate("exHyp",exHyp.getPredicate());
//			return serializableReasonerInput;
//		}
//		
//	}

}
