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

/**
 * Common implementation of the {@code org.eventb.core.seqprover.rm} reasoner.
 * 
 * There are as many sub-classes as levels of this reasoner.
 */
public abstract class RemoveMembership extends AbstractManualRewrites {

	/**
	 * Default instance of this family of reasoners.
	 */
	public static final RemoveMembership DEFAULT = new RemoveMembershipL1();

	public static enum RMLevel {
		L0, L1;
		
		public boolean from(RMLevel other) {
			return this.ordinal() >= other.ordinal();
		}
	}

	private final RMLevel level;

	protected RemoveMembership(RMLevel level) {
		this.level = level;
	}

	public RMLevel getLevel() {
		return level;
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
