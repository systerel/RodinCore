/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.SequentProver;

public class RelOvrRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".relOvrRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return " rewrites in goal";
		return " rewrites in hyp (" + pred.getSubFormula(position) + ")";
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		Formula<?> subFormula = pred.getSubFormula(position);
		if (!(subFormula instanceof Expression))
			return null;
		
		Formula<?> formula = pred.getSubFormula(position.getParent());
		
		if (formula.getTag() == Expression.OVR) {
			IFormulaRewriter rewriter = new RelOvrRewriterImpl(
					(Expression) subFormula);

			Formula<?> newSubFormula = rewriter
					.rewrite((AssociativeExpression) formula);

			if (newSubFormula == formula) // No rewrite occurs
				return null;

			return pred.rewriteSubFormula(position.getParent(), newSubFormula);
		}
		return null;
	}

}
