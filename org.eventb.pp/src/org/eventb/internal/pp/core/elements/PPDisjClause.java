package org.eventb.internal.pp.core.elements;

import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;

public class PPDisjClause extends AbstractPPClause {

//	@Deprecated
//	public PPDisjClause(int level, List<IPredicate> predicates, List<ILiteral> others) {
//		super(level, predicates, others);
//	}

	public PPDisjClause(Level level, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic, List<IEquality> conditions) {
		super(level, predicates, equalities, arithmetic, conditions);
		
		assert predicates != null && equalities != null && arithmetic != null && conditions != null;
	}
	
	public PPDisjClause(Level level, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		super(level, predicates, equalities, arithmetic);
		
		assert predicates != null && equalities != null && arithmetic != null;
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
		return simplifier.simplifyDisjunctiveClause(this);
	}

	public void infer(IInferrer inferrer) {
		inferrer.inferFromDisjunctiveClause(this);
	}


}