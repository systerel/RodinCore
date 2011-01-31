/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.TBIJ;
import static org.eventb.core.ast.Formula.UPTO;

import java.math.BigInteger;
import java.util.Arrays;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class FiniteDefRewrites extends AbstractManualRewrites {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".finiteDefRewrites";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName(Predicate pred, IPosition position) {
		return "finite definition";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

	@Override
	public Predicate rewrite(Predicate pred, IPosition position,
			FormulaFactory ff) {
		Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || !(subFormula.getTag() == Formula.KFINITE)) {
			return null;
		}
		final SimplePredicate predicate = (SimplePredicate) subFormula;
		final IFormulaRewriter rewriter = new FiniteDefRewriter(true, ff);
		final Predicate newSubPredicate = predicate.rewrite(rewriter);
		return pred.rewriteSubFormula(position, newSubPredicate, ff);
	}

	public static class FiniteDefRewriter extends DefaultRewriter {

		public FiniteDefRewriter(boolean autoFlattening, FormulaFactory ff) {
			super(autoFlattening, ff);
		}

		@ProverRule("DEF_FINITE")
		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			if (Lib.isFinite(predicate)) {
				final Expression set = predicate.getExpression();

				final Type intType = ff.makeIntegerType();
				final Type fType = ff.makeRelationalType(intType, set.getType()
						.getBaseType());

				final BoundIdentDecl[] decls = new BoundIdentDecl[] {
						ff.makeBoundIdentDecl("n", null, intType),
						ff.makeBoundIdentDecl("f", null, fType) };

				final BoundIdentifier n = ff.makeBoundIdentifier(1, null,
						intType);
				final BoundIdentifier f = ff
						.makeBoundIdentifier(0, null, fType);
				final IntegerLiteral one = ff.makeIntegerLiteral(
						BigInteger.ONE, null);

				final Expression upTo = ff.makeBinaryExpression(UPTO, one, n,
						null);
				final Expression bij = ff.makeBinaryExpression(TBIJ, upTo,
						set.shiftBoundIdentifiers(2, ff), null);
				final Predicate inRel = ff.makeRelationalPredicate(IN, f, bij,
						null);

				return ff.makeQuantifiedPredicate(EXISTS, decls, inRel, null);
			}
			return predicate;
		}

	}

}
