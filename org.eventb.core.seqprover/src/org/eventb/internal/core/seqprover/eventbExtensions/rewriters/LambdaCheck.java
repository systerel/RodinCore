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

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.QuantifiedExpression;

/**
 * Framework for checking if a given comprehension set of the form {x · P ∣ E}
 * is a function, based on its syntactical properties (hence the name
 * {@link LambdaCheck}, although it does not necessarily takes a lambda
 * expression as input). It implies the following checking:
 * <ul>
 * <li>E is of the form F ↦ G</li>
 * <li>all locally bound variables that occur in G also occur in F</li>
 * <li>F looks like a tree constructed with either:</li>
 * <ul>
 * <li>maplet operators</li>
 * <li>bound identifiers, either locally bound or not</li>
 * <li>expressions that do not contain locally bound variables that occur both
 * in F and G</li>
 * </ul>
 * </ul>
 * 
 * @author Benoît Lucet
 */
public class LambdaCheck {

	public static boolean lambdaCheck(QuantifiedExpression expression) {
		return new LambdaCheck(expression).verify();
	}

	private final QuantifiedExpression qExpr;
	private final int nbBound;
	private final List<BoundIdentifier> commonLocallyBound;

	private LambdaCheck(QuantifiedExpression qExpr) {
		this.qExpr = qExpr;
		this.nbBound = qExpr.getBoundIdentDecls().length;
		this.commonLocallyBound = new ArrayList<BoundIdentifier>();
	}

	private boolean verify() {
		final Expression child = qExpr.getExpression();
		if (child.getTag() != MAPSTO) {
			return false;
		}
		final BinaryExpression maplet = (BinaryExpression) child;
		final Expression left = maplet.getLeft();
		final Expression right = maplet.getRight();
		return checkIdents(left, right) && isInjective(left);
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
			return !containsCommonLocallyBound(expr);
		}
	}

	private boolean containsCommonLocallyBound(Expression expression) {
		for (final BoundIdentifier bi : expression.getBoundIdentifiers()) {
			if (commonLocallyBound.contains(bi)) {
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
			if (isLocallyBound(rbi)) {
				if (lbis.contains(rbi)) {
					commonLocallyBound.add(rbi);
				} else {
					return false;
				}
			}
		}
		return true;
	}

}