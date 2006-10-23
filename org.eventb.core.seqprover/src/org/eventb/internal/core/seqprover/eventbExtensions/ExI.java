package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;

public class ExI implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".exI";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		return new MultipleExprInput(reasonerInputSerializer);
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProofMonitor pm){
	
		if (! Lib.isExQuant(seq.goal()))
			return ProverFactory.reasonerFailure(
					this,reasonerInput,
					"Goal is not existentially quantified"); 
		
		// Organize Input
		MultipleExprInput input = (MultipleExprInput) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(seq.goal());
		
		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		Expression[] instantiations = input.computeInstantiations(boundIdentDecls);
		if (instantiations == null)
			return ProverFactory.reasonerFailure(
				this,reasonerInput,
				"Type error when trying to instantiate bound identifiers");
		
		assert instantiations.length == boundIdentDecls.length;	
		
		
		// Generate the well definedness predicate for the witnesses
		Predicate WDpred = Lib.WD(instantiations);
		// Generate the instantiated predicate
		Predicate instantiatedPred = Lib.instantiateBoundIdents(seq.goal(),instantiations);
		assert instantiatedPred != null;
		
		// Generate the anticidents
		IAnticident[] anticidents = new IAnticident[2];
		
		// Well definedness condition
		anticidents[0] = ProverFactory.makeAnticident(WDpred);
		
		// The instantiated goal
		anticidents[1] = ProverFactory.makeAnticident(instantiatedPred);

		// Generate the successful reasoner output
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				"∃ goal (inst "+displayInstantiations(instantiations)+")",
				anticidents);
		
//		// Generate the successful reasoner output
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.display = "∃ goal (inst "+displayInstantiations(instantiations)+")";
//		reasonerOutput.goal = seq.goal();
//
//		// Generate the anticidents
//		reasonerOutput.anticidents = new Anticident[2];
//		
//		// Well definedness condition
//		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
//		reasonerOutput.anticidents[0].goal = WDpred;
//		
//		// The instantiated goal
//		reasonerOutput.anticidents[1] = new ProofRule.Anticident();
//		reasonerOutput.anticidents[1].goal = instantiatedPred;
				
		return reasonerOutput;
	}
	
	private String displayInstantiations(Expression[] instantiations){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < instantiations.length; i++) {
			if (instantiations[i] == null)
				str.append("_");
			else
				str.append(instantiations[i].toString());
			if (i != instantiations.length-1) str.append(",");
		}
		return str.toString();
	}

}
