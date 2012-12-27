/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ProverFactory;

public abstract class InclusionSetMinusRewrites extends AbstractManualRewrites implements
		IReasoner {

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ⊆ with ∖ in " + pred.getSubFormula(position);
		return "remove ⊆ with ∖ in goal";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	protected abstract IFormulaRewriter makeRewriter(FormulaFactory ff);
	
	@Override
	public Predicate rewrite(Predicate pred, IPosition position, FormulaFactory ff) {
		IFormulaRewriter rewriter = makeRewriter(ff);
		Formula<?> predicate = pred.getSubFormula(position);

		Formula<?> newSubPredicate = null;
		if (predicate instanceof Predicate
				&& predicate.getTag() == Predicate.SUBSETEQ) {
			newSubPredicate = rewriter.rewrite((RelationalPredicate) predicate);
		}
		if (newSubPredicate == null || newSubPredicate == predicate)
			return null;
		return pred.rewriteSubFormula(position, newSubPredicate, ff);
	}

}
