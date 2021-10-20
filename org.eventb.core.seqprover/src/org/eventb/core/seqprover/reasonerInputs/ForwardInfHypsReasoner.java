/*******************************************************************************
 * Copyright (c) 2021 Université de Lorraine
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import static java.util.Arrays.stream;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Common implementation for reasoners which input is the set of predicates of
 * their first forward inference rule.
 *
 * Warning: the predicates order in the input may not be preserved by
 * serialization/deserialization.
 *
 * @author Guillaume Verdier
 * @since 3.5
 */
public abstract class ForwardInfHypsReasoner implements IReasoner {

	public static final class Input implements IReasonerInput, ITranslatableReasonerInput {

		private Predicate[] predicates;

		public Input(Predicate[] predicates) {
			this.predicates = predicates;
		}

		public Predicate[] getPredicates() {
			return predicates;
		}

		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			return new Input(stream(predicates).map(pred -> pred.translate(factory)).toArray(Predicate[]::new));
		}

		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory.makeTypeEnvironment();
			for (Predicate pred : predicates) {
				typeEnv.addAll(pred.getFreeIdentifiers());
			}
			return typeEnv;
		}

		@Override
		public boolean hasError() {
			return false;
		}

		@Override
		public String getError() {
			return null;
		}

		@Override
		public void applyHints(ReplayHints renaming) {
			for (int i = 0; i < predicates.length; ++i) {
				predicates[i] = renaming.applyHints(predicates[i]);
			}
		}

	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		// nothing to do
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 1) {
			throw new SerializeException(new IllegalStateException("Expected exactly one antecedent."));
		}
		List<IHypAction> hypActions = antecedents[0].getHypActions();
		if (hypActions.size() < 1) {
			throw new SerializeException(new IllegalStateException("Expected at least one hyp action."));
		}
		IHypAction action = hypActions.get(0);
		if (!(action instanceof IForwardInfHypAction)) {
			throw new SerializeException(
					new IllegalStateException("Expected the first hyp action to be a forward inference."));
		}
		return new Input(((IForwardInfHypAction) action).getHyps().toArray(Predicate[]::new));
	}

}
