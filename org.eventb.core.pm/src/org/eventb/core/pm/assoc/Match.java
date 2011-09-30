/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.pm.IBinding;

/**
 * A simple implementation of a match.
 * 
 * <p> A match has an indexed pattern, an indexed formula and a binding 
 * that when applied to the pattern results in the formula.
 * @author maamria
 *
 */
public class Match<F extends Formula<F>> {

	private IndexedFormula<F> indexedFormula;
	private IndexedFormula<F> indexedPattern;
	private IBinding binding;
	
	/**
	 * Creates a match between the formula and the patterns. The supplied binding, when applied, must match formula to the pattern.
	 * @param indexedFormula the indexed formula
	 * @param indexedPattern the indexed pattern
	 * @param binding the binding
	 */
	public Match(IndexedFormula<F> indexedFormula, IndexedFormula<F> indexedPattern, IBinding binding){
		this.indexedFormula = indexedFormula;
		this.indexedPattern = indexedPattern;
		this.binding = binding;
	}

	/**
	 * Returns the indexed formula.
	 * @return the indexed formula
	 */
	public IndexedFormula<F> getIndexedFormula() {
		return indexedFormula;
	}

	/**
	 * Returns the indexed pattern.
	 * @return the indexed pattern
	 */
	public IndexedFormula<F> getIndexedPattern() {
		return indexedPattern;
	}

	/**
	 * Returns the binding that unifies the pattern with the formula.
	 * @return the binding
	 */
	public IBinding getBinding() {
		return binding;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Match{");
		builder.append("Formula : " + indexedFormula + ",");
		builder.append("Pattern : " + indexedPattern + ",");
		builder.append("Binding : "+binding+"}");
		return builder.toString();
	}
	
}
