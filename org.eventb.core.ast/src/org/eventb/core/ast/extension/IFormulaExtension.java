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

import static org.eventb.core.ast.extension.ExtensionFactory.TWO_EXPRS;
import static org.eventb.core.ast.extension.ExtensionFactory.TWO_OR_MORE_EXPRS;
import static org.eventb.core.ast.extension.ExtensionFactory.NO_CHILD;
import static org.eventb.core.ast.extension.ExtensionFactory.ONE_EXPR;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.INFIX;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.internal.core.ast.extension.ExtensionKind;

/**
 * Common protocol for formula extensions.
 * <p>
 * Standard supported extension kinds are provided as constants. Additionally,
 * instances obtained from
 * {@link ExtensionFactory#makePrefixKind(FormulaType, ITypeDistribution)} are
 * supported as well, which makes it possible to customize the arity of a
 * parenthesized formula.
 * </p>
 * <p>
 * For instance, an implementation of {@link #getKind()} for an expression with
 * three children of type expression, using methods from
 * {@link ExtensionFactory}, could be:
 * 
 * <pre>
 * public IExtensionKind getKind() {
 * 	return makePrefixKind(EXPRESSION, makeAllExpr(makeFixedArity(3)));
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

	/**
	 * Kind for atomic expressions. An atomic expression is an extended
	 * expression that takes no parameter, such as <code>"pred"</code> in the
	 * core language.
	 */
	IExtensionKind ATOMIC_EXPRESSION = makePrefixKind(EXPRESSION, NO_CHILD);

	/**
	 * Kind for binary infix expressions. A binary infix expression is an
	 * extended expression that takes two expressions as parameter, such as
	 * <code>"-"</code>" in the core language.
	 */
	IExtensionKind BINARY_INFIX_EXPRESSION = new ExtensionKind(INFIX,
			EXPRESSION, TWO_EXPRS, false);

	/**
	 * Kind for associative infix expressions. An associative infix expression
	 * is an extended expression that takes at least two expressions as
	 * parameters, such as "<code>+</code>" in the core language.
	 */
	IExtensionKind ASSOCIATIVE_INFIX_EXPRESSION = new ExtensionKind(INFIX,
			EXPRESSION, TWO_OR_MORE_EXPRS, true);

	/**
	 * Kind for unary prefix expressions. A unary prefix expression is an
	 * extended expression that takes one expression as parameter, such as
	 * <code>"max"</code>" in the core language. In the concrete syntax, the
	 * parameter must be bracketed with parentheses.
	 */
	IExtensionKind PARENTHESIZED_UNARY_EXPRESSION = makePrefixKind(EXPRESSION,
			ONE_EXPR);

	/**
	 * Kind for binary prefix expressions. A binary prefix expression is an
	 * extended expression that takes two expressions as parameters. In the
	 * concrete syntax, the parameters must be bracketed with parentheses and
	 * separated with a comma.
	 */
	IExtensionKind PARENTHESIZED_BINARY_EXPRESSION = makePrefixKind(EXPRESSION,
			TWO_EXPRS);

	/**
	 * Kind for unary prefix predicates. A unary prefix predicate is an extended
	 * predicate that takes one expression as parameter, such as
	 * <code>"finite"</code>" in the core language. In the concrete syntax, the
	 * parameter must be bracketed with parentheses.
	 */
	IExtensionKind PARENTHESIZED_UNARY_PREDICATE = makePrefixKind(PREDICATE,
			ONE_EXPR);

	/**
	 * Kind for binary prefix predicates. A binary prefix predicate is an
	 * extended predicate that takes two expressions as parameters. In the
	 * concrete syntax, the parameters must be bracketed with parentheses and
	 * separated with a comma.
	 */
	IExtensionKind PARENTHESIZED_BINARY_PREDICATE = makePrefixKind(PREDICATE,
			TWO_EXPRS);

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

	IExtensionKind getKind();

	/**
	 * Returns the origin of this extension, or <code>null</code> if no origin
	 * is defined.
	 * 
	 * @return an Object or <code>null</code>
	 */
	Object getOrigin();

	void addCompatibilities(ICompatibilityMediator mediator);

	void addPriorities(IPriorityMediator mediator);

}
