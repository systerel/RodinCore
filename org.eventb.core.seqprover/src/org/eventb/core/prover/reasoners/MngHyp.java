package org.eventb.core.prover.reasoners;

import java.util.Set;

import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.Reasoner;
import org.eventb.core.prover.ReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public class MngHyp implements Reasoner{
	
	public String getReasonerID() {
		return "mngHyp";
	}
	
	public ReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new SingleStringInput(reasonerInputSerializers[0]),
				new MultiplePredInput(reasonerInputSerializers[1])
		);
	}
	
	public ReasonerOutput apply(IProverSequent seq,ReasonerInput reasonerInput){
		
		//	 Organize Input
		CombiInput input = (CombiInput) reasonerInput;
		
		ActionType actionType = 
			ActionType.valueOf(((SingleStringInput)input.getReasonerInputs()[0]).getString());
		assert actionType != null;
		Set<Hypothesis> hyps = Hypothesis.Hypotheses(
				((MultiplePredInput)input.getReasonerInputs()[1]).getPredicates());
		Action action = new Action(actionType,hyps);
		
		// TODO : maybe remove absent hyps - useful for later replay
		// input.action.getHyps().retainAll(seq.hypotheses());
		
		ReasonerOutputSucc reasonerOutput = new ReasonerOutputSucc(this,input);
		reasonerOutput.goal = seq.goal();
		reasonerOutput.display = "sl/ds";
		reasonerOutput.anticidents = new Anticident[1];
		
		reasonerOutput.anticidents[0] = new ReasonerOutputSucc.Anticident();
		reasonerOutput.anticidents[0].hypAction.add(action);
		reasonerOutput.anticidents[0].subGoal = seq.goal();
				
		return reasonerOutput;
	}

}
