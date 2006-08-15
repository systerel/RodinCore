package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class AllD implements Reasoner{
	
	public String getReasonerID() {
		return "allD";
	}
	
	public ReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		final MultipleExprInput multipleExprInput = new MultipleExprInput(reasonerInputSerializers[0]);
		final SinglePredInput singlePredInput = new SinglePredInput(reasonerInputSerializers[1]);
		final CombiInput combiInput = new CombiInput(
				multipleExprInput,
				singlePredInput);
		return combiInput;
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput reasonerInput, IProgressMonitor progressMonitor){
	
		// Organize Input
		CombiInput input;
//		if (reasonerInput instanceof SerializableReasonerInput){
//			input = new CombiInput((SerializableReasonerInput)reasonerInput);
//			input.getReasonerInputs()[0] = new MultipleExprInput((SerializableReasonerInput)input.getReasonerInputs()[0]);
//			input.getReasonerInputs()[1] = new SinglePredInput((SerializableReasonerInput)input.getReasonerInputs()[1]);
//		} 
//		else 
			
			input = (CombiInput) reasonerInput;

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
		Expression[] expressions = ((MultipleExprInput)input.getReasonerInputs()[0]).getExpressions();
		
		
		// copy and check that old input is still compatable
		// it can be that the number of bound variables have increased 
	    // or decreased, or their types have changed.
		Expression[] instantiations = new Expression[boundIdentDecls.length];
		for (int i = 0; i < instantiations.length; i++) {
			if (i< expressions.length)
			{
				if (! expressions[i].getType().
						equals(boundIdentDecls[i].getType()))
					return new ReasonerOutputFail(this,input,
							"Type check failed : "+expressions[i]+" expected type "+ boundIdentDecls[i].getType());
				instantiations[i] = expressions[i];
			}
			else
			{
				instantiations[i]=null;
			}	
		}
		
		// Generate the well definedness predicate for the instantiations
		Predicate WDpred = Lib.WD(instantiations);
		// Generate the instantiated predicate
		Predicate instantiatedPred = Lib.instantiateBoundIdents(univHypPred,instantiations);
		assert instantiatedPred != null;
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "∀ hyp (inst "+displayInstantiations(instantiations)+")";
		reasonerOutput.neededHypotheses.add(univHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[2];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = WDpred;
		
		// The instantiated goal
		reasonerOutput.anticidents[1] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[1].addConjunctsToAddedHyps(instantiatedPred);
		reasonerOutput.anticidents[1].hypAction.add(Lib.deselect(univHyp));
		reasonerOutput.anticidents[1].subGoal = seq.goal();
				
		return reasonerOutput;
	}
	
	private String displayInstantiations(Expression[] witnesses){
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < witnesses.length; i++) {
			if (witnesses[i] == null)
				str.append("_");
			else
				str.append(witnesses[i].toString());
			if (i != witnesses.length-1) str.append(",");
		}
		return str.toString();
	}

}
