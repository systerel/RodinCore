/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Université de Lorraine - rules FIN_KINTER_R and FIN_QINTER_R
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.stream;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

/**
 * Implementation of the "Finite of inter" reasoners.
 *
 * There are three rules to prove the finiteness of the three types of intersection: ∩,
 * inter and ⋂ (internally named BINTER, KINTER and QINTER).
 *
 * @author Thai Son Hoang
 * @author Guillaume Verdier
 */
public class FiniteInter extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteInter";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	protected Predicate getNewGoal(IProverSequent seq) {
		Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();

		// goal should have the form finite(...)
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		Expression expr = sPred.getExpression();
		switch (expr.getTag()) {
		case BINTER:
			return getNewGoalBInter((AssociativeExpression) expr, ff);
		case KINTER:
			return getNewGoalKInter((UnaryExpression) expr, ff);
		case QINTER:
			return getNewGoalQInter((QuantifiedExpression) expr, ff);
		default:
			// Not applicable
			return null;
		}
	}

	// Input: S ∩ ... ∩ T
	// Output: finite(S) ∨ ... ∨ finite(T)
	@ProverRule("FIN_BINTER_R")
	protected Predicate getNewGoalBInter(AssociativeExpression aExp, FormulaFactory ff) {
		Expression[] children = aExp.getChildren();
		Predicate[] newChildren = stream(children).map(e -> ff.makeSimplePredicate(KFINITE, e, null))
				.toArray(Predicate[]::new);
		return ff.makeAssociativePredicate(LOR, newChildren, null);
	}

	// Input: inter(S)
	// Output: ∃s · s ∈ S ∧ finite(s)
	@ProverRule("FIN_KINTER_R")
	protected Predicate getNewGoalKInter(UnaryExpression exp, FormulaFactory ff) {
		Expression set = exp.getChild();
		Type sType = set.getType().getBaseType();
		BoundIdentDecl decl = ff.makeBoundIdentDecl("s", null, sType);
		BoundIdentifier s = ff.makeBoundIdentifier(0, null, sType);
		Predicate pred1 = ff.makeRelationalPredicate(IN, s, set, null);
		Predicate pred2 = ff.makeSimplePredicate(KFINITE, s, null);
		Predicate pred = ff.makeAssociativePredicate(LAND, new Predicate[] { pred1, pred2 }, null);
		return ff.makeQuantifiedPredicate(EXISTS, new BoundIdentDecl[] { decl }, pred, null);
	}

	// Input: ⋂ s · P ∣ E
	// Output: ∃s · P ∧ finite(E)
	@ProverRule("FIN_QINTER_R")
	protected Predicate getNewGoalQInter(QuantifiedExpression exp, FormulaFactory ff) {
		BoundIdentDecl[] expDecls = exp.getBoundIdentDecls();
		Predicate pred1 = exp.getPredicate();
		Predicate pred2 = ff.makeSimplePredicate(KFINITE, exp.getExpression(), null);
		Predicate pred = ff.makeAssociativePredicate(LAND, new Predicate[] { pred1, pred2 }, null);
		return ff.makeQuantifiedPredicate(EXISTS, expDecls, pred, null);
	}

	protected String getDisplayName() {
		return "finite of intersection";
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		Predicate newGoal = getNewGoal(seq);
		if (newGoal == null)
			return reasonerFailure(this, input, "Inference '" + getDisplayName() + "' is not applicable");

		// Generate the successful reasoner output
		return makeProofRule(this, input, seq.goal(), getDisplayName(), makeAntecedent(newGoal));
	}

}
