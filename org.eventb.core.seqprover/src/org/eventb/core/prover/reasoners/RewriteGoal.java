package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasoners.rewriter.Rewriter;
import org.eventb.core.prover.reasoners.rewriter.RewriterRegistry;
import org.eventb.core.prover.sequent.IProverSequent;

public class RewriteGoal extends SinglePredInputReasoner{
	
	public String getReasonerID() {
		return "rewriteGoal";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		SingleStringInput input;
		input = (SingleStringInput) reasonerInput;
		
		if (input.hasError())
		{
			return new ReasonerOutputFail(this,input,input.getError());
		}
		
		Rewriter rewriter = RewriterRegistry.getRewriter(input.getString());
		
		if (rewriter == null) 
			return new ReasonerOutputFail(this,input,
					"Uninstalled rewriter");
		
		Predicate newGoal = rewriter.apply(seq.goal());
		if (newGoal == null)
			return new ReasonerOutputFail(this,input,
					"Rewriter " + rewriter +" inapplicable for goal "+ seq.goal());

		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = newGoal;
				
		return reasonerOutput;
	}

}
