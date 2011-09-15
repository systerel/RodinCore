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
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rule.BinaryRule;

/**
 * Internal rules used by the Membership Goal reasoner for justification.
 */
@SuppressWarnings("unused")
public class MembershipGoalRules {

	private final FormulaFactory ff;

	%include {FormulaV2.tom}
	
	public MembershipGoalRules(FormulaFactory ff) {
		this.ff = ff;
	}

	/**
	 * Rule whose consequent is a relational predicate.
	 */
	private Rule<RelationalPredicate> relational(int tag, Expression left,
			Expression right, Rule<?>... antecedents) {
		return new Rule<RelationalPredicate>(ff.makeRelationalPredicate(tag,
				left, right, null), ff, antecedents);
	}

	/**
	 * Rule whose consequent is an IN predicate.
	 */
	private Rule<RelationalPredicate> in(Expression member, Expression set,
			Rule<?>... antecedents) {
		return relational(IN, member, set, antecedents);
	}

	/**
	 * Rule whose consequent is a SUBSET predicate.
	 */
	private Rule<RelationalPredicate> subset(Expression member, Expression set,
			Rule<?>... antecedents) {
		return relational(SUBSET, member, set, antecedents);
	}

	/**
	 * Rule whose consequent is a SUBSETEQ predicate.
	 */
	private Rule<RelationalPredicate> subseteq(Expression member,
			Expression set, Rule<?>... antecedents) {
		return relational(SUBSETEQ, member, set, antecedents);
	}

	/**
	 * Rule whose consequent is a given hypothesis.
	 */
	public <T extends Predicate> Rule<T> hypothesis(T hyp) {
		return new Rule.Hypothesis<T>(hyp, ff);
	}

	public Predicate in(Expression member, Expression set) {
		return ff.makeRelationalPredicate(IN, member, set, null);
	}

	public Predicate subseteq(Expression member, Expression set) {
		return ff.makeRelationalPredicate(SUBSETEQ, member, set, null);
	}

	public Expression dom(Expression child) {
		return ff.makeUnaryExpression(KDOM, child, null);
	}

	public Expression ran(Expression child) {
		return ff.makeUnaryExpression(KRAN, child, null);
	}

	public Expression cprod(Expression left, Expression right) {
		return ff.makeBinaryExpression(CPROD, left, right, null);
	}

	// TODO put back RelationalPredicate as type argument?
	public Rule<RelationalPredicate> compose(Rule<?> left,
			Rule<?> right) {
		final Predicate leftConsequent = left.getConsequent(); 
		final Predicate rightConsequent = right.getConsequent(); 
		%match (leftConsequent, rightConsequent) {
			In(x, A), SubsetEq(A, B) -> {
				return in(`x, `B, left, right);
			}
			In(x, A), Subset(A, B) -> {
				return in(`x, `B, left, right);
			}
			SubsetEq(A, B), SubsetEq(B, C) -> {
				return subseteq(`A, `C, left, right);
			}
			Subset(A, B), SubsetEq(B, C) -> {
				return subset(`A, `C, left, right);
			}
			SubsetEq(A, B), Subset(B, C) -> {
				return subset(`A, `C, left, right);
			}
			Subset(A, B), Subset(B, C) -> {
				return subset(`A, `C, left, right);
			}
		}
		throw new IllegalArgumentException("Can't compose " + left
				+ " with " + right);
	}

	public Rule<RelationalPredicate> domPrj(Rule<?> child) {
		final Predicate childConsequent = child.getConsequent();
		%match (childConsequent) {
			In(Mapsto(x,_), Cprod(A,_)) -> {
				return in(`x, `A, child);
			}
			In(Mapsto(x,_), S) -> {
				return in(`x, dom(`S), child);
			}
		}
		throw new IllegalArgumentException("Can't project on domain for "
				+ child);
	}

	public Rule<RelationalPredicate> ranPrj(Rule<?> child) {
		final Predicate childConsequent = child.getConsequent();
		%match (childConsequent) {
			In(Mapsto(_,y), Cprod(_,B)) -> {
				return in(`y, `B, child);
			}
			In(Mapsto(_,y), S) -> {
				return in(`y, ran(`S), child);
			}
		}
		throw new IllegalArgumentException("Can't project on range for "
				+ child);
	}

	public Rule<RelationalPredicate> setExtMember(Expression member, Rule<?> child) {
		final Predicate childConsequent = child.getConsequent();
		%match (Expression member, childConsequent) {
			x, (Subset|SubsetEq)(SetExtension(eList(_*,x,_*)), S) -> {
				return in(`x, `S, child);
			}
		}
		throw new IllegalArgumentException("Can't extract set extension member "
				+ member + " from "	+ child);
	}

	public Rule<RelationalPredicate> relToCprod(Rule<?> child) {
		final Predicate childConsequent = child.getConsequent();
		%match (childConsequent) {
			In(x, (Rel|Trel|Srel|Strel
				  |Pfun|Tfun|Pinj|Tinj|Psur|Tsur|Tbij)(A, B)) -> {
				return subseteq(`x, cprod(`A, `B), child);
			}
		}
		throw new IllegalArgumentException("Can't find a relational set in: "
				+ child);
	}

	public Rule<RelationalPredicate> eqToSubset(boolean leftToRight,
			Rule<?> child) {
		final Predicate childConsequent = child.getConsequent();
		%match (childConsequent) {
			Equal(A, B) -> {
				assert `A.getType().getBaseType() != null;
				if (leftToRight) {
					return subseteq(`A, `B, child);
				} else {
					return subseteq(`B, `A, child);
				}
			}
		}
		throw new IllegalArgumentException("Can't find set equality in: "
				+ child);
	}

}
