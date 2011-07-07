/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pptrans;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pptrans.translator.BoundIdentifierDecomposition;
import org.eventb.internal.pptrans.translator.FreeIdentifierDecomposition;
import org.eventb.internal.pptrans.translator.GoalChecker;
import org.eventb.internal.pptrans.translator.PredicateSimplification;

/**
 * Provides access to the Translator of the Predicate Prover. The intended use
 * of this class is to call the first three provided methods in sequence, as
 * follows
 * <ol>
 * <li>{@link #decomposeIdentifiers(Predicate, FormulaFactory)}</li>
 * <li>{@link #reduceToPredicateCalulus(Predicate, FormulaFactory)}</li>
 * <li>{@link #simplifyPredicate(Predicate, FormulaFactory)}</li>
 * </ol>
 * <p>
 * The additional method {@link #isInGoal(Predicate)} is provided for test
 * purposes. It allows to check that the result of a translation is indeed in
 * the target sub-language.
 * </p>
 * 
 * @author Matthias Konrad
 * @since 0.2
 */
public class Translator {

	private Translator() {
		// Non-instantiable class
	}

	/**
	 * Decomposes every free or bound identifier of Cartesian product type in
	 * the given predicate.
	 * 
	 * @param predicate
	 *            the predicate whose identifiers are to be decomposed
	 * @param ff
	 *            the formula factory to use
	 * @return an equivalent predicate that contains no identifier of Cartesian
	 *         product type, except for the quantification of the free
	 *         identifiers
	 */
	public static Predicate decomposeIdentifiers(Predicate predicate,
			FormulaFactory ff) {
		predicate = FreeIdentifierDecomposition.decomposeIdentifiers(predicate,
				ff);
		return BoundIdentifierDecomposition.decomposeBoundIdentifiers(
				predicate, ff);
	}

	/**
	 * Transforms a predicate from Set-Theory to Predicate Calculus.
	 * 
	 * @param predicate
	 *            the predicate to reduce. This predicate must be in a
	 *            decomposed form
	 * @param ff
	 *            the formula factory to use
	 * @return a reduced predicate equivalent to the input predicate
	 */
	public static Predicate reduceToPredicateCalulus(Predicate predicate,
			FormulaFactory ff) {
		return org.eventb.internal.pptrans.translator.Translator
				.reduceToPredCalc(predicate, ff);
	}

	/**
	 * Simplifies the given predicate using some basic simplification rules.
	 * 
	 * @param predicate
	 *            the predicate to simplify
	 * @param ff
	 *            the formula factory to use
	 * @return a simplified predicate equivalent to the input predicate
	 */
	public static Predicate simplifyPredicate(Predicate predicate,
			FormulaFactory ff) {
		return PredicateSimplification.simplifyPredicate(predicate, ff);
	}

	/**
	 * Tells whether the given predicate is in the target sub-language of the PP
	 * translator. The predicate returned by method
	 * {@link #reduceToPredicateCalulus(Predicate, FormulaFactory)} is
	 * guaranteed to pass this test.
	 * 
	 * @param predicate
	 *            a predicate to test
	 * @return <code>true</code> iff the given predicate is in the target
	 *         sub-language of the PP translator
	 */
	public static boolean isInGoal(Predicate predicate) {
		return GoalChecker.isInGoal(predicate);
	}
}
