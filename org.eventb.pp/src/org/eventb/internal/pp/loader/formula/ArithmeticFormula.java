/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral.AType;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class represents a signature for an arithmetic literal. ArithmeticFormula
 * literals are literals of the form AE op AE where op is &le;,&lt;,&ge;,&gt;
 *
 * @author François Terrier
 *
 */
public class ArithmeticFormula extends AbstractSingleFormula<ArithmeticDescriptor> {

	public enum Type {LESS_EQUAL, LESS, EQUAL}
	
	private List<TermSignature> definingTerms;
	
	private Type type;
	
	public ArithmeticFormula(Type type, List<TermSignature> terms, List<TermSignature> definingTerms, ArithmeticDescriptor descriptor) {
		super(terms,descriptor);
		assert definingTerms.size() == 2;
		
		this.definingTerms = definingTerms;
		this.type = type;
	}

	public Type getType() {
		return type;
	}
	
	private List<TermSignature> transform(List<TermSignature> termList) {
		List<TermSignature> result = new ArrayList<TermSignature>();
		List<TermSignature> copy = new ArrayList<TermSignature>(termList.size());
		copy.addAll(termList);
		for (TermSignature sig : definingTerms) {
			sig.appendTermFromTermList(copy, result, -1, -1);
		}
		assert copy.isEmpty();
		return result;
	}

	@Override
	Literal<?,?> getLabelPredicate(List<TermSignature> termList, ClauseContext context) {
		List<TermSignature> newTerms = transform(termList);
		List<Term> terms = getTermsFromTermSignature(newTerms, context);
		// normalize terms here
		Term left = terms.get(0);
		Term right = terms.get(1);
		if (type == Type.EQUAL) {
			if (left instanceof SimpleTerm && right instanceof SimpleTerm) {
				return new EqualityLiteral((SimpleTerm)left,(SimpleTerm)right,context.isPositive());
			}
			else {
				return new ArithmeticLiteral(left,right,context.isPositive()?AType.EQUAL:AType.UNEQUAL);
			}
		}
		if (context.isPositive()) {
			return new ArithmeticLiteral(left,right,type == Type.LESS?AType.LESS:AType.LESS_EQUAL);
		} else {
			left = terms.get(1);
			right = terms.get(0);
			return new ArithmeticLiteral(left,right,type == Type.LESS?AType.LESS_EQUAL:AType.LESS);
		}
	}
	

	@Override
	void split() {
		return;
	}

}
