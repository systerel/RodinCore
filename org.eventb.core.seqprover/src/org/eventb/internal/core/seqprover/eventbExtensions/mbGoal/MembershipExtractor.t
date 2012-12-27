/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.mbGoal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.DomProjection;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.LastOverride;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.RanProjection;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rationale.SetExtensionMember;

/**
 * Extract useful membership predicates from a set of hypotheses.  Usefulness
 * means having the given expression as left-hand side.  Predicates can be either
 * a given hypothesis or some predicate derived soundly from a hypothesis.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
@SuppressWarnings("unused")
public class MembershipExtractor extends AbstractExtractor {

	private final Expression member;
	private final List<Rationale> result;

	%include {FormulaV2.tom}

	public MembershipExtractor(MembershipGoalRules rf, Expression member,
			Set<Predicate> hyps, IProofMonitor pm) {
		super(rf, hyps, pm);
		this.member = member;
		this.result = new ArrayList<Rationale>();
	}

	public List<Rationale> extract() {
		for (Predicate hyp : hyps) {
			extract(hyp);
		}
		return result;
	}

	/* TODO
	 * Idea for later: also remember negative membership for fast exit
	 * when membership is known to not hold. However, this might prevent
	 * discharging when faced with contradictory hypotheses.
	 */

	protected void extractIn(final Rationale rat) {
		if (pm.isCanceled()) {
			return;
		}
		final Predicate pred = rat.predicate();
		%match (pred) {
			In(x, _) && x << Expression member -> {
				result.add(rat);

				// TODO Also simplify rhs when restrictive
				// TODO a: dom(f;g;h) => a: dom(f;g) and similar
				// TODO a: A <| f => a: f and similar
				// TODO a: dom(A <| f) => a: A and similar
				// TODO a: A /\ B => a: A and similar
				// TODO a: A \ B => a: A

				// Found a matching member, don't recurse anymore.
				return;
			}
			In(Mapsto(x, y), Cprod(A, B)) -> {
				extractIn(new DomProjection(true, rf.in(`x, `A), rat));
				extractIn(new RanProjection(true, rf.in(`y, `B), rat));
			}
			In(Mapsto(x, y), S) -> {
				extractIn(new DomProjection(false, rf.in(`x, rf.dom(`S)), rat));
				extractIn(new RanProjection(false, rf.in(`y, rf.ran(`S)), rat));
			}
		}
		// Must be after detection of useful membership
		super.extractIn(rat);
	}

	protected void extractSubset(boolean strict, Expression left,
			Expression right, Rationale rat) {
		%match (Expression left, Expression right) {
			SetExtension(eList(_*,x,_*)), S -> {
				extractIn(new SetExtensionMember(`x, rf.in(`x, `S), rat));
			}
			Ovr(eList(_*, g)), S -> {
				extractSubset(new LastOverride(rf.subset(strict, `g, `S), rat));
			}
			// TODO same with extensions such as union around eset.

		}
	}

}
