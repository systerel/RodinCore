/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
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

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * Common implementation for reasoners that work on many hypotheses and
 * mark it as the sole needed hypotheses in their generated rule.
 * 
 * @author Emmanuel Billaud
 * @since 2.3
 */
public abstract class HypothesesReasoner implements IReasoner {

	public static final class Input implements IReasonerInput {

		private Predicate[] preds;

		/**
		 * The parameter is the hypothesis on which to work. If
		 * <code>null</code>, the work will deal only with the goal.
		 * 
		 * @param preds
		 *            hypotheses to work with or <code>null</code>
		 */
		public Input(Predicate... preds) {
			this.preds = preds;
		}

		@Override
		public void applyHints(ReplayHints hints) {
			if (preds != null) {
				for (Predicate pred : preds) {
					pred = hints.applyHints(pred);
				}
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

	}

	public final void serializeInput(IReasonerInput rInput,
			IReasonerInputWriter writer) throws SerializeException {
		// Nothing to do, all is in the generated rule
	}

	public final Input deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final Set<Predicate> neededHyps = reader.getNeededHyps();
		if (neededHyps.size() == 0) {
			return new Input((Predicate) null);
		}
		Predicate[] hyps = new Predicate[neededHyps.size()];
		int count = 0; 
		for (Predicate hyp : neededHyps) {
			hyps[count] = hyp;
			count++;
		}
		return new Input(hyps);
	}

}
