/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;

/**
 * An implementation of a matching engine.
 * <p> All matching processes are initiated from within a matcher. Each matcher works with
 * a an instance of a <code>FormulaFactory</code> to ensure consistency of mathematical
 * extensions used throughout a matching process.
 * <p> This class is not intended to be sub-classed by clients.
 * @author maamria
 * @since 1.0
 * 
 */
public final class Matcher {

	private FormulaFactory factory;
	private MatchingFactory matchingFactory;

	public Matcher(FormulaFactory factory) {
		this.factory = factory;
		this.matchingFactory = MatchingFactory.getInstance();
	}

	/**
	 * Matches the formula and the pattern and produces a matching result.
	 * <p> The matching process can be instructed to produce partial matches. This is relevant when matching
	 * two associative expressions (or predicates).
	 * @param form the formula
	 * @param pattern the pattern
	 * @param acceptPartialMatch whether to accept a partial match
	 * @return the binding, or <code>null</code> if matching failed
	 */
	public IBinding match(Formula<?> form, Formula<?> pattern, boolean acceptPartialMatch) {
		IBinding initialBinding = matchingFactory.createBinding(form, pattern,acceptPartialMatch, factory);
		if (matchingFactory.match(form, pattern, initialBinding)){
			initialBinding.makeImmutable();
			return initialBinding;
		}
		return null;
	}

	/**
	 * Returns the formula factory with which this matcher is working.
	 * @return the formula factory
	 */
	public FormulaFactory getFactory() {
		return factory;
	}

	/**
	 * Returns the matching factory used by this matcher.
	 * @return the matching factory
	 */
	public MatchingFactory getMatchingFactory() {
		return matchingFactory;
	}

}
