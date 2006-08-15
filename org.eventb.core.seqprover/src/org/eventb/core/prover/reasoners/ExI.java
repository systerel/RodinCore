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
import org.eventb.core.prover.sequent.IProverSequent;

public class ExI implements Reasoner{
	
	public String getReasonerID() {
		return "exI";
	}
	
	public ReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		return new MultipleExprInput(reasonerInputSerializer);
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput reasonerInput, IProgressMonitor progressMonitor){
	
		if (! Lib.isExQuant(seq.goal()))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,reasonerInput);
			reasonerOutput.error = "Goal is not existentially quantified";
			return reasonerOutput;
		}
		
		// Organize Input
		MultipleExprInput input = (MultipleExprInput) reasonerInput;
		
		if (input.hasError())
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,reasonerInput);
			reasonerOutput.error = input.getError();
			return reasonerOutput;
		}
		
		BoundIdentDecl[] boundIdentDecls = Lib.getBoundIdents(seq.goal());
		
//		
//		if (input.witnessesExp == null)
//		{
//			// Parse and typecheck input
//			input.witnessesExpr = new Expression[boundIdentDecls.length];
//			Expression witness;
//			for (int i = 0; i < boundIdentDecls.length; i++) {
//				if ( i< input.witnesses.length &&
//						input.witnesses[i] != null && 
//						input.witnesses[i].trim().length() != 0)
//				{
//					witness = Lib.parseExpression(input.witnesses[i]);
//					if (witness == null) 
//						return new ReasonerOutputFail(this,input,
//								"Parse error for expression "+input.witnesses[i]);
//					if (! Lib.isWellTypedInstantiation(witness,boundIdentDecls[i].getType(),seq.typeEnvironment())) 
//						return new ReasonerOutputFail(this,input,
//								"Type check failed : "+input.witnesses[i]+" expected type "+ boundIdentDecls[i].getType());
//					input.witnessesExpr[i] = witness;
//				}
//				else
//					input.witnessesExpr[i] = null;
//			}
//		}
//		else
		
			// copy and check that old input is still compatable
			// it can be that the number of bound variables have increased 
		    // or decreased, or their types have changed.
			Expression[] witnesses = new Expression[boundIdentDecls.length];
			for (int i = 0; i < witnesses.length; i++) {
				if (i< input.getExpressions().length)
				{
					if (! input.getExpressions()[i].getType().
							equals(boundIdentDecls[i].getType()))
						return new ReasonerOutputFail(this,input,
								"Type check failed : "+input.getExpressions()[i]+" expected type "+ boundIdentDecls[i].getType());
					witnesses[i] = input.getExpressions()[i];
				}
				else
				{
					witnesses[i]=null;
				}	
			}
			// input. = witnessesExprCopy;
		
		
		// Generate the well definedness predicate for the witnesses
		Predicate WDpred = Lib.WD(witnesses);
		// Generate the instantiated predicate
		Predicate instantiatedPred = Lib.instantiateBoundIdents(seq.goal(),witnesses);
		assert instantiatedPred != null;
		
		// Generate the successful reasoner output
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.display = "∃ goal (inst "+displayWitnesses(witnesses)+")";
		reasonerOutput.goal = seq.goal();

		// Generate the anticidents
		reasonerOutput.anticidents = new Anticident[2];
		
		// Well definedness condition
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = WDpred;
		
		// The instantiated goal
		reasonerOutput.anticidents[1] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[1].subGoal = instantiatedPred;
				
		return reasonerOutput;
	}
	
	private String displayWitnesses(Expression[] witnesses){
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
	
//	public static class Input implements ReasonerInput{
//		public final String[] witnesses;
//		public Expression[] witnessesExpr;
//		
//		public Input(String[] witnesses){
//			this.witnesses = witnesses;
//			this.witnessesExpr = null;
//		}
//		
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.witnesses = null;
//			int length = Integer.parseInt(serializableReasonerInput.getString("length"));
//			this.witnessesExpr = new Expression[length];
//			for (int i = 0; i < length; i++) {
//				// null value taken care of in getExpression.
//				this.witnessesExpr[i] = serializableReasonerInput.getExpression(String.valueOf(i));
//			}
//		}
//		
//		public SerializableReasonerInput genSerializable() {
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			assert witnessesExpr != null;
//			serializableReasonerInput.putString("length",String.valueOf(witnessesExpr.length));
//			for (int i = 0; i < witnessesExpr.length; i++) {
//				if (witnessesExpr[i]!=null)
//				serializableReasonerInput.putExpression(String.valueOf(i),witnessesExpr[i]);
//			}
//			return serializableReasonerInput;
//		}
//		
//	}

}
