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
package org.eventb.internal.ui.utils;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor2;
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
 * Implementation of a simple visitor for computing the height of a predicate as
 * used in the Prover UI pretty-print. The visitor is private and a static
 * method is provided for using the visitor.
 * 
 * @author Laurent Voisin
 */
public class PredicateHeightComputer implements ISimpleVisitor2 {

	private static final PredicateHeightComputer INSTANCE = new PredicateHeightComputer();

	/**
	 * Returns the height of the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return the height of the given predicate
	 */
	public static int getHeight(Predicate pred) {
		INSTANCE.height = 0;
		pred.accept(INSTANCE);
		return INSTANCE.height;
	}

	// Height of the last visited node
	int height;

	private PredicateHeightComputer() {
		this.height = 0;
	}

	public void visitAssociativePredicate(AssociativePredicate predicate) {
		int maxChildHeight = 0;
		for (Predicate child : predicate.getChildren()) {
			child.accept(this);
			if (height > maxChildHeight) {
				maxChildHeight = height;
			}
		}
		height = maxChildHeight + 1;
	}

	public void visitBinaryPredicate(BinaryPredicate predicate) {
		predicate.getLeft().accept(this);
		final int leftHeight = height;
		predicate.getRight().accept(this);
		height = height > leftHeight ? height + 1 : leftHeight + 1;
	}

	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		predicate.getPredicate().accept(this);
		// No height change through this node
	}

	public void visitUnaryPredicate(UnaryPredicate predicate) {
		predicate.getChild().accept(this);
		++height;
	}

	public void commonVisit() {
		height = 0;
	}

	public void visitPredicateVariable(PredicateVariable predVar) {
		commonVisit();
	}

	public void visitAssociativeExpression(AssociativeExpression expression) {
		commonVisit();
	}

	public void visitAtomicExpression(AtomicExpression expression) {
		commonVisit();
	}

	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		commonVisit();
	}

	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		commonVisit();
	}

	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		commonVisit();
	}

	public void visitBinaryExpression(BinaryExpression expression) {
		commonVisit();
	}

	public void visitBoolExpression(BoolExpression expression) {
		commonVisit();
	}

	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		commonVisit();
	}

	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		commonVisit();
	}

	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		commonVisit();
	}

	public void visitIntegerLiteral(IntegerLiteral expression) {
		commonVisit();
	}

	public void visitLiteralPredicate(LiteralPredicate predicate) {
		commonVisit();
	}

	public void visitMultiplePredicate(MultiplePredicate predicate) {
		commonVisit();
	}

	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		commonVisit();
	}

	public void visitRelationalPredicate(RelationalPredicate predicate) {
		commonVisit();
	}

	public void visitSetExtension(SetExtension expression) {
		commonVisit();
	}

	public void visitSimplePredicate(SimplePredicate predicate) {
		commonVisit();
	}

	public void visitUnaryExpression(UnaryExpression expression) {
		commonVisit();
	}

	public void visitExtendedExpression(ExtendedExpression expression) {
		commonVisit();
	}

	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		commonVisit();
	}

}
