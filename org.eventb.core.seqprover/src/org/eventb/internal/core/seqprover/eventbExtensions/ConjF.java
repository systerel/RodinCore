/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added Input.getPred()
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner;

/**
 * A reasoner that generates a forward inference to split a conjunctive hypothesis into its conjuncts and hides the
 * original conjunction 
 * 
 * @author Farhad Mehta
 *
 */
public final class ConjF extends ForwardInfReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".conjF";
	
	@Override
	protected String getDisplay(Predicate pred) {
		return "∧ hyp (" + pred + ")";
	}

	@ProverRule("AND_L")
	@Override
	protected IRewriteHypAction getRewriteAction(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {
		if (! Lib.isConj(pred)) {
			throw new IllegalArgumentException(
					"Predicate is not a conjunction: " + pred);
		}
		final Set<Predicate> inferredHyp = Lib.breakPossibleConjunct(pred);
		final Set<Predicate> neededHyp = singleton(pred);
		return ProverFactory.makeRewriteHypAction(neededHyp, inferredHyp,
				neededHyp);
	}
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

}
