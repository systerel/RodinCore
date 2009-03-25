/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.UnaryPredicate;

public class FormulaSimplification {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	private static <T extends Formula<T>> Collection<T> simplifiedAssociativeFormula(
			T[] children, T neutral, T determinant, boolean eliminateDuplicate) {
		Collection<T> formulas;
		if (eliminateDuplicate)
			formulas = new LinkedHashSet<T>();
		else
			formulas = new ArrayList<T>();

		for (T child : children) {
			if (child.equals(determinant)) {
				formulas = new ArrayList<T>();
				formulas.add(determinant);
				return formulas;
			}

			if (!child.equals(neutral)) {
				if (child instanceof Predicate) {
					final Predicate pred = (Predicate) child;
					final Predicate negation;
					if (pred instanceof UnaryPredicate
							&& pred.getTag() == Predicate.NOT) {
						negation = ((UnaryPredicate) pred).getChild();
					} else {
						negation = ff.makeUnaryPredicate(Predicate.NOT,
								pred, null);
					}
					if (formulas.contains(negation)) {
						formulas = new ArrayList<T>();
						formulas.add(determinant);
						return formulas;
					} else {
						formulas.add(child);
					}
				} else {
					formulas.add(child);
				}
			}
		}
		return formulas;
	}

	public static Predicate simplifyAssociativePredicate(
			AssociativePredicate predicate, Predicate[] children,
			Predicate neutral, Predicate determinant) {

		Collection<Predicate> predicates = simplifiedAssociativeFormula(
				children, neutral, determinant, true);
		if (predicates.size() != children.length) {
			if (predicates.size() == 0)
				return neutral;

			if (predicates.size() == 1)
				return predicates.iterator().next();

			AssociativePredicate newPred = ff.makeAssociativePredicate(
					predicate.getTag(), predicates, null);
			return newPred;
		}
		return predicate;
	}

	public static Expression simplifyAssociativeExpression(
			AssociativeExpression expression, Expression[] children) {
		int tag = expression.getTag();

		final Expression neutral;
		final Expression determinant;
		final Expression number0 = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		final Expression number1 = ff.makeIntegerLiteral(BigInteger.ONE, null);
		switch (tag) {
		case Expression.BUNION:
			neutral = ff.makeEmptySet(expression.getType(), null);
			determinant = expression.getType().getBaseType().toExpression(ff);
			break;
		case Expression.BINTER:
			neutral = expression.getType().getBaseType().toExpression(ff);
			determinant = ff.makeEmptySet(expression.getType(), null);
			break;
		case Expression.PLUS:
			neutral = number0;
			determinant = null;
			break;
		case Expression.MUL:
			neutral = number1;
			determinant = number0;
			break;
		default:
			assert false;
			return expression;
		}
		boolean eliminateDuplicate = (tag == Expression.BUNION || tag == Expression.BINTER);

		Collection<Expression> expressions = simplifiedAssociativeFormula(
				children, neutral, determinant, eliminateDuplicate);

		if (expressions.size() != children.length) {
			if (expressions.size() == 0)
				return neutral;

			if (expressions.size() == 1)
				return expressions.iterator().next();

			AssociativeExpression newExpression = ff.makeAssociativeExpression(
					tag, expressions, null);
			return newExpression;
		}
		return expression;
	}

	public static Expression getFaction(Expression E, Expression F) {
		return ff.makeBinaryExpression(Expression.DIV, E, F, null);
	}

	public static Expression getFaction(Expression expression, BigInteger E,
			Expression F) {
		switch (E.signum()) {
		case -1:
			final Expression minusE = ff.makeIntegerLiteral(E.negate(), null);
			return ff.makeBinaryExpression(Expression.DIV, minusE, F, null);
		case 0:
			return ff.makeIntegerLiteral(E, null);
		default:
			return expression;
		}
	}

	public static Expression getFaction(Expression expression, 
			Expression E, BigInteger F) {
		if (F.signum() < 0) {
			final Expression minusF = ff.makeIntegerLiteral(F.negate(), null);
			return ff.makeBinaryExpression(Expression.DIV, E, minusF, null);
		}
		if (F.equals(BigInteger.ONE) ) {
			return ff.makeUnaryExpression(Expression.UNMINUS, E, null);
		}
		return expression;
	}

	public static Expression getFaction(Expression expression, 
			BigInteger E, BigInteger F) {
		if (E.signum() == 0)
			return ff.makeIntegerLiteral(BigInteger.ZERO, null);
		if (F.equals(BigInteger.ONE)) {
			return ff.makeIntegerLiteral(E, null);
		}
		if (E.signum() < 0 && F.signum() < 0) {
			final Expression minusE = ff.makeIntegerLiteral(E.negate(), null);
			final Expression minusF = ff.makeIntegerLiteral(F.negate(), null);
			return ff.makeBinaryExpression(Expression.DIV, minusE, minusF, null);
		}
		return expression;
	}

	public static Predicate checkForAllOnePointRule(Predicate predicate,
			BoundIdentDecl[] identDecls, Predicate[] children, Predicate R) {
		for (Predicate child : children) {
			if (child instanceof RelationalPredicate
					&& child.getTag() == Predicate.EQUAL) {
				RelationalPredicate rPred = (RelationalPredicate) child;
				Expression left = rPred.getLeft();
				if (left instanceof BoundIdentifier) {
					BoundIdentifier y = (BoundIdentifier) left;
					Expression right = rPred.getRight();
					BoundIdentifier[] boundIdentifiers = right
							.getBoundIdentifiers();
					if (y.getBoundIndex() < identDecls.length
							&& !contain(boundIdentifiers, y)) {
						// TODO Do the subtitution here
						return predicate;
						// return subtitute(rPred, identDecls, children, R);

					}
				}
			}
		}
		return predicate;
	}

	// private static Predicate subtitute(RelationalPredicate pred,
	// BoundIdentDecl[] identDecls, Predicate[] children, Predicate r) {
	// Collection<Predicate> predicates = new ArrayList<Predicate>();
	//
	// for (Predicate child : children) {
	// if (!child.equals(pred)) {
	// predicates.add(child);
	// }
	// }
	// AssociativePredicate left = ff.makeAssociativePredicate(Predicate.LAND,
	// predicates, null);
	//
	// BinaryPredicate innerPred = ff.makeBinaryPredicate(Predicate.LIMP,
	// left, r, null);
	// BoundIdentifier y = ((BoundIdentifier) pred.getLeft());
	// QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
	// Predicate.FORALL, new BoundIdentDecl[] { y
	// .getDeclaration(identDecls) }, innerPred, null);
	// Predicate instantiate = qPred.instantiate(new Expression[] { pred
	// .getRight() }, ff);
	// return ff.makeQuantifiedPredicate(Predicate.FORALL, remove(identDecls,
	// y.getDeclaration(identDecls)), instantiate, null);
	// }


	private static <T extends Object> boolean contain(T[] array, T element) {
		for (T member : array) {
			if (member.equals(element))
				return true;
		}
		return false;
	}

}
