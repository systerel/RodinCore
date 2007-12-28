/*******************************************************************************
 * Copyright (c) 2006, 2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.loader.formula.SignedFormula;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;

/**
 * This class is used by the builder to store informations about the literals
 * encountered in the current level of the abstract syntax tree (AST) of an Event-B
 * predicate. The predicate loader maintains a stack of objects of this class,
 * one for each sub-formula of the AST.
 *
 * @author François Terrier
 *
 */
public class NormalizedFormula implements INormalizedFormula {

	private List<ResultPair> list = new ArrayList<ResultPair>();
	
	protected Comparator<SignedFormula<?>> orderer;
	private int startOffset, endOffset;
	private BoundIdentDecl[] boundIdentDecls;
	
	private final IOrigin origin;
	
	public NormalizedFormula(Comparator<SignedFormula<?>> orderer, int startOffset, int endOffset,
			BoundIdentDecl[] boundIdentDecls, IOrigin origin) {
		this.orderer = orderer;
		this.boundIdentDecls = boundIdentDecls;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.origin = origin;
	}
	
	protected void orderList() {
		Collections.sort(list);
	}
	
	public BoundIdentDecl[] getBoundIdentDecls() {
		return boundIdentDecls;
	}
	
	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	/**
	 * Puts the negation sign on the first literal of the list, or
	 * no negation sign. This is meant for equivalence clauses. The clause
	 * should be ordered.
	 */
	protected void reduceNegations() {
		int numberOfNegations = 0;
		for (ResultPair pair : list) {
			if (!pair.signature.isPositive()) {
				numberOfNegations++;
				pair.signature.negate();
			}
		}
		if (numberOfNegations % 2 != 0) {
			list.get(0).signature.negate();
		}
	}
	
	public void addResult(SignedFormula<?> signature, IIntermediateResult inRes) {
		list.add(new ResultPair(signature, inRes));
	}

	public List<SignedFormula<?>> getLiterals() {
		List<SignedFormula<?>> result = new ArrayList<SignedFormula<?>>();
		for (ResultPair pair : list) {
			result.add(pair.signature);
		}
		return result;
	}
	
	private List<IIntermediateResult> getIntermediateResults() {
		List<IIntermediateResult> result = new ArrayList<IIntermediateResult>();
		for (ResultPair pair : list) {
			result.add(pair.result);
		}
		return result;
	}
	
	public IIntermediateResult getNewIntermediateResult() {
		return new IntermediateResultList(getIntermediateResults());
	}
	
	public SignedFormula<?> getSignature() {
		return list.get(0).signature;
	}

	public List<TermSignature> getTerms() {
		return list.get(0).result.getTerms();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NormalizedFormula) {
			NormalizedFormula temp = (NormalizedFormula) obj;
			return list.equals(temp.list);
		}
		return false;
	}

	private class ResultPair implements Comparable<ResultPair> {
		IIntermediateResult result;
		SignedFormula<?> signature;
		
		ResultPair(SignedFormula<?> signature, IIntermediateResult result) {
			this.result = result;
			this.signature = signature;
		}

		public int compareTo(ResultPair o) {
			return orderer.compare(signature, o.signature);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ResultPair) {
				ResultPair temp = (ResultPair) obj;
				return result.equals(temp.result) && signature.equals(temp.signature);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return signature + " " + result;
		}
	}

	@Override
	public String toString() {
		return list.toString();
	}

	public IOrigin getOrigin() {
		return origin;
	}

}
