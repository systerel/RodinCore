/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
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
 * Simplified version of the visitor. This visitor does not support predicate
 * variables. See {@link IVisitor2}. Here, the corresponding accept method only
 * performs a call of the visit method. AST traversal must be managed within the
 * visit methods of the implementer of this class.
 * 
 * <p>
 * This interface is intended to be implemented by clients who contribute new
 * visitors.
 * </p>
 * 
 * @see Formula#accept(ISimpleVisitor)
 * @see DefaultSimpleVisitor
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public interface ISimpleVisitor {

	/**
	 * Visits the given assignment node.
	 * 
	 * @param assignment
	 *            the assignment node to visit
	 */
	void visitBecomesEqualTo(BecomesEqualTo assignment);

	/**
	 * Visits the given assignment node.
	 * 
	 * @param assignment
	 *            the assignment node to visit
	 */
	void visitBecomesMemberOf(BecomesMemberOf assignment);

	/**
	 * Visits the given assignment node.
	 * 
	 * @param assignment
	 *            the assignment node to visit
	 */
	void visitBecomesSuchThat(BecomesSuchThat assignment);

	/**
	 * Visits the given declaration node.
	 * 
	 * @param boundIdentDecl
	 *            the declaration node to visit
	 */
	void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitAssociativeExpression(AssociativeExpression expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitAtomicExpression(AtomicExpression expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitBinaryExpression(BinaryExpression expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitBoolExpression(BoolExpression expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitIntegerLiteral(IntegerLiteral expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitQuantifiedExpression(QuantifiedExpression expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitSetExtension(SetExtension expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param expression
	 *            the expression node to visit
	 */
	void visitUnaryExpression(UnaryExpression expression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param identifierExpression
	 *            the identifier expression node to visit
	 */
	void visitBoundIdentifier(BoundIdentifier identifierExpression);

	/**
	 * Visits the given expression node.
	 * 
	 * @param identifierExpression
	 *            the identifier expression node to visit
	 */
	void visitFreeIdentifier(FreeIdentifier identifierExpression);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitAssociativePredicate(AssociativePredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitBinaryPredicate(BinaryPredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitLiteralPredicate(LiteralPredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitMultiplePredicate(MultiplePredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitQuantifiedPredicate(QuantifiedPredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitRelationalPredicate(RelationalPredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitSimplePredicate(SimplePredicate predicate);

	/**
	 * Visits the given predicate node.
	 * 
	 * @param predicate
	 *            the predicate node to visit
	 */
	void visitUnaryPredicate(UnaryPredicate predicate);

	/**
	 * @since 2.0
	 */
	void visitExtendedExpression(ExtendedExpression expression);

	/**
	 * @since 2.0
	 */
	void visitExtendedPredicate(ExtendedPredicate predicate);

}
