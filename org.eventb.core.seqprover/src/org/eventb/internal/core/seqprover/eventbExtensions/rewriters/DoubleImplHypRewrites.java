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

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.SequentProver;

public class DoubleImplHypRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".doubleImplHypRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		assert pred != null;
		return "db impl (" + pred.getSubFormula(position) + ")";
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position) {
		BinaryPredicate predicate = (BinaryPredicate) pred
				.getSubFormula(position);
		IFormulaRewriter rewriter = new DoubleImplicationRewriter(true);
		Predicate newSubPredicate = rewriter.rewrite(predicate);
		return pred.rewriteSubFormula(position, newSubPredicate);
	}

}
