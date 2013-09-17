/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.utils;

import static java.util.Collections.singletonList;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

/**
 * Generates variations of a given predicate, according to different criteria.
 * <p>
 * Implementation notes : When working with integer relations, we reduce all
 * cases to less-than positive operators. We do not process contracted operators
 * (such as <code>NOTEQUAL</code>), assuming they have been removed by the
 * normalization.
 * </p>
 * 
 * @author Josselin Dolhen
 */
public class Variations {

	/**
	 * Returns a list of predicates <code>Q</code> such that
	 * 
	 * <pre>
	 * P => Q
	 * </pre>
	 * 
	 * where <code>P</code> is some predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of predicates weaker than the given predicate
	 */
	public static List<Predicate> getWeakerPositive(Predicate pred) {
		if (isNeg(pred)) {
			return getWeakerNegative(makeNeg(pred));
		}
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final Expression lhs = relPred.getLeft();
			final Expression rhs = relPred.getRight();
			return getWeakerPositiveRelational(relPred.getTag(), lhs, rhs);
		}
		return singletonList(pred);
	}

	private static List<Predicate> getWeakerPositiveRelational(int tag,
			Expression lhs, Expression rhs) {
		// Reduce to less than relations
		switch (tag) {
		case GE:
			return getWeakerPositiveRelational(LE, rhs, lhs);
		case GT:
			return getWeakerPositiveRelational(LT, rhs, lhs);
		}
		final List<Predicate> variations = new ArrayList<Predicate>();
		addEquivalentPositiveRelational(variations, tag, lhs, rhs);
		switch (tag) {
		case EQUAL:
			if (isSet(rhs)) {
				variations.add(rel(SUBSETEQ, lhs, rhs));
				variations.add(rel(SUBSETEQ, rhs, lhs));
				variations.add(makeNeg(rel(SUBSET, lhs, rhs)));
				variations.add(makeNeg(rel(SUBSET, rhs, lhs)));
			} else if (isInteger(rhs)) {
				variations.add(rel(LE, lhs, rhs));
				variations.add(rel(GE, rhs, lhs));
				variations.add(rel(LE, rhs, lhs));
				variations.add(rel(GE, lhs, rhs));
			}
			break;
		case LE:
			// Nothing to add
			break;
		case LT:
			variations.add(rel(LE, lhs, rhs));
			variations.add(rel(GE, rhs, lhs));
			variations.add(makeNeg(rel(EQUAL, lhs, rhs)));
			variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
			break;
		case SUBSET:
			variations.add(rel(SUBSETEQ, lhs, rhs));
			variations.add(makeNeg(rel(EQUAL, lhs, rhs)));
			variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
			variations.add(makeNeg(rel(SUBSET, rhs, lhs)));
			variations.add(makeNeg(rel(SUBSETEQ, rhs, lhs)));
			break;
		case SUBSETEQ:
			variations.add(makeNeg(rel(SUBSET, rhs, lhs)));
			break;
		}
		return variations;
	}

	/**
	 * Returns a list of predicates <code>Q</code> such that
	 * 
	 * <pre>
	 * ¬ P => Q
	 * </pre>
	 * 
	 * where <code>P</code> is the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of predicates weaker than the negation of the input
	 *         predicate
	 */
	public static List<Predicate> getWeakerNegative(Predicate pred) {
		if (isNeg(pred)) {
			return getWeakerPositive(makeNeg(pred));
		}
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final Expression lhs = relPred.getLeft();
			final Expression rhs = relPred.getRight();
			return getWeakerNegativeRelational(relPred.getTag(), lhs, rhs);
		}
		return singletonList(makeNeg(pred));
	}

	private static List<Predicate> getWeakerNegativeRelational(int tag,
			Expression lhs, Expression rhs) {
		// Reduce to positive relations
		switch (tag) {
		case LE:
			return getWeakerPositiveRelational(LT, rhs, lhs);
		case GE:
			return getWeakerPositiveRelational(LT, lhs, rhs);
		case LT:
			return getWeakerPositiveRelational(LE, rhs, lhs);
		case GT:
			return getWeakerPositiveRelational(LE, lhs, rhs);
		}
		final List<Predicate> variations = new ArrayList<Predicate>();
		addEquivalentNegativeRelational(variations, tag, lhs, rhs);
		switch (tag) {
		case EQUAL:
			variations.add(makeNeg(rel(EQUAL, lhs, rhs)));
			variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
			break;
		case SUBSET:
			variations.add(makeNeg(rel(SUBSET, lhs, rhs)));
			break;
		case SUBSETEQ:
			variations.add(makeNeg(rel(SUBSET, lhs, rhs)));
			variations.add(makeNeg(rel(SUBSETEQ, lhs, rhs)));
			variations.add(makeNeg(rel(EQUAL, lhs, rhs)));
			variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
			break;
		}
		return variations;
	}

	/**
	 * Returns a list of predicates <code>Q</code> such that
	 * 
	 * <pre>
	 * Q => P
	 * </pre>
	 * 
	 * where <code>P</code> is some predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of predicates stronger than the given predicate
	 */
	public static List<Predicate> getStrongerPositive(Predicate pred) {
		if (isNeg(pred)) {
			return getStrongerNegative(makeNeg(pred));
		}
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final Expression lhs = relPred.getLeft();
			final Expression rhs = relPred.getRight();
			return getStrongerPositiveRelational(relPred.getTag(), lhs, rhs);
		}
		return singletonList(pred);
	}

	private static List<Predicate> getStrongerPositiveRelational(int tag,
			Expression lhs, Expression rhs) {
		// Reduce to less than relations
		switch (tag) {
		case GE:
			return getStrongerPositiveRelational(LE, rhs, lhs);
		case GT:
			return getStrongerPositiveRelational(LT, rhs, lhs);
		}
		final List<Predicate> variations = new ArrayList<Predicate>();
		addEquivalentPositiveRelational(variations, tag, lhs, rhs);
		switch (tag) {
		case EQUAL:
			// Nothing to add
			break;
		case LE:
			variations.add(rel(LT, lhs, rhs));
			variations.add(rel(GT, rhs, lhs));
			variations.add(rel(EQUAL, lhs, rhs));
			variations.add(rel(EQUAL, rhs, lhs));
			break;
		case LT:
			// Nothing to add
			break;
		case SUBSET:
			// Nothing to add
			break;
		case SUBSETEQ:
			variations.add(rel(SUBSET, lhs, rhs));
			variations.add(rel(EQUAL, lhs, rhs));
			variations.add(rel(EQUAL, rhs, lhs));
			break;
		}
		return variations;
	}

	/**
	 * Returns a list of predicates <code>Q</code> such that
	 * 
	 * <pre>
	 * Q => ¬ P
	 * </pre>
	 * 
	 * where <code>P</code> is the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of predicates stronger than the negation of the input
	 *         predicate
	 */
	public static List<Predicate> getStrongerNegative(Predicate pred) {
		if (isNeg(pred)) {
			return getStrongerPositive(makeNeg(pred));
		}
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final Expression lhs = relPred.getLeft();
			final Expression rhs = relPred.getRight();
			return getStrongerNegativeRelational(relPred.getTag(), lhs, rhs);
		}
		return singletonList(makeNeg(pred));
	}

	private static List<Predicate> getStrongerNegativeRelational(int tag,
			Expression lhs, Expression rhs) {
		// Reduce to positive relations
		switch (tag) {
		case LE:
			return getStrongerPositiveRelational(LT, rhs, lhs);
		case GE:
			return getStrongerPositiveRelational(LT, lhs, rhs);
		case LT:
			return getStrongerPositiveRelational(LE, rhs, lhs);
		case GT:
			return getStrongerPositiveRelational(LE, lhs, rhs);
		}
		final List<Predicate> variations = new ArrayList<Predicate>();
		addEquivalentNegativeRelational(variations, tag, lhs, rhs);
		switch (tag) {
		case EQUAL:
			if (isSet(rhs)) {
				variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
				variations.add(makeNeg(rel(SUBSETEQ, rhs, lhs)));
				variations.add(makeNeg(rel(SUBSETEQ, lhs, rhs)));
				variations.add(rel(SUBSET, lhs, rhs));
				variations.add(rel(SUBSET, rhs, lhs));
			} else if (isInteger(rhs)) {
				variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
				variations.add(rel(GT, lhs, rhs));
				variations.add(rel(LT, rhs, lhs));
				variations.add(rel(LT, lhs, rhs));
				variations.add(rel(GT, rhs, lhs));
			}
			break;
		case SUBSET:
			variations.add(rel(SUBSET, rhs, lhs));
			variations.add(rel(SUBSETEQ, rhs, lhs));
			variations.add(rel(EQUAL, lhs, rhs));
			variations.add(rel(EQUAL, rhs, lhs));
			variations.add(makeNeg(rel(SUBSETEQ, lhs, rhs)));
			break;
		case SUBSETEQ:
			variations.add(rel(SUBSET, rhs, lhs));
			break;
		}
		return variations;
	}

	/**
	 * Returns a list of predicates equivalent to the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of equivalent predicates
	 */
	public static List<Predicate> getEquivalent(Predicate pred) {
		if (isNeg(pred)) {
			return getEquivalentNegative(makeNeg(pred));
		}
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final int tag = relPred.getTag();
			final Expression lhs = relPred.getLeft();
			final Expression rhs = relPred.getRight();
			final List<Predicate> variations = new ArrayList<Predicate>();
			addEquivalentPositiveRelational(variations, tag, lhs, rhs);
			return variations;
		}
		return singletonList(pred);
	}

	private static void addEquivalentPositiveRelational(
			List<Predicate> variations, int tag, Expression lhs, Expression rhs) {
		variations.add(rel(tag, lhs, rhs));
		switch (tag) {
		case EQUAL:
			variations.add(rel(EQUAL, rhs, lhs));
			break;
		case LT:
			variations.add(rel(GT, rhs, lhs));
			break;
		case LE:
			variations.add(rel(GE, rhs, lhs));
			break;
		case GT:
			variations.add(rel(LT, rhs, lhs));
			break;
		case GE:
			variations.add(rel(LE, rhs, lhs));
			break;
		}
	}

	/**
	 * Returns a list of predicates equivalent to the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of equivalent predicates
	 */
	public static List<Predicate> getEquivalentNegative(Predicate pred) {
		if (isNeg(pred)) {
			return getEquivalent(makeNeg(pred));
		}
		if (pred instanceof RelationalPredicate) {
			final RelationalPredicate relPred = (RelationalPredicate) pred;
			final int tag = relPred.getTag();
			final Expression lhs = relPred.getLeft();
			final Expression rhs = relPred.getRight();
			final List<Predicate> variations = new ArrayList<Predicate>();
			addEquivalentNegativeRelational(variations, tag, lhs, rhs);
			return variations;
		}
		return singletonList(makeNeg(pred));

	}

	private static void addEquivalentNegativeRelational(
			List<Predicate> variations, int tag, Expression lhs, Expression rhs) {
		switch (tag) {
		case EQUAL:
			variations.add(makeNeg(rel(EQUAL, lhs, rhs)));
			variations.add(makeNeg(rel(EQUAL, rhs, lhs)));
			return;
		case LT:
			variations.add(rel(GE, lhs, rhs));
			variations.add(rel(LE, rhs, lhs));
			return;
		case LE:
			variations.add(rel(GT, lhs, rhs));
			variations.add(rel(LT, rhs, lhs));
			return;
		case GT:
			variations.add(rel(LE, lhs, rhs));
			variations.add(rel(GE, rhs, lhs));
			return;
		case GE:
			variations.add(rel(LT, lhs, rhs));
			variations.add(rel(GT, rhs, lhs));
			return;
		}
		variations.add(makeNeg(rel(tag, lhs, rhs)));
	}

	private static boolean isSet(Expression expr) {
		return expr.getType() instanceof PowerSetType;
	}

	private static boolean isInteger(Expression rhs) {
		return rhs.getType() instanceof IntegerType;
	}

	private static RelationalPredicate rel(int tag, Expression left,
			Expression right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

}
