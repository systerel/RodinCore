/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;

public class DisjunctionToImplicationRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".disjToImplRewrites";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		if (pred != null)
			return "∨ to ⇒ in " + pred.getSubFormula(position);
		return "∨ to ⇒ in goal";
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
		Formula<?> subFormula = pred
						.getSubFormula(position);
		if (subFormula == null || !(subFormula instanceof AssociativePredicate)) 
			return null;
		AssociativePredicate predicate = (AssociativePredicate) subFormula;
		IFormulaRewriter rewriter = new DisjunctionToImplicationRewriter(true, ff);
		Predicate newSubPredicate = predicate.rewrite(rewriter);
		return pred.rewriteSubFormula(position, newSubPredicate, ff);
	}

}
