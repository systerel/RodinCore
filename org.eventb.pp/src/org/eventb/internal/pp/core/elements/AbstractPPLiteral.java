package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public abstract class AbstractPPLiteral<T extends ILiteral<T>> implements ILiteral<T> {

	final protected List<Term> terms;
	
	public AbstractPPLiteral(List<Term> terms) {
		this.terms = terms;
	}
	
	public List<Term> getTerms() {
		return terms;
	}
	
	public boolean isQuantified() {
		for (Term term : terms) {
			if (term.isQuantified()) return true;
		}
		return false;
	}

	public boolean isConstant() {
		for (Term term : terms) {
			if (!term.isConstant()) return false;
		}
		return true;
	}
	
	public boolean isForall() {
		for (Term term : terms) {
			if (term.isForall()) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return terms.hashCode();
	}
	
	public int hashCodeWithDifferentVariables() {
		int hashCode = 1;
		for (Term term : terms) {
			hashCode = 31*hashCode + term.hashCodeWithDifferentVariables();
		}
		return hashCode;
	}
	
	public T getCopyWithNewVariables(IVariableContext context, 
			HashMap<AbstractVariable, AbstractVariable> substitutionsMap) {
		Set<Variable> variables = new HashSet<Variable>();
		List<LocalVariable> localVariables = new ArrayList<LocalVariable>();
		for (Term term : terms) {
			term.collectVariables(variables);
			term.collectLocalVariables(localVariables);
		}
		for (Variable variable : variables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, context.getNextVariable(variable.getSort()));
		}
		for (LocalVariable variable : localVariables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, new LocalVariable(context.getNextLocalVariableID(),variable.isForall(),variable.getSort()));
		}
		return substitute(substitutionsMap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractPPLiteral) {
			AbstractPPLiteral<?> temp = (AbstractPPLiteral)obj;
			return terms.equals(temp.terms);
		}
		return false;
	}
	
	
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof AbstractPPLiteral<?>) {
			AbstractPPLiteral<?> temp = (AbstractPPLiteral) literal;
			if (terms.size() != temp.terms.size()) return false;
			else {
				for (int i = 0; i < terms.size(); i++) {
					Term term1 = terms.get(i);
					Term term2 = temp.terms.get(i);
					if (!term1.equalsWithDifferentVariables(term2, map)) return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public List<Term> substituteHelper(Map<AbstractVariable, ? extends Term> map) {
		List<Term> result = new ArrayList<Term>();
		for (Term child : terms) {
			result.add(child.substitute(map));
		}
		return result;
	}
	
	public List<Term> getInverseHelper() {
		List<Term> result = new ArrayList<Term>();
		for (Term term : terms) {
			result.add(term.getInverse());
		}
		return result;
	}
	
	@Override
	public String toString() {
		return toString(new HashMap<Variable, String>());
	}
	
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append("[");
		for (Term term : terms) {
			str.append(term.toString(variableMap));
			str.append(",");
		}
		str.deleteCharAt(str.length()-1);
		str.append("]");
		return str.toString();
	}
}
