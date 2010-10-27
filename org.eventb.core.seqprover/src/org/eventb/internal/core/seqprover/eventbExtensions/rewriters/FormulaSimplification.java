/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added simplification for OVR, removed checkForallOnePointRune
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Collections.singleton;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryPredicate;

public class FormulaSimplification {
	
	private FormulaFactory factory;
	
	public FormulaSimplification(FormulaFactory factory) {
		this.factory = factory;
	}

	private <T extends Formula<T>> Collection<T> simplifiedAssociativeFormula(
			T[] children, T neutral, T determinant, boolean eliminateDuplicate) {
		Collection<T> formulas;
		if (eliminateDuplicate)
			formulas = new LinkedHashSet<T>();
		else
			formulas = new ArrayList<T>();

		for (T child : children) {
			if (child.equals(determinant)) {
				return singleton(determinant);
			}

			if (!child.equals(neutral)) {
				if (child instanceof Predicate) {
					final Predicate pred = (Predicate) child;
					final Predicate negation;
					if (pred instanceof UnaryPredicate
							&& pred.getTag() == Predicate.NOT) {
						negation = ((UnaryPredicate) pred).getChild();
					} else {
						negation = factory.makeUnaryPredicate(Predicate.NOT,
								pred, null);
					}
					if (formulas.contains(negation)) {
						return singleton(determinant);
					}
					formulas.add(child);
				} else {
					formulas.add(child);
				}
			}
		}
		return formulas;
	}

	public Predicate simplifyAssociativePredicate(
			AssociativePredicate predicate, Predicate[] children,
			Predicate neutral, Predicate determinant) {

		Collection<Predicate> predicates = simplifiedAssociativeFormula(
				children, neutral, determinant, true);
		if (predicates.size() != children.length) {
			if (predicates.size() == 0)
				return neutral;

			if (predicates.size() == 1)
				return predicates.iterator().next();

			AssociativePredicate newPred = factory.makeAssociativePredicate(
					predicate.getTag(), predicates, null);
			return newPred;
		}
		return predicate;
	}

	public Expression simplifyAssociativeExpression(
			AssociativeExpression expression, Expression[] children) {
		int tag = expression.getTag();

		final Expression neutral;
		final Expression determinant;
		final boolean eliminateDuplicate;
		final Expression number0 = factory.makeIntegerLiteral(BigInteger.ZERO, null);
		final Expression number1 = factory.makeIntegerLiteral(BigInteger.ONE, null);
		switch (tag) {
		case Expression.BUNION:
			neutral = factory.makeEmptySet(expression.getType(), null);
			determinant = expression.getType().getBaseType().toExpression(factory);
			eliminateDuplicate = true;
			break;
		case Expression.BINTER:
			neutral = expression.getType().getBaseType().toExpression(factory);
			determinant = factory.makeEmptySet(expression.getType(), null);
			eliminateDuplicate = true;
			break;
		case Expression.PLUS:
			neutral = number0;
			determinant = null;
			eliminateDuplicate = false;
			break;
		case Expression.MUL:
			neutral = number1;
			determinant = number0;
			eliminateDuplicate = false;
			break;
		case Expression.OVR:
			neutral = factory.makeEmptySet(expression.getType(), null);
			determinant = null;
			eliminateDuplicate = false;
			break;
		default:
			assert false;
			return expression;
		}

		Collection<Expression> expressions = simplifiedAssociativeFormula(
				children, neutral, determinant, eliminateDuplicate);

		if (expressions.size() != children.length) {
			if (expressions.size() == 0)
				return neutral;

			if (expressions.size() == 1)
				return expressions.iterator().next();

			AssociativeExpression newExpression = factory.makeAssociativeExpression(
					tag, expressions, null);
			return newExpression;
		}
		return expression;
	}

	public Expression getFaction(Expression E, Expression F) {
		return factory.makeBinaryExpression(Expression.DIV, E, F, null);
	}

	public Expression getFaction(Expression expression, BigInteger E,
			Expression F) {
		switch (E.signum()) {
		case -1:
			final Expression minusE = factory.makeIntegerLiteral(E.negate(), null);
			return factory.makeBinaryExpression(Expression.DIV, minusE, F, null);
		case 0:
			return factory.makeIntegerLiteral(E, null);
		default:
			return expression;
		}
	}

	public Expression getFaction(Expression expression, 
			Expression E, BigInteger F) {
		if (F.signum() < 0) {
			final Expression minusF = factory.makeIntegerLiteral(F.negate(), null);
			return factory.makeBinaryExpression(Expression.DIV, E, minusF, null);
		}
		if (F.equals(BigInteger.ONE) ) {
			return factory.makeUnaryExpression(Expression.UNMINUS, E, null);
		}
		return expression;
	}

	public Expression getFaction(Expression expression, 
			BigInteger E, BigInteger F) {
		if (E.signum() == 0)
			return factory.makeIntegerLiteral(BigInteger.ZERO, null);
		if (F.equals(BigInteger.ONE)) {
			return factory.makeIntegerLiteral(E, null);
		}
		if (E.signum() < 0 && F.signum() < 0) {
			final Expression minusE = factory.makeIntegerLiteral(E.negate(), null);
			final Expression minusF = factory.makeIntegerLiteral(F.negate(), null);
			return factory.makeBinaryExpression(Expression.DIV, minusE, minusF, null);
		}
		return expression;
	}

}
