/*******************************************************************************
 * Copyright (c) 2008, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added support for predicate variables
 *     Systerel - added support for mathematical extensions
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Default class implementing ISimpleVisitor. All methods are empty.
 * <p>
 * This class is intended to be extended by clients, adding useful behavior.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public class DefaultSimpleVisitor implements ISimpleVisitor2 {

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		// Do nothing
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		// Do nothing
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		// Do nothing
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		// Do nothing
	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		// Do nothing
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		// Do nothing
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		// Do nothing
	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		// Do nothing
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		// Do nothing
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		// Do nothing
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		// Do nothing
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		// Do nothing
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		// Do nothing
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		// Do nothing
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		// Do nothing
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void visitPredicateVariable(PredicateVariable predVar) {
		// Do nothing
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		// Do nothing
	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		// Do nothing
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		// Do nothing
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		// Do nothing
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		// Do nothing
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		// Do nothing
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		// Do nothing
	}

	/**
	 * @since 2.0
	 */
	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		// Do nothing
	}

	/**
	 * @since 2.0
	 */
	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		// Do nothing
	}

}
