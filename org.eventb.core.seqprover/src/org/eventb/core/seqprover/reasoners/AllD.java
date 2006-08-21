package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.ProofRule;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.eventb.core.seqprover.reasonerInputs.CombiInput;
import org.eventb.core.seqprover.reasonerInputs.MultipleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class AllD implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".allD";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		final MultipleExprInput multipleExprInput = new MultipleExprInput(reasonerInputSerializers[0]);
		final SinglePredInput singlePredInput = new SinglePredInput(reasonerInputSerializers[1]);
		final CombiInput combiInput = new CombiInput(
				multipleExprInput,
				singlePredInput);
		return combiInput;
	}
	
	public ReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
	
		// Organize Input
		CombiInput input = (CombiInput) reasonerInput;

		if (input.hasError())
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,reasonerInput);
			reasonerOutput.error = input.getError();
			return reasonerOutput;
		}
		
		Predicate univHypPred = ((SinglePredInput)input.getReasonerInputs()[1]).getPredicate();
		Hypothesis univHyp = new Hypothesis(univHypPred);
		
		if (! seq.hypotheses().contains(univHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+univHyp);
		if (! Lib.isUnivQuant(univHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not universally quantified:"+univHyp);
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHypPred);
		
		
		MultipleExprInput multipleExprInput = (MultipleExprInput) input.getReasonerInputs()[0];

		// compute instantiations from the input: 
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		// Not sure if reasoner should actually modify its input to reflect this.
		Expression[] instantiations = multipleExprInput.computeInstantiations(boundIdentDecls);
		
		if (instantiations == null)
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,reasonerInput);
			reasonerOutput.error = "Type error when trying to instantiate bound identifiers";
			return reasonerOutput;
		}
		assert instantiations.length == boundIdentDecls.length;
		
		
		// Generate the well definedness predicate for the instantiations
		Predicate WDpred = Lib.WD(instantiations);
		// Generate the instantiated predicate
		Predicate instantiatedPred = Lib.instantiateBoundIdents(univHypPred,instantiations);
		assert instantiatedPred != null;
		
		// Generate the successful reasoner output
		ProofRule reasonerOutput = new ProofRule(this,input);
		reasonerOutput.display = "∀ hyp (inst "+displayInstantiations(instantiations)+")";
		reasonerOutput.neededHypotheses.add(univHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[2];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
		reasonerOutput.anticidents[0].subGoal = WDpred;
		
		// The instantiated goal
		reasonerOutput.anticidents[1] = new ProofRule.Anticident();
		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(instantiatedPred);
		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(univHyp));
		reasonerOutput.anticidents[1].subGoal = seq.goal();
				
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
