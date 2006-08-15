package org.eventb.core.prover.reasoners;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.prover.IReasonerInputSerializer;
import org.eventb.core.prover.IReasoner;
import org.eventb.core.prover.IReasonerInput;
import org.eventb.core.prover.ReasonerOutput;
import org.eventb.core.prover.ReasonerOutputSucc;
import org.eventb.core.prover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.prover.ReasonerOutputSucc.Anticident;
import org.eventb.core.prover.reasonerInputs.CombiInput;
import org.eventb.core.prover.reasonerInputs.MultiplePredInput;
import org.eventb.core.prover.reasonerInputs.SingleStringInput;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.HypothesesManagement.Action;
import org.eventb.core.prover.sequent.HypothesesManagement.ActionType;

public class MngHyp implements IReasoner{
	
	public String getReasonerID() {
		return "mngHyp";
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new SingleStringInput(reasonerInputSerializers[0]),
				new MultiplePredInput(reasonerInputSerializers[1])
		);
	}
	
	public ReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
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
