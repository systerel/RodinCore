/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula;

import java.util.List;

import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.PPArithmetic;
import org.eventb.internal.pp.core.elements.PPArithmetic.AType;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.BooleanEqualityTable;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.descriptor.ArithmeticDescriptor;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class represents a signature for an arithmetic literal. Arithmetic
 * literals are literals of the form AE op AE where op is &le;,&lt;,&ge;,&gt;
 *
 * @author François Terrier
 *
 */
public class ArithmeticLiteral extends AbstractSingleFormula<ArithmeticDescriptor> {

	public enum Type {LESS_EQUAL, LESS, EQUAL};
	
	private Type type;
	
	public ArithmeticLiteral(Type type, List<TermSignature> terms, ArithmeticDescriptor descriptor) {
		super(terms,descriptor);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public ILiteral<?> getLiteral(List<TermSignature> termList, TermVisitorContext flags, VariableTable table, BooleanEqualityTable bool) {
		assert termList.size() == 2;
		List<Term> terms = getTermsFromTermSignature(termList, flags, table);
		// normalize terms here
		Term left = terms.get(0);
		Term right = terms.get(1);
		if (type == Type.EQUAL) {
			return new PPArithmetic(left,right,flags.isPositive?AType.EQUAL:AType.UNEQUAL);
		}
		if (flags.isPositive) {
			return new PPArithmetic(left,right,type == Type.LESS?AType.LESS:AType.LESS_EQUAL);
		} else {
			left = terms.get(1);
			right = terms.get(0);
			return new PPArithmetic(left,right,type == Type.LESS?AType.LESS_EQUAL:AType.LESS);
		}
	}

	public boolean hasEquivalenceFirst() {
		return false;
	}

}
