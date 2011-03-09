package org.eventb.core.seqprover.reasoners;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.SelectionHypAction;

public class MngHyp implements IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".mngHyp";

	public static class Input implements IReasonerInput {

		ISelectionHypAction action;

//		public Input(ActionType type, Set<Predicate> hyps) {
//			this.action = new Action(type, hyps);
//		}

		public Input(ISelectionHypAction action) {
			this.action = action;
		}

		// TODO share this with Review reasoner input
		public void applyHints(ReplayHints hints) {

			final String type = action.getActionType();
			final Collection<Predicate> hyps = action.getHyps();
			Predicate[] newPreds = new Predicate[hyps.size()];
			int i = 0;
			for (Predicate hyp : hyps) {
				newPreds[i++] = hints.applyHints(hyp);
			}
			action = new SelectionHypAction(type, new LinkedHashSet<Predicate>(Arrays.asList(newPreds)));
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	public void serializeInput(IReasonerInput rInput, IReasonerInputWriter writer) throws SerializeException {
		// Nothing to serialize, all is in the rule
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		final IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Two many antecedents in the rule"));
		}
		final List<IHypAction> actions = antecedents[0].getHypActions();
		if (actions.size() != 1) {
			throw new SerializeException(new IllegalStateException(
					"Two many actions in the rule antecedent"));
		}
		return new Input((ISelectionHypAction) actions.get(0));
	}

	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		Input input = (Input) reasonerInput;
		IHypAction action = input.action;
		
		return ProverFactory.makeProofRule(this, input, "sl/ds", Collections.singletonList(action));
		
//		IAntecedent antecedent = ProverFactory.makeAntecedent(seq.goal(), null,
//				action);
//		IProofRule reasonerOutput = ProverFactory.makeProofRule(this, input,
//				seq.goal(), "sl/ds", antecedent);
//		return reasonerOutput;
	}

}
