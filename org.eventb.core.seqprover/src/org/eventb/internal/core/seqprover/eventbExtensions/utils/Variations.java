/*******************************************************************************
 * Copyright (c) 2013, 2023 Systerel and others.
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
import static java.util.stream.Collectors.toList;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FALSE;
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
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.seqprover.eventbExtensions.DLib.makeNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.Level.L0;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.Variations.Level.L1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AtomicExpression;
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
 * NAT and NAT1 as well as equivalences between booleans.</li>
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

	public static final Variations INSTANCE_L0 = new Variations(L0);

	public static final Variations INSTANCE_L1 = new Variations(L1);

	/**
	 * Get the implementation level.
	 *
	 * @return level
	 */
	public Level getLevel() {
		return level;
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
			case EQUAL:
				if (isZero(rhs)) {
					addEquivalentNegativeRelational(variations, IN, lhs, nat1(lhs.getFactory()));
				}
				if (isZero(lhs)) {
					addEquivalentNegativeRelational(variations, IN, rhs, nat1(rhs.getFactory()));
				}
				break;
			case LE:
			case LT:
				// Cases where the number is equal to zero are handled in equivalences
				if (isPositiveIntLit(lhs)) {
					FormulaFactory ff = lhs.getFactory();
					addEquivalentPositiveRelational(variations, IN, rhs, nat(ff));
					addEquivalentPositiveRelational(variations, IN, rhs, nat1(ff));
				}
				break;
			case IN:
				if (rhs.getTag() == NATURAL1) {
					FormulaFactory ff = lhs.getFactory();
					addEquivalentPositiveRelational(variations, IN, lhs, nat(ff));
					addEquivalentNegativeRelational(variations, EQUAL, lhs,
							ff.makeIntegerLiteral(BigInteger.ZERO, null));
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
		List<Predicate> result = getStrongerPositive(pred);
		// result can't be modified in-place: it may be immutable (e.g., singletonList())
		return result.stream().map(this::makeNegRelational).collect(toList());
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
				if (isNegativeIntLit(lhs)) {
					FormulaFactory ff = lhs.getFactory();
					addEquivalentPositiveRelational(variations, IN, rhs, nat(ff));
					addEquivalentPositiveRelational(variations, IN, rhs, nat1(ff));
				}
				break;
			case IN:
				if (rhs.getTag() == NATURAL) {
					addEquivalentPositiveRelational(variations, IN, lhs, nat1(lhs.getFactory()));
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
		List<Predicate> result = getWeakerPositive(pred);
		// result can't be modified in-place: it may be immutable (e.g., singletonList())
		return result.stream().map(this::makeNegRelational).collect(toList());
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
			case EQUAL: {
				var falseExpr = ff.makeAtomicExpression(FALSE, null, ff.makeBooleanType());
				if (lhs.getTag() == TRUE) {
					variations.add(makeNeg(rel(EQUAL, falseExpr, rhs)));
					variations.add(makeNeg(rel(EQUAL, rhs, falseExpr)));
				}
				if (rhs.getTag() == TRUE) {
					variations.add(makeNeg(rel(EQUAL, lhs, falseExpr)));
					variations.add(makeNeg(rel(EQUAL, falseExpr, lhs)));
				}
				var trueExpr = ff.makeAtomicExpression(TRUE, null, ff.makeBooleanType());
				if (lhs.getTag() == FALSE) {
					variations.add(makeNeg(rel(EQUAL, trueExpr, rhs)));
					variations.add(makeNeg(rel(EQUAL, rhs, trueExpr)));
				}
				if (rhs.getTag() == FALSE) {
					variations.add(makeNeg(rel(EQUAL, lhs, trueExpr)));
					variations.add(makeNeg(rel(EQUAL, trueExpr, lhs)));
				}
				break;
			}
			case LT:
				if (lhs.getTag() == INTLIT) {
					if (isZero(lhs)) {
						variations.add(rel(IN, rhs, nat1(ff)));
					} else if (isMinusOne(lhs)) {
						variations.add(rel(IN, rhs, nat(ff)));
					}
					variations.add(rel(LE, literalPlusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(GE, rhs, literalPlusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					if (isZero(rhs)) {
						variations.add(makeNeg(rel(IN, lhs, nat(ff))));
					} else if (isOne(rhs)) {
						variations.add(makeNeg(rel(IN, lhs, nat1(ff))));
					}
					variations.add(rel(LE, lhs, literalMinusOne((IntegerLiteral) rhs)));
					variations.add(rel(GE, literalMinusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case LE:
				if (lhs.getTag() == INTLIT) {
					if (isZero(lhs)) {
						variations.add(rel(IN, rhs, nat(ff)));
					} else if (isOne(lhs)) {
						variations.add(rel(IN, rhs, nat1(ff)));
					}
					variations.add(rel(LT, literalMinusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(GT, rhs, literalMinusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					if (isZero(rhs)) {
						variations.add(makeNeg(rel(IN, lhs, nat1(ff))));
					} else if (isMinusOne(rhs)) {
						variations.add(makeNeg(rel(IN, lhs, nat(ff))));
					}
					variations.add(rel(LT, lhs, literalPlusOne((IntegerLiteral) rhs)));
					variations.add(rel(GT, literalPlusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case GT:
				if (lhs.getTag() == INTLIT) {
					if (isZero(lhs)) {
						variations.add(makeNeg(rel(IN, rhs, nat(ff))));
					} else if (isOne(lhs)) {
						variations.add(makeNeg(rel(IN, rhs, nat1(ff))));
					}
					variations.add(rel(GE, literalMinusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(LE, rhs, literalMinusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					if (isZero(rhs)) {
						variations.add(rel(IN, lhs, nat1(ff)));
					} else if (isMinusOne(rhs)) {
						variations.add(rel(IN, lhs, nat(ff)));
					}
					variations.add(rel(GE, lhs, literalPlusOne((IntegerLiteral) rhs)));
					variations.add(rel(LE, literalPlusOne((IntegerLiteral) rhs), lhs));
				}
				break;
			case GE:
				if (lhs.getTag() == INTLIT) {
					if (isZero(lhs)) {
						variations.add(makeNeg(rel(IN, rhs, nat1(ff))));
					} else if (isMinusOne(lhs)) {
						variations.add(makeNeg(rel(IN, rhs, nat(ff))));
					}
					variations.add(rel(GT, literalPlusOne((IntegerLiteral) lhs), rhs));
					variations.add(rel(LT, rhs, literalPlusOne((IntegerLiteral) lhs)));
				}
				if (rhs.getTag() == INTLIT) {
					if (isZero(rhs)) {
						variations.add(rel(IN, lhs, nat(ff)));
					} else if (isOne(rhs)) {
						variations.add(rel(IN, lhs, nat1(ff)));
					}
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

	// makeNeg() with special handling of inequalities
	private Predicate makeNegRelational(Predicate pred) {
		if (pred instanceof RelationalPredicate) {
			var rel = (RelationalPredicate) pred;
			switch (rel.getTag()) {
			case LE:
				return pred.getFactory().makeRelationalPredicate(GT, rel.getLeft(), rel.getRight(), null);
			case GE:
				return pred.getFactory().makeRelationalPredicate(LT, rel.getLeft(), rel.getRight(), null);
			case LT:
				return pred.getFactory().makeRelationalPredicate(GE, rel.getLeft(), rel.getRight(), null);
			case GT:
				return pred.getFactory().makeRelationalPredicate(LE, rel.getLeft(), rel.getRight(), null);
			}
		}
		return makeNeg(pred);
	}

	private boolean isSet(Expression expr) {
		return expr.getType() instanceof PowerSetType;
	}

	private boolean isInteger(Expression rhs) {
		return rhs.getType() instanceof IntegerType;
	}

	private boolean isZero(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().signum() == 0;
	}

	private boolean isOne(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().equals(BigInteger.ONE);
	}

	private static final BigInteger MINUS_ONE = BigInteger.ONE.negate();

	private boolean isMinusOne(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().equals(MINUS_ONE);
	}

	private boolean isPositiveIntLit(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().signum() > 0;
	}

	private boolean isNegativeIntLit(Expression expr) {
		return expr.getTag() == INTLIT && ((IntegerLiteral) expr).getValue().signum() < 0;
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

	private AtomicExpression nat(FormulaFactory ff) {
		return ff.makeAtomicExpression(NATURAL, null);
	}

	private AtomicExpression nat1(FormulaFactory ff) {
		return ff.makeAtomicExpression(NATURAL1, null);
	}

}
