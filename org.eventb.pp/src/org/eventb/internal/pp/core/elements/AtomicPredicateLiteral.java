package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;

public final class AtomicPredicateLiteral extends PredicateLiteral {

	public AtomicPredicateLiteral(PredicateDescriptor descriptor) {
		super(descriptor, new ArrayList<SimpleTerm>());
	}

	@Override
	public AtomicPredicateLiteral getInverse() {
		return new AtomicPredicateLiteral(descriptor.getInverse());
	}

	@Override
	public AtomicPredicateLiteral substitute(Map<SimpleTerm, SimpleTerm> map) {
		return this;
	}
	
	public List<EqualityLiteral> getConditions(AtomicPredicateLiteral predicate) {
		return new ArrayList<EqualityLiteral>();
	}

	@Override
	public AtomicPredicateLiteral getCopyWithNewVariables(IVariableContext context, HashMap<SimpleTerm, SimpleTerm> substitutionsMap) {
		return this;
	}

	@Override
	public boolean equalsWithDifferentVariables(PredicateLiteral literal, HashMap<SimpleTerm, SimpleTerm> map) {
		return literal instanceof AtomicPredicateLiteral && super.equalsWithDifferentVariables(literal, map);
	}

	public void resetInstantiationCount() {
		//do nothing
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof AtomicPredicateLiteral) {
			AtomicPredicateLiteral temp = (AtomicPredicateLiteral) obj;
			return super.equals(temp);
		}
		return false;
	}
	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(descriptor.isPositive()?"":"¬");
		str.append("R" + descriptor.getIndex());
		return str.toString();
	}
	
}
