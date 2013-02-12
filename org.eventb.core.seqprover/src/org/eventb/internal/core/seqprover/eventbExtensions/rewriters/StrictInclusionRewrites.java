/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;

public class StrictInclusionRewrites extends AbstractManualRewrites implements
		IReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".sir";

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "rewrite ⊂ in " + pred.getSubFormula(position);
		return "rewrite ⊂ in goal";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new StrictInclusionRewriterImpl();
		Formula<?> predicate = pred.getSubFormula(position);

		Formula<?> newSubPredicate = null;
		if (predicate.getTag() == Predicate.SUBSET)
			newSubPredicate = rewriter.rewrite((RelationalPredicate) predicate);
		if (newSubPredicate == null)
			return null;
		return pred.rewriteSubFormula(position, newSubPredicate);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

}
