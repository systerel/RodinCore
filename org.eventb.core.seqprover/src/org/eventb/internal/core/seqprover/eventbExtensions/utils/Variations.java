/*******************************************************************************
 * Copyright (c) 2013, 2022 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Systerel - initial API and implementation
 * Université de Lorraine - level 1
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.utils;

import static java.util.Collections.singletonList;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.INTLIT;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.NATURAL;
import static org.eventb.core.ast.Formula.NATURAL1;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.Level.L0;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.Level.L1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
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
 * This class has two levels:
 * <ul>
 * <li>Level 0 is the initial implementation with general rules about
 * equalities, inequalities and subsets.</li>
 * <li>Level 1 adds variations between numeric comparisons and set membership to
 * NAT and NAT1.</li>
 * </ul>
 * 
 * @author Josselin Dolhen
 */
public class Variations {

	public static enum Level {
		L0, // Initial implementation
		L1; // Natural numbers

		public boolean from(Level other) {
			return this.ordinal() >= other.ordinal();
		}
	}

	private Level level;

	private Variations(Level level) {
		this.level = level;
	}

	private static final Variations INSTANCE_LEVEL_0 = new Variations(L0);

	private static final Variations INSTANCE_LEVEL_1 = new Variations(L1);

	/**
	 * Get an instance of this class for the provided level.
	 *
	 * @param level desired level
	 * @return instance of this class for the provided level
	 */
	public static Variations getInstance(Level level) {
		switch (level) {
		case L0:
			return INSTANCE_LEVEL_0;
		case L1:
			return INSTANCE_LEVEL_1;
		default:
			throw new IllegalArgumentException("Provided level is not a valid enumeration value");
		}
	}

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
	public List<Predicate> getWeakerPositive(Predicate pred) {
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

	private List<Predicate> getWeakerPositiveRelational(int tag,
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
		if (level.from(L1)) {
			switch (tag) {
			case LE:
			case LT:
				// Cases where the number is equal to zero are handled in equivalences
				if (isPositive(lhs)) {
					FormulaFactory ff = lhs.getFactory();
					variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL, null)));
					variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL1, null)));
				}
				break;
			case IN:
				if (rhs.getTag() == NATURAL1) {
					FormulaFactory ff = lhs.getFactory();
					variations.add(rel(IN, lhs, ff.makeAtomicExpression(NATURAL, null)));
					variations.add(makeNeg(rel(EQUAL, lhs, ff.makeIntegerLiteral(BigInteger.ZERO, null))));
				}
				break;
			}
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
	public List<Predicate> getWeakerNegative(Predicate pred) {
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

	private List<Predicate> getWeakerNegativeRelational(int tag,
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
	public List<Predicate> getStrongerPositive(Predicate pred) {
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

	private List<Predicate> getStrongerPositiveRelational(int tag,
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
		if (level.from(L1)) {
			switch (tag) {
			case LE:
			case LT:
				// Cases where the number is equal to zero are handled in equivalences
				if (isNegative(lhs)) {
					FormulaFactory ff = lhs.getFactory();
					variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL, null)));
					variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL1, null)));
				}
				break;
			case IN:
				if (rhs.getTag() == NATURAL) {
					FormulaFactory ff = lhs.getFactory();
					variations.add(rel(IN, lhs, ff.makeAtomicExpression(NATURAL1, null)));
				}
				break;
			}
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
	public List<Predicate> getStrongerNegative(Predicate pred) {
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

	private List<Predicate> getStrongerNegativeRelational(int tag,
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
				if (level.from(L1)) {
					FormulaFactory ff = lhs.getFactory();
					if (isZero(lhs)) {
						variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL1, null)));
					} else if (isZero(rhs)) {
						variations.add(rel(IN, lhs, ff.makeAtomicExpression(NATURAL1, null)));
					}
				}
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
	public List<Predicate> getEquivalent(Predicate pred) {
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

	private void addEquivalentPositiveRelational(
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
		if (level.from(L1)) {
			FormulaFactory ff = lhs.getFactory();
			switch (tag) {
			case LT:
				if (isZero(lhs)) {
					variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL1, null)));
				}
				if (lhs.getTag() == INTLIT) {
					variations.add(rel(LE, literalPlusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(GE, rhs, literalPlusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					variations.add(rel(LE, lhs, literalMinusOne((IntegerLiteral) rhs)));
					variations.add(rel(GE, literalMinusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case LE:
				if (isZero(lhs)) {
					variations.add(rel(IN, rhs, ff.makeAtomicExpression(NATURAL, null)));
				}
				if (lhs.getTag() == INTLIT) {
					variations.add(rel(LT, literalMinusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(GT, rhs, literalMinusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					variations.add(rel(LT, lhs, literalPlusOne((IntegerLiteral) rhs)));
					variations.add(rel(GT, literalPlusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case GT:
				if (isZero(rhs)) {
					variations.add(rel(IN, lhs, ff.makeAtomicExpression(NATURAL1, null)));
				}
				if (lhs.getTag() == INTLIT) {
					variations.add(rel(GE, literalMinusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(LE, rhs, literalMinusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					variations.add(rel(GE, lhs, literalPlusOne((IntegerLiteral) rhs)));
					variations.add(rel(LE, literalPlusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case GE:
				if (isZero(rhs)) {
					variations.add(rel(IN, lhs, ff.makeAtomicExpression(NATURAL, null)));
				}
				if (lhs.getTag() == INTLIT) {
					variations.add(rel(GT, literalPlusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(LT, rhs, literalPlusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					variations.add(rel(GT, lhs, literalMinusOne((IntegerLiteral) rhs)));
					variations.add(rel(LT, literalMinusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case IN:
				switch (rhs.getTag()) {
				case NATURAL:
					addEquivalentPositiveRelational(variations, LE, ff.makeIntegerLiteral(BigInteger.ZERO, null), lhs);
					break;
				case NATURAL1:
					addEquivalentPositiveRelational(variations, LT, ff.makeIntegerLiteral(BigInteger.ZERO, null), lhs);
					break;
				}
				break;
			}
		}
	}

	/**
	 * Returns a list of predicates equivalent to the given predicate.
	 * 
	 * @param pred
	 *            a predicate
	 * @return a list of equivalent predicates
	 */
	public List<Predicate> getEquivalentNegative(Predicate pred) {
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

	private void addEquivalentNegativeRelational(
			List<Predicate> variations, int tag, Expression lhs, Expression rhs) {
		variations.add(makeNeg(rel(tag, lhs, rhs)));
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
		case IN:
			if (level.from(L1)) {
				FormulaFactory ff = lhs.getFactory();
				switch (rhs.getTag()) {
				case NATURAL:
					addEquivalentPositiveRelational(variations, GT, ff.makeIntegerLiteral(BigInteger.ZERO, null), lhs);
					return;
				case NATURAL1:
					addEquivalentPositiveRelational(variations, GE, ff.makeIntegerLiteral(BigInteger.ZERO, null), lhs);
					return;
				}
			}
			break;
		}
		variations.add(makeNeg(rel(tag, lhs, rhs)));
	}

	private boolean isSet(Expression expr) {
		return expr.getType() instanceof PowerSetType;
	}

	private boolean isInteger(Expression rhs) {
		return rhs.getType() instanceof IntegerType;
	}

	private boolean isZero(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().equals(BigInteger.ZERO);
	}

	private boolean isPositive(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().compareTo(BigInteger.ZERO) > 0;
	}

	private boolean isNegative(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().compareTo(BigInteger.ZERO) < 0;
	}

	private RelationalPredicate rel(int tag, Expression left,
			Expression right) {
		final FormulaFactory ff = left.getFactory();
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	private IntegerLiteral literalPlusOne(IntegerLiteral lit) {
		return lit.getFactory().makeIntegerLiteral(lit.getValue().add(BigInteger.ONE), null);
	}

	private IntegerLiteral literalMinusOne(IntegerLiteral lit) {
		return lit.getFactory().makeIntegerLiteral(lit.getValue().subtract(BigInteger.ONE), null);
	}

}
