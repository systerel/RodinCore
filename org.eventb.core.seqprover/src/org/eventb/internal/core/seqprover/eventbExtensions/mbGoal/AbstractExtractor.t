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
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;

import java.math.BigInteger;
import java.util.Set;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.EqualToSubset;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.Hypothesis;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.RelationToCartesian;

/**
 * Common implementation for extracting useful direct and derived hypotheses
 * from a given set. Predicates can be either given hypotheses or some
 * predicates derived soundly from hypotheses.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
@SuppressWarnings("unused")
public abstract class AbstractExtractor {

	protected final MembershipGoalRules rf;
	protected final Set<Predicate> hyps;
	protected final IProofMonitor pm;

	%include {FormulaV2.tom}

	protected AbstractExtractor(MembershipGoalRules rf, Set<Predicate> hyps, 
			IProofMonitor pm) {
		this.rf = rf;
		this.hyps = hyps;
		this.pm = pm;
	}

	/**
	 * Extracts from a given hypothesis.
	 */
	protected final void extract(Predicate hyp) {
		if (pm.isCanceled()) {
			return;
		}
		final Rationale rat = new Hypothesis(hyp, rf);
		%match (hyp) {
			In(_, _) -> {
				extractIn(rat);
			}
			(Subset|SubsetEq)(_, _) -> {
				extractSubset(rat);
			}
			Equal(A, B) -> {
				// Ensure `A is a set
				if (`A.getType().getBaseType() != null) {
					extractSubset(new EqualToSubset(true, rf.subseteq(`A, `B),
							rat));
					extractSubset(new EqualToSubset(false, rf.subseteq(`B, `A),
							rat));
				}
			}
		}
	}

	protected void extractIn(Rationale rat) {
		if (pm != null && pm.isCanceled()) {
			return;
		}
		final Predicate pred = rat.predicate();
		%match (pred) {
			In(x, (Rel|Trel|Srel|Strel
				  |Pfun|Tfun|Pinj|Tinj|Psur|Tsur|Tbij)(A, B)) -> {
				final Predicate child = rf.subseteq(`x, rf.cprod(`A, `B));
				extractSubset(new RelationToCartesian(child, rat));
			}
		}
	}

	/*
	 * Intermediate method to deconstruct the inclusion so that we're not
	 * bothered with the two cases (SUBSETEQ and SUBSET) afterwards.
	 */
	protected final void extractSubset(Rationale rat) {
		if (pm != null && pm.isCanceled()) {
			return;
		}
		final Predicate pred = rat.predicate();
		final int tag = pred.getTag();
		assert tag == SUBSETEQ || tag == SUBSET;
		final RelationalPredicate rpred = (RelationalPredicate) pred;
		final Expression left = rpred.getLeft();
		final Expression right = rpred.getRight();
		extractSubset(tag == SUBSET, left, right, rat);
	}

	protected abstract void extractSubset(boolean strict, Expression left,
			Expression right, Rationale rat);

}
