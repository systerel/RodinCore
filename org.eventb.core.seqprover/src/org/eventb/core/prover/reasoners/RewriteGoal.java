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

public class RewriteGoal implements Reasoner{
	
	public String getReasonerID() {
		return "rewriteGoal";
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
		
		Predicate newGoal = input.rewriter.apply(seq.goal());
		if (newGoal == null)
			return new ReasonerOutputFail(this,input,
					"Rewriter " + input.rewriter +" inapplicable for goal "+ seq.goal());

		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].subGoal = newGoal;
				
		return reasonerOutput;
	}
	
	public static class Input implements ReasonerInput{

		public final Rewriter rewriter;
		
		public Input(Rewriter rewriter){
			this.rewriter = rewriter;
		}

		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.rewriter = RewriterRegistry.getRewriter(serializableReasonerInput.getString("rewriterID"));
			assert this.rewriter != null;
		}
		
		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			serializableReasonerInput.putString("rewriterID",rewriter.getRewriterID());
			return serializableReasonerInput;
		}
	}

}
