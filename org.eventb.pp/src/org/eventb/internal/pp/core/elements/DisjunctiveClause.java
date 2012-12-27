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
 * Concrete implementation of {@link Clause} for disjunctive clauses.
 * <p>
 * Disjunctive clauses must not be empty.
 * 
 * @author François Terrier
 *
 */
public final class DisjunctiveClause extends Clause {

	private static final int BASE_HASHCODE = 3;
	
	DisjunctiveClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions) {
		super(origin, predicates, equalities, arithmetic, conditions, BASE_HASHCODE);
		
		assert predicates.size() + equalities.size() + arithmetic.size() + conditions.size() >=1;
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
	}
	
	DisjunctiveClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic) {
		super(origin, predicates, equalities, arithmetic, BASE_HASHCODE);
		
		assert predicates.size() + equalities.size() + arithmetic.size() + conditions.size() >=1;
		assert predicates != null && equalities != null && arithmetic != null;
	}
	
	@Override
	public String toString() {
		return "D"+super.toString();
	}
	
	@Override
	protected void computeBitSets() {
		for (PredicateLiteral literal : predicates) {
			if (literal.isPositive()) {
				literal.setBit(positiveLiterals);
			}
			else {
				literal.setBit(negativeLiterals);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DisjunctiveClause) {
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public Clause simplify(ISimplifier simplifier) {
		Clause result = simplifier.simplifyDisjunctiveClause(this);
		assert result != null;
		return result;
	}

	@Override
	public void infer(IInferrer inferrer) {
		inferrer.inferFromDisjunctiveClause(this);
	}
	
	@Override
	public boolean matches(PredicateLiteralDescriptor predicate, boolean isPositive) {
		return hasPredicateOfSign(predicate, !isPositive);
	}
	
	@Override
	public boolean matchesAtPosition(PredicateLiteralDescriptor predicate, boolean isPositive, int position) {
		PredicateLiteral matched = predicates.get(position);
		return predicate.equals(matched.getDescriptor())
			&& isPositive == !matched.isPositive();
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
		return false;
	}

}