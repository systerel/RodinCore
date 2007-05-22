package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class PPProposition extends AbstractPPPredicate {

	public PPProposition(int index, boolean isPositive) {
		super(index, isPositive, new ArrayList<Term>());
	}

	public IPredicate getInverse() {
		return new PPProposition(index, !isPositive);
	}

	public ILiteralDescriptor getDescriptor() {
		return new PropositionDescriptor(index);
	}
	
	public IPredicate substitute(Map<AbstractVariable, ? extends Term> map) {
		return this;
	}
	
	public List<IEquality> getConditions(IPredicate predicate) {
		return new ArrayList<IEquality>();
	}

	@Override
	public IPredicate getCopyWithNewVariables(IVariableContext context, HashMap<AbstractVariable, AbstractVariable> substitutionsMap) {
		return this;
	}

	@Override
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPProposition) {
			return super.equalsWithDifferentVariables(literal, map);
		}
		return false;
	}

	public boolean updateInstantiationCount(IPredicate predicate) {
		// do nothing
		return false;
	}

	public void resetInstantiationCount() {
		//do nothing
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof PPProposition) {
			PPProposition temp = (PPProposition) obj;
			return super.equals(temp);
		}
		return false;
	}
	
	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(isPositive?"":"¬");
		str.append("R" + index);
		return str.toString();
	}
	
}
