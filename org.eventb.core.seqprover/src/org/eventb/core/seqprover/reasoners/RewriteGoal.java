package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ProofRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.ProofRule.Anticident;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInputReasoner;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.reasoners.rewriter.Rewriter;
import org.eventb.core.seqprover.reasoners.rewriter.RewriterRegistry;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class RewriteGoal extends SinglePredInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".rewriteGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
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

		
		ProofRule reasonerOutput = new ProofRule(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = rewriter.getName()+" goal";
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
		reasonerOutput.anticidents[0].subGoal = newGoal;
				
		return reasonerOutput;
	}

}
