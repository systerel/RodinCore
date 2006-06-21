package org.eventb.core.prover.reasoners;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputFail;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.SerializableReasonerInput;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public class Review implements Reasoner{
	
	public String getReasonerID() {
		return "reviewed";
	}
	
	public ReasonerOutput apply(IProverSequent seq, ReasonerInput reasonerInput){
	
		// Organize Input
		Input input;
		if (reasonerInput instanceof SerializableReasonerInput){
			input = new Input((SerializableReasonerInput)reasonerInput);
		} 
		else input = (Input) reasonerInput;
		
		if (input == null){
			input = new Input(seq.selectedHypotheses(),seq.goal());
		}
		
		if ((! (seq.goal().equals(input.goal))) ||
		   (! (seq.hypotheses().containsAll(input.hyps))))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Reviewed sequent does not match";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.neededHypotheses = input.hyps;
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "reviewed";
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}
	
	public static class Input implements ReasonerInput{
		public final Set<Hypothesis> hyps;
		public final Predicate goal;
		
		public Input(Set<Hypothesis> hyps,Predicate goal){
			this.hyps = hyps;
			this.goal = goal;
		}
		
		public Input(SerializableReasonerInput serializableReasonerInput) {
			this.hyps = serializableReasonerInput.hypAction.getHyps();
			this.goal = serializableReasonerInput.getPredicate("goal");
		}
		
		public SerializableReasonerInput genSerializable() {
			SerializableReasonerInput serializableReasonerInput 
			= new SerializableReasonerInput();
			// the action type is irrelevant : just reusing code to store hypSets
			// inside hypActions.
			// TODO : maybe fix later
			serializableReasonerInput.hypAction =
				new HypothesesManagement.Action(ActionType.SELECT,this.hyps);
			serializableReasonerInput.putPredicate("goal",goal);
			return serializableReasonerInput;
		}
		
	}

}
