/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.util.Arrays.asList;
import static java.util.Collections.reverse;
import static org.eventb.core.ast.Formula.BCOMP;
import static org.eventb.core.ast.Formula.FCOMP;

import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

/**
 * Rewriter for the definition of backward composition.
 *
 * Rewrites r ∘ … ∘ s to s ; … ; r.
 *
 * @author Guillaume Verdier
 */
public class BCompDefRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".bcompDefRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "backward composition definition";
	}

	@Override
	@ProverRule("DEF_BCOMP")
	public Predicate rewrite(Predicate pred, IPosition position) {
		Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != BCOMP) {
			return null;
		}
		var bcomp = (AssociativeExpression) subFormula;
		FormulaFactory ff = bcomp.getFactory();
		List<Expression> children = asList(bcomp.getChildren());
		reverse(children);
		Expression rewritten = ff.makeAssociativeExpression(FCOMP, children, null);
		return pred.rewriteSubFormula(position, rewritten);
	}

}
