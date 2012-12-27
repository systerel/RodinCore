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
 * Class for variable holders. A variable holder is a place where
 * a variable can be inserted. Used for decomposing quantified sub-formulas
 * and arithmetic terms.
 *
 * @author François Terrier
 *
 */
public class VariableHolder extends TermSignature {

	public VariableHolder(Sort sort) {
		super(sort);
	}
	
	@Override
	public TermSignature deepCopy() {
		assert false;
		return null;
	}

	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset, List<TermSignature> termList) {
		assert false;
		return null;
	}

	@Override
	public boolean isConstant() {
		assert false;
		return false;
	}

	@Override
	public boolean isQuantified(int startOffset, int endOffset) {
		assert false;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof VariableHolder) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ("{&}").hashCode();
	}
	
	@Override
	public String toString() {
		return "{&}("+sort+")";
	}

	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		TermSignature term = indexList.remove(0);
		newList.add(term);
	}

	
	@Override
	public Term getTerm(ClauseContext context) {
		assert false;
		return null;
	}

	@Override
	public TermSignature getSimpleTerm(List<TermSignature> termList) {
		assert false;
		return null;
	}
}
