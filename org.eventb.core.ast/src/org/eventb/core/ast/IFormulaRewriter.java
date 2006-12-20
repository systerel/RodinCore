/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Common protocol for formula rewriters. A formula rewriter implements
 * rewriting of sub-formulas of some formula.
 * <p>
 * This interface contains one method for each of the sub-classes of
 * <code>Formula</code>, except assignments which are not covered by
 * sub-formula rewriting.
 * </p>
 * <p>
 * Rewriting must be compatible with type-checking. For each
 * <code>rewrite</code> method, if the input formula is type-checked, then the
 * returned formula must also be type-checked. Moreover, if the input formula is
 * a type-checked expression, then the returned expression must bear the same
 * mathematical type.
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @author Laurent Voisin
 * @see Formula#rewrite(IFormulaRewriter)
 */
public interface IFormulaRewriter {

	/**
	 * Rewriting is entering a quantified formula. When traversing the formula
	 * tree, this method is called when the rewriting traverses a quantifier,
	 * while going down the tree. In other term, this method is called just
	 * before rewriting the children of a quantified formula.
	 * <p>
	 * This method is especially handful if the rewriting manipulates some
	 * non-closed formula, the bound identifiers of which need to be shifted so
	 * that they retain the index corresponding to their declaration.
	 * <p>
	 * This method may be empty in case the rewriting is always performed on a
	 * local basis (such as replacing a formula by an equivalent but simpler
	 * one).
	 * </p>
	 * 
	 * @param nbOfDeclarations
	 *            nb of bound identifiers declared
	 * @see Formula#shiftBoundIdentifiers(int, FormulaFactory)
	 * @see #leavingQuantifier(int)
	 */
	void enteringQuantifier(int nbOfDeclarations);

	/**
	 * Return the formula factory which is used for the rewriting.
	 * 
	 * @return the formula factory to use when rewriting
	 */
	FormulaFactory getFactory();

	/**
	 * Rewriting is leaving a quantifier. When traversing the formula tree, this
	 * method is called when the rewriting traverses a quantifier, while going
	 * back up the tree. In other term, this method is called just after
	 * rewriting the children of a quantified formula, and just before rewriting
	 * the quantified formula itself.
	 * <p>
	 * This method may be empty in case the rewriting is always performed on a
	 * local basis (such as replacing a formula by an equivalent but simpler
	 * one).
	 * </p>
	 * 
	 * @param nbOfDeclarations
	 *            nb of bound identifiers declared
	 * @see Formula#shiftBoundIdentifiers(int, FormulaFactory)
	 * @see #enteringQuantifier(int)
	 */
	void leavingQuantifier(int nbOfDeclarations);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(AssociativeExpression expression);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(AssociativePredicate predicate);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(AtomicExpression expression);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(BinaryExpression expression);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(BinaryPredicate predicate);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(BoolExpression expression);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param identifier
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(BoundIdentifier identifier);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param identifier
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(FreeIdentifier identifier);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param literal
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(IntegerLiteral literal);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(LiteralPredicate predicate);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(QuantifiedExpression expression);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(QuantifiedPredicate predicate);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(RelationalPredicate predicate);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(SetExtension expression);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(SimplePredicate predicate);

	/**
	 * Rewrites the given expression.
	 * 
	 * @param expression
	 *            expression to rewrite
	 * @return the given expression rewritten
	 */
	Expression rewrite(UnaryExpression expression);

	/**
	 * Rewrites the given predicate.
	 * 
	 * @param predicate
	 *            expression to rewrite
	 * @return the given predicate rewritten
	 */
	Predicate rewrite(UnaryPredicate predicate);

}
