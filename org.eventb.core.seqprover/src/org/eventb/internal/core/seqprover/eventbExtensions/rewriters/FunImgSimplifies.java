/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.Arrays;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

@ProverRule( { "FUNIMG_SET_DOMSUB", "FUNIMG_DOMSUB" })
public class FunImgSimplifies extends AbstractManualRewrites implements
		IVersionedReasoner {

	private static final int VERSION = 0;

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".funImgSimplifies";

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	public int getVersion() {
		return VERSION;
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;
		final FunImgSimpImpl impl = new FunImgSimpImpl(hyp, seq);
		if (hyp == null) {
			final Predicate goal = seq.goal();
			final Predicate neededHyp = impl.getNeededHyp(getFunction(goal,
					position));
			if (neededHyp == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " is inapplicable for goal " //
						+ goal + " at position " + position);
			}
		} else {
			final Predicate neededHyp = impl.getNeededHyp(getFunction(hyp,
					position));
			if (neededHyp == null) {
				return ProverFactory.reasonerFailure(this, input, "Rewriter "
						+ getReasonerID() + " is inapplicable for hypothesis "
						+ hyp + " at position " + position);
			}
		}
		return super.apply(seq, reasonerInput, pm);
	}

	@Override
	protected Predicate rewrite(Predicate pred, IPosition position) {
		final Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Expression.FUNIMAGE) {
			return null;
		}
		final BinaryExpression funImage = (BinaryExpression) subFormula;
		final Expression G = funImage.getRight();
		final Expression replacement = ff.makeBinaryExpression(
				Expression.FUNIMAGE, getFunction(pred, position), G, null);
		return pred.rewriteSubFormula(position, replacement, ff);
	}

	private Expression getFunction(Predicate pred, IPosition position) {
		if (position == null) {
			return null;
		}
		final Formula<?> subFormula = pred.getSubFormula(position);
		if (subFormula == null || subFormula.getTag() != Expression.FUNIMAGE) {
			return null;
		}
		final BinaryExpression funImage = (BinaryExpression) subFormula;
		if (funImage.getLeft().getTag() != Expression.DOMSUB) {
			return null;
		}
		final BinaryExpression domSub = (BinaryExpression) funImage.getLeft();
		return domSub.getRight();
	}

	@Override
	protected String getDisplayName(Predicate hyp, IPosition position) {
		if (hyp == null)
			return "Functional image simplification in goal";
		return "Functional image simplification in hyp ("
				+ hyp.getSubFormula(position) + ")";
	}

	@Override
	protected IHypAction getHypAction(Predicate pred, IPosition position) {
		if (pred == null) {
			return null;
		}
		return ProverFactory.makeHideHypAction(Arrays.asList(pred));
	}

}
