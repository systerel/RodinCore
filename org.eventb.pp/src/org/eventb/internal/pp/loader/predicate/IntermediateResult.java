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
package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * Implementation of the argument list of a literal predicate.
 * <p>
 * See {@link IntermediateResultList} for the class representing a formula,
 * i.e., a list of list of arguments.
 * </p>
 * 
 * @see IntermediateResultList
 * 
 * @author François Terrier
 */
public class IntermediateResult implements IIntermediateResult {

	private final TermSignature[] terms;
	
	public IntermediateResult(List<TermSignature> list) {
		this.terms = list.toArray(new TermSignature[list.size()]);
	}

	@Override
	public List<TermSignature> getTerms() {
		return new ArrayList<TermSignature>(Arrays.asList(terms));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntermediateResult))
			return false;
		final IntermediateResult other = (IntermediateResult) obj;
		return Arrays.equals(this.terms, other.terms);
	}

	@Override
	public List<IIntermediateResult> getResultList() {
		final List<IIntermediateResult> result = new ArrayList<IIntermediateResult>();
		result.add(this);
		return result;
	}

	@Override
	public String toString() {
		return Arrays.toString(terms);
	}
	
}
