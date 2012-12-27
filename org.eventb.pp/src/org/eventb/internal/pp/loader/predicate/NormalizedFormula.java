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

import java.util.Comparator;
import java.util.List;

import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

public class NormalizedFormula implements Comparable<NormalizedFormula>,
		INormalizedFormula {

	private final Comparator<SignedFormula<?>> orderer;
	private final IIntermediateResult result;
	private final SignedFormula<?> signature;
	private final IOrigin origin;
	
	public NormalizedFormula(SignedFormula<?> signature,
			IIntermediateResult result, Comparator<SignedFormula<?>> orderer,
			IOrigin origin) {
		this.orderer = orderer;
		this.result = result;
		this.signature = signature;
		this.origin = origin;
	}

	@Override
	public int compareTo(NormalizedFormula o) {
		return orderer.compare(signature, o.signature);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.result.hashCode();
		result = prime * result + signature.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof NormalizedFormula))
			return false;
		final NormalizedFormula other = (NormalizedFormula) obj;
		return result.equals(other.result) && signature.equals(other.signature);
	}

	@Override
	public String toString() {
		return signature + " " + result;
	}

	public IIntermediateResult getResult() {
		return result;
	}

	@Override
	public SignedFormula<?> getSignature() {
		return signature;
	}

	@Override
	public IOrigin getOrigin() {
		return origin;
	}

	@Override
	public List<TermSignature> getTerms() {
		return result.getTerms();
	}
	
	public void negate() {
		signature.negate();
	}

	public boolean isPositive() {
		return signature.isPositive();
	}

}