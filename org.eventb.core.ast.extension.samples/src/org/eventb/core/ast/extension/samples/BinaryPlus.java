/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension.samples;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.internal.core.typecheck.TypeCheckResult;

/**
 * A first attempt at coding an extension. Multiple compilation errors invite to
 * refactor the extension mechanism.
 * 
 * @author Nicolas Beauger
 * 
 */
public class BinaryPlus implements IExpressionExtension {

	
	// problem: need a factory for types
	public Type getType(Expression[] childExpressions,
			Predicate[] childPredicates) {
		for (Expression child: childExpressions) {
			final Type childType = child.getType();
			if (! (childType instanceof IntegerType)) {
				return null;
			}
		}
		return childExpressions[0].getType();
	}

	// class TypeCheckResult is internal and not exported
	// visibility problems
	// => need an intermediate structure, extensors should only provide
	// a set of high level type equations like
	// 'all children have the same type' or
	// 'T_e_1 = POW (T_e_2 × T_e_3)' or
	// 'T_e_1 = POW ( _ × POW ( _ ) )
	public Type typeCheck(TypeCheckResult result,
			BoundIdentDecl[] quantifiedIdentifiers,
			Expression[] childExpressions, Predicate[] childPredicates, Expression origin) {
		final Type resultType = result.makeIntegerType();
		for (int i = 0; i < childExpressions.length; i++) {
			childExpressions[i].typeCheck(result,quantifiedIdentifiers);
			result.unify(childExpressions[i].getType(), resultType, origin);
		}
		return resultType;
	}

	public void checkPreconditions(Expression[] expressions,
			Predicate[] predicates) {
		assert expressions.length >= 2;
		assert predicates.length == 0;
	}

	public String getTagOperator() {
		return "+";
	}

	// visibility problems => utility classes or intermediate structure
	// extensors should only provide a set of 
	// high level WD predicate like
	// 'children WD conjunction' (default and mandatory) or
	// 'e_1 >=0 and e_2 > e_1'
	public Predicate getWDPredicateRaw(FormulaFactory formulaFactory,
			Expression[] childExpressions, Predicate[] childPredicates) {
		return Formula.getWDConjunction(formulaFactory, childExpressions);
//		Predicate leftConjunct = childExpressions[0].getWDPredicateRaw(formulaFactory);
//		Predicate rightConjunct = childExpressions[1].getWDPredicateRaw(formulaFactory);
//		return Formula.getWDSimplifyC(formulaFactory, leftConjunct, rightConjunct);
	}

	public boolean isFlattenable() {
		return false;
	}

	// visibility problems => need a high level notation expression like
	// 'simple infix'
	// '[ e_1 @ e_2 | e_3 ]'
	public void prettyPrint(StringBuilder builder, int tag,
			String[] boundNames, boolean withTypes,
			Expression[] childExpressions, Predicate[] childPredicates) {
		childExpressions[0].toString(builder, false, tag, boundNames, withTypes);
		builder.append(getTagOperator());
		childExpressions[1].toString(builder, true, tag, boundNames, withTypes);
	}

	public void prettyPrintFullyParenthesized(StringBuilder builder, int tag,
			String[] boundNames, Expression[] childExpressions,
			Predicate[] childPredicates) {
		childExpressions[0].toStringFullyParenthesized(builder, boundNames);
		builder.append(getTagOperator());
		childExpressions[1].toStringFullyParenthesized(builder, boundNames);
	}

}
