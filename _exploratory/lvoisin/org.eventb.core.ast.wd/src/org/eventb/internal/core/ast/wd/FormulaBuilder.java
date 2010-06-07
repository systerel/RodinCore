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

import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.NOTEQUAL;
import static org.eventb.core.ast.Formula.PFUN;

import java.math.BigInteger;
import java.util.LinkedList;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;

/**
 * Utility methods for building new formulas in the course of the computation of
 * a WD lemma.
 * 
 * @author Laurent Voisin
 */
public class FormulaBuilder {

	public final FormulaFactory ff;
	public final Predicate btrue;

	/**
	 * Caches constant zero. Shall always be accessed through method
	 * <code>zero()</code>.
	 */
	private Expression zero_cache;

	/**
	 * Caches type INTEGER. Shall always be accessed through method
	 * <code>Z()</code>.
	 */
	private Type Z_cache;

	public FormulaBuilder(FormulaFactory ff) {
		this.ff = ff;
		this.btrue = ff.makeLiteralPredicate(BTRUE, null);
	}

	public Predicate bounded(Expression set, boolean lower) {
		final BoundIdentifier b0 = ff.makeBoundIdentifier(0, null, Z());
		final BoundIdentifier b1 = ff.makeBoundIdentifier(1, null, Z());
		final int tag = lower ? LE : GE;
		final Predicate rel = ff.makeRelationalPredicate(tag, b1, b0, null);
		final RelationalPredicate xInSet = ff.makeRelationalPredicate(IN, b0,
				set.shiftBoundIdentifiers(2, ff), null);
		final Predicate impl = ff.makeBinaryPredicate(LIMP, xInSet, rel, null);
		final BoundIdentDecl[] b = new BoundIdentDecl[] { ff
				.makeBoundIdentDecl("b", null, Z()) };
		final BoundIdentDecl[] x = new BoundIdentDecl[] { ff
				.makeBoundIdentDecl("x", null, Z()) };
		final Predicate conj2 = ff.makeQuantifiedPredicate(EXISTS, b, ff
				.makeQuantifiedPredicate(FORALL, x, impl, null), null);
		return conj2;
	}

	public Predicate exists(BoundIdentDecl[] decls, Predicate pred) {
		if (pred.getTag() == BTRUE)
			return pred;
		return ff.makeQuantifiedPredicate(EXISTS, decls, pred, null);
	}

	public Predicate finite(Expression expr) {
		return ff.makeSimplePredicate(KFINITE, expr, null);
	}

	public Predicate forall(BoundIdentDecl[] decls, Predicate pred) {
		if (pred.getTag() == BTRUE)
			return pred;
		return ff.makeQuantifiedPredicate(FORALL, decls, pred, null);
	}

	public Predicate inDomain(Expression fun, Expression expr) {
		final Expression dom = ff.makeUnaryExpression(KDOM, fun, null);
		return ff.makeRelationalPredicate(IN, expr, dom, null);
	}

	/**
	 * Returns the simplified conjunction of the given predicates. The
	 * simplifications made are based on the following properties:
	 * <ul>
	 * <li>BTRUE is a neutral element for conjunction</li>
	 * <li>The conjunction operator is idempotent.</li>
	 * </ul>
	 * 
	 * @param left
	 *            a predicate
	 * @param right
	 *            another predicate
	 * @return the simplified conjunction of the given predicates
	 */
	public Predicate land(Predicate left, Predicate right) {

		if (left.getTag() == BTRUE) {
			return right;
		}
		if (right.getTag() == BTRUE) {
			return left;
		}
		// if (left.equals(right)) {
		// return left;
		// }
		// if (right.getTag() == LAND) {
		// AssociativePredicate ap = (AssociativePredicate) right;
		// for (int i = 0; i < ap.getChildren().length; i++) {
		// if (ap.getChildren()[i].equals(left)) {
		// return right;
		// }
		// }
		// }
		// if (right.getTag() == LAND) {
		// AssociativePredicate leftAP = (AssociativePredicate) right;
		// for (int i = 0; i < leftAP.getChildren().length; i++) {
		// if (leftAP.getChildren()[i].equals(right)) {
		// return left;
		// }
		// }
		// }

		final Predicate[] children = new Predicate[] { left, right };
		return ff.makeAssociativePredicate(LAND, children, null);
	}

	public Predicate land(Predicate... children) {
		final LinkedList<Predicate> conjuncts = new LinkedList<Predicate>();
		for (Predicate child : children) {
			if (child.getTag() != BTRUE)
				conjuncts.add(child);
		}
		switch (conjuncts.size()) {
		case 0:
			return btrue;
		case 1:
			return conjuncts.getFirst();
		default:
			return ff.makeAssociativePredicate(LAND, conjuncts, null);
		}
	}

	public Predicate limp(Predicate left, Predicate right) {
		if (left.getTag() == BTRUE || right.getTag() == BTRUE)
			return right;
		if (right.getTag() == LIMP) {
			final Predicate rightLeft = ((BinaryPredicate) right).getLeft();
			final Predicate newRight = ((BinaryPredicate) right).getRight();
			final Predicate newLeft = land(left, rightLeft);
			return limp(newLeft, newRight);
		}
		if (left.equals(right)) {
			return btrue;
		}
		return ff.makeBinaryPredicate(LIMP, left, right, null);
	}

	public Predicate lor(Predicate left, Predicate right) {
		if (left.getTag() == BTRUE)
			return left;
		if (right.getTag() == BTRUE)
			return right;
		final Predicate[] children = new Predicate[] { left, right };
		return ff.makeAssociativePredicate(LOR, children, null);
	}

	public Predicate nonNegative(Expression expr) {
		return ff.makeRelationalPredicate(LE, zero(), expr, null);
	}

	public Predicate notEmpty(final Expression expr) {
		final Expression emptyset = ff.makeEmptySet(expr.getType(), null);
		return ff.makeRelationalPredicate(NOTEQUAL, expr, emptyset, null);
	}

	public RelationalPredicate notZero(Expression expr) {
		return ff.makeRelationalPredicate(NOTEQUAL, expr, zero(), null);
	}

	public Predicate partial(Expression fun) {
		final Type funType = fun.getType();
		final Expression src = funType.getSource().toExpression(ff);
		final Expression trg = funType.getTarget().toExpression(ff);
		final Expression pfun = ff.makeBinaryExpression(PFUN, src, trg, null);
		return ff.makeRelationalPredicate(IN, fun, pfun, null);
	}

	public RelationalPredicate positive(Expression expr) {
		return ff.makeRelationalPredicate(LT, zero(), expr, null);
	}

	public Type Z() {
		if (Z_cache == null) {
			Z_cache = ff.makeIntegerType();
		}
		return Z_cache;
	}

	public Expression zero() {
		if (zero_cache == null) {
			zero_cache = ff.makeIntegerLiteral(BigInteger.ZERO, null);
		}
		return zero_cache;
	}

}