package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
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

public class AllD implements Reasoner{
	
	public String getReasonerID() {
		return "allD";
	}
	
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput reasonerInput){
	
		// Organize Input
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;

		Hypothesis univHyp = input.univHyp;
		Predicate univHypPred = input.univHyp.getPredicate();
		
		
		if (! seq.hypotheses().contains(univHyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+univHyp);
		if (! Lib.isUnivQuant(univHypPred))
			return new ReasonerOutputFail(this,input,
					"Hypothesis is not universally quantified:"+univHyp);
		
		
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(univHypPred);
		
		
		if (input.instantiationsExpr == null)
		{
			// Parse and typecheck input
			input.instantiationsExpr = new Expression[boundIdentDecls.length];
			Expression instantiation;
			for (int i = 0; i < boundIdentDecls.length; i++) {
				if ( i< input.instantiations.length &&
						input.instantiations[i] != null && 
						input.instantiations[i].trim().length() != 0)
				{
					instantiation = Lib.parseExpression(input.instantiations[i]);
					if (instantiation == null) 
						return new ReasonerOutputFail(this,input,
								"Parse error for expression "+input.instantiations[i]);
					if (! Lib.isWellTypedInstantiation(instantiation,boundIdentDecls[i].getType(),seq.typeEnvironment())) 
						return new ReasonerOutputFail(this,input,
								"Type check failed : "+input.instantiations[i]+" expected type "+ boundIdentDecls[i].getType());
					input.instantiationsExpr[i] = instantiation;
				}
				else
					input.instantiationsExpr[i] = null;
			}
		}
		else
		{
			// copy and check that old input is still compatable
			Expression[] instantiationsExprCopy = new Expression[boundIdentDecls.length];
			for (int i = 0; i < instantiationsExprCopy.length; i++) {
				if (i< input.instantiationsExpr.length)
				{
					if (! input.instantiationsExpr[i].getType().
							equals(boundIdentDecls[i].getType()))
						return new ReasonerOutputFail(this,input,
								"Type check failed : "+input.instantiations[i]+" expected type "+ boundIdentDecls[i].getType());
					instantiationsExprCopy[i] = input.instantiationsExpr[i];
				}
				else
				{
					instantiationsExprCopy[i]=null;
				}	
			}
			input.instantiationsExpr = instantiationsExprCopy;
		}
		
		// Generate the well definedness predicate for the instantiations
		Predicate WDpred = Lib.WD(input.instantiationsExpr);
		// Generate the instantiated predicate
		Predicate instantiatedPred = Lib.instantiateBoundIdents(univHypPred,input.instantiationsExpr);
		assert instantiatedPred != null;
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "∀ inst ("+displayInstantiations(input.instantiationsExpr)+")";
		reasonerOutput.neededHypotheses.add(univHyp);
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[2];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = WDpred;
		
		// The instantiated goal
		reasonerOutput.anticidents[1] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[1].addedHypotheses.add(instantiatedPred);
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
	
	public static class Input implements ReasonerInput{
		public final String[] instantiations;
		public Expression[] instantiationsExpr;
		public Hypothesis univHyp;
		
		public Input(String[] witnesses,Hypothesis univHyp){
			this.instantiations = witnesses;
			this.instantiationsExpr = null;
			this.univHyp = univHyp;
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.instantiations = null;
			int length = Integer.parseInt(serializableReasonerInput.getString("length"));
			this.instantiationsExpr = new Expression[length];
			for (int i = 0; i < length; i++) {
				// null value taken care of in getExpression.
				this.instantiationsExpr[i] = serializableReasonerInput.getExpression(String.valueOf(i));
			}
			this.univHyp = new Hypothesis(serializableReasonerInput.getPredicate("univHyp"));
		}
		
		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			assert instantiationsExpr != null;
			serializableReasonerInput.putString("length",String.valueOf(instantiationsExpr.length));
			for (int i = 0; i < instantiationsExpr.length; i++) {
				if (instantiationsExpr[i]!=null)
				serializableReasonerInput.putExpression(String.valueOf(i),instantiationsExpr[i]);
			}
			serializableReasonerInput.putPredicate("univHyp",univHyp.getPredicate());
			return serializableReasonerInput;
		}
		
	}

}
