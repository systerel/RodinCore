/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - deselect WD predicate and used hypothesis
 *     Systerel - deselect WD predicate only if not already selected
 *     Systerel - refactored duplicated code with AllmpD
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

// TODO : maybe Rename to AllE or AllF
// TDOD : maybe return a fwd inference instead of a complete rule if WD is trivial
public class AllD extends AbstractAllD {
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".allD";
	private static final String display = "∀ hyp"; 
	
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	protected String getDisplayedRuleName() {
		return display;
	}

	/**
	 * Generates the antecedents for the rule to make.
	 * 
	 * @param ff
	 *            the formula factory
	 * @param WDpreds
	 *            the well definedness predicates for the instantiations.
	 * @param univHyp
	 *            the universal input predicate
	 * @param instantiatedPred
	 *            the universal predicate after instantiation
	 * @return the antecedent of the rule to make
	 */
	@Override
	protected IAntecedent[] getAntecedents(FormulaFactory ff, Set<Predicate> WDpreds,
			Predicate univHyp, Predicate instantiatedPred) {

		final IAntecedent[] antecedents = new IAntecedent[2];

		// First antecedent : Well Definedness condition
		antecedents[0] = ProverFactory.makeAntecedent(DLib.makeConj(ff, WDpreds));

		// The instantiated goal
		final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.addAll(WDpreds);
		addedHyps.addAll(Lib.breakPossibleConjunct(instantiatedPred));

		antecedents[1] = ProverFactory
				.makeAntecedent(
						null,
						addedHyps,
						WDpreds,
						Lib.NO_FREE_IDENT,
						Collections
								.<IHypAction> singletonList(getDeselectAction(univHyp)));
		return antecedents;
	}
	
	@Override
	protected String checkInstantiations(Expression[] expressions,
			BoundIdentDecl[] boundIdentDecls) {
		return null;
	}

}
