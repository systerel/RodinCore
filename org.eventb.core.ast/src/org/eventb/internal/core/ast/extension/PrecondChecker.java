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
package org.eventb.internal.core.ast.extension;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * @author Nicolas Beauger
 *
 */
public class PrecondChecker {
	public static final int NO_LIMIT = Integer.MAX_VALUE;
	
	private final int minExpr;
	private final int maxExpr;
	private final int minPred;
	private final int maxPred;
	
	public PrecondChecker(int minExpr, int maxExpr, int minPred,
			int maxPred) {
		this.minExpr = minExpr;
		this.maxExpr = maxExpr;
		this.minPred = minPred;
		this.maxPred = maxPred;
	}

	// TODO the method is always the same for a given extension kind
	// => implement for every extension kind, then remove this method
	public boolean checkPreconditions(Expression[] expressions, Predicate[] predicates) {
		return minExpr <= expressions.length && expressions.length <= maxExpr
				&& minPred <= predicates.length && predicates.length <= maxPred;
	}

}
