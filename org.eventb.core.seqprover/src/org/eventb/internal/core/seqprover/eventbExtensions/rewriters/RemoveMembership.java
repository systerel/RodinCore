/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactor code
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.IN;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.SequentProver;

/**
 * Common implementation of the {@code org.eventb.core.seqprover.rm} reasoner.
 * 
 * There are as many sub-classes as levels of this reasoner.
 */
public abstract class RemoveMembership extends AbstractManualRewrites {

	/*
	 * HOW TO CREATE A NEW LEVEL OF THIS REASONER:
	 *
	 * - First create a new Level in the enum below
	 * - Then create a new subclass of this class.
	 * - Update plugin.xml to declare the new level.
	 * - Change the value of DEFAULT below.
	 */

	/**
	 * Default instance of this family of reasoners.
	 */
	public static final RemoveMembership DEFAULT = new RemoveMembershipL2();

	public static enum Level {
		L0, L1, L2;
		
		public boolean from(Level other) {
			return this.ordinal() >= other.ordinal();
		}
	}

	// Base name for reasoner id.
	private static final String REASONER_ID = SequentProver.PLUGIN_ID + ".rm";

	// For testing reasoner applicability
	private static final RemoveMembershipRewriterImpl TEST_REWRITER //
			= new RemoveMembershipRewriterImpl(DEFAULT.level, false);

	/**
	 * Tells whether this reasoner is applicable to the given predicate.
	 */
	public static final boolean isApplicableTo(Predicate predicate) {
		return TEST_REWRITER.isApplicableOrRewrite(predicate);
	}

	private final Level level;

	protected RemoveMembership(Level level) {
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

	@Override
	public String getReasonerID() {
		if (level == Level.L0) {
			return REASONER_ID;
		}
		return REASONER_ID + level;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ∈ in " + pred.getSubFormula(position);
		return "remove ∈ in goal";
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new RemoveMembershipRewriterImpl(level);
		Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula.getTag() != IN) {
			return null;
		}

		Predicate newSubPredicate = rewriter.rewrite((RelationalPredicate) subFormula);
		if (newSubPredicate == null || newSubPredicate == subFormula) {
			return null;
		}

		return pred.rewriteSubFormula(position, newSubPredicate);
	}

}
