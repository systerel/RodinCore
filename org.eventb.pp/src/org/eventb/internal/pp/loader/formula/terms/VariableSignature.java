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

	// Index associated to this variable occurrence in the given context (unique
	// in that context)
	private final int varIndex;

	// Complement of the original index in the input formula (i.e., de Bruijn
	// index).
	// This index is computed by traversing the formula from the root down,
	// while the de Bruijn index is computed from a leaf up.
	private final int revBoundIndex;

	public VariableSignature(int varIndex, int revBoundIndex, Sort sort) {
		super(sort);

		this.revBoundIndex = revBoundIndex;
		this.varIndex = varIndex;
	}

	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset,
			List<TermSignature> termList) {
		if (!isQuantified(startOffset, endOffset)) {
			addTermCopy(this, termList);
			return new VariableHolder(sort);
		}
		return new VariableSignature(varIndex, revBoundIndex, sort);
	}

	@Override
	public boolean isQuantified(int startOffset, int endOffset) {
		if (revBoundIndex >= startOffset && revBoundIndex <= endOffset) {
			return true;
		}
		return false;
	}

	@Override
	public TermSignature deepCopy() {
		return new VariableSignature(varIndex, revBoundIndex, sort);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof VariableSignature))
			return false;
		final VariableSignature other = (VariableSignature) obj;
		return this.revBoundIndex == other.revBoundIndex;
	}

	@Override
	public int hashCode() {
		return revBoundIndex;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public String toString() {
		return varIndex + "," + revBoundIndex + "(" + sort + ")";
	}

	/*
	 * This method might not be called, there are no variables, only variable
	 * holder.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.pptrans.loader.signature.TermSignature#appendTermFromTermsList(java.util.List,
	 *      java.util.List, boolean)
	 */
	@Override
	public void appendTermFromTermList(List<TermSignature> indexList,
			List<TermSignature> newList, int startOffset, int endOffset) {
		assert isQuantified(startOffset, endOffset);

		newList.add(this.deepCopy());
	}

	@Override
	public Term getTerm(ClauseContext context) {
		Term var;
		if (!context.isQuantified()
				|| !isQuantified(context.getStartOffset(), context
						.getEndOffset())) {
			var = context.getVariableTable().getVariable(varIndex, sort);
		} else if (context.isEquivalence()) {
			var = context.getVariableTable().getLocalVariable(varIndex,
					context.isForall(), sort);
		} else {
			if (context.isForall()) {
				var = context.getVariableTable().getVariable(varIndex, sort);
			} else {
				var = context.getVariableTable().getLocalVariable(varIndex,
						false, sort);
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
