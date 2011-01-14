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
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.match;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.matchAndDissociate;
import static org.eventb.internal.core.seqprover.eventbExtensions.OnePointInstantiator2.instantiatePredicate;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointFilter.QuantifiedFormUtil;

/**
 * Handles the processing for rewriting rules SIMP_IN_COMPSET and
 * SIMP_IN_COMPSET_ONEPOINT. The following operations are performed:
 * <ul>
 * <li>transforming the input predicate in an appropriate form while memorizing
 * the replacements to make,</li>
 * <li>instantiating the bound identifiers with their corresponding expressions
 * in the predicate.</li>
 * </ul>
 */
public class OnePointProcessorRewriting extends OnePointProcessor<Predicate> {

	public OnePointProcessorRewriting(RelationalPredicate rPred,
			FormulaFactory ff) {
		super(ff);
		this.original = toQuantifiedForm(rPred);
		this.processing = ((QuantifiedPredicate) original).getPredicate();
	}

	/**
	 * Converts the given predicate of the form <code>F ∈ {x · P ∣ E}</code>
	 * in a suitable form for the processing to be applied
	 * <code>∃x · E=F' ∧ P</code>, where <code>F'</code> is <code>F</code>
	 * pushed inside the quantification for <code>x</code>.
	 */
	private QuantifiedPredicate toQuantifiedForm(RelationalPredicate predicate) {
		assert match(predicate);
		QuantifiedFormUtil qfu = matchAndDissociate(predicate);

		final RelationalPredicate replacement = ff.makeRelationalPredicate(
				EQUAL,
				qfu.getExpression(),
				qfu.getElement().shiftBoundIdentifiers(
						qfu.getBoundIdents().length, ff), null);

		this.bids = qfu.getBoundIdents();
		this.replacements = new Expression[bids.length];

		final Predicate conjunctionPred = buildQuantifiedFormPredicate(
				replacement, qfu.getGuard());

		return ff.makeQuantifiedPredicate(EXISTS, qfu.getBoundIdents(),
				conjunctionPred, null);
	}

	private Predicate buildQuantifiedFormPredicate(
			RelationalPredicate replacement, Predicate existingGuard) {
		assert this.bids != null;
		assert this.replacements != null;

		final List<Predicate> conjuncts = breakMaplet(replacement);

		/*
		 * This list contains the predicates in which no replacement will be
		 * extracted. It contains the guard of the original comprehension set as
		 * well as potentially non-valid replacements.
		 */
		final List<Predicate> predicates = new ArrayList<Predicate>();
		predicates.add(existingGuard);

		for (Predicate eq : conjuncts) {
			/*
			 * If the current replacement is valid, then it is added to the
			 * replacements. Otherwise, it is added to the list of predicates
			 * described above.
			 */
			if (!checkReplacement((RelationalPredicate) eq)) {
				predicates.add(eq);
			}
		}
		if (predicates.size() == 1) {
			return existingGuard;
		} else {
			return ff.makeAssociativePredicate(LAND, predicates, null);
		}
	}

	@Override
	public void matchAndInstantiate() {
		successfullyApplied = false;

		if (!availableReplacement()) {
			return;
		}

		processing = instantiate(processing, replacements);
		successfullyApplied = true;
	}

	@Override
	protected Predicate instantiate(Predicate predicate,
			Expression[] replacements) {
		final QuantifiedPredicate newQPred = ff.makeQuantifiedPredicate(
				original.getTag(), bids, predicate, null);
		return instantiatePredicate(newQPred, replacements, ff);
	}

	public QuantifiedPredicate getQuantifiedPredicate() {
		return (QuantifiedPredicate) original;
	}

}
