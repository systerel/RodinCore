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
 * Extract useful inclusion predicates from a set of hypotheses.
 * 
 * @author Laurent Voisin
 * @author Emmanuel Billaud
 */
@SuppressWarnings("unused")
public class GeneratorExtractor extends AbstractExtractor {

	private final List<Generator> result;

	%include {FormulaV2.tom}

	public GeneratorExtractor(MembershipGoalRules rf, Set<Predicate> hyps) {
		super(rf, hyps);
		this.result = new ArrayList<Generator>();
	}

	public List<Generator> extract() {
		for (Predicate hyp : hyps) {
			extract(hyp);
		}
		return result;
	}

	protected void extractSubset(boolean strict, Expression left,
			Expression right, Rationale rat) {
		%match (Expression left, Expression right) {
			_, _ -> {
				result.add(new Inclusion(rat));
			}
			A, B -> {
				if (`A.getType().getSource() != null) {
					extractSubset(new DomProjection(false, rf.subset(
						strict, rf.dom(`A), rf.dom(`B)), rat, rf));
					extractSubset(new RanProjection(false, rf.subset(
						strict, rf.ran(`A), rf.ran(`B)), rat, rf));
				}
			}
		}
	}

}
