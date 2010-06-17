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
import static org.eventb.internal.core.ast.extension.ExtensionPrinters.ATOMIC_EXPR_PRINTER;
import static org.eventb.internal.core.ast.extension.ExtensionPrinters.INFIX_EXPR_PRINTER;
import static org.eventb.internal.core.ast.extension.ExtensionPrinters.PAREN_PREFIX_EXPR_PRINTER;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IOperatorProperties.Arity;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.internal.core.ast.extension.ExtensionPrinters.IExtensionPrinter;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	/**
	 * Standard supported extension kinds.
	 */
	public static enum ExtensionKind implements IExtensionKind {
		ATOMIC_EXPRESSION(PREFIX, EXPRESSION, NULLARY, EXPRESSION,
				ATOMIC_EXPR_PRINTER, false),

		// a op b
		BINARY_INFIX_EXPRESSION(INFIX, EXPRESSION, BINARY, EXPRESSION,
				INFIX_EXPR_PRINTER, false),

		// a op b op ... op c
		ASSOCIATIVE_INFIX_EXPRESSION(INFIX, EXPRESSION, MULTARY_2, EXPRESSION,
				INFIX_EXPR_PRINTER, true),

		// op(a, b, ..., c) with 1 or more arguments
		PARENTHESIZED_EXPRESSION_1(PREFIX, EXPRESSION, MULTARY_1, EXPRESSION,
				PAREN_PREFIX_EXPR_PRINTER, false),

		// op(a, b, ..., c) with 2 or more arguments
		PARENTHESIZED_EXPRESSION_2(PREFIX, EXPRESSION, MULTARY_2, EXPRESSION,
				PAREN_PREFIX_EXPR_PRINTER, false),

		// TODO PARENTHESIZED_PREDICATE
		;


		private final IOperatorProperties operProps;
		private final IExtensionPrinter printer;
		private final boolean flattenable;
		
		private ExtensionKind(Notation notation,
				FormulaType formulaType, Arity arity, FormulaType argumentType,
				IExtensionPrinter printer, boolean flattenable) {
			this.operProps = makeOperProps(notation, formulaType, arity, argumentType);
			this.printer = printer;
			this.flattenable = flattenable;
		}

		public IOperatorProperties getProperties() {
			return operProps;
		}

		public IExtensionPrinter getPrinter() {
			return printer;
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
