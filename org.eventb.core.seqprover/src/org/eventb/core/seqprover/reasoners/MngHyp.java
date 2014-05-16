/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasoners;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.internal.core.seqprover.SelectionHypAction;

public class MngHyp implements IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".mngHyp";

	public static class Input implements IReasonerInput, ITranslatableReasonerInput {

		ISelectionHypAction action;

//		public Input(ActionType type, Set<Predicate> hyps) {
//			this.action = new Action(type, hyps);
//		}

		public Input(ISelectionHypAction action) {
			this.action = action;
		}

		// TODO share this with Review reasoner input
		@Override
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

		@Override
		public String getError() {
			return null;
		}

		@Override
		public boolean hasError() {
			return false;
		}

		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			final ISelectionHypAction trAction = (ISelectionHypAction) action
					.translate(factory);
			return new Input(trAction);
		}

		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory
					.makeTypeEnvironment();
			for (Predicate hyp : action.getHyps()) {
				typeEnv.addAll(hyp.getFreeIdentifiers());
				
			}
			return typeEnv;
		}

	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public void serializeInput(IReasonerInput rInput, IReasonerInputWriter writer) throws SerializeException {
		// Nothing to serialize, all is in the rule
	}
	
	@Override
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

	@Override
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
