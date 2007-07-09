package org.eventb.internal.pp.core.elements;

import java.util.List;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public final class DisjunctiveClause extends Clause {

	private static final int BASE_HASHCODE = 3;
	
//	@Deprecated
//	public DisjunctiveClause(int level, List<PredicateFormula> predicates, List<Literal> others) {
//		super(level, predicates, others);
//	}

	public DisjunctiveClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic, List<EqualityLiteral> conditions) {
		super(origin, predicates, equalities, arithmetic, conditions, BASE_HASHCODE);
		
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
		assert !isEmpty();
	}
	
	public DisjunctiveClause(IOrigin origin, List<PredicateLiteral> predicates, List<EqualityLiteral> equalities, List<ArithmeticLiteral> arithmetic) {
		super(origin, predicates, equalities, arithmetic, BASE_HASHCODE);
		
		assert predicates != null && equalities != null && arithmetic != null;
		assert !isEmpty();
	}
	
	@Override
	public String toString() {
		return "D"+super.toString();
	}
	
	@Override
	protected void computeBitSets() {
		for (PredicateLiteral literal : predicates) {
			if (literal.getDescriptor().isPositive()) {
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
	
//	public boolean contains(PredicateDescriptor predicate) {
//		return hasPredicateOfSign(predicate, false);
//	}

	@Override
	public boolean matches(PredicateDescriptor predicate) {
		return hasPredicateOfSign(predicate, true);
	}
	
	@Override
	public boolean matchesAtPosition(PredicateDescriptor predicate, int position) {
		PredicateDescriptor matched = predicates.get(position).getDescriptor();
		return predicate.getIndex() == matched.getIndex() 
			&& predicate.isPositive() == !matched.isPositive();
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