/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isFinite;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

public class FiniteSet extends SingleExprInputReasoner implements
		IVersionedReasoner {

	public static String REASONER_ID = SequentProver.PLUGIN_ID + ".finiteSet";

	private static final int VERSION = 0;

	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule("FIN_SUBSETEQ_R")
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final Predicate goal = seq.goal();
		if (!isFinite(goal))
			return reasonerFailure(this, input, "Goal is not a finiteness");
		final Expression S = ((SimplePredicate) goal).getExpression();

		if (input.hasError()) {
			return reasonerFailure(this, input, ((SingleExprInput) input)
					.getError());
		}
		if (!(input instanceof SingleExprInput))
			return reasonerFailure(this, input,
					"Expected a single expression input");
		final Expression T = ((SingleExprInput) input).getExpression();

		if (!(S.getType().equals(T.getType()))) {
			return reasonerFailure(this, input, "Incorrect input type");
		}

		final FormulaFactory ff = seq.getFormulaFactory();
		final IAntecedent[] antecedents = new IAntecedent[] {
		// T is well-defined
				makeAntecedent(T.getWDPredicate(ff)),

				// finite(T)
				makeAntecedent(ff.makeSimplePredicate(KFINITE, T, null)),

				// S <: T
				makeAntecedent(ff.makeRelationalPredicate(SUBSETEQ, S, T, null)), //
		};

		return makeProofRule(this, input, goal, "finite set", antecedents);
	}

	public int getVersion() {
		return VERSION;
	}

}
