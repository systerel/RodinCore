/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 ******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;

public class FinitePositive extends EmptyInputReasoner {

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".finitePositive";

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("FIN_GE_0")
	protected IAntecedent[] getAntecedents(IProverSequent seq) {
		Predicate goal = seq.goal();

		// goal should have the form finite(S)
		if (!Lib.isFinite(goal))
			return null;
		SimplePredicate sPred = (SimplePredicate) goal;
		if (!Lib.isSetOfIntegers(sPred.getExpression()))
			return null;

		// There will be 2 antecidents
		IAntecedent[] antecidents = new IAntecedent[2];

		Expression S = sPred.getExpression();

		// #n.(!x.x : S => x <= n)
		final FormulaFactory ff = seq.getFormulaFactory();
		BoundIdentDecl nDecl = ff.makeBoundIdentDecl("n", null);
		BoundIdentDecl xDecl = ff.makeBoundIdentDecl("x", null);
		BoundIdentifier n = ff.makeBoundIdentifier(1, null);
		BoundIdentifier x = ff.makeBoundIdentifier(0, null);

		RelationalPredicate left = ff.makeRelationalPredicate(Predicate.IN, x,
				S, null);
		RelationalPredicate right = ff.makeRelationalPredicate(Predicate.LE, x,
				n, null);
		BinaryPredicate limp = ff.makeBinaryPredicate(Predicate.LIMP, left,
				right, null);
		QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
				Predicate.FORALL, new BoundIdentDecl[] { xDecl }, limp, null);
		Predicate newGoal0 = ff.makeQuantifiedPredicate(Predicate.EXISTS,
				new BoundIdentDecl[] { nDecl }, qPred, null);
		newGoal0.typeCheck(ff.makeTypeEnvironment());
		antecidents[0] = ProverFactory.makeAntecedent(newGoal0);

		// S <: NAT
		Expression nat = ff.makeAtomicExpression(
						Expression.NATURAL, null);
		Predicate newGoal1 = ff.makeRelationalPredicate(Predicate.SUBSETEQ, S,
				nat, null);
		antecidents[1] = ProverFactory.makeAntecedent(newGoal1);
		return antecidents;
	}

	protected String getDisplayName() {
		return "finite of set of non-negative numbers";
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IAntecedent[] antecidents = getAntecedents(seq);
		if (antecidents == null)
			return ProverFactory.reasonerFailure(this, input, "Inference "
					+ getReasonerID() + " is not applicable");

		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				getDisplayName(), antecidents);
	}

}
