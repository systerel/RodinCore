package org.eventb.core.seqprover.reasoners;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.ReasonerOutput;
import org.eventb.core.seqprover.ReasonerOutputFail;
import org.eventb.core.seqprover.ReasonerOutputSucc;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.ReasonerOutputSucc.Anticident;
import org.eventb.core.seqprover.reasonerInputs.CombiInput;
import org.eventb.core.seqprover.reasonerInputs.MultiplePredInput;
import org.eventb.core.seqprover.reasonerInputs.SinglePredInput;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.IProverSequent;

public class Review implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".review";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new MultiplePredInput(reasonerInputSerializers[0]),
				new SinglePredInput(reasonerInputSerializers[1]),
				new SingleStringInput(reasonerInputSerializers[2])
		);
	}
	
	public ReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
	
		// Organize Input
		CombiInput input = (CombiInput) reasonerInput;
		
		Set<Hypothesis> hyps = 
			Hypothesis.Hypotheses(
					((MultiplePredInput)input.getReasonerInputs()[0]).getPredicates());
		Predicate goal = ((SinglePredInput)input.getReasonerInputs()[1]).getPredicate();
		int reviewerConfidence = Integer.parseInt(((SingleStringInput)input.getReasonerInputs()[2]).getString());
		
		if ((! (seq.goal().equals(goal))) ||
		   (! (seq.hypotheses().containsAll(hyps))))
		{
			ReasonerOutputFail reasonerOutput = new ReasonerOutputFail(this,input);
			reasonerOutput.error = "Reviewed sequent does not match";
			return reasonerOutput;
		}
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.neededHypotheses = hyps;
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "rv (confidence "+reviewerConfidence+")";
		assert reviewerConfidence > 0;
		assert reviewerConfidence <= IConfidence.REVIEWED_MAX;
		reasonerOutput.reasonerConfidence = reviewerConfidence;
		
		reasonerOutput.anticidents = new Anticident[0];
		
		return reasonerOutput;
	}
	
}
