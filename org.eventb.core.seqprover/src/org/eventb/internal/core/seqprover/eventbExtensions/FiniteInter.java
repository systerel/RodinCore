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

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
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
	
	@ProverRule({"FIN_BINTER_R", "FIN_KINTER_R"})
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();
		final FormulaFactory ff = seq.getFormulaFactory();

		// goal should have the form finite(...)
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		Expression expr = sPred.getExpression();
		switch (expr.getTag()) {
		case Expression.BINTER:
			return getAntecedentsBInter((AssociativeExpression) expr, ff);
		case Expression.KINTER:
			return getAntecedentKInter((UnaryExpression) expr, ff);
		default:
			// Not applicable
			return null;
		}
	}
		
	protected IAntecedent[] getAntecedentsBInter(AssociativeExpression aExp, FormulaFactory ff) {
		// There will be 1 antecedent
		IAntecedent[] antecedents = new IAntecedent[1];
		
		Expression[] children = aExp.getChildren();
		Predicate [] newChildren = new Predicate[children.length];
		
		for (int i = 0; i < children.length; ++i) {
			newChildren[i] = ff.makeSimplePredicate(Predicate.KFINITE,
					children[i], null);
		}
		
		Predicate newGoal = ff.makeAssociativePredicate(Predicate.LOR,
				newChildren, null);
		
		antecedents[0] = ProverFactory.makeAntecedent(newGoal);
		return antecedents;
	}

	protected IAntecedent[] getAntecedentKInter(UnaryExpression exp, FormulaFactory ff) {
		Expression set = exp.getChild();
		// Generate: ∃s · s ∈ set ∧ finite(s)
		BoundIdentDecl decl = ff.makeBoundIdentDecl("s", null);
		BoundIdentifier s = ff.makeBoundIdentifier(0, null);
		Predicate pred1 = ff.makeRelationalPredicate(Predicate.IN, s, set, null);
		Predicate pred2 = ff.makeSimplePredicate(Predicate.KFINITE, s, null);
		Predicate pred = ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] { pred1, pred2 }, null);
		Predicate newGoal = ff.makeQuantifiedPredicate(Predicate.EXISTS, new BoundIdentDecl[] { decl }, pred, null);
		newGoal.typeCheck(ff.makeTypeEnvironment());
		return new IAntecedent[] { ProverFactory.makeAntecedent(newGoal) };
	}

	protected String getDisplayName() {
		return "finite of ∩";
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IAntecedent[] antecedents = getAntecedents(seq);
		if (antecedents == null)
			return ProverFactory.reasonerFailure(this, input,
					"Inference " + getReasonerID()
							+ " is not applicable");

		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				getDisplayName(), antecedents);
	}

}
