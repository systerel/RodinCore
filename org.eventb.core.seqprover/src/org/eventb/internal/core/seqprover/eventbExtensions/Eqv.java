/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.seqprover.eventbExtensions.Lib.equivalenceRewrite;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

/**
 * Abstract class for equivalence rewriter
 * 
 * @author Josselin Dolhen
 */
public abstract class Eqv extends EqvEq<Predicate> {

	private class EqvRewriter extends Rewriter {

		public EqvRewriter(Predicate hypEq, Predicate from, Predicate to) {
			super(hypEq, from, to);
		}

		@Override
		public Predicate doRewrite(Predicate pred) {
			return equivalenceRewrite(pred, from, to);
		}

	}

	@Override
	protected int getTag() {
		return Formula.LEQV;
	}

	@Override
	protected Rewriter getRewriter(Predicate hyp, Predicate from, Predicate to) {
		return new EqvRewriter(hyp, from, to);
	}

}
