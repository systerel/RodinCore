/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
 * Provides a facade to the org.eventb.pptrans package.
 * 
 * 
 * @author Matthias Konrad
 * @since 0.2
 */

public class Translator {
	
	private Translator() {
		// Non-instantiable class
	}
	
	/**
	 * Decomposes every free or bound identifier of a Cartesian product type. Decomposed
	 * predicates can be reduced with reduceToPredicateCalculus.
	 * @param predicate the predicate to be decomposed
	 * @param ff the formula factory to be used during decomposition
	 * @return Returns a new predicate that has no identifiers of a Cartesian product type,
	 * except for the quantification of the free identifiers.
	 */
	public static Predicate decomposeIdentifiers(Predicate predicate, FormulaFactory ff) {
		predicate = FreeIdentifierDecomposition.decomposeIdentifiers(predicate, ff);
		return BoundIdentifierDecomposition.decomposeBoundIdentifiers(predicate, ff);
	}
		
	/**
	 * Transforms a predicate from Set-Theory to Predicate Calculus
	 * @param predicate the predicate to be reduced. This predicate needs to be
	 * in a decomposed form. Use decomposeIdentifier if this is not yet the case. 
	 * @param ff the formula factory that should be used during the reduction
	 * @return Returns a new reduced predicate 
	 */
	public static Predicate reduceToPredicateCalulus(Predicate predicate, FormulaFactory ff) {
		return org.eventb.internal.pptrans.translator.Translator.reduceToPredCalc(predicate, ff);
	}
	
	
	/**
	 * Simplifies predicates with some basic rules.
	 * @param predicate the predicate to be simplified.
	 * @param ff the formula factory that should be used during the simplification
	 * @return A new simplified predicate
	 */
	public static Predicate simplifyPredicate(Predicate predicate, FormulaFactory ff) {
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
