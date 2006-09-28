package org.eventb.core.seqprover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInputReasoner;
import org.eventb.core.seqprover.reasoners.rewriter.Rewriter;
import org.eventb.core.seqprover.reasoners.rewriter.RewriterRegistry;

public class RewriteGoal extends SingleStringInputReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".rewriteGoal";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		SingleStringInput input;
		input = (SingleStringInput) reasonerInput;
		
		if (input.hasError())
			ProverFactory.reasonerFailure(this,input,input.getError());
		
		Rewriter rewriter = RewriterRegistry.getRewriter(input.getString());
		
		if (rewriter == null) 
			return ProverFactory.reasonerFailure(this,input,"Uninstalled rewriter");
		
		Predicate newGoal = rewriter.apply(seq.goal());
		if (newGoal == null)
			return ProverFactory.reasonerFailure(this,input,
					"Rewriter " + rewriter +" inapplicable for goal "+ seq.goal());

		IAnticident[] anticidents = new IAnticident[1];
		
		anticidents[0] = ProverFactory.makeAnticident(newGoal);
		
		IProofRule reasonerOutput = ProverFactory.makeProofRule(
				this,input,
				seq.goal(),
				rewriter.getName()+" goal",
				anticidents
				);		
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = rewriter.getName()+" goal";
//		reasonerOutput.anticidents = new Anticident[1];
//		
//		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
//		reasonerOutput.anticidents[0].goal = newGoal;
				
		return reasonerOutput;
	}

}
