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
 * 
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	// FIXME move this class to a non-API package
	public static class ExtensionKind implements IExtensionKind {

		private final IOperatorProperties operProps;

		ExtensionKind(Notation notation, FormulaType formulaType, Arity arity,
				FormulaType argumentType, boolean isAssociative) {
			this.operProps = makeOperProps(notation, formulaType, arity,
					argumentType, isAssociative);
		}

		@Override
		public IOperatorProperties getProperties() {
			return operProps;
		}

		@Override
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
	// FIXME should rather be available through a factory
	public static class PrefixKind extends ExtensionKind {

		public PrefixKind(FormulaType formulaType, int arity,
				FormulaType argumentType) {
			super(PREFIX, formulaType, new FixedArity(arity), argumentType,
					false);
		}

	}

	// Standard supported extension kinds.

	/**
	 * Kind for atomic expressions. An atomic expression is an extended
	 * expression that takes no parameter, such as <code>"pred"</code> in the
	 * core language.
	 */
	IExtensionKind ATOMIC_EXPRESSION = new PrefixKind(EXPRESSION, 0, EXPRESSION);

	/**
	 * Kind for binary infix expressions. A binary infix expression is an
	 * extended expression that takes two expressions as parameter, such as
	 * <code>"-"</code>" in the core language.
	 */
	IExtensionKind BINARY_INFIX_EXPRESSION = new ExtensionKind(INFIX,
			EXPRESSION, BINARY, EXPRESSION, false);

	/**
	 * Kind for associative infix expressions. An associative infix expression
	 * is an extended expression that takes at least two expressions as
	 * parameters, such as "<code>+</code>" in the core language.
	 */
	IExtensionKind ASSOCIATIVE_INFIX_EXPRESSION = new ExtensionKind(INFIX,
			EXPRESSION, MULTARY_2, EXPRESSION, true);

	/**
	 * Kind for unary prefix expressions. A unary prefix expression is an
	 * extended expression that takes one expression as parameter, such as
	 * <code>"max"</code>" in the core language. In the concrete syntax, the
	 * parameter must be bracketed with parentheses.
	 */
	IExtensionKind PARENTHESIZED_UNARY_EXPRESSION = new PrefixKind(EXPRESSION,
			1, EXPRESSION);

	/**
	 * Kind for binary prefix expressions. A binary prefix expression is an
	 * extended expression that takes two expressions as parameters. In the
	 * concrete syntax, the parameters must be bracketed with parentheses and
	 * separated with a comma.
	 */
	IExtensionKind PARENTHESIZED_BINARY_EXPRESSION = new PrefixKind(EXPRESSION,
			2, EXPRESSION);

	/**
	 * Kind for unary prefix predicates. A unary prefix predicate is an extended
	 * predicate that takes one expression as parameter, such as
	 * <code>"finite"</code>" in the core language. In the concrete syntax, the
	 * parameter must be bracketed with parentheses.
	 */
	IExtensionKind PARENTHESIZED_UNARY_PREDICATE = new PrefixKind(PREDICATE, 1,
			EXPRESSION);

	/**
	 * Kind for binary prefix predicates. A binary prefix predicate is an
	 * extended predicate that takes two expressions as parameters. In the
	 * concrete syntax, the parameters must be bracketed with parentheses and
	 * separated with a comma.
	 */
	IExtensionKind PARENTHESIZED_BINARY_PREDICATE = new PrefixKind(PREDICATE,
			2, EXPRESSION);

	String getSyntaxSymbol();

	Predicate getWDPredicate(IExtendedFormula formula, IWDMediator wdMediator);

	/**
	 * Whether or not children WD is conjoined to the returned WD predicate.
	 * <p>
	 * If <code>true</code>, the resulting WD is 'getWDPredicate() and
	 * WD(children)'. If <code>false</code>, the resulting WD is just
	 * 'getWDPredicate()'.
	 * </p>
	 * <p>
	 * In most cases, children WD shall be conjoined. Reasons not to do so
	 * include the case where a WD of the form 'P and ( P => WD(children) )' is
	 * desired. In the latter case, it is the responsibility of
	 * {@link #getWDPredicate(IExtendedFormula, IWDMediator)} to explicitly
	 * embed the WD conditions for children in the returned predicate.
	 * </p>
	 * 
	 * @return <code>true</code> iff children WD is conjoined.
	 */
	boolean conjoinChildrenWD();

	String getId();

	String getGroupId();

	// FIXME redundancy between formula type provided through the kind and the
	// choice to implement IExpressionExtension or IPredicateExtension requires
	// to enforce a compatibility constraint.
	IExtensionKind getKind();

	void addCompatibilities(ICompatibilityMediator mediator);

	void addPriorities(IPriorityMediator mediator);

}
