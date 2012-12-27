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

public abstract class AbstractConstantSignature extends TermSignature {

	public AbstractConstantSignature(Sort sort) {
		super(sort);
	}

	@Override
	public TermSignature getUnquantifiedTerm(int startOffset, int endOffset, List<TermSignature> termList) {
		addTermCopy(this, termList);
		return new VariableHolder(sort);
	}

	@Override
	public boolean isQuantified(int startOffset, int endOffset) {
		return false;
	}

	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public abstract Term getTerm(ClauseContext context);

	@Override
	public void appendTermFromTermList(List<TermSignature> indexList, List<TermSignature> newList, int startOffset, int endOffset) {
		newList.add(this.deepCopy());
	}

	@Override
	public TermSignature getSimpleTerm(List<TermSignature> termList) {
		addTermCopy(this, termList);
		return new VariableHolder(sort);
	}

}