/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;

/**
 * 
 * @author maamria
 *
 */
public class DefaultExtendedExpressionMatcher extends ExpressionMatcher<ExtendedExpression>{

	public DefaultExtendedExpressionMatcher() {
		super(ExtendedExpression.class);
	}

	@Override
	protected boolean gatherBindings(ExtendedExpression form, ExtendedExpression pattern, IBinding existingBinding) {
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
	protected ExtendedExpression getExpression(Expression e) {
		return (ExtendedExpression) e;
	}

}
