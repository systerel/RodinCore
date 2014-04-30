/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.SequentProver;

public class RemoveInclusionUniversal extends AbstractManualRewrites implements
		IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".riUniversal";

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ⊆ (universal) in " + pred.getSubFormula(position);
		return "remove ⊆ (universal) in goal";
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new RemoveInclusionUniversalRewriterImpl();
		Formula<?> predicate = pred.getSubFormula(position);

		Formula<?> newSubPredicate = null;
		if (predicate instanceof Predicate
				&& predicate.getTag() == Predicate.SUBSETEQ)
			newSubPredicate = rewriter.rewrite((RelationalPredicate) predicate);
		if (newSubPredicate == null)
			return null;
		return pred.rewriteSubFormula(position, newSubPredicate);
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

}
