package org.eventb.internal.pp.core.elements;

import java.util.ArrayList;
import java.util.Arrays;
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

public class PPArithmetic implements IArithmetic {

	private Term left;
	private Term right;
	private AType type;
	
	// TODO get rid of this when normalizing
	public enum AType {LESS, LESS_EQUAL, EQUAL, UNEQUAL} 
	
	public PPArithmetic(Term left, Term right, AType type) {
		this.left = left;
		this.right = right;
		this.type = type;
	}
	
	public List<Term> getTerms() {
		return Arrays.asList(new Term[]{left,right});
	}

	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(left.toString(variableMap));
		switch(type){
			case LESS:
				str.append("<");
				break;
			case LESS_EQUAL:
				str.append("≤");
				break;
			case EQUAL:
				str.append("=");
				break;
			case UNEQUAL:
				str.append("≠");
				break;
		}
		str.append(right.toString(variableMap));
		return str.toString();	
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPArithmetic) {
			PPArithmetic temp = (PPArithmetic) obj;
			return left.equals(temp.left) && right.equals(temp.right) && type == temp.type;
		}
		return false;
	}

	public IArithmetic substitute(Map<AbstractVariable, ? extends Term> map) {
		Term newleft = left.substitute(map);
		Term newright = right.substitute(map);
		return new PPArithmetic(newleft, newright, type);
	}

	public boolean isQuantified() {
		return left.isQuantified() || right.isQuantified();
	}

	public boolean isConstant() {
		return left.isConstant() && right.isConstant();
	}

	public boolean equalsWithDifferentVariables(IArithmetic literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPArithmetic) {
			PPArithmetic temp = (PPArithmetic) literal;
			if (type != temp.type) return false;
			else {
				HashMap<AbstractVariable, AbstractVariable> copy = new HashMap<AbstractVariable, AbstractVariable>(map);
				if (left.equalsWithDifferentVariables(temp.left, copy)
				 && right.equalsWithDifferentVariables(temp.right, copy))
					return true;
			}
		}
		return false;
	}

	public IArithmetic getCopyWithNewVariables(IVariableContext context, HashMap<AbstractVariable, AbstractVariable> substitutionsMap) {
		Set<Variable> variables = new HashSet<Variable>();
		List<LocalVariable> localVariables = new ArrayList<LocalVariable>();
		left.collectVariables(variables);
		left.collectLocalVariables(localVariables);
		right.collectVariables(variables);
		right.collectLocalVariables(localVariables);
			
		for (Variable variable : variables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, context.getNextVariable(variable.getSort()));
		}
		for (LocalVariable variable : localVariables) {
			if (!substitutionsMap.containsKey(variable)) substitutionsMap.put(variable, new LocalVariable(context.getNextLocalVariableID(),variable.isForall(),variable.getSort()));
		}
		return substitute(substitutionsMap);
	}


	public IArithmetic getInverse() {
		// TODO Auto-generated method stub
		return null;
	}

	public int hashCodeWithDifferentVariables() {
		return (31 * left.hashCodeWithDifferentVariables() + right.hashCodeWithDifferentVariables())*2 + type.hashCode();
	}

}
