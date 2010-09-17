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

package org.eventb.internal.core.seqprover.eventbExtensions;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class IsFunImageGoal extends AbstractManualInference {

	public static String REASONER_ID = SequentProver.PLUGIN_ID
			+ ".funImgGoal";

	@Override
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final Predicate pred = input.pred;
		final IPosition position = input.position;
		final IAntecedent[] antecedents = getAntecedents(seq, pred, position);

		if (antecedents == null) {
			return ProverFactory.reasonerFailure(this, input, "Inference "
					+ getReasonerID() + " is not applicable for "
					+ (pred == null ? seq.goal() : pred) + " at position "
					+ position);
		}

		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(), pred,
				getDisplayName(pred, position), antecedents);

	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		final Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();
		final Formula<?> funApp = goal.getSubFormula(position);

		if(funApp==null){
			return null;
		}
		
		if (!seq.containsHypothesis(pred)) {
			return null;
		}

		if (!Lib.isWDStrictPosition(goal, position)) {
			return null;
		}

		if (funApp.getTag() != Formula.FUNIMAGE) {
			return null;
		}

		final Expression function = ((BinaryExpression) funApp).getLeft();

		if (Lib.isInclusion(pred)) {
			final Expression element = Lib.getElement(pred);
			final Expression set = Lib.getSet(pred);

			if (Lib.isFun(set) || Lib.isRel(set)) {
				final Expression rightSet = Lib.getRight(set);
				if (element.equals(function)) {
					final Predicate addedHyp = ff.makeRelationalPredicate(
							Formula.IN, (Expression) funApp, rightSet, null);
					Lib.postConstructionCheck(addedHyp);
					Set<Predicate> addedHyps = new HashSet<Predicate>();
					addedHyps.add(addedHyp);
					IAntecedent[] anticidents = new IAntecedent[1];
					anticidents[0] = ProverFactory.makeAntecedent(goal,
							addedHyps, null);
					return anticidents;
				}
			}
		}

		return null;
	}

	@Override
	protected String getDisplayName() {
		return "functional image goal";
	}

}
