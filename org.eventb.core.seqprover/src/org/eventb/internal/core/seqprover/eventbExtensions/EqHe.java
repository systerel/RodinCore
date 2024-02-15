/*******************************************************************************
 * Copyright (c) 2006, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactoring around a hierarchy of classes
 *     Université de Lorraine - hide hypothesis when rewriting single variable
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.seqprover.ProverFactory.makeDeselectHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L1;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L2;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * This class handles reasoners Eq and He.
 */
public abstract class EqHe extends EqvEq<Expression> implements
		IVersionedReasoner {

	public static enum Level {
		L0, L1, L2;

		public boolean from(Level other) {
			return this.ordinal() >= other.ordinal();
		}
	}

	private class EqRewriter extends Rewriter {

		public EqRewriter(Predicate hypEq, Expression from, Expression to) {
			super(hypEq, from, to);
		}

		@Override
		public Predicate doRewrite(Predicate pred) {
			return DLib.rewrite(pred, from, to);
		}

	}

	private final Level level;

	public EqHe(Level level) {
		this.level = level;
	}

	@Override
	protected int getTag() {
		return Formula.EQUAL;
	}

	@Override
	protected Rewriter getRewriter(Predicate hyp, Expression from, Expression to) {
		return new EqRewriter(hyp, from, to);
	}

	@Override
	protected ISelectionHypAction hideOrDeselectPredicate(IProverSequent sequent, Predicate hyp) {
		/*
		 * Since level 1, if we rewrite a single identifier, like x = ... or ... = x, we
		 * can hide the hypothesis after rewriting: the identifier is not used anymore
		 * and the equality hypothesis is useless.
		 * 
		 * Since level 2, if the identifier is used in the default hypotheses, the
		 * equality hypothesis is deselected instead of being hidden. Moreover,
		 * recursive rewrites do not deselect nor hide the hypothesis.
		 */
		Expression from = getFrom(hyp);
		if (level.from(L1) && from.getTag() == FREE_IDENT) {
			if (level.from(L2)) {
				if (stream(getTo(hyp).getFreeIdentifiers()).anyMatch(from::equals)) {
					// This is some kind of recursive rewriting (e.g., x = f(x)); it's probably
					// better not to do anything and leave the hypothesis as-is
					return null;
				} else if (identifierInDefaultHyps(sequent, (FreeIdentifier) from)) {
					return makeDeselectHypAction(singleton(hyp));
				}
			}
			return makeHideHypAction(singleton(hyp));
		}
		return null;
	}

	private boolean identifierInDefaultHyps(IProverSequent sequent, FreeIdentifier ident) {
		for (var hyp : sequent.visibleMinusSelectedIterable()) {
			if (stream(hyp.getFreeIdentifiers()).anyMatch(ident::equals)) {
				return true;
			}
		}
		return false;
	}
}
