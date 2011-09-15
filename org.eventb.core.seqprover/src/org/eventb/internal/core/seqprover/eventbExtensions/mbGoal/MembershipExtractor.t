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

import static org.eventb.core.ast.Formula.*;

import java.math.BigInteger;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.DomProjection;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.Hypothesis;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.RanProjection;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.SetExtensionMember;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.RelationToCartesian;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.EqualToSubset;

/**
 * Extract useful membership predicates from a set of hypotheses.  Usefulness
 * means having the given expression as left-hand side.  Predicates can be either
 * a given hypothesis or some predicate derived soundly from a hypothesis.
 */
@SuppressWarnings("unused")
public class MembershipExtractor {

	private final MembershipGoalRules rf;
	private final Expression member;
	private final Set<Predicate> hyps;
	private final List<Rationale> result;

	%include {FormulaV2.tom}

	public MembershipExtractor(MembershipGoalRules rf, Expression member,
			Set<Predicate> hyps) {
		this.rf = rf;
		this.member = member;
		this.hyps = hyps;
		this.result = new ArrayList<Rationale>();
	}

	public List<Rationale> extract() {
		for (Predicate hyp : hyps) {
			extract(hyp);
		}
		return result;
	}

	private void extract(final Predicate hyp) {
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
							rat, rf));
					extractSubset(new EqualToSubset(false, rf.subseteq(`B, `A),
							rat, rf));
				}
			}

			// TODO Simplify rhs when restrictive
			// TODO a: dom(f;g;h) => a: dom(f;g) and similar
			// TODO a: A <| f => a: f and similar
			// TODO a: dom(A <| f) => a: A and similar
			// TODO a: A /\ B => a: A and similar
			// TODO a: A \ B => a: A

			/* TODO
			 * Idea for later: also remember negative membership for fast exit
			 * when membership is know to not hold. However, this might prevent
			 * discharging when faced with contradictory hypotheses.
			 */
		}
	}

	private void extractIn(final Rationale rat) {
		final Predicate pred = rat.predicate();
		%match (pred) {
			In(x, _) && x << Expression member -> {
				result.add(rat);
				return;
			}
			In(Mapsto(x, y), Cprod(A, B)) -> {
				extractIn(new DomProjection(rf.in(`x, `A), rat, rf));
				extractIn(new RanProjection(rf.in(`y, `B), rat, rf));
				return;
			}
			In(Mapsto(x, y), S) -> {
				extractIn(new DomProjection(rf.in(`x, rf.dom(`S)), rat, rf));
				extractIn(new RanProjection(rf.in(`y, rf.ran(`S)), rat, rf));
			}
			In(x, (Rel|Trel|Srel|Strel
				  |Pfun|Tfun|Pinj|Tinj|Psur|Tsur|Tbij)(A, B)) -> {
				final Expression left = `x;
				final Expression right = rf.cprod(`A, `B);
				final Predicate child = rf.subseteq(left,right);
				extractSubset(new RelationToCartesian(child, rat, rf));
			}
		}
	}

	private void extractSubset(Rationale rat) {
		final Predicate pred = rat.predicate();
		final int tag = pred.getTag();
		assert tag == SUBSETEQ || tag == SUBSET;
		final RelationalPredicate rpred = (RelationalPredicate) pred;
		final Expression left = rpred.getLeft();
		final Expression right = rpred.getRight();
		%match (Expression left, Expression right) {
			SetExtension(eList(_*,x,_*)), S -> {
				extractIn(new SetExtensionMember(`x, rf.in(`x, `S), rat, rf));
			}

			// TODO same with extensions such as union around eset.
			// TODO same when eset is last member of overriding.

		}
	}

}
