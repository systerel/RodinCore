/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.SequentProver;

public class DomRanUnionDistRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".domRanUnionDistRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred == null)
			return "dom. or ran. / union dist. rewrites in goal";
		return "dom. or ran. / union dist. rewrites in hyp (" + pred.getSubFormula(position) + ")";
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		IFormulaRewriter rewriter = new DomRanUnionDistRewriterImpl();
		Formula<?> subFormula = pred.getSubFormula(position);
		
		Formula<?> newSubFormula = null;
		if (subFormula instanceof UnaryExpression) {
			newSubFormula = rewriter
				.rewrite((UnaryExpression) subFormula);
		}
		if (newSubFormula == null)
			return null;
		
		if (newSubFormula == subFormula) // No rewrite occurs
			return null;

		return pred.rewriteSubFormula(position, newSubFormula);
	}

}
