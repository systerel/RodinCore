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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Arrays.asList;
import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.QuantifiedExpression;

// TODO Benoît Present this algorithm here
public class LambdaCheck {

	public static boolean lambdaCheck(QuantifiedExpression expression) {
		return new LambdaCheck(expression).verify();
	}

	private final QuantifiedExpression qExpr;
	private final int nbBound;

	private LambdaCheck(QuantifiedExpression qExpr) {
		this.qExpr = qExpr;
		this.nbBound = qExpr.getBoundIdentDecls().length;
	}

	private boolean verify() {
		final Expression child = qExpr.getExpression();
		if (child.getTag() != MAPSTO) {
			return false;
		}
		final BinaryExpression maplet = (BinaryExpression) child;
		final Expression left = maplet.getLeft();
		final Expression right = maplet.getRight();
		return isInjective(left) && checkIdents(left, right);
	}

	private boolean isInjective(Expression expr) {
		switch (expr.getTag()) {
		case MAPSTO:
			final BinaryExpression maplet = (BinaryExpression) expr;
			return isInjective(maplet.getLeft())
					&& isInjective(maplet.getRight());
		case BOUND_IDENT:
			return true;
		default:
			return !containsLocallyBound(expr);
		}
	}

	private boolean containsLocallyBound(Expression expression) {
		for (final BoundIdentifier bi : expression.getBoundIdentifiers()) {
			if (isLocallyBound(bi)) {
				return true;
			}
		}
		return false;
	}

	private boolean isLocallyBound(final BoundIdentifier bi) {
		return bi.getBoundIndex() < nbBound;
	}

	private boolean checkIdents(Expression left, Expression right) {
		final List<BoundIdentifier> lbis = asList(left.getBoundIdentifiers());
		for (final BoundIdentifier rbi : right.getBoundIdentifiers()) {
			if (isLocallyBound(rbi) && ! lbis.contains(rbi)) {
				return false;
			}
		}
		return true;
	}
}