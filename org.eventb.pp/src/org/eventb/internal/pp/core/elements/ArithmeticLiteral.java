package org.eventb.internal.pp.core.elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

public class ArithmeticLiteral extends Literal<ArithmeticLiteral,Term> {
	final private AType type;
	
	// TODO get rid of this when normalizing
	public enum AType {LESS, LESS_EQUAL, EQUAL, UNEQUAL} 
	
	public ArithmeticLiteral(Term left, Term right, AType type) {
		super(Arrays.asList(new Term[]{left,right}));
		this.type = type;
	}
	
	private ArithmeticLiteral(List<Term> terms, AType type) {
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
		if (obj instanceof ArithmeticLiteral) {
			ArithmeticLiteral temp = (ArithmeticLiteral) obj;
			return type == temp.type && super.equals(obj);
		}
		return false;
	}

	@Override
	public ArithmeticLiteral substitute(Map<SimpleTerm, SimpleTerm> map) {
		return new ArithmeticLiteral(substituteHelper(map,terms), type);
	}

	@Override
	public boolean equalsWithDifferentVariables(ArithmeticLiteral literal, HashMap<SimpleTerm, SimpleTerm> map) {
		if (type != literal.type) return false;
		else {
			return super.equalsWithDifferentVariables(literal, map);
		}
	}

	@Override
	public ArithmeticLiteral getInverse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCodeWithDifferentVariables() {
		return super.hashCodeWithDifferentVariables() + type.hashCode();
	}

}
