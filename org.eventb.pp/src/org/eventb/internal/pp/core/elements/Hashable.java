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
package org.eventb.internal.pp.core.elements;

import java.util.Collection;

/**
 * Abstract base class for hashable literals.
 * <p>
 * This class holds two different hashcodes. A literal has two hashcodes,
 * one taking into account the fact that variables in two equal clauses
 * are in fact different instances, and one considering that all variables
 * have the same hashcode.
 *
 * @author François Terrier
 *
 */
public abstract class Hashable {

	final protected int hashCode;
	final protected int hashCodeWithDifferentVariables;
	
	protected Hashable(int hashCode, int hashCodeWithDifferentVariables) {
		this.hashCode = hashCode;
		this.hashCodeWithDifferentVariables = hashCodeWithDifferentVariables;
	}
	
	public final int hashCodeWithDifferentVariables() {
		return hashCodeWithDifferentVariables;
	}
	
	@Override
	public final int hashCode() {
		return hashCode;
	}
	
	/**
	 * Returns the combination of two hash codes.
	 * 
	 * @param hash1
	 *            a hash code
	 * @param hash2
	 *            another hash code
	 * @return a combination of the two hash codes
	 */
	protected static int combineHashCodes(int hash1, int hash2) {
		return hash1 * 37 + hash2;
	}

	/**
	 * Returns the combination of some formulas' hash codes.
	 * 
	 * @param formulas
	 *            some formulas
	 * @return a combination of the formulas' hash codes
	 */
	protected static <T extends Hashable> int combineHashCodesWithSameVariables(
			Collection<? extends T> formulas) {
		int result = 1;
		for (T formula: formulas) {
			result = combineHashCodes(result, formula.hashCode);
		}
		return result;
	}
	
	/**
	 * Returns the combination of some formulas' hash codes.
	 * 
	 * @param formulas
	 *            some formulas
	 * @return a combination of the formulas' hash codes
	 */
	protected static <T extends Hashable> int combineHashCodesWithDifferentVariables(
			Collection<? extends T> formulas) {
		int result = 1;
		for (T formula: formulas) {
			result = combineHashCodes(result, formula.hashCodeWithDifferentVariables);
		}
		return result;
	}
}
