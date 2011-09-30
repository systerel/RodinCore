/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.PredicateMatcher;

/**
 * 
 * @author maamria
 *
 */
public class DefaultExtendedPredicateMatcher extends PredicateMatcher<ExtendedPredicate> {

	public DefaultExtendedPredicateMatcher() {
		super(ExtendedPredicate.class);
	}

	@Override
	protected boolean gatherBindings(ExtendedPredicate form, ExtendedPredicate pattern, IBinding existingBinding) {
		Expression[] formulaExpressions = form.getChildExpressions();
		Expression[] patternExpressions =  pattern.getChildExpressions();
		if (formulaExpressions.length != patternExpressions.length){
			return false;
		}
		for (int i = 0 ; i < formulaExpressions.length ; i++){
			if (patternExpressions[i] instanceof FreeIdentifier){
				if (! existingBinding.putExpressionMapping((FreeIdentifier) patternExpressions[i], formulaExpressions[i])){
					return false;
				}
			}
			else if (!matchingFactory.match(formulaExpressions[i], patternExpressions[i], existingBinding)){
				return false;
			}
		}
		Predicate[] formulaPredicates = form.getChildPredicates();
		Predicate[] patternPredicates = pattern.getChildPredicates();
		if (formulaPredicates.length != patternPredicates.length){
			return false;
		}
		for (int i = 0 ; i < formulaPredicates.length ; i++){
			if (patternPredicates[i] instanceof PredicateVariable){
				if (!existingBinding.putPredicateMapping((PredicateVariable) patternPredicates[i], formulaPredicates[i])){
					return false;
				}
			}
			else if (!matchingFactory.match(formulaPredicates[i], patternPredicates[i], existingBinding)){
				return false;
			}
		}
		return true;
	}

	@Override
	protected ExtendedPredicate getPredicate(Predicate p) {
		return (ExtendedPredicate) p;
	}

}
