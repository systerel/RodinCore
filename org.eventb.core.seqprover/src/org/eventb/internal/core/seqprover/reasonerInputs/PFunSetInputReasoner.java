/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.reasonerInputs;

import static org.eventb.core.seqprover.ProverFactory.makeAntecedent;
import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerFailure;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInputReasoner;

public abstract class PFunSetInputReasoner extends SingleExprInputReasoner {

	@Override
	public PFunSetInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new PFunSetInput(reader);
	}

	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {

		final Predicate goal = seq.goal();
		if (!Lib.isFinite(goal)) {
			return reasonerFailure(this, input, "Goal is not a finiteness");
		}
		final IReasonerFailure goalFailure = verifyGoal(goal, input);
		if (goalFailure != null) {
			return goalFailure;
		}

		if (input.hasError()) {
			return reasonerFailure(this, input, input.getError());
		}
		if (!(input instanceof PFunSetInput))
			return reasonerFailure(this, input,
					"Expected a set of all partial functions S ⇸ T");
		final PFunSetInput pInput = (PFunSetInput) input;
		final FormulaFactory ff = seq.getFormulaFactory();
		final IReasonerFailure inputFailure = verifyInput(goal, pInput, ff);
		if (inputFailure != null) {
			return inputFailure;
		}
		final IAntecedent[] antes = makeAntecedents(getSubgoals(goal, pInput,
				ff));
		return makeProofRule(this, input, goal, getReasonerDesc(), antes);
	}

	abstract protected String getReasonerDesc();

	/**
	 * Check if the function f enclosed in the sequent goal is of the correct
	 * type. Returns an IReasonerFailure if check failed, or null if type check
	 * succeeded.
	 * 
	 * @param goal
	 *            The predicate enclosing the function f
	 * @param input
	 *            The input given by the user
	 * @return an IReasonerFailure if f is not of the right form, null otherwise
	 */
	abstract protected IReasonerFailure verifyGoal(Predicate goal,
			IReasonerInput input);

	/**
	 * Check if the input has the correct type. Returns an IReasonerFailure if
	 * type check occured, or null if type check succeeded.
	 * 
	 * @param goal
	 *            the predicate enclosing the function f
	 * @param input
	 *            the set of partial functions given by the user, to type check
	 *            the function
	 * @param ff 
	 * 			  the formula factory to use
	 * @return an IReasonerFailure if type check failed, null otherwise
	 */
	abstract protected IReasonerFailure verifyInput(Predicate goal,
			PFunSetInput input, FormulaFactory ff);

	/**
	 * Computes an array of subgoals that constitute the antecedents of the rule
	 * implemented by this reasoner.
	 * 
	 * @param goal
	 *            the predicate enclosing the function f
	 * @param input
	 *            the input given by the user
	 * @param ff
	 * 			  the formula factory to use
	 * @return an array of subgoals to build the ProofRule
	 */
	abstract protected Predicate[] getSubgoals(Predicate goal,
			PFunSetInput input, FormulaFactory ff);

	/**
	 * Assuming the goal to be of form <code>finite(E)</code>, returns
	 * expression <code>E</code>.
	 * 
	 * @param goal
	 * @return the expression of the function f
	 */
	protected Expression getFiniteExpression(Predicate goal) {
		return ((SimplePredicate) goal).getExpression();
	}

	private IAntecedent[] makeAntecedents(Predicate[] predicates) {
		final IAntecedent[] antecedents = new IAntecedent[predicates.length];
		for (int i = 0; i < predicates.length; i++) {
			antecedents[i] = makeAntecedent(predicates[i]);
		}
		return antecedents;
	}

}
