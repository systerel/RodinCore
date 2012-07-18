/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Common protocol for an internal formula rewriter which is also responsible
 * for checking that the resulting formula is type compatible with the original
 * formula.
 * <p>
 * The first four methods are common with interface {@link IFormulaRewriter},
 * but as the latter is published while this interface is not, we cannot share
 * these declarations.
 * </p>
 * <p>
 * The remaining methods should perform the rewrite for the given sub-class of
 * <code>Formula</code> together with the type-checking. They take one parameter
 * if the node does not contain any children, or two parameters (the original
 * node and a new node possibly flattened and with children already rewritten).
 * In both cases, rewriting shall be performed on the last parameter.
 * </p>
 * <p>
 * Finally, there are special cases for mathematical extensions where it is not
 * always possible to build an intermediate node (i.e., with children already
 * rewritten to a different mathematical language). For extended expressions and
 * predicates, we instead pass part of the internal data-structures that hold
 * the already rewritten children, rather than a complete node.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 2.6
 */
public interface ITypedFormulaRewriter {

	boolean autoFlatteningMode();

	FormulaFactory getFactory();

	void enteringQuantifier(int nbOfBoundIdentDecls);

	void leavingQuantifier(int nbOfBoundIdentDecls);

	BoundIdentDecl rewrite(BoundIdentDecl src);

	Expression rewrite(AssociativeExpression src,
			AssociativeExpression expression);

	Predicate rewrite(AssociativePredicate src, AssociativePredicate pred);

	Predicate rewrite(PredicateVariable src);

	Expression rewrite(AtomicExpression src);

	Expression rewrite(BinaryExpression src, BinaryExpression expr);

	Predicate rewrite(BinaryPredicate src, BinaryPredicate pred);

	Expression rewrite(BoolExpression src, BoolExpression expr);

	Expression rewrite(BoundIdentifier src);

	Expression rewrite(FreeIdentifier src);

	Expression rewrite(IntegerLiteral src);

	Predicate rewrite(LiteralPredicate src);

	Predicate rewrite(MultiplePredicate src, MultiplePredicate pred);

	Expression rewrite(QuantifiedExpression src, QuantifiedExpression expr);

	Predicate rewrite(QuantifiedPredicate src, QuantifiedPredicate pred);

	Predicate rewrite(RelationalPredicate src, RelationalPredicate pred);

	Expression rewrite(SetExtension src, SetExtension expr);

	Expression rewrite(SetExtension src, AtomicExpression expr);

	Predicate rewrite(SimplePredicate src, SimplePredicate pred);

	Expression rewrite(UnaryExpression src, UnaryExpression expr);

	Expression rewrite(UnaryExpression src, IntegerLiteral expr);

	Predicate rewrite(UnaryPredicate src, UnaryPredicate pred);

	Expression rewrite(ExtendedExpression src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds);

	Predicate rewrite(ExtendedPredicate src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds);

}
