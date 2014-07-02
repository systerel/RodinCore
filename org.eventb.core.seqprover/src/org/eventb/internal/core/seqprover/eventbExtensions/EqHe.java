/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring around a hierarchy of classes
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * This class handles reasoners Eq and He.
 */
public abstract class EqHe extends EqvEq<Expression> implements
		IVersionedReasoner {

	private class EqRewriter extends Rewriter {

		public EqRewriter(Predicate hypEq, Expression from, Expression to) {
			super(hypEq, from, to);
		}

		@Override
		public Predicate doRewrite(Predicate pred) {
			return DLib.rewrite(pred, from, to);
		}

	}

	@Override
	protected int getTag() {
		return Formula.EQUAL;
	}

	@Override
	protected Rewriter getRewriter(Predicate hyp, Expression from, Expression to) {
		return new EqRewriter(hyp, from, to);
	}
}
