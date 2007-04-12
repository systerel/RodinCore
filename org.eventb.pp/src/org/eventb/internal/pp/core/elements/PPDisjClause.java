package org.eventb.internal.pp.core.elements;

import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.inferrers.IInferrer;
import org.eventb.internal.pp.core.simplifiers.ISimplifier;

public class PPDisjClause extends AbstractPPClause {

//	@Deprecated
//	public PPDisjClause(int level, List<IPredicate> predicates, List<ILiteral> others) {
//		super(level, predicates, others);
//	}

	public PPDisjClause(Level level, List<IPredicate> predicates, List<IEquality> equalities, List<IArithmetic> arithmetic) {
		super(level, predicates, equalities, arithmetic);
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
			PPDisjClause tmp = (PPDisjClause) obj;
			HashMap<AbstractVariable, AbstractVariable> map = new HashMap<AbstractVariable, AbstractVariable>();
			return super.equalsWithDifferentVariables(tmp,map);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCodeWithDifferentVariables();
	}

	public IClause simplify(ISimplifier simplifier) {
		return simplifier.simplifyDisjunctiveClause(this);
	}

	public void infer(IInferrer inferrer) {
		inferrer.inferFromDisjunctiveClause(this);
	}


}