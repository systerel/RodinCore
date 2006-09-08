package org.eventb.core.seqprover.reasoners;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputSerializer;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.RuleFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAnticident;
import org.eventb.core.seqprover.IReasonerInputSerializer.SerializeException;
import org.eventb.core.seqprover.reasonerInputs.CombiInput;
import org.eventb.core.seqprover.reasonerInputs.MultiplePredInput;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.sequent.HypothesesManagement.Action;
import org.eventb.core.seqprover.sequent.HypothesesManagement.ActionType;

public class MngHyp implements IReasoner{
	
	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".mngHyp";
	
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	public IReasonerInput deserializeInput(IReasonerInputSerializer reasonerInputSerializer) throws SerializeException {
		IReasonerInputSerializer[] reasonerInputSerializers = reasonerInputSerializer.getSubInputSerializers();		
		return new CombiInput(
				new SingleStringInput(reasonerInputSerializers[0]),
				new MultiplePredInput(reasonerInputSerializers[1])
		);
	}
	
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProgressMonitor progressMonitor){
		
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
		
		
		IAnticident[] anticidents = new IAnticident[1];
		
		anticidents[0] = RuleFactory.makeAnticident(
				seq.goal(),
				null,
				action);
		
		IProofRule reasonerOutput = RuleFactory.makeProofRule(
				this,input,
				seq.goal(),
				"sl/ds",
				anticidents);
		
		
//		ProofRule reasonerOutput = new ProofRule(this,input);
//		reasonerOutput.goal = seq.goal();
//		reasonerOutput.display = "sl/ds";
//		reasonerOutput.anticidents = new Anticident[1];
//		
//		reasonerOutput.anticidents[0] = new ProofRule.Anticident();
//		reasonerOutput.anticidents[0].hypAction.add(action);
//		reasonerOutput.anticidents[0].goal = seq.goal();
				
		return reasonerOutput;
	}

}
