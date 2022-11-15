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
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KUNION;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.QUNION;
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

	@ProverRule({ "FIN_BUNION_R", "FIN_KUNION_R", "FIN_QUNION_R" })
	protected Predicate getNewGoal(IProverSequent seq) {
		Predicate goal = seq.goal();
		FormulaFactory ff = seq.getFormulaFactory();

		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		Expression expr = sPred.getExpression();
		switch (expr.getTag()) {
		case BUNION:
			return getNewGoalBUnion((AssociativeExpression) expr, ff);
		case KUNION:
			return getNewGoalKUnion((UnaryExpression) expr, ff);
		case QUNION:
			return getNewGoalQUnion((QuantifiedExpression) expr, ff);
		default:
			// Not applicable
			return null;
		}
	}

	protected Predicate getNewGoalBUnion(AssociativeExpression exp, FormulaFactory ff) {
		Predicate[] newChildren = stream(exp.getChildren()).map(e -> ff.makeSimplePredicate(KFINITE, e, null))
				.toArray(Predicate[]::new);
		return ff.makeAssociativePredicate(LAND, newChildren, null);
	}

	protected Predicate getNewGoalKUnion(UnaryExpression exp, FormulaFactory ff) {
		Expression set = exp.getChild();
		// Generate: finite(set) ∧ (∀s · s ∈ set ⇒ finite(s))
		Type sType = set.getType().getBaseType();
		Predicate finite = ff.makeSimplePredicate(KFINITE, set, null);
		BoundIdentDecl decl = ff.makeBoundIdentDecl("s", null, sType);
		BoundIdentifier s = ff.makeBoundIdentifier(0, null, sType);
		Predicate pred1 = ff.makeRelationalPredicate(IN, s, set, null);
		Predicate pred2 = ff.makeSimplePredicate(KFINITE, s, null);
		Predicate pred = ff.makeBinaryPredicate(LIMP, pred1, pred2, null);
		Predicate quantified = ff.makeQuantifiedPredicate(FORALL, new BoundIdentDecl[] { decl }, pred, null);
		return ff.makeAssociativePredicate(LAND, new Predicate[] { finite, quantified }, null);
	}

	protected Predicate getNewGoalQUnion(QuantifiedExpression exp, FormulaFactory ff) {
		BoundIdentDecl[] expDecls = exp.getBoundIdentDecls();
		// Generate: finite({s · P | E}) ∧ (∀s · P ⇒ finite(E))
		Predicate expPred = exp.getPredicate();
		Expression exprExpr = exp.getExpression();
		Expression cset = ff.makeQuantifiedExpression(CSET, expDecls, expPred, exprExpr, null, exp.getForm());
		Predicate finite = ff.makeSimplePredicate(KFINITE, cset, null);
		Predicate exprFin = ff.makeSimplePredicate(KFINITE, exprExpr, null);
		Predicate pred = ff.makeBinaryPredicate(LIMP, expPred, exprFin, null);
		Predicate quantified = ff.makeQuantifiedPredicate(FORALL, expDecls, pred, null);
		return ff.makeAssociativePredicate(LAND, new Predicate[] { finite, quantified }, null);
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
