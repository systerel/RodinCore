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

package org.eventb.internal.core.seqprover.proofBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eventb.core.ast.ISealedTypeEnvironment;
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
 * Visitor that decomposes a predicate into a list of subgoals. Clients shall
 * use this class with <code>new PredicateDecomposer().decompose(formula)</code>
 * .
 * 
 * @since 2.0
 */

public class PredicateDecomposer implements ISimpleVisitor2 {

	private final Set<Predicate> subGoals;
	private ISealedTypeEnvironment env;

	public PredicateDecomposer(ISealedTypeEnvironment env) {
		this.subGoals = new HashSet<Predicate>();
		this.env = env;
	}

	/**
	 * Decompose a predicate into a list of subgoals.
	 * 
	 * @param pred
	 * @return list of the subgoals from the given predicate
	 */
	public List<Predicate> decompose(Predicate pred) {
		subGoals.clear();
		pred.accept(this);
		return new ArrayList<Predicate>(subGoals);
	}

	@Override
	public void visitAssociativeExpression(AssociativeExpression expression) {
		assert false;
	}

	@Override
	public void visitAssociativePredicate(AssociativePredicate predicate) {
		Predicate[] predChildren = predicate.getChildren();
		for (Predicate predChild : predChildren) {
			predChild.accept(this);
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
		final Predicate left = predicate.getLeft();
		final Predicate right = predicate.getRight();
		left.accept(this);
		right.accept(this);
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
		subGoals.add(predicate);
	}

	@Override
	public void visitMultiplePredicate(MultiplePredicate predicate) {
		subGoals.add(predicate);
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
		final FormulaFactory ff = env.getFormulaFactory();
		final FreeIdentifier[] freeIdents = env.makeBuilder()
				.makeFreshIdentifiers(predicate.getBoundIdentDecls());
		final Predicate newpred = predicate.instantiate(freeIdents, ff);
		newpred.accept(this);
	}

	@Override
	public void visitRelationalPredicate(RelationalPredicate predicate) {
		subGoals.add(predicate);

	}

	@Override
	public void visitSetExtension(SetExtension expression) {
		assert false;
	}

	@Override
	public void visitSimplePredicate(SimplePredicate predicate) {
		subGoals.add(predicate);

	}

	@Override
	public void visitUnaryExpression(UnaryExpression expression) {
		assert false;
	}

	@Override
	public void visitUnaryPredicate(UnaryPredicate predicate) {
		Predicate child = predicate.getChild();
		child.accept(this);
	}

	@Override
	public void visitExtendedPredicate(ExtendedPredicate predicate) {
		subGoals.add(predicate);
	}

	@Override
	public void visitExtendedExpression(ExtendedExpression expression) {
		assert false;
	}

}
