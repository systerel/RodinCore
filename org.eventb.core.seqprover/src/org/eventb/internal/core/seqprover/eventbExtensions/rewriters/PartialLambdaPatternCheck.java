/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
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

/**
 * Framework for checking if a given expression is similar to a lambda pattern,
 * that is looks like a tree constructed with
 * <ul>
 * <li>maplet operators as internal nodes</li>
 * <li>bound identifiers as leaves, which are locally bound and pairwise
 * distinct</li>
 * </ul>
 * Here, locally bound means that the de Bruijn index is less than a given
 * constraint.
 * 
 * @author Benoît Lucet
 */
public class PartialLambdaPatternCheck {

	public static boolean partialLambdaPatternCheck(Expression expression,
			int nbBound) {
		return new PartialLambdaPatternCheck(expression, nbBound).verify();
	}

	private final Expression expression;
	private final int nbBound;

	// Set of de Bruijn indexes of already encountered bound identifiers,
	// used to check for pairwise distinctness
	private final BitSet alreadyPresent = new BitSet();

	private PartialLambdaPatternCheck(Expression expression, int nbBound) {
		this.expression = expression;
		this.nbBound = nbBound;
	}

	private boolean verify() {
		return checkTree(expression);
	}

	private boolean checkTree(Expression node) {
		switch (node.getTag()) {
		case MAPSTO:
			final BinaryExpression bExpr = (BinaryExpression) node;
			return checkTree(bExpr.getLeft()) && checkTree(bExpr.getRight());
		case BOUND_IDENT:
			final int biIndex = ((BoundIdentifier) node).getBoundIndex();
			if (!isLocallyBound(biIndex)) {
				return false;
			}
			if (alreadyPresent.get(biIndex)) {
				return false;
			}
			alreadyPresent.set(biIndex);
			return true;
		default:
			return false;
		}
	}

	private boolean isLocallyBound(final int index) {
		return index < nbBound;
	}

}