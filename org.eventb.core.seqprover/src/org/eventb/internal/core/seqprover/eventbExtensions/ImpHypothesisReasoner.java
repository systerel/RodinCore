/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

/**
 * Common implementation for reasoners that works on an implicative hypothesis.
 * 
 * @author Emmanuel Billaud
 */
public abstract class ImpHypothesisReasoner extends HypothesisReasoner {

	@Override
	protected final IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {

		if (pred == null) {
			throw new IllegalArgumentException("Null hypothesis");
		}
		if (!Lib.isImp(pred)) {
			throw new IllegalArgumentException(
					"Hypothesis is not an implication: " + pred);
		}

		final Predicate impLeft = Lib.impLeft(pred);
		final Predicate impRight = Lib.impRight(pred);
		final IHypAction hideHypAction = makeHideHypAction(singleton(pred));
		return getAntecedents(impLeft, impRight, hideHypAction);
	}

	protected abstract IAntecedent[] getAntecedents(Predicate left,
			Predicate right, IHypAction hideHypAction);

	protected boolean isGoalDependent(IProverSequent sequent, Predicate pred) {
		return false;
	}

}
