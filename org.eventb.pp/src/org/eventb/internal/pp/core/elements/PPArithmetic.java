package org.eventb.internal.pp.core.elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class PPArithmetic extends AbstractPPLiteral<IArithmetic> implements IArithmetic {

	final private AType type;
	
	// TODO get rid of this when normalizing
	public enum AType {LESS, LESS_EQUAL, EQUAL, UNEQUAL} 
	
	public PPArithmetic(Term left, Term right, AType type) {
		super(Arrays.asList(new Term[]{left,right}));
		this.type = type;
	}
	
	private PPArithmetic(List<Term> terms, AType type) {
		super(terms);
		this.type = type;
	}
	
	public Term getLeft() {
		return terms.get(0);
	}
	
	public Term getRight() {
		return terms.get(1);
	}

	@Override
	public String toString(HashMap<Variable, String> variableMap) {
		StringBuffer str = new StringBuffer();
		str.append(getLeft().toString(variableMap));
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
		str.append(getRight().toString(variableMap));
		return str.toString();	
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PPArithmetic) {
			PPArithmetic temp = (PPArithmetic) obj;
			return type == temp.type && super.equals(obj);
		}
		return false;
	}

	public IArithmetic substitute(Map<AbstractVariable, ? extends Term> map) {
		return new PPArithmetic(substituteHelper(map), type);
	}

	@Override
	public boolean equalsWithDifferentVariables(ILiteral<?> literal, HashMap<AbstractVariable, AbstractVariable> map) {
		if (literal instanceof PPArithmetic) {
			PPArithmetic temp = (PPArithmetic) literal;
			if (type != temp.type) return false;
			else {
				return super.equalsWithDifferentVariables(literal, map);
			}
		}
		return false;
	}


	public IArithmetic getInverse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCodeWithDifferentVariables() {
		return super.hashCodeWithDifferentVariables() + type.hashCode();
	}

}
