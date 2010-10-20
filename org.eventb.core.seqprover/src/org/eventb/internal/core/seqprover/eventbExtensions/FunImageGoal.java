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

import static java.util.Collections.singleton;

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
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;


/**
 * A reasoner that generates proof Rules that add a hypothesis of the form 
 * f(E) : S2 if the reasoner finds an hypothesis of the form f : E1 op E2 
 * where E, E1 and E2 are expressions.
 *
 * @author Hugo De Sa Pereira Pinto
 */

public class FunImageGoal extends PredicatePositionReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".funImgGoal";

	@Override
	@ProverRule("FUN_IMAGE_GOAL")
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;
		final Predicate pred = input.getPred();
		final IPosition position = input.getPosition();
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

	protected IAntecedent[] getAntecedents(IProverSequent seq, Predicate pred,
			IPosition position) {
		final Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();
		final Formula<?> funApp = goal.getSubFormula(position);

		if (funApp == null) {
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

		if (!Lib.isInclusion(pred)) {
			return null;
		}

		final Expression element = Lib.getElement(pred);
		final Expression set = Lib.getSet(pred);

		if (!Lib.isFun(set) && !Lib.isRel(set)) {
			return null;
		}	
		
		final Expression rightSet = Lib.getRight(set);
		
		if (element.equals(function)) {
			final DLib dl = DLib.mDLib(ff);
			final Predicate addedHyp = dl.makeInclusion(
					(Expression) funApp, rightSet);
			return new IAntecedent[] { //
					ProverFactory.makeAntecedent(goal, singleton(addedHyp),
							null), //
			};
		}		

		return null;
	}

	@Override
	protected String getDisplayName() {
		return "functional image goal";
	}

}
