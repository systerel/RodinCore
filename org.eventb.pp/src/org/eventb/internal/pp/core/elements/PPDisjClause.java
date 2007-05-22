package org.eventb.internal.pp.core.elements;

import java.util.List;

import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;
import org.eventb.internal.pp.core.tracing.IOrigin;

public final class PPDisjClause extends AbstractPPClause {

//	@Deprecated
//	public PPDisjClause(int level, List<IPredicate> predicates, List<ILiteral> others) {
//		super(level, predicates, others);
//	}

	public PPDisjClause(IOrigin origin, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic, List<IEquality> conditions) {
		super(origin, predicates, equalities, arithmetic, conditions);
		
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
		assert !isEmpty();
	}
	
	public PPDisjClause(IOrigin origin, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		super(origin, predicates, equalities, arithmetic);
		
		assert predicates != null && equalities != null && arithmetic != null;
		assert !isEmpty();
	}
	
	@Override
	public String toString() {
		return "D"+super.toString();
	}
	
	@Override
	protected void computeBitSets() {
		for (IPredicate literal : predicates) {
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
		if (obj instanceof PPDisjClause) {
			return super.equals(obj);
		}
		return false;
	}

	public IClause simplify(ISimplifier simplifier) {
		IClause result = simplifier.simplifyDisjunctiveClause(this);
		assert result != null;
		return result;
	}

	public void infer(IInferrer inferrer) {
		inferrer.inferFromDisjunctiveClause(this);
	}

	public boolean isFalse() {
		return false;
	}

	public boolean isTrue() {
		return false;
	}

	public boolean isEquivalence() {
		return false;
	}


}