/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
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
 * Default class implementing ISimpleVisitor. All methods are empty.
 * <p>
 * This class is intended to be extended by clients, adding useful behavior.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 1.0
 */
public class DefaultSimpleVisitor implements ISimpleVisitor {

	public void visitAssociativeExpression(AssociativeExpression expression) {
		// Do nothing
	}

	public void visitAssociativePredicate(AssociativePredicate predicate) {
		// Do nothing
	}

	public void visitAtomicExpression(AtomicExpression expression) {
		// Do nothing
	}

	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		// Do nothing
	}

	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		// Do nothing
	}

	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		// Do nothing
	}

	public void visitBinaryExpression(BinaryExpression expression) {
		// Do nothing
	}

	public void visitBinaryPredicate(BinaryPredicate predicate) {
		// Do nothing
	}

	public void visitBoolExpression(BoolExpression expression) {
		// Do nothing
	}

	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		// Do nothing
	}

	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		// Do nothing
	}

	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		// Do nothing
	}

	public void visitIntegerLiteral(IntegerLiteral expression) {
		// Do nothing
	}

	public void visitLiteralPredicate(LiteralPredicate predicate) {
		// Do nothing
	}

	public void visitMultiplePredicate(MultiplePredicate predicate) {
		// Do nothing
	}

	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		// Do nothing
	}

	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		// Do nothing
	}

	public void visitRelationalPredicate(RelationalPredicate predicate) {
		// Do nothing
	}

	public void visitSetExtension(SetExtension expression) {
		// Do nothing
	}

	public void visitSimplePredicate(SimplePredicate predicate) {
		// Do nothing
	}

	public void visitUnaryExpression(UnaryExpression expression) {
		// Do nothing
	}

	public void visitUnaryPredicate(UnaryPredicate predicate) {
		// Do nothing
	}

}
