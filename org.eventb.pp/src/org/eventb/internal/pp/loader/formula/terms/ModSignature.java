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

import org.eventb.internal.pp.core.elements.terms.Mod;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.clause.VariableTable;
import org.eventb.internal.pp.loader.formula.TermVisitorContext;

public class ModSignature extends BinaryTermSignature {

	public ModSignature(TermSignature left, TermSignature right) {
		super(left, right);
	}
	
	protected ModSignature(List<TermSignature> terms) {
		super(terms);
		assert terms.size() == 2;
	}

	@Override
	public TermSignature deepCopy() {
		return new ModSignature(deepCopyHelper());
	}

	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset, List<TermSignature> termList) {
		if (!isQuantified(startOffset, endOffset)) {
			addTerm(this.deepCopy(), termList);
			return new VariableHolder(sort);
		}
		else {
			return new ModSignature(super.getUnquantifiedSignatureHelper(startOffset, endOffset, termList));
		}	
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ModSignature) {
			ModSignature temp = (ModSignature) obj;
			return super.equals(temp);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() * 31 + ("[O]").hashCode();
	}

	@Override
	public String toString() {
		return "mod " + super.toString();
	}
	
	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		List<TermSignature> terms = new ArrayList<TermSignature>();
		super.appendTermFromTermList(indexList, terms, startOffset, endOffset);
		TermSignature term = new ModSignature(terms);
		newList.add(term);
	}
	
	@Override
	public Term getTerm(VariableTable table, TermVisitorContext context) {
		return new Mod(super.getTermHelper(table, context));
	}
}
