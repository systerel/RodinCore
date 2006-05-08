/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.pp;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.translator.BoundIdentifierDecomposition;
import org.eventb.internal.pp.translator.FreeIdentifierDecomposition;
import org.eventb.internal.pp.translator.GoalChecker;
import org.eventb.internal.pp.translator.PredicateSimplification;

/**
 * Provides a facade to the org.eventb.pp package.
 * 
 * 
 * @author Matthias Konrad
 */

public abstract class Translator {
	
	
	/**
	 * Decomposes every free or bound idetnifier of a cartesian product type. Decomposed
	 * predicates can be reduced with reduceToPredicateCalculus.
	 * @param predicate the predicate to be decomposed
	 * @param ff the formula factory to be used during decomposition
	 * @return Returns a new predicate that has no identifiers of a cartesian product type,
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
		return org.eventb.internal.pp.translator.Translator.reduceToPredCalc(predicate, ff);
	}
	
	
	/**
	 * Simplifies predicates with some basic rules.
	 * @param predicate the predicate to be simplified.
	 * @param ff the formula factury that should be used during the simplification
	 * @return A new simplified predicate
	 */
	public static Predicate simplifyPredicate(Predicate predicate, FormulaFactory ff) {
		return PredicateSimplification.simplifyPredicate(predicate, ff);
	}

	public static boolean isInGoal(Predicate predicate) {
		return GoalChecker.isInGoal(predicate);
	}
}
