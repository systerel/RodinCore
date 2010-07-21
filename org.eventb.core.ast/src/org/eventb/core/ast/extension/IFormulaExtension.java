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

import static org.eventb.core.ast.extension.IOperatorProperties.*;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.*;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.*;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IOperatorProperties.Arity;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

/**
 * Common protocol for formula extensions.
 * <p>
 * Standard supported extension kinds are provided as constants. Additionally,
 * instances of {@link PrefixKind} are supported as well, which makes it
 * possible to customize the arity of a parenthesized formula.
 * </p>
 * <p>
 * For instance, an implementation of {@link #getKind()} could be:
 * 
 * <pre>
 * public IExtensionKind getKind() {
 * 	return new IFormulaExtension.PrefixKind(EXPRESSION, 3, EXPRESSION);
 * }
 * </pre>
 * 
 * which produces an expression extension of kind 'op(a,b,c)'.
 * </p>
 * <p>
 * In contrast, not all instances of {@link ExtensionKind} are supported, hence
 * the default visibility of the constructor.
 * </p>
 * 
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	public static class ExtensionKind implements IExtensionKind {

		private final IOperatorProperties operProps;
		
		ExtensionKind(Notation notation, FormulaType formulaType,
				Arity arity, FormulaType argumentType, boolean isAssociative) {
			this.operProps = makeOperProps(notation, formulaType, arity,
					argumentType, isAssociative);
		}

		public IOperatorProperties getProperties() {
			return operProps;
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

	// FIXME for now, only EXPRESSION children are supported.
	public static class PrefixKind extends ExtensionKind {

		public PrefixKind(FormulaType formulaType, int arity,
				FormulaType argumentType) {
			super(PREFIX, formulaType, new FixedArity(
					arity), argumentType, false);
		}
		
	}
	
	// Standard supported extension kinds.

	// op
	public static final IExtensionKind ATOMIC_EXPRESSION = new PrefixKind(EXPRESSION, 0, EXPRESSION);

	// a op b
	public static final IExtensionKind BINARY_INFIX_EXPRESSION = new ExtensionKind(INFIX, EXPRESSION, BINARY, EXPRESSION,
			false);

	// a op b op ... op c
	public static final IExtensionKind ASSOCIATIVE_INFIX_EXPRESSION = new ExtensionKind(INFIX, EXPRESSION, MULTARY_2, EXPRESSION,
			true);

	// op(a)
	public static final IExtensionKind PARENTHESIZED_UNARY_EXPRESSION = new PrefixKind(EXPRESSION, 1, EXPRESSION);

	// op(a, b)
	public static final IExtensionKind PARENTHESIZED_BINARY_EXPRESSION = new PrefixKind(EXPRESSION, 2, EXPRESSION);

	// op(a)
	public static final IExtensionKind PARENTHESIZED_UNARY_PREDICATE = new PrefixKind(PREDICATE, 1, EXPRESSION);

	// op(a, b)
	public static final IExtensionKind PARENTHESIZED_BINARY_PREDICATE = new PrefixKind(PREDICATE, 2, EXPRESSION);


	String getSyntaxSymbol();

	Predicate getWDPredicate(IWDMediator wdMediator, IExtendedFormula formula);

	String getId();

	String getGroupId();

	// FIXME redundancy between formula type provided through the kind and the
	// choice to implement IExpressionExtension or IPredicateExtension requires
	// to enforce a compatibility constraint.
	IExtensionKind getKind();

	void addCompatibilities(ICompatibilityMediator mediator);

	void addPriorities(IPriorityMediator mediator);

}
