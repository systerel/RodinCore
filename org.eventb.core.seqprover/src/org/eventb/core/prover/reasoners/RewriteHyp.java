package org.eventb.core.prover.reasoners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.IReasoner;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasonerInputs.CombiInput;
import org.eventb.core.prover.reasonerInputs.SinglePredInput;
import org.eventb.core.prover.reasonerInputs.SingleStringInput;
import org.eventb.core.prover.reasoners.rewriter.Rewriter;
import org.eventb.core.prover.reasoners.rewriter.RewriterRegistry;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class RewriteHyp implements IReasoner{
	
	public String getReasonerID() {
		return "rewriteHyp";
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new SinglePredInput(reasonerInputSerializers[0]),
				new SingleStringInput(reasonerInputSerializers[1])
		);
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		CombiInput input = (CombiInput)reasonerInput;
		
		if (input.hasError())
		{
			return new ReasonerOutputFail(this,input,input.getError());
		}
		
		Hypothesis hyp = new Hypothesis(((SinglePredInput)input.getReasonerInputs()[0]).getPredicate());
		Rewriter rewriter = RewriterRegistry.getRewriter(((SingleStringInput)input.getReasonerInputs()[1]).getString());
		
		if (rewriter == null) 
			return new ReasonerOutputFail(this,input,
					"Uninstalled rewriter");
		
		if (! seq.hypotheses().contains(hyp))
			return new ReasonerOutputFail(this,input,
					"Nonexistent hypothesis:"+hyp);
		
		Predicate newHyp = rewriter.apply(hyp.getPredicate());
		if (newHyp == null)
			return new ReasonerOutputFail(this,input,
					"Rewriter " + rewriter +" inapplicable for hypothesis "+ hyp);

		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.neededHypotheses.add(hyp);
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].addedHypotheses.add(newHyp);
		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(hyp));
		reasonerOutput.anticidents[0].subGoal = seq.goal();
				
		return reasonerOutput;
	}
	
//	public static class Input implements ReasonerInput{
//
//		public final Rewriter rewriter;
//		public final Hypothesis hyp;
//		
//		public Input(Rewriter rewriter,Hypothesis hyp){
//			this.rewriter = rewriter;
//			this.hyp = hyp;
//		}
//
//		public Input(SerializableReasonerInput serializableReasonerInput) {
//			this.rewriter = RewriterRegistry.getRewriter(serializableReasonerInput.getString("rewriterID"));
//			assert this.rewriter != null;
//			this.hyp = new Hypothesis(serializableReasonerInput.getPredicate("hyp"));
//		}
//		
//		public SerializableReasonerInput genSerializable() {
//			SerializableReasonerInput serializableReasonerInput 
//			= new SerializableReasonerInput();
//			serializableReasonerInput.putString("rewriterID",rewriter.getRewriterID());
//			serializableReasonerInput.putPredicate("hyp",hyp.getPredicate());
//			return serializableReasonerInput;
//		}
//	}

}
