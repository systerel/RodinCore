/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Refactored class hierarchy
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;

public class Conj extends HypothesisReasoner implements IVersionedReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".conj";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	protected String getDisplay(Predicate pred) {
		return "∧ goal";
	}

	@ProverRule({ "AND_L", "AND_R" })
	@Override
	protected IAntecedent[] getAntecedents(IProverSequent sequent, Predicate hyp) {
		if (hyp != null) {
			throw new IllegalArgumentException("Reasoner " + getReasonerID()
					+ " inapplicable to a hypothesis");
		}
		return makeAntecedents(conjuncts(sequent.goal()));
	}

	// Use a linked hash set to remove duplicates without loosing the order of
	// children
	private Predicate[] conjuncts(Predicate pred) {
		if (pred.getTag() != LAND) {
			throw new IllegalArgumentException("Reasoner " + getReasonerID()
					+ " inapplicable to " + pred);
		}
		final Predicate[] children = ((AssociativePredicate) pred)
				.getChildren();
		final Set<Predicate> conjuncts = new LinkedHashSet<Predicate>(
				asList(children));
		return conjuncts.toArray(new Predicate[conjuncts.size()]);
	}

	private IAntecedent[] makeAntecedents(final Predicate[] newGoals) {
		final int length = newGoals.length;
		final IAntecedent[] antecedents = new IAntecedent[length];
		for (int i = 0; i < length; ++i) {
			antecedents[i] = makeAntecedent(newGoals[i]);
		}
		return antecedents;
	}

}
