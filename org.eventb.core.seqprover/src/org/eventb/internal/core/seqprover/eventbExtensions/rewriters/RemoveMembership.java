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
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class RemoveMembership extends AbstractManualRewrites implements
		IReasoner {

	public static enum RMLevel {
		L0, L1;
		
		public boolean from(RMLevel other) {
			return this.ordinal() >= other.ordinal();
		}
	}
	
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".rm";

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ∈ in " + pred.getSubFormula(position);
		return "remove ∈ in goal";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position, FormulaFactory ff) {
		IFormulaRewriter rewriter = getRewriter();
		Formula<?> predicate = pred.getSubFormula(position);

		Formula<?> newSubPredicate = null;
		if (predicate instanceof Predicate
				&& Lib.isInclusion((Predicate) predicate))
			newSubPredicate = rewriter.rewrite((RelationalPredicate) predicate);
		if (newSubPredicate == null || newSubPredicate == predicate)
			return null;
		return pred.rewriteSubFormula(position, newSubPredicate, ff);
	}

	protected RemoveMembershipRewriterImpl getRewriter() {
		return new RemoveMembershipRewriterImpl(RMLevel.L0);
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

}
