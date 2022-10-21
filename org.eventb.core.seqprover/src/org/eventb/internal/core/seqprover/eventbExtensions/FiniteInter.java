/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
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
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class FiniteInter extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteInter";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule({ "FIN_BINTER_R", "FIN_KINTER_R", "FIN_QINTER_R" })
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();

		// goal should have the form finite(...)
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		Expression expr = sPred.getExpression();
		switch (expr.getTag()) {
		case BINTER:
			return getAntecedentsBInter((AssociativeExpression) expr, ff);
		case KINTER:
			return getAntecedentKInter((UnaryExpression) expr, ff);
		case QINTER:
			return getAntecedentQInter((QuantifiedExpression) expr, ff);
		default:
			// Not applicable
			return null;
		}
	}

	protected IAntecedent[] getAntecedentsBInter(AssociativeExpression aExp, FormulaFactory ff) {
		Expression[] children = aExp.getChildren();
		Predicate[] newChildren = stream(children).map(e -> ff.makeSimplePredicate(KFINITE, e, null))
				.toArray(Predicate[]::new);
		Predicate newGoal = ff.makeAssociativePredicate(LOR, newChildren, null);
		return new IAntecedent[] { makeAntecedent(newGoal) };
	}

	protected IAntecedent[] getAntecedentKInter(UnaryExpression exp, FormulaFactory ff) {
		Expression set = exp.getChild();
		// Generate: ∃s · s ∈ set ∧ finite(s)
		Type sType = set.getType().getBaseType();
		BoundIdentDecl decl = ff.makeBoundIdentDecl("s", null, sType);
		BoundIdentifier s = ff.makeBoundIdentifier(0, null, sType);
		Predicate pred1 = ff.makeRelationalPredicate(IN, s, set, null);
		Predicate pred2 = ff.makeSimplePredicate(KFINITE, s, null);
		Predicate pred = ff.makeAssociativePredicate(LAND, new Predicate[] { pred1, pred2 }, null);
		Predicate newGoal = ff.makeQuantifiedPredicate(EXISTS, new BoundIdentDecl[] { decl }, pred, null);
		return new IAntecedent[] { makeAntecedent(newGoal) };
	}

	protected IAntecedent[] getAntecedentQInter(QuantifiedExpression exp, FormulaFactory ff) {
		BoundIdentDecl[] expDecls = exp.getBoundIdentDecls();
		Predicate pred1 = exp.getPredicate();
		Predicate pred2 = ff.makeSimplePredicate(KFINITE, exp.getExpression(), null);
		Predicate pred = ff.makeAssociativePredicate(LAND, new Predicate[] { pred1, pred2 }, null);
		Predicate newGoal = ff.makeQuantifiedPredicate(EXISTS, expDecls, pred, null);
		return new IAntecedent[] { makeAntecedent(newGoal) };
	}

	protected String getDisplayName() {
		return "finite of intersection";
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input, IProofMonitor pm) {
		IAntecedent[] antecedents = getAntecedents(seq);
		if (antecedents == null)
			return reasonerFailure(this, input, "Inference '" + getDisplayName() + "' is not applicable");

		// Generate the successful reasoner output
		return makeProofRule(this, input, seq.goal(), getDisplayName(), antecedents);
	}

}
