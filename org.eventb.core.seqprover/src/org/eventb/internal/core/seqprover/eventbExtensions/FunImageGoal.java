/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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
import static org.eventb.core.ast.Formula.FUNIMAGE;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Generates a proof rule that adds a hypothesis <code>f(E) ∈ E2</code> if the
 * sequent contains hypothesis <code>f ∈ E1 op E2</code> and the goal contains
 * expression <code>f(E)</code> in a WD-strict position.
 * 
 * @author Hugo De Sa Pereira Pinto
 */
public class FunImageGoal extends PredicatePositionReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".funImgGoal";

	@Override
	@ProverRule("FUN_IMAGE_GOAL")
	public IReasonerOutput apply(IProverSequent seq,
			IReasonerInput reasonerInput, IProofMonitor pm) {

		final Input input = (Input) reasonerInput;

		final Predicate goal = seq.goal();
		final IPosition position = input.getPosition();
		if (!Lib.isWDStrictPosition(goal, position)) {
			return reasonerFailure(this, input,
					"Non WD-strict position in goal");
		}
		final BinaryExpression funImage = extractFunImage(goal, position);
		if (funImage == null) {
			return reasonerFailure(this, input,
					"Position does not denote a function application");
		}
		final Expression fun = funImage.getLeft();

		final Predicate hyp = input.getPred();
		if (hyp == null || !seq.containsHypothesis(hyp)) {
			return reasonerFailure(this, input, "Missing hypothesis " + hyp);
		}
		final Expression E2 = extractE2(hyp, fun);
		if (E2 == null) {
			return reasonerFailure(this, input, "Ill-formed hypothesis " + hyp);
		}

		final Predicate addedHyp = DLib.makeInclusion(funImage, E2);
		final IAntecedent antecedent = makeAntecedent(goal,
				singleton(addedHyp), null);
		final String display = getDisplayName() + " for " + funImage;
		return makeProofRule(this, input, goal, hyp, display,
				new IAntecedent[] { antecedent });

	}

	/*
	 * Extract the function application from the goal, if possible.
	 */
	private BinaryExpression extractFunImage(Predicate goal, IPosition position) {
		final Formula<?> subFormula = goal.getSubFormula(position);
		if (subFormula.getTag() == FUNIMAGE) {
			return (BinaryExpression) subFormula;
		}
		return null;
	}

	private Expression extractE2(Predicate hyp, Expression fun) {
		if (hyp.getTag() != IN) {
			return null;
		}
		final RelationalPredicate in = (RelationalPredicate) hyp;
		if (!fun.equals(in.getLeft())) {
			return null;
		}
		final Expression set = in.getRight();
		if (!Lib.isFun(set) && !Lib.isRel(set)) {
			return null;
		}
		return ((BinaryExpression) set).getRight();
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	protected String getDisplayName() {
		return "functional image goal";
	}

}
