/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;

/**
 * This class represents an arithmetic literal.
 * <p>
 * TODO for now, there is no normalization for arithmetic literals. This means 
 * that we have the four possible types &lt;,≤,=,≠. Think about a nice way to normalize
 * arithmetic literals.
 *
 * @author François Terrier
 *
 */
public final class ArithmeticLiteral extends Literal<ArithmeticLiteral,Term> {
	
	private static final int BASE_HASHCODE = 17;
	
	final private AType type;
	
	// TODO get rid of this when normalizing
	public static enum AType {LESS, LESS_EQUAL, EQUAL, UNEQUAL} 
	
	public ArithmeticLiteral(Term left, Term right, AType type) {
		super(Arrays.asList(new Term[]{left,right}), BASE_HASHCODE);
		this.type = type;
	}
	
	private ArithmeticLiteral(List<Term> terms, AType type) {
		super(terms, BASE_HASHCODE);
		this.type = type;
	}
	
	public Term getLeft() {
		return terms.get(0);
	}
	
	public Term getRight() {
		return terms.get(1);
	}

	public AType getType() {
		return type;
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
		boolean inverse = false;
		AType newtype = null;
		switch (this.type) {
		case EQUAL:
			newtype = AType.UNEQUAL;
			break;
		case UNEQUAL:
			newtype = AType.EQUAL;
			break;
		case LESS:
			newtype = AType.LESS_EQUAL;
			inverse = true;
			break;
		case LESS_EQUAL:
			newtype = AType.LESS;
			inverse = true;
			break;
		default:
			assert false;
			break;
		}
		List<Term> newterms = getInverseHelper(terms);
		if (inverse) return new ArithmeticLiteral(newterms.get(1),newterms.get(0),newtype);
		else return new ArithmeticLiteral(newterms, newtype);
	}

}
