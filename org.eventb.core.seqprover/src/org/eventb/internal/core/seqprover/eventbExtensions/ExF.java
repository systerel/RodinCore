/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Collections.singleton;
import static org.eventb.core.seqprover.ProverFactory.makeRewriteHypAction;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;

import java.util.Set;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.ForwardInfReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiaton;

/**
 * Reasoner that returns a forward inference to free existentially bound
 * variables in a hypothesis.
 * 
 * @author Farhad Mehta
 */
public class ExF extends ForwardInfReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".exF";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "∃ hyp (" + pred + ")";
	}

	@ProverRule("XST_L")
	@Override
	protected IRewriteHypAction getRewriteAction(IProverSequent sequent,
			Predicate pred) {

		if (!Lib.isExQuant(pred)) {
			throw new IllegalArgumentException(
					"Predicate is not existentially quantified: " + pred);
		}
		final QuantifiedPredicate exQ = (QuantifiedPredicate) pred;
		final ISealedTypeEnvironment typenv = sequent.typeEnvironment();
		final FreshInstantiaton inst = new FreshInstantiaton(exQ, typenv);
		final FreeIdentifier[] freshIdents = inst.getFreshIdentifiers();
		final Set<Predicate> inferredHyps = breakPossibleConjunct(inst
				.getResult());
		final Set<Predicate> neededHyp = singleton(pred);
		return makeRewriteHypAction(neededHyp, freshIdents, inferredHyps,
				neededHyp);
	}

}
