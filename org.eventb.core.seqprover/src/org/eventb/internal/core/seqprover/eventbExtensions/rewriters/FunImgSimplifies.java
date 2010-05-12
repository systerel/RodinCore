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

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

public class FunImgSimplifies implements IVersionedReasoner {

	private final static String POSITION_KEY = "pos";

	private static final int VERSION = 0;

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".funImgSimplifies";

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	public static class Input implements IReasonerInput {

		IPosition position;

		/**
		 * The parameter is the predicate to rewrite.
		 * 
		 * @param position
		 */
		public Input(IPosition position) {
			this.position = position;
		}

		public void applyHints(ReplayHints renaming) {
		}

		public String getError() {
			return null;
		}

		public boolean hasError() {
			return false;
		}

		public IPosition getPosition() {
			return position;
		}

	}

	public int getVersion() {
		return VERSION;
	}

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule( { "FUNIMG_SET_DOMSUB_L", "FUNIMG_DOMSUB_L" })
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final FunImgSimpImpl impl = new FunImgSimpImpl(seq);

		final Predicate goal = seq.goal();
		final Expression function = getFunction(goal, input);
		if (function == null) {
			return failure(input, goal);
		}
		final Predicate rewritten = rewrite(goal, input, function, impl);
		if (rewritten == null) {
			return failure(input, goal);
		}
		final Predicate neededHyp = impl.getNeededHyp(function);
		if (neededHyp == null) {
			return failure(input, goal);
		}
		final Predicate newGoal = rewritten;
		final IAntecedent antecedent = ProverFactory.makeAntecedent(newGoal);

		return ProverFactory.makeProofRule(this, input, goal, neededHyp,
				getDisplayName(goal, input.position), antecedent);
	}

	protected Predicate rewrite(Predicate pred, Input input,
			Expression function, FunImgSimpImpl functions) {
		final Formula<?> subFormula = pred.getSubFormula(input.position);
		if (subFormula == null || subFormula.getTag() != Expression.FUNIMAGE) {
			return null;
		}
		final BinaryExpression funImage = (BinaryExpression) subFormula;
		final Expression G = funImage.getRight();
		final Expression replacement = ff.makeBinaryExpression(
				Expression.FUNIMAGE, function, G, null);
		return pred.rewriteSubFormula(input.position, replacement, ff);

	}

	private Expression getFunction(Predicate pred, Input input) {
		if (input.position == null) {
			return null;
		}
		final Formula<?> subFormula = pred.getSubFormula(input.position);
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

	private IReasonerOutput failure(final Input input, final Predicate pred) {
		return ProverFactory.reasonerFailure(this, input, "Rewriter "
				+ getReasonerID() + " is inapplicable for goal " + pred
				+ " at position " + input.position);
	}

	protected String getDisplayName(Predicate pred, IPosition position) {
		return "Functional image simplification in goal";
	}

	public final IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {

		final FormulaFactory ff = FormulaFactory.getDefault();
		final String posString = reader.getString(POSITION_KEY);
		final IPosition position = ff.makePosition(posString);

		if (reader.getGoal() != null) {
			// Goal rewriting
			return new Input(position);
		}
		throw new SerializeException(new IllegalStateException(
				"Expected a goal to apply simplification"));
	}

	public final void serializeInput(IReasonerInput input,
			IReasonerInputWriter writer) throws SerializeException {
		// Serialise the position only, the predicate is contained inside the
		// rule
		writer.putString(POSITION_KEY, ((Input) input).position.toString());
	}

}
