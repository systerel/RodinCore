/*******************************************************************************
 * Copyright (c) 2011, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.reasonerInputs;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Common implementation for reasoners that take as input a list of hypotheses
 * and mark them as the only needed hypotheses in their generated rule.
 * 
 * @author Emmanuel Billaud
 * @since 2.3
 */
public abstract class HypothesesReasoner implements IReasoner {

	public static final class Input implements IReasonerInput, ITranslatableReasonerInput {

		private final Predicate[] preds;

		/**
		 * Creates an input with the given hypotheses.
		 * 
		 * @param preds
		 *            hypotheses to work with
		 */
		public Input(Predicate... preds) {
			if (preds == null) {
				throw new NullPointerException("null list of hypotheses");
			}
			this.preds = preds;
		}

		@Override
		public void applyHints(ReplayHints hints) {
			for (int i = 0; i < preds.length; i++) {
				preds[i] = hints.applyHints(preds[i]);
			}
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public Predicate[] getPred() {
			return preds;
		}

		/**
		 * @since 3.0
		 */
		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			final Predicate[] trPreds = new Predicate[preds.length];
			for (int i = 0; i < preds.length; i++) {
				trPreds[i] = preds[i].translate(factory);
			}
			return new Input(trPreds);
		}

		/**
		 * @since 3.0
		 */
		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			final ITypeEnvironmentBuilder typeEnv = factory
					.makeTypeEnvironment();
			for (int i = 0; i < preds.length; i++) {
				typeEnv.addAll(preds[i].getFreeIdentifiers());
			}
			return typeEnv;
		}

	}

	public final void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		// Nothing to do, all is in the generated rule
	}

	public final Input deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final Set<Predicate> neededHyps = reader.getNeededHyps();
		return new Input(neededHyps.toArray(new Predicate[neededHyps.size()]));
	}

}
