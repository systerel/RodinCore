/*******************************************************************************
 * Copyright (c) 2006, 2007 ETH Zurich.
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
 * This class represents an intermediate result for a single literal.
 * See {@link IntermediateResultList} for the class representing a formula, i.e.
 * a list of intermediate result.
 * 
 * @see IntermediateResultList
 * 
 * @author François Terrier
 */
public class IntermediateResult implements IIntermediateResult {

	private final List<TermSignature> results;
	
	public IntermediateResult() {
		this.results  = new ArrayList<TermSignature>();
	}
	
	public IntermediateResult(List<TermSignature> list) {
		this.results = new ArrayList<TermSignature>(list);
	}

	public void addResult(TermSignature signature) {
		this.results.add(signature);
	}
	
	public List<TermSignature> getTerms() {
		return results;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntermediateResult))
			return false;
		final IntermediateResult other = (IntermediateResult) obj;
		return results.equals(other.results);
	}

	public List<IIntermediateResult> getResultList() {
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>();
		result.add(this);
		return result;
	}

	@Override
	public String toString() {
		return results.toString();
	}
	
}
