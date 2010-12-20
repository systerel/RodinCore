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
 * is a function, based on its syntactical properties. It implies the following
 * checking:
 * <ul>
 * <li>E is of the form F ↦ G</li>
 * <li>all locally bound variables that occur in G also occur in F</li>
 * <li>F looks like a tree constructed with either:</li>
 * <ul>
 * <li>maplet operators</li>
 * <li>bound identifiers, either locally bound or not</li>
 * <li>expressions</li>
 * </ul>
 * with the following restriction : if a locally bound variable appears in G,
 * then it has to appear as a leaf of a maplet operator at least once in F </ul>
 * 
 * @author Benoît Lucet
 */
public class FunctionalCheck {

	public static boolean functionalCheck(QuantifiedExpression expression) {
		return new FunctionalCheck(expression).verify();
	}

	private final QuantifiedExpression qExpr;
	private final int nbBound;

	// This bitset contains locally bound identifiers that occur in the
	// right-hand side of the expression of qExpr and not yet detected in the
	// left-hand side in an injective position.
	private final BitSet locallyBoundRight;

	private FunctionalCheck(QuantifiedExpression qExpr) {
		this.qExpr = qExpr;
		this.nbBound = qExpr.getBoundIdentDecls().length;
		this.locallyBoundRight = new BitSet();
	}

	private boolean verify() {
		final Expression child = qExpr.getExpression();
		if (child.getTag() != MAPSTO) {
			return false;
		}
		final BinaryExpression maplet = (BinaryExpression) child;
		computeLocallyBoundRight(maplet.getRight());
		checkOccurrences(maplet.getLeft());
		// If the bitset is empty, then all quantified identifiers occurring in
		// the right-hand side of the expression are guaranteed to occur as
		// leaves at least once in the left-hand side
		return locallyBoundRight.isEmpty();
	}

	private void computeLocallyBoundRight(Expression right) {
		for (final BoundIdentifier rbi : right.getBoundIdentifiers()) {
			final int index = rbi.getBoundIndex();
			if (isLocallyBound(index)) {
				locallyBoundRight.set(index);
			}
		}
	}

	private boolean isLocallyBound(final int index) {
		return index < nbBound;
	}

	private void checkOccurrences(Expression expr) {
		switch (expr.getTag()) {
		case MAPSTO:
			final BinaryExpression maplet = (BinaryExpression) expr;
			checkOccurrences(maplet.getLeft());
			checkOccurrences(maplet.getRight());
			break;
		case BOUND_IDENT:
			final int boundIndex = ((BoundIdentifier) expr).getBoundIndex();
			locallyBoundRight.clear(boundIndex);
			break;
		default:
			// other expression
			break;
		}
	}

}