/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class represents an intermediate result for formulas containing more
 * than one literal. It consists of a list of {@link IIntermediateResult}.
 *
 * @author François Terrier
 *
 */
public class IntermediateResultList implements IIntermediateResult {

	private List<IIntermediateResult> list;
	
	public IntermediateResultList(List<IIntermediateResult> list) {
		this.list = list;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.pptrans.loader.IIntermediateResult#getTerms()
	 */
	@Override
	public List<TermSignature> getTerms() {
		List<TermSignature> indexes = new ArrayList<TermSignature>();
		for (IIntermediateResult res : list) {
			indexes.addAll(res.getTerms());
		}
		return indexes;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntermediateResultList) {
			IntermediateResultList temp = (IntermediateResultList) obj;
			return list.equals(temp.list);
		}
		return false;
	}

	@Override
	public List<IIntermediateResult> getResultList() {
		return list;
	}

	@Override
	public String toString() {
		return list.toString();
	}

}
