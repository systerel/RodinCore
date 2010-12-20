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

import static org.eventb.core.ast.Formula.BOUND_IDENT;
import static org.eventb.core.ast.Formula.MAPSTO;

import java.util.BitSet;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.QuantifiedExpression;

/**
 * Framework for checking if a given comprehension set of the form {x · P ∣ E}
 * is a lambda expression, which implies that E looks like a tree constructed
 * with either:
 * <ul>
 * <li>maplet operators</li>
 * <li>bound identifiers as leaves, which are locally bound and pairwise
 * distinct</li>
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
	private final BitSet locallyBound;

	private LambdaCheck(QuantifiedExpression qExpr) {
		this.qExpr = qExpr;
		this.nbBound = qExpr.getBoundIdentDecls().length;
		locallyBound = new BitSet();
	}

	private boolean verify() {
		final Expression expression = qExpr.getExpression();
		return checkTree(expression);
	}

	private boolean checkTree(Expression expression) {
		switch (expression.getTag()) {
		case MAPSTO:
			final BinaryExpression bExpr = (BinaryExpression) expression;
			return checkTree(bExpr.getLeft()) && checkTree(bExpr.getRight());
		case BOUND_IDENT:
			final int biIndex = ((BoundIdentifier) expression).getBoundIndex();
			if (!isLocallyBound(biIndex)) {
				return false;
			}
			if (locallyBound.get(biIndex)) {
				return false;
			}
			locallyBound.set(biIndex);
			return true;
		default:
			return false;
		}
	}

	private boolean isLocallyBound(final int index) {
		return index < nbBound;
	}

}