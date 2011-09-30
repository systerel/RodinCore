/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.pm.basis.engine.MatchingUtilities;
import org.eventb.core.pm.basis.engine.PredicateVariableSubstituter;

/**
 * An implementation of a more structured binder that can be used when
 * associative matching is involved.
 * 
 * <p> Complex binders can be used to bind the right hand side of a rewrite rule with
 * associative complement potentially playing a part.
 * 
 * <p> This class is not intended to be sub-classed by clients.
 * 
 * @since 1.0
 * @author maamria
 * 
 */
public final class ComplexBinder {
	/**
	 * Helper variable name
	 */
	private static final String TEMP_IDENT = "z0_z1_z2_z3_z4_z123456789";
	
	FormulaFactory factory;
	
	public ComplexBinder(FormulaFactory factory) {
		this.factory =  factory;
	}

	/**
	 * Returns the formula resulting from binding the pattern to the binding of
	 * the given matching result.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param binding
	 *            the matching result
	 * @param includeComplement
	 *            whether associative complements should be considered
	 * @return the resultant formula
	 */
	public Formula<?> bind(Formula<?> pattern, IBinding binding, boolean includeComplement) {
		if (binding == null) {
			return null;
		}
		Formula<?> resultFormula = MatchingUtilities.parseFormula(
				pattern.toString(), pattern instanceof Expression, factory);
		Formula<?> finalResultFormula = resultFormula.rewrite(
				new PredicateVariableSubstituter(binding.getPredicateMappings(), factory));
		finalResultFormula.typeCheck(binding.getTypeEnvironment());
		// if the result is still not type-checked (e.g., dealing with atomic expression like {})
		if(!finalResultFormula.isTypeChecked()){
			// get the formula of the binding
			Formula<?> matchedFormula = binding.getFormula();
			// this should only happen with expressions
			if (matchedFormula instanceof Expression){
				// make a relational predicate with the formula = finalResultFormula to 
				// facilitate type checking
				FreeIdentifier identifier = factory.makeFreeIdentifier(TEMP_IDENT, null, ((Expression) matchedFormula).getType());
				RelationalPredicate relationalPredicate = 
					factory.makeRelationalPredicate(Formula.EQUAL, 
							(Expression)finalResultFormula, 
							identifier, null);
				// type check
				relationalPredicate.typeCheck(binding.getTypeEnvironment());
				// we should have a type-checked version of the final result
				finalResultFormula = relationalPredicate.getLeft();
			}
		}
		// make the substitutions
		Formula<?> formula = finalResultFormula.substituteFreeIdents(binding.getExpressionMappings(), factory);
		if (!includeComplement) {
			return formula;
		}
		if (formula instanceof Expression) {
			AssociativeExpressionComplement comp = binding.getAssociativeExpressionComplement();
			if (comp != null) {
				Expression e1 = comp.getToAppend();
				Expression e2 = comp.getToPrepend();
				int tag = comp.getTag();
				return MatchingUtilities.makeAppropriateAssociativeExpression(tag, factory, e1, (Expression) formula, e2);
			}
		} else {
			AssociativePredicateComplement comp = binding.getAssociativePredicateComplement();
			if (comp != null) {
				Predicate e1 = comp.getToAppend();
				Predicate e2 = comp.getToPrepend();
				int tag = comp.getTag();
				return MatchingUtilities.makeAssociativePredicate(tag, factory, e1, (Predicate) formula, e2);
			}
		}
		return formula;
	}

}
