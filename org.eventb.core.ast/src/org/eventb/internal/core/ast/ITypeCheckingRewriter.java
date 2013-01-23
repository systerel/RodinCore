/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - always rewrite leaf node when factory changed 
 *******************************************************************************/
package org.eventb.internal.core.ast;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

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
 * <p>
 * Implementation note: In case the node is a leaf, the implementor must
 * guarantee that the node is rebuilt with the rewriter factory if the factory
 * of the node if the factory of the node is different. Pay attention to the
 * fact that extensions can be leaf nodes.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 2.6
 */
public interface ITypeCheckingRewriter {

	boolean autoFlatteningMode();

	FormulaFactory getFactory();

	void enteringQuantifier(int nbOfBoundIdentDecls);

	void leavingQuantifier(int nbOfBoundIdentDecls);

	/**
	 * Rewrite the bound identifier declaration leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src the leaf node to be rewritten
	 * @return the rewritten node
	 */
	BoundIdentDecl rewrite(BoundIdentDecl src);

	Expression rewrite(AssociativeExpression src,
			AssociativeExpression expression);

	Predicate rewrite(AssociativePredicate src, AssociativePredicate pred);

	/**
	 * Rewrite the predicate variable leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src
	 *            the leaf node to be rewritten
	 * @return the rewritten node
	 */
	Predicate rewrite(PredicateVariable src);

	/**
	 * Rewrite the atomic expression leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src
	 *            the leaf node to be rewritten
	 * @return the rewritten node
	 */
	Expression rewrite(AtomicExpression src);

	Expression rewrite(BinaryExpression src, BinaryExpression expr);

	Predicate rewrite(BinaryPredicate src, BinaryPredicate pred);

	Expression rewrite(BoolExpression src, BoolExpression expr);

	/**
	 * Rewrite the bound identifier leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src the leaf node to be rewritten
	 * @return the rewritten node
	 */
	Expression rewrite(BoundIdentifier src);

	/**
	 * Rewrite the free identifier leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src the leaf node to be rewritten
	 * @return the rewritten node
	 */
	Expression rewrite(FreeIdentifier src);

	/**
	 * Rewrite the integer literal leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src the leaf node to be rewritten
	 * @return the rewritten node
	 */
	Expression rewrite(IntegerLiteral src);

	/**
	 * Rewrite the literal predicate leaf node.
	 * <p>
	 * The node must be rebuilt with the rewriter factory if the node factory is
	 * different whatever the node content is modified or not.
	 * </p>
	 * 
	 * @param src the leaf node to be rewritten
	 * @return the rewritten node
	 */
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

	/**
	 * Rewrite the extended expression node.
	 * <p>
	 * Since this node can be a leaf, it must be rebuilt with the rewriter
	 * factory if the node factory is different whatever the node content is
	 * modified or not.
	 * </p>
	 * 
	 * @param src
	 *            the extended expression node to be rewritten
	 * @return the rewritten node
	 */
	Expression rewrite(ExtendedExpression src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds);

	/**
	 * Rewrite the extended predicate node.
	 * <p>
	 * Since this node can be a leaf, it must be rebuilt with the rewriter
	 * factory if the node factory is different whatever the node content is
	 * modified or not.
	 * </p>
	 * 
	 * @param src
	 *            the extended predicate node to be rewritten
	 * @return the rewritten node
	 */
	Predicate rewrite(ExtendedPredicate src, boolean changed,
			Expression[] newChildExprs, Predicate[] newChildPreds);

}
