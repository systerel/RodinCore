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
import static org.eventb.core.ast.Formula.KINTER;
import static org.eventb.core.ast.Formula.QINTER;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
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
import org.eventb.internal.core.seqprover.eventbExtensions.utils.FormulaBuilder;

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
		final FormulaBuilder fb = new FormulaBuilder(seq.getFormulaFactory());

		// goal should have the form finite(...)
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		Expression expr = sPred.getExpression();
		switch (expr.getTag()) {
		case BINTER:
			return getNewGoalBInter((AssociativeExpression) expr, fb);
		case KINTER:
			return getNewGoalKInter((UnaryExpression) expr, fb);
		case QINTER:
			return getNewGoalQInter((QuantifiedExpression) expr, fb);
		default:
			// Not applicable
			return null;
		}
	}

	// Input: S ∩ ... ∩ T
	// Output: finite(S) ∨ ... ∨ finite(T)
	@ProverRule("FIN_BINTER_R")
	protected Predicate getNewGoalBInter(AssociativeExpression aExp, FormulaBuilder fb) {
		Predicate[] newChildren = stream(aExp.getChildren()).map(fb::finite).toArray(Predicate[]::new);
		return fb.or(newChildren);
	}

	// Input: inter(S)
	// Output: ∃s · s ∈ S ∧ finite(s)
	@ProverRule("FIN_KINTER_R")
	protected Predicate getNewGoalKInter(UnaryExpression exp, FormulaBuilder fb) {
		Expression set = exp.getChild();
		Type sType = set.getType().getBaseType();
		BoundIdentifier s = fb.boundIdent(0, sType);
		return fb.exists(fb.boundIdentDecl("s", sType), fb.and(fb.in(s, set), fb.finite(s)));
	}

	// Input: ⋂ s · P ∣ E
	// Output: ∃s · P ∧ finite(E)
	@ProverRule("FIN_QINTER_R")
	protected Predicate getNewGoalQInter(QuantifiedExpression exp, FormulaBuilder fb) {
		BoundIdentDecl[] expDecls = exp.getBoundIdentDecls();
		return fb.exists(expDecls, fb.and(exp.getPredicate(), fb.finite(exp.getExpression())));
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
