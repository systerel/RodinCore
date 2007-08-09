/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.formula.terms;

import java.util.List;

import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.loader.formula.ClauseContext;

/**
 * This class represents a variable signature.
 * 
 * @author Francois Terrier
 *
 */
public class VariableSignature extends TermSignature {

	private int uniqueIndex;
	private int index;
	
	public VariableSignature(int uniqueIndex, int index, Sort sort) {
		super(sort);
		
		this.index = index;
		this.uniqueIndex = uniqueIndex;
	}
	
	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset, List<TermSignature> termList) {
		if (!isQuantified(startOffset, endOffset)) {
			addTermCopy(this, termList);
			return new VariableHolder(sort);
		}
		return new VariableSignature(uniqueIndex, index, sort);
	}

	@Override
	public boolean isQuantified(int startOffset, int endOffset) {
		if (index >= startOffset && index <= endOffset) {
			return true;
		}
		return false;
	}

	@Override
	public TermSignature deepCopy() {
		return new VariableSignature(uniqueIndex, index, sort);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof VariableSignature) {
			VariableSignature temp = (VariableSignature) obj;
			return index == temp.index;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public boolean isConstant() {
		return false;
	}
	
	@Override
	public String toString() {
		return ""+uniqueIndex+","+index+"("+sort+")";
	}

	/* 
	 * This method might not be called, there are no variables, only variable holder.
	 * 
	 * (non-Javadoc)
	 * @see org.eventb.pptrans.loader.signature.TermSignature#appendTermFromTermsList(java.util.List, java.util.List, boolean)
	 */
	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		assert isQuantified(startOffset, endOffset);

		newList.add(this.deepCopy());
	}
	
	@Override
	public Term getTerm(ClauseContext context) {
		Term var;
		if (!context.isQuantified() || !isQuantified(context.getStartOffset(), context.getEndOffset())) {
			var = context.getVariableTable().getVariable(uniqueIndex, sort);
		}
		else if (context.isEquivalence()) {
			var = context.getVariableTable().getLocalVariable(uniqueIndex, context.isForall(), sort);
		}
		else {
			if (context.isForall()) {
				var = context.getVariableTable().getVariable(uniqueIndex, sort);
			} else {
				var = context.getVariableTable().getLocalVariable(uniqueIndex, false, sort);
			}
		}
		return var;
	}

	@Override
	public TermSignature getSimpleTerm(List<TermSignature> termList) {
		addTermCopy(this, termList);
		return new VariableHolder(sort);
	}

}
