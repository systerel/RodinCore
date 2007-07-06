/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.terms;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.terms.Divide;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.TermVisitorContext;


/**
 * This class represents the signature for the divide operation.
 *
 * @author François Terrier
 *
 */
public class DivideSignature extends BinaryTermSignature {

	public DivideSignature(TermSignature left, TermSignature right) {
		super(left, right);
	}
	
	protected DivideSignature(List<TermSignature> terms) {
		super(terms);
		assert terms.size() == 2;
	}

	@Override
	public TermSignature deepCopy() {
		return new DivideSignature(deepCopyHelper());
	}

	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset, List<TermSignature> termList) {
		if (isQuantified(startOffset,endOffset)) {
			return new DivideSignature(super.getUnquantifiedTermHelper(startOffset, endOffset, termList));
		} else {
			addTermCopy(this, termList);
			return new VariableHolder(sort);
		}
	}
	

	@Override
	public TermSignature getSimpleTerm(List<TermSignature> termList) {
		return new DivideSignature(super.getSimpleTermHelper(termList));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof DivideSignature) {
			DivideSignature temp = (DivideSignature) obj;
			return super.equals(temp);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 31 + ("[D]").hashCode();
	}

	@Override
	public String toString() {
		return "÷ " + super.toString();
	}
	
	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		List<TermSignature> terms = new ArrayList<TermSignature>();
		super.appendTermFromTermList(indexList, terms, startOffset, endOffset);
		TermSignature term = new DivideSignature(terms);
		newList.add(term);
	}

	@Override
	public Term getTerm(VariableTable table, TermVisitorContext context) {
		return new Divide(super.getTermHelper(table, context));
	}

}
