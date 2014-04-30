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
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeDeselectHypAction;
import static org.eventb.core.seqprover.eventbExtensions.Lib.breakPossibleConjunct;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiation;

/**
 * Deprecated implementation of the existential elimination reasoner.
 * <p>
 * This class must not be used in new code but is kept to ensure backward
 * compatibility of proof trees.
 * </p>
 * 
 * @author fmehta
 * 
 * @deprecated use the reasoner ExF instead since it generates a forward
 *             inference instead
 */
@Deprecated
public class ExE extends HypothesisReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".exE";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("XST_L")
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent,
			Predicate pred) throws IllegalArgumentException {

		if (pred == null) {
			throw new IllegalArgumentException("Null hypothesis");
		}
		if (!Lib.isExQuant(pred)) {
			throw new IllegalArgumentException(
					"Hypothesis is not existentially quantified: " + pred);
		}
		final QuantifiedPredicate exQ = (QuantifiedPredicate) pred;
		final ISealedTypeEnvironment typenv = sequent.typeEnvironment();
		final FreshInstantiation inst = new FreshInstantiation(exQ, typenv);
		final FreeIdentifier[] freshIdents = inst.getFreshIdentifiers();
		final IHypAction action = makeDeselectHypAction(asList(pred));
		return new IAntecedent[] { makeAntecedent(sequent.goal(),
				breakPossibleConjunct(inst.getResult()), freshIdents,
				singletonList(action)) };
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "∃ hyp";
	}

}
