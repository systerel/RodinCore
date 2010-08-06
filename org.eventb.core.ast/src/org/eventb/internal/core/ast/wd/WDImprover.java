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
package org.eventb.internal.core.ast.wd;

import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;

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
import org.eventb.core.ast.FormulaFactory;
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
 * Visitor that creates a new tree made up of predicates, implications,
 * conjunctions and universal quantifiers , while traversing the whole AST. This
 * tree will be simplified using a list of redundant predicates and
 * optimizations, and will be used to build a new AST.
 */
public class WDImprover implements ISimpleVisitor2 {

	private final FormulaBuilder fb;

	/**
	 * Result of last visit. This field must never be read directly, but through
	 * method <code>nodeFor()</code>. Moreover, every <code>visitXXX()</code>
	 * method must set this variable (or raise an exception).
	 */
	private Node node;

	public WDImprover(FormulaFactory formulaFactory) {
		this.fb = new FormulaBuilder(formulaFactory);
	}

	/**
	 * Simplifies the given formula
	 * 
	 * @param formula
	 *            a formula to simplify
	 * @return simplified formula
	 */

	public Predicate improve(Predicate formula) {

		// Build a new tree while visiting the AST
		final Node root = nodeFor(formula);
		// Simplifies the tree and obtains a new AST
		return root.simplifyTree(fb);
	}

	private Node nodeFor(Predicate pred) {
		pred.accept(this);
		return node;
	}

	private Node[] nodesFor(Predicate[] preds) {
		final int length = preds.length;
		final Node[] result = new Node[length];
		for (int i = 0; i < length; i++) {
			result[i] = nodeFor(preds[i]);
		}
		return result;
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		assert false;
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		if (predicate.getTag() == LAND) {
			node = new NodeLand(nodesFor(predicate.getChildren()));
		} else {
			node = new NodePred(predicate);
		}
	}

	@Override
	public void visitAtomicExpression(AtomicExpression expression) {
		assert false;
	}

	@Override
	public void visitBecomesEqualTo(BecomesEqualTo assignment) {
		assert false;

	}

	@Override
	public void visitBecomesMemberOf(BecomesMemberOf assignment) {
		assert false;
	}

	@Override
	public void visitBecomesSuchThat(BecomesSuchThat assignment) {
		assert false;
	}

	@Override
	public void visitBinaryExpression(BinaryExpression expression) {
		assert false;

	}

	@Override
	public void visitBinaryPredicate(BinaryPredicate predicate) {
		if (predicate.getTag() == LIMP) {
			final Node left = nodeFor(predicate.getLeft());
			final Node right = nodeFor(predicate.getRight());
			node = new NodeLimp(left, right);
		} else {
			node = new NodePred(predicate);
		}
	}

	@Override
	public void visitBoolExpression(BoolExpression expression) {
		assert false;
	}

	@Override
	public void visitBoundIdentDecl(BoundIdentDecl boundIdentDecl) {
		assert false;
	}

	@Override
	public void visitBoundIdentifier(BoundIdentifier identifierExpression) {
		assert false;
	}

	@Override
	public void visitFreeIdentifier(FreeIdentifier identifierExpression) {
		assert false;
	}

	@Override
	public void visitIntegerLiteral(IntegerLiteral expression) {
		assert false;
	}

	@Override
	public void visitLiteralPredicate(LiteralPredicate predicate) {
		node = new NodePred(predicate);
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		node = new NodePred(predicate);
	}

	@Override
	public void visitPredicateVariable(PredicateVariable predVar) {
		assert false;
	}

	@Override
	public void visitQuantifiedExpression(QuantifiedExpression expression) {
		assert false;
	}

	@Override
	public void visitQuantifiedPredicate(QuantifiedPredicate predicate) {
		if (predicate.getTag() == FORALL) {
			final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
			final Predicate child = predicate.getPredicate();
			node = new NodeForAll(decls, nodeFor(child));
		} else {
			node = new NodePred(predicate);
		}
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		node = new NodePred(predicate);
	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		assert false;
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		node = new NodePred(predicate);
	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		assert false;
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		node = new NodePred(predicate);
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		node = new NodePred(predicate);
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		assert false;
	}

}