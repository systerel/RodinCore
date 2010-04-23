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

import org.eclipse.core.runtime.Assert;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.IToStringMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * A third attempt at coding an extension. Looks better (no compile errors !).
 * 
 * @author Nicolas Beauger
 * 
 */
public class BinaryPlus3 implements IExpressionExtension {

	public Type getType(ITypeMediator mediator, ExtendedExpression expression) {
		final Type resultType = mediator.makeIntegerType();
		for (Expression child : expression.getChildExpressions()) {
			final Type childType = child.getType();
			if (!childType.equals(resultType)) {
				return null;
			}
		}
		return resultType;
	}

	public Type typeCheck(ITypeCheckMediator mediator,
			ExtendedExpression expression) {
		final Type resultType = mediator.makeIntegerType();
		for (Expression child : expression.getChildExpressions()) {
			mediator.sameType(child.getType(), resultType);
		}
		return resultType;
	}

	public void checkPreconditions(Expression[] expressions,
			Predicate[] predicates) {
		Assert.isTrue(expressions.length >= 2);
		Assert.isTrue(predicates.length == 0);
	}

	public String getSyntaxSymbol() {
		return "+";
	}

	public Predicate getWDPredicate(IWDMediator mediator,
			IExtendedFormula formula) {
		return mediator.makeChildWDConjunction(formula);
	}

	public void toString(IToStringMediator mediator, IExtendedFormula formula) {
		final Expression[] childExpressions = formula.getChildExpressions();
		mediator.append(childExpressions[0], false);
		mediator.append(getSyntaxSymbol());
		mediator.append(childExpressions[1], true);
	}

	public boolean isFlattenable() {
		return false;
	}

	public void addCompatibilities(ICompatibilityMediator mediator) {
		mediator.addCompatibility(getId(), getId());
	}

	public void addPriorities(IPriorityMediator mediator) {
		// no priorities to set
	}

	public String getGroupId() {
		return "arithmetic";
	}

	public String getId() {
		return "binary plus 3";
	}

	public ExtensionKind getKind() {
		return ExtensionKind.BINARY_INFIX_EXPRESSION;
	}

}
