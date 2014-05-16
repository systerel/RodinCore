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
import org.eventb.core.seqprover.eventbExtensions.Lib;

public abstract class RemoveMembership extends AbstractManualRewrites {

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
	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "remove ∈ in " + pred.getSubFormula(position);
		return "remove ∈ in goal";
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new RemoveMembershipRewriterImpl(level);
		Formula<?> predicate = pred.getSubFormula(position);

		Formula<?> newSubPredicate = null;
		if (predicate instanceof Predicate
				&& Lib.isInclusion((Predicate) predicate))
			newSubPredicate = rewriter.rewrite((RelationalPredicate) predicate);
		if (newSubPredicate == null || newSubPredicate == predicate)
			return null;
		return pred.rewriteSubFormula(position, newSubPredicate);
	}

}
