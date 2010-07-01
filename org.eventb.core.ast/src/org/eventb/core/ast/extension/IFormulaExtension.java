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
package org.eventb.core.ast.extension;

import static org.eventb.core.ast.extension.IOperatorProperties.Arity.*;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.*;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.*;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IOperatorProperties.Arity;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	/**
	 * Standard supported extension kinds.
	 */
	public static enum ExtensionKind implements IExtensionKind {
		
		// op
		ATOMIC_EXPRESSION(PREFIX, EXPRESSION, NULLARY, EXPRESSION,
				false),

		// a op b
		BINARY_INFIX_EXPRESSION(INFIX, EXPRESSION, BINARY, EXPRESSION,
				false),

		// a op b op ... op c
		ASSOCIATIVE_INFIX_EXPRESSION(INFIX, EXPRESSION, MULTARY_2, EXPRESSION,
				true),

		// op(a, b, ..., c) with 1 or more arguments
		PARENTHESIZED_EXPRESSION_1(PREFIX, EXPRESSION, MULTARY_1, EXPRESSION,
				false),

		// op(a, b, ..., c) with 2 or more arguments
		PARENTHESIZED_EXPRESSION_2(PREFIX, EXPRESSION, MULTARY_2, EXPRESSION,
				false),

		// TODO PARENTHESIZED_PREDICATE
		;


		private final IOperatorProperties operProps;
		private final boolean flattenable;
		
		private ExtensionKind(Notation notation, FormulaType formulaType,
				Arity arity, FormulaType argumentType, boolean flattenable) {
		this.operProps = makeOperProps(notation, formulaType, arity, argumentType);
			this.flattenable = flattenable;
		}

		public IOperatorProperties getProperties() {
			return operProps;
		}

		public boolean isFlattenable() {
			return flattenable;
		}

		public boolean checkPreconditions(Expression[] childExprs,
				Predicate[] childPreds) {
			final int children;
			final int alien;
			if (operProps.getArgumentType() == EXPRESSION) {
				children = childExprs.length;
				alien = childPreds.length;
			} else {
				children = childPreds.length;
				alien = childExprs.length;
			}
			return operProps.getArity().check(children) && alien == 0;
		}
	}

	String getSyntaxSymbol();

	Predicate getWDPredicate(IWDMediator wdMediator, IExtendedFormula formula);

	String getId();

	String getGroupId();

	IExtensionKind getKind();

	void addCompatibilities(ICompatibilityMediator mediator);

	void addPriorities(IPriorityMediator mediator);

}
