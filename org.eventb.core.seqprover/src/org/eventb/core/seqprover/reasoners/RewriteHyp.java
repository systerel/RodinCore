package org.eventb.core.seqprover.reasoners;

import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.reasonerInputs.CombiInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.reasoners.rewriter.Rewriter;
import org.eventb.core.seqprover.reasoners.rewriter.RewriterRegistry;
import org.eventb.core.seqprover.sequent.Hypothesis;

public class RewriteHyp implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".rewriteHyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new SinglePredInput(reasonerInputSerializers[0]),
				new SingleStringInput(reasonerInputSerializers[1])
		);
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
		CombiInput input = (CombiInput)reasonerInput;
		
		if (input.hasError())
			RuleFactory.reasonerFailure(this,input,input.getError());
		
		Hypothesis hyp = new Hypothesis(((SinglePredInput)input.getReasonerInputs()[0]).getPredicate());
		Rewriter rewriter = RewriterRegistry.getRewriter(((SingleStringInput)input.getReasonerInputs()[1]).getString());
		
		if (rewriter == null) 
			return RuleFactory.reasonerFailure(this,input,
					"Uninstalled rewriter");
		
		if (! seq.hypotheses().contains(hyp))
			return RuleFactory.reasonerFailure(this,input,
					"Nonexistent hypothesis:"+hyp);
		
		Predicate newHyp = rewriter.apply(hyp.getPredicate());
		if (newHyp == null)
			return RuleFactory.reasonerFailure(this,input,
					"Rewriter " + rewriter +" inapplicable for hypothesis "+ hyp);

		IAnticident[] anticidents = new IAnticident[1];
		
		anticidents[0] = RuleFactory.makeAnticident(
				seq.goal(),
				Collections.singleton(newHyp),
				Lib.deselect(hyp));
		
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				hyp,
				" hyp ("+hyp.toString()+")",
				anticidents);
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = rewriter.getName()+" hyp ("+hyp.toString()+")";
//		reasonerOutput.neededHypotheses.add(hyp);
//		reasonerOutput.anticidents = new Anticident[1];
//		
//		reasonerOutput.anticidents[0] = new ProofRule.Anticident(seq.goal());
//		reasonerOutput.anticidents[0].addToAddedHyps(newHyp);
//		reasonerOutput.anticidents[0].hypAction.add(Lib.deselect(hyp));
				
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
