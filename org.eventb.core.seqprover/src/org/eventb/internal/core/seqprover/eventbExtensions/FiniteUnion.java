/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.Arrays.stream;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.KUNION;
import static org.eventb.core.ast.Formula.QUNION;
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
 * Implementation of the "Finite of union" reasoners.
 *
 * There are three rules to prove the finiteness of the three types of union: ∪,
 * union and ⋃ (internally named BUNION, KUNION and QUNION).
 *
 * @author Guillaume Verdier
 */
public class FiniteUnion extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteUnion";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	protected Predicate getNewGoal(IProverSequent seq) {
		Predicate goal = seq.goal();
		FormulaBuilder fb = new FormulaBuilder(seq.getFormulaFactory());

		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		Expression expr = sPred.getExpression();
		switch (expr.getTag()) {
		case BUNION:
			return getNewGoalBUnion((AssociativeExpression) expr, fb);
		case KUNION:
			return getNewGoalKUnion((UnaryExpression) expr, fb);
		case QUNION:
			return getNewGoalQUnion((QuantifiedExpression) expr, fb);
		default:
			// Not applicable
			return null;
		}
	}

	// Input: S ∪ ... ∪ T
	// Output: finite(S) ∧ ... ∧ finite(T)
	@ProverRule("FIN_BUNION_R")
	protected Predicate getNewGoalBUnion(AssociativeExpression exp, FormulaBuilder fb) {
		Predicate[] newChildren = stream(exp.getChildren()).map(e -> fb.finite(e)).toArray(Predicate[]::new);
		return fb.and(newChildren);
	}

	// Input: union(S)
	// Output: finite(S) ∧ (∀s · s ∈ S ⇒ finite(s))
	@ProverRule("FIN_KUNION_R")
	protected Predicate getNewGoalKUnion(UnaryExpression exp, FormulaBuilder fb) {
		Expression set = exp.getChild();
		Type sType = set.getType().getBaseType();
		// We add a new quantified variable but do not need to call
		// set.shiftBoundIdentifiers() since union(set) is the root expression of the
		// goal and therefore can't contain bound identifiers
		BoundIdentifier s = fb.boundIdent(0, sType);
		return fb.and(fb.finite(set), fb.forall(fb.boundIdentDecl("s", sType), fb.imp(fb.in(s, set), fb.finite(s))));
	}

	// Input: ⋃ s · P ∣ E
	// Output: finite({s · P ∣ E}) ∧ (∀s · P ⇒ finite(E))
	@ProverRule("FIN_QUNION_R")
	protected Predicate getNewGoalQUnion(QuantifiedExpression exp, FormulaBuilder fb) {
		BoundIdentDecl[] expDecls = exp.getBoundIdentDecls();
		Predicate expPred = exp.getPredicate();
		Expression exprExpr = exp.getExpression();
		return fb.and(fb.finite(fb.cset(expDecls, expPred, exprExpr, exp.getForm())),
				fb.forall(expDecls, fb.imp(expPred, fb.finite(exprExpr))));
	}

	protected String getDisplayName() {
		return "finite of union";
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		Predicate newGoal = getNewGoal(seq);
		if (newGoal == null) {
			return reasonerFailure(this, input, "Inference '" + getDisplayName() + "' is not applicable");
		} else {
			return makeProofRule(this, input, seq.goal(), getDisplayName(), makeAntecedent(newGoal));
		}
	}

}
