/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.elements;

import java.util.List;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

/**
 * Concrete implementation of {@link Clause} for equivalence clauses.
 * <p>
 * Equivalence clauses must not be empty. Beside, they must contain at
 * least two literals that are not conditions.
 * 
 * @author François Terrier
 *
 */
public final class EquivalenceClause extends Clause {
	
	private static final int BASE_HASHCODE = 5;
	
	EquivalenceClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic) {
		super(origin, predicates, equalities, arithmetic, BASE_HASHCODE);
		
		// not a unit clause. unit clauses are disjunctive clauses
		assert !isUnit();
		assert predicates.size() + equalities.size() + arithmetic.size() >=2;
		assert predicates != null && equalities != null && arithmetic != null;
	}

	EquivalenceClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions) {
		super(origin, predicates, equalities, arithmetic, conditions, BASE_HASHCODE);
		
		// not a unit clause. unit clauses are disjunctive clauses
		assert !isUnit();
		assert predicates.size() + equalities.size() + arithmetic.size() >=2;
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
	}
	
	
	@Override
	protected void computeBitSets() {
		for (PredicateLiteral literal : predicates) {
			literal.setBit(positiveLiterals);
			literal.setBit(negativeLiterals);
		}
	}
	
	@Override
	public void infer(IInferrer inferrer) {
		inferrer.inferFromEquivalenceClause(this);
	}
	
	@Override
	public Clause simplify(ISimplifier simplifier) {
		Clause result = simplifier.simplifyEquivalenceClause(this);
		assert result != null;
		return result;
	}
	
	@Override
	public String toString() {
		return "E"+super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EquivalenceClause) {
			return super.equals(obj);
		}
		return false;
	}
	
	public static void inverseOneliteral(List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic) {
		// we must inverse one sign
		if (predicates.size() > 0) {
			PredicateLiteral toInverse = predicates.remove(0);
			predicates.add(0, toInverse.getInverse());
		}
		else if (equalities.size() > 0) {
			EqualityLiteral toInverse = equalities.remove(0);
			equalities.add(0, toInverse.getInverse());
		}
		else if (arithmetic.size() > 0) {
			ArithmeticLiteral toInverse = arithmetic.remove(0);
			arithmetic.add(0, toInverse.getInverse());
		}
	}

	@Override
	public boolean matches(PredicateLiteralDescriptor predicate, boolean isPositive) {
		return hasPredicateOfSign(predicate, isPositive) || hasPredicateOfSign(predicate, !isPositive);
	}
	
	@Override
	public boolean matchesAtPosition(PredicateLiteralDescriptor predicate, boolean isPositive, int position) {
		PredicateLiteral matched = predicates.get(position);
		return predicate.equals(matched.getDescriptor());
	}

	@Override
	public boolean isFalse() {
		return false;
	}

	@Override
	public boolean isTrue() {
		return false;
	}

	@Override
	public boolean isEquivalence() {
		return true;
	}

}
