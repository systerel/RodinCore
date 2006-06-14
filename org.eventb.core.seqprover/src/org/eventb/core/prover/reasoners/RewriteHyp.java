package org.eventb.core.prover.reasoners;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasoners.rewriter.Rewriter;
import org.eventb.core.prover.reasoners.rewriter.RewriterRegistry;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class RewriteHyp implements Reasoner{
	
	public String getReasonerID() {
		return "rewriteHyp";
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		if (input.rewriter == null) 
			return new ReasonerOutputFail(this,input,
					"Uninstalled rewriter");
		
		if (! seq.hypotheses().contains(input.hyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+input.hyp);
		
		Predicate newHyp = input.rewriter.apply(input.hyp.getPredicate());
		if (newHyp == null)
			return new ReasonerOutputFail(this,input,
					"Rewriter " + input.rewriter +" inapplicable for hypothesis "+ input.hyp);

		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.neededHypotheses.add(input.hyp);
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].addedHypotheses.add(newHyp);
		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(input.hyp));
		reasonerOutput.anticidents[0].subGoal = seq.goal();
				
		return reasonerOutput;
	}
	
	public static class Input implements ReasonerInput{

		public final Rewriter rewriter;
		public final Hypothesis hyp;
		
		public Input(Rewriter rewriter,Hypothesis hyp){
			this.rewriter = rewriter;
			this.hyp = hyp;
		}

		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.rewriter = RewriterRegistry.getRewriter(serializableReasonerInput.getString("rewriterID"));
			assert this.rewriter != null;
			this.hyp = new Hypothesis(serializableReasonerInput.getPredicate("hyp"));
		}
		
		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			serializableReasonerInput.putString("rewriterID",rewriter.getRewriterID());
			serializableReasonerInput.putPredicate("hyp",hyp.getPredicate());
			return serializableReasonerInput;
		}
	}

}
